package testmario;

//sumber kode algoritma genetik dari https://www.youtube.com/watch?v=UcVJsV-tqlo dan kode program yang diberikan ko Lionov di teams

public class GeneticAlgorithm {
    public static int POPULATION_SIZE; //jumlah populasi untuk setiap generasi
    public static int rowSize; //jumlah baris pada puzzle (didapatkan dari input)
    public static int colSize; //jumlah kolom pada puzzle (didapatkan dari input)
    public static char[] puzzle; //array char yang berisi petak atau tembok pada puzzle sesuai input
    public static int arrSize; //jumlah petak dan tembok pada puzzle
    public static double MUTATION_RATE; //rate dilakukannya mutasi
    public static int NUMB_OF_ELITE_CHROMOSOMES; //jumlah kromosom elit (fitness terbesar) yang akan disimpan untuk setiap generasi
    public static int TOURNAMENT_SELECTION_SIZE; //besarnya populasi seleksi
    
    public Population evolve(Population population){ //crossover dan mutasi populasi
        return mutatePopulation(crossoverPopulation(population));
    }
    private Population crossoverPopulation(Population population){ //buat populasi dengan kromosom elit dan hasil crossover
        Population crossoverPopulation = new Population(population.getChromosomes().length); //buat populasi baru untuk dikembalikan setelah crossover
        for(int i = 0; i<NUMB_OF_ELITE_CHROMOSOMES;i++){ //jika kromosom pada index adalah elit, tidak dicrossover
            crossoverPopulation.getChromosomes()[i] = population.getChromosomes()[i];
        }
        for(int i = NUMB_OF_ELITE_CHROMOSOMES; i<population.getChromosomes().length;i++){ //jika kromosom pada index bukan elit, lakukan crossover
            Chromosome chromosome1 = selectTournamentPopulation(population).getChromosomes()[0]; //lakukan seleksi populasi dan ambil fitness terbesar
            Chromosome chromosome2 = selectTournamentPopulation(population).getChromosomes()[0]; //lakukan seleksi populasi kedua dan ambil fitness terbesar
            crossoverPopulation.getChromosomes()[i] = crossoverChromosome(chromosome1, chromosome2); //crossover kromosom dari hasil kedua seleksi
        }
        return crossoverPopulation; //kembalikan populasi setelah crossover
    }
    private Population mutatePopulation(Population population){ //mutasi populasi
        Population mutatePopulation = new Population(population.getChromosomes().length); //buat populasi baru untuk dikembalikan setelah mutasi
        for(int i = 0; i<NUMB_OF_ELITE_CHROMOSOMES;i++){ //jika kromosom pada index adalah elit, tidak dimutasi
            mutatePopulation.getChromosomes()[i] = population.getChromosomes()[i];
        }
        for(int i = NUMB_OF_ELITE_CHROMOSOMES; i<population.getChromosomes().length;i++){ //jika kromosom pada index bukan elit, lakukan mutasi
            mutatePopulation.getChromosomes()[i] = mutateChromosome(population.getChromosomes()[i]); //panggil method mutasi kromosom
        }
        return mutatePopulation; //kembalikan populasi setelah mutasi
    }
    private Chromosome crossoverChromosome(Chromosome chromosome1, Chromosome chromosome2){ //crossover kromosom dari hasil seleksi pada method crossoverPopulation
        Chromosome crossoverChromosome = new Chromosome(arrSize); //buat kromosom baru untuk dikembalikan setelah crossover
        for(int i = 0; i<chromosome1.getGenes().length;i++){ //isi setiap gen kromosom baru dengan gen kromosom induk 1 atau kromosom induk 2 secara random
            if(Main.rand.nextFloat() < 0.5) crossoverChromosome.getGenes()[i] = chromosome1.getGenes()[i]; //jika hasil random lebih kecil dari 0.5, isi gen kromosom baru dengan gen kromosom induk 1 pada index yang sama
            else crossoverChromosome.getGenes()[i] = chromosome2.getGenes()[i]; //jika hasil random lebih besar sama dengan 0.5, isi gen kromosom baru dengan gen kromosom induk 2 pada index yang sama
        }
        return crossoverChromosome; //kembalikan hasil kromosom crossover
    }
    private Chromosome mutateChromosome(Chromosome chromosome){ //mutasi kromosom bukan elit
        Chromosome mutateChromosome = new Chromosome(arrSize); //buat kromosom baru untuk dikembalikan setelah mutasi
        for(int i = 0; i<chromosome.getGenes().length;i++){ //lakukan random mutasi setiap gen yang berisi petak kosong atau lampu
            if(chromosome.getGenes()[i] == 'x' || chromosome.getGenes()[i] == 'y'){ //jika gen dengan idx saat ini adalah petak kosong atau lampu, lakukan mutasi
                if(Main.rand.nextFloat() < MUTATION_RATE){ //jika hasil random lebih kecil dari rate mutasi, lakukan mutasi
                    if(Main.rand.nextFloat() < 0.5) mutateChromosome.getGenes()[i] = 'y'; //jika hasil random lebih kecil dari 0.5, mutasi/isi gen idx ini dengan lampu
                    else mutateChromosome.getGenes()[i] = 'x'; //jika hasil random lebih besar sama dengan 0.5, mutasi/isi gen idx ini dengan petak kosong
                } else mutateChromosome.getGenes()[i] = chromosome.getGenes()[i]; //jika hasil random lebih besar atau sama dengan rate mutasi, mutasi tidak dilakukan untuk gen idx ini (isi gen mutateChromosome dengan gen chromosome awal)
            }
            else{
                mutateChromosome.getGenes()[i] = chromosome.getGenes()[i]; //jika gen pada idx adalah tembok, tidak dilakukan mutasi
            }
        }
        return mutateChromosome; //kembalikan kromosom hasil mutasi
    }
    private Population selectTournamentPopulation(Population population){ //buat populasi seleksi untuk dicrossover
        Population tournamentPopulation = new Population(TOURNAMENT_SELECTION_SIZE); //buat populasi seleksi untuk dikembalikan
        for(int i = 0; i<TOURNAMENT_SELECTION_SIZE;i++){ //isi populasi seleksi dengan kromosom random dari populasi pada generasi ini
            tournamentPopulation.getChromosomes()[i] = population.getChromosomes()[(int)(Main.rand.nextFloat()*population.getChromosomes().length)]; //pilih kromosom dari populasi secara random dan masukkan ke populasi seleksi
        }
        tournamentPopulation.sortChromosomesByFitness(); //sort populasi seleksi dari fitness terbesar ke terkecil
        return tournamentPopulation; //kembalikan hasil populasi seleksi
    }
}
