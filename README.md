# 🏦 Simple Bank

Prosta symulacja banku w **JavaFX** z lokalną bazą danych **MySQL/MariaDB** (XAMPP).
Aplikacja obsługuje rejestrację, logowanie, panel klienta oraz podstawowe operacje
bankowe: przelew, wpłatę, wypłatę, karty, kredyty i zmianę danych logowania.

---

## 1. Wymagania

- **JDK 17** lub nowszy
- **Maven 3.8+** (zalecane — projekt jest gotowy do `mvn javafx:run`)
- **XAMPP** z uruchomionym **MySQL/MariaDB** oraz **phpMyAdmin**
- IntelliJ IDEA / Eclipse / NetBeans (opcjonalnie)

Zależności (pobierane automatycznie przez Maven):
- `org.openjfx:javafx-controls` i `javafx-fxml` (21.0.2)
- `com.mysql:mysql-connector-j` (8.3.0)

---

## 2. Struktura projektu

```
JavaFX-project/
├─ pom.xml
├─ README.md
├─ database/
│  ├─ 001_migration.sql      # tworzy bazę i tabele
│  └─ 002_seeder.sql         # dane testowe
└─ src/main/
   ├─ java/com/simplebank/
   │  ├─ App.java
   │  ├─ controller/  (Login, Register, Dashboard, Transfer, Deposit,
   │  │               Withdraw, Cards, Loans, Settings)
   │  ├─ dao/         (User, BankAccount, Transaction, Card, Loan,
   │  │               AccountType, TransactionType)
   │  ├─ model/       (User, BankAccount, AccountType, Transaction,
   │  │               TransactionType, Card, Loan)
   │  ├─ service/     (Auth, Bank, Card, Loan, User, BankException)
   │  └─ util/        (DatabaseConnection, PasswordUtil, ValidationUtil,
   │                  SessionManager, AlertUtil, SceneManager)
   └─ resources/com/simplebank/
      ├─ view/        (pliki .fxml)
      └─ css/styles.css
```

---

## 3. Przygotowanie bazy danych (phpMyAdmin)

> Kolejność jest ważna: **najpierw migracja, potem seeder.**

1. Uruchom w XAMPP **Apache** i **MySQL**.
2. Wejdź na `http://localhost/phpmyadmin`.
3. Kliknij zakładkę **SQL** (na górze, bez wybierania bazy).
4. Otwórz plik [`database/001_migration.sql`](database/001_migration.sql),
   skopiuj **całą** zawartość, wklej w pole SQL i kliknij **Wykonaj**.
   - Skrypt utworzy bazę **`bank_app`** wraz ze wszystkimi tabelami.
5. Następnie otwórz [`database/002_seeder.sql`](database/002_seeder.sql),
   skopiuj całą zawartość, wklej w pole SQL i kliknij **Wykonaj**.
   - Skrypt wypełni tabele danymi testowymi.

Alternatywnie (zakładka **Import** w phpMyAdmin): zaimportuj najpierw
`001_migration.sql`, a potem `002_seeder.sql`.

---

## 4. Dane połączenia z bazą

Domyślnie ustawione pod XAMPP (plik
[`DatabaseConnection.java`](src/main/java/com/simplebank/util/DatabaseConnection.java)):

| Parametr   | Wartość     |
|------------|-------------|
| host       | `localhost` |
| port       | `3306`      |
| baza       | `bank_app`  |
| użytkownik | `root`      |
| hasło      | *(puste)*   |

Jeśli Twój MySQL ma inne dane logowania, zmień stałe `USER` / `PASSWORD`
w klasie `DatabaseConnection`.

---

## 5. Uruchomienie aplikacji

### A) Maven (najprościej)

W katalogu projektu:

```bash
mvn clean javafx:run
```

### B) IntelliJ IDEA

1. **File → Open** → wskaż katalog z `pom.xml`.
2. Poczekaj, aż Maven pobierze zależności.
3. Uruchom konfigurację Maven `javafx:run` **lub** prawym przyciskiem na
   `App.java` → **Run 'App.main()'**.
   - Jeśli przy uruchamianiu bezpośrednio `App` pojawi się błąd o brakujących
     komponentach JavaFX, użyj `mvn javafx:run` (plugin sam dokłada moduły JavaFX).

### C) Eclipse / NetBeans

1. Zaimportuj jako **istniejący projekt Maven**.
2. Uruchom cel Mavena: `javafx:run`.

---

## 6. Konta testowe (z seedera)

Hasło dla wszystkich kont: **`haslo123`**

| E-mail                          | Hasło      | Uwagi                            |
|---------------------------------|------------|----------------------------------|
| `jan.kowalski@example.com`      | `haslo123` | 2 konta, karta, aktywny kredyt   |
| `anna.nowak@example.com`        | `haslo123` | 1 konto, karta                   |
| `piotr.wisniewski@example.com`  | `haslo123` | konto premium, wniosek kredytowy |

Możesz też założyć nowe konto przyciskiem **„Zarejestruj się”** — nowy
użytkownik dostaje automatycznie konto osobiste z saldem 0 zł.

---

## 7. Funkcje aplikacji

- **Logowanie / rejestracja** z walidacją (PESEL 11 cyfr, format e-mail,
  unikalny e-mail, zgodność haseł).
- **Panel klienta**: dane użytkownika, konta, saldo, status, historia transakcji.
- **Przelew** — transakcja SQL z `commit`/`rollback`, sprawdzanie salda i
  istnienia konta odbiorcy.
- **Wpłata / wypłata** gotówki z zapisem w historii.
- **Karty** — podgląd i generowanie nowych kart (numer, CVV, data ważności).
- **Kredyty** — składanie wniosku, lista kredytów, uproszczone naliczanie odsetek.
- **Ustawienia** — zmiana e-maila i hasła (weryfikacja starego hasła).
- Hasła przechowywane jako **SHA-256 z indywidualną solą**.

---

## 8. Uwagi techniczne

- Projekt jest **niemodułowy** (brak `module-info.java`) — upraszcza to
  współpracę JavaFX z konektorem MySQL.
- Operacje finansowe blokują wiersze (`SELECT ... FOR UPDATE`) i działają w
  jednej transakcji SQL, więc stan kont pozostaje spójny nawet przy błędzie.
