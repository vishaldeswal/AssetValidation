package org.example;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.example.ValidationConstants.EXCEL_SHEET_PATH;


// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        try {
            // Initialize the report
            ReportGenerator.initializeReport();

            ExcelReader reader = new ExcelReader();
            reader.readExcel(EXCEL_SHEET_PATH);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Finalize the report
            try {
                ReportGenerator.closeReport();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}