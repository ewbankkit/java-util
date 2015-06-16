package com.github.ewbankkit.util;

import java.net.URLDecoder;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;

import com.netsol.adagent.util.CryptoUtil;
import com.thoughtworks.xstream.core.util.Base64Encoder;

public class CryptoUnitTest {



    @Test
    public void testEncryption() throws Exception{

        String key = "34W3Yvo9jr/7WsBDE+ql47DTjNQj6hI2GQj3HJOBdxYj8cFNIZOi2WdVhgEo"; //generate3DesKey();
        String params = getParamsString(new String[]{
                "cProdId", "42",
                "ms", System.currentTimeMillis()+"",
                "ip", "127.0.0.1"
        });

        System.out.println("Encrypting");
        String ecParam = encrypt(params, key);

        System.out.println("Encrypted Params: "+ecParam);
        System.out.println("Decrypting");


        Map<String, String> encryptionParams = new HashMap<String, String>();
        encryptionParams.put(CryptoUtil.KEY, key);
        encryptionParams.put(CryptoUtil.MODE, CryptoUtil.ECB);

        ecParam = URLDecoder.decode(ecParam, "UTF-8");

        String clearTxt = CryptoUtil.decrypt(CryptoUtil.DESede, encryptionParams, ecParam);

        System.out.println("Clear txt:  "+clearTxt);

        Assert.assertTrue("Decrypted String not equal to original String", params.equals(clearTxt));

    }




    public String generate3DesKey(){

        byte[] keybytes = new byte[45];
        new Random().nextBytes(keybytes);

        String key = new Base64Encoder().encode(keybytes);

        System.out.println("Secret key:  "+key);
        return key;
    }




    public String getParamsString(String[] params) throws Exception{
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<params.length; i+=2){

            sb. append(i==0 ? "" : "&").
                append(params[i]).
                append("=").
                append(URLEncoder.encode(params[i+1], "UTF-8"));

        }

        return sb.toString();
    }

    public static String encrypt(String clearTxt, String key)throws Exception{

        Map<String, String> encryptionParams = new HashMap<String, String>();
        encryptionParams.put(CryptoUtil.KEY, key);
        encryptionParams.put(CryptoUtil.MODE, CryptoUtil.ECB);



        String encryptedText = CryptoUtil.encrypt(CryptoUtil.DESede, encryptionParams, clearTxt);

        encryptedText = URLEncoder.encode(encryptedText, "UTF-8");

        return encryptedText;
    }

}
