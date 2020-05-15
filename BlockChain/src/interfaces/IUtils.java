package interfaces;

import jdk.internal.util.xml.impl.Pair;

import java.security.Key;
import java.security.PublicKey;
import java.util.ArrayList;

public interface IUtils {

    //Applies Sha256 to a string and returns the result.
    public String  applySha256(String input);

    public String getMerkleRoot(ArrayList<ITransaction> transactions);

    // This method returns encoded string from any key.
    public String getStringFromKey(Key key);


    //Verifies a String signature
    //This method takes in the signature, public key and string data and returns true or false if
    // the signature is valid.
    public boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature);
    public  String getPublicKeyFromID(int id);
    public void setID2PK(ArrayList<Pair> id2keys);
    public String TransactionsDatasetDir();
    }
