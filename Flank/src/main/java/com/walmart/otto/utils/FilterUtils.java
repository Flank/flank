package com.walmart.otto.utils;

import java.util.ArrayList;
import java.util.List;

public class FilterUtils {

  public static List<String> filterTests(
      List<String> testNames, List<String> skipNames, String packageName) {
    List<String> filteredTests = new ArrayList<>();

    for (String testName : testNames) {
      if (testName.startsWith(packageName) && !shouldSkip(testName, skipNames)) {
        filteredTests.add(testName);
      }
    }

    return filteredTests;
  }

  private static boolean shouldSkip(String testName, List<String> skipNames) {
    for (String skipName : skipNames) {
      if (testName.startsWith(skipName)) {
        return true;
      }
    }

    return false;
  }

  public static String filterString(String text, String textToRemove) {
    return text.replaceAll(textToRemove, "");
  }
}
