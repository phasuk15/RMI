
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;

import java.io.File; 
import java.io.FileOutputStream;
import java.io.IOException;


public class Server implements Auction{
    private SecretKey key;
    private static Cipher cipher;
    private AuctionItem[] items;
    
    public Server() {
        super();

        this.items = new AuctionItem[] {
            new AuctionItem(1, "hairpin", "priceless, ancient heirloom", 650),
            new AuctionItem(2, "vase", "porcelain", 1040),
            new AuctionItem(3, "brooch", "owned by princess", 900),
            new AuctionItem(4, "clip", "", 9950),
            new AuctionItem(5, "necklace", "princess", 780),
            new AuctionItem(6, "bracelet", "princess", 900),
            
        };
    }

    public static void generateKey() throws Exception, IllegalBlockSizeException, IOException {
        //generating a secret key from a random number         
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        SecretKey key = keyGenerator.generateKey();
        
        //initialising cipher for encryption
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        try (FileOutputStream sharedKeyFile = new FileOutputStream("../sharedKey.txt", false)) {
            sharedKeyFile.write(key.getEncoded());
        }
        
        System.out.println("Key successfully generated and saved. ");
        
    }        
    
    
    public SealedObject getSpec(int itemID) {
        for (int i=1; i<items.length; i++) {
            AuctionItem item = items[i];
            if (itemID == item.getItemID()) {
                try {
                    SealedObject sealedItem = new SealedObject(item, cipher);
                    System.out.println("Item has been sealed. ");
                    return sealedItem;
                } catch (IllegalBlockSizeException | IOException e) {
                    e.printStackTrace(); 
                }
            }
        }
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

            generateKey();
        } catch (Exception e) {
            System.err.println("Exception:");
            e.printStackTrace();
        }
    }
}