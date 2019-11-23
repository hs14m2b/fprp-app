package click.mr_b.fprp_app;


import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

class PlanEncryptionHandler {

    private Cipher encCipher, decCipher;
    private static final int STATUS_ENCRYPTED = 1;
    private static final int STATUS_DECRYPTED = 0;
    private static final int ivSize = 16;
    private KeyStore keyStore;
    private SecretKey encKey, decKey;
    private SecureRandom random;

    PlanEncryptionHandler()
    {
        try {
            generateKey();
            random = new SecureRandom();
            initCipher();
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            Log.d("PlanEncryptionHandler", "loaded keystore");
            encKey = (SecretKey) keyStore.getKey(MainActivity.KEY_NAME,null);
            Log.d("PlanEncryptionHandler", "got secret key for encryption");
            decKey = (SecretKey) keyStore.getKey(MainActivity.KEY_NAME,null);
            Log.d("authenticationSucceeded", "got secret key for decryption");
            //cipher.init(Cipher.DECRYPT_MODE, key,params);
            //Log.d("authenticationSucceeded", "initialised cipher for decryption");
        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException e) {
            Log.e("authenticationSucceeded", "Failed to get Cipher " + e.getMessage());
            throw new RuntimeException("Failed to init Cipher", e);
        }
        catch (Exception e)
        {
            Log.e("authenticationSucceeded", "Failed to initialise key " + e.getMessage());
            throw new RuntimeException("Failed to initialise key", e);
        }
    }

    Plan encrypt(Plan plan)
    {
        if (plan.getEncstatus() == STATUS_ENCRYPTED)
        {
            return plan;
        }
        else
        {
            Log.d("PlanEncryptionHandler", "created iv for encryption");
            Plan encPlan = new Plan();
            try {
                Log.d("PlanEncryptionHandler", "initialised cipher for encryption");
                encPlan.setId(plan.getId());
                encPlan.setEncstatus(STATUS_ENCRYPTED);
                encPlan.setPlanName(encString(plan.getPlanName()));
                encPlan.setPoint1(encString(plan.getPoint1()));
                encPlan.setPoint2(encString(plan.getPoint2()));
                encPlan.setPoint3(encString(plan.getPoint3()));
                encPlan.setPoint4(encString(plan.getPoint4()));
                encPlan.setPoint5(encString(plan.getPoint5()));
                encPlan.setQuestion1(encString(plan.getQuestion1()));
                encPlan.setQuestion2(encString(plan.getQuestion2()));
                encPlan.setQuestion3(encString(plan.getQuestion3()));
                encPlan.setQuestion4(encString(plan.getQuestion4()));
                encPlan.setQuestion5(encString(plan.getQuestion5()));
                encPlan.setQuestion6(encString(plan.getQuestion6()));
            } catch (Exception e) {
                Log.e("PlanEncryptionHandler", "Caught Error encrypting plan " + e.getMessage());
                e.printStackTrace();
            }
            return encPlan;
        }
    }

    Plan decrypt(Plan plan)
    {
        if (plan.getEncstatus() == STATUS_DECRYPTED)
        {
            return plan;
        }
        else
        {
            Plan decPlan = new Plan();
            decPlan.setId(plan.getId());
            decPlan.setEncstatus(STATUS_DECRYPTED);
            decPlan.setPlanName(decString(plan.getPlanName()));
            decPlan.setPoint1(decString(plan.getPoint1()));
            decPlan.setPoint2(decString(plan.getPoint2()));
            decPlan.setPoint3(decString(plan.getPoint3()));
            decPlan.setPoint4(decString(plan.getPoint4()));
            decPlan.setPoint5(decString(plan.getPoint5()));
            decPlan.setQuestion1(decString(plan.getQuestion1()));
            decPlan.setQuestion2(decString(plan.getQuestion2()));
            decPlan.setQuestion3(decString(plan.getQuestion3()));
            decPlan.setQuestion4(decString(plan.getQuestion4()));
            decPlan.setQuestion5(decString(plan.getQuestion5()));
            decPlan.setQuestion6(decString(plan.getQuestion6()));
            return decPlan;
        }
    }

