package org.example;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.example.ValidationConstants.EXCEL_SHEET_PATH;

public class ExcelReader {

    public static void findAllFiles(ExcelRowBean row) {
        String path = row.getAssetFolderPath();
        File directory = new File(path);

        if (!directory.exists()) {
            System.out.println("The specified path does not exist: " + path);
            try {
                ReportGenerator.generateReport(path, "The specified path does not exist: " + path);
            } catch (IOException e) {
                // throw new RuntimeException(e);
            }
            return;
        }

        if (!directory.isDirectory()) {
            System.out.println("The specified path is not a directory: " + path);
            try {
                ReportGenerator.generateReport(path, "The specified path does not exist: " + path);
            } catch (IOException e) {
                //
            }
            return;
        }

        // Call the recursive function to process files and validate them
        listFilesRecursively(row);
    }

    private static void listFilesRecursively(ExcelRowBean row) {
        String path = row.getAssetFolderPath();
        File directory = new File(path);
        File[] files = directory.listFiles();

        if (files != null) {
            if(files.length==0){
                try {
                    ReportGenerator.generateReport(path, "No Assets present in this folder: " + path);
                } catch (IOException e) {
                    //
                }
            }
            for (File file : files) {
                if (file.isDirectory()) {
                    ExcelRowBean newRow = new ExcelRowBean();
                    newRow.setPageType(row.getPageType());
                    newRow.setComponentName(row.getComponentName());
                    newRow.setAssetFolderPath(file.getAbsolutePath());
                    newRow.setExpectedWidth(row.getExpectedWidth());
                    newRow.setExpectedHeight(row.getExpectedHeight());

                    listFilesRecursively(newRow);
                } else {
                    System.out.println("File:"+ file.getAbsolutePath());
                    validateFile(row, file);
                }
            }
        }
    }

    private static void validateFile(ExcelRowBean row, File file) {
        String filePath = file.getAbsolutePath();
        String fileExtension = getFileExtension(filePath);
        Validator validator = new Validator();

        try {
            if (isImageFile(fileExtension)) {
                validator.validateImage(row.getPageType(), filePath, row.getExpectedWidth(), row.getExpectedHeight());
            } else if (isVideoFile(fileExtension)) {
                validator.validateVideo(row.getPageType(), filePath);
            } else {
                ReportGenerator.generateReport(filePath, "Invalid File Extension of file.");
            }
        } catch (Exception e) {
            //ReportGenerator.generateReport(filePath, "Validation failed: " + e.getMessage());
        }
    }

    // Helper method to extract file extension
    private static String getFileExtension(String filePath) {
        int dotIndex = filePath.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filePath.substring(dotIndex + 1);
    }

    private static boolean isImageFile(String extension) {
        for (ImageFormats format : ImageFormats.values()) {
            if (extension.equalsIgnoreCase(format.name())) {
                return true;
            }
        }
        return false;
    }

    private static boolean isVideoFile(String extension) {
        for (VideoFormats format : VideoFormats.values()) {
            if (extension.equalsIgnoreCase(format.name())) {
                return true;
            }
        }
        return false;
    }
    public static void main(String[] args) {



    }
    private static Workbook loadWorkbook(String path) throws IOException {
        try (FileInputStream fis = new FileInputStream(path)) {
            return new XSSFWorkbook(fis);
        }
    }

