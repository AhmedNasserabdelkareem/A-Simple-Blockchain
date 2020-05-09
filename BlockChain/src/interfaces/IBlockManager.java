package interfaces;

public interface IBlockManager {

    public void initiateNewBlockMessage();

    /*stop adding transactions to the block*/
    public IMessage finalizeBlock();

    /*primary node will call it after receiving 2f+1 commit message*/
    public void addBlockToChain(IBlock block);
}
