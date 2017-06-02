package com.walmart.otto.utils;

import java.util.ArrayList;
import java.util.List;

public class FilterUtils {

  public static List<String> filterTests(List<String> testNames, String packageName) {
    List<String> filteredTests = new ArrayList<String>();

    for (String testName : testNames) {
      if (testName.startsWith(packageName)) {
        filteredTests.add(testName);
      }
    }
    return filteredTests;
  }

  public static String filterString(String text, String textToRemove) {
    return text.replaceAll(textToRemove, "");
  }
}
