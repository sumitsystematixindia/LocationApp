#ifndef Chromosome__H
#define Chromosome__H

#include <vector>
#include "Location.h"
#include "MathUtils.h"

class Chromosome {
private:
    vector<int> city_list;
    double cost;
    double Mutation_probability;
    int length;
    int cut_length;

public:

    Chromosome();

    void create(vector<Location> &cities);

    double get_cost();

    int get_city(int i);

    void set_cities(vector<int> &citylist);

    void calculate_cost(vector<Location> &cities);

    void set_cut(int cut);

    void set_mutation(double prob);

    int mate(Chromosome &father, Chromosome &offspring1,
             Chromosome &offspring2);

};

#endif //Chromosome__H
