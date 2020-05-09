package concrete;
import interfaces.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Key;
import java.security.PublicKey;
import java.util.ArrayList;

public class Utils implements IUtils{
    private static Utils u = null;
    public static IUtils getInstance(){
        if (u == null ){
            u= new Utils();
        }
        return u;
    }
    @Override
    public  String applySha256(String input) {

        try {
            // Static getInstance method is called with hashing SHA
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // digest() method called
            // to calculate message digest of an input
            // and return array of byte
            byte[] hash =  md.digest(input.getBytes(StandardCharsets.UTF_8));

            // Convert byte array into signum representation
            BigInteger number = new BigInteger(1, hash);

            // Convert message digest into hex value
            StringBuilder hexString = new StringBuilder(number.toString(16));

            // Pad with leading zeros
            while (hexString.length() < 32)
            {
                hexString.insert(0, '0');
            }
            return hexString.toString();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public String getMerkleRoot(ArrayList<ITransaction> transactions) {
        ArrayList<String> list = new ArrayList<>();
        for (ITransaction t : transactions){
            list.add(t.hash());
        }

        return null;
    }
    private String treeLayer(ArrayList<String> list){
        if(list.size()==1){
            return list.get(0);
        }
        ArrayList<String> nlist = new ArrayList<>();
        for (int i =0 ; i < list.size();i+=2){
            String s1 = list.get(i);
            String s2;
            if (i==list.size()-1){
                s2 = list.get(i);
            }else {
                s2 = list.get(i + 1);
            }
            StringBuilder sb = new StringBuilder();
            sb.append(s1);
            sb.append("-");
            sb.append(s2);
            nlist.add( Utils.getInstance().applySha256(sb.toString()));
        }
        return treeLayer(nlist);
    }

    @Override
    public String getStringFromKey(Key key) {
        return null;
    }

    @Override
    public boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
        return false;
    }

    @Override
    public String getPublicKeyFromID(int id) {
        return String.valueOf(id);
    }
}
