package concrete;
import interfaces.*;
import jdk.internal.util.xml.impl.Pair;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Key;
import java.security.PublicKey;
import java.util.ArrayList;

public class Utils implements IUtils{
    private static String dataDir="/TxDataset_v2/txdataset_v2";
    private static Utils u = null;
    private ArrayList<Pair> id2keys;

    public static IUtils getInstance(){
        if (u == null ){
            u= new Utils();
        }
        return u;
    }

    @Override
    public String TransactionsDatasetDir() {
        return dataDir;
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
        for (Pair p : this.id2keys){
            if(p.id==id){
                return  p.value;
            }
        }
        System.out.println("this id has no public key..");
        return null;
    }

    @Override
    public void setID2PK(ArrayList<Pair> id2keys) {
        this.id2keys = id2keys;
    }
}
