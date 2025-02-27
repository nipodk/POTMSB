package com.poweroftwo.potms_backend.access_key.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class KeyHasherImpl implements KeyHasher {
    @Value("${secretes.aes}")
    private String AES_SECRETE;
    @Override
    public String encrypt(String data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        final Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, generateSecrete());
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
    }

    @Override
    public String decrypt(String encryptedData) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, generateSecrete());
        return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedData)));
    }

    private SecretKeySpec generateSecrete() {
        final byte[] decodedKey = Base64.getDecoder().decode(AES_SECRETE);
        return new SecretKeySpec(decodedKey, "AES");
    }
}
