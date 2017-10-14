/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myclient;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author A.Bagheri
 */
public class MyClient {

//private Socket socket;
    public static void main(String[] args) throws Exception {

        MyClient CLIENT = new MyClient();

        // MyClient CLIENT2 = new MyClient();
        CLIENT.readWriteBytes();

        // byte[] ClientPubKE = clientProtocol.mypublicbuilder(recievedbytes);
        //CLIENT.sendBytes(ClientPubKE);
        // TODO code application logic here
    }

    public void readWriteBytes() throws IOException, Exception {
        final ClientProtocol clientProtocol = new ClientProtocol();
        Socket socket = new Socket("localhost", 4444);

        InputStream in = socket.getInputStream();
        final DataInputStream dis = new DataInputStream(in);

        OutputStream out = socket.getOutputStream();
        final DataOutputStream dos = new DataOutputStream(out);

        int len = dis.readInt();
        byte[] data = new byte[len];
        
            dis.readFully(data);
            System.out.println("Recieved encpub of server:" + Arrays.toString(data));

            byte[] clientpubenc = clientProtocol.mypublicbuilder(data);

            int length = clientpubenc.length;
            dos.writeInt(length);
            final int start = 0;
            if (length > 0) {
                dos.write(clientpubenc, start, length);

            }
            int length2 = dis.readInt();  // shared secret length

            clientProtocol.secretKeyGenerator(length2);
            
            Thread threadRead = new Thread(new Runnable() {
        @Override
        public void run(){
            long startTime = System.currentTimeMillis();
            while (false || (System.currentTimeMillis() - startTime) < 240000) {
                  
                try {
                    int len2 = dis.readInt();
                    byte[] cbcParam = new byte[len2];
                    dis.readFully(cbcParam);
                    
                    int len3 = dis.readInt();
                    byte[] ciphertxt = new byte[len3];
                    dis.readFully(ciphertxt);
                    
                    try {
                        clientProtocol.aesCBCDecod(cbcParam, ciphertxt);
                    } catch (Exception ex) {
                        Logger.getLogger(MyClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(MyClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            }
            });
            
             Thread threadWrite = new Thread(new Runnable() {
        @Override
        public void run(){
            long startTime = System.currentTimeMillis();
            while (false || (System.currentTimeMillis() - startTime) < 240000) {
                  
                
                try {
                    System.out.println("Type what you want to...");
                    Scanner mycommond = new Scanner(System.in);
                    String Commond = mycommond.nextLine();
                    
                    byte[] ciphertext = clientProtocol.aesCBCEncod(Commond);
                    byte[] cbcParamet = clientProtocol.encodedParams;
                    
                    int length3 = cbcParamet.length;
                    dos.writeInt(length3);
                    dos.write(cbcParamet, start, length3);
                    
                    int length4 = ciphertext.length;
                    dos.writeInt(length4);
                    dos.write(ciphertext, start, length4);
                    dos.flush();
                    
                    //System.out.print(len);
                } catch (Exception ex) {
                    Logger.getLogger(MyClient.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
        }
    });
             threadRead.start();
             threadWrite.start();
        }
    }
        
             

/*
 public void sendBytes(byte[] myByteArray) throws IOException {
 sendBytes(myByteArray, 0, myByteArray.length);
 }

 public void sendBytes(byte[] myByteArray, int start, int len) throws IOException {
 Socket socket = new Socket("localhost", 4444);
 if (len < 0) {
 throw new IllegalArgumentException("Negative length not allowed");
 }
 if (start < 0 || start >= myByteArray.length) {
 throw new IndexOutOfBoundsException("Out of bounds: " + start);
 }
 // Other checks if needed.

 // May be better to save the streams in the support class;
 // just like the socket variable.
 OutputStream out = socket.getOutputStream();
 DataOutputStream dos = new DataOutputStream(out);

 dos.writeInt(len);
 if (len > 0) {
 dos.write(myByteArray, start, len);
 }
 }*/

/*
 public static byte[] serialize(Object obj) throws IOException {
 ByteArrayOutputStream out = new ByteArrayOutputStream();
 ObjectOutputStream os = new ObjectOutputStream(out);
 os.writeObject(obj);
 return out.toByteArray();
 }

 public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
 ByteArrayInputStream in = new ByteArrayInputStream(data);
 ObjectInputStream is = new ObjectInputStream(in);
 return is.readObject();
 }*/
/*
 public void run() throws Exception {

 //Socket SOCK = new Socket("localhost", 4444);
 // Socket socket = null;
 // DataOutputStream out = null;
 //DataInputStream in = null;
 //String host = "localhost";

 // socket = new Socket(host, 4444);
 }*/
/*
 ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
 Frame frame = new Frame();
 Frame frame = ois.readObject(); */

/*
 //____________base encoder and some stream______________________________ 
 InputStreamReader ISR = new InputStreamReader(socket.getInputStream());
 // BufferedReader BRR = new BufferedReader(ISR);
 String MSG = ISR.toString();
 BASE64Decoder decoder = new BASE64Decoder();
 byte[] sigBytes2 = decoder.decodeBuffer(MSG);
 ClientProtocol encPubBob = new ClientProtocol();
 encPubBob.mypublicbuilder(sigBytes2);
 */ //_____________________________________________________________________
        /*  out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
 in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
 int length = in.readInt();                    // read length of incoming message
 if (length > 0) {
 byte[] message = new byte[length];
 in.readFully(message, 0, message.length);
        
 */
        // PrintStream PS = new PrintStream(SOCK.getOutputStream());
// PS.println("Hi, Lets stay safe");
// InputStreamReader ISR = new InputStreamReader(SOCK.getInputStream());
// BufferedReader BRR = new BufferedReader(ISR);
// ObjectOutputStream oos = new ObjectOutputStream(SOCK.getOutputStream());
        /*DataInputStream dIn = new DataInputStream(SOCK.getInputStream());

 int length = dIn.readInt();                    // read length of incoming message
 if (length > 0) {
 byte[] message = new byte[length];
 dIn.readFully(message, 0, message.length); // read the message
 }

 ClientProtocol encPubBob;
 byte[] receivingPubKeyEnc; */

/*
 try (ObjectInputStream ois = new ObjectInputStream(SOCK.getInputStream())) {
 encPubBob = new ClientProtocol();
 receivingPubKeyEnc = serialize(ois.readObject());
 }*/
        // System.out.println(Arrays.toString(receivingPubKeyEnc));
//byte[] test = encPubBob.mypublicbuilder(receivingPubKeyEnc);
//  System.out.println(Arrays.toString(test));
//String MSG = BRR.readLine();
        /*
 if (MSG!=null){
 System.out.println(MSG);
 ClientProtocol encPubBob = new ClientProtocol();
 /* BASE64Decoder decoder = new BASE64Decoder();
 byte[] sigBytes2 = decoder.decodeBuffer(MSG);
 byte[] commingPubKeyEnc = MSG.getBytes();
           
 //PS.println(Arrays.toString(publicKeyEnc.run()));
 String Display = Arrays.toString(encPubBob.mypublicbuilder(commingPubKeyEnc));
 PS.println(Display);
 System.out.println(Display);

 */
        // bob or my client gets the encoded material and initialize his DH

