/**
 * should have 2 concrete classes Pow and BFT
 */
public interface IAgreementMethod {
    public void mine(IBlock block ,int difficulty); //called after block verification
    public  int getNonce(); //return -1 BFT/ nonce pow   called while header creation
}
