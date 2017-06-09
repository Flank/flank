package com.walmart.otto.tools;

import com.walmart.otto.configurator.Configurator;
import java.util.ArrayList;
import java.util.List;

public abstract class Tool {
  private final String name;
  private final ProcessExecutor processExecutor;

  private Configurator configurator;

  private String appAPK;
  private String testAPK;

  private List<String> errorStream = new ArrayList<>();

  Tool(String name, ToolManager.Config config) {
    this.name = name;
    this.configurator = config.configurator;
    this.appAPK = config.appAPK;
    this.testAPK = config.testAPK;
    processExecutor = config.processExecutor;
  }

  public String getName() {
    return name;
  }

  public Configurator getConfigurator() {
    return configurator;
  }

  public String getAppAPK() {
    return appAPK;
  }

  public String getTestAPK() {
    return testAPK;
  }

  public List<String> getErrorStreamList() {
    return errorStream;
  }

  public void executeCommand(final String[] commands, List<String> inputStream) {
    processExecutor.executeCommand(commands, inputStream, errorStream);
  }

  public void executeCommand(final String[] commands) throws RuntimeException {
    List<String> inputErrorStreamList = new ArrayList<>();

    processExecutor.executeCommand(commands, new ArrayList<>(), inputErrorStreamList);

    for (String input : inputErrorStreamList) {
      if (input.contains("Exception")) {
        throw new RuntimeException(input);
      }
    }
  }

  public void executeCommand(
      final String[] commands, List<String> inputStreamList, List<String> errorStreamList) {
    processExecutor.executeCommand(commands, inputStreamList, errorStreamList);
  }
}
