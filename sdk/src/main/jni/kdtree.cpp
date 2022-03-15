#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <cstring>
#include "kdtree.h"

#define multiply2(s)           ((s) * (s))

static struct HyperRectangle *hyperRectangleDuplicate(const struct HyperRectangle *rect);

static void hyperRectangleExtend(struct HyperRectangle *rect, const double *key);

static double hyperRectangleDistanceCalc(struct HyperRectangle *rect, const double *key);

static int resHyperPointInsert(struct ResHyperPoint *list, struct HyperNode *item, double dist_sq);

static void clearResults(struct KdRes *set);

static struct HyperRectangle *hyperRectangleCreate(int dim, const double *min, const double *max);

static void hyperRectangleFree(struct HyperRectangle *rect);

static void clearHyperRectangle(struct HyperNode *node, void (*destr)(void *));

static int insertHyperRectangle(struct HyperNode **node, const double *key, void *data, int dir,
                                int dim);


struct kdtree *kdCreate(int k) {
    struct kdtree *tree;

    if (!(tree = new kdtree)) {
        return 0;
    }

    tree->dim = k;
    tree->root = 0;
    tree->destr = 0;
    tree->rect = 0;

    return tree;
}

void kdFree(struct kdtree *tree) {
    if (tree) {
        kdClear(tree);
        delete tree;
    }
}

static void clearHyperRectangle(struct HyperNode *node, void (*destr)(void *)) {
    if (!node) return;

    clearHyperRectangle(node->left, destr);
    clearHyperRectangle(node->right, destr);

    if (destr) {
        destr(node->data);
    }
    delete[] node->key;
    delete node;
}

void kdClear(struct kdtree *tree) {
    clearHyperRectangle(tree->root, tree->destr);
    tree->root = 0;

    if (tree->rect) {
        hyperRectangleFree(tree->rect);
        tree->rect = 0;
    }
}

void kdDataDestructor(struct kdtree *tree, void (*destr)(void *)) {
    tree->destr = destr;
}


static int insertHyperRectangle(struct HyperNode **nptr, const double *key, void *data, int dir,
                                int dim) {
    int new_dir;
    struct HyperNode *node;

    if (!*nptr) {
        if (!(node = new HyperNode)) {
            return -1;
        }
        if (!(node->key = new double[dim])) {
            delete[] node;
            return -1;
        }
        memcpy(node->key, key, dim * sizeof *node->key);
        node->data = data;
        node->dir = dir;
        node->left = node->right = 0;
        *nptr = node;
        return 0;
    }

    node = *nptr;
    new_dir = (node->dir + 1) % dim;
    if (key[node->dir] < node->key[node->dir]) {
        return insertHyperRectangle(&(*nptr)->left, key, data, new_dir, dim);
    }
    return insertHyperRectangle(&(*nptr)->right, key, data, new_dir, dim);
}

int kdInsert(struct kdtree *tree, const double *key, void *data) {
    if (insertHyperRectangle(&tree->root, key, data, 0, tree->dim)) {
        return -1;
    }

    if (tree->rect == 0) {
        tree->rect = hyperRectangleCreate(tree->dim, key, key);
    } else {
        hyperRectangleExtend(tree->rect, key);
    }

    return 0;
}

static int findNearest(struct HyperNode *node, const double *key, double range,
                       struct ResHyperPoint *list, int ordered, int dim) {
    double dist_sq, dx;
    int i, ret, added_res = 0;

    if (!node) return 0;

    dist_sq = 0;
    for (i = 0; i < dim; i++) {
        dist_sq += multiply2(node->key[i] - key[i]);
    }
    if (dist_sq <= multiply2(range)) {
        if (resHyperPointInsert(list, node, ordered ? dist_sq : -1.0) == -1) {
            return -1;
        }
        added_res = 1;
    }

    dx = key[node->dir] - node->key[node->dir];

    ret = findNearest(dx <= 0.0 ? node->left : node->right, key, range, list, ordered, dim);
    if (ret >= 0 && fabs(dx) < range) {
        added_res += ret;
        ret = findNearest(dx <= 0.0 ? node->right : node->left, key, range, list, ordered, dim);
    }
    if (ret == -1) {
        return -1;
    }
    added_res += ret;

    return added_res;
}


