package testmario;

//sumber kode algoritma genetik dari https://www.youtube.com/watch?v=UcVJsV-tqlo dan kode program yang diberikan ko Lionov di teams

import java.util.Arrays;

public class Population {
    private Chromosome[] chromosomes;
    
    public Population(int length){ //buat populasi dengan jumlah sesuai parameter
        chromosomes = new Chromosome[length];
    }
    public Population initializePopulation(){ //inisialisasi setiap kromosom dengan tembok-tembok sesuai puzzle, dan ubah petak kosong dengan lampu secara random
        for(int i=0;i<chromosomes.length;i++){
            chromosomes[i] = new Chromosome(GeneticAlgorithm.arrSize).initializeChromosome(); //panggil method untuk menginisialisasi kromosom random
        }
        sortChromosomesByFitness(); //sort populasi dengan kromosom fitness terbesar ke terkecil
        return this; //kembalikan populasi yang dihasilkan
    }
    public Chromosome[] getChromosomes(){ //getter populasi
        return chromosomes;
    }
    public void sortChromosomesByFitness(){ //sort populasi dengan kromosom fitness terbesar ke terkecil
        Arrays.sort(chromosomes, (chromosome1, chromosome2) ->{
            int flag = 0;
            if(chromosome1.getFitness() > chromosome2.getFitness()) flag = -1;
            else if(chromosome1.getFitness() < chromosome2.getFitness()) flag = 1;
            return flag;
        });
    }
}
