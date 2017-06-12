package com.walmart.otto.tools;

import java.io.IOException;
import java.util.List;

public class ProcessBuilder {
  private int status;
  private StreamBoozer seInfo;
  private StreamBoozer seError;
  private java.lang.ProcessBuilder pb;
  private List<String> inputStream;
  private List<String> errorStream;
  private String[] command;

  public ProcessBuilder(String[] command, List<String> inputStream, List<String> errorStream)
      throws IOException {
    this.inputStream = inputStream;
    this.errorStream = errorStream;
    this.command = command.clone();
    pb = new java.lang.ProcessBuilder(command);
  }

  public void start() throws IOException, InterruptedException {
    Process process = pb.start();
    seInfo = new StreamBoozer(process.getInputStream(), inputStream, command);
    seError = new StreamBoozer(process.getErrorStream(), errorStream, command);
    seInfo.start();
    seError.start();
    status = process.waitFor();
    seInfo.join();
    seError.join();
  }

  public int getStatus() {
    return status;
  }

  public void kill() {
    if (seInfo != null) {
      seInfo.stop();
      seInfo.interrupt();
    }
    if (seError != null) {
      seError.stop();
      seError.interrupt();
    }
  }
}
