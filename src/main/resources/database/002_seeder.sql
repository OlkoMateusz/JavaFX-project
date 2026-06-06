/*
 * ============================================================================
 *  Simple Bank - Seeder (dane testowe)
 * ----------------------------------------------------------------------------
 *  Wypełnia bazę bank_app danymi pozwalającymi od razu przetestować aplikację.
 *
 *  WAŻNE: uruchom ten plik DOPIERO PO 001_migration.sql.
 *
 *  Dane logowania wszystkich kont testowych:
 *      hasło: haslo123
 *  (hash SHA-256 z solą został policzony dla tego hasła)
 *
 *  Przykładowe loginy (e-mail):
 *      jan.kowalski@example.com   / haslo123
 *      anna.nowak@example.com     / haslo123
 *      piotr.wisniewski@example.com / haslo123
 * ============================================================================
 */

USE bank_app;

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE transakcje;
TRUNCATE TABLE karty;
TRUNCATE TABLE kredyty;
TRUNCATE TABLE konta_bankowe;
TRUNCATE TABLE typy_transakcji;
TRUNCATE TABLE typ_konta;
TRUNCATE TABLE uzytkownicy;
SET FOREIGN_KEY_CHECKS = 1;

/*
 * Typy kont.
 */
INSERT INTO typ_konta (rodzaj_konta, nazwa, oprocentowanie, mozliwosc_kredytu) VALUES
    (1, 'Konto osobiste',      0.00, TRUE),
    (2, 'Konto oszczędnościowe', 4.50, FALSE),
    (3, 'Konto premium',       1.50, TRUE);

/*
 * Typy transakcji. Czas realizacji podany w minutach.
 */
INSERT INTO typy_transakcji (typ_transakcji, nazwa, czas_realizacji, oplata) VALUES
    (1, 'Przelew',  5,  0.00),
    (2, 'Wpłata',   0,  0.00),
    (3, 'Wypłata',  0,  0.00);

/*
 * Użytkownicy testowi. Hasło dla wszystkich: haslo123
 *   sól (Base64):  U2ltcGxlQmFua1NhbHQh
 *   hash (Base64): V3tDp4B7h8Bp10T6m9GCAYjw6/BR0w/sabt0J0TE6c4=
 */
INSERT INTO uzytkownicy (user_id, imie, nazwisko, pesel, email, haslo_hash, haslo_sol) VALUES
    (1, 'Jan',   'Kowalski',   '90010112345', 'jan.kowalski@example.com',
        'V3tDp4B7h8Bp10T6m9GCAYjw6/BR0w/sabt0J0TE6c4=', 'U2ltcGxlQmFua1NhbHQh'),
    (2, 'Anna',  'Nowak',      '85052254321', 'anna.nowak@example.com',
        'V3tDp4B7h8Bp10T6m9GCAYjw6/BR0w/sabt0J0TE6c4=', 'U2ltcGxlQmFua1NhbHQh'),
    (3, 'Piotr', 'Wiśniewski', '92030367890', 'piotr.wisniewski@example.com',
        'V3tDp4B7h8Bp10T6m9GCAYjw6/BR0w/sabt0J0TE6c4=', 'U2ltcGxlQmFua1NhbHQh');

/*
 * Konta bankowe. Numery kont w formacie 26-cyfrowym.
 */
INSERT INTO konta_bankowe (nr_konta, rodzaj_konta, saldo, user_id, status) VALUES
    ('11114000040000300012345601', 1, 5000.00, 1, 'AKTYWNE'),
    ('22221234123412341234123401', 2, 12000.50, 1, 'AKTYWNE'),
    ('33335000050000500050000502', 1, 800.00, 2, 'AKTYWNE'),
    ('44449999888877776666555503', 3, 25000.00, 3, 'AKTYWNE');

/*
 * Karty płatnicze powiązane z kontami.
 */
INSERT INTO karty (id_konta, nr_karty, cvv, data_waznosci, status) VALUES
    ('11114000040000300012345601', '4539111122223333', '123', '2028-12-31', 'AKTYWNA'),
    ('33335000050000500050000502', '5500666677778888', '456', '2027-06-30', 'AKTYWNA');

/*
 * Przykładowe kredyty.
 */
INSERT INTO kredyty (user_id, kwota, stopa_kredytu, miesiace, status, splacone) VALUES
    (1, 10000.00, 9.50, 24, 'AKTYWNY',   2500.00),
    (3, 50000.00, 8.00, 60, 'OCZEKUJACY', 0.00);

/*
 * Przykładowe transakcje.
 * Przelew z konta Jana na konto Anny, wpłata oraz wypłata.
 */
INSERT INTO transakcje (from_nr_konta, to_nr_konta, kwota, typ_transakcji, data_transakcji) VALUES
    ('11114000040000300012345601', '33335000050000500050000502', 300.00, 1, '2026-05-10 10:15:00'),
    (NULL, '11114000040000300012345601', 1000.00, 2, '2026-05-11 09:00:00'),
    ('11114000040000300012345601', NULL, 200.00, 3, '2026-05-12 17:30:00');
