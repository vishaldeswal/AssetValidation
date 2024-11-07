package org.example;

import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageInfo;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.example.ValidationConstants.*;

public class Validator {
    public void validateImage(String pageCategory, String filePath, int expectedWidth, int expectedHeight) throws IOException {
        boolean isValidationFailed =false;

        File file = new File(filePath);
        String name=file.getName();
        String extension=name.substring(name.lastIndexOf('.') + 1).toLowerCase();

        try {
            //Validate Paths of Images
            if(!isValidationFailed && !validatePath(pageCategory,filePath)){
                isValidationFailed=true;
                ReportGenerator.generateReport(filePath, "Error: The file path must include either 'nexa' or 'arena' (e.g., /nexa/ or /arena/). For the card-details page, the path should also contain the specific car model (e.g., /grand-vitara/).");
            }



            // Validate Size of Image
            if (!isValidationFailed && !validateImageSize(file)) {
                isValidationFailed=true;
                ReportGenerator.generateReport(filePath, String.format("Error: File size exceeds the allowed limit for this format(%s). Allowed Limit:(%d)",
                        extension, ImageFormats.valueOf(extension.toUpperCase()).getMaxSize()));
            }

            //Validate Name of Image File
            if(!isValidationFailed && !validateFilename(file)){
                isValidationFailed=true;
                ReportGenerator.generateReport(filePath, String.format("Error: Invalid filename '%s'. Filename must contain only lowercase alphabets, numbers, and hyphens, " +
                        "start with an alphabet, and not start or end with a hyphen.", name));
            }



            //Validate Expect Ratio of All Image formats Except SVG

            if(!isValidationFailed && !extension.equalsIgnoreCase(ImageFormats.SVG.name())){
                ImageInfo imageInfo = Imaging.getImageInfo(file);
                int actualWidth = imageInfo.getWidth();
                int actualHeight = imageInfo.getHeight();

                double expectedAspectRatio = (double) expectedWidth / expectedHeight;
                double actualAspectRatio = (double) actualWidth / actualHeight;

                expectedAspectRatio = toTwoDecimalPlaces(expectedAspectRatio);
                actualAspectRatio = toTwoDecimalPlaces(actualAspectRatio);

                if (actualAspectRatio != expectedAspectRatio) {
                    ReportGenerator.generateReport(filePath, String.format(
                            "Invalid image dimensions. Expected AR: %.2f, Actual AR: %.2f",
                            expectedAspectRatio, actualAspectRatio));
                }
            }

        } catch (ImageReadException e) {
            ReportGenerator.generateReport(filePath, "Error reading image");
        }

    }

    public void validateVideo(String pageCategory, String filePath) {
        boolean isValidationFailed =false;

        File file = new File(filePath);
        String name=file.getName();
        String extension=name.substring(name.lastIndexOf('.') + 1).toLowerCase();

        try {
            if (!isValidationFailed && !validatePath(pageCategory, filePath)) {
                isValidationFailed=true;
                ReportGenerator.generateReport(filePath, "Error: The file path must include either 'nexa' or 'arena' (e.g., /nexa/ or /arena/). For the card-details page, the path should also contain the specific car model (e.g., /grand-vitara/).");
            }

            if (!isValidationFailed && !validateVideoSize(file)) {
                isValidationFailed=true;
                ReportGenerator.generateReport(filePath, String.format("Error: File size exceeds the allowed limit for this format(%s). Allowed Limit:(%d)",
                        extension, VideoFormats.valueOf(extension.toUpperCase()).getMaxSize()));
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public double toTwoDecimalPlaces(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private boolean validateImageSize(File file) {
        long fileSize = file.length();
        String name=file.getName();
        String extension=name.substring(name.lastIndexOf('.') + 1).toLowerCase();
        for (ImageFormats format : ImageFormats.values()) {
            if (extension.equalsIgnoreCase(format.name())) {
                return fileSize <= format.getMaxSize();
            }
        }
        return false;
    }

    private boolean validateVideoSize(File file) {
        String name=file.getName();
        String extension=name.substring(name.lastIndexOf('.') + 1).toLowerCase();
        long fileSize = file.length();
        for (VideoFormats format : VideoFormats.values()) {
            if (extension.equalsIgnoreCase(format.name())) {
                    return fileSize <= format.getMaxSize();
            }
        }
        return false;
    }

    public boolean validateFilename(File file) {
        String filename = file.getName();

        if (filename == null || filename.isEmpty()) {
            return false;
        }

        // Split the filename and extension
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return false; // No extension or empty extension
        }

        String namePart = filename.substring(0, lastDotIndex);

        // Validate the name part with the regex
        if (!VALID_FILENAME_PATTERN.matcher(namePart).matches()) {
            return false;
        }

       return true;
    }


    public boolean validatePath(String pageCategory, String path) {
        int nexaIndex = path.indexOf("nexa");
        int arenaIndex = path.indexOf("arena");
        // Check car models only if "nexa" or "arena" is present
        // Check for "nexa" and corresponding car models
        if(pageCategory.equalsIgnoreCase(CAR_DETAIL_PAGE)){
            if (nexaIndex != -1) {
                return Arrays.stream(NEXA_CAR_MODELS)
                        .anyMatch(model -> path.indexOf(model, nexaIndex) > nexaIndex);
            }
            // Check for "arena" and corresponding car models
            else if (arenaIndex != -1) {
                return Arrays.stream(ARENA_CAR_MODELS)
                        .anyMatch(model -> path.indexOf(model, arenaIndex) > arenaIndex);
            }

        }

        return nexaIndex>0 || arenaIndex>0;

    }





}