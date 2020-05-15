package concrete;

import interfaces.IAgreementMethod;
import interfaces.IBlock;
import interfaces.INTW;
import interfaces.INode;

import java.io.Serializable;
import java.util.ArrayList;

public class AgreementMethod implements IAgreementMethod, Serializable {
    private int viewNum = 0;
    private INTW network;
    private INode primaryNode;
    private ArrayList<Node> nodes;
    private int maxMaliciousNodes;

    @Override
    public int roundRobin(int noOfNodes) {
        this.viewNum++;
        return viewNum % noOfNodes;//index of primary node in network
    }

    @Override
    //apply POW (mining) verification by Increasing nonce value until hash target is reached.
    public void pow(IBlock block, int difficulty) {
        int nonce = 0;
        String hash = block.getHeader().getHash();
        String merkleRoot = Utils.getMerkleRoot(block.getTransactions());
        block.getHeader().setTransactionsHash(merkleRoot);
        String target = Utils.getDificultyString(difficulty); //Create a string with difficulty * "0"
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            block.getHeader().setNonce(nonce);
            hash = block.getHeader().calculateHash();
        }
        block.getHeader().setHash(hash);
        block.getHeader().setNonce(nonce);
    }

    @Override
    public void pbft(ArrayList<Node> nodes) {

        this.nodes = nodes;
        this.maxMaliciousNodes = (nodes.size()-1)/3;
        INode currentNode;
        this.primaryNode = this.network.getPrimaryNode(roundRobin(this.nodes.size()));
        for (int i =0 ;i<nodes.size();i++){
            currentNode = nodes.get(i);
            currentNode.generateKeyPair();
            currentNode.setPrimaryId(this.primaryNode.getPrimaryId());
            currentNode.setMaxMaliciousNodes(this.maxMaliciousNodes);
        }


        /*initialize new block then broadcast it*/
        this.primaryNode.generateNewBlockMessage();

        /*primary will generate the pre-prepare message and broadcast it to all other nodes*/
        this.primaryNode.generatePreprepareMessage();

        /*secondary nodes will receive pre-prepare message from the primary*/
        //nasser

        /*prepare phase*/
        for (int i =0;i<nodes.size();i++){
            currentNode = nodes.get(i);
            if(currentNode.getNodePublicKey()!=this.primaryNode.getNodePublicKey()){
                currentNode.generatePrepareMessage();
            }
        }

        /*Secondary nodes will receive prepare messages from each other*/
        //nasser

        /*Commit phase*/
        for (int i =0;i<nodes.size();i++){
            currentNode = nodes.get(i);
            currentNode.generateCommitMessage();

        }


        /*All nodes will receive commit messages from each other*/
        //nasser

    }
}
