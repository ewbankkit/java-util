/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ewbankkit.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Session generator.
 */
public class SessionGenerator {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:28 SessionGenerator.java NSI";

    public static final SessionGenerator INSTANCE = new SessionGenerator();

    protected Log log = LogFactory.getLog(SessionGenerator.class);

    protected DataInputStream randomIS=null;
    protected String devRandomSource="/dev/urandom";

    protected int sessionIdLength = 32;

    /**
     * The default message digest algorithm to use if we cannot use
     * the requested one.
     */
    protected static final String DEFAULT_ALGORITHM = "MD5";


    /**
     * The message digest algorithm to be used when generating session
     * identifiers.  This must be an algorithm supported by the
     * <code>java.security.MessageDigest</code> class on your platform.
     */
    protected String algorithm = DEFAULT_ALGORITHM;


    /**
     * Return the MessageDigest implementation to be used when
     * creating session identifiers.
     */
    protected MessageDigest digest = null;


    /**
     * A String initialization parameter used to increase the entropy of
     * the initialization of our random number generator.
     */
    protected String entropy = null;


    /**
     * The descriptive information string for this implementation.
     */
    private static final String info = "SessionGenerator/1.0";


    /**
     * The descriptive name of this Manager implementation (for logging).
     */
    protected static String name = "SessionGenerator";


    /**
     * A random number generator to use when generating session identifiers.
     */
    protected Random random = null;


    /**
     * The Java class name of the random number generator class to be used
     * when generating session identifiers.
     */
    protected String randomClass = "java.security.SecureRandom";

    protected boolean initialized=false;

    // ------------------------------------------------------------- Properties

    /**
     * Return the message digest algorithm for this Manager.
     */
    public String getAlgorithm() {

        return (this.algorithm);

    }


    /**
     * Set the message digest algorithm for this Manager.
     *
     * @param algorithm The new message digest algorithm
     */
    public void setAlgorithm(String algorithm) {

        this.algorithm = algorithm;

    }


    /** Returns the name of the implementation class.
     */
    public String getClassName() {
        return this.getClass().getName();
    }


    /**
     * Return the MessageDigest object to be used for calculating
     * session identifiers.  If none has been created yet, initialize
     * one the first time this method is called.
     */
    public synchronized MessageDigest getDigest() {

        if (this.digest == null) {
            long t1=System.currentTimeMillis();
            if (log.isDebugEnabled())
                log.debug(name + " getting instance of " + algorithm);
            try {
                this.digest = MessageDigest.getInstance(algorithm);
            } catch (NoSuchAlgorithmException e) {
                log.error(name + " failed getting instance of " + algorithm, e);
                try {
                    this.digest = MessageDigest.getInstance(DEFAULT_ALGORITHM);
                } catch (NoSuchAlgorithmException f) {
                    log.error(name + " failed getting instance of " +
                                     DEFAULT_ALGORITHM, e);
                    this.digest = null;
                }
            }
            long t2=System.currentTimeMillis();
            if( log.isDebugEnabled() )
                log.debug("getDigest() " + (t2-t1));
        }

        return (this.digest);

    }


    /**
     * Return the entropy increaser value, or compute a semi-useful value
     * if this String has not yet been set.
     */
    public String getEntropy() {

        // Calculate a semi-useful value if this has not been set
        if (this.entropy == null) {
            setEntropy(this.toString());
        }

        return (this.entropy);

    }


    /**
     * Set the entropy increaser value.
     *
     * @param entropy The new entropy increaser value
     */
    public void setEntropy(String entropy) {

        this.entropy = entropy;

    }


    /**
     * Return descriptive information about this Manager implementation and
     * the corresponding version number, in the format
     * <code>&lt;description&gt;/&lt;version&gt;</code>.
     */
    public String getInfo() {

        return (info);

    }


    /**
     * Gets the session id length (in bytes) of Sessions created by
     * this Manager.
     *
     * @return The session id length
     */
    public int getSessionIdLength() {

        return (this.sessionIdLength);

    }


    /**
     * Sets the session id length (in bytes) for Sessions created by this
     * Manager.
     *
     * @param idLength The session id length
     */
    public void setSessionIdLength(int idLength) {

        this.sessionIdLength = idLength;

    }


    /**
     * Return the descriptive short name of this Manager implementation.
     */
    public String getName() {

        return (name);

    }

