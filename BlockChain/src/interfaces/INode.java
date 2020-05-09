package interfaces;

import java.security.PublicKey;

public interface INode {
    public void setConfigs(int maxNumTransactions, IAgreementMethod method, String[] IPsOfOtherPeers, int nodeType);//0 for client , 1 for miner

    public void setNTW(INTW ntw);

    public int getNodeType();

    public void addTransaction(ITransaction t); //when # of ts == max , call create block

    public void createBlock(); //creates a block using the transactions , prev block and agreement method

    public boolean verifyTransaction(ITransaction t);

    public void shareBlock(IBlock block, INTW ntw); //share the block over the network

    // agree/disagree on a block coming from the ntw..send the decision to the ntw and add/not to the chain
    // use th agreementmethod (BFT/pow) to agree/not
    public void receiveBlock(IBlock block, INTW ntw, int flag);//flag : 0 block , 1 response

    public void addToChain(IBlock block);

    public IBlock getLastBlock();



    public PublicKey getPrimaryId();

    public void setPrimaryId(PublicKey primaryId);

    public IBlockManager getBlockManager();

    public void setBlockManager(IBlockManager blockManager);

    public int getSeqNum();

    public void setSeqNum(int seqNum);

    public int getViewNum();

    public void setViewNum(int viewNum);

    public void setIsPrimary(boolean isPrimary);

    public boolean getIsPrimary();

    public void setState(String state);

    public String getState();

    public void setMaxMaliciousNodes(int f);

    public int getMaxMaliciousNodes();

    public IMessagePool getNewBlockMessagePool();

    public void insertNewBlockMessageInPool(IMessage newBlockMessage);

    public IMessagePool getPrepreparePool();

    public void insertPreprepareMessageInPool(IMessage preprepareMessage);

    public IMessagePool getPreparePool();

    public void insertPrepareMessageInPool(IMessage prepareMessage);

    public IMessagePool getCommitPool();

    public void insertCommitMessageInPool(IMessage commitMessage);

    public IMessagePool getChangeViewPool();

    public void insertChangeViewPool(IMessage changeViewMessage);

    public IMessagePool getViewChangedPool();

    public void insertViewChangedPool(IMessage viewChangedMessage);

    public IMessage generateNewBlockMessage();

    public IMessage generateViewChangeMessage(int newViewNum);

    public IMessage generateViewChanged();

    public IMessage generatePreprepareMessage();

    public IMessage generatePrepareMessage();

    public IMessage generateCommitMessage();

    public void ignoreBlock();

    public void broadCastMessage(IMessage message);


}
