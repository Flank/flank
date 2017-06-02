package com.walmart.otto.tools;

import com.walmart.otto.configurator.Configurator;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ProcessExecutor {
  private Configurator configurator;

  public ProcessExecutor(Configurator configurator) {
    this.configurator = configurator;
  }
  // skip: ' - [113/113 files][  2.1 MiB/  2.1 MiB] 100% Done '
  private static Pattern uploadProgress = Pattern.compile(".*\\[\\d*/\\d* files]\\[.*% Done.*");

  public void executeCommand(
      final String[] commands, List<String> inputStream, List<String> errorStream) {
    Boolean isDebug = configurator.isDebug();
    try {
      if (isDebug) {
        StringBuilder command = new StringBuilder();
        command.append("\u001B[32m"); // green
        for (String cmd : commands) {
          command.append(cmd).append(" ");
        }
        command.append("\u001B[0m");
        System.out.println("$ " + command.toString());
      }
      new ProcessBuilder(commands, inputStream, errorStream);
      if (isDebug) {
        List<String> cleanErrorStream = new ArrayList<>();

        for (String line : errorStream) {
          if (!uploadProgress.matcher(line).matches()) {
            cleanErrorStream.add(line);
          }
        }

        printStreams(inputStream, cleanErrorStream);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void printStreams(List<String> inputStream, List<String> errorStream) {
    for (String line : inputStream) {
      System.out.println(line);
    }

    for (String line : errorStream) {
      System.out.println(line);
    }
  }
}
