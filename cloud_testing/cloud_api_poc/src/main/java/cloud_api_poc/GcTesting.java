package cloud_api_poc;

import static cloud_api_poc.Config.getCredential;
import static cloud_api_poc.Constants.*;

import com.google.testing.Testing;

public abstract class GcTesting {
  private GcTesting() {}

  private static Testing testing = null;

  static {
    testing =
        new Testing.Builder(httpTransport, JSON_FACTORY, getCredential())
            .setApplicationName(applicationName)
            .build();
  }

  public static Testing get() {
    return testing;
  }
}
