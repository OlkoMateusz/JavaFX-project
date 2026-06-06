package com.simplebank.util;

import com.simplebank.model.User;

/*
 * Prosty menedżer sesji aplikacji. Przechowuje aktualnie zalogowanego
 * użytkownika w pamięci, dzięki czemu poszczególne kontrolery mają dostęp
 * do jego danych bez ponownego pobierania ich z bazy.
 */
public final class SessionManager {

    private static User currentUser;

    private SessionManager() {
    }

    public static void login(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void logout() {
        currentUser = null;
    }
}
