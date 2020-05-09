package concrete;
import interfaces.*;

import java.util.ArrayList;

public class Block implements IBlock {
    private IBlockHeader header;
    private ArrayList<ITransaction> ts;
    private IBlock prevBlock=null;
    public Block(){
        this.header = new BlockHeader();
    }
    @Override
    public IBlockHeader getHeader() {
        return null;
    }

    @Override
    public void setTransactions(ArrayList<ITransaction> ts) {
        this.header.createTransactionsHash(ts);
        this.ts=ts;
    }

    @Override
    public ArrayList<ITransaction> getTransactions() {
        return this.ts;
    }

    @Override
    public void setPrevBlock(IBlock block) {
        this.header.createPrevBlockHash(block);
        this.prevBlock = block;
    }

    @Override
    public IBlock getPrevBlock() {
        return this.prevBlock;
    }

    @Override
    public void setAgreementMethod(IAgreementMethod method) {

    }

    @Override
    public String getBlockHash() {
        return null;
    }

    @Override
    public boolean verifyBlockHash() {
        return false;
    }

    @Override
    public boolean verifyPrevHash() {
        return false;
    }

    @Override
    public boolean verifySignature() {
        return false;
    }

    @Override
    public String calculateHash() {
        return null;
    }

    @Override
    public byte[] getClientSignature() {
        return new byte[0];
    }

    @Override
    public void setClientSignature(byte[] clientSignature) {

    }

    @Override
    public ITransaction getTransactionByID(int id) {
        return null;
    }
}