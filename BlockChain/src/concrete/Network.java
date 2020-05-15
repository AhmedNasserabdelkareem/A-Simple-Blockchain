package concrete;

import interfaces.IBlock;
import interfaces.IMessage;
import interfaces.INTW;
import interfaces.INode;
import jdk.internal.util.xml.impl.Pair;

import java.io.*;
import java.net.*;
import java.security.PublicKey;
import java.util.ArrayList;

public class Network implements INTW {
    private ArrayList<String> peers = new ArrayList<>();
    private String PrimaryPeer  ="";
    private Node node;
    private InetAddress destination ;
    private final static int PORT =5555;
    private static ObjectOutputStream outputStream;
    private static ObjectInputStream inputStream;
    private ServerSocket ss;


    @Override
    public void setNode(Node node) throws IOException, ClassNotFoundException {
        this.node  =node;
        this.destination = InetAddress.getByName(getExternalIP());
        startServer();
    }

    @Override
    public void listenForNewConnections(String IP) {
        //TODO handle this to object
        peers.add(IP);
    }

    @Override
    public ArrayList<String> getConnectedPeers() {
        return peers;
    }

    @Override
    public void listenForTransactions(Transaction t) {
        this.node.addTransaction(t);
    }

    @Override
    public void issueTransaction(Transaction transaction) throws IOException {
        for (String peer:peers) {
            Socket socket = new Socket(InetAddress.getByName(peer), PORT);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(transaction);
            outputStream.flush();
            outputStream.close();
            socket.close();
        }
    }

    @Override
    public void shareBlock(IBlock block, String peer) throws IOException {
        Socket socket = new Socket(InetAddress.getByName(peer), PORT);
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        outputStream.writeObject(block);
        outputStream.flush();
        outputStream.close();
        socket.close();
    }

    @Override
    public void shareResponse(Block block, boolean response) throws IOException {
        Response r = new Response(block,response);
        Socket socket = new Socket(destination, PORT);
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        outputStream.writeObject(r);
        outputStream.flush();
        outputStream.close();
        socket.close();
    }

    @Override
    public void listenForResponses(Response r) {
        //TODO CHECK WHAT THIS METHOD DO BLOCK AND BOOLEAN AND DIFF WITH LISTEN FOR BLOCKS
    }

    @Override
    public void listenForBlocks(Block b) {
        this.node.receiveBlock(b);
    }

    @Override
    public String getExternalIP() throws IOException {
        URL whatismyip = new URL("http://checkip.amazonaws.com");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                whatismyip.openStream()));
        return in.readLine();
    }

    @Override
    public void startServer() throws IOException, ClassNotFoundException {
        ss = new ServerSocket(this.PORT);
        while(true){
            Socket s =ss.accept();
            inputStream = new ObjectInputStream(s.getInputStream());
            Object t = inputStream.readObject();
            if (t instanceof Transaction){
                listenForTransactions((Transaction) t);
            }else if (t instanceof Block){
                listenForBlocks((Block) t);
            }else if ( t instanceof Response) {
                listenForResponses((Response) t);
            }else if (t instanceof ArrayList){
                setPublicKeys((ArrayList<Pair>) t);
            }else{
                listenForNewConnections((String) t);
            }
        }

    }

    public void setPublicKeys(ArrayList<Pair> t) {
        node.setPublicKeys(t);
    }

    @Override
    public void broadcastlock(IBlock block) throws IOException {
        for (String peer:peers) {
            shareBlock(block,peer);
        }
    }

    @Override
    public void broadcastPK(ArrayList<Pair> keys) throws IOException {
        for (String peer:peers) {
            sharepublickeys(keys,peer);
        }
    }

    public void sharepublickeys(ArrayList<Pair> keys, String peer) throws IOException {
        Socket socket = new Socket(InetAddress.getByName(peer), PORT);
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        outputStream.writeObject(keys);
        outputStream.flush();
        outputStream.close();
        socket.close();
    }

    @Override
    public PublicKey getPrimaryID(int viewNum) {
        return null;
    }

    @Override
    public INode getPrimaryNode(int nodeIndex) {
        return null;
    }

    @Override
    public void shareMessage(IMessage message,String peer) throws IOException {
        Socket socket = new Socket(InetAddress.getByName(peer), PORT);
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        outputStream.writeObject(message);
        outputStream.flush();
        outputStream.close();
        socket.close();
    }

    @Override
    public void broadcastMessage(IMessage message) throws IOException {
        for (String p:peers
             ) {
            shareMessage(message,p);
        }

    }
}
