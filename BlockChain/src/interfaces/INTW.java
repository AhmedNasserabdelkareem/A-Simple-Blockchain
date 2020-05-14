package interfaces;

import concrete.Block;
import concrete.Node;
import concrete.Response;
import concrete.Transaction;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public interface INTW {
    public void setNode(Node node) throws IOException, ClassNotFoundException;
    public void listenForNewConnections(String ip); //listens for any new node request and add it to peers List
    public ArrayList<String> getConnectedPeers();
    public void listenForTransactions(Transaction t);
    public void issueTransaction(Transaction transaction) throws IOException;//to all ips / primary
    public void shareBlock(Block block) throws IOException; //share the block using the agreement method
    public void shareResponse(Block block , boolean response) throws IOException;
    public void listenForResponses(Response r);
    public void listenForBlocks(Block b); //listen for any shared blocks and calls agreeOnBlock (only if node type is 1 in pow)
    public String getExternalIP() throws IOException;
    public void startServer() throws IOException, ClassNotFoundException;

}
