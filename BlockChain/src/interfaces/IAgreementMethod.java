package interfaces;

import concrete.Node;

import java.util.ArrayList;

/**
 * should have 2 concrete classes Pow and BFT
 */
public interface IAgreementMethod {
    public void pow(IBlock block ,int difficulty); //called after block verification
//    public  int getNonce(); //return -1 BFT/ nonce pow   called while header creation

    public void pbft(ArrayList<Node> nodes); //practical Byzantine fault tolerance

    public int roundRobin(int noOfNodes);
}
