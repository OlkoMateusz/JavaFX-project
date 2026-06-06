/*
 * ============================================================================
 *  Simple Bank - Migracja bazy danych
 * ----------------------------------------------------------------------------
 *  Ten skrypt tworzy bazę danych "bank_app" wraz ze wszystkimi tabelami,
 *  kluczami głównymi, kluczami obcymi oraz ograniczeniami UNIQUE.
 *  Struktura powstała na podstawie ERD i została lekko rozbudowana o pola
 *  potrzebne do poprawnego działania aplikacji (sól hasła, statusy, daty).
 *
 *  Kolejność uruchamiania w phpMyAdmin:
 *    1) 001_migration.sql   (ten plik)
 *    2) 002_seeder.sql      (dane testowe)
 * ============================================================================
 */

CREATE DATABASE IF NOT EXISTS bank_app
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_polish_ci;

USE bank_app;

/*
 * Wyłączenie kontroli kluczy obcych na czas tworzenia/porządkowania tabel
 * oraz usunięcie istniejących tabel, aby skrypt można było uruchomić ponownie.
 */
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS transakcje;
DROP TABLE IF EXISTS karty;
DROP TABLE IF EXISTS kredyty;
DROP TABLE IF EXISTS konta_bankowe;
DROP TABLE IF EXISTS typy_transakcji;
DROP TABLE IF EXISTS typ_konta;
DROP TABLE IF EXISTS uzytkownicy;
SET FOREIGN_KEY_CHECKS = 1;

/*
 * Tabela słownikowa typów kont.
 * Określa nazwę produktu bankowego, jego oprocentowanie oraz informację,
 * czy dany typ konta umożliwia zaciąganie kredytu.
 */
