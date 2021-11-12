package testmario;

import java.util.BitSet;

public class Chromosome {
    private boolean isFitnessChanged = true;
    private int fitness = 0;
    private BitSet genes;
    public Chromosome(int length){
        genes = new BitSet(length);
        genes.set(length-1, true);
    }
    public Chromosome initializeChromosome(){
        toString();
        for(int i=0;i<genes.length();i++){
            if(Math.random() >= 0.5) genes.set(i, true);
            else genes.set(i, false);
        }
        return this;
    }
    public BitSet getGenes(){
        isFitnessChanged = true;
        return genes;
    }
    public int getFitness(){
        if(isFitnessChanged){
            fitness = recalculateFitness();
            isFitnessChanged = false;
        }
        return fitness;
    }
    public int recalculateFitness(){
        int chromosomeFitness = 0;
        for(int x=0;x<genes.length();x++){
            if(genes.get(x) == GeneticAlgorithm.getTarget().get(x)){
                chromosomeFitness++;
            }
        }
        return chromosomeFitness;
    }
    public String toString(){
        StringBuilder s = new StringBuilder();
        for(int i=0; i<genes.length();i++)
        {
            s.append(genes.get(i) == true ? 1: 0 );
        }
        return s.toString();
    }
}
