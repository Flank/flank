package com.walmart.otto.tools;

import com.walmart.otto.Constants;
import com.walmart.otto.configurator.Configurator;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

import static com.walmart.otto.utils.FileUtils.getSimpleName;

public class GsutilTool extends Tool {
    private String bucket;

    public GsutilTool(ToolManager.Config config) {
        super(ToolManager.GSUTIL_TOOL, config);
    }


    // Match _GenerateUniqueGcsObjectName from api_lib/firebase/test/arg_validate.py
    //
    // Example: 2017-05-31_17:19:36.431540_hRJD
    //
    // https://cloud.google.com/storage/docs/naming
    private String uniqueObjectName() {
        StringBuilder bucketName = new StringBuilder();
        Instant instant = Instant.now();

        bucketName.append(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss.")
                .withZone(ZoneOffset.UTC)
                .format(instant));
        bucketName.append(String.valueOf(instant.getNano()).substring(0, 6));
        bucketName.append("_");

        Random random = new Random();
        // a-z: 97 - 122
        // A-Z: 65 - 90
        for (int i = 0; i < 4; i++) {
            int ascii = random.nextInt(26);
            char letter = (char) (ascii + 'a');

            if (ascii % 2 == 0) {
                letter -= 32; // upcase
            }

            bucketName.append(letter);
        }

        bucketName.append("/");

        return bucketName.toString();
    }

    public String uploadAPKsToBucket() {
        bucket = getConfigurator().getTestTimeBucket();
        executeCommand(createBucket(bucket));

        System.out.println("\nCreating bucket: " + bucket + "\n");

        bucket = bucket + uniqueObjectName();

        System.out.println("Uploading: " + getAppAPK() + " to: " + bucket + "\n");

        executeCommand(copyFileToBucket(getAppAPK(), bucket));

        System.out.println("Uploading: " + getTestAPK() + " to: " + bucket + "\n");

        executeCommand(copyFileToBucket(getTestAPK(), bucket));

        return bucket;
    }

    public void uploadTestTimeFile() {
        if (!findTestTimeBucket()) {
            executeCommand(createBucket(getConfigurator().getTestTimeBucket()));
        }
        executeCommand(copyFileToBucket(Constants.TEST_TIME_FILE, getConfigurator().getTestTimeBucket()));
    }

    public void downloadTestTimeFile() {
        executeCommand(downloadTestTime(), new ArrayList<String>(), new ArrayList<>());
    }

    public Map<String, String> fetchResults() {
        Map<String, String> xmlFileAndDevice = new HashMap<String, String>();
        File currentDir = new File("");
        File resultsDir = new File(currentDir.getAbsolutePath() + File.separator + Constants.RESULTS_DIR);

        System.out.println("\nFetching results to: " + resultsDir.getAbsolutePath() + "\n");
        resultsDir.mkdirs();
        String[] fetchFiles = fetchXMLFiles(resultsDir);

        executeCommand(fetchFiles, new ArrayList<>());

        getDeviceNames(xmlFileAndDevice);

        return xmlFileAndDevice;
    }

    public boolean findTestTimeFile() {
        List<String> inputstreamList = new ArrayList<>();
        System.setOut(emptyStream);
        executeCommand(findFile(getConfigurator().getTestTimeBucket() + Constants.TEST_TIME_FILE), inputstreamList, new ArrayList<>());
        System.setOut(originalStream);

        for (String input : inputstreamList) {
            if (input.contains(getConfigurator().getTestTimeBucket())) {
                return true;
            }
        }
        return false;
    }

    public boolean findTestTimeBucket() {
        List<String> inputstreamList = new ArrayList<>();
        System.setOut(emptyStream);
        executeCommand(findFile(getConfigurator().getTestTimeBucket()), inputstreamList, new ArrayList<>());
        System.setOut(originalStream);

        for (String input : inputstreamList) {
            if (input.contains(getConfigurator().getTestTimeBucket())) {
                return true;
            }
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
        String[] downloadTestTimeFile = new String[]{
                getConfigurator().getGsutil(),
                "--quiet",
                "-m",
                "cp",
                "-r",
                "-U",
                getConfigurator().getTestTimeBucket() + Constants.TEST_TIME_FILE,
                new File("").getAbsolutePath()
        };
        return downloadTestTimeFile;
    }

    private String[] fetchXMLFiles(File file) {
        String[] fetchFiles = new String[]{
                getConfigurator().getGsutil(),
                "-m",
                "cp",
                "-r",
                "-U",
                bucket + "**/*.xml",
                file.getAbsolutePath()
        };
        return fetchFiles;
    }

    private String[] findFile(String name) {
        String[] findFile = new String[]{
                getConfigurator().getGsutil(),
                "--quiet",
                "ls",
                name
        };
        return findFile;
    }

    private String[] createBucket(String nameOfBucket) {
        String[] createBucket = new String[]{
                getConfigurator().getGsutil(),
                "--quiet",
                "mb",
                nameOfBucket,
        };
        return createBucket;
    }

    private String[] deleteApp() {
        String[] deleteApp = new String[]{
                getConfigurator().getGsutil(),
                "--quiet",
                "rm",
                "-r",
                bucket + getSimpleName(getAppAPK())
        };
        return deleteApp;
    }

    private String[] deleteTest() {
        String[] deleteTest = new String[]{
                getConfigurator().getGsutil(),
                "--quiet",
                "rm",
                "-r",
                bucket + getSimpleName(getTestAPK())
        };
        return deleteTest;
    }

    private String[] copyFileToBucket(String file, String bucket) {
        String[] copyTest = new String[]{
                getConfigurator().getGsutil(),
                "--quiet",
                "cp",
                file,
                bucket,
        };
        return copyTest;
    }

    static PrintStream originalStream = System.out;
    static PrintStream emptyStream = new PrintStream(new OutputStream() {
        public void write(int b) {
        }
    });
}
