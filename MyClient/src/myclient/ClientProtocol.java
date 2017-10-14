/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myclient;

import static com.sun.org.apache.bcel.internal.classfile.Utility.toHexString;
import java.math.BigInteger;
import java.security.AlgorithmParameters;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;

import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;

import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;

/**
 *
 * @author A.Bagheri
 */
public class ClientProtocol {

    private KeyFactory clientKeyFac;
    private PublicKey serverPubKey;
    private KeyAgreement clientKeyAgr;
    private X509EncodedKeySpec x509KeySpec;
    public byte[] encodedParams;

    public byte[] mypublicbuilder(byte[] serverPubKeyEnc) throws Exception {
        // public byte[] mypublicbuilder(Object obj) throws Exception { 

        clientKeyFac = KeyFactory.getInstance("DH");
        x509KeySpec = new X509EncodedKeySpec(serverPubKeyEnc);
        serverPubKey = clientKeyFac.generatePublic(x509KeySpec);
       // PublicKey alicePubKey = KeyFactory.getInstance("DH").generatePublic
        //(new X509EncodedKeySpec(alicePubKeyEnc));
        //  PublicKey alicePubKey = bobKeyFac.generatePublic( alicePubKeyEnc);

        //Dwn here bob gets the parameter of alice or server pub key
        DHParameterSpec ParamSpecif = ((DHPublicKey) serverPubKey).getParams();
        BigInteger Base = ParamSpecif.getG();
        BigInteger Moduluse = ParamSpecif.getP();
        System.out.println("Base" + Base);
        System.out.println("Moduluse" + Moduluse);

        // Bob creates his dh key pair
        System.out.println("Wait While We Are Generating Key Pair");
        KeyPairGenerator clientKeyPairGen = KeyPairGenerator.getInstance("DH");
        clientKeyPairGen.initialize(ParamSpecif);
        //based on server public key and its specification, I build my clients public key pair
        KeyPair clientKeypari = clientKeyPairGen.generateKeyPair();

        // after getting encoded material,initializing dh,
        //getting the server params, and key pari generation .
        //then bob or client key agreement init,
        System.out.println("Client Initialization ...");
        clientKeyAgr = KeyAgreement.getInstance("DH");
        clientKeyAgr.init(clientKeypari.getPrivate());

        //I send my clien'ts pub key to my server or alice
        byte[] clientPubKeyEnc = clientKeypari.getPublic().getEncoded();
        System.out.println("CLient Pub String: " + Arrays.toString(clientPubKeyEnc));

        return clientPubKeyEnc;
    }

    public void secretKeyGenerator(int serverSharedKLength) throws Exception {
        System.out.println("BOB: Execute PHASE1 ...");
        clientKeyAgr.doPhase(serverPubKey, true);

        // here my client uses server secret key length to build a secret key with the same length and also to build his shared key
        byte[] clientSharedKey = new byte[serverSharedKLength];

        int clientSharedKeyLen;

        // provide output buffer of required size
        //Generates the shared secret, and places it into the buffer sharedSecret, beginning at offset inclusive.
        clientSharedKeyLen = clientKeyAgr.generateSecret(clientSharedKey, 0);
        System.out.println("Client SharedKey: "
                + toHexString(clientSharedKey));

    }

    public byte[] aesCBCEncod(String theCommond) throws Exception {

        clientKeyAgr.doPhase(serverPubKey, true);
        SecretKey clientAesKey = clientKeyAgr.generateSecret("DESede");

        Cipher clientCipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        clientCipher.init(Cipher.ENCRYPT_MODE, clientAesKey);

        byte[] cleartext = theCommond.getBytes();
        byte[] ciphertext = clientCipher.doFinal(cleartext);
        encodedParams = clientCipher.getParameters().getEncoded();
        return ciphertext;
        /*  if i want to specify IV my self
         SecretKeySpec k = new SecretKeySpec(key, "AES");
         c.init(Cipher.ENCRYPT_MODE, k);
         byte[] encryptedData = c.doFinal(dataToSend);
       
         byte[] iv = c.getIV();
         // send IV to Bob
         // send encryptedData to Bob
         byte[] iv = // read from stream sent by Alice
         Cipher c = Cipher.getInstance("AES/CBC/PKCS5PADDING");
         c.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(iv));
         */
    }
    public void aesCBCDecod(byte[] encodedParams, byte[] ciphertext) throws Exception {

        clientKeyAgr.doPhase(serverPubKey, true);
        SecretKey serverAesKey = clientKeyAgr.generateSecret("DESede"); // 256 bit
        AlgorithmParameters params = AlgorithmParameters.getInstance("DESede");
        params.init(encodedParams);
        Cipher serverCipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        serverCipher.init(Cipher.DECRYPT_MODE, serverAesKey, params);
        byte[] recovered = serverCipher.doFinal(ciphertext);

        System.out.println("Server Says:");
        System.out.println(new String(recovered, "UTF-8"));

    }

}
