/**
 * Copyright (C) Network Solutions, LLC.
 */

// ------------------------------------------------------------------
// Package
// ------------------------------------------------------------------
package com.netsol.adagent.util;

// ------------------------------------------------------------------
// Import
// ------------------------------------------------------------------
import java.io.IOException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * ----------------Copyright (c) 2004 Network Solutions, LLC. --------
 *
 * This is the crypto util class. It encapsulates the DES or Triple DES encryption and decryption
 * process through the JCE crypto suite.
 *
 *
 * <br>
 * <br>
 *
 * @author Jin Kuang
 * @since 10-19-2004
 * @version 1.0 -------------------------------------------------------------------
 */
public class CryptoUtil
{

    /**
     * SCCS control ID
     */
    static final String sccsID = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:40 CryptoUtil.java NSI";

    // we only handle two most popular modes.
    // The other stream cipher modes are not implemented.
    public static final String CBC = "CBC";
    public static final String ECB = "ECB";

    // two encryption/descryption methods are supported
    public static final String DES = "DES";
    public static final String DESede = "DESede";

    // the padding scheme
    public static String PADDINGSCHEME = "paddingScheme";
    public static String defaultPaddingScheme = "PKCS5Padding";

    public static String KEY = "key";
    public static String MODE = "mode";
    public static String IV = "iv";

    // Sha1
    public static final String SHA = "SHA";
    public static final String STRING_ENCODING_SCHEME_8859_1 = "8859_1";

    /**
     * Used for conversion from hex digits to bytes.
     * <p>
     */
    private static String hexDigits = "0123456789ABCDEF";

    /**
     * Used for conversion from bytes to hex digits.
     * <p>
     */
    private static char[] hexChars = hexDigits.toCharArray();

    /**
     * Convert a byte array (for example array = {0x05, 0x23, 0x9A, 0x3E, 0xA7, 0x8F, 0x05, 0xDA}
     * into string representation "05239A3EA78F05DA"
     *
     * @param inputBytes
     * @return
     */
    public static String bytesToHexString(byte[] inputBytes)
    {
        int length = inputBytes.length;

        StringBuffer sb = new StringBuffer(length << 1); // Twice the size
        for (int i = 0; i < length; i++) {
            int nextByte = (int) inputBytes[i];
            if (0 > nextByte) nextByte += 256; // Move -128..-1 -> 128..255

            sb.append(hexChars[nextByte >> 4]) // High nibble
            .append(hexChars[nextByte & 0x0f]); // Low nibble
        }
        return (sb.toString());

    }

    /**
     * Conver a string representation of a byte array into the byte array construct
     *
     * @param inputString
     * @return
     * @throws NumberFormatException
     */
    public static byte[] hexStringToBytes(String inputString) throws NumberFormatException
    {
        int length = inputString.length();
        if (1 == (length & 0x01)) // Odd length
        {
            inputString = "0" + inputString;
            ++length;
        }

        byte[] hexBytes = new byte[length >> 1]; // Half the size
        for (int i = 0; i < length; i += 2) {
            int highNibble = byteValue(inputString.charAt(i));
            int lowNibble = byteValue(inputString.charAt(i + 1));

            hexBytes[i >> 1] = (byte) ((highNibble << 4) + lowNibble);
        }
        return (hexBytes);

    }

    /**
     * Give a character in the range of 0-F (the range of a 4 bits, half of a byte)
     *
     * @param c
     * @return
     * @throws NumberFormatException
     */
    private static byte byteValue(char c) throws NumberFormatException
    {
        switch (c) {
            case '0':
                return (byte) 0;
            case '1':
                return (byte) 1;
            case '2':
                return (byte) 2;
            case '3':
                return (byte) 3;
            case '4':
                return (byte) 4;
            case '5':
                return (byte) 5;
            case '6':
                return (byte) 6;
            case '7':
                return (byte) 7;
            case '8':
                return (byte) 8;
            case '9':
                return (byte) 9;
            default:
                switch (c) {
                    case 'A':
                        return (byte) 0xA;
                    case 'B':
                        return (byte) 0xB;
                    case 'C':
                        return (byte) 0xC;
                    case 'D':
                        return (byte) 0xD;
                    case 'E':
                        return (byte) 0xE;
                    case 'F':
                        return (byte) 0xF;
                    default:
                        throw new NumberFormatException("'" + c + "' is an invalid hex digit.");
                }
        }
    }

