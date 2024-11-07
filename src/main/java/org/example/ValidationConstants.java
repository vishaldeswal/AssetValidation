package org.example;

import java.io.File;
import java.util.regex.Pattern;

public class ValidationConstants {

    public static final String parentDirectory;

    static{
        // Get the parent directory
        String currentDir = System.getProperty("user.dir");
        File currentFolder = new File(currentDir);
        parentDirectory = currentFolder.getParent();
    }

    private ValidationConstants() {
        // prevent outside initialization
    }

    public static final String EXCEL_SHEET_PATH=parentDirectory+"\\ValidationTestBook.xlsx";
    public static final String REPORT_FILE_PATH = parentDirectory+"\\assetReport.csv";
    public static final Pattern VALID_FILENAME_PATTERN = Pattern.compile("^[a-z][a-z0-9-]*[a-z0-9]$");

    public static final String ARENA_CAR_MODELS[]= {
            "swift","celerio", "dezire"
    };

    public static final String NEXA_CAR_MODELS[]= {
            "grand-vitara","fronx", "baleno", "ciaz" , "invicto"
    };

    public static  final String CAR_DETAIL_PAGE="CAR DETAIL";
    public static  final String HOMEPAGE="HOMEPAGE";
}
