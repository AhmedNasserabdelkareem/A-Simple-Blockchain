public interface INTW {
    public void setNode(INode node);
    public void listenForNewConnections(); //listens for any new node request and add it to peers List
    public String[] getConnectedPeers();
    public void listenForTransactions();
    public void issueTransaction(ITransaction transaction);//to all ips
    public void shareBlock(IBlock block); //share the block using the agreement method
    public void shareResponse(IBlock block , boolean resonse);
    public void listenForResponses();
    public void listenForBlocks(); //listen for any shared blocks and calls agreeOnBlock (only if node type is 1 in pow)

}
