package testmario;

import java.math.BigInteger;

public class GeneticAlgorithm {
    public static final int POPULATION_SIZE = 8;
    public static MyBitSet getTarget(){
        MyBitSet target = new MyBitSet(41);
        target.set(3, true);
        target.set(5, true);
        target.set(13, true);
        target.set(23, true);
        target.set(24, true);
        target.set(31, true);
        target.set(37, true);
        target.set(39, true);
        target.set(40, true);
        return target;
    }
    private Population evolve(Population population){
        return mutatePopulation(crossoverPopulation(population));
    }
    private Population crossoverPopulation(Population population){
        return population;
    }
    private Population mutatePopulation(Population population){
        return population;
    }
}