    /**
     * This method encodes the raw bytes into base64 encoded string
     *
     * @param encoded
     * @return
     */
    public static String base64Encode(byte[] bytes, boolean removeLineBreak)
    {
        BASE64Encoder base64Encoder = new BASE64Encoder();

        // unfortunately, by RFC definition, each line in
        // BASE64 encoded string can not be longer than
        // 76 characters. Most BASE64 encoder implementation
        // automatically insert a new line before or right after
        // 76 characters. You can remove this restriction by
        // specifying the removeLineBreak to be true.
        String encodedStrWithNewLine = base64Encoder.encode(bytes);

        if (removeLineBreak && encodedStrWithNewLine != null) {

            StringBuffer strBuf = new StringBuffer();
            char ch;

            for (int i = 0; i < encodedStrWithNewLine.length(); i++) {
                ch = encodedStrWithNewLine.charAt(i);
                if (ch != '\n') {
                    strBuf.append(ch);
                }
            }

            return strBuf.toString();

        } else {
            return encodedStrWithNewLine;
        }

    }

    /**
     * This method decode the base64 encoded string back to raw bytes.
     *
     * @param encoded
     * @return
     */
    public static byte[] base64Decode(String encoded)
    {
        BASE64Decoder base64Decoder = new BASE64Decoder();
        try {
            return base64Decoder.decodeBuffer(encoded);
        }
        catch (IOException ioe) {
            return null;
        }
    }

    /**
     * This method expects a crypto method, a mode, a Hashmap of supporting params for the method,
     * and the clear text that will be encrypted.
     *
     * For the metohd, it should be either DES or DESede (we currently only suppor these two crypto
     * methods
     *
     * For the params, it must contain the name "mode" and its associated value. The value is either
     * CBC or EBC.
     *
     * For the params, it must contain the name "key" and its associated key value in Base64 encoded
     * form.
     *
     * For the params, if the mode is CBC, it must also contain the name "iv" (as for initialization
     * vector) and its associated value in Base64 encoded form.
     *
     * The clear text is the string that will be encrypted.
     *
     * The return string is the cipher text that is Base64 encoded.
     *
     * @param method
     * @param params
     * @param clearText
     * @return
     */
    public static String encrypt(String method, Map<String, String> params, String clearText)
    {

        String key = null;
        String mode = null;
        String paddingScheme = null;
        String iv = null;

        if (method == null || params == null || clearText == null) {
            return null;
        }

        if (params.containsKey(KEY)) {
            key = (String) params.get(KEY);
        }

        if (params.containsKey(MODE)) {
            mode = (String) params.get(MODE);
        }

        if (key == null || mode == null) {
            return null;
        }

        // note that iv CAN BE NULL if the mode is ECB so
        // we don't do sanity check for it.
        if (params.containsKey(IV)) {
            iv = (String) params.get(IV);
        }

        if (params.containsKey(PADDINGSCHEME)) {
            paddingScheme = (String) params.get(PADDINGSCHEME);
        }

        if (paddingScheme == null) {
            // set to default
            paddingScheme = defaultPaddingScheme;
        }

        if (method.equalsIgnoreCase(DES)) {
            return desEncrypt(mode, paddingScheme, key, iv, clearText);
        } else if (method.equalsIgnoreCase(DESede)) {
            return desedeEncrypt(mode, paddingScheme, key, iv, clearText);
        } else {
            // mode not supported
            return null;
        }
    }

