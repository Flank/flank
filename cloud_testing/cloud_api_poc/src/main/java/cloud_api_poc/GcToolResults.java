package cloud_api_poc;

import static cloud_api_poc.Config.getCredential;
import static cloud_api_poc.Constants.*;

import com.google.api.services.toolresults.ToolResults;

public abstract class GcToolResults {
  private GcToolResults() {}

  private static ToolResults toolresults = null;

  static {
    toolresults =
        new ToolResults.Builder(httpTransport, JSON_FACTORY, getCredential())
            .setApplicationName(applicationName)
            .build();
  }

  public static ToolResults get() {
    return toolresults;
  }
}