    /**
     * Use /dev/random-type special device. This is new code, but may reduce
     * the big delay in generating the random.
     *
     *  You must specify a path to a random generator file. Use /dev/urandom
     *  for linux ( or similar ) systems. Use /dev/random for maximum security
     *  ( it may block if not enough "random" exist ). You can also use
     *  a pipe that generates random.
     *
     *  The code will check if the file exists, and default to java Random
     *  if not found. There is a significant performance difference, very
     *  visible on the first call to getSession ( like in the first JSP )
     *  - so use it if available.
     */
    public void setRandomFile( String s ) {
            try{
                devRandomSource=s;
                File f=new File( devRandomSource );
                if( ! f.exists() ) return;
                randomIS= new DataInputStream( new FileInputStream(f));
                randomIS.readLong();
                if( log.isDebugEnabled() )
                    log.debug( "Opening " + devRandomSource );
            } catch( IOException ex ) {
                try {
                    randomIS.close();
                } catch (Exception e) {
                    log.warn("Failed to close randomIS.");
                }

                randomIS=null;
            }
    }

    public String getRandomFile() {
        return devRandomSource;
    }


    /**
     * Return the random number generator instance we should use for
     * generating session identifiers.  If there is no such generator
     * currently defined, construct and seed a new one.
     */
    public Random getRandom() {
        if (this.random == null) {
            // Calculate the new random number generator seed
            long seed = System.currentTimeMillis();
            char entropy[] = getEntropy().toCharArray();
            for (int i = 0; i < entropy.length; i++) {
                long update = ((byte) entropy[i]) << ((i % 8) * 8);
                seed ^= update;
            }
            try {
                // Construct and seed a new random number generator
                Class<?> clazz = Class.forName(randomClass);
                this.random = (Random) clazz.newInstance();
                this.random.setSeed(seed);
            } catch (Exception e) {
                // Fall back to the simple case
                log.error(name + " unable to load randomizer: " + randomClass + ", reverting to java.util.Random", e);
                this.random = new java.util.Random();
                this.random.setSeed(seed);
            }
        }

        return (this.random);

    }


    /**
     * Return the random number generator class name.
     */
    public String getRandomClass() {

        return (this.randomClass);

    }


    /**
     * Set the random number generator class name.
     *
     * @param randomClass The new random number generator class name
     */
    public void setRandomClass(String randomClass) {

        this.randomClass = randomClass;

    }


    // --------------------------------------------------------- Public Methods


    public void init() {
        if( initialized ) return;
        initialized=true;

        log = LogFactory.getLog(SessionGenerator.class);

        // Initialize random number generation
        getRandomBytes(new byte[16]);

    }

    protected void getRandomBytes(byte bytes[]) {
        // Generate a byte array containing a session identifier
        if (devRandomSource != null && randomIS == null) {
            setRandomFile(devRandomSource);
        }
        if (randomIS != null) {
            try {
                int len = randomIS.read(bytes);
                if (len == bytes.length) {
                    return;
                }
                if(log.isDebugEnabled())
                    log.debug("Got " + len + " " + bytes.length );
            } catch (Exception ex) {
                // Ignore
            }
            devRandomSource = null;

            try {
                randomIS.close();
            } catch (Exception e) {
                log.warn("Failed to close randomIS.");
            }

            randomIS = null;
        }
        getRandom().nextBytes(bytes);
    }


    /**
     * Generate and return a new session identifier.
     */
    public synchronized String generateSessionId() {

        byte random[] = new byte[16];
        String result = null;

        // Render the result as a String of hexadecimal digits
        StringBuilder buffer = new StringBuilder();
        int resultLenBytes = 0;

        while (resultLenBytes < this.sessionIdLength) {
                getRandomBytes(random);
                random = getDigest().digest(random);
                for (int j = 0; j < random.length && resultLenBytes < this.sessionIdLength; j++) {
                    byte b1 = (byte) ((random[j] & 0xf0) >> 4);
                    byte b2 = (byte) (random[j] & 0x0f);
                    if (b1 < 10)
                        buffer.append((char) ('0' + b1));
                    else
                        buffer.append((char) ('A' + (b1 - 10)));
                    if (b2 < 10)
                        buffer.append((char) ('0' + b2));
                    else
                        buffer.append((char) ('A' + (b2 - 10)));
                    resultLenBytes++;
                }
        }
        result = buffer.toString();
        return (result);
    }

    /**
     * Constructor.
     */
    private SessionGenerator() {
        return;
    }

    public static void main(String[] argv) {
        try {
            int loops = 10;
            SessionGenerator sg = new SessionGenerator();
            for (int i = 0; i < loops; i++) {

                // generate a (hopefully) unique session ID
                String sid = sg.generateSessionId();
                System.out.println(sid);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
