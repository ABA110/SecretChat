/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myserver;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import sun.misc.BASE64Encoder;
import java.io.Serializable;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author A.Bagheri
 */
public class MyServer {

    //public Socket socket;
    public static void main(String[] args) throws Exception {
        // TODO code application logic here

        MyServer SERVER = new MyServer();
        SERVER.readWriteBytes();
        //SERVER.run();

    }
    /*
     public void run() throws Exception {
     //ServerSocket SERVSOCK = new ServerSocket(4444);
     //Socket clientSocket = SERVSOCK.accept();

     //MyProtocolS protocolObject = new MyProtocolS();
     }*/

    public void readWriteBytes() throws IOException, Exception {

        final MyProtocolS protocolObject = new MyProtocolS();
        byte[] serverpubenc = protocolObject.publicKeybuilder();

        ServerSocket SERVSOCK = new ServerSocket(4444);
        Socket clientSocket = SERVSOCK.accept();

        OutputStream out = clientSocket.getOutputStream();
        final DataOutputStream dos = new DataOutputStream(out);
        InputStream in = clientSocket.getInputStream();
        final DataInputStream dis = new DataInputStream(in);

        int length = serverpubenc.length;
        dos.writeInt(length);
        final int start = 0;
        if (length > 0) {
            dos.write(serverpubenc, start, length);
            int len = dis.readInt();
            byte[] clientPubKeyEnc = new byte[len];
            if (len > 0) {
                dis.readFully(clientPubKeyEnc);
                System.out.println("Recieved encpub of client:" + Arrays.toString(clientPubKeyEnc));
                protocolObject.secretKeyGenerator(clientPubKeyEnc);
            }

            //sends server secret key length
            dos.writeInt(protocolObject.secretKeyGenerator(clientPubKeyEnc));
        }

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
                        protocolObject.aesCBCDecod(cbcParam, ciphertxt);
                    } catch (Exception ex) {
                        Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex);
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
                    
                    byte[] ciphertext = protocolObject.aesCBCEncod(Commond);
                    byte[] cbcParamet = protocolObject.encodedParams;
                    
                    int length3 = cbcParamet.length;
                    dos.writeInt(length3);
                    dos.write(cbcParamet, start, length3);
                    
                    int length4 = ciphertext.length;
                    dos.writeInt(length4);
                    dos.write(ciphertext, start, length4);
                    dos.flush();
                    
                    //System.out.print(len);
                } catch (Exception ex) {
                    Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex);
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
 ServerSocket SERVSOCK = new ServerSocket(4444);
 Socket clientSocket = SERVSOCK.accept();
 if (len < 0) {
 throw new IllegalArgumentException("Negative length not allowed");
 }
 if (start < 0 || start >= myByteArray.length) {
 throw new IndexOutOfBoundsException("Out of bounds: " + start);
 }
 // Other checks if needed.

 // May be better to save the streams in the support class;
 // just like the socket variable.
 OutputStream out = clientSocket.getOutputStream();
 DataOutputStream dos = new DataOutputStream(out);

 dos.writeInt(len);
 if (len > 0) {
 dos.write(myByteArray, start, len);
 }
 }  */
/*
 public byte[] readBytes() throws IOException {

 ServerSocket SERVSOCK = new ServerSocket(4444);
 Socket clientSocket = SERVSOCK.accept();
 InputStream in = clientSocket.getInputStream();
 DataInputStream dis = new DataInputStream(in);

 int len = dis.readInt();
 byte[] clientPubKeyEnc = new byte[len];
 if (len > 0) {
 dis.readFully(clientPubKeyEnc);
 System.out.println(Arrays.toString(clientPubKeyEnc));
 }
 return clientPubKeyEnc;
 }*/

/*  //_________using frame and obect stream serialized________________
 ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
       
 Frame frame = new Frame();
 frame.clientPubKeyEnc = protocolObject.run();
 oos.writeObject(frame);*/
        //_______________________________________________________
// clientPubKeyEnc stream via base decoder, encoder
       /* // DataOutputStream dOut = new DataOutputStream(clientSocket.getOutputStream());
 PrintStream PS = new PrintStream(clientSocket.getOutputStream());
 BASE64Encoder encoder = new BASE64Encoder();
 String pubKeyStr = encoder.encode(protocolObject.run());
 PS.println(pubKeyStr);
 */
       // byte[] message = protocolObject.run();
// dOut.writeInt(message.length); // write length of the message
// dOut.write(message);
// Object sendingObject = deserialize(protocolObject.run());
// ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
       /*
 try (ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream())) {
 oos.writeObject(protocolObject.run());
 oos.close();
 }*/
                             //   Object obj = obIn.readObject();
//   otherPublicKey = (PublicKey) obj;
        /*
 InputStreamReader ISR = new InputStreamReader(clientSocket.getInputStream());
 BufferedReader BRR = new BufferedReader(ISR);

 String MSG = BRR.readLine();
 System.out.println(MSG);
 PrintStream PS = new PrintStream(clientSocket.getOutputStream());
 */
/*
 if (MSG!=null){
           
           
 //  PrintStream PS = new PrintStream(clientSocket.getOutputStream());
 MyProtocolS protocolObject = new MyProtocolS();
           
 //PS.println(Arrays.toString(protocolObject.run()));
         
 //  BASE64Encoder encoder = new BASE64Encoder();
 //  String  string = encoder.encode(protocolObject.run() );
 String Display = Arrays.toString(protocolObject.run());
 PS.println(Display);
 System.out.println(Display);
          
           
           
 } */
        /////////////////////////////////////  Testing Hex to string
         /*
 MyProtocolS protocolObject = new MyProtocolS();
 byte[] test = protocolObject.run();
        
 PS.println(toHexString(test));
 }
    
 private void byte2hex(byte b, StringBuffer buf) {
 char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
 '9', 'A', 'B', 'C', 'D', 'E', 'F' };
 int high = ((b & 0xf0) >> 4);
 int low = (b & 0x0f);
 buf.append(hexChars[high]);
 buf.append(hexChars[low]);
 }
 private String toHexString(byte[] block) {
 StringBuffer buf = new StringBuffer();

 int len = block.length;

 for (int i = 0; i < len; i++) {
 byte2hex(block[i], buf);
 if (i < len-1) {
 buf.append(":");
 }
 }
 return buf.toString();
 } */
/*
 public static byte[] serialize(Object obj) throws IOException {
 ByteArrayOutputStream out = new ByteArrayOutputStream();
 ObjectOutputStream os = new ObjectOutputStream(out);
 os.writeObject(obj);
 return out.toByteArray();
 }

 public static Object deserialize(byte[] clientPubKeyEnc) throws IOException, ClassNotFoundException {
 ByteArrayInputStream in = new ByteArrayInputStream(clientPubKeyEnc);
 ObjectInputStream is = new ObjectInputStream(in);
 return is.readObject();
 }

 } */
