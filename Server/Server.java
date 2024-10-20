
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server implements Auction{
    AuctionItem[] items = {
        new AuctionItem(0, "hairpin", "priceless, ancient heirloom", 650),
        new AuctionItem(1, "vase", "porcelain", 1040),
        new AuctionItem(2, "brooch", "princess", 900),
        
    };

    public Server() {
        super();
    }
  
    public AuctionItem getSpec(int itemID) {
        for (int i=0; i<items.length; i++) {
            AuctionItem item = items[i];
            if (itemID == item.itemID) {
                return item;
            }
        }
        return null;
    }
  
    public static void main(String[] args) {
        try {
            //creates server instance
            Server s = new Server();
            String name = "myserver";

            //export server object for remote access
            Auction stub = (Auction) UnicastRemoteObject.exportObject(s, 0);

            //get local RMI registry
            Registry registry = LocateRegistry.getRegistry();

            //binds server instance to RMI registry with name
            registry.rebind(name, stub);
            
            System.out.println("Server ready");
        } catch (Exception e) {
            System.err.println("Exception:");
            e.printStackTrace();
        }
    }
}