static void kdNearestElement(struct HyperNode *node, const double *key, struct HyperNode **result,
                             double *result_dist_sq, struct HyperRectangle *rect) {
    int dir = node->dir;
    int i;
    double dummy, dist_sq;
    struct HyperNode *nearer_subtree, *farther_subtree;
    double *nearer_hyperrect_coord, *farther_hyperrect_coord;


    dummy = key[dir] - node->key[dir];
    if (dummy <= 0) {
        nearer_subtree = node->left;
        farther_subtree = node->right;
        nearer_hyperrect_coord = rect->max + dir;
        farther_hyperrect_coord = rect->min + dir;
    } else {
        nearer_subtree = node->right;
        farther_subtree = node->left;
        nearer_hyperrect_coord = rect->min + dir;
        farther_hyperrect_coord = rect->max + dir;
    }

    if (nearer_subtree) {
        dummy = *nearer_hyperrect_coord;
        *nearer_hyperrect_coord = node->key[dir];

        kdNearestElement(nearer_subtree, key, result, result_dist_sq, rect);

        *nearer_hyperrect_coord = dummy;
    }


    dist_sq = 0;
    for (i = 0; i < rect->dim; i++) {
        dist_sq += multiply2(node->key[i] - key[i]);
    }
    if (dist_sq < *result_dist_sq) {
        *result = node;
        *result_dist_sq = dist_sq;
    }

    if (farther_subtree) {

        dummy = *farther_hyperrect_coord;
        *farther_hyperrect_coord = node->key[dir];

        if (hyperRectangleDistanceCalc(rect, key) < *result_dist_sq) {

            kdNearestElement(farther_subtree, key, result, result_dist_sq, rect);
        }

        *farther_hyperrect_coord = dummy;
    }
}

struct KdRes *kdNearest(struct kdtree *kd, const double *key) {
    struct HyperRectangle *rect;
    struct HyperNode *result;
    struct KdRes *rset;
    double dist_sq;
    int i;

    if (!kd) return 0;
    if (!kd->rect) return 0;


    if (!(rset = new KdRes)) {
        return 0;
    }
    if (!(rset->rPrevResPt = new ResHyperPoint)) {
        delete rset;
        return 0;
    }
    rset->rPrevResPt->next = 0;
    rset->tree = kd;


    if (!(rect = hyperRectangleDuplicate(kd->rect))) {
        kdResFree(rset);
        return 0;
    }


    result = kd->root;
    dist_sq = 0;
    for (i = 0; i < kd->dim; i++)
        dist_sq += multiply2(result->key[i] - key[i]);


    kdNearestElement(kd->root, key, &result, &dist_sq, rect);


    hyperRectangleFree(rect);


    if (result) {
        if (resHyperPointInsert(rset->rPrevResPt, result, -1.0) == -1) {
            kdResFree(rset);
            return 0;
        }
        rset->size = 1;
        kdResRearange(rset);
        return rset;
    } else {
        kdResFree(rset);
        return 0;
    }
}

struct KdRes *kdGetNearestInRange(struct kdtree *kd, const double *key, double range) {
    int ret;
    struct KdRes *rset;

    if (!(rset = new KdRes)) {
        return 0;
    }
    if (!(rset->rPrevResPt = new ResHyperPoint)) {
        delete rset;
        return 0;
    }
    rset->rPrevResPt->next = 0;
    rset->tree = kd;

    if ((ret = findNearest(kd->root, key, range, rset->rPrevResPt, 0, kd->dim)) == -1) {
        kdResFree(rset);
        return 0;
    }
    rset->size = ret;
    kdResRearange(rset);
    return rset;
}


static int findNearestInRect(struct HyperNode *node, const double *key, double topLeftX,
                             double topLeftY, double bottomRightX, double bottomRightY,
                             struct ResHyperPoint *list, int ordered, int dim) {
    double dist_sq, dx;
    int i, ret, added_res = 0;

    if (!node) return 0;

    dist_sq = 0;


    if (node->key[0] > topLeftX && node->key[0] < bottomRightX
        && node->key[1] > topLeftY && node->key[1] < bottomRightY) {

        for (i = 0; i < dim; i++) {
            dist_sq += multiply2(node->key[i] - key[i]);
        }

    }

    if (dist_sq != 0) {
        if (resHyperPointInsert(list, node, ordered ? dist_sq : -1.0) == -1) {
            return -1;
        }
        added_res = 1;
    }

    dx = key[node->dir] - node->key[node->dir];

    ret = findNearestInRect(dx <= 0.0 ? node->left : node->right, key, topLeftX, topLeftY,
                            bottomRightX, bottomRightY, list, ordered, dim);
    if (ret >= 0) {
        added_res += ret;
        ret = findNearestInRect(dx <= 0.0 ? node->right : node->left, key, topLeftX, topLeftY,
                                bottomRightX, bottomRightY, list, ordered, dim);
    }

    if (ret == -1) {
        return -1;
    }
    added_res += ret;

    return added_res;
}

