package testmario;

//sumber kode algoritma genetik dari https://www.youtube.com/watch?v=UcVJsV-tqlo
//penggunaan random dan format output dari program yang diberikan ko Lionov di teams

public class Chromosome {
    private boolean isFitnessChanged = true; //menandakan apakah fitness sudah dihitung (true = belum di hitung)
    private int fitness = 0; //isi fitness kromosom mula-mula dengan 0
    private char[] genes; //isi nilai setiap gen kromosom ('x' = petak kosong, 'y' = lampu, 'n' = tembok tanpa angka, 'a' = petak kosong yg tidak boleh dimutasi, 'y' = lampu yg tidak boleh dimutasi, 'n' = tembok tanpa angka, '0' tembok dengan angka 0, '1' tembok dengan angka 1, '2' tembok dengan angka 2, '3' tembok dengan angka 3, '4' tembok dengan angka 4)
    public Chromosome(int length){ //konstruktor kromosom untuk membuat array gen dengan besar array/puzzle parameter
        genes = new char[length]; //inisialisasi gen dengan array char kosong
    }
    public Chromosome initializeChromosome(){ //ubah petak kosong pada puzzle input dengan lampu secara random
        char[] puzzle = GeneticAlgorithm.puzzle; //simpan puzzle input ke suatu variabel
        if (GeneticAlgorithm.definedPuzzle == null) { //jika definedPuzzle (puzzle yg sudah diisi dengan lampu atau petak kosong pada posisi pasti) belum inisialisasi, akan dibuat baru
            genes = puzzle.clone(); //genes mula mula diisi dengan nilai puzzle
            int index = 0; //simpan idx untuk iterasi penempatan petak kosong dan lampu yang pasti
            while(index < 10) { //akan diiterasi 10 kali
                for (int i = 0; i < genes.length; i++) { //lakukan iterasi untuk seluruh jumlah gen
                    if (index > 0 && genes[i] == '4') { //jika idx i pada puzzle adalah tembok berangka 4, ubah petak disekitarnya menjadi lampu ('b') dan ubah petak diagonal menjadi petak kosong ('a')
                        putLightAdjacent(i, puzzle);
                        putEmptyDiagonally(i, puzzle, true, true, true, true);
                    } else if (index > 0 && genes[i] == '0') { //jika idx i pada puzzle adalah tembok berangka 0, ubah petak disekitarnya menjadi petak kosong ('a')
                        putEmptyAdjacent(i, puzzle);
                    } else if (genes[i] == '3') { //jika idx i pada puzzle adalah tembok berangka 3, ubah petak diagonal menjadi petak kosong ('a') dan jika jumlah petak disekitarnya sama dengan angka tembok, isi dengan lampu ('b')
                        putEmptyDiagonally(i, puzzle, true, true, true, true);
                        putLightIfGuaranteed(i, puzzle);
                        putEmptyIfGuaranteed(i, puzzle);
                    } else if (genes[i] == '2') { //jika idx i pada puzzle adalah tembok berangka 2, dan petak disekitarnya berjumlah < 4, ubah petak diagonal menjadi petak kosong ('a'), dan jika jumlah petak disekitarnya sama dengan angka tembok, isi dengan lampu ('b')
                        putEmptyDiagonallyIfRestricted(i, puzzle);
                        putLightIfGuaranteed(i, puzzle);
                        putEmptyIfGuaranteed(i, puzzle);
                    } else if (genes[i] == '1') { //jika idx i pada puzzle adalah tembok berangka 1, dan jumlah petak disekitarnya sama dengan angka tembok, isi dengan lampu ('b')
                        putLightIfGuaranteed(i, puzzle);
                        putEmptyIfGuaranteed(i, puzzle);
                    }
                    else if (genes[i] == 'x' || genes[i] == 'a') { //jika gen dgn idx saat ini adalah petak kosong, cek apakah hanya bisa diterangi oleh satu posisi petak, jika ya akan ditempatkan lampu diposisi tersebut
                        checkCanOnlyLitByOneGrid(i, puzzle);
                    }
                }
                index++; //tambahkan nilai idx iterasi
            }
            GeneticAlgorithm.definedPuzzle = genes.clone(); //setelah didapatkan puzzle yang memiliki petak kosong dan lampu pada posisi pasti, simpan nilai tersebut pada definedPuzzle
        }
        else{ //jika definedPuzzle sudah dibuat sebelumnya, ambil nilainya dan simpan ke genes
            genes = GeneticAlgorithm.definedPuzzle.clone();
        }
        for(int i=0;i<genes.length;i++){ //lakukan iterasi untuk seluruh jumlah gen
            if(genes[i] == '1' || genes[i] == '2' || genes[i] == '3'){ //jika gen dgn idx saat ini adalah tembok berangka yang kekurangan lampu, akan ditempatkan lampu yang kurang pada posisi disekitar tembok secara random
                randomizeLightAdjacent(i, genes);
            }
        }
        for(int i=0;i<genes.length;i++){ //lakukan iterasi untuk seluruh jumlah gen
            if(genes[i] == 'x'){ //jika gen dgn idx saat ini adalah petak kosong, ubah menjadi lampu secara random
                boolean lighted = checkIfLighted(i, puzzle); //cek apakah petak sudah diterangi
                boolean besideNumberedWall = checkIfBesideNumberedWall(i, puzzle); //cek apakah petak berada di samping tembok berangka
                if(!lighted && !besideNumberedWall){ //jika petak belum diterangi dan tidak berada disamping tembok berangka, ubah menjadi lampu secara random
                    if(Main.rand.nextFloat() >= 0.5) genes[i] = 'y'; //jika hasil random lebih besar sama dengan 0.5, ubah petak kosong dengan lampu
                    else genes[i] = 'x'; //jika hasil random lebih kecil dari 0.5, petak kosong tidak diubah
                }
            }
        }
        return this; //kembalikan hasil random kromosom
    }

