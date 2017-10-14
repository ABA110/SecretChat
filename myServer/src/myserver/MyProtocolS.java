/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myserver;


import java.math.BigInteger;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import static com.sun.org.apache.bcel.internal.classfile.Utility.toHexString;
import java.util.Arrays;

/**
 *
 * @author A.Bagheri
 */
   //   DHKeyAgreement for 2
public class MyProtocolS {

    private KeyPairGenerator serverKPairGen;
    private KeyPair serverKPair;
    private KeyFactory serverKeyFac;
    private KeyAgreement serverKeyAgree;
    private PublicKey clientPubKey;
    public byte[] encodedParams;

    /*
     private MyProtocolS() {}

     public static void DHstart() throws Exception {
    

     MyProtocolS keyAgree = new MyProtocolS();

           

     keyAgree.run();
     
     }  */
    public byte[] publicKeybuilder() throws Exception {
        // public Object run() throws Exception {
        // public void run() throws Exception {

        System.out.println("Creating DH parameters.....");

        //  DHGenParameterSpec dhgenparam = new DHGenParameterSpec(int 1024,int1024);
       
        AlgorithmParameterGenerator paramGen = AlgorithmParameterGenerator.getInstance("DH");

        //  SecureRandom random = SecureRandom.getInstance("DH", "SUNJCE");
        //  keyGen.initialize(2048, random);
        paramGen.init(1024); // my modulus size
        AlgorithmParameters params = paramGen.generateParameters();

        DHParameterSpec dhSkipParamSpec = (DHParameterSpec) params.getParameterSpec(DHParameterSpec.class);
        BigInteger Base = dhSkipParamSpec.getG();
        BigInteger Moduluse = dhSkipParamSpec.getP();
        System.out.println("Base & Moduluse are tested.");
        //   dhSkipParamSpec = new DHParameterSpec(skip1024Modulus,skip1024Base);
        // Down Here I am trying to create Key Pair for server or ALICE
        System.out.println("Server is Generating DH keypair ...");
        serverKPairGen = KeyPairGenerator.getInstance("DH");
        serverKPairGen.initialize(dhSkipParamSpec);
        serverKPair = serverKPairGen.generateKeyPair();

        // initial key agreement
        System.out.println("Calculating  ");
        serverKeyAgree = KeyAgreement.getInstance("DH");
        serverKeyAgree.init(serverKPair.getPrivate());

        //encode(Hide) the public key then ready tp send
        // Object pubKeyObj = serverKPair.getPublic();
        /////////////////////////////////////////////////////////////////////////////////////      
       /* BigInteger bigintalicpub = getBigInteger(pubKeyObj);
         System.out.println( bigintalicpub);
         */
        //////////////////////////////////////////////////////////////////////////////////////
        byte[] serverPubKeyEnc = serverKPair.getPublic().getEncoded();
        System.out.println("serverPubKeyEnc:" + Arrays.toString(serverPubKeyEnc));
        // server secret key 

        String s = serverKPair.getPublic().getFormat();
        System.out.println("Encoding:" + s);

      //  DHPublicKeySpec1 = (DHPublicKeySpec) params.getParameterSpec
        //(DHPublicKeySpec.class);    i TRYED THIS LINE TO SEE IF i CAM INSTANCIATE DHPUBLICkEYsPEC.GET(Y)
        // return alicePubKeyEnc Base Moduluse;
        return serverPubKeyEnc;

    }

    public int secretKeyGenerator(byte[] clientPubKeyEnc) throws Exception {

        serverKeyFac = KeyFactory.getInstance("DH");
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(clientPubKeyEnc);
        clientPubKey = serverKeyFac.generatePublic(x509KeySpec);
        System.out.println("Server Building Intermediat Key From Client Spec ...");
        //the (intermediate) key resulting from this phase, or null if this phase does not yield a key
        serverKeyAgree.doPhase(clientPubKey, true);

        byte[] serverSharedKey = serverKeyAgree.generateSecret();
        System.out.println("Recieved encpub of client:" + Arrays.toString(serverSharedKey));
        System.out.println("Server SharedKey: "
                + toHexString(serverSharedKey));
        int serverSharedKLength = serverSharedKey.length;
        return serverSharedKLength;

    }

    public void aesCBCDecod(byte[] encodedParams, byte[] ciphertext) throws Exception {

        serverKeyAgree.doPhase(clientPubKey, true);
        SecretKey serverAesKey = serverKeyAgree.generateSecret("DESede"); // 256 bit
        //KeyAgree.generateSecret above reset the key
        // agreement object, so we call doPhase again prior to another
        // generateSecret call
        AlgorithmParameters params = AlgorithmParameters.getInstance("DESede");
        params.init(encodedParams);
        Cipher serverCipher = Cipher.getInstance("DESede/CBC/PKCS5Padding"); // pkcs size is 8 byte block
        serverCipher.init(Cipher.DECRYPT_MODE, serverAesKey, params);
        byte[] recovered = serverCipher.doFinal(ciphertext);

        System.out.println("Client Says:");
        System.out.println(new String(recovered, "UTF-8"));

    }

    public byte[] aesCBCEncod(String theCommond) throws Exception {

        serverKeyAgree.doPhase(clientPubKey, true);
        SecretKey clientAesKey = serverKeyAgree.generateSecret("DESede");

        Cipher clientCipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        clientCipher.init(Cipher.ENCRYPT_MODE, clientAesKey);

        byte[] cleartext = theCommond.getBytes();
        byte[] ciphertext = clientCipher.doFinal(cleartext);
        encodedParams = clientCipher.getParameters().getEncoded();
        return ciphertext;
    }
    /*
     public static BigInteger getBigInteger(Object value) {
     BigInteger ret = null;
     if ( value != null ) {
     if ( value instanceof BigInteger ) {
     ret = (BigInteger) value;
     } else if ( value instanceof String ) {
     ret = new BigInteger( (String) value );
     } else if ( value instanceof BigDecimal ) {
     ret = ((BigDecimal) value).toBigInteger();
     } else if ( value instanceof Number ) {
     ret = BigInteger.valueOf( ((Number) value).longValue() );
     } else {
     throw new ClassCastException( "Not possible to coerce [" + value + "] from class " + value.getClass() + " into a BigInteger." );
     }
     }
     return ret;
     } */
}
