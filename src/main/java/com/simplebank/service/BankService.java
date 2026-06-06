package com.simplebank.service;

import com.simplebank.dao.BankAccountDAO;
import com.simplebank.dao.TransactionDAO;
import com.simplebank.model.BankAccount;
import com.simplebank.model.Transaction;
import com.simplebank.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/*
 * Serwis realizujący główne operacje bankowe: przelew, wpłatę oraz wypłatę.
 *
 * Operacje modyfikujące salda są wykonywane w obrębie jednej transakcji SQL.
 * W przypadku jakiegokolwiek błędu transakcja jest wycofywana (rollback),
 * dzięki czemu stan kont pozostaje spójny.
 */
public class BankService {

    private static final int TYP_PRZELEW = 1;
    private static final int TYP_WPLATA = 2;
    private static final int TYP_WYPLATA = 3;

    private final BankAccountDAO accountDAO = new BankAccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    public List<BankAccount> getAccounts(int userId) {
        return accountDAO.findByUserId(userId);
    }

    public List<Transaction> getTransactions(int userId) {
        return transactionDAO.findByUserId(userId);
    }

    /*
     * Wykonuje przelew z konta nadawcy na konto odbiorcy. Sprawdza poprawność
     * kwoty, istnienie konta odbiorcy oraz dostępność środków. Całość przebiega
     * w transakcji SQL z zatwierdzeniem lub wycofaniem zmian.
     */
    public void transfer(String fromNrKonta, String toNrKonta, BigDecimal kwota) {
        if (kwota == null || kwota.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BankException("Kwota przelewu musi być dodatnia.");
        }
        if (toNrKonta == null || toNrKonta.trim().isEmpty()) {
            throw new BankException("Podaj numer konta odbiorcy.");
        }
        if (fromNrKonta.equals(toNrKonta.trim())) {
            throw new BankException("Nie można wykonać przelewu na to samo konto.");
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            BigDecimal saldoNadawcy = accountDAO.getSaldoForUpdate(conn, fromNrKonta);
            if (saldoNadawcy == null) {
                throw new BankException("Konto nadawcy nie istnieje.");
            }

            BigDecimal saldoOdbiorcy = accountDAO.getSaldoForUpdate(conn, toNrKonta.trim());
            if (saldoOdbiorcy == null) {
                throw new BankException("Konto odbiorcy nie istnieje.");
            }

            if (saldoNadawcy.compareTo(kwota) < 0) {
                throw new BankException("Niewystarczające środki na koncie.");
            }

            accountDAO.updateSaldo(conn, fromNrKonta, saldoNadawcy.subtract(kwota));
            accountDAO.updateSaldo(conn, toNrKonta.trim(), saldoOdbiorcy.add(kwota));

            Transaction t = new Transaction();
            t.setFromNrKonta(fromNrKonta);
            t.setToNrKonta(toNrKonta.trim());
            t.setKwota(kwota);
            t.setTypTransakcji(TYP_PRZELEW);
            transactionDAO.insert(conn, t);

            conn.commit();
        } catch (SQLException e) {
            rollback(conn);
            throw new BankException("Wystąpił błąd podczas przelewu. Operacja została wycofana.");
        } catch (BankException e) {
            rollback(conn);
            throw e;
        } finally {
            zamknij(conn);
        }
    }

    /*
     * Realizuje wpłatę gotówki na wskazane konto.
     */
    public void deposit(String nrKonta, BigDecimal kwota) {
        if (kwota == null || kwota.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BankException("Kwota wpłaty musi być dodatnia.");
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            BigDecimal saldo = accountDAO.getSaldoForUpdate(conn, nrKonta);
            if (saldo == null) {
                throw new BankException("Konto nie istnieje.");
            }

            accountDAO.updateSaldo(conn, nrKonta, saldo.add(kwota));

            Transaction t = new Transaction();
            t.setFromNrKonta(null);
            t.setToNrKonta(nrKonta);
            t.setKwota(kwota);
            t.setTypTransakcji(TYP_WPLATA);
            transactionDAO.insert(conn, t);

            conn.commit();
        } catch (SQLException e) {
            rollback(conn);
            throw new BankException("Wystąpił błąd podczas wpłaty. Operacja została wycofana.");
        } catch (BankException e) {
            rollback(conn);
            throw e;
        } finally {
            zamknij(conn);
        }
    }

    /*
     * Realizuje wypłatę gotówki z wskazanego konta po sprawdzeniu salda.
     */
    public void withdraw(String nrKonta, BigDecimal kwota) {
        if (kwota == null || kwota.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BankException("Kwota wypłaty musi być dodatnia.");
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            BigDecimal saldo = accountDAO.getSaldoForUpdate(conn, nrKonta);
            if (saldo == null) {
                throw new BankException("Konto nie istnieje.");
            }
            if (saldo.compareTo(kwota) < 0) {
                throw new BankException("Niewystarczające środki na koncie.");
            }

            accountDAO.updateSaldo(conn, nrKonta, saldo.subtract(kwota));

            Transaction t = new Transaction();
            t.setFromNrKonta(nrKonta);
            t.setToNrKonta(null);
            t.setKwota(kwota);
            t.setTypTransakcji(TYP_WYPLATA);
            transactionDAO.insert(conn, t);

            conn.commit();
        } catch (SQLException e) {
            rollback(conn);
            throw new BankException("Wystąpił błąd podczas wypłaty. Operacja została wycofana.");
        } catch (BankException e) {
            rollback(conn);
            throw e;
        } finally {
            zamknij(conn);
        }
    }

    private void rollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ignored) {
            }
        }
    }

    private void zamknij(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException ignored) {
            }
        }
    }
}
