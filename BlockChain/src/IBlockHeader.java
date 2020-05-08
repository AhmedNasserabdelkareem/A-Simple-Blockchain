public interface IBlockHeader {
    public void createTimeStamp();
    public void createPrevBlockHash(IBlock prevBlock);
    public  void createTransactionsHash(ITransaction [] ts ); // Merkle root hash
    public  void setPOW( int pow); // -1 by default
    public boolean isSet();//returns true if the prevBlock and transactions' hash are set
    public  int getTimeStamp();
    public int getTransactionsHash();
    public int getPrevBlockHash();
    public int getPow();


}

//        hashPrevBlock: 256-bit hash of the previous block header.
//        hashMerkleRoot: 256-bit hash based on all of the transactions in block.
//        Time: Current block timestamp as seconds since 1970-01-01T00:00 UTC.
//        Nonce: 32-bit number (starts at 0) (used when PoW mining is used -1 otherwise).

