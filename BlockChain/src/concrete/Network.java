package concrete;

import interfaces.IBlock;
import interfaces.IMessage;
import interfaces.INTW;
import interfaces.INode;

import java.io.*;
import java.net.*;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Network implements INTW ,Runnable{
    private ArrayList<String> peers = new ArrayList<>();
    private ArrayList<String> ips = new ArrayList<>();
    private ArrayList<Integer> nodeTypes = new ArrayList<>();
    private ArrayList<String> tableOfNodes = new ArrayList<>();
    private String PrimaryPeer  ="";
    private Node node;
    private InetAddress sourceIP;
    private final static int PORT =5555;
    private static ObjectOutputStream outputStream;
    private static ObjectInputStream inputStream;
    private ServerSocket ss;

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    @Override
    public int getsizeofPeers() {
        return peers.size();
    }

    public void sendConfigMessage(IMessage m) throws IOException {
        isPrimary = false;
        for (String peer:peers) {


            if (peer.equals(getNextPrimary())){
                m.setisPrimary(true);
            }
                Socket socket = new Socket(InetAddress.getByName(peer), PORT);
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.writeObject(m);
                outputStream.flush();
                outputStream.close();
                socket.close();

        }

    }


    public void constructTable() throws IOException {
        tableOfNodes.clear();
        tableOfNodes.add(getExternalIP());
        for (String p:peers) {
            tableOfNodes.add(p);
        }
        Collections.sort(tableOfNodes,new AlphanumComparator());
    }

    public String getNextPrimary() {
        return tableOfNodes.get((tableOfNodes.indexOf(sourceIP.getHostAddress())+1)%tableOfNodes.size());
    }

    private boolean isPrimary=false;


    @Override
    public void setNode(Node node) throws IOException {
        this.node  =node;
        this.sourceIP = InetAddress.getByName(getExternalIP());
        constructTable();

        if (tableOfNodes.get(0).equals(getExternalIP())){
            setPrimary(true);
        }
    }

    @Override
    public void listenForNewConnections(String IP) throws IOException {
        //TODO handle this to object
        peers.add(IP);
        constructTable();
    }

    @Override
    public ArrayList<String> getConnectedPeers() {
        return peers;
    }

    @Override

    public void listenForTransactions(Transaction t) throws IOException {
        this.node.addTransaction(t);
    }

    @Override
    public void issueTransaction(Transaction transaction) throws IOException {
        //System.out.println(peers);
        for (String peer:peers) {
            //System.out.println(peer);
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
    public void shareResponse(Response r,String peer) throws IOException {
        Socket socket = new Socket(InetAddress.getByName(peer), PORT);
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        outputStream.writeObject(r);
        outputStream.flush();
        outputStream.close();
        socket.close();
    }
    public void broadcastResponse(Block block,boolean response) throws IOException {
        Response r = new Response(block,response);
        for (String p:peers) {
            shareResponse(r,p);
        }
    }

    @Override
    public void sendPeers(ArrayList<String> ips,ArrayList<Integer> nodeTypes) throws IOException {
        peers.clear();
        this.nodeTypes = nodeTypes;
        int indexOfIssuer = nodeTypes.indexOf(0);
        this.ips = ips;
        if (node.getNodeType() ==0){
            for (String p : ips) {
                if (!p.equals(getExternalIP())) {
                        peers.add(p);
                }
            }
        }else {
            for (String p : ips) {
                if (!p.equals(getExternalIP())) {
                    if (!p.equals(ips.get(indexOfIssuer)))
                        peers.add(p);
                }
            }
        }
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
            }else if (t instanceof HashMap){
                setPublicKeys((HashMap<Integer, PublicKey>) t);
            }else if (t instanceof Message) {
                listenForMessages((IMessage) t);
            }else {
                listenForNewConnections((String) t);
            }
        }

    }

    public void listenForMessages(IMessage t) throws IOException {
        node.receiveMessage(t);
    }

    public void setPublicKeys(HashMap<Integer,PublicKey> t) {
        node.setPublicKeys(t);
    }

    @Override
    public void broadcastlock(IBlock block) throws IOException {
        for (String peer:peers) {
            shareBlock(block,peer);
        }
    }

    @Override
    public void broadcastPK(HashMap<Integer, PublicKey> keys) throws IOException {
        for (String peer:peers) {
            sharepublickeys(keys,peer);
        }
    }

    public void sharepublickeys(HashMap<Integer, PublicKey> keys, String peer) throws IOException {
        Socket socket = new Socket(InetAddress.getByName(peer), PORT);
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        outputStream.writeObject(keys);
        outputStream.flush();
        outputStream.close();
        socket.close();
    }

    @Override
    public void broadcastPK(String ip, PublicKey publicKey) throws IOException {

    }

    @Override
    public void sharepublickeys(String ip, PublicKey publicKey, String peer) throws IOException {

    }

    @Override
    public String getIP() {
        return null;
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
        for (String p:peers) {
            shareMessage(message,p);
        }

    }

    @Override
    public void run() {
        try {
            startServer();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}