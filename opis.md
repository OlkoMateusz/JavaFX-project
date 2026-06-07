# 🏦 Simple Bank — opis projektu (materiał do dokumentacji)

> Dokument przygotowany jako podstawa do napisania dokumentacji projektu.
> W miejscach oznaczonych **[ZDJĘCIE: ...]** wstaw zrzut ekranu — pod każdym
> takim miejscem znajduje się gotowy opis, który możesz wykorzystać jako podpis
> pod ilustracją.

---

## Spis treści

1. [Opis ogólny aplikacji](#1-opis-ogólny-aplikacji)
2. [Użyte technologie](#2-użyte-technologie)
3. [Architektura aplikacji](#3-architektura-aplikacji)
4. [Struktura projektu (katalogi i pliki)](#4-struktura-projektu-katalogi-i-pliki)
5. [Baza danych — jak powstaje i jak działa](#5-baza-danych--jak-powstaje-i-jak-działa)
6. [Diagram ERD i opis tabel](#6-diagram-erd-i-opis-tabel)
7. [Jak działa aplikacja (przepływ użytkownika)](#7-jak-działa-aplikacja-przepływ-użytkownika)
8. [Opis poszczególnych ekranów i zakładek](#8-opis-poszczególnych-ekranów-i-zakładek)
9. [Bezpieczeństwo i walidacja](#9-bezpieczeństwo-i-walidacja)
10. [Uruchomienie aplikacji](#10-uruchomienie-aplikacji)
11. [Dane testowe](#11-dane-testowe)

---

## 1. Opis ogólny aplikacji

**Simple Bank** to desktopowa aplikacja bankowa napisana w **JavaFX**, symulująca
podstawowe działanie banku internetowego. Dane przechowywane są w lokalnej bazie
**MySQL/MariaDB** (uruchamianej w środowisku **XAMPP**).

Aplikacja umożliwia:

- **rejestrację** nowego klienta (wraz z automatycznym założeniem konta bankowego),
- **logowanie** do panelu klienta,
- przegląd **danych użytkownika**, jego **kont** oraz **historii transakcji**,
- wykonywanie operacji bankowych: **przelew**, **wpłata**, **wypłata**,
- zarządzanie **kartami płatniczymi** (generowanie nowej karty),
- składanie **wniosków kredytowych** i przegląd kredytów,
- zmianę **danych logowania** (adres e-mail oraz hasło).

Jedną z istotnych cech projektu jest to, że aplikacja **sama tworzy i wypełnia
bazę danych** przy pierwszym uruchomieniu — nie trzeba ręcznie importować
skryptów SQL (choć można to zrobić również w phpMyAdmin).

> **[ZDJĘCIE: ekran startowy / logo aplikacji]**
>
> *Podpis:* Aplikacja Simple Bank — desktopowa symulacja bankowości elektronicznej
> napisana w JavaFX, korzystająca z lokalnej bazy danych MySQL.

---

## 2. Użyte technologie

| Obszar | Technologia | Wersja |
|---|---|---|
| Język | Java | 17 |
| Interfejs graficzny | JavaFX (controls + FXML) | 24.0.2 |
| Baza danych | MySQL / MariaDB (XAMPP) | — |
| Sterownik JDBC | MySQL Connector/J | 8.3.0 |
| Budowanie projektu | Apache Maven | 3.8+ |
| Styl interfejsu | CSS (JavaFX) | — |

Konfiguracja zależności i budowania znajduje się w pliku [pom.xml](pom.xml).
Aplikacja jest uruchamiana wtyczką `javafx-maven-plugin` (`mvn javafx:run`),
a klasą główną jest [App.java](src/main/java/com/simplebank/App.java).

---

## 3. Architektura aplikacji

Projekt został zbudowany w oparciu o **warstwowy podział odpowiedzialności**
(zbliżony do wzorca MVC + warstwa serwisów i DAO):

```
┌──────────────────────────────────────────────────────────────┐
│  WIDOK (FXML + CSS)                                             │
│  pliki .fxml w resources/com/simplebank/view + styles.css      │
│  Definiują wygląd ekranów: logowanie, panel, karty, kredyty... │
└───────────────▲────────────────────────────────────────────────┘
                │ wiąże pola i akcje (@FXML)
┌───────────────┴────────────────────────────────────────────────┐
│  KONTROLERY (controller/)                                       │
│  Obsługują zdarzenia z interfejsu (kliknięcia, formularze),     │
│  pobierają dane z pól, wołają serwisy i prezentują wyniki.      │
└───────────────▲────────────────────────────────────────────────┘
                │ wywołuje logikę biznesową
┌───────────────┴────────────────────────────────────────────────┐
│  SERWISY (service/)                                             │
│  Logika biznesowa: uwierzytelnianie, przelewy, kredyty, karty.  │
│  Walidacja reguł, transakcyjność operacji (commit / rollback).  │
└───────────────▲────────────────────────────────────────────────┘
                │ operacje na danych
┌───────────────┴────────────────────────────────────────────────┐
│  DAO (dao/)  — Data Access Object                              │
│  Zapytania SQL do bazy (SELECT/INSERT/UPDATE) na obiektach.     │
└───────────────▲────────────────────────────────────────────────┘
                │ połączenie JDBC
┌───────────────┴────────────────────────────────────────────────┐
│  BAZA DANYCH MySQL (bank_app)                                  │
└────────────────────────────────────────────────────────────────┘

   Obok: MODELE (model/) — klasy danych (User, BankAccount, Card...)
         NARZĘDZIA (util/) — połączenie z bazą, sesja, hasła, sceny.
```

**Najważniejsze pakiety:**

- **`model`** — klasy reprezentujące dane (encje): `User`, `BankAccount`,
  `Card`, `Loan`, `Transaction`, `AccountType`, `TransactionType`.
- **`dao`** — klasy dostępu do bazy danych, jedna na encję (np. `UserDAO`,
  `BankAccountDAO`, `TransactionDAO`).
- **`service`** — logika biznesowa: `AuthService` (logowanie/rejestracja),
  `BankService` (przelew/wpłata/wypłata), `CardService`, `LoanService`,
  `UserService`. Błędy biznesowe zgłaszane są przez `BankException`.
- **`controller`** — kontrolery JavaFX powiązane z plikami FXML.
- **`util`** — klasy pomocnicze:
  - `DatabaseConnection` — nawiązywanie połączenia z bazą,
  - `DatabaseInitializer` — automatyczne tworzenie i wypełnianie bazy,
  - `PasswordUtil` — hashowanie haseł (SHA-256 + sól),
  - `SessionManager` — przechowywanie zalogowanego użytkownika,
  - `SceneManager` — przełączanie widoków i okien dialogowych,
  - `ValidationUtil` — walidacja danych (e-mail, PESEL, kwoty),
  - `AlertUtil` — okienka komunikatów (informacja / błąd / potwierdzenie).

> **[ZDJĘCIE: diagram architektury / schemat warstw]**
>
> *Podpis:* Warstwowa architektura aplikacji — od widoku (FXML), przez
> kontrolery i serwisy, aż po warstwę dostępu do danych (DAO) i bazę MySQL.

---

## 4. Struktura projektu (katalogi i pliki)

```
JavaFX-project/
├── pom.xml                         # konfiguracja Maven i zależności
├── README.md                       # krótka instrukcja
├── opis.md                         # ten dokument
├── database/                       # kopia skryptów SQL do importu w phpMyAdmin
│   ├── 001_migration.sql
│   └── 002_seeder.sql
└── src/main/
    ├── java/com/simplebank/
    │   ├── App.java                # klasa główna (start aplikacji)
    │   ├── Launcher.java           # launcher (uruchamianie z poziomu JAR)
    │   ├── controller/             # kontrolery ekranów (9 plików)
    │   ├── dao/                    # dostęp do bazy danych (7 plików)
    │   ├── model/                  # modele/encje (7 plików)
    │   ├── service/                # logika biznesowa (6 plików)
    │   └── util/                   # klasy pomocnicze (7 plików)
    └── resources/com/simplebank/
        ├── css/styles.css          # arkusz stylów aplikacji
        ├── view/                   # widoki FXML (9 plików)
        └── ../../database/         # skrypty SQL wbudowane w aplikację
            ├── 001_migration.sql   # tworzenie struktury bazy
            └── 002_seeder.sql      # dane testowe
```

> Skrypty SQL znajdują się w **dwóch miejscach**: w `src/main/resources/database/`
> (są wbudowane w aplikację i wykonywane automatycznie) oraz w katalogu
> `database/` w głównym folderze (kopia do ręcznego importu w phpMyAdmin).

---

## 5. Baza danych — jak powstaje i jak działa

### 5.1. Dwa sposoby utworzenia bazy

**A) Automatycznie (zalecane).**
Przy każdym uruchomieniu klasa [App.java](src/main/java/com/simplebank/App.java)
wywołuje `DatabaseInitializer.initialize()`. Mechanizm ten:

1. **Tworzy bazę** `bank_app`, jeśli jeszcze nie istnieje
   (`CREATE DATABASE IF NOT EXISTS bank_app ...` z kodowaniem `utf8mb4`).
2. **Sprawdza, czy istnieją wszystkie wymagane tabele** (zapytanie do
   `information_schema.tables`). Jeśli brakuje którejkolwiek — wykonuje skrypt
   `001_migration.sql` (tworzy całą strukturę).
3. **Sprawdza, czy w tabeli `uzytkownicy` są dane.** Jeśli jest pusta —
   wykonuje `002_seeder.sql` (wgrywa dane testowe).

Dzięki temu, jeśli baza dopiero powstaje, aplikacja sama przygotuje strukturę
i dane. Jeśli dane już są — żaden skrypt nie zostanie wykonany ponownie
(operacja jest **idempotentna**).

**B) Ręcznie (przez phpMyAdmin).**
Można też zaimportować po kolei skrypty z katalogu `database/`:
najpierw `001_migration.sql`, potem `002_seeder.sql`.

> **[ZDJĘCIE: konsola / logi przy pierwszym uruchomieniu]**
>
> *Podpis:* Przy pierwszym starcie aplikacja wypisuje w konsoli komunikaty
> `[DB] Brak tabel - wykonuję migrację...` oraz `[DB] Brak danych - wykonuję
> seeder...`, co potwierdza automatyczne tworzenie bazy.

### 5.2. Połączenie z bazą

Parametry połączenia są zdefiniowane w
[DatabaseConnection.java](src/main/java/com/simplebank/util/DatabaseConnection.java)
i odpowiadają domyślnej konfiguracji XAMPP:

| Parametr | Wartość |
|---|---|
| Host | `localhost` |
| Port | `3306` |
| Baza | `bank_app` |
| Użytkownik | `root` |
| Hasło | *(puste)* |

Klasa udostępnia dwa typy połączeń:
- `getConnection()` — połączenie z konkretną bazą `bank_app`,
- `getServerConnection()` — połączenie z serwerem bez wskazania bazy
  (używane do `CREATE DATABASE`, gdy baza może jeszcze nie istnieć).

### 5.3. Jak wykonywany jest skrypt SQL

Klasa
[DatabaseInitializer.java](src/main/java/com/simplebank/util/DatabaseInitializer.java)
wczytuje plik `.sql` z zasobów aplikacji, **usuwa komentarze** (blokowe `/* */`
oraz liniowe `--`), **dzieli treść na pojedyncze polecenia** po znaku średnika
`;`, a następnie wykonuje je kolejno na jednym połączeniu.

> **[ZDJĘCIE: widok bazy bank_app w phpMyAdmin z listą tabel]**
>
> *Podpis:* Struktura bazy `bank_app` w phpMyAdmin — 7 tabel utworzonych przez
> skrypt migracyjny: `uzytkownicy`, `typ_konta`, `konta_bankowe`,
> `typy_transakcji`, `transakcje`, `karty`, `kredyty`.

---

## 6. Diagram ERD i opis tabel

### 6.1. Diagram ERD (relacje między tabelami)

```
        ┌────────────────────┐
        │     typ_konta      │
        │────────────────────│
        │ PK rodzaj_konta    │
        │    nazwa           │
        │    oprocentowanie  │
        │    mozliwosc_kredytu│
        └─────────▲──────────┘
                  │ 1
                  │            ┌─────────────────────┐
                  │            │     uzytkownicy     │
                  │            │─────────────────────│
                  │            │ PK user_id          │
                  │            │    imie, nazwisko   │
                  │            │    pesel (UNIQUE)   │
                  │            │    email (UNIQUE)   │
                  │            │    haslo_hash       │
                  │            │    haslo_sol        │
                  │            └──────▲───────▲──────┘
                  │                 1 │       │ 1
              N   │                   │       │   N
        ┌─────────┴──────────┐        │       └──────────────┐
        │   konta_bankowe    │────────┘                      │
        │────────────────────│ N                             │
        │ PK nr_konta        │                       ┌───────┴────────┐
        │ FK rodzaj_konta    │                       │     kredyty    │
        │ FK user_id         │                       │────────────────│
        │    saldo           │                       │ PK id_kredytu  │
        │    status          │                       │ FK user_id     │
        └───▲────────▲───────┘                       │    kwota       │
          1 │      1 │                               │    stopa_kredytu│
            │        │                               │    miesiace    │
        N   │        │ N                             │    status      │
   ┌────────┴───┐  ┌─┴──────────────────┐           │    splacone    │
   │   karty    │  │     transakcje     │           └────────────────┘
   │────────────│  │────────────────────│
   │ PK id_karty│  │ PK id_transakcji   │        ┌─────────────────────┐
   │ FK id_konta│  │ FK from_nr_konta ──┼───────►│  (konta_bankowe)    │
   │    nr_karty│  │ FK to_nr_konta   ──┼───────►│                     │
   │    cvv     │  │    kwota           │        └─────────────────────┘
   │    status  │  │ FK typ_transakcji ─┼──┐
   └────────────┘  └────────────────────┘  │     ┌─────────────────────┐
                                            └────►│  typy_transakcji    │
                                                  │─────────────────────│
                                                  │ PK typ_transakcji   │
                                                  │    nazwa            │
                                                  │    czas_realizacji  │
                                                  │    oplata           │
                                                  └─────────────────────┘
```

**Relacje (kardynalność):**

- `uzytkownicy` **1 — N** `konta_bankowe` — jeden użytkownik może mieć wiele kont.
- `uzytkownicy` **1 — N** `kredyty` — jeden użytkownik może mieć wiele kredytów.
- `typ_konta` **1 — N** `konta_bankowe` — jeden typ przypisany do wielu kont.
- `konta_bankowe` **1 — N** `karty` — do jednego konta można wydać wiele kart.
- `konta_bankowe` **1 — N** `transakcje` (jako nadawca *lub* odbiorca).
- `typy_transakcji` **1 — N** `transakcje` — jeden typ opisuje wiele transakcji.

> **[ZDJĘCIE: diagram ERD wygenerowany w phpMyAdmin / narzędziu do modelowania]**
>
> *Podpis:* Diagram związków encji (ERD) bazy `bank_app` przedstawiający siedem
> tabel oraz relacje kluczy obcych między nimi.

### 6.2. Opis tabel

#### `uzytkownicy`
Przechowuje dane klientów banku. Hasło **nigdy** nie jest zapisywane jawnie —
trzymany jest jego hash SHA-256 wraz z indywidualną solą. Pola `email` i `pesel`
są unikalne.

| Kolumna | Typ | Opis |
|---|---|---|
| `user_id` (PK) | INT AI | identyfikator użytkownika |
| `imie`, `nazwisko` | VARCHAR | dane osobowe |
| `pesel` | CHAR(11), UNIQUE | numer PESEL |
| `email` | VARCHAR, UNIQUE | adres e-mail (login) |
| `haslo_hash` | VARCHAR(128) | hash hasła (SHA-256, Base64) |
| `haslo_sol` | VARCHAR(64) | sól użyta przy hashowaniu |
| `data_utworzenia` | TIMESTAMP | data założenia konta |

#### `typ_konta`
Tabela słownikowa typów kont (produktów bankowych).

| Kolumna | Typ | Opis |
|---|---|---|
| `rodzaj_konta` (PK) | INT AI | identyfikator typu |
| `nazwa` | VARCHAR, UNIQUE | np. „Konto osobiste" |
| `oprocentowanie` | DECIMAL(5,2) | oprocentowanie w % |
| `mozliwosc_kredytu` | BOOLEAN | czy typ pozwala na kredyt |

#### `konta_bankowe`
Konta klientów. Numer konta (`nr_konta`, 26 cyfr) jest kluczem głównym i
jednocześnie identyfikatorem w przelewach.

| Kolumna | Typ | Opis |
|---|---|---|
| `nr_konta` (PK) | VARCHAR(26) | numer konta |
| `rodzaj_konta` (FK) | INT | → `typ_konta` |
| `saldo` | DECIMAL(15,2) | aktualne saldo |
| `user_id` (FK) | INT | → `uzytkownicy` |
| `status` | ENUM | AKTYWNE / ZABLOKOWANE / ZAMKNIETE |

#### `typy_transakcji`
Tabela słownikowa rodzajów operacji.

| Kolumna | Typ | Opis |
|---|---|---|
| `typ_transakcji` (PK) | INT AI | identyfikator typu |
| `nazwa` | VARCHAR, UNIQUE | Przelew / Wpłata / Wypłata |
| `czas_realizacji` | INT | czas w minutach |
| `oplata` | DECIMAL(10,2) | ewentualna opłata |

#### `transakcje`
Historia operacji. Pola `from_nr_konta` i `to_nr_konta` mogą być `NULL`:
- **wpłata** — brak nadawcy (`from` = NULL),
- **wypłata** — brak odbiorcy (`to` = NULL),
- **przelew** — wypełnione obie kolumny.

| Kolumna | Typ | Opis |
|---|---|---|
| `id_transakcji` (PK) | INT AI | identyfikator |
| `from_nr_konta` (FK) | VARCHAR(26), NULL | konto nadawcy |
| `to_nr_konta` (FK) | VARCHAR(26), NULL | konto odbiorcy |
| `kwota` | DECIMAL(15,2) | kwota operacji |
| `typ_transakcji` (FK) | INT | → `typy_transakcji` |
| `data_transakcji` | TIMESTAMP | data i czas |

#### `karty`
Karty płatnicze powiązane z kontem. Numer karty (16 cyfr) jest unikalny.

| Kolumna | Typ | Opis |
|---|---|---|
| `id_karty` (PK) | INT AI | identyfikator |
| `id_konta` (FK) | VARCHAR(26) | → `konta_bankowe` |
| `nr_karty` | CHAR(16), UNIQUE | numer karty |
| `cvv` | CHAR(3) | kod CVV |
| `data_waznosci` | DATE | data ważności |
| `status` | ENUM | AKTYWNA / ZABLOKOWANA |

#### `kredyty`
Wnioski i aktywne kredyty użytkowników.

| Kolumna | Typ | Opis |
|---|---|---|
| `id_kredytu` (PK) | INT AI | identyfikator |
| `user_id` (FK) | INT | → `uzytkownicy` |
| `kwota` | DECIMAL(15,2) | kwota kredytu |
| `stopa_kredytu` | DECIMAL(5,2) | oprocentowanie w % |
| `miesiace` | INT | okres spłaty |
| `status` | ENUM | OCZEKUJACY / AKTYWNY / SPLACONY / ODRZUCONY |
| `splacone` | DECIMAL(15,2) | kwota już spłacona |
| `data_kredytu` | TIMESTAMP | data złożenia wniosku |

**Integralność danych:** klucze obce wykorzystują reguły `ON DELETE CASCADE`
(usunięcie użytkownika usuwa jego konta, karty i kredyty), `ON DELETE SET NULL`
(usunięcie konta nie kasuje transakcji, jedynie zeruje powiązanie) oraz
`ON DELETE RESTRICT` (nie można usunąć typu konta/transakcji, jeśli jest używany).

---

## 7. Jak działa aplikacja (przepływ użytkownika)

```
   START aplikacji
        │
        ▼
  [DatabaseInitializer] ── tworzy/uzupełnia bazę bank_app
        │
        ▼
   Ekran LOGOWANIA ──────────────► Ekran REJESTRACJI
        │  (e-mail + hasło)          │ (dane + hasło)
        │                            │ zakłada użytkownika
        │                            │ + konto osobiste
        ▼                            │
   AuthService.login()  ◄────────────┘ (powrót do logowania)
        │ poprawne dane → zapis w SessionManager
        ▼
   PANEL KLIENTA (dashboard) z zakładkami:
        ├── Pulpit      → dane, konta, historia, operacje
        ├── Karty       → lista i generowanie kart
        ├── Kredyty     → wnioski i lista kredytów
        └── Ustawienia  → zmiana e-maila / hasła
        │
        ▼
   Wylogowanie → SessionManager.logout() → powrót do logowania
```

**Najważniejsze mechanizmy działania:**

- **Sesja** — po zalogowaniu obiekt `User` jest przechowywany w
  `SessionManager`, dzięki czemu każda zakładka wie, kto jest zalogowany,
  bez ponownego sięgania do bazy.
- **Przełączanie widoków** — realizuje `SceneManager`: pełne ekrany
  (`switchScene`) oraz okna modalne dla operacji bankowych (`openDialog`).
- **Transakcyjność operacji** — przelew, wpłata i wypłata wykonywane są w
  ramach jednej transakcji SQL (`setAutoCommit(false)` → `commit()` lub
  `rollback()` w razie błędu). Saldo konta jest blokowane do odczytu
  (`SELECT ... FOR UPDATE`), aby uniknąć błędów przy równoczesnych operacjach.

---

## 8. Opis poszczególnych ekranów i zakładek

### 8.1. Ekran logowania

Widok [login.fxml](src/main/resources/com/simplebank/view/login.fxml),
kontroler [LoginController.java](src/main/java/com/simplebank/controller/LoginController.java).

Użytkownik podaje **adres e-mail** i **hasło**. Po kliknięciu „Zaloguj"
`AuthService` pobiera użytkownika z bazy i porównuje hasło (hash z solą).
W razie błędu pojawia się komunikat. Z tego ekranu można przejść do rejestracji.

> **[ZDJĘCIE: ekran logowania]**
>
> *Podpis:* Ekran logowania — uwierzytelnianie użytkownika na podstawie adresu
> e-mail i hasła. Hasło weryfikowane jest poprzez porównanie hashu SHA-256.

### 8.2. Ekran rejestracji

Widok [register.fxml](src/main/resources/com/simplebank/view/register.fxml),
kontroler [RegisterController.java](src/main/java/com/simplebank/controller/RegisterController.java).

Formularz zbiera: imię, nazwisko, PESEL, e-mail, hasło i powtórzenie hasła.
`AuthService.register()` waliduje dane (poprawność PESEL i e-maila, zgodność
haseł, unikalność e-maila i PESEL-u), zapisuje użytkownika z zahashowanym hasłem
i **automatycznie zakłada mu konto osobiste** z saldem 0 zł i losowym,
unikalnym 26-cyfrowym numerem.

> **[ZDJĘCIE: ekran rejestracji]**
>
> *Podpis:* Formularz rejestracji nowego klienta. Po pomyślnej walidacji
> aplikacja zakłada użytkownika oraz przypisuje mu pierwsze konto bankowe.

### 8.3. Zakładka „Pulpit" (panel główny)

Widok [dashboard.fxml](src/main/resources/com/simplebank/view/dashboard.fxml),
kontroler [DashboardController.java](src/main/java/com/simplebank/controller/DashboardController.java).

Główny ekran po zalogowaniu, zbudowany jako `TabPane` z czterema zakładkami.
Pulpit prezentuje:
- **dane użytkownika** (imię i nazwisko, e-mail, PESEL),
- **tabelę kont** (numer konta, rodzaj, saldo, status),
- **przyciski operacji**: *Przelew*, *Wpłata*, *Wypłata*, *Odśwież*,
- **tabelę historii transakcji** (data, typ, kwota, z konta, na konto).

Każda operacja otwiera osobne okno modalne, a po jego zamknięciu dane są
automatycznie odświeżane.

> **[ZDJĘCIE: zakładka Pulpit z danymi, kontami i historią transakcji]**
>
> *Podpis:* Panel klienta (Pulpit) — dane użytkownika, lista kont z saldami
> oraz historia transakcji. U góry przyciski operacji bankowych.

### 8.4. Okno „Przelew"

Widok [transfer.fxml](src/main/resources/com/simplebank/view/transfer.fxml),
kontroler [TransferController.java](src/main/java/com/simplebank/controller/TransferController.java).

Użytkownik wybiera **konto źródłowe**, wpisuje **numer konta odbiorcy** i
**kwotę**. `BankService.transfer()` sprawdza: dodatniość kwoty, istnienie obu
kont, czy nadawca ≠ odbiorca oraz wystarczające środki. Operacja jest
transakcyjna — przy błędzie następuje rollback.

> **[ZDJĘCIE: okno przelewu]**
>
> *Podpis:* Okno przelewu — wybór konta źródłowego, numer odbiorcy i kwota.
> Operacja realizowana transakcyjnie z kontrolą salda.

### 8.5. Okno „Wpłata gotówki"

Widok [deposit.fxml](src/main/resources/com/simplebank/view/deposit.fxml),
kontroler [DepositController.java](src/main/java/com/simplebank/controller/DepositController.java).

Wybór konta i kwoty; `BankService.deposit()` zwiększa saldo i zapisuje
transakcję typu *Wpłata* (bez konta nadawcy).

> **[ZDJĘCIE: okno wpłaty]**
>
> *Podpis:* Okno wpłaty gotówki — zasilenie wybranego konta wskazaną kwotą.

### 8.6. Okno „Wypłata gotówki"

Widok [withdraw.fxml](src/main/resources/com/simplebank/view/withdraw.fxml),
kontroler [WithdrawController.java](src/main/java/com/simplebank/controller/WithdrawController.java).

Wybór konta i kwoty; `BankService.withdraw()` sprawdza saldo, zmniejsza je i
zapisuje transakcję typu *Wypłata* (bez konta odbiorcy).

> **[ZDJĘCIE: okno wypłaty]**
>
> *Podpis:* Okno wypłaty gotówki — pobranie środków z konta po weryfikacji
> dostępnego salda.

### 8.7. Zakładka „Karty"

Widok [cards.fxml](src/main/resources/com/simplebank/view/cards.fxml),
kontroler [CardsController.java](src/main/java/com/simplebank/controller/CardsController.java).

Wyświetla tabelę kart użytkownika (numer karty w formacie czytelnym, CVV,
ważność, status, powiązane konto). Pozwala **wygenerować nową kartę** dla
wybranego konta — `CardService` losuje unikalny 16-cyfrowy numer, kod CVV oraz
ustawia datę ważności na 4 lata od dziś.

> **[ZDJĘCIE: zakładka Karty z listą kart i przyciskiem generowania]**
>
> *Podpis:* Zakładka Karty — przegląd kart płatniczych przypisanych do kont
> oraz generowanie nowej karty dla wybranego konta.

### 8.8. Zakładka „Kredyty"

Widok [loans.fxml](src/main/resources/com/simplebank/view/loans.fxml),
kontroler [LoansController.java](src/main/java/com/simplebank/controller/LoansController.java).

Umożliwia złożenie **wniosku kredytowego** (kwota + liczba miesięcy spłaty,
maks. 120). `LoanService` wylicza stopę kredytu (stopa bazowa 8% powiększona o
narzut zależny od długości okresu) i zapisuje wniosek ze statusem *OCZEKUJACY*.
Tabela pokazuje kwotę, oprocentowanie, liczbę miesięcy, status, kwotę spłaconą
oraz **całkowitą kwotę do spłaty** (kapitał + odsetki) i datę złożenia.

> **[ZDJĘCIE: zakładka Kredyty z formularzem wniosku i listą kredytów]**
>
> *Podpis:* Zakładka Kredyty — składanie wniosku kredytowego oraz przegląd
> kredytów wraz z wyliczoną całkowitą kwotą do spłaty.

### 8.9. Zakładka „Ustawienia"

Widok [settings.fxml](src/main/resources/com/simplebank/view/settings.fxml),
kontroler [SettingsController.java](src/main/java/com/simplebank/controller/SettingsController.java).

Pozwala **zmienić adres e-mail** (z kontrolą formatu i unikalności) oraz
**zmienić hasło** (wymaga podania i weryfikacji dotychczasowego hasła; nowe
hasło zapisywane jest jako hash z nową solą).

> **[ZDJĘCIE: zakładka Ustawienia ze zmianą e-maila i hasła]**
>
> *Podpis:* Zakładka Ustawienia — zmiana adresu e-mail oraz hasła zalogowanego
> użytkownika. Zmiana hasła wymaga potwierdzenia dotychczasowego.

---

## 9. Bezpieczeństwo i walidacja

- **Hasła** — przechowywane wyłącznie jako hash **SHA-256** liczony z połączenia
  *(sól + hasło)*. Każdy użytkownik ma własną, losową sól (16 bajtów, Base64),
  generowaną przez `SecureRandom`. Zob.
  [PasswordUtil.java](src/main/java/com/simplebank/util/PasswordUtil.java).
- **Walidacja danych** ([ValidationUtil.java](src/main/java/com/simplebank/util/ValidationUtil.java)) —
  sprawdzanie poprawności e-maila (wyrażenie regularne), formatu PESEL (11 cyfr),
  niepustych pól oraz dodatnich kwot.
- **Bezpieczeństwo zapytań** — operacje na bazie wykorzystują `PreparedStatement`
  (parametryzowane zapytania), co chroni przed SQL Injection.
- **Spójność operacji** — przelewy/wpłaty/wypłaty są transakcyjne (commit /
  rollback), a salda blokowane są przy odczycie (`FOR UPDATE`).
- **Obsługa błędów** — błędy biznesowe (`BankException`) prezentowane są
  użytkownikowi w czytelnych okienkach (`AlertUtil`), bez ujawniania szczegółów
  technicznych.

---

## 10. Uruchomienie aplikacji

1. Zainstaluj i uruchom **XAMPP**, włącz moduł **MySQL/MariaDB**.
2. Upewnij się, że masz **JDK 17+** oraz **Maven**.
3. W katalogu projektu wykonaj:

   ```bash
   mvn clean javafx:run
   ```

4. Przy pierwszym uruchomieniu baza `bank_app` zostanie utworzona i wypełniona
   danymi automatycznie.
5. Zaloguj się jednym z kont testowych (patrz niżej) lub zarejestruj nowe.

> **[ZDJĘCIE: aplikacja uruchomiona / okno startowe]**
>
> *Podpis:* Uruchomienie aplikacji poleceniem `mvn javafx:run` — po starcie
> pojawia się ekran logowania.

---

## 11. Dane testowe

Skrypt `002_seeder.sql` wgrywa przykładowych użytkowników. Hasło dla wszystkich
kont testowych to **`haslo123`**.

| E-mail (login) | Hasło | Użytkownik |
|---|---|---|
| `jan.kowalski@example.com` | `haslo123` | Jan Kowalski |
| `anna.nowak@example.com` | `haslo123` | Anna Nowak |
| `piotr.wisniewski@example.com` | `haslo123` | Piotr Wiśniewski |

W bazie znajdują się również przykładowe konta z saldami, karty, kredyty oraz
transakcje, dzięki czemu od razu po zalogowaniu widać działanie aplikacji.

> **[ZDJĘCIE: tabela uzytkownicy w phpMyAdmin z danymi testowymi]**
>
> *Podpis:* Przykładowe dane testowe w tabeli `uzytkownicy` — hasła zapisane są
> wyłącznie w postaci hashu SHA-256 z solą.
