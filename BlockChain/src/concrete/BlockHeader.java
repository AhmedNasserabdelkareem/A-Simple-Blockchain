package concrete;
import interfaces.*;

import java.io.Serializable;
import java.util.ArrayList;

public class BlockHeader implements IBlockHeader, Serializable {
    private long timeStamp = 0;
    private int nonce = -1;
    private String prevBlockHash = null;
    private String transactionsHash = null;


    public BlockHeader(){
        this.timeStamp = System.currentTimeMillis() / 1000l;
    }

    @Override
    public void resetTimeStamp() {
        this.timeStamp = System.currentTimeMillis() / 1000l;
    }

    @Override
    public void createPrevBlockHash(IBlock prevBlock) {
        this.prevBlockHash = prevBlock.getBlockHash();
    }

    @Override
    public void createTransactionsHash(ArrayList<ITransaction> ts) {
        this.transactionsHash = Utils.getInstance().getMerkleRoot(ts);
    }

    @Override
    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    @Override
    public boolean isSet() {
        return (this.prevBlockHash != null) && (this.transactionsHash !=null);
    }

    @Override
    public long getTimeStamp() {
        return this.timeStamp;
    }

    @Override
    public String getTransactionsHash() {
        return this.transactionsHash;
    }

    @Override
    public String getPrevBlockHash() {
        return this.prevBlockHash;
    }

    @Override
    public int getNonce() {
        return this.nonce;
    }
}
