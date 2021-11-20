package testmario;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        Population population = new Population(GeneticAlgorithm.POPULATION_SIZE).initializePopulation();
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
        System.out.println("-------------------------------------------------");
        System.out.println("Generation # 0" + " | Fittest chromosome fitness: " + population.getChromosomes()[0].getFitness());
        printPopulation(population);
        int generationNumber = 0;
        while(population.getChromosomes()[0].getFitness() < 100){
            generationNumber++;
            System.out.println("\n-------------------------------------------------");
            population = geneticAlgorithm.evolve(population);
            population.sortChromosomesByFitness();
            System.out.println("Generation # " +generationNumber+" | Fittest chromosome fitness: " + population.getChromosomes()[0].getFitness());
            printPopulation(population);
        }
    }
    public static void printPopulation(Population population){
        System.out.println("-------------------------------------------------");
        for(int x = 0; x<population.getChromosomes().length;x++){
            System.out.println("Chromosome # "+ x + " : " + toString(population.getChromosomes()[x].getGenes())+
                    " | Fitness: " + population.getChromosomes()[x].getFitness());
        }
    }
    public static String toString(char[] chromosome){
        StringBuilder s = new StringBuilder();
        for( int i = 0; i < chromosome.length;  i++ )
        {
            s.append(chromosome[i]);
        }
        return s.toString();
    }
}