    /**
     * This method expects a crypto method, a mode, a Hashmap of supporting params for the method,
     * and the cipher text (encoded in Base64) that will be decrypted.
     *
     * For the metohd, it should be either DES or DESede (we currently only suppor these two crypto
     * methods
     *
     * For the params, it must contain the name "mode" and its associated value. The value is either
     * CBC or EBC.
     *
     * For the params, it must contain the name "key" and its associated value in Base64 encoded
     * form.
     *
     * For the params, if the mode is CBC, it must also contain the name "iv" (as for initialization
     * vector) and its associated value in Base64 encoded form.
     *
     * The cipher text is Base64 encoded.
     *
     * The return String is the clear text.
     *
     * @param method
     * @param params
     * @param clearText
     * @return
     */
    public static String decrypt(String method, Map<String, String> params, String cipherText)
    {
        String key = null;
        String mode = null;
        String paddingScheme = null;
        String iv = null;

        if (method == null || params == null || cipherText == null) {
            return null;
        }

        if (params.containsKey(KEY)) {
            key = (String) params.get(KEY);
        }

        if (params.containsKey(MODE)) {
            mode = (String) params.get(MODE);
        }

        if (key == null || mode == null) {
            return null;
        }

        // note that iv CAN BE NULL if the mode is ECB so
        // we don't do sanity check for it.
        if (params.containsKey(IV)) {
            iv = (String) params.get(IV);
        }

        if (params.containsKey(PADDINGSCHEME)) {
            paddingScheme = (String) params.get(PADDINGSCHEME);
        }

        if (paddingScheme == null) {
            // set to default
            paddingScheme = defaultPaddingScheme;
        }

        if (method.equalsIgnoreCase(DES)) {
            return desDecrypt(mode, paddingScheme, key, iv, cipherText);
        } else if (method.equalsIgnoreCase(DESede)) {
            return desedeDecrypt(mode, paddingScheme, key, iv, cipherText);
        } else {
            // mode not supported
            return null;
        }
    }

    /**
     * This method generates either the DES or Triple DES
     *
     * method: "DES" or "DESede"
     *
     * @param method
     * @return
     */
    public static String genDESSecretKey(String method)
    {
        // sanity check
        if (method == null || method.length() == 0
                || (!method.equalsIgnoreCase(DES) && !method.equalsIgnoreCase(DESede))) {
            return null;
        }

        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(DESede);
            SecretKey key = keyGen.generateKey();

            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DESede);
            DESedeKeySpec keySpec = (DESedeKeySpec) keyFactory.getKeySpec(key, DESedeKeySpec.class);

            byte[] keyInByte = keySpec.getKey();

            // convert the raw bytes into text format
            String keyInString = base64Encode(keyInByte, true);

            return keyInString;

        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * generate the specified byte size secured random key and have it base 64 encoded
     *
     * @param byteSize
     * @return
     */
    public static String genSecureRandomKeyWithBase64Encode(int byteSize)
    {
        byte[] bytes = genSecureRandomKey(byteSize);

        // convert the bytes into base64 encoded string
        BASE64Encoder base64Encoder = new BASE64Encoder();
        String encodedStr = base64Encoder.encode(bytes);
        return encodedStr;
    }

    /**
     * generate the specified byte size secured random key
     *
     * @param byteSize
     * @return
     */
    public static byte[] genSecureRandomKey(int byteSize)
    {
        SecureRandom sr = new SecureRandom();
        byte[] bytes = new byte[byteSize];
        sr.nextBytes(bytes);
        return bytes;
    }

    /**
     * If no secret key is specified, set as null. If the secret key is not null and not empty, it
     * is appended to the clear text.
     *
     * @param clearText
     * @param secretKey
     * @return
     */
    public static String genShaMessageDigestWithBase64Encoding(String clearText, String secretKey)
    {
        String fullText = null;
        if (!((secretKey == null) || (secretKey.length() < 1))) {
            fullText = clearText + secretKey;
        } else {
            fullText = clearText;
        }

        byte[] bytes = genShaMessageDigest(fullText);

        if (bytes == null) {
            return null;
        }

        BASE64Encoder base64Encoder = new BASE64Encoder();
        String encodedStr = base64Encoder.encode(bytes);
        return encodedStr;
    }

