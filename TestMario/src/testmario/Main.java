package testmario;

//sumber kode algoritma genetik dari https://www.youtube.com/watch?v=UcVJsV-tqlo dan kode program yang diberikan ko Lionov di teams

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static Random rand;
    
    public static void main(String[] args) throws FileNotFoundException {
        rand = new Random(); //buat objek Random untuk seluruh algoritma
        long seed = rand.nextLong(); //buat seed untuk disimpan
        rand.setSeed(seed); //set objek Random dengan seed yang dibuat untuk generasi pertama
        
        //input
        Scanner sc = new Scanner(System.in);
        System.out.print("Insert the number of loop: ");
        int loop = sc.nextInt(); //input banyak loop

        System.out.print("Insert the number of generation for every loop: ");
        int generation = sc.nextInt(); //input banyak generasi pada setiap loop

        System.out.print("Insert the number of population for every generation: ");
        int populationSize = sc.nextInt(); //input banyak populasi pada setiap generasi
        GeneticAlgorithm.POPULATION_SIZE = populationSize;

        System.out.print("Insert the number of population for selection: ");
        int selectionSize = sc.nextInt(); //input banyak populasi seleksi
        GeneticAlgorithm.TOURNAMENT_SELECTION_SIZE = selectionSize;

        System.out.print("Insert the number of elite chromosome: ");
        int eliteSize = sc.nextInt(); //input banyak kromosom elit (jmlh kromosom terbaik yang akan disimpan pada setiap generasi)
        GeneticAlgorithm.NUMB_OF_ELITE_CHROMOSOMES = eliteSize;

        System.out.print("Insert the rate of mutation for every gene (0.0 - 1.0): ");
        double mutationRate = sc.nextDouble(); //input kemungkinan terjadinya mutasi
        GeneticAlgorithm.MUTATION_RATE = mutationRate;

        System.out.println();

        sc = new Scanner(new File("input2.txt"));
        int rowSize = sc.nextInt();
        int colSize = sc.nextInt();
        GeneticAlgorithm.rowSize = rowSize; //isi jumlah baris puzzle dari input text
        GeneticAlgorithm.colSize = colSize; //isi jumlah kolom puzzle dari input text
        GeneticAlgorithm.arrSize = rowSize*colSize; //isi besar array puzzle
        
        GeneticAlgorithm.puzzle = new char[(rowSize*colSize)]; //instansiasi puzzle (array char dengan besar baris*kolom)
        for(int i=0;i<(rowSize*colSize);i++){
            GeneticAlgorithm.puzzle[i] = sc.next().charAt(0); //isi array char/puzzle dengan input text
        }

        for(int i=1;i<=loop;i++){ //lakukan algoritma genetik sebanyak loop input
            seed = rand.nextLong();  //buat seed baru untuk generasi selanjutnya
            rand.setSeed(seed); //set seed baru pada objek Random
            
            Population population = new Population(GeneticAlgorithm.POPULATION_SIZE).initializePopulation(); //buat populasi dengan isi kromosom random
            GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(); //buat objek GeneticAlgorithm untuk memanggil method evolve (crossover dan mutasi populasi)
            int currGen = 1; //simpan generasi saat ini
            while(currGen < generation){ //evolusi populasi sebanyak generasi input
                currGen++;
                population = geneticAlgorithm.evolve(population); //evolusi populasi untuk generasi saat ini
                population.sortChromosomesByFitness(); //sort populasi dari fitness terbesar ke terkecil
            }
            System.out.println("----------------------------------------------------");
            System.out.printf("%d: Fitness = %d Seed: %d%n",i,population.getChromosomes()[0].getFitness(),seed); //print fitness kromosom terbesar dengan seed random saat ini
            printGenes(population.getChromosomes()[0].getGenes()); //print hasil kromosom / solusi puzzle dengan fitness terbesar pada generasi saat ini
        }
    }
    public static void printGenes(char[] genes){ //print kromosom / solusi puzzle yang ditemukan
        int i = 0;
        while(i<genes.length){
            for(int j = 0; j<GeneticAlgorithm.colSize;j++){
                if (genes[i] == 'a'){
                    System.out.print('x');
                }
                else if (genes[i] == 'b'){
                    System.out.print('y');
                }
                else {
                    System.out.print(genes[i]);
                }
                i++;
            }
            System.out.println();
        }
        System.out.println();
    }
}
