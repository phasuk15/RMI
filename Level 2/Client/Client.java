import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.crypto.Cipher;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Client{

    public static AuctionItem unsealItem(SealedObject sealedObject, SecretKey key) throws Exception {
        //initialising cipher to decrypt
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);

        System.out.println("Item successfully unsealed. ");
        return (AuctionItem) sealedObject.getObject(cipher);
    }

    public static SecretKey loadKey() throws FileNotFoundException, IOException {
        //getting key
        File sharedKeyFile = new File("../sharedKey.txt");
        byte[] keyBytes = new byte[(int) sharedKeyFile.length()];
        try (FileInputStream fis = new FileInputStream(sharedKeyFile)) {
            fis.read(keyBytes);
        }

        return new SecretKeySpec(keyBytes, "AES");
    }


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
            
            SealedObject sealedItem = server.getSpec(n);
            SecretKey key = loadKey();
            AuctionItem item = unsealItem(sealedItem, key);

            System.out.println("Item name: " + item.getName() + " Description: " + item.getDescription() + " Highest bid: " + item.getHighestBid());
        }
        catch (Exception e) {
            System.err.println("Exception:");
            e.printStackTrace();
        }
      }
}