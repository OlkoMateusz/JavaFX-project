package com.simplebank.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/*
 * Klasa pomocnicza odpowiedzialna za bezpieczne przetwarzanie haseł.
 *
 * Hasła nie są nigdy przechowywane w postaci jawnej. Dla każdego hasła
 * generowana jest losowa sól, a w bazie zapisywany jest hash SHA-256 z
 * wartości (sól + hasło). Przy logowaniu hasło podane przez użytkownika
 * jest hashowane tą samą solą i porównywane z wartością z bazy.
 */
public final class PasswordUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {
    }

    /*
     * Generuje nową, losową sól zakodowaną w Base64.
     */
    public static String generateSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /*
     * Oblicza hash SHA-256 dla podanego hasła oraz soli (przekazanej w Base64).
     * Wynik jest zwracany jako ciąg zakodowany w Base64.
     */
    public static String hashPassword(String password, String saltBase64) {
        try {
            byte[] salt = Base64.getDecoder().decode(saltBase64);
            byte[] pwd = password.getBytes(StandardCharsets.UTF_8);

            byte[] combined = new byte[salt.length + pwd.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(pwd, 0, combined, salt.length, pwd.length);

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(combined);
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Brak algorytmu SHA-256", e);
        }
    }

    /*
     * Sprawdza, czy podane hasło odpowiada zapisanemu hashowi i soli.
     */
    public static boolean verifyPassword(String password, String saltBase64, String expectedHash) {
        String computed = hashPassword(password, saltBase64);
        return computed.equals(expectedHash);
    }
}
