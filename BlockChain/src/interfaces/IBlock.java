package interfaces;

import java.util.ArrayList;

public interface IBlock {

    public IBlockHeader getHeader();
    public void setTransactions(ArrayList<ITransaction> ts);
    public ArrayList<ITransaction> getTransactions();
    public void setPrevBlock(IBlock block);
    public IBlock getPrevBlock();
    public void setAgreementMethod(IAgreementMethod method); //must be set to create  the header nonce
    public String getBlockHash();
    public ITransaction getTransactionByID(int id);

    public boolean verifyBlockHash();

    public boolean verifyPrevHash();

    public boolean verifySignature();

    public String calculateHash();


    public byte[] getClientSignature();

    public void setClientSignature(byte[] clientSignature);


}
