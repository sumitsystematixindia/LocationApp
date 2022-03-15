#include "Chromosome.h"

using namespace std;

Chromosome::Chromosome() :
        length(0), cost(0), cut_length(0), Mutation_probability(0) {
}

void Chromosome::create(vector<Location> &cities) {
    length = (int) cities.size();

    city_list.clear();

    vector<bool> taken(length, false);
    for (int i = 0; i < length; i++) {
        city_list.push_back(0);
    }

    cost = 0.0;

    for (int i = 0; i < length - 1; i++) {
        int icandidate;
        do {
            icandidate = (int) (0.999999 * MathUtils::getRandom()
                                * (double) length);
            //printf("%d",icandidate);
        } while (taken[icandidate]);
        city_list[i] = icandidate;
        taken[icandidate] = true;
        if (i == length - 2) {
            icandidate = 0;
            while (taken[icandidate])
                icandidate++;
            city_list[i + 1] = icandidate;
        }
    }


    // index of origin
    int idx = -1;
    for (int i = 0; i < length; i++) {
        if (city_list[i] == 0) {
            idx = i;
            break;
        }
    }

    int temp = city_list[0];
    city_list[0] = 0;
    city_list[idx] = temp;


    calculate_cost(cities);
    cut_length = 1;
}

void Chromosome::calculate_cost(vector<Location> &cities) {
    cost = cities[city_list[0]].proximity();
    for (int i = 0; i < length - 1; i++) {
        double dist = cities[city_list[i]].proximity(cities[city_list[i + 1]]);
        cost += dist;
    }
}

double Chromosome::get_cost() {
    return cost;
}

int Chromosome::get_city(int i) {
    return city_list[i];
}

void Chromosome::set_cities(vector<int> &citylist) {

    for (int i = 0; i < length; i++) {
        city_list[i] = citylist[i];
    }
}

void Chromosome::set_cut(int cut) {
    cut_length = cut;
}

void Chromosome::set_mutation(double prob) {
    Mutation_probability = prob;
}

int Chromosome::mate(Chromosome &father, Chromosome &offspring1,
                     Chromosome &offspring2) {
    int cutpoint1 = (int) (0.999999 * MathUtils::getRandom()
                           * (double) (length - cut_length));
    int cutpoint2 = cutpoint1 + cut_length;

    // System.out.print("\nMother: "); print();
    // System.out.print("\nFather: "); father.print();
    // System.out.println("\nCutpoints: "+cutpoint1+" "+cutpoint2);

    vector<bool> taken1(length, false);
    vector<bool> taken2(length, false);

    vector<int> off1(length, 0);
    vector<int> off2(length, 0);

    for (int i = 0; i < length; i++) {
        if (i < cutpoint1 || i >= cutpoint2) {
            off1[i] = -1;
            off2[i] = -1;
        } else {
            int imother = city_list[i];
            int ifather = father.get_city(i);
            off1[i] = ifather;
            off2[i] = imother;
            taken1[ifather] = true;
            taken2[imother] = true;
        }
    }

    // System.out.print("\nOff1  : "); offspring1.print();
    // System.out.print("\nOff2  : "); offspring2.print();

    for (int i = 0; i < cutpoint1; i++) {
        if (off1[i] == -1) {
            for (int j = 0; j < length; j++) {
                int imother = city_list[j];
                if (!taken1[imother]) {
                    off1[i] = imother;
                    taken1[imother] = true;
                    break;
                }
            }
        }
        if (off2[i] == -1) {
            for (int j = 0; j < length; j++) {
                int ifather = father.get_city(j);
                if (!taken2[ifather]) {
                    off2[i] = ifather;
                    taken2[ifather] = true;
                    break;
                }
            }
        }
    }
    for (int i = length - 1; i >= cutpoint2; i--) {
        if (off1[i] == -1) {
            for (int j = length - 1; j >= 0; j--) {
                int imother = city_list[j];
                if (!taken1[imother]) {
                    off1[i] = imother;
                    taken1[imother] = true;
                    break;
                }
            }
        }
        if (off2[i] == -1) {
            for (int j = length - 1; j >= 0; j--) {
                int ifather = father.get_city(j);
                if (!taken2[ifather]) {
                    off2[i] = ifather;
                    taken2[ifather] = true;
                    break;
                }
            }
        }
    }

    offspring1.set_cities(off1);
    offspring2.set_cities(off2);

    // System.out.print("\nOff1  : "); offspring1.print();
    // System.out.print("\nOff2  : "); offspring2.print();

    int mutate = 0;
    if (MathUtils::getRandom() < Mutation_probability) {
        int iswap1 = (int) (0.999999 * MathUtils::getRandom()
                            * (double) (length));
        int iswap2 = (int) (0.999999 * MathUtils::getRandom() * (double) length);
        int i = off1[iswap1];
        off1[iswap1] = off1[iswap2];
        off1[iswap2] = i;
        mutate++;
    }
    if (MathUtils::getRandom() < Mutation_probability) {
        int iswap1 = (int) (0.999999 * MathUtils::getRandom()
                            * (double) (length));
        int iswap2 = (int) (0.999999 * MathUtils::getRandom() * (double) length);
        int i = off2[iswap1];
        off2[iswap1] = off2[iswap2];
        off2[iswap2] = i;
        mutate++;
    }
    // System.out.print("\nOff1  : "); offspring1.print();
    // System.out.print("\nOff2  : "); offspring2.print();
    return mutate;
}

