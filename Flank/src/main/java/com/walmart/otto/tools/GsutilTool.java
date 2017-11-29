package com.walmart.otto.tools;

import static com.walmart.otto.utils.FileUtils.getSimpleName;

import com.walmart.otto.Constants;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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

    bucketName.append(
        DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss.")
            .withZone(ZoneOffset.UTC)
            .format(instant));

    String nanoseconds = String.valueOf(instant.getNano());

    if (nanoseconds.length() >= 6) {
      bucketName.append(nanoseconds.substring(0, 6));
    } else {
      bucketName.append(nanoseconds.substring(0, nanoseconds.length() - 1));
    }

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

  public String uploadAPKsToBucket() throws RuntimeException, IOException, InterruptedException {

    bucket = getConfigurator().getTestTimeBucket();

    if (!findGSFile(bucket)) {
      System.out.println("\nCreating bucket: " + bucket + "\n");
      executeCommand(createBucket(bucket));
    }

    bucket = bucket + uniqueObjectName();

    System.out.println("Uploading: " + getAppAPK() + " to: " + bucket + "\n");

    executeCommand(copyFileToBucket(getAppAPK(), bucket));

    System.out.println("Uploading: " + getTestAPK() + " to: " + bucket + "\n");

    executeCommand(copyFileToBucket(getTestAPK(), bucket));

    return bucket;
  }

  public void uploadTestTimeFile() throws IOException, InterruptedException {
    if (!findTestTimeBucket()) {
      executeCommand(createBucket(getConfigurator().getTestTimeBucket()));
    }
    executeCommand(
        copyFileToBucket(Constants.TEST_TIME_FILE, getConfigurator().getTestTimeBucket()));
  }

  public void downloadTestTimeFile() throws IOException, InterruptedException {
    executeCommand(downloadTestTime(), new ArrayList<String>(), new ArrayList<>());
  }

  public File[] fetchResults() throws IOException, InterruptedException {
    File currentDir = new File("");
    File resultsDir =
        new File(currentDir.getAbsolutePath() + File.separator + Constants.RESULTS_DIR);

    boolean createdFolder = resultsDir.mkdirs();

    if (createdFolder) {
      System.out.println("Created folder: " + resultsDir.getAbsolutePath() + "\n");
    }

    System.out.println("Fetching results to: " + resultsDir.getAbsolutePath());

    String[] fetchFiles = fetchXMLFiles(resultsDir);

    executeCommand(fetchFiles, new ArrayList<>());

    return resultsDir.listFiles();
  }

  public Optional<File> fetchBucket() throws IOException, InterruptedException {
    if (!getConfigurator().isFetchBucket()) {
      return Optional.empty();
    }

    File currentDir = new File("");
    File resultsDir = new File(currentDir.getAbsolutePath() + File.separator + "bucket");

    boolean createdFolder = resultsDir.mkdirs();

    if (createdFolder) {
      System.out.println("Created folder: " + resultsDir.getAbsolutePath());
    }

    System.out.println("\nFetching bucket to: " + resultsDir.getAbsolutePath());

    String[] fetchBucket = fetchBucket(resultsDir);

    executeCommand(fetchBucket, new ArrayList<>());

    return Optional.of(new File(resultsDir, bucket.split("/")[3]));
  }

  public boolean findGSFile(String fileName) throws IOException, InterruptedException {
    List<String> errorStreamList = new ArrayList<>();
    System.setOut(getEmptyStream());
    executeCommand(findFile(fileName), new ArrayList<>(), errorStreamList);
    System.setOut(originalStream);

    for (String input : errorStreamList) {
      if (input.contains("Exception")) {
        return false;
      }
    }
    return true;
  }

  public boolean findTestTimeFile() throws IOException, InterruptedException {
    return findGSFile(getConfigurator().getTestTimeBucket() + Constants.TEST_TIME_FILE);
  }

  public boolean findTestTimeBucket() throws IOException, InterruptedException {
    List<String> inputstreamList = new ArrayList<>();
    System.setOut(getEmptyStream());
    executeCommand(
        findFile(getConfigurator().getTestTimeBucket()), inputstreamList, new ArrayList<>());
    System.setOut(originalStream);

    for (String input : inputstreamList) {
      if (input.contains(getConfigurator().getTestTimeBucket())) {
        return true;
      }
    }
    return false;
  }

  public void deleteAPKs() throws IOException, InterruptedException {
    executeCommand(deleteApp());
    executeCommand(deleteTest());
  }

  private String[] downloadTestTime() {
    String[] downloadTestTimeFile =
        new String[] {
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
    String[] fetchFiles =
        new String[] {
          getConfigurator().getGsutil(),
          "-m",
          "rsync",
          "-r",
          "-x",
          "^((?!test_result_\\d+\\.xml).)*$",
          bucket,
          file.getAbsolutePath()
        };
    return fetchFiles;
  }

  private String[] fetchBucket(File file) {
    String[] fetchFiles =
        new String[] {
          getConfigurator().getGsutil(), "-m", "cp", "-r", "-U", bucket, file.getAbsolutePath()
        };
    return fetchFiles;
  }

  private String[] findFile(String name) {
    String[] findFile = new String[] {getConfigurator().getGsutil(), "--quiet", "ls", name};
    return findFile;
  }

  private String[] createBucket(String nameOfBucket) {
    String[] createBucket =
        new String[] {
          getConfigurator().getGsutil(), "--quiet", "mb", nameOfBucket,
        };
    return createBucket;
  }

  private String[] deleteApp() {
    String[] deleteApp =
        new String[] {
          getConfigurator().getGsutil(),
          "--quiet",
          "-m",
          "rm",
          "-r",
          bucket + "**/" + getSimpleName(getAppAPK())
        };
    return deleteApp;
  }

  private String[] deleteTest() {
    String[] deleteTest =
        new String[] {
          getConfigurator().getGsutil(),
          "--quiet",
          "-m",
          "rm",
          "-r",
          bucket + "**/" + getSimpleName(getTestAPK())
        };
    return deleteTest;
  }

  private String[] copyFileToBucket(String file, String bucket) {
    return new String[] {
      getConfigurator().getGsutil(), "--quiet", "cp", file, bucket,
    };
  }

  private String[] copyDirectoryToBucket(String directory, String bucket) {
    return new String[] {
      getConfigurator().getGsutil(), "--quiet", "cp", "-r", directory, bucket,
    };
  }

  static PrintStream originalStream = System.out;

  public PrintStream getEmptyStream() {
    PrintStream emptyStream = null;
    try {
      emptyStream =
          new PrintStream(
              new OutputStream() {
                public void write(int b) {}
              },
              false,
              "UTF-8");
    } catch (UnsupportedEncodingException ignored) {
    }
    return emptyStream;
  }

  public void uploadAggregatedXmlFiles(File reportsBaseDir)
      throws IOException, InterruptedException {
    uploadFiles(reportsBaseDir, "*.xml");
  }

  public void uploadAggregatedHtmlReports(File reportsBaseDir)
      throws IOException, InterruptedException {
    uploadFiles(reportsBaseDir, "*.html");
    uploadDirectoryIfExists(reportsBaseDir, "video");
    uploadDirectoryIfExists(reportsBaseDir, "logcat");
  }

  private void uploadFiles(File reportsBaseDir, String pattern)
      throws IOException, InterruptedException {
    executeCommand(
        copyFileToBucket(reportsBaseDir.getAbsolutePath() + File.separator + pattern, bucket));
  }

  private void uploadDirectoryIfExists(File file, String child)
      throws IOException, InterruptedException {
    File childDirectory = new File(file, child);
    if (childDirectory.exists()) {
      executeCommand(copyDirectoryToBucket(childDirectory.getAbsolutePath(), bucket));
    }
  }
}
