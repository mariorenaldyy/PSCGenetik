package testmario;

//sumber kode algoritma genetik dari https://www.youtube.com/watch?v=UcVJsV-tqlo dan kode program yang diberikan ko Lionov di teams

public class Chromosome {
    private boolean isFitnessChanged = true; //menandakan apakah fitness sudah dihitung (true = belum di hitung)
    private int fitness = 0; //isi fitness kromosom mula-mula dengan 0
    private char[] genes; //isi nilai setiap gen ('x' = petak kosong, 'y' = lampu, 'n' = tembok tanpa angka, '0' tembok dengan angka 0, '1' tembok dengan angka 1, '2' tembok dengan angka 2, '3' tembok dengan angka 3, '4' tembok dengan angka 4)
    public Chromosome(int length){ //konstruktor kromosom untuk membuat array gen dengan besar array/puzzle parameter
        genes = new char[length]; //inisialisasi gen dengan array char kosong
    }
    public Chromosome initializeChromosome(){ //ubah petak kosong pada puzzle input dengan lampu secara random
        char[] puzzle = GeneticAlgorithm.puzzle; //buat kromosom/puzzle baru untuk dikembalikan setelah diinisialisasi secara random
        for(int i=0;i<genes.length;i++){ //lakukan iterasi untuk seluruh jumlah gen
            if(puzzle[i] == 'x'){ //jika gen dgn idx saat ini adalah petak kosong, ubah menjadi lampu secara random
                if(Main.rand.nextFloat() >= 0.5) genes[i] = 'y'; //jika hasil random lebih besar sama dengan 0.5, ubah petak kosong dengan lampu
                else genes[i] = 'x'; //jika hasil random lebih kecil dari 0.5, petak kosong tidak diubah
            }
            else{
                genes[i] = puzzle[i]; //jika gen pada idx saat ini bukan petak kosong (tembok), maka isi kromosom baru dengan tembok yang sama
            }
        }
        return this;
    }
    public char[] getGenes(){ //getter kromosom
        isFitnessChanged = true; //jika kromosom diambil, kromosom dapat berubah, set isFitnessChanged menjadi true agar nantinya fitness dihitung kembali
        return genes;
    }
    public int getFitness(){ //getter fitness kromosom
        if(isFitnessChanged){ //jika fitness belum dihitung, hitung fitnessnya
            fitness = recalculateFitness();
            isFitnessChanged = false; //ubah isFitnessChanged menjadi false agar nantinya fitness tidak perlu dihitung lagi jika method ini dipanggil
        }
        return fitness;
    }
    public int recalculateFitness(){ //hitung fitness kromosom
        int chromosomeFitness = 100; //fitness kromosom mula-mula adalah 100
        for(int x=0;x<genes.length;x++){ //untuk setiap gen, cek apakah ada lampu yang bentrok, tembok yang kekurangan lampu, dan petak yang belum diterangi
            int colSize = GeneticAlgorithm.colSize; //simpan jumlah kolom puzzle pada suatu variabel agar tidak perlu mengakses kelas GeneticAlgorithm berulang kali
            int currMaxColIdx = colSize; //buat variabel untuk menandakan batas kanan dari baris gen yang diakses, mula mula isi dengan jumlah kolom
            while(currMaxColIdx <= x){ //jika gen yang diakses berada pada baris yang berbeda (dibawah) dari baris batas, geser batas ke baris selanjutnya
                currMaxColIdx += currMaxColIdx;
            }
            int currMinColIdx = colSize*(colSize-1) - 1; //buat variabel untuk menandakan batas kiri dari baris gen yang diakses, mula mula isi dengan batas kiri baris terakhir dari puzzle
            while(currMinColIdx >= x){ //jika gen yang diakses berada pada baris yang berbeda (diatas) dari baris batas, geser batas ke baris sebelumnya
                currMinColIdx -= colSize;
            }
            if(genes[x] == 'y'){ //jika idx arr ini berisi lampu, cek apakah bentrok dengan lampu lain
                int y = x;
                while(true){ //cek petak-petak di sebelah kanan apakah ada lampu yang menerangi lampu ini (bentrok)
                    y++;
                    if(y < genes.length && y < currMaxColIdx){ //jika petak yang diakses belum melebihi batas kanan baris saat ini, lakukan pengecekan
                        if(genes[y] == 'n' || genes[y] == '0' || genes[y] == '1' || genes[y] == '2' || genes[y] == '3' || genes[y] == '4'){ //jika ditemukan tembok, fitness tidak dikurangi (tidak bentrok)
                            break;
                        }
                        else if(genes[y] == 'y'){ //jika lampu ini bentrok dengan lampu lain, kurangi fitness
                            chromosomeFitness--;
                        }
                    }
                    else{
                        break;
                    }
                }
                
                y=x;
                while(true){ //cek petak-petak di sebelah kiri apakah ada lampu yang menerangi lampu ini (bentrok)
                    y--;
                    if(y > -1 && y > currMinColIdx){ //jika petak yang diakses belum melebihi batas kiri baris saat ini, lakukan pengecekan
                        if(genes[y] == 'n' || genes[y] == '0' || genes[y] == '1' || genes[y] == '2' || genes[y] == '3' || genes[y] == '4'){ //jika ditemukan tembok, fitness tidak dikurangi (tidak bentrok)
                            break;
                        }
                        else if(genes[y] == 'y'){ //jika lampu ini bentrok dengan lampu lain, kurangi fitness
                            chromosomeFitness--;
                        }
                    }
                    else{
                        break;
                    }
                }
                
                y=x;
                while(true){ //cek petak-petak di atas apakah ada lampu yang menerangi lampu ini (bentrok)
                    y -= colSize;
                    if(y > -1){ //jika petak yang diakses belum melebihi batas atas puzzle, lakukan pengecekan
                        if(genes[y] == 'n' || genes[y] == '0' || genes[y] == '1' || genes[y] == '2' || genes[y] == '3' || genes[y] == '4'){ //jika ditemukan tembok, fitness tidak dikurangi (tidak bentrok)
                            break;
                        }
                        else if(genes[y] == 'y'){ //jika lampu ini bentrok dengan lampu lain, kurangi fitness
                            chromosomeFitness--;
                        }
                    }
                    else{
                        break;
                    }
                }
                
                y=x;
                while(true){ //cek petak-petak di bawah apakah ada lampu yang menerangi lampu ini (bentrok)
                    y += colSize;
                    if(y < GeneticAlgorithm.arrSize){ //jika petak yang diakses belum melebihi batas bawah puzzle, lakukan pengecekan
                        if(genes[y] == 'n' || genes[y] == '0' || genes[y] == '1' || genes[y] == '2' || genes[y] == '3' || genes[y] == '4'){ //jika ditemukan tembok, fitness tidak dikurangi (tidak bentrok)
                            break;
                        }
                        else if(genes[y] == 'y'){ //jika lampu ini bentrok dengan lampu lain, kurangi fitness
                            chromosomeFitness--;
                        }
                    }
                    else{
                        break;
                    }
                }
            }
            else if(genes[x] != 'x'){ //jika idx arr ini berisi tembok, cek apakah jumlah lampu disekitarnya sesuai dengan angka tembok
                if(genes[x] != 'n'){ //jika tembok memiliki angka, hitung jumlah lampu disekitarnya, kurangi fitness jika kekurangan atau kelebihan
                    int angkaTembok = Character.getNumericValue(genes[x]);
                    int countBulb = 0;
                    int y = x;
                    if(x+1 < genes.length && x+1 < currMaxColIdx){ //cek kanan apakah ada lampu
                        if(genes[x+1] == 'y'){
                            countBulb++; //tambahkan jumlah lampu jika ditemukan
                        }
                    }
                    if(x-1 > -1 && x-1 > currMinColIdx){ //cek kiri apakah ada lampu
                        if(genes[x-1] == 'y'){
                            countBulb++; //tambahkan jumlah lampu jika ditemukan
                        }
                    }
                    if(x-colSize > -1){ //cek atas apakah ada lampu
                        if(genes[x-colSize] == 'y'){
                            countBulb++; //tambahkan jumlah lampu jika ditemukan
                        }
                    }
                    if(x+colSize < genes.length){ //cek bawah apakah ada lampu
                        if(genes[x+colSize] == 'y'){
                            countBulb++; //tambahkan jumlah lampu jika ditemukan
                        }
                    }
                    
                    if(countBulb < angkaTembok){ //jika lampu disekitar kurang dari angka tembok, kurangi fitness
                        chromosomeFitness -= (angkaTembok-countBulb); //fitness -1 untuk setiap lampu yang kurang
                    }
                    else if(countBulb > angkaTembok){ //jika lampu disekitar lebih dari angka tembok, kurangi fitness
                        chromosomeFitness -= (countBulb-angkaTembok); //fitness -1 untuk setiap lampu yang lebih
                    }
                }
            }
            else{ //jika idx arr ini berisi petak kosong, cek apakah ada lampu yang menerangi (memastikan seluruh petak sudah diterangi)
                int y = x;
                boolean bulbFound = false;
                while(!bulbFound){ //cek petak-petak di sebelah kanan apakah ada lampu yang menerangi petak ini
                    y++;
                    if(y < genes.length && y < currMaxColIdx){
                        if(genes[y] == 'n' || genes[y] == '0' || genes[y] == '1' || genes[y] == '2' || genes[y] == '3' || genes[y] == '4'){ //jika ditemukan tembok, nilai bulbFound tidak diubah (lampu belum ditemukan)
                            break;
                        }
                        else if(genes[y] == 'y'){
                            bulbFound = true; //jika ditemukan lampu, ubah bulbFound menjadi true dan hentikan looping (petak sudah diterangi)
                            break;
                        }
                    }
                    else{
                        break;
                    }
                }
                
                y=x;
                while(!bulbFound){ //cek petak-petak di sebelah kiri apakah ada lampu yang menerangi petak ini
                    y--;
                    if(y > -1 && y > currMinColIdx){
                        if(genes[y] == 'n' || genes[y] == '0' || genes[y] == '1' || genes[y] == '2' || genes[y] == '3' || genes[y] == '4'){ //jika ditemukan tembok, nilai bulbFound tidak diubah (lampu belum ditemukan)
                            break;
                        }
                        else if(genes[y] == 'y'){
                            bulbFound = true; //jika ditemukan lampu, ubah bulbFound menjadi true dan hentikan looping (petak sudah diterangi)
                            break;
                        }
                    }
                    else{
                        break;
                    }
                }
                
                y=x;
                while(!bulbFound){ //cek petak-petak di atas apakah ada lampu yang menerangi petak ini
                    y -= colSize;
                    if(y > -1){
                        if(genes[y] == 'n' || genes[y] == '0' || genes[y] == '1' || genes[y] == '2' || genes[y] == '3' || genes[y] == '4'){ //jika ditemukan tembok, nilai bulbFound tidak diubah (lampu belum ditemukan)
                            break;
                        }
                        else if(genes[y] == 'y'){
                            bulbFound = true; //jika ditemukan lampu, ubah bulbFound menjadi true dan hentikan looping (petak sudah diterangi)
                            break;
                        }
                    }
                    else{
                        break;
                    }
                }
                
                y=x;
                while(!bulbFound){ //cek petak-petak di bawah apakah ada lampu yang menerangi petak ini
                    y += colSize;
                    if(y < GeneticAlgorithm.arrSize){
                        if(genes[y] == 'n' || genes[y] == '0' || genes[y] == '1' || genes[y] == '2' || genes[y] == '3' || genes[y] == '4'){ //jika ditemukan tembok, nilai bulbFound tidak diubah (lampu belum ditemukan)
                            break;
                        }
                        else if(genes[y] == 'y'){
                            bulbFound = true; //jika ditemukan lampu, ubah bulbFound menjadi true dan hentikan looping (petak sudah diterangi)
                            break;
                        }
                    }
                    else{
                        break;
                    }
                }
                if(bulbFound == false){ //jika pada sisi atas, bawah, kiri, dan kanan tidak ditemukan lampu, kurangi fitness
                    chromosomeFitness--;
                }
            }
        }
        return chromosomeFitness; //kembalikan hasil fitness untuk kromosom ini
    }
}
