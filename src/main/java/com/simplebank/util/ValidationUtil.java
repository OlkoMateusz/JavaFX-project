package com.simplebank.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/*
 * Klasa pomocnicza zawierająca metody walidacji danych wprowadzanych przez
 * użytkownika: poprawność adresu e-mail, formatu numeru PESEL, niepustych
 * pól tekstowych oraz dodatnich kwot pieniężnych.
 */
public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PESEL_PATTERN = Pattern.compile("^\\d{11}$");

    private ValidationUtil() {
    }

    public static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPesel(String pesel) {
        return pesel != null && PESEL_PATTERN.matcher(pesel.trim()).matches();
    }

    /*
     * Sprawdza, czy kwota jest liczbą dodatnią (większą od zera).
     */
    public static boolean isPositiveAmount(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }

    /*
     * Próbuje sparsować tekst na kwotę. Akceptuje przecinek jako separator
     * dziesiętny. Zwraca null, jeśli tekst nie jest poprawną liczbą.
     */
    public static BigDecimal parseAmount(String text) {
        if (!isNotBlank(text)) {
            return null;
        }
        try {
            return new BigDecimal(text.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
