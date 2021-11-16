package testmario;

import java.util.Arrays;
import java.util.BitSet;

public class Main {
    public static void main(String[] args) {
        Population population = new Population(GeneticAlgorithm.POPULATION_SIZE).initializePopulation();
        System.out.println("-------------------------------------------------");
        System.out.println("Generation # 0 " + " | Fittest chromosome fitness: " + population.getChromosomes()[0].getFitness());
        printPopulation(population, "Target Chromosome: " + toString(GeneticAlgorithm.getTarget()));
    }
    public static void printPopulation(Population population, String heading){
        System.out.println(heading);
        System.out.println("-------------------------------------------------");
        for(int x = 0; x<population.getChromosomes().length;x++){
            System.out.println("Chromosome # "+ x + " : " + toString(population.getChromosomes()[x].getGenes())+
                    " | Fitness: " + population.getChromosomes()[x].getFitness());
        }
    }
    public static String toString(MyBitSet chromosome){
        StringBuilder s = new StringBuilder();
        for( int i = 0; i < chromosome.realSize();  i++ )
        {
            s.append( chromosome.get(i) == true ? 1: 0 );
        }
        return s.toString();
    }
}
