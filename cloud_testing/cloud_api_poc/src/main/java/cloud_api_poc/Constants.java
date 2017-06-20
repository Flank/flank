package cloud_api_poc;

import static cloud_api_poc.Utils.fatalError;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.cloud.ServiceOptions;

public abstract class Constants {
  private Constants() {}

  public static final String applicationName = "Flank";
  public static final String projectId = ServiceOptions.getDefaultProjectId();
  public static final String GCS_PREFIX = "gs://";
  public static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

  public static HttpTransport httpTransport = null;

  static {
    try {
      httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    } catch (Exception e) {
      fatalError(e);
    }
  }
}
