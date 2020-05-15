package interfaces;

import jdk.internal.util.xml.impl.Pair;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.PublicKey;
import java.util.ArrayList;

public interface INode {
    public void setConfigs(boolean isPow,int maxNumTransactions, IAgreementMethod method, ArrayList<String> IPsOfOtherPeers, int nodeType);//0 for client , 1 for miner
    public void issueTransactions(int from , int to);//for client nodes, issue for ids from .. to ..
    public void setNTW(INTW ntw);

    public int getNodeType();

    public void addTransaction(ITransaction t); //when # of ts == max , call create block

    public void createBlock(); //creates a block using the transactions , prev block and agreement method

    public boolean verifyTransaction(ITransaction t);

    //returns the tr with that id if it still has values !=0 , null otherwise
    public ITransaction getUnspentTransactionByID(int id);
    //called if the block was rejected to reset the available to be equal to the original value
    public void resetUnspent();
    //called if the block was accepted to decrease the values to be equal to availabl
    public void commitUnspent();
    public void shareBlock(IBlock block) throws IOException; //share the block over the network

    // agree/disagree on a block coming from the ntw..send the decision to the ntw and add/not to the chain
    // use th agreementmethod (BFT/pow) to agree/not
    public void receiveBlock(IBlock block);//flag : 0 block , 1 response

    public void addToChain(IBlock block);

    public IBlock getLastBlock();

    public PublicKey getPrimaryId();

    public void setPrimaryId(PublicKey primaryId);


    public int getSeqNum();
    public void setSeqNum();

    public PublicKey getNodePublicKey();
    public int getViewNum();

    public void setViewNum(int viewNum);

    public void setIsPrimary(boolean isPrimary);

    public boolean getIsPrimary();

    public void setState(String state);

    public String getState();

    public void setMaxMaliciousNodes(int f);

    public int getMaxMaliciousNodes();

    public IMessagePool getPreparePool();

    public IMessagePool getCommitPool();


    public IMessagePool getChangeViewPool();


    public void generateNewBlockMessage() throws IOException;

    public void generateViewChangeMessage(int newViewNum) throws IOException;

    public void generateViewChangedMessage() throws IOException;

    public void generatePreprepareMessage() throws IOException;

    public void generatePrepareMessage() throws IOException;

    public void generateCommitMessage() throws IOException;

    public void broadcastMessage(IMessage message) throws IOException;
    public void addTransaction(ArrayList<ITransaction> ledger);
    public void broadCastPublicKeys(ArrayList<Pair> keys) throws IOException;

    void setPublicKeys(ArrayList<Pair> t);
    public void readConfiguration() throws IOException;


    public void generateKeyPair();
    public void generateNodeSignature();
    public IBlock getNewBlock();
    public void setNewBlock(IMessage newBlockMessage);
    public void insertPreprepareMessage(IMessage preprepareMessage);
    public void insertPrepareMessageInPool(ArrayList<IMessage> prepareMessages) throws IOException;
    public void insertCommitMessageInPool(ArrayList<IMessage> commitMessages);
    public void insertChangeViewMessageInPool(ArrayList<IMessage> changeViewMessages) throws IOException;
    public void checkTruthyOfNewView(IMessage viewChangedMessage);
    public boolean verifyNewViewPool(IMessagePool messagePool);
    public INode getPrimaryNode(int nodeIndex);
}
