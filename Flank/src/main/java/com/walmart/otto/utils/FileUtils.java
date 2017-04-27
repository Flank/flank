package com.walmart.otto.utils;

import com.walmart.otto.Constants;
import org.w3c.dom.Document;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileUtils {

    public static void cleanUpDuplicateXmlFiles(String directory) {
        List<String> xmlFiles = new ArrayList<>();

        File currentDir = new File("");
        File[] resultXmls = new File(currentDir.getAbsolutePath() + "/" + directory).listFiles();

        for (File file : resultXmls) {

            if (!file.getName().contains("xml")) {
                continue;
            }
            Document resultsDocument = XMLUtils.getXMLFile(file.getAbsolutePath());

            if (resultsDocument == null) {
                continue;
            }

            String text = XMLUtils.getText(resultsDocument).toString();

            if (xmlFiles.contains(text)) {
                file.delete();
            } else {
                xmlFiles.add(text);
            }
        }
    }


    public static boolean containsText(String text) {
        File file = new File(Constants.TEST_TIME_FILE);
        Scanner scanner = null;

        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException ignored) {
            return false;
        }

        while (scanner.hasNextLine()) {
            final String lineFromFile = scanner.nextLine();

            if (lineFromFile.replaceAll("\\d", "").trim().equals(text)) {
                scanner.close();
                return true;
            }
        }
        scanner.close();
        return false;
    }

    public static void writeToShardFile(String text, String timeToComplete) {
        //Return if test case is already there
        if (containsText(text.trim())) {
            return;
        }

        if (text.charAt(0) == ' ') {
            text = text.substring(1, text.length());
        }

        File file = new File(Constants.TEST_TIME_FILE);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
            writer.write(text + " " + timeToComplete + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}