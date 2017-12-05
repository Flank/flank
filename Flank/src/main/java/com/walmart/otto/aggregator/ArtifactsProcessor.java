package com.walmart.otto.aggregator;

import com.walmart.otto.configurator.Configurator;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.Instant;
import java.time.Year;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ArtifactsProcessor {

  private static final DateTimeFormatter DATE_TIME_FORMATTER =
      new DateTimeFormatterBuilder()
          .appendPattern("MM-dd HH:mm:ss.SSS")
          .parseDefaulting(ChronoField.YEAR_OF_ERA, Year.now().getValue())
          .toFormatter(Locale.ENGLISH)
          .withZone(ZoneId.systemDefault());

  private static final Pattern TEST_STARTED_LINE =
      Pattern.compile(".+: I/TestRunner\\(\\d+\\): started: .+");

  private static final Duration TEN_SECONDS = Duration.ofSeconds(10);

  private static final String CUT_VIDEO_COMMAND_TEMPLATE = "ffmpeg -i %s -ss %s -to %s %s";

  private Configurator configurator;

  public ArtifactsProcessor(Configurator configurator) {
    this.configurator = configurator;
  }

  public void generateArtifactsForTestCase(Path reportBaseDir, String matrixName, TestCase testCase)
      throws IOException, InterruptedException {

    final Pattern targetTestStartedLine = createTestStartedPattern(testCase);
    final Pattern targetTestFinishedLine = createTestFinishedPattern(testCase);

    Path sourceLogcatFile =
        reportBaseDir.resolve(
            testCase.getShardName() + File.separator + matrixName + File.separator + "logcat");

    Path logcatsFolder = getOrCreateChildFolder(reportBaseDir, "logcat");
    Path testLogcatFile = logcatsFolder.resolve(testCase.getId() + ".txt");

    Path entireLogcatFile = logcatsFolder.resolve("shard_" + testCase.getShardName() + ".txt");
    Files.copy(sourceLogcatFile, entireLogcatFile, StandardCopyOption.REPLACE_EXISTING);

    Instant firstTestStartedTime = null;
    Instant targetTestStartedTime = null;
    Instant targetTestFinishedTime = null;

    if (!Files.exists(sourceLogcatFile)) {
      System.err.printf(
          "Can't process logcat file for %s. Expected file %s does not exist.%n",
          testCase.getId(), sourceLogcatFile.toString());
      return;
    }

    try (BufferedReader reader = Files.newBufferedReader(sourceLogcatFile);
        BufferedWriter writer = Files.newBufferedWriter(testLogcatFile, Charset.forName("UTF-8"))) {

      String line;
      boolean copy = false;

      while ((line = reader.readLine()) != null) {

        if (firstTestStartedTime == null && TEST_STARTED_LINE.matcher(line).matches()) {
          firstTestStartedTime = extractInstant(line);
        }

        if (targetTestStartedLine.matcher(line).matches()) {
          targetTestStartedTime = extractInstant(line);
          copy = true;
        }

        if (copy) {
          writer.write(line);
          writer.newLine();
        }

        if (targetTestFinishedLine.matcher(line).matches()) {
          targetTestFinishedTime = extractInstant(line);
          break;
        }
      }
    }

    processVideo(
        reportBaseDir,
        matrixName,
        testCase,
        firstTestStartedTime,
        targetTestStartedTime,
        targetTestFinishedTime);
  }

  private void processVideo(
      Path reportBaseDir,
      String matrixName,
      TestCase testCase,
      Instant firstTestExecuted,
      Instant startOfTargetTest,
      Instant endOfTargetTest)
      throws IOException, InterruptedException {

    Path videoFile =
        reportBaseDir.resolve(
            testCase.getShardName() + File.separator + matrixName + File.separator + "video.mp4");

    if (!Files.exists(videoFile)) {
      System.err.printf(
          "Can't process video file for %s. Expected file %s does not exist.%n",
          testCase.getId(), videoFile.toString());
      return;
    }

    Path videosFolder = getOrCreateChildFolder(reportBaseDir, "video");
    Path outputFile = videosFolder.resolve(testCase.getId() + ".mp4");
    Path entireVideoFile = videosFolder.resolve("shard_" + testCase.getShardName() + ".mp4");

    Files.copy(videoFile, entireVideoFile, StandardCopyOption.REPLACE_EXISTING);

    if (!configurator.isGenerateSplitVideo()) {
      return; // if we don't split the video it's enough to copy the full one
    }

    if (checkTimePreconditions(firstTestExecuted, startOfTargetTest, endOfTargetTest)) {

      Duration normalizedStartOfTest = Duration.between(firstTestExecuted, startOfTargetTest);
      if (normalizedStartOfTest.getSeconds() > 10) {
        normalizedStartOfTest = normalizedStartOfTest.minusSeconds(10);
      }

      Duration normalizedEndOfTest =
          Duration.between(firstTestExecuted, endOfTargetTest).plus(TEN_SECONDS);

      String normalizedStartOfTestString = TimeUtils.formatDuration(normalizedStartOfTest);
      String normalizedEndOfTestString = TimeUtils.formatDuration(normalizedEndOfTest);

      String command =
          String.format(
              CUT_VIDEO_COMMAND_TEMPLATE,
              videoFile.toString(),
              normalizedStartOfTestString,
              normalizedEndOfTestString,
              outputFile.toString());
      if (this.configurator.isDebug()) {
        System.out.println(command);
      }
      runCommandSafely(command);
    } else {
      if (this.configurator.isDebug()) {
        System.out.printf(
            "Unable to process video for: %s shard: %s firstTestExecuted %s startOfTargetTest %s endOftest %s%n",
            testCase.getId(),
            testCase.getShardName(),
            firstTestExecuted,
            startOfTargetTest,
            endOfTargetTest);
      }
    }
  }

  private void runCommandSafely(String command) {
    try {
      Runtime.getRuntime().exec(command);
    } catch (IOException e) {
      System.err.println("Error running ffmpg: " + e.getMessage() + " Is it installed?");
    }
  }

  private boolean checkTimePreconditions(
      Instant startOfLogcat, Instant startOfTest, Instant endOfTest) {
    return startOfLogcat != null
        && startOfTest != null
        && endOfTest != null
        && startOfLogcat.isBefore(endOfTest)
        && startOfTest.isBefore(endOfTest);
  }

  private Instant extractInstant(String line) {
    Pattern timePattern = Pattern.compile("(.*): \\w/.*");
    Matcher matcher = timePattern.matcher(line);
    if (matcher.find()) {
      String time = matcher.group(1);
      return DATE_TIME_FORMATTER.parse(time, Instant::from);
    } else {
      throw new IllegalStateException("Cant extract time from: " + line);
    }
  }

  private Path getOrCreateChildFolder(Path base, String child) throws IOException {
    Path folder = base.resolve(child);
    if (!Files.exists(folder)) {
      try {
        Files.createDirectory(folder);
      } catch (FileAlreadyExistsException e) {
        // it's ok to ignore
      }
    }
    return folder;
  }

  private Pattern createTestFinishedPattern(TestCase testCase) {
    String testFinishedRegex =
        String.format(
            ".+: I/TestRunner\\(\\d+\\): finished: %s\\(%s\\)",
            testCase.getTestName(), testCase.getClassName());
    return Pattern.compile(testFinishedRegex);
  }

  private Pattern createTestStartedPattern(TestCase testCase) {
    String testStartedRegex =
        String.format(
            ".+: I/TestRunner\\(\\d+\\): started: %s\\(%s\\)",
            testCase.getTestName(), testCase.getClassName());
    return Pattern.compile(testStartedRegex);
  }
}
