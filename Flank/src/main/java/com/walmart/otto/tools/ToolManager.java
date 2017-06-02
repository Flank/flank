package com.walmart.otto.tools;

import com.walmart.otto.configurator.Configurator;
import java.util.ArrayList;
import java.util.List;

public class ToolManager {
  public static final String GSUTIL_TOOL = "gsutilTool";
  public static final String GCLOUD_TOOL = "gcloudTool";

  private List<Tool> tools = new ArrayList<>();

  public ToolManager load(Config config) {
    tools.add(new GcloudTool(config));
    tools.add(new GsutilTool(config));
    return this;
  }

  public Tool get(String toolName) {
    for (Tool tool : tools) {
      if (tool.getName().equals(toolName)) {
        return tool;
      }
    }

    return null;
  }

  public <T> T get(Class<T> outClazz) {
    for (Tool tool : tools) {
      if (outClazz.isInstance(tool)) {
        return outClazz.cast(tool);
      }
    }

    return null;
  }

  public static class Config {
    public String appAPK;
    public String testAPK;
    public Configurator configurator;
    public ProcessExecutor processExecutor;
  }
}
