package com.helloworldconsulting;


import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class App {

    /*public static final String MAIL = "linkedintesting83@gmail.com";
    public static final String PASSWORD = "";*/

    public static String MAIL;
    public static String PASSWORD;
    public static String csvFile;
    public static String propertiesFile;
    public static List<String> urlsList;
    public static final String POST = "https://www.youtube.com/watch?v=GagctuPfJcg";
    public static final String TITLE = "                                                   " +
                                       "                           Why was Default Method introduced in Java Interfaces | And how's it different from Abstract Classes";
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }


    public static void setConfigurations(String[]args) throws IOException, CsvException {

        for(String arg : args ) {
            if(arg.endsWith(".properties")) { // .properties
                propertiesFile = arg;
            }else if(arg.endsWith(".csv")) {
                csvFile = arg;
            }
        }

        if(!csvFile.isEmpty()) {
            CSVReader csvReader = new CSVReader(new FileReader(csvFile));
            List<String[]> gropus = csvReader.readAll();
            urlsList = new ArrayList<>();
            for(String[] row : gropus) {
                System.out.println("Row  "+row[0]);
                urlsList.add(row[0]);
            }
        }

        if (!propertiesFile.isEmpty()){
           PropertiesModel pm =  PropertiesLoader.setProperty(propertiesFile);
           MAIL = pm.getMail();
           PASSWORD = pm.getPassword();
        }

    }

}