struct KdRes *kdGetNearestInRectRange(struct kdtree *kd, const double *key, double topLeftX,
                                      double topLeftY, double bottomRightX, double bottomRightY) {
    int ret;
    struct KdRes *rset;

    if (!(rset = new KdRes)) {
        return 0;
    }
    if (!(rset->rPrevResPt = new ResHyperPoint)) {
        delete rset;
        return 0;
    }
    rset->rPrevResPt->next = 0;
    rset->tree = kd;

    if ((ret = findNearestInRect(kd->root, key, topLeftX, topLeftY, bottomRightX, bottomRightY,
                                 rset->rPrevResPt, 0, kd->dim)) == -1) {
        kdResFree(rset);
        return 0;
    }
    rset->size = ret;
    kdResRearange(rset);
    return rset;
}

void kdResFree(struct KdRes *rset) {
    clearResults(rset);
    delete rset->rPrevResPt;
    delete rset;
}

int kdResSize(struct KdRes *set) {
    return (set->size);
}

void kdResRearange(struct KdRes *rset) {
    rset->rNextResPt = rset->rPrevResPt->next;
}

int kdIsResEnd(struct KdRes *rset) {
    return rset->rNextResPt == 0;
}

int kdResNext(struct KdRes *rset) {
    rset->rNextResPt = rset->rNextResPt->next;
    return rset->rNextResPt != 0;
}

void *kdGetResItem(struct KdRes *rset, double *key) {
    if (rset->rNextResPt) {
        if (key) {
            memcpy(key, rset->rNextResPt->item->key, rset->tree->dim * sizeof *key);
        }
        return rset->rNextResPt->item->data;
    }
    return 0;
}

void *kdGetResItemData(struct KdRes *set) {
    return kdGetResItem(set, 0);
}


static struct HyperRectangle *hyperRectangleCreate(int dim, const double *min, const double *max) {
    size_t size = dim * sizeof(double);
    struct HyperRectangle *rect = 0;

    if (!(rect = new HyperRectangle)) {
        return 0;
    }

    rect->dim = dim;
    if (!(rect->min = new double[size])) {
        delete rect;
        return 0;
    }
    if (!(rect->max = new double[size])) {
        delete[] rect->min;
        delete rect;
        return 0;
    }
    memcpy(rect->min, min, size);
    memcpy(rect->max, max, size);

    return rect;
}

static void hyperRectangleFree(struct HyperRectangle *rect) {
    delete[] rect->min;
    delete[] rect->max;
    delete rect;
}

static struct HyperRectangle *hyperRectangleDuplicate(const struct HyperRectangle *rect) {
    return hyperRectangleCreate(rect->dim, rect->min, rect->max);
}

static void hyperRectangleExtend(struct HyperRectangle *rect, const double *key) {
    int i;

    for (i = 0; i < rect->dim; i++) {
        if (key[i] < rect->min[i]) {
            rect->min[i] = key[i];
        }
        if (key[i] > rect->max[i]) {
            rect->max[i] = key[i];
        }
    }
}

static double hyperRectangleDistanceCalc(struct HyperRectangle *rect, const double *key) {
    int i;
    double result = 0;

    for (i = 0; i < rect->dim; i++) {
        if (key[i] < rect->min[i]) {
            result += multiply2(rect->min[i] - key[i]);
        } else if (key[i] > rect->max[i]) {
            result += multiply2(rect->max[i] - key[i]);
        }
    }

    return result;
}

// heapsort
static int resHyperPointInsert(struct ResHyperPoint *list, struct HyperNode *item, double dist_sq) {
    struct ResHyperPoint *rnode;

    if (!(rnode = new ResHyperPoint)) {
        return -1;
    }
    rnode->item = item;
    rnode->dist_sq = dist_sq;

    if (dist_sq >= 0.0) {
        while (list->next && list->next->dist_sq < dist_sq) {
            list = list->next;
        }
    }
    rnode->next = list->next;
    list->next = rnode;
    return 0;
}

static void clearResults(struct KdRes *rset) {
    struct ResHyperPoint *tmp, *node = rset->rPrevResPt->next;

    while (node) {
        tmp = node;
        node = node->next;
        delete tmp;
    }

    rset->rPrevResPt->next = 0;
}
