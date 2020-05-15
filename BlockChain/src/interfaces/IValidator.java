package interfaces;

import java.util.ArrayList;

public interface IValidator {

    public void initiateNewBlockMessage(IBlock prevBlock, ArrayList<ITransaction> transactions);

    /*stop adding transactions to the block*/
    public IMessage finalizeBlock();

}
