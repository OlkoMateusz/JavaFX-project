package com.simplebank.service;

import com.simplebank.dao.BankAccountDAO;
import com.simplebank.dao.UserDAO;
import com.simplebank.model.BankAccount;
import com.simplebank.model.User;
import com.simplebank.util.PasswordUtil;
import com.simplebank.util.ValidationUtil;

import java.math.BigDecimal;
import java.security.SecureRandom;

/*
 * Serwis odpowiedzialny za uwierzytelnianie i rejestrację użytkowników.
 *
 * Podczas logowania pobiera użytkownika z bazy i porównuje podane hasło
 * z zapisanym hashem. Podczas rejestracji waliduje dane, sprawdza unikalność
 * adresu e-mail oraz numeru PESEL, zapisuje hasło w postaci hasha z solą
 * i automatycznie zakłada nowemu użytkownikowi podstawowe konto bankowe.
 */
public class AuthService {

    private static final int DOMYSLNY_TYP_KONTA = 1;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserDAO userDAO = new UserDAO();
    private final BankAccountDAO accountDAO = new BankAccountDAO();

    /*
     * Loguje użytkownika na podstawie e-maila i hasła. W razie niepowodzenia
     * zgłasza wyjątek BankException z komunikatem dla użytkownika.
     */
    public User login(String email, String password) {
        if (!ValidationUtil.isNotBlank(email) || !ValidationUtil.isNotBlank(password)) {
            throw new BankException("Wprowadź adres e-mail oraz hasło.");
        }

        User user = userDAO.findByEmail(email.trim());
        if (user == null) {
            throw new BankException("Nie znaleziono użytkownika o podanym adresie e-mail.");
        }

        if (!PasswordUtil.verifyPassword(password, user.getHasloSol(), user.getHasloHash())) {
            throw new BankException("Nieprawidłowe hasło.");
        }
        return user;
    }

    /*
     * Rejestruje nowego użytkownika wraz z założeniem podstawowego konta.
     */
    public User register(String imie, String nazwisko, String pesel, String email,
                         String haslo, String powtorzHaslo) {

        if (!ValidationUtil.isNotBlank(imie) || !ValidationUtil.isNotBlank(nazwisko)) {
            throw new BankException("Imię i nazwisko nie mogą być puste.");
        }
        if (!ValidationUtil.isValidPesel(pesel)) {
            throw new BankException("PESEL musi składać się z 11 cyfr.");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            throw new BankException("Podaj poprawny adres e-mail.");
        }
        if (!ValidationUtil.isNotBlank(haslo)) {
            throw new BankException("Hasło nie może być puste.");
        }
        if (!haslo.equals(powtorzHaslo)) {
            throw new BankException("Podane hasła nie są takie same.");
        }
        if (userDAO.emailExists(email.trim())) {
            throw new BankException("Podany adres e-mail jest już zajęty.");
        }
        if (userDAO.peselExists(pesel.trim())) {
            throw new BankException("Użytkownik o podanym numerze PESEL już istnieje.");
        }

        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hashPassword(haslo, salt);

        User user = new User();
        user.setImie(imie.trim());
        user.setNazwisko(nazwisko.trim());
        user.setPesel(pesel.trim());
        user.setEmail(email.trim());
        user.setHasloHash(hash);
        user.setHasloSol(salt);
        userDAO.insert(user);

        utworzPodstawoweKonto(user.getUserId());
        return user;
    }

    /*
     * Tworzy nowemu użytkownikowi konto osobiste z saldem początkowym 0 zł
     * i unikalnym numerem konta.
     */
    private void utworzPodstawoweKonto(int userId) {
        BankAccount account = new BankAccount();
        account.setNrKonta(wygenerujNumerKonta());
        account.setRodzajKonta(DOMYSLNY_TYP_KONTA);
        account.setSaldo(BigDecimal.ZERO);
        account.setUserId(userId);
        account.setStatus("AKTYWNE");
        accountDAO.insert(account);
    }

    /*
     * Generuje unikalny, 26-cyfrowy numer konta.
     */
    private String wygenerujNumerKonta() {
        String numer;
        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 26; i++) {
                sb.append(RANDOM.nextInt(10));
            }
            numer = sb.toString();
        } while (accountDAO.exists(numer));
        return numer;
    }
}