CREATE TABLE typ_konta (
    rodzaj_konta      INT             NOT NULL AUTO_INCREMENT,
    nazwa             VARCHAR(60)     NOT NULL,
    oprocentowanie    DECIMAL(5,2)    NOT NULL DEFAULT 0.00,
    mozliwosc_kredytu BOOLEAN         NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_typ_konta PRIMARY KEY (rodzaj_konta),
    CONSTRAINT uq_typ_konta_nazwa UNIQUE (nazwa)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*
 * Tabela użytkowników aplikacji.
 * Hasło jest przechowywane wyłącznie jako hash SHA-256 wraz z indywidualną
 * solą (haslo_sol). PESEL oraz e-mail muszą być unikalne.
 */
CREATE TABLE uzytkownicy (
    user_id          INT          NOT NULL AUTO_INCREMENT,
    imie             VARCHAR(50)  NOT NULL,
    nazwisko         VARCHAR(50)  NOT NULL,
    pesel            CHAR(11)     NOT NULL,
    email            VARCHAR(120) NOT NULL,
    haslo_hash       VARCHAR(128) NOT NULL,
    haslo_sol        VARCHAR(64)  NOT NULL,
    data_utworzenia  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_uzytkownicy PRIMARY KEY (user_id),
    CONSTRAINT uq_uzytkownicy_email UNIQUE (email),
    CONSTRAINT uq_uzytkownicy_pesel UNIQUE (pesel)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*
 * Tabela kont bankowych.
 * Numer konta (nr_konta) jest kluczem głównym i jednocześnie identyfikatorem
 * używanym w przelewach. Każde konto należy do jednego użytkownika i ma
 * przypisany typ z tabeli typ_konta oraz status.
 */
CREATE TABLE konta_bankowe (
    nr_konta         VARCHAR(26)  NOT NULL,
    rodzaj_konta     INT          NOT NULL,
    saldo            DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    user_id          INT          NOT NULL,
    status           ENUM('AKTYWNE','ZABLOKOWANE','ZAMKNIETE') NOT NULL DEFAULT 'AKTYWNE',
    data_utworzenia  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_konta_bankowe PRIMARY KEY (nr_konta),
    CONSTRAINT fk_konta_uzytkownik
        FOREIGN KEY (user_id) REFERENCES uzytkownicy (user_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_konta_typ
        FOREIGN KEY (rodzaj_konta) REFERENCES typ_konta (rodzaj_konta)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*
 * Tabela słownikowa typów transakcji.
 * Przechowuje nazwę operacji, jej czas realizacji (w minutach) oraz
 * ewentualną opłatę. Wartości te są wykorzystywane przy zapisie transakcji.
 */
CREATE TABLE typy_transakcji (
    typ_transakcji   INT          NOT NULL AUTO_INCREMENT,
    nazwa            VARCHAR(60)  NOT NULL,
    czas_realizacji  INT          NOT NULL DEFAULT 0,
    oplata           DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT pk_typy_transakcji PRIMARY KEY (typ_transakcji),
    CONSTRAINT uq_typy_transakcji_nazwa UNIQUE (nazwa)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*
 * Tabela transakcji.
 * Kolumny from_nr_konta i to_nr_konta mogą być NULL, ponieważ:
 *  - wpłata gotówki nie ma konta nadawcy,
 *  - wypłata gotówki nie ma konta odbiorcy.
 * Przelew wewnętrzny uzupełnia obie kolumny.
 */
CREATE TABLE transakcje (
    id_transakcji    INT          NOT NULL AUTO_INCREMENT,
    from_nr_konta    VARCHAR(26)  NULL,
    to_nr_konta      VARCHAR(26)  NULL,
    kwota            DECIMAL(15,2) NOT NULL,
    typ_transakcji   INT          NOT NULL,
    data_transakcji  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_transakcje PRIMARY KEY (id_transakcji),
    CONSTRAINT fk_transakcje_from
        FOREIGN KEY (from_nr_konta) REFERENCES konta_bankowe (nr_konta)
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_transakcje_to
        FOREIGN KEY (to_nr_konta) REFERENCES konta_bankowe (nr_konta)
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_transakcje_typ
        FOREIGN KEY (typ_transakcji) REFERENCES typy_transakcji (typ_transakcji)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*
 * Tabela kart płatniczych.
 * Każda karta jest powiązana z konkretnym kontem bankowym. Numer karty
 * jest unikalny. Dodano status karty na potrzeby aplikacji.
 */
CREATE TABLE karty (
    id_karty       INT          NOT NULL AUTO_INCREMENT,
    id_konta       VARCHAR(26)  NOT NULL,
    nr_karty       CHAR(16)     NOT NULL,
    cvv            CHAR(3)      NOT NULL,
    data_waznosci  DATE         NOT NULL,
    status         ENUM('AKTYWNA','ZABLOKOWANA') NOT NULL DEFAULT 'AKTYWNA',
    CONSTRAINT pk_karty PRIMARY KEY (id_karty),
    CONSTRAINT uq_karty_nr UNIQUE (nr_karty),
    CONSTRAINT fk_karty_konto
        FOREIGN KEY (id_konta) REFERENCES konta_bankowe (nr_konta)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*
 * Tabela kredytów.
 * Przechowuje wnioski i aktywne kredyty użytkowników. Pole splacone
 * informuje, jaka część kredytu została już spłacona.
 */
CREATE TABLE kredyty (
    id_kredytu     INT          NOT NULL AUTO_INCREMENT,
    user_id        INT          NOT NULL,
    kwota          DECIMAL(15,2) NOT NULL,
    stopa_kredytu  DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    miesiace       INT          NOT NULL,
    status         ENUM('OCZEKUJACY','AKTYWNY','SPLACONY','ODRZUCONY') NOT NULL DEFAULT 'OCZEKUJACY',
    splacone       DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    data_kredytu   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_kredyty PRIMARY KEY (id_kredytu),
    CONSTRAINT fk_kredyty_uzytkownik
        FOREIGN KEY (user_id) REFERENCES uzytkownicy (user_id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
