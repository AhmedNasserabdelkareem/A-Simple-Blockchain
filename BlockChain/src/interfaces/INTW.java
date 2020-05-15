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
    public void listenForNewConnections(String ip); //listens for any new node request and add it to peers List
    public ArrayList<String> getConnectedPeers();
    public void listenForTransactions(Transaction t);
    public void issueTransaction(Transaction transaction) throws IOException;//to all ips / primary
    public void shareBlock(IBlock block, String peer) throws IOException; //share the block using the agreement method
    public void shareResponse(Block block , boolean response) throws IOException;
    public void listenForResponses(Response r);
    public void listenForBlocks(Block b); //listen for any shared blocks and calls agreeOnBlock (only if node type is 1 in pow)
    public String getExternalIP() throws IOException;
    public void startServer() throws IOException, ClassNotFoundException;
    public void setPublicKeys(ArrayList<Pair> t);
    public void broadcastlock(IBlock block) throws IOException;
    public void broadcastPK(HashMap<Integer,PublicKey> keys) throws IOException;
    public void sharepublickeys(ArrayList<Pair> keys, String peer) throws IOException;

    public PublicKey getPrimaryID(int viewNum); //return the public key of the primary for the given view number

    public INode getPrimaryNode(int nodeIndex);

    public void shareMessage(IMessage message,String peer) throws IOException; //share message to all nodes
    public void broadcastMessage(IMessage message) throws IOException;
    public void listenForMessages(IMessage t) throws IOException;
    public boolean isPrimary();

    public void setPrimary(boolean primary);
    public int getsizeofPeers();

    public void roundrobin(int index);
    public void sendConfigMessage();
    }