    /**
     * Sha-1 message digest
     *
     * @param clearText
     * @return
     */
    public static byte[] genShaMessageDigest(String clearText)
    {
        byte[] digest = null;

        try {
            byte[] theTextToDigestAsBytes = clearText.getBytes(STRING_ENCODING_SCHEME_8859_1 /* encoding */);

            MessageDigest md = MessageDigest.getInstance(SHA);
            md.update(theTextToDigestAsBytes);
            digest = md.digest();
        }
        catch (Exception e) {
            // just cover all
            return null;
        }

        return digest;
    }

    private static String desEncrypt(String mode, String paddingScheme, String key, String iv, String clearText)
    {
        // sanity check
        if (mode == null || paddingScheme == null || key == null || clearText == null) {
            return null;
        }

        if (mode.equalsIgnoreCase(CBC) && iv == null) {
            // iv must be specified for CBC mode operation
            return null;
        }

        byte[] cipherTextInBytes = null;
        String cipherText = null;

        // conver the key into bytes
        byte[] keyInBytes = base64Decode(key);

        try {
            // load the bytes into a des key spec
            DESKeySpec desKeySpec = new DESKeySpec(keyInBytes);
            // from the key spec translate into a secret key
            // using the secret key factory
            SecretKeyFactory desKeyFactory = SecretKeyFactory.getInstance(DES);
            SecretKey desKey = desKeyFactory.generateSecret(desKeySpec);

            // create a DES cipher with the mode and a padding specified by the
            // paddingScheme attribute
            String desCipherString = DES + "/" + mode + "/" + paddingScheme;

            Cipher cipher = Cipher.getInstance(desCipherString);

            // if is CBC, we need to set up the IV to init
            if (mode.equalsIgnoreCase(CBC)) {
                byte[] ivInBytes = base64Decode(iv);
                IvParameterSpec spec = new IvParameterSpec(ivInBytes);
                cipher.init(Cipher.ENCRYPT_MODE, desKey, spec);
            } else if (mode.equalsIgnoreCase(ECB)) {
                cipher.init(Cipher.ENCRYPT_MODE, desKey);
            } else {
                // not supported
                return null;
            }

            cipherTextInBytes = cipher.doFinal(clearText.getBytes());
            cipherText = base64Encode(cipherTextInBytes, true);
            return cipherText;

        }
        catch (Exception e) {
            return null;
        }
        catch (Throwable t) {
            return null;
        }
    }

    private static String desedeEncrypt(String mode, String paddingScheme, String key, String iv, String clearText)
    {
        // sanity check
        if (mode == null || paddingScheme == null || key == null || clearText == null) {
            return null;
        }

        if (mode.equalsIgnoreCase(CBC) && iv == null) {
            // iv must be specified for CBC mode operation
            return null;
        }

        byte[] cipherTextInBytes = null;
        String cipherText = null;

        // conver the key into bytes
        byte[] keyInBytes = base64Decode(key);

        try {
            // load the bytes into a des key spec
            DESedeKeySpec desedeKeySpec = new DESedeKeySpec(keyInBytes);
            // from the key spec translate into a secret key
            // using the secret key factory
            SecretKeyFactory desedeKeyFactory = SecretKeyFactory.getInstance(DESede);
            SecretKey desedeKey = desedeKeyFactory.generateSecret(desedeKeySpec);

            // create a DESede cipher with the mode and a padding specified by the
            // paddingScheme attribute
            String desedeCipherString = DESede + "/" + mode + "/" + paddingScheme;

            Cipher cipher = Cipher.getInstance(desedeCipherString);

            // if is CBC, we need to set up the IV to init
            if (mode.equalsIgnoreCase(CBC)) {
                byte[] ivInBytes = base64Decode(iv);
                IvParameterSpec spec = new IvParameterSpec(ivInBytes);
                cipher.init(Cipher.ENCRYPT_MODE, desedeKey, spec);
            } else if (mode.equalsIgnoreCase(ECB)) {
                cipher.init(Cipher.ENCRYPT_MODE, desedeKey);
            } else {
                // not supported
                return null;
            }

            cipherTextInBytes = cipher.doFinal(clearText.getBytes("UTF-8"));
            cipherText = base64Encode(cipherTextInBytes, true);
            return cipherText;

        }
        catch (Exception e) {
            return null;
        }
        catch (Throwable t) {
            return null;
        }
    }

