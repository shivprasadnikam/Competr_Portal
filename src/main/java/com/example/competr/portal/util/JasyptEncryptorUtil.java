package com.example.competr.portal.util;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

/**
 * Utility class to encrypt sensitive properties for application.properties
 * Run this class to generate encrypted values for your passwords
 */
public class JasyptEncryptorUtil {

    public static void main(String[] args) {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        
        // Use the same configuration as JasyptConfig
        config.setPassword("competr-default-key-change-in-production");
        config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64");
        
        encryptor.setConfig(config);
        
        // Encrypt sensitive values
        String dbPassword = "jWxe1uygpS88g8asesgcjz4swSW7YFU0";
        String jwtSecret = "1234566225261";
        
        System.out.println("=== Encrypted Values ===");
        System.out.println("Database Password: ENC(" + encryptor.encrypt(dbPassword) + ")");
        System.out.println("JWT Secret: ENC(" + encryptor.encrypt(jwtSecret) + ")");
        
        // Test decryption
        String encryptedDbPassword = encryptor.encrypt(dbPassword);
        String decryptedDbPassword = encryptor.decrypt(encryptedDbPassword);
        System.out.println("\n=== Verification ===");
        System.out.println("Original DB Password: " + dbPassword);
        System.out.println("Decrypted DB Password: " + decryptedDbPassword);
        System.out.println("Match: " + dbPassword.equals(decryptedDbPassword));
    }
}
