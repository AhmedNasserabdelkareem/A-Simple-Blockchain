package interfaces;

import java.security.PublicKey;

public interface IMessage {


    public boolean verifyMessage(IMessage message);

    public IBlock getBlock();

    public void setBlock(IBlock block);

    public String getMessageType();

    public void setMessageType(String type);

    public PublicKey getPrimaryNode();

    public void setPrimaryNode(PublicKey primaryNodeKey);

    public int getSeqNum();

    public void setSeqNum(int seqNum);

    public int getViewNum();

    public void setViewNum(int viewNum);

    public byte[] getClientSignature();

    public void setClientSignature(byte[] clientSignature);


}
