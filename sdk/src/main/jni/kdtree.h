#ifndef _KDTREE_H_
#define _KDTREE_H_

#ifdef __cplusplus
extern "C" {
#endif

struct HyperRectangle {
    int dim;
    double *min, *max;
};

struct HyperNode {
    double *key;
    int dir;
    void *data;

    struct HyperNode *left, *right;
};

struct ResHyperPoint {
    struct HyperNode *item;
    double dist_sq;
    struct ResHyperPoint *next;
};

struct kdtree {
    int dim;
    struct HyperNode *root;
    struct HyperRectangle *rect;

    void (*destr)(void *);
};

struct KdRes {
    struct kdtree *tree;
    struct ResHyperPoint *rPrevResPt, *rNextResPt;
    int size;
};


struct kdtree *kdCreate(int k);

void kdFree(struct kdtree *tree);

void kdClear(struct kdtree *tree);

void kdResRearange(struct KdRes *set);

int kdIsResEnd(struct KdRes *set);

int kdResNext(struct KdRes *set);

void *kdGetResItem(struct KdRes *set, double *key);

void *kdGetResItemData(struct KdRes *set);

void kdDataDestructor(struct kdtree *tree, void (*destr)(void *));

int kdInsert(struct kdtree *tree, const double *key, void *data);

struct KdRes *kdNearest(struct kdtree *tree, const double *key);

struct KdRes *kdGetNearestInRange(struct kdtree *tree, const double *key, double range);

struct KdRes *kdGetNearestInRectRange(struct kdtree *tree, const double *key, double topLeftX,
                                      double topLeftY, double bottomRightX, double bottomRightY);

void kdResFree(struct KdRes *set);

int kdResSize(struct KdRes *set);


#ifdef __cplusplus
}
#endif

#endif  /* _KDTREE_H_ */
