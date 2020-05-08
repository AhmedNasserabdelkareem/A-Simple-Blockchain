public interface IBlock {
    public IBlockHeader getHeader();
    public void setTransactions(ITransaction [] ts);
    public ITransaction[] getTransactions();
    public void setPrevBlock(IBlock block);
    public IBlock getPrevBlock();
    public void setAgreementMethod(IAgreementMethod method); //must be set to create  the header nonce
    public int getBlockHash();


    public boolean verifyBlockHash();

    public boolean verifyPrevHash();

    public boolean verifySignature();

    public String calculateHash();


    public byte[] getClientSignature();

    public void setClientSignature(byte[] clientSignature);


}