    private static List<String> readCarModels(Workbook workbook, String brand) {
        List<String> carModels = new ArrayList<>();
        Sheet sheet = workbook.getSheetAt(1);
        int brandCol = -1;

        Row headerRow = sheet.getRow(0);
        for (Cell cell : headerRow) {
            if (cell.getStringCellValue().equalsIgnoreCase(brand)) {
                brandCol = cell.getColumnIndex();
                break;
            }
        }

        if (brandCol == -1) {
            throw new RuntimeException("Brand column not found for " + brand);
        }

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                Cell cell = row.getCell(brandCol);
                if (cell != null && !cell.getStringCellValue().isEmpty()) {
                    carModels.add(cell.getStringCellValue());
                }
            }
        }
        return carModels;
    }

    private static List<ExcelRowBean> readDummyPaths(Workbook workbook) {
        List<ExcelRowBean> excelData = new ArrayList<>();
        Sheet sheet = workbook.getSheetAt(0);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            ExcelRowBean excelRowBean = new ExcelRowBean();
            if (row != null) {
                Cell pageTypeCell = row.getCell(0);
                Cell componetNameCell = row.getCell(1);
                Cell filePathCell = row.getCell(2);
                Cell expectedWidthCell = row.getCell(3);
                Cell expectedHeightCell = row.getCell(4); // Assuming the file path is in the second column
                if (pageTypeCell != null && !pageTypeCell.getStringCellValue().isEmpty()) {
                    excelRowBean.setPageType(pageTypeCell.getStringCellValue());
                }
                if (componetNameCell != null && !componetNameCell.getStringCellValue().isEmpty()) {
                    excelRowBean.setComponentName(componetNameCell.getStringCellValue());
                }
                if (filePathCell != null && !filePathCell.getStringCellValue().isEmpty()) {
                    excelRowBean.setAssetFolderPath(filePathCell.getStringCellValue());
                }
                if (expectedWidthCell != null && !Double.isNaN(expectedWidthCell.getNumericCellValue())) {
                    excelRowBean.setExpectedWidth((int)expectedWidthCell.getNumericCellValue());
                }

                if (expectedHeightCell != null && !Double.isNaN(expectedHeightCell.getNumericCellValue())) {
                    excelRowBean.setExpectedHeight((int)expectedHeightCell.getNumericCellValue());
                }

                excelData.add(excelRowBean);
            }
        }
        return excelData;
    }

    private static List<ExcelRowBean> generatePaths(String brand, String carModel, List<ExcelRowBean> excelRowBeans ) {
        List<ExcelRowBean> allPossiblePathBeans = new ArrayList<>();


        // Get the parent directory
        String currentDir = System.getProperty("user.dir");
        File currentFolder = new File(currentDir);
        String parentDir = currentFolder.getParent();

        for (ExcelRowBean excelRowBean : excelRowBeans) {
            String dummyPath = excelRowBean.getAssetFolderPath();
            if(dummyPath.contains(brand)){
                ExcelRowBean pathBean = new ExcelRowBean();
                String path = dummyPath.replace("${carModelName}", carModel);
                pathBean.setAssetFolderPath(parentDir+path);

                pathBean.setPageType(excelRowBean.getPageType());
                pathBean.setComponentName(excelRowBean.getComponentName());
                pathBean.setExpectedWidth(excelRowBean.getExpectedWidth());
                pathBean.setExpectedHeight(excelRowBean.getExpectedHeight());
                allPossiblePathBeans.add(pathBean);
            }
        }
        return allPossiblePathBeans;
    }


    public void readExcel(String filePath) throws IOException {
        // printAllFiles("C:\\VishalDrive\\AEM-NEXA\\AssetValidation\\assetMigration\\nexa\\cars\\models\\grand-vitara\\car-detail-page");

        try {

            ReportGenerator.initializeReport();
            Workbook workbook = loadWorkbook(EXCEL_SHEET_PATH);
            List<String> nexaCarModels = readCarModels(workbook, "NEXA");
            List<String> arenaCarModels = readCarModels(workbook, "ARENA");
            List<ExcelRowBean> excelRowBeans = readDummyPaths(workbook);

            List<ExcelRowBean> finalPaths = new ArrayList<>();

            for (String carModel : nexaCarModels) {
                List<ExcelRowBean> allNexaFolder = generatePaths("nexa", carModel, excelRowBeans);
                finalPaths.addAll(allNexaFolder);
            }

            for (String carModel : arenaCarModels) {
                List<ExcelRowBean> allArenaFolder = generatePaths("arena", carModel, excelRowBeans);
                finalPaths.addAll(allArenaFolder);
            }


            for(ExcelRowBean row : finalPaths){
                findAllFiles(row);
            }


            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to process the Excel file", e);
        }
    }

}