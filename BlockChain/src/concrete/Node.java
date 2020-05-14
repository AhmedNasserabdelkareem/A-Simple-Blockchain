package concrete;
import interfaces.*;

import java.security.PublicKey;
import java.security.Signature;

import interfaces.*;

import java.security.PublicKey;

public class Node implements INode {
    @Override
    public void setConfigs(int maxNumTransactions, IAgreementMethod method, String[] IPsOfOtherPeers, int nodeType) {

    }

    @Override
    public void setNTW(INTW ntw) {

    }

    @Override
    public int getNodeType() {
        return 0;
    }

    @Override
    public void addTransaction(ITransaction t) {

    }

    @Override
    public void createBlock() {

    }

    private boolean verifyTransactionSign(ITransaction t){
        int signer = t.getIPs().get(0);
        byte[] signature = t.getSignedHash();
        try {
            Signature s = Signature.getInstance("SHA1WithRSA");
            s.initVerify(t.getPayerPK());
            s.update(t.hash().getBytes());
            boolean b = s.verify(t.getSignedHash());
        }catch (Exception e){
            e.printStackTrace();
        }
        return  true;
    }

    private boolean verifyTransactionVal(ITransaction t){
        int prevID = t.getPrevID();
        //ITransaction prev =
        return  true;
    }

    @Override
    public ITransaction getTransactionByID(int id) {
        return null;
    }

    @Override
    public boolean verifyTransaction(ITransaction t) {
        return verifyTransactionSign(t) && verifyTransactionVal(t);

    }

    @Override
    public void shareBlock(IBlock block, INTW ntw) {

    }

    @Override
    public void receiveBlock(IBlock block) {

    }

    @Override
    public void addToChain(IBlock block) {

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
}
