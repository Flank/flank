package com.walmart.otto.tools;

import com.walmart.otto.Constants;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class GsutilTool extends Tool {
    private String bucket;

    public GsutilTool(ToolManager.Config config) {
        super(ToolManager.GSUTIL_TOOL, config);
    }

    public String uploadAPKsToBucket() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm");
        Date date = new Date();

        bucket = Constants.ROOT_APK_BUCKET + simpleDateFormat.format(date);

        System.out.println("\nCreating bucket: " + bucket + "\n");

        executeCommand(createBucket(bucket));

        System.out.println("Uploading: " + getAppAPK() + " to bucket: " + bucket + "\n");

        executeCommand(copyFileToBucket(getAppAPK(), bucket));

        System.out.println("Uploading: " + getTestAPK() + " to bucket: " + bucket + "\n");

        executeCommand(copyFileToBucket(getTestAPK(), bucket));

        return bucket;
    }

    public void uploadTestTimeFile() {
        executeCommand(createBucket(Constants.ROOT_FLANK_BUCKET));
        executeCommand(copyFileToBucket(Constants.TEST_TIME_FILE, Constants.ROOT_FLANK_BUCKET));
    }

    public void downloadTestTimeFile() {
        executeCommand(downloadTestTime());
    }

    public Map<String, String> fetchResults() {
        Map<String, String> xmlFileAndDevice = new HashMap<String, String>();
        File currentDir = new File("");
        File resultsDir = new File(currentDir.getAbsolutePath() + File.separator + Constants.RESULTS_DIR);

        System.out.println("\nFetching results to: " + resultsDir.getAbsolutePath() + "\n");
        resultsDir.mkdirs();
        String[] fetchFiles = fetchXMLFiles(resultsDir);

        executeCommand(fetchFiles);

        getDeviceNames(xmlFileAndDevice);

        return xmlFileAndDevice;
    }

    public boolean findTestTimeFile() {
        System.setOut(flankStream);
        executeCommand(findFile(Constants.ROOT_FLANK_BUCKET + Constants.TEST_TIME_FILE));
        System.setOut(originalStream);
        if (flankStreamStringBuilder.toString().contains(Constants.ROOT_FLANK_BUCKET)) {
            return true;
        }
        return false;
    }

    public void deleteAPKs() {
        executeCommand(deleteApp());
        executeCommand(deleteTest());
    }

    private void getDeviceNames(Map<String, String> xmlFileAndDevice) {
        for (String name : getErrorStreamList()) {
            String[] line;
            if (name.contains("Copying gs")) {
                line = name.split(Pattern.quote("/"));
                xmlFileAndDevice.put(line[line.length - 1].replace("xml...", "xml"), line[line.length - 2]);
            }
        }

    }

    private String[] downloadTestTime() {
        String[] fetchFiles = new String[7];
        fetchFiles[0] = getConfigurator().getGsutil();
        fetchFiles[1] = "-m";
        fetchFiles[2] = "cp";
        fetchFiles[3] = "-r";
        fetchFiles[4] = "-U";
        fetchFiles[5] = Constants.ROOT_FLANK_BUCKET + Constants.TEST_TIME_FILE;
        fetchFiles[6] = new File("").getAbsolutePath();
        return fetchFiles;
    }

    private String[] fetchXMLFiles(File file) {
        String[] fetchFiles = new String[7];
        fetchFiles[0] = getConfigurator().getGsutil();
        fetchFiles[1] = "-m";
        fetchFiles[2] = "cp";
        fetchFiles[3] = "-r";
        fetchFiles[4] = "-U";
        fetchFiles[5] = bucket + "/**/*.xml";
        fetchFiles[6] = file.getAbsolutePath();
        return fetchFiles;
    }

    private String[] findFile(String name) {
        String[] findFile = new String[3];
        findFile[0] = getConfigurator().getGsutil();
        findFile[1] = "ls";
        findFile[2] = name;
        return findFile;
    }

    private String[] createBucket(String nameOfBucket) {
        String[] createBucket = new String[3];
        createBucket[0] = getConfigurator().getGsutil();
        createBucket[1] = "mkdir";
        createBucket[2] = nameOfBucket;
        return createBucket;
    }

    private String[] deleteApp() {
        String[] deleteApp = new String[4];
        deleteApp[0] = getConfigurator().getGsutil();
        deleteApp[1] = "rm";
        deleteApp[2] = "-r";
        deleteApp[3] = bucket + File.separator + getAppAPK();
        return deleteApp;
    }

    private String[] deleteTest() {
        String[] deleteTest = new String[4];
        deleteTest[0] = getConfigurator().getGsutil();
        deleteTest[1] = "rm";
        deleteTest[2] = "-r";
        deleteTest[3] = bucket + File.separator + getTestAPK();
        return deleteTest;
    }

    private String[] copyFileToBucket(String file, String bucket) {
        String[] copyTest = new String[4];
        copyTest[0] = getConfigurator().getGsutil();
        copyTest[1] = "cp";
        copyTest[2] = file;
        copyTest[3] = bucket;
        return copyTest;
    }

    static StringBuilder flankStreamStringBuilder = new StringBuilder();

    static PrintStream originalStream = System.out;

    static PrintStream flankStream = new PrintStream(new OutputStream() {
        public void write(int b) {
            flankStreamStringBuilder.append(String.valueOf((char) b));
        }
    });
}
