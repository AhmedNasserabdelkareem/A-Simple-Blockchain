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

    }
}
