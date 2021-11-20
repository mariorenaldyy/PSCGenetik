package testmario;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static Random rand;
    
    public static void main(String[] args) throws FileNotFoundException {
        rand = new Random();
        long seed = rand.nextLong();
        rand.setSeed(seed);
        
        //input
        Scanner sc = new Scanner(new File("input2.txt"));
        int generationNum = sc.nextInt();
        int rowSize = sc.nextInt();
        int colSize = sc.nextInt();
        GeneticAlgorithm.rowSize = rowSize;
        GeneticAlgorithm.colSize = colSize;
        GeneticAlgorithm.arrSize = rowSize*colSize;
        
        GeneticAlgorithm.puzzle = new char[(rowSize*colSize)];
        for(int i=0;i<(rowSize*colSize);i++){
            GeneticAlgorithm.puzzle[i] = sc.next().charAt(0);
        }
        
        sc = new Scanner(System.in);
        int loop = sc.nextInt();
        for(int i=1;i<=loop;i++){
            seed = rand.nextLong();
            rand.setSeed(seed);
            
            Population population = new Population(GeneticAlgorithm.POPULATION_SIZE).initializePopulation();
            GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
    //        System.out.println("-------------------------------------------------");
    //        System.out.println("Generation # 1" + " | Fittest chromosome fitness: " + population.getChromosomes()[0].getFitness());
    //        printPopulation(population);
            int currGen = 1;
            while(currGen < generationNum){
                currGen++;
    //            System.out.println("\n-------------------------------------------------");
                population = geneticAlgorithm.evolve(population);
                population.sortChromosomesByFitness();
    //            System.out.println("Generation # " +generationNumber+" | Fittest chromosome fitness: " + population.getChromosomes()[0].getFitness());
    //            printPopulation(population);
            }
            System.out.printf("%2d: Fitness = %d (%s) Seed: %d%n",i,population.getChromosomes()[0].getFitness(),toString(population.getChromosomes()[0].getGenes()),seed);
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
