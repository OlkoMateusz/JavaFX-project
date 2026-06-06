package com.simplebank.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

/*
 * Klasa odpowiedzialna za automatyczną inicjalizację bazy danych przy każdym
 * uruchomieniu aplikacji.
 *
 * Działanie:
 *  1) zapewnia istnienie bazy danych (CREATE DATABASE IF NOT EXISTS),
 *  2) sprawdza, czy istnieją wszystkie wymagane tabele - jeśli nie, wykonuje
 *     skrypt migracji (001_migration.sql),
 *  3) sprawdza, czy w bazie znajdują się dane - jeśli tabela użytkowników jest
 *     pusta, wykonuje skrypt z danymi (002_seeder.sql).
 *
 * Dzięki temu, jeżeli baza nie została jeszcze przygotowana w phpMyAdmin,
 * aplikacja samodzielnie utworzy strukturę i wgra dane testowe. Jeżeli dane
 * już istnieją, żaden ze skryptów nie jest wykonywany ponownie.
 */
public final class DatabaseInitializer {

    private static final String MIGRATION_RESOURCE = "/database/001_migration.sql";
    private static final String SEEDER_RESOURCE = "/database/002_seeder.sql";

    private static final List<String> WYMAGANE_TABELE = Arrays.asList(
            "uzytkownicy", "typ_konta", "konta_bankowe",
            "typy_transakcji", "transakcje", "karty", "kredyty");

    private DatabaseInitializer() {
    }

    /*
     * Główna metoda inicjalizująca. Powinna być wywołana raz, przy starcie
     * aplikacji, przed wyświetleniem pierwszego widoku.
     */
    public static void initialize() {
        zapewnijIstnienieBazy();

        if (!wszystkieTabeleIstnieja()) {
            System.out.println("[DB] Brak tabel - wykonuję migrację...");
            wykonajSkrypt(MIGRATION_RESOURCE);
        } else {
            System.out.println("[DB] Struktura tabel jest obecna.");
        }

        if (potrzebnySeeder()) {
            System.out.println("[DB] Brak danych - wykonuję seeder...");
            wykonajSkrypt(SEEDER_RESOURCE);
        } else {
            System.out.println("[DB] Dane są już wgrane.");
        }
    }

    /*
     * Tworzy bazę danych, jeśli jeszcze nie istnieje. Wykonywane na połączeniu
     * z serwerem (bez wskazania konkretnej bazy).
     */
    private static void zapewnijIstnienieBazy() {
        String sql = "CREATE DATABASE IF NOT EXISTS " + DatabaseConnection.getDatabaseName()
                + " CHARACTER SET utf8mb4 COLLATE utf8mb4_polish_ci";
        try (Connection conn = DatabaseConnection.getServerConnection();
             Statement st = conn.createStatement()) {
            st.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Nie udało się połączyć z serwerem MySQL ani utworzyć bazy danych. "
                            + "Upewnij się, że MySQL w XAMPP jest uruchomiony.\nSzczegóły: "
                            + e.getMessage(), e);
        }
    }

    /*
     * Sprawdza, czy w bazie istnieją wszystkie wymagane tabele.
     */
    private static boolean wszystkieTabeleIstnieja() {
        String sql = "SELECT COUNT(*) FROM information_schema.tables "
                + "WHERE table_schema = ? AND table_name IN "
                + "('uzytkownicy','typ_konta','konta_bankowe','typy_transakcji',"
                + "'transakcje','karty','kredyty')";
        try (Connection conn = DatabaseConnection.getServerConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, DatabaseConnection.getDatabaseName());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == WYMAGANE_TABELE.size();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd sprawdzania struktury bazy: " + e.getMessage(), e);
        }
        return false;
    }

    /*
     * Sprawdza, czy konieczne jest wgranie danych testowych. Zwraca true, gdy
     * tabela użytkowników jest pusta.
     */
    private static boolean potrzebnySeeder() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM uzytkownicy")) {
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd sprawdzania danych w bazie: " + e.getMessage(), e);
        }
        return false;
    }

    /*
     * Wczytuje skrypt SQL z zasobów aplikacji, dzieli go na pojedyncze polecenia
     * i wykonuje je po kolei na połączeniu z serwerem.
     */
    private static void wykonajSkrypt(String resourcePath) {
        String content = wczytajZasob(resourcePath);
        List<String> polecenia = podzielNaPolecenia(content);

        try (Connection conn = DatabaseConnection.getServerConnection();
             Statement st = conn.createStatement()) {
            for (String polecenie : polecenia) {
                st.execute(polecenie);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd wykonywania skryptu " + resourcePath
                    + ": " + e.getMessage(), e);
        }
    }

    private static String wczytajZasob(String resourcePath) {
        try (InputStream in = DatabaseInitializer.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new RuntimeException("Nie znaleziono pliku zasobu: " + resourcePath);
            }
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
            }
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException("Błąd odczytu zasobu " + resourcePath, e);
        }
    }

    /*
     * Usuwa komentarze blokowe oraz liniowe i dzieli treść skryptu na
     * pojedyncze polecenia SQL na podstawie znaku średnika.
     */
    private static List<String> podzielNaPolecenia(String content) {
        String bezKomentarzyBlokowych = content.replaceAll("(?s)/\\*.*?\\*/", "");

        StringBuilder czysty = new StringBuilder();
        for (String line : bezKomentarzyBlokowych.split("\n")) {
            String trimmed = line.trim();
            if (trimmed.startsWith("--") || trimmed.isEmpty()) {
                continue;
            }
            czysty.append(line).append('\n');
        }

        return Arrays.stream(czysty.toString().split(";"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(java.util.stream.Collectors.toList());
    }
}
