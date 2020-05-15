package concrete;
import interfaces.*;
import jdk.internal.util.xml.impl.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.PublicKey;
import java.security.Signature;

import java.util.ArrayList;
import java.util.HashMap;

public class Node implements INode {
    HashMap<Integer,ITransaction> AvOps = new HashMap<>();
    ArrayList<Integer> newAddedTs = new ArrayList<>();
    private INTW network;
    private int nodeType;
    private int maxTransaction;
    private IAgreementMethod agreementMethod;
    private ArrayList<String> peers;
    private ArrayList<ITransaction> transactions;
    private IBlock currentBlock;
    private ArrayList<IBlock> blockChain;
    private ArrayList<Pair> id2keys;
    private final String CONFIG_FILE;


    public Node(String config_file) throws IOException {
        CONFIG_FILE = config_file;
        peers = new ArrayList<>();
        transactions = new ArrayList<>();
        blockChain = new ArrayList<>();
        id2keys = new ArrayList<>();
        readConfiguration();
    }

    public void readConfiguration() throws IOException {
        //TODO read from remote file >> config
        URL conf = new URL(CONFIG_FILE);
        BufferedReader in = new BufferedReader(new InputStreamReader(conf.openStream()));
        String res = in.readLine();
        //TODO Split file
        //setConfigs();
    }

    @Override
    public void setConfigs(int maxNumTransactions, IAgreementMethod method, ArrayList<String> IPsOfOtherPeers, int nodeType) {
        this.nodeType = nodeType;
        this.agreementMethod = method;
        this.maxTransaction = maxNumTransactions;
        this.peers=IPsOfOtherPeers;
    }

    @Override
    public void setNTW(INTW ntw) {
        this.network = ntw;
    }

    @Override
    public int getNodeType() {
        return nodeType;
    }

    @Override
    public void addTransaction(ITransaction t) {

        if(verifyTransaction(t)){
            newAddedTs.add(t.getID());
            AvOps.put(t.getID(),t);
            transactions.add(t);
            if(transactions.size()==maxTransaction){
                createBlock();
                transactions.clear();
                AvOps.clear();

            }
        }
    }

    @Override
    public void createBlock() {
        currentBlock = new Block();
        currentBlock.setAgreementMethod(agreementMethod);
        currentBlock.setTransactions(transactions);
        //TODO SET SIGNATURE AND PREV BLOCK
    }

    private boolean verifyTransactionSign(ITransaction t){
        int signer = t.getIPs().get(0);
        byte[] signature = t.getSignedHash();

        boolean b =false;
        try {
            Signature s = Signature.getInstance("SHA1WithRSA");
            s.initVerify(t.getPayerPK());
            s.update(t.hash().getBytes());
            b = s.verify(t.getSignedHash());
        }catch (Exception e){
            e.printStackTrace();
        }
        return  b;
    }

    private boolean verifyTransactionVal(ITransaction t){
        int prevID = t.getPrevID();
        ITransaction prev =getUnspentTransactionByID(prevID);
        if(prev == null ){
            return false;
        }
        int out = t.getOutIndex();
        float totalPayed =0;
        for (ITransaction.OutputPair p : t.getOPs()){
            totalPayed+=p.value;
        }
        ArrayList<ITransaction.OutputPair>ops =  prev.getOPs();
        boolean av = prev.getOPs().get(out).available >= totalPayed;
        if(!av){
            return false;
        }
        prev.getOPs().get(out).available -= totalPayed ;

        return  true;
    }



    @Override
    public boolean verifyTransaction(ITransaction t) {
        return verifyTransactionSign(t) && verifyTransactionVal(t);
    }

    @Override
    public void resetUnspent() {
        for (ITransaction t : AvOps.values()){
            for (ITransaction.OutputPair o : t.getOPs()){
                o.available = o.committedVal;
            }
        }
        for (int i : newAddedTs){
            AvOps.remove(i);
        }
        //TODO remove all the transactions added to the hashmap in the last round
    }

    @Override
    public void commitUnspent() {
        for (ITransaction t : AvOps.values()) {
            float totAv =0;
            for (ITransaction.OutputPair p : t.getOPs()) {
                totAv += p.available;
                p.committedVal = p.available;
            }
            if(totAv <=0){
                AvOps.remove(t.getID());
            }
        }
    }

    @Override
    public void issueTransactions(int from, int to) {

    }

    @Override
    public ITransaction getUnspentTransactionByID(int id) {
        return  AvOps.get(id);
    }

    @Override
    public void shareBlock(IBlock block) throws IOException {
        network.broadcastlock(block);
    }

    @Override
    public void receiveBlock(IBlock block) {

    }

    @Override
    public void addToChain(IBlock block) {
        blockChain.add(block);
    }

    @Override
    public IBlock getLastBlock() {
        return null;
    }

    @Override
    public PublicKey getPrimaryId() {
        return null;
    }

    @Override
    public void setPrimaryId(PublicKey primaryId) {

    }

    @Override
    public IBlockManager getBlockManager() {
        return null;
    }

    @Override
    public void setBlockManager(IBlockManager blockManager) {

    }

    @Override
    public int getSeqNum() {
        return 0;
    }

    @Override
    public void setSeqNum(int seqNum) {

    }

    @Override
    public int getViewNum() {
        return 0;
    }

    @Override
    public void setViewNum(int viewNum) {

    }

    @Override
    public void setIsPrimary(boolean isPrimary) {

    }

    @Override
    public boolean getIsPrimary() {
        return false;
    }

    @Override
    public void setState(String state) {

    }

    @Override
    public String getState() {
        return null;
    }

    @Override
    public void setMaxMaliciousNodes(int f) {

    }

    @Override
    public int getMaxMaliciousNodes() {
        return 0;
    }

    @Override
    public IMessagePool getNewBlockMessagePool() {
        return null;
    }

    @Override
    public void insertNewBlockMessageInPool(IMessage newBlockMessage) {

    }

    @Override
    public IMessagePool getPrepreparePool() {
        return null;
    }

    @Override
    public void insertPreprepareMessageInPool(IMessage preprepareMessage) {

    }

    @Override
    public IMessagePool getPreparePool() {
        return null;
    }

    @Override
    public void insertPrepareMessageInPool(IMessage prepareMessage) {

    }

    @Override
    public IMessagePool getCommitPool() {
        return null;
    }

    @Override
    public void insertCommitMessageInPool(IMessage commitMessage) {

    }

    @Override
    public IMessagePool getChangeViewPool() {
        return null;
    }

    @Override
    public void insertChangeViewPool(IMessage changeViewMessage) {

    }

    @Override
    public IMessagePool getViewChangedPool() {
        return null;
    }

    @Override
    public void insertViewChangedPool(IMessage viewChangedMessage) {

    }

    @Override
    public IMessage generateNewBlockMessage() {
        return null;
    }

    @Override
    public IMessage generateViewChangeMessage(int newViewNum) {
        return null;
    }

    @Override
    public IMessage generateViewChanged() {
        return null;
    }

    @Override
    public IMessage generatePreprepareMessage() {
        return null;
    }

    @Override
    public IMessage generatePrepareMessage() {
        return null;
    }

    @Override
    public IMessage generateCommitMessage() {
        return null;
    }

    @Override
    public void ignoreBlock() {

    }

    @Override
    public void broadCastMessage(IMessage message) {

    }

    @Override
    public void broadCastPublicKeys(ArrayList<Pair> keys) throws IOException {
        network.broadcastPK(keys);
    }

    @Override
    public void setPublicKeys(ArrayList<Pair> t) {
        this.id2keys = t;
        Utils.getInstance().setID2PK(t);
    }
}
