package com.walmart.otto.utils;

public class FilterUtils {

  public static String filterString(String text, String textToRemove) {
    return text.replaceAll(textToRemove, "");
  }
}
