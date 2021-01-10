package io.mendirl.spring.server.domain;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvIgnore;

public class Puissance {

    @CsvBindByName(column = "Périmètre")
    private String perimeter;

    @CsvBindByName(column = "Nature")
    private String origin;

    @CsvBindByName(column = "Date")
    private String date;

    @CsvBindByName(column = "Heures")
    private String hour;

    @CsvBindByName(column = "Consommation")
    private int consumption;

    @CsvBindByName(column = "Prévision J-1")
    private int forcastprevious;

    @CsvBindByName(column = "Prévision J")
    private int forcast;

    @CsvBindByName(column = "Fioul")
    private int fioul;

    @CsvBindByName(column = "Charbon")
    private int charbon;

    @CsvBindByName(column = "Gaz")
    private int gaz;

    @CsvBindByName(column = "Nucléaire")
    private int nucleaire;

    @CsvBindByName(column = "Eolien")
    private int eolien;

    @CsvBindByName(column = "Solaire")
    private int solaire;

    @CsvBindByName(column = "Hydraulique")
    private int hydraulique;

    @CsvBindByName(column = "Pompage")
    private int pompage;

    @CsvBindByName(column = "Bioénergies")
    private int bioenergie;

    @CsvBindByName(column = "Ech. physiques")
    private int echangephysique;

    @CsvBindByName(column = "Taux de Co2")
    private int tauxco2;

    @CsvBindByName(column = "Ech. comm. Angleterre")
    private int exchange1;

    @CsvBindByName(column = "Ech. comm. Espagne")
    private int exchange2;

    @CsvBindByName(column = "Ech. comm. Italie")
    private int exchange3;

    @CsvBindByName(column = "Ech. comm. Suisse")
    private int exchange4;

    @CsvBindByName(column = "Ech. comm. Allemagne-Belgique")
    private int exchange5;

    @CsvBindByName(column = "Ech. comm. Angleterre")
    private int exchange6;

    @CsvBindByName(column = "Fioul - TAC")
    private int fioul1;

    @CsvBindByName(column = "Fioul - Cogén.")
    private int fioul2;

    @CsvBindByName(column = "Fioul - Autres")
    private int fioul3;

    @CsvBindByName(column = "Gaz - TAC")
    private int gaz1;

    @CsvBindByName(column = "Gaz - Cogén.")
    private int gaz2;

    @CsvBindByName(column = "Gaz - CCG")
    private int gaz3;

    @CsvBindByName(column = "Gaz - Autres")
    private int gaz4;

    @CsvBindByName(column = "Hydraulique - Fil de l?eau + éclusée")
    private int hydro1;

    @CsvBindByName(column = "Hydraulique - Lacs")
    private int hydro2;

    @CsvBindByName(column = "Hydraulique - STEP turbinage")
    private int hydro3;

    @CsvBindByName(column = "Bioénergies - Déchets")
    private int bioenergie1;

    @CsvBindByName(column = "Bioénergies - Biomasse")
    private int bioenergie2;

    @CsvBindByName(column = "Bioénergies - Biogaz")
    private int bioenergie3;

    @CsvBindByName(column = "Consommation corrigée")
    private int consumptionFixed;

    @CsvBindByName(column = "")
    @CsvIgnore
    private String empty;
}