    private boolean checkCanOnlyLitByOneGrid(int i, char[] puzzle) { //method untuk mengecek apakah suatu petak hanya bisa di terangi oleh satu posisi
        int colSize = GeneticAlgorithm.colSize; //dapatkan jumlah kolom puzzle
        int currMaxColIdx = getMaxColIdx(colSize, i); //dapatkan batas kanan dari baris gen yang diakses
        int currMinColIdx = getMinColIdx(colSize, i); //dapatkan batas kiri dari baris gen yang diakses
        int y = i+1; //dapatkan idx kanan dari gen
        int numOfGrid = 0; //jumlah petak yang dapat diisi oleh lampu disekitar
        int idxOnlyGrid = 0; //idx posisi petak yang dapat menerangi
        if(genes[i] == 'x'){ //jika gen idx i adalah petak kosong, maka lampu dapat ditempatkan pada tempatnya sendiri
            numOfGrid++;
            idxOnlyGrid = i;
        }
        while(y < genes.length && y < currMaxColIdx){ //selama petak y dapat diakses, akan dicari petak yang dapat menerangi petak i
            if(genes[y] == 'n' || genes[y] == '0' || genes[y] == '1' || genes[y] == '2' || genes[y] == '3' || genes[y] == '4'){ //jika petak y adalah tembok, hentikan loop
                break;
            }
            else if(genes[y] == 'x' || genes[y] == 'b' || genes[y] == 'y'){ //jika petak y adalah petak yang dapat diisi, tambahkan nilai numOfGrid
                if(numOfGrid >= 2){ //jika petak yang dapat menerangi gen i lebih dari 2, return false
                    return false;
                }
                else{
                    idxOnlyGrid = y; //simpan idx petak tersebut
                    numOfGrid++; //tambahkan nilai numOfGrid
                }
            }
            y++;
        }

        y=i-1; //dapatkan idx kiri dari gen
        while(y > -1 && y > currMinColIdx){ //selama petak y dapat diakses, akan dicari petak yang dapat menerangi petak i
            if(genes[y] == 'n' || genes[y] == '0' || genes[y] == '1' || genes[y] == '2' || genes[y] == '3' || genes[y] == '4'){ //jika petak y adalah tembok, hentikan loop
                break;
            }
            else if(genes[y] == 'x' || genes[y] == 'b' || genes[y] == 'y'){ //jika petak y adalah petak yang dapat diisi, tambahkan nilai numOfGrid
                if(numOfGrid >= 2){ //jika petak yang dapat menerangi gen i lebih dari 2, return false
                    return false;
                }
                else{
                    idxOnlyGrid = y; //simpan idx petak tersebut
                    numOfGrid++;  //tambahkan nilai numOfGrid
                }
            }
            y--;
        }

        y=i-colSize; //dapatkan idx atas dari gen
        while(y > -1){ //selama petak y dapat diakses, akan dicari petak yang dapat menerangi petak i
            if(genes[y] == 'n' || genes[y] == '0' || genes[y] == '1' || genes[y] == '2' || genes[y] == '3' || genes[y] == '4'){ //jika petak y adalah tembok, hentikan loop
                break;
            }
            else if(genes[y] == 'x' || genes[y] == 'b' || genes[y] == 'y'){ //jika petak y adalah petak yang dapat diisi, tambahkan nilai numOfGrid
                if(numOfGrid >= 2){ //jika petak yang dapat menerangi gen i lebih dari 2, return false
                    return false;
                }
                else{
                    idxOnlyGrid = y; //simpan idx petak tersebut
                    numOfGrid++;  //tambahkan nilai numOfGrid
                }
            }
            y -= colSize;
        }

        y=i+colSize; //dapatkan idx bawah dari gen
        while(y < GeneticAlgorithm.arrSize){ //selama petak y dapat diakses, akan dicari petak yang dapat menerangi petak i
            if(genes[y] == 'n' || genes[y] == '0' || genes[y] == '1' || genes[y] == '2' || genes[y] == '3' || genes[y] == '4'){ //jika petak y adalah tembok, hentikan loop
                break;
            }
            else if(genes[y] == 'x' || genes[y] == 'b' || genes[y] == 'y'){ //jika petak y adalah petak yang dapat diisi, tambahkan nilai numOfGrid
                if(numOfGrid >= 2){ //jika petak yang dapat menerangi gen i lebih dari 2, return false
                    return false;
                }
                else{
                    idxOnlyGrid = y; //simpan idx petak tersebut
                    numOfGrid++;  //tambahkan nilai numOfGrid
                }
            }
            y += colSize;
        }
        if(numOfGrid==1){ //jika jumlah numOfGrid hanya 1, tempatkan lampu pada posisi idxOnlyGrid
            genes[idxOnlyGrid] = 'b';
            putEmptyVertHrz(idxOnlyGrid, puzzle); //tempatkan 'a' (petak kosong yg tidak boleh dimutasi) untuk seluruh tetangga lampu secara vertikal dan horizontal
            return true;
        }
        return false;
    }

