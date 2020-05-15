import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class test {
    public static void main(String[] args) throws UnknownHostException {
        InetAddress a = InetAddress.getByName("127.0.0.1");
        InetAddress b = InetAddress.getByName("127.0.0.2");
        InetAddress c = InetAddress.getByName("127.1.0.1");
        InetAddress d = InetAddress.getByName("41.0.3.1");
        InetAddress e = InetAddress.getByName("8888.0.0.1");
        ArrayList<InetAddress> addresses  =new ArrayList<>();
        addresses.add(a);addresses.add(b);addresses.add(c);addresses.add(d);addresses.add(e);
        //Collections.sort(addresses);

    }
}
