package com.workflowos.integration.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class TokenVaultService {
    private static final int IV_BYTES = 12;
    private static final int TAG_BITS = 128;
    private final SecureRandom secureRandom = new SecureRandom();
    private final SecretKeySpec key;

    public TokenVaultService(@Value("${workflowos.token-vault.key:dev-only-32-byte-token-vault-key!!}") String rawKey) {
        byte[] bytes = rawKey.getBytes(StandardCharsets.UTF_8);
        byte[] normalized = new byte[32];
        System.arraycopy(bytes, 0, normalized, 0, Math.min(bytes.length, normalized.length));
        this.key = new SecretKeySpec(normalized, "AES");
    }

    public String encrypt(String plaintext) {
        if (plaintext == null) return null;
        try {
            byte[] iv = new byte[IV_BYTES];
            secureRandom.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));
            byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(iv) + "." + Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to encrypt token", e);
        }
    }

    public String decrypt(String ciphertext) {
        if (ciphertext == null) return null;
        try {
            String[] parts = ciphertext.split("\\.", 2);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, Base64.getDecoder().decode(parts[0])));
            return new String(cipher.doFinal(Base64.getDecoder().decode(parts[1])), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to decrypt token", e);
        }
    }
}