    private boolean checkIfBesideNumberedWall(int i, char[] puzzle) { //method untuk mengecek apakah suatu petak berada di sebelah tembok berangka 1/2/3
        int colSize = GeneticAlgorithm.colSize; //dapatkan jumlah kolom puzzle
        int currMaxColIdx = getMaxColIdx(colSize, i); //dapatkan batas kanan dari baris gen yang diakses
        int currMinColIdx = getMinColIdx(colSize, i); //dapatkan batas kiri dari baris gen yang diakses

        if(i+1 < puzzle.length && i+1 < currMaxColIdx){ //cek kanan apakah ada petak
            if(genes[i+1] == '1' || genes[i+1] == '2' || genes[i+1] == '3'){ //jika berisi tembok berangka 1/2/3 return true
                return true;
            }
        }
        if(i-1 > -1 && i-1 > currMinColIdx){ //cek kiri apakah ada petak
            if(genes[i-1] == '1' || genes[i-1] == '2' || genes[i-1] == '3'){ //jika berisi tembok berangka 1/2/3 return true
                return true;
            }
        }
        if(i-colSize > -1){ //cek atas apakah ada petak
            if(genes[i-colSize] == '1' || genes[i-colSize] == '2' || genes[i-colSize] == '3'){ //jika berisi tembok berangka 1/2/3 return true
                return true;
            }
        }
        if(i+colSize < puzzle.length){ //cek bawah apakah ada petak
            if(genes[i+colSize] == '1' || genes[i+colSize] == '2' || genes[i+colSize] == '3'){ //jika berisi tembok berangka 1/2/3 return true
                return true;
            }
        }
        return false;
    }

