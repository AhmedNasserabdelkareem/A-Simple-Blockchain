package concrete;
import interfaces.*;
import jdk.internal.util.xml.impl.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.*;

import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

public class Node implements INode {
    HashMap<Integer, ITransaction> AvOps = new HashMap<>();
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
    private PublicKey nodePublicKey;
    private PrivateKey nodePrivateKey;
    private PublicKey primaryNodePublicKey; // primary node's public key
    private int seqNum = 0; //the sequence number of the current block the node is working on
    private int viewNum = 0; //round number
    private int newViewNum = 0; // next view we are moving to
    private String state = "";//which phase the node is currently in
    private String prevState = "";//prev state before requesting view change
    private int maxMaliciousNodes = 0; //max f nodes out of 3f+1 node
    private IBlock block; //current block the node is validating
    private IBlock newBlock; //client (validator) block
    // private ArrayList<PublicKey> peers;
    private IMessagePool preparePool; //contains prepare messages
    private IMessagePool commitPool; // contains commit messages
    private IMessagePool viewChangePool;
    private byte[] nodeSignature;
    private IValidator validator; //validator is a helper node
    private Timer configTimer;
    private Timer idleTimer;
    private Timer viewChangeTimer;
    private ArrayList<ITransaction> ledger;
    private ArrayList<IBlock> chain;
    private int chainIndex = 0;
    private int maxNumTransactions;
    private IAgreementMethod method;
    private String[] IPsOfOtherPeers;


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
        this.peers = IPsOfOtherPeers;
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
            if (transactions.size() == maxTransaction) {
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

    private boolean verifyTransactionSign(ITransaction t) {
        int signer = t.getIPs().get(0);
        byte[] signature = t.getSignedHash();

        boolean b = false;
        try {
            Signature s = Signature.getInstance("SHA1WithRSA");
            s.initVerify(t.getPayerPK());
            s.update(t.hash().getBytes());
            b = s.verify(t.getSignedHash());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    private boolean verifyTransactionVal(ITransaction t) {
        int prevID = t.getPrevID();
        ITransaction prev = getUnspentTransactionByID(prevID);
        if (prev == null) {
            return false;
        }
        int out = t.getOutIndex();
        float totalPayed = 0;
        for (ITransaction.OutputPair p : t.getOPs()) {
            totalPayed += p.value;
        }
        ArrayList<ITransaction.OutputPair> ops = prev.getOPs();
        boolean av = prev.getOPs().get(out).available >= totalPayed;
        if (!av) {
            return false;
        }
        prev.getOPs().get(out).available -= totalPayed;

        return true;
    }

    public ArrayList<ITransaction> verifyBlockTransactions(ArrayList<ITransaction> transactions){
        for(int i =0; i<transactions.size();i++){
            if(!verifyTransaction(transactions.get(i)))
                transactions.remove(i);
        }
        return  transactions;
    }

    @Override
    public boolean verifyTransaction(ITransaction t) {
        return verifyTransactionSign(t) && verifyTransactionVal(t);
    }

    @Override
    public void resetUnspent() {
        for (ITransaction t : AvOps.values()) {
            for (ITransaction.OutputPair o : t.getOPs()) {
                o.available = o.committedVal;
            }
        }
        for (int i : newAddedTs) {
            AvOps.remove(i);
        }
        //TODO remove all the transactions added to the hashmap in the last round
    }

    @Override
    public void commitUnspent() {
        for (ITransaction t : AvOps.values()) {
            float totAv = 0;
            for (ITransaction.OutputPair p : t.getOPs()) {
                totAv += p.available;
                p.committedVal = p.available;
            }
            if (totAv <= 0) {
                AvOps.remove(t.getID());
            }
        }
    }

    @Override
    public void issueTransactions(int from, int to) {

    }

    @Override
    public ITransaction getUnspentTransactionByID(int id) {
        return AvOps.get(id);
    }

    @Override
    public void shareBlock(IBlock block) throws IOException {
        network.broadcastlock(block);
    }


    @Override
    public void addToChain(IBlock block) {
        blockChain.add(block);
    }

    @Override
    public void broadCastPublicKeys(ArrayList<Pair> keys) throws IOException {
        network.broadcastPK(keys);

    }

    @Override
    public void setPublicKeys(ArrayList<Pair> t) {
        this.id2keys = t;
    }


        
////////////////////////////////////////////////////////////////////////////////////////

    /*Generate the public and private key for the node*/
    @Override
    public void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            // Initialize the key generator and generate a KeyPair
            keyGen.initialize(ecSpec, random);   //256 bytes provides an acceptable security level
            KeyPair keyPair = keyGen.generateKeyPair();
            // Set the public and private keys from the keyPair
            this.nodePrivateKey = keyPair.getPrivate();
            this.nodePublicKey = keyPair.getPublic();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*Generate a signature for the node using her private key to sign the message*/
    @Override
    public void generateNodeSignature() {
        String data = Utils.getStringFromKey(this.nodePublicKey) + String.valueOf(this.seqNum) +
                String.valueOf(this.viewNum) + this.newBlock.getHeader().getTransactionsHash();
        this.nodeSignature = Utils.applyECDSASig(this.nodePrivateKey, data);

    }


    /*this is the new block that the client broadcasts it to all nodes*/
    @Override
    public IBlock getNewBlock() {
        return this.newBlock;
    }

    /*the node will save the client block with her*/
    @Override
    public void setNewBlock(IMessage newBlockMessage) {
        if (newBlockMessage.getMessageType().equals("new block") &&
                newBlockMessage.getPrimaryPublicKey() == primaryNodePublicKey &&
                newBlockMessage.getSeqNum() == this.seqNum && newBlockMessage.getViewNum() == this.viewNum
                && newBlockMessage.getBlock().verifySignature()) { // i mean by block signature -> transaction signature
            this.newBlock = newBlockMessage.getBlock();
        }
        generateNodeSignature();

    }

    /*this is only for the primary which will let the client to sent the block*/
    @Override
    public void generateNewBlockMessage() throws IOException {
        this.validator = new Validator(this.primaryNodePublicKey, this.seqNum, this.viewNum, this.maxMaliciousNodes);
        this.validator.initiateNewBlockMessage(getLastBlock(), getBlockTransactions());
        // timer
        IMessage newBlockMessage = validator.finalizeBlock();
        broadcastMessage(newBlockMessage);

    }

    /*this is for the primary node it's the only one who can generate the pre-prepare message
     * and broadcast it to all nodes*/
    @Override
    public void generatePreprepareMessage() throws IOException {
        IMessage prePrepareMessage = new Message("pre-prepare", this.primaryNodePublicKey, this.seqNum, this.viewNum, this.nodeSignature, this.nodePublicKey, this.newBlock);
        broadcastMessage(prePrepareMessage);
    }

    /*All nodes except the primary will validate the pre-prepare message and if valid
     * they will take the block inside it to continue the next phases*/
    @Override
    public void insertPreprepareMessage(IMessage preprepareMessage) {
        if (preprepareMessage.getMessageType().equals("pre-prepare") && preprepareMessage.verifyPeerSignature() &&
                preprepareMessage.getPrimaryPublicKey() == this.primaryNodePublicKey &&
                preprepareMessage.getViewNum() == this.viewNum &&
                preprepareMessage.getBlock().getHeader().getHash().equals(this.newBlock.getHeader().getHash())) {

            this.state = "pre-prepare";
            this.seqNum = preprepareMessage.getSeqNum();
            this.block = preprepareMessage.getBlock();
        } else {
            System.out.println("Error with secondary Node in preprepare phase verification so the node will ignore this message");
        }
    }


    @Override
    public IMessagePool getPreparePool() {
        return this.preparePool;
    }


    /*All nodes except the primary will generate this message and broadcasts it to all the network
     * the node has to finish the pre-prepare state before entering prepare state*/
    @Override
    public void generatePrepareMessage() throws IOException {
        if (this.state.equals("pre-prepare")) {
            IMessage prepareMessage = new Message("prepare", this.primaryNodePublicKey, this.seqNum, this.viewNum, this.nodeSignature, this.block, this.nodePublicKey);
            this.preparePool.insertMessage(prepareMessage);
            broadcastMessage(prepareMessage);
        } else {
            System.out.println("Node is out it won't generate a prepare message");
        }
    }

    /*the node will validate the prepare messages that are sent to here
     * and insert them in her prepare pool if a message is invalid it will ignore it.
     * the node has to receive min 2*f+1 prepare message to be able to move to the next phase*/
    @Override
    public void insertPrepareMessageInPool(ArrayList<IMessage> prepareMessages) {
        IMessage prepareMessage;
        for (int i = 0; i < prepareMessages.size(); i++) {
            prepareMessage = prepareMessages.get(i);

            if (this.state.equals("pre-prepare") && prepareMessage.getMessageType().equals("prepare") &&
                    prepareMessage.getPrimaryPublicKey() == this.primaryNodePublicKey &&
                    prepareMessage.getViewNum() == this.viewNum && prepareMessage.getSeqNum() == this.seqNum &&
                    prepareMessage.verifyPeerSignature() &&
                    prepareMessage.getBlock().getHeader().getHash().equals(this.block.getHeader().getHash()) &&
                    !this.preparePool.isMessageExists(prepareMessage)) {
                if (prepareMessage.getNodePublicKey() == this.primaryNodePublicKey) {
                    System.out.println("Error in prepare phase the primary sent a message");
                    this.newViewNum = this.viewNum + 1;
                    generateViewChangeMessage(this.newViewNum);
                }

                //check belal's function
                prepareMessage.getBlock().setTransactions(verifyBlockTransactions(prepareMessage.getBlock().getTransactions()));




                this.preparePool.insertMessage(prepareMessage);
            } else {
                System.out.println("Error with secondary node in prepare phase validation, the node will ignore this message");
            }
        }

        if (this.preparePool.getPoolSize() >= 2 * this.maxMaliciousNodes + 1) {
            this.state = "prepare";
            this.preparePool.clean();
        }

    }


    @Override
    public IMessagePool getCommitPool() {
        return this.commitPool;
    }

    /*All nodes including primary will broadcast a commit message all other nodes*/
    @Override
    public void generateCommitMessage() throws IOException {
        if (this.state.equals("prepare")) {
            IMessage commitMessage = new Message("commit", this.primaryNodePublicKey, this.seqNum, this.viewNum, this.nodeSignature, this.block, this.nodePublicKey);
            this.commitPool.insertMessage(commitMessage);
            broadcastMessage(commitMessage);
        } else {
            System.out.println("Node is out it won't generate a commit message as it doesn't finish her prepare phase");
        }
    }

    /*the node will validate the commit messages that are sent to here
     * and insert them in her commit pool if a message is invalid it will ignore it.
     * the node has to receive min 2*f+1 commit message to be able to move to the final phase*/
    @Override
    public void insertCommitMessageInPool(ArrayList<IMessage> commitMessages) {
        IMessage commitMessage;
        for (int i = 0; i < commitMessages.size(); i++) {
            commitMessage = commitMessages.get(i);

            if (this.state.equals("prepare") && commitMessage.getMessageType().equals("commit") &&
                    commitMessage.getPrimaryPublicKey() == this.primaryNodePublicKey &&
                    commitMessage.getSeqNum() == this.seqNum && commitMessage.getViewNum() == this.viewNum &&
                    commitMessage.verifyPeerSignature() &&
                    commitMessage.getBlock().getHeader().getHash().equals(this.block.getHeader().getHash()) &&
                    !commitPool.isMessageExists(commitMessage)) {

                this.commitPool.insertMessage(commitMessage);
            } else {
                System.out.println("Error with Node while validating this commit message the node will ignore it.");
            }
        }

        if (this.commitPool.getPoolSize() >= 2 * this.maxMaliciousNodes + 1) {
            this.state = "commit";
            this.commitPool.clean();
            addToChain(this.block);
        }

    }


    @Override
    public IMessagePool getChangeViewPool() {
        return this.viewChangePool;
    }

    /*the asks to change the view which means
     it asks to change the primary node as the primary node is malicious*/
    @Override
    public void generateViewChangeMessage(int newViewNum) throws IOException {
        IMessage changeViewMessage = new Message("change view", this.primaryNodePublicKey, this.seqNum, this.viewNum, this.nodeSignature, this.block, this.newViewNum, this.nodePublicKey);
        this.viewChangePool.insertMessage(changeViewMessage);

        broadcastMessage(changeViewMessage);
    }

    /*the node will validate the changeView messages that are sent to here
     * and insert them in her changeView pool if a message is invalid it will ignore it.
     * the node has to receive min 2*f+1 changeView message .
     * when the new primary receives his 2*f+1 changeView messages that refers to him to be the new primary
     * he will set him self as the new primary and broadcast that to all nodes*/
    @Override
    public void insertChangeViewMessageInPool(ArrayList<IMessage> changeViewMessages) {

        IMessage changeViewMessage;
        for (int i = 0; i < changeViewMessages.size(); i++) {
            changeViewMessage = changeViewMessages.get(i);

            if (changeViewMessage.getMessageType().equals("change view") && changeViewMessage.getPrimaryPublicKey() == this.primaryNodePublicKey &&
                    changeViewMessage.getViewNum() == this.viewNum && changeViewMessage.getSeqNum() == this.seqNum &&
                    changeViewMessage.verifyPeerSignature() &&
                    changeViewMessage.getBlock().getHeader().getHash().equals(this.block.getHeader().getHash()) &&
                    !this.viewChangePool.isMessageExists(changeViewMessage) &&
                    changeViewMessage.getNewViewNum() == this.newViewNum) {

                this.viewChangePool.insertMessage(changeViewMessage);

            } else {
                System.out.println("Error with node in view change phase verification, the node will ignore this message.");
            }
        }

        if (this.viewChangePool.getPoolSize() >= 2 * this.maxMaliciousNodes + 1) {
            this.prevState = this.state;
            this.state = "change view";
            if (this.nodePublicKey == this.network.getPrimaryID(this.newViewNum)) {
                this.primaryNodePublicKey = this.nodePublicKey;
                this.viewNum = this.newViewNum;
                generateViewChangedMessage();
            }
        }


    }


    /*the new primary will generate it to be broadcasted to all the network */
    @Override
    public void generateViewChangedMessage() throws IOException {
        IMessage viewChangedMessage = new Message("view changed", this.primaryNodePublicKey, this.seqNum, this.newViewNum, this.nodeSignature, this.block, this.viewChangePool);
        broadcastMessage(viewChangedMessage);
    }


    /*other nodes will verify that he is right and he is the new primary by checking his commit pool*/
    @Override
    public void checkTruthyOfNewView(IMessage viewChangedMessage) {
        if (viewChangedMessage.getMessageType().equals("view changed") && viewChangedMessage.getSeqNum() == this.seqNum &&
                viewChangedMessage.getViewNum() == this.viewNum && viewChangedMessage.verifyPeerSignature() &&
                viewChangedMessage.getBlock().getHeader().getHash().equals(this.block.getHeader().getHash()) &&
                verifyNewViewPool(viewChangedMessage.getMessagePool())) {
            this.primaryNodePublicKey = viewChangedMessage.getPrimaryPublicKey();
            this.viewNum = viewChangedMessage.getViewNum();
            this.state = this.prevState;
            System.out.println("View changed to be " + this.viewNum + " for the primary " + this.primaryNodePublicKey);
        } else {
            System.out.println("Error with secondary node while receiving the confirmation message for the new view so it will ignore it.");
        }
    }

    /*secondary nodes need to check that the new primary is telling the truth by verifying its commit pool*/
    @Override
    public boolean verifyNewViewPool(IMessagePool messagePool) {
        int counter = 0;
        IMessage[] pool = (IMessage[]) messagePool.getPool().toArray();
        for (int i = 0; i < pool.length; i++) {
            if (this.viewChangePool.isMessageExists(pool[i]))
                counter++;
        }
        if (counter >= 2 * this.maxMaliciousNodes + 1)
            return true;
        return false;
    }

    @Override
    public INode getPrimaryNode(int nodeIndex) {
        return null;
    }


    @Override
    public void broadcastMessage(IMessage message) throws IOException {
        this.network.broadcastMessage(message);
    }


    public ArrayList<ITransaction> createBlockTransactions() {
        return null;
    }

    public void createTransForBlock() {

    }


    @Override
    public void addTransaction(ArrayList<ITransaction> ledger) {
        this.ledger = ledger;
    }


    @Override
    public void receiveBlock(IBlock block) {
        this.block = block;
    }


    @Override
    public IBlock getLastBlock() {
        return chain.get(chain.size() - 1);
    }

    @Override
    public PublicKey getPrimaryId() {
        return this.primaryNodePublicKey;
    }

    @Override
    public void setPrimaryId(PublicKey primaryId) {
        this.primaryNodePublicKey = primaryId;
    }

    @Override
    public int getSeqNum() {
        return this.seqNum;
    }

    @Override
    public void setSeqNum() {
        this.seqNum = this.chain.size() + 1;
    }

    @Override
    public int getViewNum() {
        return this.viewNum;
    }

    @Override
    public void setViewNum(int viewNum) {
        this.viewNum = viewNum;
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
        this.maxMaliciousNodes = f;
    }

    @Override
    public int getMaxMaliciousNodes() {
        return this.maxMaliciousNodes;
    }

    @Override
    public PublicKey getNodePublicKey() {
        return this.nodePublicKey;
    }

}