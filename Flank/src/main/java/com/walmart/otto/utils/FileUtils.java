package com.walmart.otto.utils;

import com.walmart.otto.Constants;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;
import java.util.regex.Pattern;

public class FileUtils {

  public static String getSimpleName(String file) {
    String[] parts = file.split(Pattern.quote(File.separator));
    return parts[parts.length - 1];
  }

  public static boolean fileExists(String filePath) {
    return new File(filePath).exists();
  }

  public static boolean fileExists(Path filePath) {
    return filePath.toFile().exists();
  }

  public static boolean containsText(String text) {
    File file = new File(Constants.TEST_TIME_FILE);
    Scanner scanner = null;

    try {
      scanner = new Scanner(file, "UTF-8");
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
    try (BufferedWriter writer =
        Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
      writer.write(text + " " + timeToComplete + "\n");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