    private void randomizeLightAdjacent(int i, char[] genes) { //method untuk menempatkan lampu disekitar tembok berangka jika kekurangan
        int colSize = GeneticAlgorithm.colSize; //dapatkan jumlah kolom puzzle
        int currMaxColIdx = getMaxColIdx(colSize, i); //dapatkan batas kanan dari baris gen yang diakses
        int currMinColIdx = getMinColIdx(colSize, i); //dapatkan batas kiri dari baris gen yang diakses

        boolean leftAvailable = false; //penanda petak kiri bisa ditempatkan lampu
        boolean rightAvailable = false; //penanda petak kanan bisa ditempatkan lampu
        boolean upAvailable = false; //penanda petak atas bisa ditempatkan lampu
        boolean downAvailable = false; //penanda petak bawah bisa ditempatkan lampu

        int numOfBulb = 0; //variabel menghitung jumlah lampu
        if(i+1 < genes.length && i+1 < currMaxColIdx){ //cek kanan apakah ada petak
            if(genes[i+1] == 'b' || genes[i+1] == 'y'){ //jika berisi lampu, tambahkan numOfBulb
                numOfBulb++;
            }
            else if(genes[i+1] == 'x'){ //jika berisi petak kosong, ubah rightAvailable menjadi true
                rightAvailable = true;
            }
        }
        if(i-1 > -1 && i-1 > currMinColIdx){ //cek kiri apakah ada petak
            if(genes[i-1] == 'b' || genes[i-1] == 'y'){ //jika berisi lampu, tambahkan numOfBulb
                numOfBulb++;
            }
            else if(genes[i-1] == 'x'){ //jika berisi petak kosong, ubah leftAvailable menjadi true
                leftAvailable = true;
            }
        }
        if(i-colSize > -1){ //cek atas apakah ada petak
            if(genes[i-colSize] == 'b' || genes[i-colSize] == 'y'){ //jika berisi lampu, tambahkan numOfBulb
                numOfBulb++;
            }
            else if(genes[i-colSize] == 'x'){ //jika berisi petak kosong, ubah upAvailable menjadi true
                upAvailable = true;
            }
        }
        if(i+colSize < genes.length){ //cek bawah apakah ada petak
            if(genes[i+colSize] == 'b' || genes[i+colSize] == 'y'){ //jika berisi lampu, tambahkan numOfBulb
                numOfBulb++;
            }
            else if(genes[i+colSize] == 'x'){ //jika berisi petak kosong, ubah downAvailable menjadi true
                downAvailable = true;
            }
        }
        int angkaTembok = Character.getNumericValue(genes[i]); //dapatkan angka tembok
        if(angkaTembok > numOfBulb){ //jika angka tembok lebih besar dari jumlah lampu disekitarnya, ubah tetangga yang dapat diisi lampu menjadi lampu secara random
            int bulbLeft = angkaTembok - numOfBulb; //sisa lampu yang harus di tempatkan

            while(bulbLeft > 0){ //loop selama lampu masih kurang
                if(Main.rand.nextFloat() <= 0.25){ //jika random lebih kecil dari 0.25, tempatkan lampu pada posisi yang tersedia dengan urutan kiri atas kanan bawah
                    if(leftAvailable){
                        genes[i-1] = 'y';
                        leftAvailable = false;
                        bulbLeft--;
                    }
                    else if(upAvailable){
                        genes[i-colSize] = 'y';
                        upAvailable = false;
                        bulbLeft--;
                    }
                    else if(rightAvailable){
                        genes[i+1] = 'y';
                        rightAvailable = false;
                        bulbLeft--;
                    }
                    else if(downAvailable){
                        genes[i+colSize] = 'y';
                        downAvailable = false;
                        bulbLeft--;
                    }
                }
                else if(Main.rand.nextFloat() <= 0.5){ //jika random lebih kecil dari 0.5, tempatkan lampu pada posisi yang tersedia dengan urutan kanan bawah kiri atas
                    if(rightAvailable){
                        genes[i+1] = 'y';
                        rightAvailable = false;
                        bulbLeft--;
                    }
                    else if(downAvailable){
                        genes[i+colSize] = 'y';
                        downAvailable = false;
                        bulbLeft--;
                    }
                    else if(leftAvailable){
                        genes[i-1] = 'y';
                        leftAvailable = false;
                        bulbLeft--;
                    }
                    else if(upAvailable){
                        genes[i-colSize] = 'y';
                        upAvailable = false;
                        bulbLeft--;
                    }
                }
                else if(Main.rand.nextFloat() <= 0.75){ //jika random lebih kecil dari 0.75, tempatkan lampu pada posisi yang tersedia dengan urutan atas bawah kanan kiri
                    if(upAvailable){
                        genes[i-colSize] = 'y';
                        upAvailable = false;
                        bulbLeft--;
                    }
                    else if(downAvailable){
                        genes[i+colSize] = 'y';
                        downAvailable = false;
                        bulbLeft--;
                    }
                    else if(rightAvailable){
                        genes[i+1] = 'y';
                        rightAvailable = false;
                        bulbLeft--;
                    }
                    else if(leftAvailable){
                        genes[i-1] = 'y';
                        leftAvailable = false;
                        bulbLeft--;
                    }
                }
                else{ //jika random lebih besar dari 0.75, tempatkan lampu pada posisi yang tersedia dengan urutan bawah kiri atas kanan
                    if(downAvailable){
                        genes[i+colSize] = 'y';
                        downAvailable = false;
                        bulbLeft--;
                    }
                    else if(leftAvailable){
                        genes[i-1] = 'y';
                        leftAvailable = false;
                        bulbLeft--;
                    }
                    else if(upAvailable){
                        genes[i-colSize] = 'y';
                        upAvailable = false;
                        bulbLeft--;
                    }
                    else if(rightAvailable){
                        genes[i+1] = 'y';
                        rightAvailable = false;
                        bulbLeft--;
                    }
                }
            }
        }
    }

