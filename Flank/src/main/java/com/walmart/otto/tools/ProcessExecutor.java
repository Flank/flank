package com.walmart.otto.tools;

import com.walmart.otto.configurator.Configurator;

import java.util.List;

public class ProcessExecutor {
    private Configurator configurator;

    public ProcessExecutor(Configurator configurator) {
        this.configurator = configurator;
    }

    public void executeCommand(final String[] commands, List<String> inputStream, List<String> errorStream) {

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(commands, inputStream, errorStream);
            if (configurator.isDebug()) {
                printStreams(inputStream, errorStream);
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
