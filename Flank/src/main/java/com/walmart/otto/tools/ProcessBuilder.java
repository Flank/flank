package com.walmart.otto.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;


public class ProcessBuilder {
    String[] command;
    private int status;
    private List<String> lines = null;
    private StreamBoozer seInfo;
    private StreamBoozer seError;

    public ProcessBuilder(String[] command, List<String> inputStream, List<String> errorStream) throws Exception {
        this.command = command;

        java.lang.ProcessBuilder pb = new java.lang.ProcessBuilder(command);

        Process process = pb.start();
        seInfo = new StreamBoozer(process.getInputStream(), inputStream, command);
        seError = new StreamBoozer(process.getErrorStream(), errorStream, command);
        seInfo.start();
        seError.start();
        status = process.waitFor();
        seInfo.join();
        seError.join();
    }

    public List<String> getOutput() {
        return lines;
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

    class StreamBoozer extends Thread {
        private final InputStream in;
        private final List<String> lines;
        private final String[] command;

        StreamBoozer(InputStream in, List<String> lines, String[] command) {
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

}
