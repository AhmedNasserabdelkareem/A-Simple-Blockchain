package interfaces;

import java.io.Serializable;

public interface IAnalyser {
    public class Analytics implements Serializable {
        public float avgNumOfMessExch4Block = 0; //pow 1 , bft 1

        public float totalTTM = 0;               //pow 3
        public int numberOfSuccessfulMines = 0;

        public int stales = 0;              //pow 2

        public int numberOfAgreedOnBlocks = 0;  //bft 2
        public float totalAgreeOnBlockTime = 0;
    }
    public void setType(int i);//0 for pow , 1 for bft
    public void setBlockSize(int size);
    public void setDifficulty(int diff);
    public void setNumOfParticipants(int num);



    public void reportMessageSent();
    public void reportBlockDone();

    public void reportStartingMining();
    public void reportEndingMiningSuccessfully();
    public void reportEndingMiningUnsuccessfully();


    public void reportStale();

    public void reportStartingBFTVoting();
    public void reportEndingBFTVoting();


    public void broadcastData(INTW ntw);
    public void receiveData(Analytics data);
    public boolean isDoneExchanging();
    public String saveReport();
    public String getReport();


    /*
    pow
    Block size, 200 TXs, 400TXs, 800TXs.,, Difficulty 3, 5, 10.
            – Average message complexity per block (Number of messages exchanged for a block/
            number of nodes).
            – Number of stale blocks (number of blocks found after the first block in a round (with
            a later timestamp)).
            – Average time to mine a block.


    bft
    number of participants
            – Message complexity per block (Number of messages exchanged for a block).
            – Average time to agree on a block.
    */



    //pow

    public float getAvgMessageComplexity();
    public float getNumberOfStaleBlocks();
    public float getAvgTimeToMine();

    //bft

    public float getMessageComplexity();
    public float getAvgTimeToAgreeOnBlock();









}