    private static String desDecrypt(String mode, String paddingScheme, String key, String iv, String cipherText)
    {
        // sanity check
        if (mode == null || paddingScheme == null || key == null || cipherText == null) {
            return null;
        }

        if (mode.equalsIgnoreCase(CBC) && iv == null) {
            // iv must be specified for CBC mode operation
            return null;
        }

        byte[] cipherTextInBytes = null;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            cipherTextInBytes = decoder.decodeBuffer(cipherText);
        }
        catch (IOException e1) {
            return null;
        }

        byte[] clearTextInBytes = null;
        String clearText = null;

        // conver the key into bytes
        byte[] keyInBytes = base64Decode(key);

        try {
            // load the bytes into a des key spec
            DESKeySpec desKeySpec = new DESKeySpec(keyInBytes);
            // from the key spec translate into a secret key
            // using the secret key factory
            SecretKeyFactory desKeyFactory = SecretKeyFactory.getInstance(DES);
            SecretKey desKey = desKeyFactory.generateSecret(desKeySpec);

            // create a DES cipher with the mode and a padding specified by the
            // paddingScheme attribute
            String desCipherString = DES + "/" + mode + "/" + paddingScheme;

            Cipher cipher = Cipher.getInstance(desCipherString);

            // if is CBC, we need to set up the IV to init
            if (mode.equalsIgnoreCase(CBC)) {
                byte[] ivInBytes = base64Decode(iv);
                IvParameterSpec spec = new IvParameterSpec(ivInBytes);
                cipher.init(Cipher.DECRYPT_MODE, desKey, spec);
            } else if (mode.equalsIgnoreCase(ECB)) {
                cipher.init(Cipher.DECRYPT_MODE, desKey);
            } else {
                // not supported
                return null;
            }

            clearTextInBytes = cipher.doFinal(cipherTextInBytes);
            clearText = new String(clearTextInBytes);
            return clearText;

        }
        catch (Exception e) {
            return null;
        }
        catch (Throwable t) {
            return null;
        }
    }

    private static String desedeDecrypt(String mode, String paddingScheme, String key, String iv, String cipherText)
    {
        // sanity check
        if (mode == null || paddingScheme == null || key == null || cipherText == null) {
            return null;
        }

        if (mode.equalsIgnoreCase(CBC) && iv == null) {
            // iv must be specified for CBC mode operation
            return null;
        }

        byte[] cipherTextInBytes = null;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            cipherTextInBytes = decoder.decodeBuffer(cipherText);
        }
        catch (IOException e1) {
            return null;
        }
        byte[] clearTextInBytes = null;
        String clearText = null;

        // conver the key into bytes
        byte[] keyInBytes = base64Decode(key);

        try {
            // load the bytes into a des key spec
            DESedeKeySpec desedeKeySpec = new DESedeKeySpec(keyInBytes);
            // from the key spec translate into a secret key
            // using the secret key factory
            SecretKeyFactory desedeKeyFactory = SecretKeyFactory.getInstance(DESede);
            SecretKey desedeKey = desedeKeyFactory.generateSecret(desedeKeySpec);

            // create a DESede cipher with the mode and a padding specified by the
            // paddingScheme attribute
            String desedeCipherString = DESede + "/" + mode + "/" + paddingScheme;

            Cipher cipher = Cipher.getInstance(desedeCipherString);

            // if is CBC, we need to set up the IV to init
            if (mode.equalsIgnoreCase(CBC)) {
                byte[] ivInBytes = base64Decode(iv);
                IvParameterSpec spec = new IvParameterSpec(ivInBytes);
                cipher.init(Cipher.DECRYPT_MODE, desedeKey, spec);
            } else if (mode.equalsIgnoreCase(ECB)) {
                cipher.init(Cipher.DECRYPT_MODE, desedeKey);
            } else {
                // not supported
                return null;
            }

            clearTextInBytes = cipher.doFinal(cipherTextInBytes);
            clearText = new String(clearTextInBytes);
            return clearText;

        }
        catch (Exception e) {
            return null;
        }
        catch (Throwable t) {
            return null;
        }
    }
}
