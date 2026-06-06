package com.simplebank.service;

import com.simplebank.dao.UserDAO;
import com.simplebank.model.User;
import com.simplebank.util.PasswordUtil;
import com.simplebank.util.ValidationUtil;

/*
 * Serwis obsługujący zmianę danych logowania użytkownika: adresu e-mail
 * oraz hasła. Przy zmianie hasła wymagane jest podanie i zweryfikowanie
 * dotychczasowego hasła, a nowe hasło jest zapisywane jako hash z nową solą.
 */
public class UserService {

    private final UserDAO userDAO = new UserDAO();

    /*
     * Zmienia adres e-mail użytkownika po sprawdzeniu formatu i unikalności.
     * Aktualizuje również przekazany obiekt User.
     */
    public void changeEmail(User user, String newEmail) {
        if (!ValidationUtil.isValidEmail(newEmail)) {
            throw new BankException("Podaj poprawny adres e-mail.");
        }
        String email = newEmail.trim();
        if (email.equalsIgnoreCase(user.getEmail())) {
            throw new BankException("Nowy adres e-mail jest taki sam jak obecny.");
        }
        if (userDAO.emailExistsForOtherUser(email, user.getUserId())) {
            throw new BankException("Podany adres e-mail jest już zajęty.");
        }
        userDAO.updateEmail(user.getUserId(), email);
        user.setEmail(email);
    }

    /*
     * Zmienia hasło użytkownika po weryfikacji dotychczasowego hasła.
     * Aktualizuje również hash i sól w przekazanym obiekcie User.
     */
    public void changePassword(User user, String oldPassword, String newPassword, String repeatPassword) {
        if (!PasswordUtil.verifyPassword(oldPassword, user.getHasloSol(), user.getHasloHash())) {
            throw new BankException("Dotychczasowe hasło jest nieprawidłowe.");
        }
        if (!ValidationUtil.isNotBlank(newPassword)) {
            throw new BankException("Nowe hasło nie może być puste.");
        }
        if (!newPassword.equals(repeatPassword)) {
            throw new BankException("Nowe hasła nie są takie same.");
        }
        if (newPassword.equals(oldPassword)) {
            throw new BankException("Nowe hasło musi różnić się od dotychczasowego.");
        }

        String newSalt = PasswordUtil.generateSalt();
        String newHash = PasswordUtil.hashPassword(newPassword, newSalt);
        userDAO.updatePassword(user.getUserId(), newHash, newSalt);
        user.setHasloHash(newHash);
        user.setHasloSol(newSalt);
    }
}
