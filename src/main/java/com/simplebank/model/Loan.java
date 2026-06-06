package com.simplebank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
 * Model reprezentujący kredyt (tabela kredyty).
 * Przechowuje kwotę kredytu, oprocentowanie, liczbę miesięcy spłaty,
 * status oraz kwotę już spłaconą.
 */
public class Loan {

    private int idKredytu;
    private int userId;
    private BigDecimal kwota;
    private BigDecimal stopaKredytu;
    private int miesiace;
    private String status;
    private BigDecimal splacone;
    private LocalDateTime dataKredytu;

    public Loan() {
    }

    public int getIdKredytu() {
        return idKredytu;
    }

    public void setIdKredytu(int idKredytu) {
        this.idKredytu = idKredytu;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BigDecimal getKwota() {
        return kwota;
    }

    public void setKwota(BigDecimal kwota) {
        this.kwota = kwota;
    }

    public BigDecimal getStopaKredytu() {
        return stopaKredytu;
    }

    public void setStopaKredytu(BigDecimal stopaKredytu) {
        this.stopaKredytu = stopaKredytu;
    }

    public int getMiesiace() {
        return miesiace;
    }

    public void setMiesiace(int miesiace) {
        this.miesiace = miesiace;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getSplacone() {
        return splacone;
    }

    public void setSplacone(BigDecimal splacone) {
        this.splacone = splacone;
    }

    public LocalDateTime getDataKredytu() {
        return dataKredytu;
    }

    public void setDataKredytu(LocalDateTime dataKredytu) {
        this.dataKredytu = dataKredytu;
    }

    /*
     * Zwraca całkowitą kwotę do spłaty wraz z odsetkami, wyliczoną w sposób
     * uproszczony jako kwota powiększona o oprocentowanie roczne rozłożone
     * na zadeklarowaną liczbę miesięcy.
     */
    public BigDecimal getCalkowitaKwota() {
        if (kwota == null || stopaKredytu == null) {
            return kwota;
        }
        BigDecimal lata = new BigDecimal(miesiace).divide(new BigDecimal(12), 4, java.math.RoundingMode.HALF_UP);
        BigDecimal odsetki = kwota
                .multiply(stopaKredytu).divide(new BigDecimal(100), 4, java.math.RoundingMode.HALF_UP)
                .multiply(lata);
        return kwota.add(odsetki).setScale(2, java.math.RoundingMode.HALF_UP);
    }
}