    private String encString(String plainText)
    {
        if (plainText.equals("")) return "";
        try
        {
            // Generating IV.
            byte[] iv = new byte[ivSize];
            random.nextBytes(iv);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            encCipher.init(Cipher.ENCRYPT_MODE, encKey,ivParameterSpec);
            byte[] encrypted = encCipher.doFinal(plainText.getBytes());
            Log.d("PEH - encString", "encrypted bytes");
            // Combine IV and encrypted part.
            byte[] encryptedIVAndText = new byte[ivSize + encrypted.length];
            System.arraycopy(iv, 0, encryptedIVAndText, 0, ivSize);
            System.arraycopy(encrypted, 0, encryptedIVAndText, ivSize, encrypted.length);
            Log.d("PEH - encString", "combined iv and encrypted bytes");
            String encoded = Base64.encodeToString(encryptedIVAndText, Base64.NO_WRAP);
            Log.d("PEH - encString", "created base64 encoded value of encrypted iv + bytes" + encoded);
            return encoded;
        }
        catch (Exception ex)
        {
            Log.e("PEH - encString", "Caught error encrypting string " + ex.getMessage());
            return "";
        }
    }

    private String decString(String encText)
    {
        try
        {
            byte[] encryptedIvTextBytes = Base64.decode(encText, Base64.NO_WRAP);
            Log.d("PEH - decString", "base64 decoded encrypted string");
            // Extract IV.
            byte[] iv = new byte[ivSize];
            System.arraycopy(encryptedIvTextBytes, 0, iv, 0, iv.length);
            Log.d("PEH - decString", "extracted iv from encrypted byte array");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            // Extract encrypted part.
            int encryptedSize = encryptedIvTextBytes.length - ivSize;
            byte[] encryptedBytes = new byte[encryptedSize];
            System.arraycopy(encryptedIvTextBytes, ivSize, encryptedBytes, 0, encryptedSize);
            Log.d("PEH - decString", "extracted encrypted bytes from encrypted byte array");
            decCipher.init(Cipher.DECRYPT_MODE, decKey, ivParameterSpec);
            Log.d("PEH - decString", "initialised cipher for decryption");

            String decoded = new String(decCipher.doFinal(encryptedBytes));
            Log.d("PEH - decString", "successfully decoded data " + decoded);
            return decoded;
        }
        catch (Exception ex)
        {
            return "";
        }
    }

    private void initCipher() {
        try {
            //Obtain a cipher instance and configure it with the properties required for fingerprint authentication//
            encCipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            decCipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            Log.e("initCipher", "Failed to get Cipher " + e.getMessage());
            throw new RuntimeException("Failed to get Cipher", e);
        }

    }

    //Create the generateKey method that we’ll use to gain access to the Android keystore and generate the encryption key//
    private void generateKey() throws Exception {
        try {
            // Obtain a reference to the Keystore using the standard Android keystore container identifier (“AndroidKeystore”)//
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            if (!keyStore.containsAlias(MainActivity.KEY_NAME)) {
                Log.d("generateKey", "Key not found in keystore so generate new key");
                //Generate the key//
                KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
                //keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");

                //Initialize the KeyGenerator//
                keyGenerator.init(new

                        //Specify the operation(s) this key can be used for//
                        KeyGenParameterSpec.Builder(MainActivity.KEY_NAME,
                        KeyProperties.PURPOSE_ENCRYPT |
                                KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        //Set randomized encryption required to false to enable iv to be specified
                        .setRandomizedEncryptionRequired(false)
                        //Configure this key so that the user has to confirm their identity with a fingerprint each time they want to use it//
                        //.setUserAuthenticationRequired(true)
                        .setEncryptionPaddings(
                                KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .build());

                //Generate the key//
                keyGenerator.generateKey();
            }
        } catch (KeyStoreException
                | NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException
                | CertificateException
                | IOException exc) {
            exc.printStackTrace();
            throw new Exception(exc);
        }
    }

}
