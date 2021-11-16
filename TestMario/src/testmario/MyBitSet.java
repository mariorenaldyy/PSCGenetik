package testmario;

import java.util.BitSet;
public class MyBitSet extends BitSet {
    private int realSize;
    
    public MyBitSet(int realsize) {
        super(realsize);
        this.realSize=realsize;
    }

    public int realSize()
    {
        return this.realSize;
    }
}
