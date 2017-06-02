package com.walmart.otto.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class StreamBoozer extends Thread {
  private final InputStream in;
  private final List<String> lines;
  private final String[] command;

  public List<String> getOutput() {
    return lines;
  }

  public StreamBoozer(InputStream in, List<String> lines, String[] command) {
    this.in = in;
    this.lines = lines;
    this.command = command;
  }

  @Override
  public void run() {
    BufferedReader br = null;
    try {
      br = new BufferedReader(new InputStreamReader(in));
      String line = null;
      while ((line = br.readLine()) != null) {
        lines.add(line);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        br.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
