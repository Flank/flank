package com.walmart.otto;

import static com.walmart.otto.utils.FileUtils.fileExists;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import com.walmart.otto.tools.ProcessBuilder;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ShardPy {

  public static final String cloudInstallDir = getGcloudInstallDir();

  private static String getGcloudInstallDir() {
    String[] commands = new String[] {"gcloud", "info", "--format", "list"};

    List<String> outputStream = new ArrayList<>();
    List<String> errorStream = new ArrayList<>();
    try {
      new ProcessBuilder(commands, outputStream, errorStream).start();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(-1);
    }

    String gcloudInstallRoot = "";
    for (String str : outputStream) {
      // Installation Root: [/Users/user/gcloud/google-cloud-sdk]
      if (str.contains("Installation Root: [")) {
        gcloudInstallRoot = str.substring(str.indexOf('[') + 1, str.lastIndexOf(']'));
        break;
      }
    }

    if (errorStream.size() > 0 || gcloudInstallRoot.isEmpty()) {
      for (String str : errorStream) {
        System.out.println(str);
      }

      throw new RuntimeException(
          "Unable to locate gcloud Installation Root. Run: gcloud info --format list");
    }

    return gcloudInstallRoot;
  }

  public static void updateShardPy() {
    String androidDir =
        Paths.get(cloudInstallDir, "lib/surface/firebase/test/android").toAbsolutePath().toString();
    Path runPy = Paths.get(androidDir, "run.py");
    Path shardPy = Paths.get(androidDir, "shard.py");
    File shardPyFile = shardPy.toFile();

    if (!fileExists(runPy)) {
      throw new RuntimeException("run.py does not exist at: " + runPy);
    }

    long modTime = -1;
    if (shardPyFile.exists()) {
      modTime = shardPyFile.lastModified();
    }

    Path bundledShardPy;
    URL shardResource = ShardPy.class.getResource("shard.py");
    if (shardResource == null) {
      String currentDir = Paths.get(".").toAbsolutePath().normalize().toString();
      bundledShardPy = Paths.get(currentDir, "/src/main/resources/shard.py");
    } else {
      bundledShardPy = Paths.get(shardResource.getFile());
    }

    long expectedModTime = bundledShardPy.toFile().lastModified();

    if (modTime != expectedModTime) {
      System.out.println("Updating shard.py: " + shardPy);
      try {
        Files.copy(bundledShardPy, shardPy, REPLACE_EXISTING);
        if (!shardPyFile.setLastModified(expectedModTime)) {
          System.out.println("failed to set last mod time on shard.py");
        }
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(-1);
      }
    } else {
      System.out.println("shard.py up to date");
    }
  }

  public static void main(String[] args) {
    updateShardPy();
  }
}
