package org.example;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.example.ValidationConstants.REPORT_FILE_PATH;

public class ReportGenerator {
    private static File reportFile;
    private static final String BASE_REPORT_FILE_PATH = REPORT_FILE_PATH;
    private static final String[] HEADER = {"File Path", "Error Message"};
    private static CSVPrinter printer;

    // Initializes the report file and CSVPrinter only once
    public static void initializeReport() throws IOException {
        reportFile = getUniqueReportFile();

        try (FileWriter out = new FileWriter(reportFile, true)) {
            printer = new CSVPrinter(out, CSVFormat.DEFAULT);
            // Add header if the file is new
            if (!reportFile.exists() || reportFile.length() == 0) {
                printer.printRecord((Object[]) HEADER);
            }
        }
    }

    public static void generateReport(String filePath, String errorMessage) throws IOException {
        if (printer == null) {
            throw new IllegalStateException("ReportGenerator is not initialized. Call initializeReport() first.");
        }

        try (FileWriter out = new FileWriter(reportFile, true);
             CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT)) {
            printer.printRecord(filePath, errorMessage); // Print the data
        }
    }

    private static File getUniqueReportFile() {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String newFilePath = BASE_REPORT_FILE_PATH.replace(".csv", "_" + timestamp + ".csv");
        return new File(newFilePath);
    }

    // Closes the CSVPrinter to finalize the report file
    public static void closeReport() throws IOException {
        if (printer != null) {
            printer.close();
            printer = null;
        }
    }
}