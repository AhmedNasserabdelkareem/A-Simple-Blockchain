package interfaces;

import concrete.Block;
import concrete.Node;
import concrete.Response;
import concrete.Transaction;
import jdk.internal.util.xml.impl.Pair;

import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;

public interface INTW {
    public void setNode(Node node) throws IOException, ClassNotFoundException;
    public void listenForNewConnections(String ip) throws IOException; //listens for any new node request and add it to peers List
    public ArrayList<String> getConnectedPeers();
    public void listenForTransactions(Transaction t) throws IOException;
    public void issueTransaction(Transaction transaction) throws IOException;//to all ips / primary
    public void shareBlock(IBlock block, String peer) throws IOException; //share the block using the agreement method
    public void listenForResponses(Response r);
    public void listenForBlocks(Block b); //listen for any shared blocks and calls agreeOnBlock (only if node type is 1 in pow)
    public String getExternalIP() throws IOException;
    public void startServer() throws IOException, ClassNotFoundException;
    public void setPublicKeys(HashMap<Integer,PublicKey> t);
    public void broadcastlock(IBlock block) throws IOException;
    public void broadcastPK(HashMap<Integer,PublicKey> keys) throws IOException;
    public void sharepublickeys(HashMap<Integer, PublicKey> keys, String peer) throws IOException;

    public void broadcastPK(String ip,PublicKey publicKey) throws IOException;
    public void sharepublickeys(String ip,PublicKey publicKey,String peer) throws IOException;
    public String getIP();

    public PublicKey getPrimaryID(int viewNum); //return the public key of the primary for the given view number

    public INode getPrimaryNode(int nodeIndex);

    public void shareMessage(IMessage message,String peer) throws IOException; //share message to all nodes
    public void broadcastMessage(IMessage message) throws IOException;
    public void listenForMessages(IMessage t) throws IOException;
    public boolean isPrimary();

    public void setPrimary(boolean primary);
    public int getsizeofPeers();

    public void sendConfigMessage(IMessage m) throws IOException;
    public String getNextPrimary();
    public void constructTable() throws IOException;
    public void shareResponse(Response r,String peer) throws IOException;
    public void broadcastResponse(Block block,boolean response) throws IOException;

    void sendPeers(ArrayList<String> ips) throws IOException;
}