    private void putEmptyIfGuaranteed(int i, char[] puzzle) { //method untuk menempatkan petak kosong (a) disekitar tembok jika jumlah lampu sudah memenuhi angka
        int colSize = GeneticAlgorithm.colSize; //dapatkan jumlah kolom puzzle
        int currMaxColIdx = getMaxColIdx(colSize, i); //dapatkan batas kanan dari baris gen yang diakses
        int currMinColIdx = getMinColIdx(colSize, i); //dapatkan batas kiri dari baris gen yang diakses

        int numOfBulb = 0; //variabel menghitung jumlah lampu
        if(i+1 < puzzle.length && i+1 < currMaxColIdx){ //cek kanan apakah ada lampu
            if(genes[i+1] == 'b'){ //jika berisi lampu, tambahkan numOfBulb
                numOfBulb++;
            }
        }
        if(i-1 > -1 && i-1 > currMinColIdx){ //cek kiri apakah ada lampu
            if(genes[i-1] == 'b'){  //jika berisi lampu, tambahkan numOfBulb
                numOfBulb++;
            }
        }
        if(i-colSize > -1){ //cek atas apakah ada lampu
            if(genes[i-colSize] == 'b'){  //jika berisi lampu, tambahkan numOfBulb
                numOfBulb++;
            }
        }
        if(i+colSize < puzzle.length){ //cek bawah apakah ada lampu
            if(genes[i+colSize] == 'b'){  //jika berisi lampu, tambahkan numOfBulb
                numOfBulb++;
            }
        }
        int angkaTembok = Character.getNumericValue(genes[i]); //dapatkan angka tembok
        if(angkaTembok == numOfBulb){ //jika angka tembok sama dengan jumlah lampu disekitarnya, ubah petak disekitar yang tidak diisi lampu dengan petak kosong (a)
            putEmptyAdjacent(i, puzzle);
        }
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
            if(genes[i+1] == 'x' || genes[i+1] == 'y' || genes[i+1] == 'b'){ //jika berisi petak atau lampu (dan bukan petak kosong yang tidak bisa dimutasi)
                numOfGrid++; //tambahkan jumlah petak
                rightBlock = false; //kanan tidak terblokir
            }
        }
        if(i-1 > -1 && i-1 > currMinColIdx){ //cek kiri apakah ada petak yang dapat diisi
            if(genes[i-1] == 'x' || genes[i-1] == 'y' || genes[i-1] == 'b'){ //jika berisi petak atau lampu (dan bukan petak kosong yang tidak bisa dimutasi)
                numOfGrid++; //tambahkan jumlah petak
                leftBlock = false; //kiri tidak terblokir
            }
        }
        if(i-colSize > -1){ //cek atas apakah ada petak yang dapat diisi
            if(genes[i-colSize] == 'x' || genes[i-colSize] == 'y' || genes[i-colSize] == 'b'){ //jika berisi petak atau lampu (dan bukan petak kosong yang tidak bisa dimutasi)
                numOfGrid++; //tambahkan jumlah petak
                topBlock = false; //atas tidak terblokir
            }
        }
        if(i+colSize < puzzle.length){ //cek bawah apakah ada petak yang dapat diisi
            if(genes[i+colSize] == 'x' || genes[i+colSize] == 'y' || genes[i+colSize] == 'b'){ //jika berisi petak atau lampu (dan bukan petak kosong yang tidak bisa dimutasi)
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
            if(genes[i+1] == 'x' || genes[i+1] == 'y' || genes[i+1] == 'b'){ //jika berisi petak atau lampu (dan bukan petak kosong yang tidak bisa dimutasi)
                numOfGrid++; //tambahkan jumlah petak
            }
        }
        if(i-1 > -1 && i-1 > currMinColIdx){ //cek kiri apakah ada petak
            if(genes[i-1] == 'x' || genes[i-1] == 'y' || genes[i-1] == 'b'){ //jika berisi petak atau lampu (dan bukan petak kosong yang tidak bisa dimutasi)
                numOfGrid++; //tambahkan jumlah petak
            }
        }
        if(i-colSize > -1){ //cek atas apakah ada petak
            if(genes[i-colSize] == 'x' || genes[i-colSize] == 'y' || genes[i-colSize] == 'b'){ //jika berisi petak atau lampu (dan bukan petak kosong yang tidak bisa dimutasi)
                numOfGrid++; //tambahkan jumlah petak
            }
        }
        if(i+colSize < puzzle.length){ //cek bawah apakah ada petak
            if(genes[i+colSize] == 'x' || genes[i+colSize] == 'y' || genes[i+colSize] == 'b'){ //jika berisi petak atau lampu (dan bukan petak kosong yang tidak bisa dimutasi)
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
            if(genes[i+1] == 'x' || genes[i+1] == 'y'){ //jika petak kanan dapat diisi (bukan tembok), isi dengan lampu
                genes[i+1] = 'b';
                putEmptyVertHrz(i+1, puzzle); //ganti seluruh petak vertical dan horizontal (yang tidak dipisahkan dengan tembok) dari lampu menjadi petak kosong
            }
        }
        if(i-1 > -1 && i-1 > currMinColIdx) { //cek petak kiri dari gen dapat diakses
            if (genes[i - 1] == 'x' || genes[i - 1] == 'y') { //jika petak kiri dapat diisi (bukan tembok), isi dengan lampu
                genes[i-1] = 'b';
                putEmptyVertHrz(i-1, puzzle); //ganti seluruh petak vertical dan horizontal (yang tidak dipisahkan dengan tembok) dari lampu menjadi petak kosong
            }
        }
        if(i-colSize > -1) { //cek petak atas dari gen dapat diakses
            if (genes[i - colSize] == 'x' || genes[i - colSize] == 'y') { //jika petak atas dapat diisi (bukan tembok), isi dengan lampu
                genes[i-colSize] = 'b';
                putEmptyVertHrz(i-colSize, puzzle); //ganti seluruh petak vertical dan horizontal (yang tidak dipisahkan dengan tembok) dari lampu menjadi petak kosong
            }
        }
        if(i+colSize < puzzle.length) { //cek petak bawah dari gen dapat diakses
            if (genes[i + colSize] == 'x' || genes[i + colSize] == 'y') { //jika petak bawah dapat diisi (bukan tembok), isi dengan lampu
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
            else if(genes[y] == 'x' || genes[y] == 'y'){ //jika petak y adalah petak yang dapat diisi, ubah menjadi petak kosong
                genes[y] = 'a';
            }
            y -= colSize;
        }

        y=i+colSize; //dapatkan idx bawah dari gen
        while(y < GeneticAlgorithm.arrSize){ //selama petak y dapat diakses
            if(genes[y] == 'n' || genes[y] == '0' || genes[y] == '1' || genes[y] == '2' || genes[y] == '3' || genes[y] == '4'){ //jika petak y adalah tembok, hentikan loop
                break;
            }
            else if(genes[y] == 'x' || genes[y] == 'y'){ //jika petak y adalah petak yang dapat diisi, ubah menjadi petak kosong
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
            if(genes[i+1] != 'a' && genes[i+1] != 'b' && puzzle[i+1] == 'x' || puzzle[i+1] == 'y'){ //jika petak kanan adalah petak yang dapat diisi, ganti menjadi petak kosong
                genes[i+1] = 'a';
            }
        }
        if(i-1 > -1 && i-1 > currMinColIdx) { //cek apakah petak kiri dapat diakses
            if (genes[i-1] != 'a' && genes[i-1] != 'b' && puzzle[i - 1] == 'x' || puzzle[i - 1] == 'y') { //jika petak kiri adalah petak yang dapat diisi, ganti menjadi petak kosong
                genes[i-1] = 'a';
            }
        }
        if(i-colSize > -1) { //cek apakah petak atas dapat diakses
            if (genes[i - colSize] != 'a' && genes[i - colSize] != 'b' && puzzle[i - colSize] == 'x' || puzzle[i - colSize] == 'y') { //jika petak atas adalah petak yang dapat diisi, ganti menjadi petak kosong
                genes[i-colSize] = 'a';
            }
        }
        if(i+colSize < puzzle.length) { //cek apakah petak bawah dapat diakses
            if (genes[i+colSize] != 'a' && genes[i+colSize] != 'b' && puzzle[i + colSize] == 'x' || puzzle[i + colSize] == 'y') { //jika petak bawah adalah petak yang dapat diisi, ganti menjadi petak kosong
                genes[i+colSize] = 'a';
            }
        }
    }

    private boolean checkIfLighted(int i, char[] puzzle){ //method untuk mengecek apakh petak kosong sudah diterangi
        int colSize = GeneticAlgorithm.colSize; //dapatkan jumlah kolom puzzle
        int currMaxColIdx = getMaxColIdx(colSize, i); //dapatkan batas kanan dari baris gen yang diakses
        int currMinColIdx = getMinColIdx(colSize, i); //dapatkan batas kiri dari baris gen yang diakses
        int y = i; //idx untuk iterasi ke kanan
        while(true){ //cek petak-petak di sebelah kanan apakah ada lampu yang menerangi petak ini
            y++;
            if(y < genes.length && y < currMaxColIdx){ //jika petak y dapat diakses
                if(genes[y] == 'n' || genes[y] == '0' || genes[y] == '1' || genes[y] == '2' || genes[y] == '3' || genes[y] == '4'){ //jika ditemukan tembok, hentikan loop
                    break;
                }
                else if(genes[y] == 'y' || genes[y] == 'b'){ //jika ditemukan lampu, return true
                    return true;
                }
            }
            else{
                break;
            }
        }

        y=i; //idx untuk iterasi ke kiri
        while(true){ //cek petak-petak di sebelah kiri apakah ada lampu yang menerangi petak ini
            y--;
            if(y > -1 && y > currMinColIdx){ //jika petak y dapat diakses
                if(genes[y] == 'n' || genes[y] == '0' || genes[y] == '1' || genes[y] == '2' || genes[y] == '3' || genes[y] == '4'){ //jika ditemukan tembok, hentikan loop
                    break;
                }
                else if(genes[y] == 'y' || genes[y] == 'b'){  //jika ditemukan lampu, return true
                    return true;
                }
            }
            else{
                break;
            }
        }

        y=i; //idx untuk iterasi ke atas
        while(true){ //cek petak-petak di atas apakah ada lampu yang menerangi petak ini
            y -= colSize;
            if(y > -1){ //jika petak y dapat diakses
                if(genes[y] == 'n' || genes[y] == '0' || genes[y] == '1' || genes[y] == '2' || genes[y] == '3' || genes[y] == '4'){ //jika ditemukan tembok, hentikan loop
                    break;
                }
                else if(genes[y] == 'y' || genes[y] == 'b'){  //jika ditemukan lampu, return true
                    return true;
                }
            }
            else{
                break;
            }
        }

        y=i; //idx untuk iterasi ke bawah
        while(true){ //cek petak-petak di bawah apakah ada lampu yang menerangi petak ini
            y += colSize;
            if(y < GeneticAlgorithm.arrSize){ //jika petak y dapat diakses
                if(genes[y] == 'n' || genes[y] == '0' || genes[y] == '1' || genes[y] == '2' || genes[y] == '3' || genes[y] == '4'){ //jika ditemukan tembok, hentikan loop
                    break;
                }
                else if(genes[y] == 'y' || genes[y] == 'b'){  //jika ditemukan lampu, return true
                    return true;
                }
            }
            else{
                break;
            }
        }
        return false;
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
            else if(genes[x] != 'x' && genes[x] != 'a'){ //jika idx arr ini berisi tembok, cek apakah jumlah lampu disekitarnya sesuai dengan angka tembok dan apakah terdapat petak yang dapat menerangi kurangnya lampu
                if(genes[x] != 'n'){ //jika tembok memiliki angka, hitung jumlah lampu disekitarnya, kurangi fitness jika kekurangan atau kelebihan
                    int angkaTembok = Character.getNumericValue(genes[x]); //simpan angka tembok
                    int countBulb = 0; //variabel penghitung lampu
                    boolean availableGridFound = false; //variabel penanda ada petak yg dapat diisi
                    if(x+1 < genes.length && x+1 < currMaxColIdx){ //cek kanan apakah ada lampu
                        if(genes[x+1] == 'y' || genes[x+1] == 'b'){
                            countBulb++; //tambahkan jumlah lampu jika ditemukan
                            availableGridFound = true; //terdapat petak yg dapat diisi
                        }
                        else if(genes[x+1] == 'x'){
                            availableGridFound = true; //terdapat petak yg dapat diisi
                        }
                    }
                    if(x-1 > -1 && x-1 > currMinColIdx){ //cek kiri apakah ada lampu
                        if(genes[x-1] == 'y' || genes[x-1] == 'b'){
                            countBulb++; //tambahkan jumlah lampu jika ditemukan
                            availableGridFound = true; //terdapat petak yg dapat diisi
                        }
                        else if(genes[x-1] == 'x'){
                            availableGridFound = true; //terdapat petak yg dapat diisi
                        }
                    }
                    if(x-colSize > -1){ //cek atas apakah ada lampu
                        if(genes[x-colSize] == 'y' || genes[x-colSize] == 'b'){
                            countBulb++; //tambahkan jumlah lampu jika ditemukan
                            availableGridFound = true; //terdapat petak yg dapat diisi
                        }
                        else if(genes[x-colSize] == 'x'){
                            availableGridFound = true; //terdapat petak yg dapat diisi
                        }
                    }
                    if(x+colSize < genes.length){ //cek bawah apakah ada lampu
                        if(genes[x+colSize] == 'y' || genes[x+colSize] == 'b'){
                            countBulb++; //tambahkan jumlah lampu jika ditemukan
                            availableGridFound = true; //terdapat petak yg dapat diisi
                        }
                        else if(genes[x+colSize] == 'x'){
                            availableGridFound = true; //terdapat petak yg dapat diisi
                        }
                    }
                    
                    if(countBulb < angkaTembok){ //jika lampu disekitar kurang dari angka tembok, kurangi fitness
                        chromosomeFitness -= (angkaTembok-countBulb); //fitness -1 untuk setiap lampu yang kurang
                        if(!availableGridFound){ //jika tidak ditemukan petak yang dapat diisi lampu, kurangi fitness
                            chromosomeFitness--;
                        }
                    }
                    else if(countBulb > angkaTembok){ //jika lampu disekitar lebih dari angka tembok, kurangi fitness
                        chromosomeFitness -= (countBulb-angkaTembok); //fitness -1 untuk setiap lampu yang lebih
                    }
                }
            }
            else{ //jika idx arr ini berisi petak kosong, cek apakah ada lampu yang menerangi (memastikan seluruh petak sudah diterangi) dan apakah ada petak yang dapat menerangi jika belum diterangi
                int y = x;
                boolean bulbFound = false; //variabel penanda lampu ditemukan
                boolean availableGridFound = false; //variabel penanda petak yang dapat diisi ditemukan
                if(genes[y] == 'x'){ //jika gen y adalah petak kosong, cek apakah dapat diisi lampu
                    if(checkIfAvailable(y, genes)){
                        availableGridFound = true; //jika dapat diisi lampu, set availableGridFound menjadi true
                    }
                }
                while(!bulbFound){ //cek petak-petak di sebelah kanan apakah ada lampu yang menerangi petak ini
                    y++;
                    if(y < genes.length && y < currMaxColIdx){
                        if(genes[y] == 'n' || genes[y] == '0' || genes[y] == '1' || genes[y] == '2' || genes[y] == '3' || genes[y] == '4'){ //jika ditemukan tembok, nilai bulbFound tidak diubah (lampu belum ditemukan)
                            break;
                        }
                        else if(genes[y] == 'y' || genes[y] == 'b'){
                            bulbFound = true; //jika ditemukan lampu, ubah bulbFound menjadi true dan hentikan looping (petak sudah diterangi)
                            availableGridFound = true; //terdapat petak yg dapat diisi
                            break;
                        }
                        else if(genes[y] == 'x'){
                            availableGridFound = true; //terdapat petak yg dapat diisi
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
                            availableGridFound = true; //terdapat petak yg dapat diisi
                            break;
                        }
                        else if(genes[y] == 'x'){
                            availableGridFound = true; //terdapat petak yg dapat diisi
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
                            availableGridFound = true; //terdapat petak yg dapat diisi
                            break;
                        }
                        else if(genes[y] == 'x'){
                            availableGridFound = true; //terdapat petak yg dapat diisi
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
                            availableGridFound = true; //terdapat petak yg dapat diisi
                            break;
                        }
                        else if(genes[y] == 'x'){
                            availableGridFound = true; //terdapat petak yg dapat diisi
                        }
                    }
                    else{
                        break;
                    }
                }
                if(!bulbFound){ //jika pada sisi atas, bawah, kiri, dan kanan tidak ditemukan lampu, kurangi fitness
                    chromosomeFitness--;
                }
                if(!availableGridFound){ //jika tidak ditemukan petak yang dapat diisi dengan lampu dan menerangi petak/gen x, kurangi fitness
                    chromosomeFitness--;
                }
            }
        }
        return chromosomeFitness; //kembalikan hasil fitness untuk kromosom ini
    }

    private boolean checkIfAvailable(int y, char[] genes) { //method untuk mengecek apakah petak kosong dapat diisi lampu (tidak disamping tembok yang jumlah lampunya sudah sama dengan angka tembok)
        int colSize = GeneticAlgorithm.colSize; //dapatkan jumlah kolom puzzle
        int currMaxColIdx = getMaxColIdx(colSize, y); //dapatkan batas kanan dari baris gen yang diakses
        int currMinColIdx = getMinColIdx(colSize, y); //dapatkan batas kiri dari baris gen yang diakses

        if(y+1 < genes.length && y+1 < currMaxColIdx){ //cek kanan apakah ada tembok
            if(genes[y+1] == '1' || genes[y+1] == '2' || genes[y+1] == '3'){ //jika ditemukan tembok 1/2/3
               if(!checkIfLackBulb(y+1, genes)){ //cek apakah tembok tersebut kekurangan lampu
                   return false; //jika tidak kekurangan, return false
               }
            }
        }
        if(y-1 > -1 && y-1 > currMinColIdx){ //cek kiri apakah ada lampu
            if(genes[y-1] == '1' || genes[y-1] == '2' || genes[y-1] == '3'){ //jika ditemukan tembok 1/2/3
                if(!checkIfLackBulb(y-1, genes)){ //cek apakah tembok tersebut kekurangan lampu
                    return false; //jika tidak kekurangan, return false
                }
            }
        }
        if(y-colSize > -1){ //cek atas apakah ada lampu
            if(genes[y-colSize] == '1' || genes[y-colSize] == '2' || genes[y-colSize] == '3'){ //jika ditemukan tembok 1/2/3
                if(!checkIfLackBulb(y-colSize, genes)){ //cek apakah tembok tersebut kekurangan lampu
                    return false; //jika tidak kekurangan, return false
                }
            }
        }
        if(y+colSize < genes.length){ //cek bawah apakah ada lampu
            if(genes[y+colSize] == '1' || genes[y+colSize] == '2' || genes[y+colSize] == '3'){ //jika ditemukan tembok 1/2/3
                if(!checkIfLackBulb(y+colSize, genes)){ //cek apakah tembok tersebut kekurangan lampu
                    return false; //jika tidak kekurangan, return false
                }
            }
        }
        return true; //return true jika dapat diisi lampu
    }

    private boolean checkIfLackBulb(int i, char[] genes) { //method untuk mengecek apakah tembok kekurangan lampu
        int colSize = GeneticAlgorithm.colSize; //dapatkan jumlah kolom puzzle
        int currMaxColIdx = getMaxColIdx(colSize, i); //dapatkan batas kanan dari baris gen yang diakses
        int currMinColIdx = getMinColIdx(colSize, i); //dapatkan batas kiri dari baris gen yang diakses

        int numOfBulb = 0; //variabel menghitung jumlah lampu
        if(i+1 < genes.length && i+1 < currMaxColIdx){ //cek kanan apakah ada petak
            if(genes[i+1] == 'b' || genes[i+1] == 'y'){ //jika berisi lampu
                numOfBulb++; //tambahkan numOfBulb
            }
        }
        if(i-1 > -1 && i-1 > currMinColIdx){ //cek kiri apakah ada petak
            if(genes[i-1] == 'b' || genes[i-1] == 'y'){ //jika berisi lampu
                numOfBulb++; //tambahkan numOfBulb
            }
        }
        if(i-colSize > -1){ //cek atas apakah ada petak
            if(genes[i-colSize] == 'b' || genes[i-colSize] == 'y'){ //jika berisi lampu
                numOfBulb++; //tambahkan numOfBulb
            }
        }
        if(i+colSize < genes.length){ //cek bawah apakah ada petak
            if(genes[i+colSize] == 'b' || genes[i+colSize] == 'y'){ //jika berisi lampu
                numOfBulb++; //tambahkan numOfBulb
            }
        }
        int angkaTembok = Character.getNumericValue(genes[i]); //dapatkan angka tembok
        if(angkaTembok > numOfBulb){ //jika angka tembok lebih besar dari jumlah lampu disekitarnya, return true
            return true;
        }
        return false; //return false jika tidak kekurangan lampu
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
