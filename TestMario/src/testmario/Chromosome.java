package testmario;

public class Chromosome {
    private boolean isFitnessChanged = true;
    private int fitness = 0;
    private char[] genes;
    public Chromosome(int length){
        genes = new char[length];
    }
    public Chromosome initializeChromosome(){
        char[] puzzle = GeneticAlgorithm.puzzle;
        for(int i=0;i<genes.length;i++){
            if(puzzle[i] == 'x'){
                if(Math.random() >= 0.5) genes[i] = 'y';
                else genes[i] = 'x';
            }
            else{
                genes[i] = puzzle[i];
            }
        }
        return this;
    }
    public char[] getGenes(){
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
        int chromosomeFitness = 100;
        for(int x=0;x<genes.length;x++){
            int rowSize = GeneticAlgorithm.rowSize;
            int currMaxRowIdx = rowSize;
            while(currMaxRowIdx <= x){
                currMaxRowIdx += currMaxRowIdx;
            }
            int currMinRowIdx = rowSize*(rowSize-1) - 1;
            while(currMinRowIdx >= x){
                currMinRowIdx -= rowSize;
            }
            if(genes[x] == 'y'){ //jika idx arr ini berisi lampu, cek apakah bentrok dengan lampu lain
                int y = x;
                while(true){ //cek petak-petak di sebelah kanan apakah ada lampu yang menerangi lampu ini (bentrok)
                    y++;
                    if(y < genes.length && y < currMaxRowIdx){
                        if(genes[y] == 'n' || genes[y] == '0' || genes[y] == '1' || genes[y] == '2' || genes[y] == '3' || genes[y] == '4'){
                            break;
                        }
                        else if(genes[y] == 'y'){
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
                    if(y > -1 && y > currMinRowIdx){
                        if(genes[y] == 'n' || genes[y] == '0' || genes[y] == '1' || genes[y] == '2' || genes[y] == '3' || genes[y] == '4'){
                            break;
                        }
                        else if(genes[y] == 'y'){
                            chromosomeFitness--;
                        }
                    }
                    else{
                        break;
                    }
                }
                
                y=x;
                while(true){ //cek petak-petak di atas apakah ada lampu yang menerangi lampu ini (bentrok)
                    y -= rowSize;
                    if(y > -1){
                        if(genes[y] == 'n' || genes[y] == '0' || genes[y] == '1' || genes[y] == '2' || genes[y] == '3' || genes[y] == '4'){
                            break;
                        }
                        else if(genes[y] == 'y'){
                            chromosomeFitness--;
                        }
                    }
                    else{
                        break;
                    }
                }
                
                y=x;
                while(true){ //cek petak-petak di bawah apakah ada lampu yang menerangi lampu ini (bentrok)
                    y += rowSize;
                    if(y < GeneticAlgorithm.arrSize){
                        if(genes[y] == 'n' || genes[y] == '0' || genes[y] == '1' || genes[y] == '2' || genes[y] == '3' || genes[y] == '4'){
                            break;
                        }
                        else if(genes[y] == 'y'){
                            chromosomeFitness--;
                        }
                    }
                    else{
                        break;
                    }
                }
            }
            else if(genes[x] != 'x'){ //jika idx arr ini berisi tembok, cek apakah jumlah lampu disekitarnya sesuai dengan angka tembok
                if(genes[x] != 'n'){
                    int angkaTembok = Character.getNumericValue(genes[x]);
                    int countBulb = 0;
                    int y = x;
                    if(x+1 < genes.length && x+1 < currMaxRowIdx){ //cek kanan apakah ada lampu
                        if(genes[x+1] == 'y'){
                            countBulb++;
                        }
                    }
                    if(x-1 > -1 && x-1 > currMinRowIdx){ //cek kiri apakah ada lampu
                        if(genes[x-1] == 'y'){
                            countBulb++;
                        }
                    }
                    if(x-rowSize > -1){ //cek atas apakah ada lampu
                        if(genes[x-rowSize] == 'y'){
                            countBulb++;
                        }
                    }
                    if(x+rowSize < genes.length){ //cek bawah apakah ada lampu
                        if(genes[x+rowSize] == 'y'){
                            countBulb++;
                        }
                    }
                    
                    if(countBulb < angkaTembok){
                        chromosomeFitness -= (angkaTembok-countBulb)*2; //fitness -2 untuk setiap lampu yang kurang
                    }
                    else if(countBulb > angkaTembok){
                        chromosomeFitness -= (countBulb-angkaTembok)*2; //fitness -2 untuk setiap lampu yang lebih
                    }
                }
            }
            else{ //jika idx arr ini berisi petak kosong, cek apakah ada lampu yang menerangi (memastikan seluruh petak sudah diterangi)
                int y = x;
                boolean bulbFound = false;
                while(!bulbFound){ //cek petak-petak di sebelah kanan apakah ada lampu yang menerangi petak ini
                    y++;
                    if(y < genes.length && y < currMaxRowIdx){
                        if(genes[y] == 'n' || genes[y] == '0' || genes[y] == '1' || genes[y] == '2' || genes[y] == '3' || genes[y] == '4'){
                            break;
                        }
                        else if(genes[y] == 'y'){
                            bulbFound = true;
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
                    if(y > -1 && y > currMinRowIdx){
                        if(genes[y] == 'n' || genes[y] == '0' || genes[y] == '1' || genes[y] == '2' || genes[y] == '3' || genes[y] == '4'){
                            break;
                        }
                        else if(genes[y] == 'y'){
                            bulbFound = true;
                            break;
                        }
                    }
                    else{
                        break;
                    }
                }
                
                y=x;
                while(!bulbFound){ //cek petak-petak di atas apakah ada lampu yang menerangi petak ini
                    y -= rowSize;
                    if(y > -1){
                        if(genes[y] == 'n' || genes[y] == '0' || genes[y] == '1' || genes[y] == '2' || genes[y] == '3' || genes[y] == '4'){
                            break;
                        }
                        else if(genes[y] == 'y'){
                            bulbFound = true;
                            break;
                        }
                    }
                    else{
                        break;
                    }
                }
                
                y=x;
                while(!bulbFound){ //cek petak-petak di bawah apakah ada lampu yang menerangi petak ini
                    y += rowSize;
                    if(y < GeneticAlgorithm.arrSize){
                        if(genes[y] == 'n' || genes[y] == '0' || genes[y] == '1' || genes[y] == '2' || genes[y] == '3' || genes[y] == '4'){
                            break;
                        }
                        else if(genes[y] == 'y'){
                            bulbFound = true;
                            break;
                        }
                    }
                    else{
                        break;
                    }
                }
                if(bulbFound == false){
                    chromosomeFitness--;
                }
            }
        }
        return chromosomeFitness;
    }
    public String toString(){
        StringBuilder s = new StringBuilder();
        for(int i=0; i<genes.length;i++)
        {
            s.append(genes[i]);
        }
        return s.toString();
    }
}
