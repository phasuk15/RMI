import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client{
     public static void main(String[] args) {
       if (args.length < 1) {
            System.out.println("Usage: java Client n");
            return;
        }

        int n = Integer.parseInt(args[0]);
        try {
            String name = "myserver";
            Registry registry = LocateRegistry.getRegistry("localhost");
            Auction server = (Auction) registry.lookup(name);

            AuctionItem spec = server.getSpec(n);
            System.out.println("Item name: " + spec.name + " Description: " + spec.description + " Highest bid: " + spec.highestBid);
        }
        catch (Exception e) {
            System.err.println("Exception:");
            e.printStackTrace();
        }
      }
}