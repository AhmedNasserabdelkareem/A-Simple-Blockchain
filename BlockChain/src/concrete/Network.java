package concrete;

import interfaces.IAnalyser;
import interfaces.IBlock;
import interfaces.IMessage;
import interfaces.INTW;

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
    private String ExternalIP  ="";
    private Node node;
    private InetAddress sourceIP;
    private final static int PORT =5555;
    private static ObjectOutputStream outputStream;
    private static ObjectInputStream inputStream;
    private ServerSocket ss;
    ArrayList<Socket> sockets;

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
                m.setPrimaryPublicKey(getPkfromPairPK(getNextPrimary()));
            }
            if (!getExternalIP().equals(getNextPrimary())) {
                outputStream = new ObjectOutputStream(sockets.get(peers.indexOf(peer)).getOutputStream());
                outputStream.writeObject(m);
                outputStream.flush();
                //outputStream.close();
                //socket.close();
            }
        }

    }
    public void sendConfigMessageAtFirst(IMessage m) throws IOException {
        for (String peer:peers) {
            if (peer.equals(getExternalIP())){
                m.setisPrimary(true);
                m.setPrimaryPublicKey(getPkfromPairPK(getNextPrimary()));
            }else{

            }
                Socket socket = new Socket(InetAddress.getByName(peer), PORT);
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.writeObject(m);
                outputStream.flush();
                //outputStream.close();
                //socket.close();

        }

    }

    public PublicKey getPkfromPairPK(String nextPrimary) {
        for (PairKeyPK pk:node.getPublicKeysIP()) {
            if(pk.getIp().equals(nextPrimary)){
                return pk.getPk();
            }
        }
        return null;
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
        this.ExternalIP = getExternalIP();
        this.sourceIP = InetAddress.getByName(ExternalIP);
        sockets = new ArrayList<>();

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

    public void listenForTransactions(Transaction t) throws IOException, InterruptedException {
        this.node.addTransaction(t);
    }

    @Override
    public void issueTransaction(Transaction transaction) throws IOException {

        for (String peer:peers) {

            System.out.println("sending to "+ peer);
            System.out.println("sockets.get(peers.indexOf(peer)).getOutputStream()"+sockets.get(peers.indexOf(peer)).getOutputStream());
            outputStream = new ObjectOutputStream(sockets.get(peers.indexOf(peer)).getOutputStream());
            outputStream.writeObject(transaction);
            System.out.println("sent..");
            //outputStream.flush();
            //outputStream.close();

            //socket.close();
        }
    }

    @Override
    public void shareBlock(IBlock block, String peer) throws IOException {
        outputStream = new ObjectOutputStream(sockets.get(peers.indexOf(peer)).getOutputStream());
        outputStream.writeObject(block);
        outputStream.flush();
        //outputStream.close();
        //socket.close();
    }


    @Override
    public void shareResponse(Response r,String peer) throws IOException {
        outputStream = new ObjectOutputStream(sockets.get(peers.indexOf(peer)).getOutputStream());
        outputStream.writeObject(r);
        outputStream.flush();
        //outputStream.close();
        //socket.close();
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
        constructTable();
        if (tableOfNodes.get(0).equals(getExternalIP())&&node.getNodeType()==1){
            setPrimary(true);
        }
    }

    @Override
    public void listenForResponses(Response r) {
        //TODO CHECK WHAT THIS METHOD DO BLOCK AND BOOLEAN AND DIFF WITH LISTEN FOR BLOCKS
    }

    @Override
    public void listenForBlocks(Block b) throws IOException {
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
    public void startServer() throws  ClassNotFoundException,IOException{
        ss = new ServerSocket(this.PORT);
        for (String p:peers) {
            while (true) {
                try {
                    Socket so = new Socket(p, PORT);
                    so.setReceiveBufferSize(4098 * 10);
                    so.setSendBufferSize(4098 * 10);
                    sockets.add(so);
                    System.out.println("Looping");
                    break;
                } catch (Exception ignored) {
                    System.out.println("CATCH");
                }
            }
        }
        try {
            while (true) {
                System.out.println("Im in");
                Socket s = ss.accept();
                System.out.println("Get socket");
                inputStream = new ObjectInputStream(s.getInputStream());
                Object t = inputStream.readObject();
                System.out.println(t);
                if (t instanceof Transaction) {
                    listenForTransactions((Transaction) t);
                } else if (t instanceof Block) {
                    listenForBlocks((Block) t);
                } else if (t instanceof Response) {
                    listenForResponses((Response) t);
                } else if (t instanceof PairKeyPK) {
                    listenforPublicKey((PairKeyPK) t);
                } else if (t instanceof HashMap) {
                    setPublicKeys((HashMap<Integer, PublicKey>) t);
                } else if (t instanceof Message) {
                    listenForMessages((IMessage) t);
                } else {
                    listenForNewConnections((String) t);
                }
            }
        }catch (InterruptedException | IOException e){
            System.out.println("Interruptes");
        }

    }

    public void listenforPublicKey(PairKeyPK t) {
        node.receivePK(t);
    }

    public void listenForMessages(IMessage t) throws IOException, InterruptedException {
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
        outputStream = new ObjectOutputStream(sockets.get(peers.indexOf(peer)).getOutputStream());
        outputStream.writeObject(keys);
        outputStream.flush();
        //outputStream.close();
        //socket.close();
    }

    @Override
    public void broadcastPK(PairKeyPK pair) throws IOException {
        for (String p:peers) {
            sharepublickeys(pair,p);
        }
    }

    @Override
    public void sharepublickeys(PairKeyPK pair, String peer) throws IOException {
        //outputStream.flush();
        outputStream = new ObjectOutputStream(sockets.get(peers.indexOf(peer)).getOutputStream());
        outputStream.writeObject(pair);
        outputStream.flush();
        //outputStream.close();
        //socket.close();
    }

    @Override
    public String getIP() throws IOException {
        return getExternalIP();
    }


    @Override
    public void shareMessage(IMessage message,String peer) throws IOException {

        outputStream = new ObjectOutputStream(sockets.get(peers.indexOf(peer)).getOutputStream());
        //outputStream.flush();
        outputStream.writeObject(message);
        //outputStream.reset();
        outputStream.flush();
        //outputStream.close();

    }

    @Override
    public void broadcastMessage(IMessage message) throws IOException {
        for (String p:peers) {
            shareMessage(message,p);
        }
        //TODO 1N SOLUTION SEND TO ME THE NEW BLOCK MESSAGE

    }

    @Override
    public void run() {
        try {
            startServer();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void broadcastAnalytics(IAnalyser.Analytics a) {

    }//TODO and put this         Analyser.getInstance().reportMessageSent(); wherever you send messege related to block/response ...
}