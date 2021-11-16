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
    public static final double MUTATION_RATE = 0.25;
    public static final int NUMB_OF_ELITE_CHROMOSOMES = 1;
    public static final int TOURNAMENT_SELECTION_SIZE = 4;
    
    public Population evolve(Population population){
        return mutatePopulation(crossoverPopulation(population));
    }
    private Population crossoverPopulation(Population population){
        Population crossoverPopulation = new Population(population.getChromosomes().length);
        for(int i = 0; i<NUMB_OF_ELITE_CHROMOSOMES;i++){
            crossoverPopulation.getChromosomes()[i] = population.getChromosomes()[i];
        }
        for(int i = NUMB_OF_ELITE_CHROMOSOMES; i<population.getChromosomes().length;i++){
            Chromosome chromosome1 = selectTournamentPopulation(population).getChromosomes()[0];
            Chromosome chromosome2 = selectTournamentPopulation(population).getChromosomes()[0];
            crossoverPopulation.getChromosomes()[i] = crossoverChromosome(chromosome1, chromosome2);
        }
        return crossoverPopulation;
    }
    private Population mutatePopulation(Population population){
        Population mutatePopulation = new Population(population.getChromosomes().length);
        for(int i = 0; i<NUMB_OF_ELITE_CHROMOSOMES;i++){
            mutatePopulation.getChromosomes()[i] = population.getChromosomes()[i];
        }
        for(int i = NUMB_OF_ELITE_CHROMOSOMES; i<population.getChromosomes().length;i++){
            mutatePopulation.getChromosomes()[i] = mutateChromosome(population.getChromosomes()[i]);
        }
        return mutatePopulation;
    }
    private Chromosome crossoverChromosome(Chromosome chromosome1, Chromosome chromosome2){
        Chromosome crossoverChromosome = new Chromosome(getTarget().realSize());
        for(int i = 0; i<chromosome1.getGenes().realSize();i++){
            if(Math.random() < 0.5) crossoverChromosome.getGenes().set(i, chromosome1.getGenes().get(i));
            else crossoverChromosome.getGenes().set(i, chromosome2.getGenes().get(i));
        }
        return crossoverChromosome;
    }
    private Chromosome mutateChromosome(Chromosome chromosome){
        Chromosome mutateChromosome = new Chromosome(getTarget().realSize());
        for(int i = 0; i<chromosome.getGenes().realSize();i++){
            if(Math.random() < MUTATION_RATE){
                if(Math.random() < 0.5) mutateChromosome.getGenes().set(i, true);
                else mutateChromosome.getGenes().set(i, false);
            } else mutateChromosome.getGenes().set(i, chromosome.getGenes().get(i));
        }
        return mutateChromosome;
    }
    private Population selectTournamentPopulation(Population population){
        Population tournamentPopulation = new Population(TOURNAMENT_SELECTION_SIZE);
        for(int i = 0; i<TOURNAMENT_SELECTION_SIZE;i++){
            tournamentPopulation.getChromosomes()[i] = population.getChromosomes()[(int)(Math.random()*population.getChromosomes().length)];
        }
        tournamentPopulation.sortChromosomesByFitness();
        return tournamentPopulation;
    }
}
