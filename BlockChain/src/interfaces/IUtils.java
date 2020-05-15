package interfaces;

import java.security.Key;
import java.security.PublicKey;
import java.util.ArrayList;

public interface IUtils {


    // This method returns encoded string from any key.


    //Verifies a String signature
    //This method takes in the signature, public key and string data and returns true or false if
    // the signature is valid.
    //public boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature);
    public  String getPublicKeyFromID(int id);
    public String TransactionsDatasetDir();
    }
