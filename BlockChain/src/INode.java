public interface INode {
    public void setConfigs(int maxNumTransactions, IAgreementMethod method ,String [] IPsOfOtherPeers, int nodeType);//0 for client , 1 for miner
    public void setNTW(INTW ntw);
    public int getNodeType();
    public void addTransaction(ITransaction t); //when # of ts == max , call create block
    public void createBlock(); //creates a block using the transactions , prev block and agreement method
    public boolean verifyTransaction(ITransaction t);
    public void shareBlock(IBlock block,INTW ntw); //share the block over the network
    // agree/disagree on a block coming from the ntw..send the decision to the ntw and add/not to the chain
    // use th agreementmethod (BFT/pow) to agree/not
    public void receiveBlock(IBlock block, INTW ntw, int flag);//flag : 0 block , 1 response
    public void addToChain(IBlock block);
    public IBlock getLastBlock();
}
