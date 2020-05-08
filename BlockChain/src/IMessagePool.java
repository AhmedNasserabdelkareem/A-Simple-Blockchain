public interface IMessagePool {

    public void addMessage(IMessage message);

    public boolean verifyMessage(IMessage message);

    public boolean isMessageExists(IMessage message);

    public void cleanPool();

    public int getPoolSize();
}
