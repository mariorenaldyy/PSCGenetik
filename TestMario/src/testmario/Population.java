package testmario;

import java.util.Arrays;

public class Population {
    private Chromosome[] chromosomes;
    
    public Population(int length){
        chromosomes = new Chromosome[length];
    }
    public Population initializePopulation(){
        for(int i=0;i<chromosomes.length;i++){
            chromosomes[i] = new Chromosome(GeneticAlgorithm.arrSize).initializeChromosome();
        }
        sortChromosomesByFitness();
        return this;
    }
    public Chromosome[] getChromosomes(){
        return chromosomes;
    }
    public void sortChromosomesByFitness(){
        Arrays.sort(chromosomes, (chromosome1, chromosome2) ->{
            int flag = 0;
            if(chromosome1.getFitness() > chromosome2.getFitness()) flag = -1;
            else if(chromosome1.getFitness() < chromosome2.getFitness()) flag = 1;
            return flag;
        });
    }
}
