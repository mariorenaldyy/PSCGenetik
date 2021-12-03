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
            if(puzzle[i] == 'x' && genes[i] != 'a' && genes[i] != 'b'){ //jika gen dgn idx saat ini adalah petak kosong, ubah menjadi lampu secara random
                boolean secluded = checkSecluded(i, puzzle); //cek apakah suatu petak kosong dikelilingi tembok (pasti akan diisi lampu)
                if(!secluded){ //jika petak tidak dikelilingi tembok, ubah menjadi lampu secara random
                    if(Main.rand.nextFloat() >= 0.5) genes[i] = 'y'; //jika hasil random lebih besar sama dengan 0.5, ubah petak kosong dengan lampu
                    else genes[i] = 'x'; //jika hasil random lebih kecil dari 0.5, petak kosong tidak diubah
                }
                else{ //jika petak dikelilingi lampu, ubah menjadi 'b' (lampu yang tidak boleh dimutasi)
                    genes[i] = 'b';
                }
            }
            else{
                if(genes[i] != 'a' && genes[i] != 'b' ) { //jika gen pda idx yg diakses bukan 'a' (petak kosong yang tidak boleh dimutasi) dan 'b' (lampu yang tidak boleh dimutasi), samakan nilai genes[i] dengan puzzle[i] (tembok)
                    genes[i] = puzzle[i];
                }
                if(puzzle[i] == '4'){ //jika idx i pada puzzle adalah tembok berangka 4, ubah petak disekitarnya menjadi lampu ('b') dan ubah petak diagonal menjadi petak kosong ('a')
                    putLightAdjacent(i, puzzle);
                    putEmptyDiagonally(i, puzzle, true, true, true, true);
                }
                else if(puzzle[i] == '0'){ //jika idx i pada puzzle adalah tembok berangka 0, ubah petak disekitarnya menjadi petak kosong ('a')
                    putEmptyAdjacent(i, puzzle);
                }
                else if(puzzle[i] == '3'){ //jika idx i pada puzzle adalah tembok berangka 3, ubah petak diagonal menjadi petak kosong ('a') dan jika jumlah petak disekitarnya sama dengan angka tembok, isi dengan lampu ('b')
                    putEmptyDiagonally(i, puzzle, true, true, true, true);
                    putLightIfGuaranteed(i, puzzle);
                }
                else if(puzzle[i] == '2'){ //jika idx i pada puzzle adalah tembok berangka 2, dan petak disekitarnya berjumlah < 4, ubah petak diagonal menjadi petak kosong ('a'), dan jika jumlah petak disekitarnya sama dengan angka tembok, isi dengan lampu ('b')
                    putEmptyDiagonallyIfRestricted(i, puzzle);
                    putLightIfGuaranteed(i, puzzle);
                }
                else if(puzzle[i] == '1'){ //jika idx i pada puzzle adalah tembok berangka 1, dan jumlah petak disekitarnya sama dengan angka tembok, isi dengan lampu ('b')
                    putLightIfGuaranteed(i, puzzle);
                }
            }
        }
        return this;
    }

    private void putEmptyDiagonallyIfRestricted(int i, char[] puzzle) { //method untuk mengganti petak diagonal menjadi kosong 'a' jika tembok berangka 2 terblokir pada suatu sisinya
        int colSize = GeneticAlgorithm.colSize; //dapatkan jumlah kolom puzzle
        int currMaxColIdx = getMaxColIdx(colSize, i); //dapatkan batas kanan dari baris gen yang diakses
        int currMinColIdx = getMinColIdx(colSize, i); //dapatkan batas kiri dari baris gen yang diakses

        int numOfGrid = 0; //variabel menghitung jumlah petak
        boolean rightBlock = true; //variabel menentukan apakah bagian kanan petak di blokir (ada tembok atau tepi puzzle)
        boolean leftBlock = true; //variabel menentukan apakah bagian kiri petak di blokir (ada tembok atau tepi puzzle)
        boolean topBlock = true; //variabel menentukan apakah bagian atas petak di blokir (ada tembok atau tepi puzzle)
        boolean bottomBlock = true; //variabel menentukan apakah bagian bawah petak di blokir (ada tembok atau tepi puzzle)
        if(i+1 < puzzle.length && i+1 < currMaxColIdx){ //cek kanan apakah ada petak yang dapat diisi
            if(genes[i+1] != 'a' && puzzle[i+1] == 'x' || puzzle[i+1] == 'y'){ //jika berisi petak atau lampu (dan bukan petak kosong yang tidak bisa dimutasi)
                numOfGrid++; //tambahkan jumlah petak
                rightBlock = false; //kanan tidak terblokir
            }
        }
        if(i-1 > -1 && i-1 > currMinColIdx){ //cek kiri apakah ada petak yang dapat diisi
            if(genes[i-1] != 'a' && puzzle[i-1] == 'x' || puzzle[i-1] == 'y'){ //jika berisi petak atau lampu (dan bukan petak kosong yang tidak bisa dimutasi)
                numOfGrid++; //tambahkan jumlah petak
                leftBlock = false; //kiri tidak terblokir
            }
        }
        if(i-colSize > -1){ //cek atas apakah ada petak yang dapat diisi
            if(genes[i-colSize] != 'a' && puzzle[i-colSize] == 'x' || puzzle[i-colSize] == 'y'){ //jika berisi petak atau lampu (dan bukan petak kosong yang tidak bisa dimutasi)
                numOfGrid++; //tambahkan jumlah petak
                topBlock = false; //atas tidak terblokir
            }
        }
        if(i+colSize < puzzle.length){ //cek bawah apakah ada petak yang dapat diisi
            if(genes[i+colSize] != 'a' && puzzle[i+colSize] == 'x' || puzzle[i+colSize] == 'y'){ //jika berisi petak atau lampu (dan bukan petak kosong yang tidak bisa dimutasi)
                numOfGrid++; //tambahkan jumlah petak
                bottomBlock = false; //bawah tidak terblokir
            }
        }
        if(numOfGrid < 4){ //jika jumlah petak disekitar lebih kecil dari 4
            if(topBlock){ //jika atas terblokir
                putEmptyDiagonally(i, puzzle, false, false, true, true); //ganti diagonal kiri bawah dan kanan bawah menjadi petak kosong
            }
            if(bottomBlock){ //jika bawah terblokir
                putEmptyDiagonally(i, puzzle, true, true, false, false); //ganti diagonal kiri atas dan kanan atas menjadi petak kosong
            }
            if(rightBlock){ //jika kanan terblokir
                putEmptyDiagonally(i, puzzle, true, false, true, false); //ganti diagonal kiri atas dan kiri bawah menjadi petak kosong
            }
            if(leftBlock){ //jika kiri terblokir
                putEmptyDiagonally(i, puzzle, false, true, false, true); //ganti diagonal kanan atas dan kanan bawah menjadi petak kosong
            }
        }
    }

    private void putLightIfGuaranteed(int i, char[] puzzle) { //method untuk mengganti petak disekitar menjadi lampu jika jumlah petak disekitar sama dengan angka tembok
        int colSize = GeneticAlgorithm.colSize; //dapatkan jumlah kolom puzzle
        int currMaxColIdx = getMaxColIdx(colSize, i); //dapatkan batas kanan dari baris gen yang diakses
        int currMinColIdx = getMinColIdx(colSize, i); //dapatkan batas kiri dari baris gen yang diakses

        int numOfGrid = 0; //variabel menghitung jumlah petak
        if(i+1 < puzzle.length && i+1 < currMaxColIdx){ //cek kanan apakah ada petak
            if(genes[i+1] != 'a' && puzzle[i+1] == 'x' || puzzle[i+1] == 'y'){ //jika berisi petak atau lampu (dan bukan petak kosong yang tidak bisa dimutasi)
                numOfGrid++; //tambahkan jumlah petak
            }
        }
        if(i-1 > -1 && i-1 > currMinColIdx){ //cek kiri apakah ada petak
            if(genes[i-1] != 'a' && puzzle[i-1] == 'x' || puzzle[i-1] == 'y'){ //jika berisi petak atau lampu (dan bukan petak kosong yang tidak bisa dimutasi)
                numOfGrid++; //tambahkan jumlah petak
            }
        }
        if(i-colSize > -1){ //cek atas apakah ada petak
            if(genes[i-colSize] != 'a' && puzzle[i-colSize] == 'x' || puzzle[i-colSize] == 'y'){ //jika berisi petak atau lampu (dan bukan petak kosong yang tidak bisa dimutasi)
                numOfGrid++; //tambahkan jumlah petak
            }
        }
        if(i+colSize < puzzle.length){ //cek bawah apakah ada petak
            if(genes[i+colSize] != 'a' && puzzle[i+colSize] == 'x' || puzzle[i+colSize] == 'y'){ //jika berisi petak atau lampu (dan bukan petak kosong yang tidak bisa dimutasi)
                numOfGrid++; //tambahkan jumlah petak
            }
        }
        int angkaTembok = Character.getNumericValue(genes[i]); //dapatkan angka tembok
        if(angkaTembok == numOfGrid){ //jika angka tembok sama dengan jumlah petak disekitarnya, ubah petak disekitar menjadi lampu
            putLightAdjacent(i, puzzle);
        }
    }

    private void putEmptyDiagonally(int i, char[] puzzle, boolean topLeft, boolean topRight, boolean bottomLeft, boolean bottomRight) { //method untuk mengganti petak diagonal menjadi petak kosong
        int colSize = GeneticAlgorithm.colSize; //dapatkan jumlah kolom puzzle
        if(i-colSize > -1) { //cek apakah petak diatas gen dapat diakses
            int upIdx = i-colSize; //dapatkan idx atas
            int upMaxColIdx = getMaxColIdx(colSize, upIdx); //dapatkan batas kanan dari baris idx atas yang diakses
            int upMinColIdx = getMinColIdx(colSize, upIdx); //dapatkan batas kiri dari baris idx atas yang diakses
            if(topRight & upIdx+1 < upMaxColIdx){ //cek apakah parameter topRight bernilai true dan diagonal kanan atas dapat diakses
                if(puzzle[upIdx+1] == 'x' || puzzle[upIdx+1] == 'y'){ //jika kanan atas adalah petak yang dapat diisi (bukan tembok), ubah menjadi petak kosong
                    genes[upIdx+1] = 'a';
                }
            }
            if(topLeft & upIdx-1 > -1 && upIdx-1 > upMinColIdx) { //cek apakah parameter topLeft bernilai true dan diagonal kiri atas dapat diakses
                if (puzzle[upIdx - 1] == 'x' || puzzle[upIdx - 1] == 'y') { //jika kiri atas adalah petak yang dapat diisi (bukan tembok), ubah menjadi petak kosong
                    genes[upIdx-1] = 'a';
                }
            }
        }
        if(i+colSize < puzzle.length) { //cek apakah petak dibawah gen dapat diakses
            int bottomIdx = i+colSize; //dapatkan idx bawah
            int bottomMaxColIdx = getMaxColIdx(colSize, bottomIdx); //dapatkan batas kanan dari baris idx bawah yang diakses
            int bottomMinColIdx = getMinColIdx(colSize, bottomIdx); //dapatkan batas kiri dari baris idx bawah yang diakses
            if(bottomRight & bottomIdx+1 < puzzle.length && bottomIdx+1 < bottomMaxColIdx){ //cek apakah parameter bottomRight bernilai true dan diagonal kanan bawah dapat diakses
                if(puzzle[bottomIdx+1] == 'x' || puzzle[bottomIdx+1] == 'y'){ //jika kanan bawah adalah petak yang dapat diisi (bukan tembok), ubah menjadi petak kosong
                    genes[bottomIdx+1] = 'a';
                }
            }
            if(bottomLeft & bottomIdx-1 > bottomMinColIdx) { //cek apakah parameter bottomLeft bernilai true dan diagonal kiri bawah dapat diakses
                if (puzzle[bottomIdx - 1] == 'x' || puzzle[bottomIdx - 1] == 'y') { //jika kiri bawah adalah petak yang dapat diisi (bukan tembok), ubah menjadi petak kosong
                    genes[bottomIdx-1] = 'a';
                }
            }
        }
    }

    private void putLightAdjacent(int i, char[] puzzle) { //method untuk mengubah petak disekitar menjadi lampu
        int colSize = GeneticAlgorithm.colSize; //dapatkan jumlah kolom puzzle
        int currMaxColIdx = getMaxColIdx(colSize, i); //dapatkan batas kanan dari baris gen yang diakses
        int currMinColIdx = getMinColIdx(colSize, i); //dapatkan batas kiri dari baris gen yang diakses
        if(i+1 < puzzle.length && i+1 < currMaxColIdx){ //cek petak kanan dari gen dapat diakses
            if(puzzle[i+1] == 'x' || puzzle[i+1] == 'y'){ //jika petak kanan dapat diisi (bukan tembok), isi dengan lampu
                genes[i+1] = 'b';
                putEmptyVertHrz(i+1, puzzle); //ganti seluruh petak vertical dan horizontal (yang tidak dipisahkan dengan tembok) dari lampu menjadi petak kosong
            }
        }
        if(i-1 > -1 && i-1 > currMinColIdx) { //cek petak kiri dari gen dapat diakses
            if (puzzle[i - 1] == 'x' || puzzle[i - 1] == 'y') { //jika petak kiri dapat diisi (bukan tembok), isi dengan lampu
                genes[i-1] = 'b';
                putEmptyVertHrz(i-1, puzzle); //ganti seluruh petak vertical dan horizontal (yang tidak dipisahkan dengan tembok) dari lampu menjadi petak kosong
            }
        }
        if(i-colSize > -1) { //cek petak atas dari gen dapat diakses
            if (puzzle[i - colSize] == 'x' || puzzle[i - colSize] == 'y') { //jika petak atas dapat diisi (bukan tembok), isi dengan lampu
                genes[i-colSize] = 'b';
                putEmptyVertHrz(i-colSize, puzzle); //ganti seluruh petak vertical dan horizontal (yang tidak dipisahkan dengan tembok) dari lampu menjadi petak kosong
            }
        }
        if(i+colSize < puzzle.length) { //cek petak bawah dari gen dapat diakses
            if (puzzle[i + colSize] == 'x' || puzzle[i + colSize] == 'y') { //jika petak bawah dapat diisi (bukan tembok), isi dengan lampu
                genes[i+colSize] = 'b';
                putEmptyVertHrz(i+colSize, puzzle); //ganti seluruh petak vertical dan horizontal (yang tidak dipisahkan dengan tembok) dari lampu menjadi petak kosong
            }
        }
    }

    private void putEmptyVertHrz(int i, char[] puzzle) { //method untuk mengganti seluruh petak vertical dan horizontal (yang tidak dipisahkan dengan tembok) dari lampu menjadi petak kosong
        int colSize = GeneticAlgorithm.colSize; //dapatkan jumlah kolom puzzle
        int currMaxColIdx = getMaxColIdx(colSize, i); //dapatkan batas kanan dari baris gen yang diakses
        int currMinColIdx = getMinColIdx(colSize, i); //dapatkan batas kiri dari baris gen yang diakses
        int y = i+1; //dapatkan idx kanan dari gen
        while(y < genes.length && y < currMaxColIdx){ //selama petak y dapat diakses
            if(genes[y] == 'n' || genes[y] == '0' || genes[y] == '1' || genes[y] == '2' || genes[y] == '3' || genes[y] == '4'){ //jika petak y adalah tembok, hentikan loop
                break;
            }
            else if(genes[y] == 'x' || genes[y] == 'y'){ //jika petak y adalah petak yang dapat diisi, ubah menjadi petak kosong
                genes[y] = 'a';
            }
            y++;
        }

        y=i-1; //dapatkan idx kiri dari gen
        while(y > -1 && y > currMinColIdx){ //selama petak y dapat diakses
            if(genes[y] == 'n' || genes[y] == '0' || genes[y] == '1' || genes[y] == '2' || genes[y] == '3' || genes[y] == '4'){ //jika petak y adalah tembok, hentikan loop
                break;
            }
            else if(genes[y] == 'x' || genes[y] == 'y'){ //jika petak y adalah petak yang dapat diisi, ubah menjadi petak kosong
                genes[y] = 'a';
            }
            y--;
        }

        y=i-colSize; //dapatkan idx atas dari gen
        while(y > -1){ //selama petak y dapat diakses
            if(genes[y] == 'n' || genes[y] == '0' || genes[y] == '1' || genes[y] == '2' || genes[y] == '3' || genes[y] == '4'){ //jika petak y adalah tembok, hentikan loop
                break;
            }
            else if(genes[y] == 'y' || genes[y] == 'b'){ //jika petak y adalah petak yang dapat diisi, ubah menjadi petak kosong
                genes[y] = 'a';
            }
            y -= colSize;
        }

        y=i+colSize; //dapatkan idx bawah dari gen
        while(y < GeneticAlgorithm.arrSize){ //selama petak y dapat diakses
            if(genes[y] == 'n' || genes[y] == '0' || genes[y] == '1' || genes[y] == '2' || genes[y] == '3' || genes[y] == '4'){ //jika petak y adalah tembok, hentikan loop
                break;
            }
            else if(genes[y] == 'y' || genes[y] == 'b'){ //jika petak y adalah petak yang dapat diisi, ubah menjadi petak kosong
                genes[y] = 'a';
            }
            y += colSize;
        }
    }

    private void putEmptyAdjacent(int i, char[] puzzle) { //method untuk mengganti petak disekitar menjadi petak kosong
        int colSize = GeneticAlgorithm.colSize; //dapatkan jumlah kolom puzzle
        int currMaxColIdx = getMaxColIdx(colSize, i); //dapatkan batas kanan dari baris gen yang diakses
        int currMinColIdx = getMinColIdx(colSize, i); //dapatkan batas kiri dari baris gen yang diakses
        if(i+1 < puzzle.length && i+1 < currMaxColIdx){ //cek apakah petak kanan dapat diakses
            if(puzzle[i+1] == 'x' || puzzle[i+1] == 'y'){ //jika petak kanan adalah petak yang dapat diisi, ganti menjadi petak kosong
                genes[i+1] = 'a';
            }
        }
        if(i-1 > -1 && i-1 > currMinColIdx) { //cek apakah petak kiri dapat diakses
            if (puzzle[i - 1] == 'x' || puzzle[i - 1] == 'y') { //jika petak kiri adalah petak yang dapat diisi, ganti menjadi petak kosong
                genes[i-1] = 'a';
            }
        }
        if(i-colSize > -1) { //cek apakah petak atas dapat diakses
            if (puzzle[i - colSize] == 'x' || puzzle[i - colSize] == 'y') { //jika petak atas adalah petak yang dapat diisi, ganti menjadi petak kosong
                genes[i-colSize] = 'a';
            }
        }
        if(i+colSize < puzzle.length) { //cek apakah petak bawah dapat diakses
            if (puzzle[i + colSize] == 'x' || puzzle[i + colSize] == 'y') { //jika petak bawah adalah petak yang dapat diisi, ganti menjadi petak kosong
                genes[i+colSize] = 'a';
            }
        }
    }

    private boolean checkSecluded(int i, char[] puzzle) { //method untuk mengecek apakah sebuah petak dikelilingi tembok atau terpisah dari petak lain (pasti diisi lampu)
        int colSize = GeneticAlgorithm.colSize; //dapatkan jumlah kolom puzzle
        int currMaxColIdx = getMaxColIdx(colSize, i); //dapatkan batas kanan dari baris gen yang diakses
        int currMinColIdx = getMinColIdx(colSize, i); //dapatkan batas kiri dari baris gen yang diakses
        if(i+1 < puzzle.length && i+1 < currMaxColIdx){ //cek apakah petak kanan dapat diakses
            if(puzzle[i+1] == 'x' || puzzle[i+1] == 'y'){ //jika terdapat petak disebelah kanan, berarti petak tidak terpisah, kembalikan false
                return false;
            }
        }
        if(i-1 > -1 && i-1 > currMinColIdx) { //cek apakah petak kiri dapat diakses
            if (puzzle[i - 1] == 'x' || puzzle[i - 1] == 'y') { //jika terdapat petak disebelah kiri, berarti petak tidak terpisah, kembalikan false
                return false;
            }
        }
        if(i-colSize > -1) { //cek apakah petak atas dapat diakses
            if (puzzle[i - colSize] == 'x' || puzzle[i - colSize] == 'y') { //jika terdapat petak disebelah atas, berarti petak tidak terpisah, kembalikan false
                return false;
            }
        }
        if(i+colSize < puzzle.length) { //cek apakah petak bawah dapat diakses
            if (puzzle[i + colSize] == 'x' || puzzle[i + colSize] == 'y') { //jika terdapat petak disebelah bawah, berarti petak tidak terpisah, kembalikan false
                return false;
            }
        }
        return true; //jika tidak ada petak disekitar, kembalikan true
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
            int currMaxColIdx = getMaxColIdx(colSize, x); //dapatkan batas kanan dari baris gen yang diakses
            int currMinColIdx = getMinColIdx(colSize, x); //dapatkan batas kiri dari baris gen yang diakses
            if(genes[x] == 'y' || genes[x] == 'b'){ //jika idx arr ini berisi lampu, cek apakah bentrok dengan lampu lain
                int y = x;
                boolean collide = false; //variabel menandakan jika lampu bentrok
                while(true){ //cek petak-petak di sebelah kanan apakah ada lampu yang menerangi lampu ini (bentrok)
                    y++;
                    if(y < genes.length && y < currMaxColIdx){ //jika petak yang diakses belum melebihi batas kanan baris saat ini, lakukan pengecekan
                        if(genes[y] == 'n' || genes[y] == '0' || genes[y] == '1' || genes[y] == '2' || genes[y] == '3' || genes[y] == '4'){ //jika ditemukan tembok, fitness tidak dikurangi (tidak bentrok)
                            break;
                        }
                        else if(genes[y] == 'y' || genes[y] == 'b'){ //jika lampu ini bentrok dengan lampu lain, kurangi fitness dan ubah collide menjadi true
                            chromosomeFitness--;
                            collide = true;
                            break;
                        }
                    }
                    else{
                        break;
                    }
                }
                
                y=x;
                while(!collide){ //cek petak-petak di sebelah kiri apakah ada lampu yang menerangi lampu ini (bentrok)
                    y--;
                    if(y > -1 && y > currMinColIdx){ //jika petak yang diakses belum melebihi batas kiri baris saat ini, lakukan pengecekan
                        if(genes[y] == 'n' || genes[y] == '0' || genes[y] == '1' || genes[y] == '2' || genes[y] == '3' || genes[y] == '4'){ //jika ditemukan tembok, fitness tidak dikurangi (tidak bentrok)
                            break;
                        }
                        else if(genes[y] == 'y' || genes[y] == 'b'){ //jika lampu ini bentrok dengan lampu lain, kurangi fitness dan ubah collide menjadi true
                            chromosomeFitness--;
                            collide = true;
                            break;
                        }
                    }
                    else{
                        break;
                    }
                }
                
                y=x;
                while(!collide){ //cek petak-petak di atas apakah ada lampu yang menerangi lampu ini (bentrok)
                    y -= colSize;
                    if(y > -1){ //jika petak yang diakses belum melebihi batas atas puzzle, lakukan pengecekan
                        if(genes[y] == 'n' || genes[y] == '0' || genes[y] == '1' || genes[y] == '2' || genes[y] == '3' || genes[y] == '4'){ //jika ditemukan tembok, fitness tidak dikurangi (tidak bentrok)
                            break;
                        }
                        else if(genes[y] == 'y' || genes[y] == 'b'){ //jika lampu ini bentrok dengan lampu lain, kurangi fitness dan ubah collide menjadi true
                            chromosomeFitness--;
                            collide = true;
                            break;
                        }
                    }
                    else{
                        break;
                    }
                }
                
                y=x;
                while(!collide){ //cek petak-petak di bawah apakah ada lampu yang menerangi lampu ini (bentrok)
                    y += colSize;
                    if(y < GeneticAlgorithm.arrSize){ //jika petak yang diakses belum melebihi batas bawah puzzle, lakukan pengecekan
                        if(genes[y] == 'n' || genes[y] == '0' || genes[y] == '1' || genes[y] == '2' || genes[y] == '3' || genes[y] == '4'){ //jika ditemukan tembok, fitness tidak dikurangi (tidak bentrok)
                            break;
                        }
                        else if(genes[y] == 'y' || genes[y] == 'b'){ //jika lampu ini bentrok dengan lampu lain, kurangi fitness dan ubah collide menjadi true
                            chromosomeFitness--;
                            collide = true;
                            break;
                        }
                    }
                    else{
                        break;
                    }
                }
            }
            else if(genes[x] != 'x' && genes[x] != 'a'){ //jika idx arr ini berisi tembok, cek apakah jumlah lampu disekitarnya sesuai dengan angka tembok
                if(genes[x] != 'n'){ //jika tembok memiliki angka, hitung jumlah lampu disekitarnya, kurangi fitness jika kekurangan atau kelebihan
                    int angkaTembok = Character.getNumericValue(genes[x]);
                    int countBulb = 0;
                    if(x+1 < genes.length && x+1 < currMaxColIdx){ //cek kanan apakah ada lampu
                        if(genes[x+1] == 'y' || genes[x+1] == 'b'){
                            countBulb++; //tambahkan jumlah lampu jika ditemukan
                        }
                    }
                    if(x-1 > -1 && x-1 > currMinColIdx){ //cek kiri apakah ada lampu
                        if(genes[x-1] == 'y' || genes[x-1] == 'b'){
                            countBulb++; //tambahkan jumlah lampu jika ditemukan
                        }
                    }
                    if(x-colSize > -1){ //cek atas apakah ada lampu
                        if(genes[x-colSize] == 'y' || genes[x-colSize] == 'b'){
                            countBulb++; //tambahkan jumlah lampu jika ditemukan
                        }
                    }
                    if(x+colSize < genes.length){ //cek bawah apakah ada lampu
                        if(genes[x+colSize] == 'y' || genes[x+colSize] == 'b'){
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
                        else if(genes[y] == 'y' || genes[y] == 'b'){
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
                        else if(genes[y] == 'y' || genes[y] == 'b'){
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
                        else if(genes[y] == 'y' || genes[y] == 'b'){
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
                        else if(genes[y] == 'y' || genes[y] == 'b'){
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
    private int getMaxColIdx(int colSize, int x) { //method untuk mencari batas kanan dari baris suatu gen/petak
        int currMaxColIdx = colSize; //variabel batas kanan mula mula diisi dengan jumlah kolom
        while(currMaxColIdx <= x){ //jika gen yang diakses berada pada baris yang berbeda (dibawah) dari baris batas, geser batas ke baris selanjutnya
            currMaxColIdx += colSize;
        }
        return currMaxColIdx;
    }
    private int getMinColIdx(int colSize, int x) { //method untuk mencari batas kiri dari baris suatu gen/petak
        int currMinColIdx = colSize*(colSize-1) - 1; //variabel batas kiri mula mula diisi dengan batas kiri baris terakhir dari puzzle
        while(currMinColIdx >= x){ //jika gen yang diakses berada pada baris yang berbeda (diatas) dari baris batas, geser batas ke baris sebelumnya
            currMinColIdx -= colSize;
        }
        return currMinColIdx;
    }
}
