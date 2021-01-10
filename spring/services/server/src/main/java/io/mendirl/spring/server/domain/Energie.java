package io.mendirl.spring.server.domain;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvIgnore;
import lombok.Data;

@Data
public class Energie {

    @CsvBindByName(column = "Mois")
    private String date;

    @CsvBindByName(column = "Année")
    private String year;

    @CsvBindByName(column = "Qualité")
    private String quality;

    @CsvBindByName(column = "Territoire")
    private String zone;

    @CsvBindByName(column = "Production totale")
    private int productionTotal;

    @CsvBindByName(column = "Production nucléaire")
    private int production1;

    @CsvBindByName(column = "Production thermique totale")
    private int production2;

    @CsvBindByName(column = "Production thermique charbon")
    private int production3;

    @CsvBindByName(column = "Production thermique fioul")
    private int production4;

    @CsvBindByName(column = "Production thermique gaz")
    private int production5;

    @CsvBindByName(column = "Production hydraulique")
    private int production6;

    @CsvBindByName(column = "Production éolien")
    private int production7;

    @CsvBindByName(column = "Production solaire")
    private int production8;

    @CsvBindByName(column = "Production bioénergies")
    private int production9;

    @CsvBindByName(column = "Consommation totale")
    private int consumptionTotal;

    @CsvBindByName(column = "Solde exportateur")
    private int soldeExport;

    @CsvBindByName(column = "Echanges export")
    private int exchangeExport;

    @CsvBindByName(column = "Echanges import")
    private int exchangeImport;

    @CsvBindByName(column = "Echanges avec le Royaume-Uni")
    private int exchange1;

    @CsvBindByName(column = "Echanges avec l'Espagne")
    private int exchange2;

    @CsvBindByName(column = "Echanges avec l'Italie")
    private int exchange3;

    @CsvBindByName(column = "Echanges avec la Suisse")
    private int exchange4;

    @CsvBindByName(column = "Echanges avec l'Allemagne et la Belgique")
    private int exchange5;

}
