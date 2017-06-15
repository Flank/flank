package com.google.api.services.testing;

import com.google.api.client.googleapis.GoogleUtils;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.Key;
import com.google.api.client.util.Preconditions;
import com.google.api.services.testing.model.CancelTestMatrixResponse;
import com.google.api.services.testing.model.Device;
import com.google.api.services.testing.model.Empty;
import com.google.api.services.testing.model.ListDevicesResponse;
import com.google.api.services.testing.model.ListTestMatricesResponse;
import com.google.api.services.testing.model.TestMatrix;
import java.io.IOException;

public class Testing extends AbstractGoogleJsonClient {
  public static final String DEFAULT_ROOT_URL = "https://testing.googleapis.com/";
  public static final String DEFAULT_SERVICE_PATH = "";
  public static final String DEFAULT_BASE_URL = "https://testing.googleapis.com/";

  public Testing(
      HttpTransport transport,
      JsonFactory jsonFactory,
      HttpRequestInitializer httpRequestInitializer) {
    this(new Testing.Builder(transport, jsonFactory, httpRequestInitializer));
  }

  Testing(Testing.Builder builder) {
    super(builder);
  }

  protected void initialize(AbstractGoogleClientRequest<?> httpClientRequest) throws IOException {
    super.initialize(httpClientRequest);
  }

  public Testing.Projects projects() {
    return new Testing.Projects();
  }

  public Testing.TestEnvironmentCatalog testEnvironmentCatalog() {
    return new Testing.TestEnvironmentCatalog();
  }

  static {
    Preconditions.checkState(
        GoogleUtils.MAJOR_VERSION.intValue() == 1 && GoogleUtils.MINOR_VERSION.intValue() >= 15,
        "You are currently running with version %s of google-api-client. You need at least version 1.15 of google-api-client to run version 1.20.0 of the Google Cloud Testing API library.",
        GoogleUtils.VERSION);
  }

  public static final class Builder
      extends com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient.Builder {
    public Builder(
        HttpTransport transport,
        JsonFactory jsonFactory,
        HttpRequestInitializer httpRequestInitializer) {
      super(
          transport,
          jsonFactory,
          "https://testing.googleapis.com/",
          "",
          httpRequestInitializer,
          false);
    }

    public Testing build() {
      return new Testing(this);
    }

    public Testing.Builder setRootUrl(String rootUrl) {
      return (Testing.Builder) super.setRootUrl(rootUrl);
    }

    public Testing.Builder setServicePath(String servicePath) {
      return (Testing.Builder) super.setServicePath(servicePath);
    }

    public Testing.Builder setHttpRequestInitializer(
        HttpRequestInitializer httpRequestInitializer) {
      return (Testing.Builder) super.setHttpRequestInitializer(httpRequestInitializer);
    }

    public Testing.Builder setApplicationName(String applicationName) {
      return (Testing.Builder) super.setApplicationName(applicationName);
    }

    public Testing.Builder setSuppressPatternChecks(boolean suppressPatternChecks) {
      return (Testing.Builder) super.setSuppressPatternChecks(suppressPatternChecks);
    }

    public Testing.Builder setSuppressRequiredParameterChecks(
        boolean suppressRequiredParameterChecks) {
      return (Testing.Builder)
          super.setSuppressRequiredParameterChecks(suppressRequiredParameterChecks);
    }

    public Testing.Builder setSuppressAllChecks(boolean suppressAllChecks) {
      return (Testing.Builder) super.setSuppressAllChecks(suppressAllChecks);
    }

    public Testing.Builder setTestingRequestInitializer(
        TestingRequestInitializer testingRequestInitializer) {
      return (Testing.Builder) super.setGoogleClientRequestInitializer(testingRequestInitializer);
    }

    public Testing.Builder setGoogleClientRequestInitializer(
        GoogleClientRequestInitializer googleClientRequestInitializer) {
      return (Testing.Builder)
          super.setGoogleClientRequestInitializer(googleClientRequestInitializer);
    }
  }

  public class TestEnvironmentCatalog {
    public Testing.TestEnvironmentCatalog.Get get(String environmentType) throws IOException {
      Testing.TestEnvironmentCatalog.Get result =
          new Testing.TestEnvironmentCatalog.Get(environmentType);
      Testing.this.initialize(result);
      return result;
    }

    public class Get
        extends TestingRequest<com.google.api.services.testing.model.TestEnvironmentCatalog> {
      private static final String REST_PATH = "v1/testEnvironmentCatalog/{environmentType}";
      @Key private String environmentType;

      protected Get(String environmentType) {
        super(
            Testing.this,
            "GET",
            "v1/testEnvironmentCatalog/{environmentType}",
            (Object) null,
            com.google.api.services.testing.model.TestEnvironmentCatalog.class);
        this.environmentType =
            (String)
                Preconditions.checkNotNull(
                    environmentType, "Required parameter environmentType must be specified.");
      }

      public HttpResponse executeUsingHead() throws IOException {
        return super.executeUsingHead();
      }

      public HttpRequest buildHttpRequestUsingHead() throws IOException {
        return super.buildHttpRequestUsingHead();
      }

      public Testing.TestEnvironmentCatalog.Get set$Xgafv(String $Xgafv) {
        return (Testing.TestEnvironmentCatalog.Get) super.set$Xgafv($Xgafv);
      }

      public Testing.TestEnvironmentCatalog.Get setAccessToken(String accessToken) {
        return (Testing.TestEnvironmentCatalog.Get) super.setAccessToken(accessToken);
      }

      public Testing.TestEnvironmentCatalog.Get setAlt(String alt) {
        return (Testing.TestEnvironmentCatalog.Get) super.setAlt(alt);
      }

      public Testing.TestEnvironmentCatalog.Get setBearerToken(String bearerToken) {
        return (Testing.TestEnvironmentCatalog.Get) super.setBearerToken(bearerToken);
      }

      public Testing.TestEnvironmentCatalog.Get setCallback(String callback) {
        return (Testing.TestEnvironmentCatalog.Get) super.setCallback(callback);
      }

      public Testing.TestEnvironmentCatalog.Get setFields(String fields) {
        return (Testing.TestEnvironmentCatalog.Get) super.setFields(fields);
      }

      public Testing.TestEnvironmentCatalog.Get setKey(String key) {
        return (Testing.TestEnvironmentCatalog.Get) super.setKey(key);
      }

      public Testing.TestEnvironmentCatalog.Get setOauthToken(String oauthToken) {
        return (Testing.TestEnvironmentCatalog.Get) super.setOauthToken(oauthToken);
      }

      public Testing.TestEnvironmentCatalog.Get setPp(Boolean pp) {
        return (Testing.TestEnvironmentCatalog.Get) super.setPp(pp);
      }

      public Testing.TestEnvironmentCatalog.Get setPrettyPrint(Boolean prettyPrint) {
        return (Testing.TestEnvironmentCatalog.Get) super.setPrettyPrint(prettyPrint);
      }

      public Testing.TestEnvironmentCatalog.Get setQuotaUser(String quotaUser) {
        return (Testing.TestEnvironmentCatalog.Get) super.setQuotaUser(quotaUser);
      }

      public Testing.TestEnvironmentCatalog.Get setUploadType(String uploadType) {
        return (Testing.TestEnvironmentCatalog.Get) super.setUploadType(uploadType);
      }

      public Testing.TestEnvironmentCatalog.Get setUploadProtocol(String uploadProtocol) {
        return (Testing.TestEnvironmentCatalog.Get) super.setUploadProtocol(uploadProtocol);
      }

      public String getEnvironmentType() {
        return this.environmentType;
      }

      public Testing.TestEnvironmentCatalog.Get setEnvironmentType(String environmentType) {
        this.environmentType = environmentType;
        return this;
      }

      public Testing.TestEnvironmentCatalog.Get set(String parameterName, Object value) {
        return (Testing.TestEnvironmentCatalog.Get) super.set(parameterName, value);
      }
    }
  }

  public class Projects {
    public Testing.Projects.Devices devices() {
      return new Testing.Projects.Devices();
    }

    public Testing.Projects.TestMatrices testMatrices() {
      return new Testing.Projects.TestMatrices();
    }

    public class TestMatrices {
      public Testing.Projects.TestMatrices.Cancel cancel(String projectId, String testMatrixId)
          throws IOException {
        Testing.Projects.TestMatrices.Cancel result =
            new Testing.Projects.TestMatrices.Cancel(projectId, testMatrixId);
        Testing.this.initialize(result);
        return result;
      }

      public Testing.Projects.TestMatrices.Create create(String projectId, TestMatrix content)
          throws IOException {
        Testing.Projects.TestMatrices.Create result =
            new Testing.Projects.TestMatrices.Create(projectId, content);
        Testing.this.initialize(result);
        return result;
      }

      public Testing.Projects.TestMatrices.Delete delete(String projectId, String testMatrixId)
          throws IOException {
        Testing.Projects.TestMatrices.Delete result =
            new Testing.Projects.TestMatrices.Delete(projectId, testMatrixId);
        Testing.this.initialize(result);
        return result;
      }

      public Testing.Projects.TestMatrices.Get get(String projectId, String testMatrixId)
          throws IOException {
        Testing.Projects.TestMatrices.Get result =
            new Testing.Projects.TestMatrices.Get(projectId, testMatrixId);
        Testing.this.initialize(result);
        return result;
      }

      public Testing.Projects.TestMatrices.List list(String projectId) throws IOException {
        Testing.Projects.TestMatrices.List result =
            new Testing.Projects.TestMatrices.List(projectId);
        Testing.this.initialize(result);
        return result;
      }

      public class List extends TestingRequest<ListTestMatricesResponse> {
        private static final String REST_PATH = "v1/projects/{projectId}/testMatrices";
        @Key private String projectId;

        protected List(String projectId) {
          super(
              Testing.this,
              "GET",
              "v1/projects/{projectId}/testMatrices",
              (Object) null,
              ListTestMatricesResponse.class);
          this.projectId =
              (String)
                  Preconditions.checkNotNull(
                      projectId, "Required parameter projectId must be specified.");
        }

        public HttpResponse executeUsingHead() throws IOException {
          return super.executeUsingHead();
        }

        public HttpRequest buildHttpRequestUsingHead() throws IOException {
          return super.buildHttpRequestUsingHead();
        }

        public Testing.Projects.TestMatrices.List set$Xgafv(String $Xgafv) {
          return (Testing.Projects.TestMatrices.List) super.set$Xgafv($Xgafv);
        }

        public Testing.Projects.TestMatrices.List setAccessToken(String accessToken) {
          return (Testing.Projects.TestMatrices.List) super.setAccessToken(accessToken);
        }

        public Testing.Projects.TestMatrices.List setAlt(String alt) {
          return (Testing.Projects.TestMatrices.List) super.setAlt(alt);
        }

        public Testing.Projects.TestMatrices.List setBearerToken(String bearerToken) {
          return (Testing.Projects.TestMatrices.List) super.setBearerToken(bearerToken);
        }

        public Testing.Projects.TestMatrices.List setCallback(String callback) {
          return (Testing.Projects.TestMatrices.List) super.setCallback(callback);
        }

        public Testing.Projects.TestMatrices.List setFields(String fields) {
          return (Testing.Projects.TestMatrices.List) super.setFields(fields);
        }

        public Testing.Projects.TestMatrices.List setKey(String key) {
          return (Testing.Projects.TestMatrices.List) super.setKey(key);
        }

        public Testing.Projects.TestMatrices.List setOauthToken(String oauthToken) {
          return (Testing.Projects.TestMatrices.List) super.setOauthToken(oauthToken);
        }

        public Testing.Projects.TestMatrices.List setPp(Boolean pp) {
          return (Testing.Projects.TestMatrices.List) super.setPp(pp);
        }

        public Testing.Projects.TestMatrices.List setPrettyPrint(Boolean prettyPrint) {
          return (Testing.Projects.TestMatrices.List) super.setPrettyPrint(prettyPrint);
        }

        public Testing.Projects.TestMatrices.List setQuotaUser(String quotaUser) {
          return (Testing.Projects.TestMatrices.List) super.setQuotaUser(quotaUser);
        }

        public Testing.Projects.TestMatrices.List setUploadType(String uploadType) {
          return (Testing.Projects.TestMatrices.List) super.setUploadType(uploadType);
        }

        public Testing.Projects.TestMatrices.List setUploadProtocol(String uploadProtocol) {
          return (Testing.Projects.TestMatrices.List) super.setUploadProtocol(uploadProtocol);
        }

        public String getProjectId() {
          return this.projectId;
        }

        public Testing.Projects.TestMatrices.List setProjectId(String projectId) {
          this.projectId = projectId;
          return this;
        }

        public Testing.Projects.TestMatrices.List set(String parameterName, Object value) {
          return (Testing.Projects.TestMatrices.List) super.set(parameterName, value);
        }
      }

      public class Get extends TestingRequest<TestMatrix> {
        private static final String REST_PATH =
            "v1/projects/{projectId}/testMatrices/{testMatrixId}";
        @Key private String projectId;
        @Key private String testMatrixId;

        protected Get(String projectId, String testMatrixId) {
          super(
              Testing.this,
              "GET",
              "v1/projects/{projectId}/testMatrices/{testMatrixId}",
              (Object) null,
              TestMatrix.class);
          this.projectId =
              (String)
                  Preconditions.checkNotNull(
                      projectId, "Required parameter projectId must be specified.");
          this.testMatrixId =
              (String)
                  Preconditions.checkNotNull(
                      testMatrixId, "Required parameter testMatrixId must be specified.");
        }

        public HttpResponse executeUsingHead() throws IOException {
          return super.executeUsingHead();
        }

        public HttpRequest buildHttpRequestUsingHead() throws IOException {
          return super.buildHttpRequestUsingHead();
        }

        public Testing.Projects.TestMatrices.Get set$Xgafv(String $Xgafv) {
          return (Testing.Projects.TestMatrices.Get) super.set$Xgafv($Xgafv);
        }

        public Testing.Projects.TestMatrices.Get setAccessToken(String accessToken) {
          return (Testing.Projects.TestMatrices.Get) super.setAccessToken(accessToken);
        }

        public Testing.Projects.TestMatrices.Get setAlt(String alt) {
          return (Testing.Projects.TestMatrices.Get) super.setAlt(alt);
        }

        public Testing.Projects.TestMatrices.Get setBearerToken(String bearerToken) {
          return (Testing.Projects.TestMatrices.Get) super.setBearerToken(bearerToken);
        }

        public Testing.Projects.TestMatrices.Get setCallback(String callback) {
          return (Testing.Projects.TestMatrices.Get) super.setCallback(callback);
        }

        public Testing.Projects.TestMatrices.Get setFields(String fields) {
          return (Testing.Projects.TestMatrices.Get) super.setFields(fields);
        }

        public Testing.Projects.TestMatrices.Get setKey(String key) {
          return (Testing.Projects.TestMatrices.Get) super.setKey(key);
        }

        public Testing.Projects.TestMatrices.Get setOauthToken(String oauthToken) {
          return (Testing.Projects.TestMatrices.Get) super.setOauthToken(oauthToken);
        }

        public Testing.Projects.TestMatrices.Get setPp(Boolean pp) {
          return (Testing.Projects.TestMatrices.Get) super.setPp(pp);
        }

        public Testing.Projects.TestMatrices.Get setPrettyPrint(Boolean prettyPrint) {
          return (Testing.Projects.TestMatrices.Get) super.setPrettyPrint(prettyPrint);
        }

        public Testing.Projects.TestMatrices.Get setQuotaUser(String quotaUser) {
          return (Testing.Projects.TestMatrices.Get) super.setQuotaUser(quotaUser);
        }

        public Testing.Projects.TestMatrices.Get setUploadType(String uploadType) {
          return (Testing.Projects.TestMatrices.Get) super.setUploadType(uploadType);
        }

        public Testing.Projects.TestMatrices.Get setUploadProtocol(String uploadProtocol) {
          return (Testing.Projects.TestMatrices.Get) super.setUploadProtocol(uploadProtocol);
        }

        public String getProjectId() {
          return this.projectId;
        }

        public Testing.Projects.TestMatrices.Get setProjectId(String projectId) {
          this.projectId = projectId;
          return this;
        }

        public String getTestMatrixId() {
          return this.testMatrixId;
        }

        public Testing.Projects.TestMatrices.Get setTestMatrixId(String testMatrixId) {
          this.testMatrixId = testMatrixId;
          return this;
        }

        public Testing.Projects.TestMatrices.Get set(String parameterName, Object value) {
          return (Testing.Projects.TestMatrices.Get) super.set(parameterName, value);
        }
      }

      public class Delete extends TestingRequest<Empty> {
        private static final String REST_PATH =
            "v1/projects/{projectId}/testMatrices/{testMatrixId}";
        @Key private String projectId;
        @Key private String testMatrixId;

        protected Delete(String projectId, String testMatrixId) {
          super(
              Testing.this,
              "DELETE",
              "v1/projects/{projectId}/testMatrices/{testMatrixId}",
              (Object) null,
              Empty.class);
          this.projectId =
              (String)
                  Preconditions.checkNotNull(
                      projectId, "Required parameter projectId must be specified.");
          this.testMatrixId =
              (String)
                  Preconditions.checkNotNull(
                      testMatrixId, "Required parameter testMatrixId must be specified.");
        }

        public Testing.Projects.TestMatrices.Delete set$Xgafv(String $Xgafv) {
          return (Testing.Projects.TestMatrices.Delete) super.set$Xgafv($Xgafv);
        }

        public Testing.Projects.TestMatrices.Delete setAccessToken(String accessToken) {
          return (Testing.Projects.TestMatrices.Delete) super.setAccessToken(accessToken);
        }

        public Testing.Projects.TestMatrices.Delete setAlt(String alt) {
          return (Testing.Projects.TestMatrices.Delete) super.setAlt(alt);
        }

        public Testing.Projects.TestMatrices.Delete setBearerToken(String bearerToken) {
          return (Testing.Projects.TestMatrices.Delete) super.setBearerToken(bearerToken);
        }

        public Testing.Projects.TestMatrices.Delete setCallback(String callback) {
          return (Testing.Projects.TestMatrices.Delete) super.setCallback(callback);
        }

        public Testing.Projects.TestMatrices.Delete setFields(String fields) {
          return (Testing.Projects.TestMatrices.Delete) super.setFields(fields);
        }

        public Testing.Projects.TestMatrices.Delete setKey(String key) {
          return (Testing.Projects.TestMatrices.Delete) super.setKey(key);
        }

        public Testing.Projects.TestMatrices.Delete setOauthToken(String oauthToken) {
          return (Testing.Projects.TestMatrices.Delete) super.setOauthToken(oauthToken);
        }

        public Testing.Projects.TestMatrices.Delete setPp(Boolean pp) {
          return (Testing.Projects.TestMatrices.Delete) super.setPp(pp);
        }

        public Testing.Projects.TestMatrices.Delete setPrettyPrint(Boolean prettyPrint) {
          return (Testing.Projects.TestMatrices.Delete) super.setPrettyPrint(prettyPrint);
        }

        public Testing.Projects.TestMatrices.Delete setQuotaUser(String quotaUser) {
          return (Testing.Projects.TestMatrices.Delete) super.setQuotaUser(quotaUser);
        }

        public Testing.Projects.TestMatrices.Delete setUploadType(String uploadType) {
          return (Testing.Projects.TestMatrices.Delete) super.setUploadType(uploadType);
        }

        public Testing.Projects.TestMatrices.Delete setUploadProtocol(String uploadProtocol) {
          return (Testing.Projects.TestMatrices.Delete) super.setUploadProtocol(uploadProtocol);
        }

        public String getProjectId() {
          return this.projectId;
        }

        public Testing.Projects.TestMatrices.Delete setProjectId(String projectId) {
          this.projectId = projectId;
          return this;
        }

        public String getTestMatrixId() {
          return this.testMatrixId;
        }

        public Testing.Projects.TestMatrices.Delete setTestMatrixId(String testMatrixId) {
          this.testMatrixId = testMatrixId;
          return this;
        }

        public Testing.Projects.TestMatrices.Delete set(String parameterName, Object value) {
          return (Testing.Projects.TestMatrices.Delete) super.set(parameterName, value);
        }
      }

      public class Create extends TestingRequest<TestMatrix> {
        private static final String REST_PATH = "v1/projects/{projectId}/testMatrices";
        @Key private String projectId;

        protected Create(String projectId, TestMatrix content) {
          super(
              Testing.this,
              "POST",
              "v1/projects/{projectId}/testMatrices",
              content,
              TestMatrix.class);
          this.projectId =
              (String)
                  Preconditions.checkNotNull(
                      projectId, "Required parameter projectId must be specified.");
        }

        public Testing.Projects.TestMatrices.Create set$Xgafv(String $Xgafv) {
          return (Testing.Projects.TestMatrices.Create) super.set$Xgafv($Xgafv);
        }

        public Testing.Projects.TestMatrices.Create setAccessToken(String accessToken) {
          return (Testing.Projects.TestMatrices.Create) super.setAccessToken(accessToken);
        }

        public Testing.Projects.TestMatrices.Create setAlt(String alt) {
          return (Testing.Projects.TestMatrices.Create) super.setAlt(alt);
        }

        public Testing.Projects.TestMatrices.Create setBearerToken(String bearerToken) {
          return (Testing.Projects.TestMatrices.Create) super.setBearerToken(bearerToken);
        }

        public Testing.Projects.TestMatrices.Create setCallback(String callback) {
          return (Testing.Projects.TestMatrices.Create) super.setCallback(callback);
        }

        public Testing.Projects.TestMatrices.Create setFields(String fields) {
          return (Testing.Projects.TestMatrices.Create) super.setFields(fields);
        }

        public Testing.Projects.TestMatrices.Create setKey(String key) {
          return (Testing.Projects.TestMatrices.Create) super.setKey(key);
        }

        public Testing.Projects.TestMatrices.Create setOauthToken(String oauthToken) {
          return (Testing.Projects.TestMatrices.Create) super.setOauthToken(oauthToken);
        }

        public Testing.Projects.TestMatrices.Create setPp(Boolean pp) {
          return (Testing.Projects.TestMatrices.Create) super.setPp(pp);
        }

        public Testing.Projects.TestMatrices.Create setPrettyPrint(Boolean prettyPrint) {
          return (Testing.Projects.TestMatrices.Create) super.setPrettyPrint(prettyPrint);
        }

        public Testing.Projects.TestMatrices.Create setQuotaUser(String quotaUser) {
          return (Testing.Projects.TestMatrices.Create) super.setQuotaUser(quotaUser);
        }

        public Testing.Projects.TestMatrices.Create setUploadType(String uploadType) {
          return (Testing.Projects.TestMatrices.Create) super.setUploadType(uploadType);
        }

        public Testing.Projects.TestMatrices.Create setUploadProtocol(String uploadProtocol) {
          return (Testing.Projects.TestMatrices.Create) super.setUploadProtocol(uploadProtocol);
        }

        public String getProjectId() {
          return this.projectId;
        }

        public Testing.Projects.TestMatrices.Create setProjectId(String projectId) {
          this.projectId = projectId;
          return this;
        }

        public Testing.Projects.TestMatrices.Create set(String parameterName, Object value) {
          return (Testing.Projects.TestMatrices.Create) super.set(parameterName, value);
        }
      }

      public class Cancel extends TestingRequest<CancelTestMatrixResponse> {
        private static final String REST_PATH =
            "v1/projects/{projectId}/testMatrices/{testMatrixId}:cancel";
        @Key private String projectId;
        @Key private String testMatrixId;

        protected Cancel(String projectId, String testMatrixId) {
          super(
              Testing.this,
              "POST",
              "v1/projects/{projectId}/testMatrices/{testMatrixId}:cancel",
              (Object) null,
              CancelTestMatrixResponse.class);
          this.projectId =
              (String)
                  Preconditions.checkNotNull(
                      projectId, "Required parameter projectId must be specified.");
          this.testMatrixId =
              (String)
                  Preconditions.checkNotNull(
                      testMatrixId, "Required parameter testMatrixId must be specified.");
        }

        public Testing.Projects.TestMatrices.Cancel set$Xgafv(String $Xgafv) {
          return (Testing.Projects.TestMatrices.Cancel) super.set$Xgafv($Xgafv);
        }

        public Testing.Projects.TestMatrices.Cancel setAccessToken(String accessToken) {
          return (Testing.Projects.TestMatrices.Cancel) super.setAccessToken(accessToken);
        }

        public Testing.Projects.TestMatrices.Cancel setAlt(String alt) {
          return (Testing.Projects.TestMatrices.Cancel) super.setAlt(alt);
        }

        public Testing.Projects.TestMatrices.Cancel setBearerToken(String bearerToken) {
          return (Testing.Projects.TestMatrices.Cancel) super.setBearerToken(bearerToken);
        }

        public Testing.Projects.TestMatrices.Cancel setCallback(String callback) {
          return (Testing.Projects.TestMatrices.Cancel) super.setCallback(callback);
        }

        public Testing.Projects.TestMatrices.Cancel setFields(String fields) {
          return (Testing.Projects.TestMatrices.Cancel) super.setFields(fields);
        }

        public Testing.Projects.TestMatrices.Cancel setKey(String key) {
          return (Testing.Projects.TestMatrices.Cancel) super.setKey(key);
        }

        public Testing.Projects.TestMatrices.Cancel setOauthToken(String oauthToken) {
          return (Testing.Projects.TestMatrices.Cancel) super.setOauthToken(oauthToken);
        }

        public Testing.Projects.TestMatrices.Cancel setPp(Boolean pp) {
          return (Testing.Projects.TestMatrices.Cancel) super.setPp(pp);
        }

        public Testing.Projects.TestMatrices.Cancel setPrettyPrint(Boolean prettyPrint) {
          return (Testing.Projects.TestMatrices.Cancel) super.setPrettyPrint(prettyPrint);
        }

        public Testing.Projects.TestMatrices.Cancel setQuotaUser(String quotaUser) {
          return (Testing.Projects.TestMatrices.Cancel) super.setQuotaUser(quotaUser);
        }

        public Testing.Projects.TestMatrices.Cancel setUploadType(String uploadType) {
          return (Testing.Projects.TestMatrices.Cancel) super.setUploadType(uploadType);
        }

        public Testing.Projects.TestMatrices.Cancel setUploadProtocol(String uploadProtocol) {
          return (Testing.Projects.TestMatrices.Cancel) super.setUploadProtocol(uploadProtocol);
        }

        public String getProjectId() {
          return this.projectId;
        }

        public Testing.Projects.TestMatrices.Cancel setProjectId(String projectId) {
          this.projectId = projectId;
          return this;
        }

        public String getTestMatrixId() {
          return this.testMatrixId;
        }

        public Testing.Projects.TestMatrices.Cancel setTestMatrixId(String testMatrixId) {
          this.testMatrixId = testMatrixId;
          return this;
        }

        public Testing.Projects.TestMatrices.Cancel set(String parameterName, Object value) {
          return (Testing.Projects.TestMatrices.Cancel) super.set(parameterName, value);
        }
      }
    }

    public class Devices {
      public Testing.Projects.Devices.Create create(String projectId, Device content)
          throws IOException {
        Testing.Projects.Devices.Create result =
            new Testing.Projects.Devices.Create(projectId, content);
        Testing.this.initialize(result);
        return result;
      }

      public Testing.Projects.Devices.Delete delete(String projectId, String deviceId)
          throws IOException {
        Testing.Projects.Devices.Delete result =
            new Testing.Projects.Devices.Delete(projectId, deviceId);
        Testing.this.initialize(result);
        return result;
      }

      public Testing.Projects.Devices.Get get(String projectId, String deviceId)
          throws IOException {
        Testing.Projects.Devices.Get result = new Testing.Projects.Devices.Get(projectId, deviceId);
        Testing.this.initialize(result);
        return result;
      }

      public Testing.Projects.Devices.Keepalive keepalive(String projectId, String deviceId)
          throws IOException {
        Testing.Projects.Devices.Keepalive result =
            new Testing.Projects.Devices.Keepalive(projectId, deviceId);
        Testing.this.initialize(result);
        return result;
      }

      public Testing.Projects.Devices.List list(String projectId) throws IOException {
        Testing.Projects.Devices.List result = new Testing.Projects.Devices.List(projectId);
        Testing.this.initialize(result);
        return result;
      }

      public class List extends TestingRequest<ListDevicesResponse> {
        private static final String REST_PATH = "v1/projects/{projectId}/devices";
        @Key private String projectId;
        @Key private Integer pageSize;
        @Key private String pageToken;

        protected List(String projectId) {
          super(
              Testing.this,
              "GET",
              "v1/projects/{projectId}/devices",
              (Object) null,
              ListDevicesResponse.class);
          this.projectId =
              (String)
                  Preconditions.checkNotNull(
                      projectId, "Required parameter projectId must be specified.");
        }

        public HttpResponse executeUsingHead() throws IOException {
          return super.executeUsingHead();
        }

        public HttpRequest buildHttpRequestUsingHead() throws IOException {
          return super.buildHttpRequestUsingHead();
        }

        public Testing.Projects.Devices.List set$Xgafv(String $Xgafv) {
          return (Testing.Projects.Devices.List) super.set$Xgafv($Xgafv);
        }

        public Testing.Projects.Devices.List setAccessToken(String accessToken) {
          return (Testing.Projects.Devices.List) super.setAccessToken(accessToken);
        }

        public Testing.Projects.Devices.List setAlt(String alt) {
          return (Testing.Projects.Devices.List) super.setAlt(alt);
        }

        public Testing.Projects.Devices.List setBearerToken(String bearerToken) {
          return (Testing.Projects.Devices.List) super.setBearerToken(bearerToken);
        }

        public Testing.Projects.Devices.List setCallback(String callback) {
          return (Testing.Projects.Devices.List) super.setCallback(callback);
        }

        public Testing.Projects.Devices.List setFields(String fields) {
          return (Testing.Projects.Devices.List) super.setFields(fields);
        }

        public Testing.Projects.Devices.List setKey(String key) {
          return (Testing.Projects.Devices.List) super.setKey(key);
        }

        public Testing.Projects.Devices.List setOauthToken(String oauthToken) {
          return (Testing.Projects.Devices.List) super.setOauthToken(oauthToken);
        }

        public Testing.Projects.Devices.List setPp(Boolean pp) {
          return (Testing.Projects.Devices.List) super.setPp(pp);
        }

        public Testing.Projects.Devices.List setPrettyPrint(Boolean prettyPrint) {
          return (Testing.Projects.Devices.List) super.setPrettyPrint(prettyPrint);
        }

        public Testing.Projects.Devices.List setQuotaUser(String quotaUser) {
          return (Testing.Projects.Devices.List) super.setQuotaUser(quotaUser);
        }

        public Testing.Projects.Devices.List setUploadType(String uploadType) {
          return (Testing.Projects.Devices.List) super.setUploadType(uploadType);
        }

        public Testing.Projects.Devices.List setUploadProtocol(String uploadProtocol) {
          return (Testing.Projects.Devices.List) super.setUploadProtocol(uploadProtocol);
        }

        public String getProjectId() {
          return this.projectId;
        }

        public Testing.Projects.Devices.List setProjectId(String projectId) {
          this.projectId = projectId;
          return this;
        }

        public Integer getPageSize() {
          return this.pageSize;
        }

        public Testing.Projects.Devices.List setPageSize(Integer pageSize) {
          this.pageSize = pageSize;
          return this;
        }

        public String getPageToken() {
          return this.pageToken;
        }

        public Testing.Projects.Devices.List setPageToken(String pageToken) {
          this.pageToken = pageToken;
          return this;
        }

        public Testing.Projects.Devices.List set(String parameterName, Object value) {
          return (Testing.Projects.Devices.List) super.set(parameterName, value);
        }
      }

      public class Keepalive extends TestingRequest<Empty> {
        private static final String REST_PATH =
            "v1/projects/{projectId}/devices/{deviceId}/keepalive";
        @Key private String projectId;
        @Key private String deviceId;

        protected Keepalive(String projectId, String deviceId) {
          super(
              Testing.this,
              "POST",
              "v1/projects/{projectId}/devices/{deviceId}/keepalive",
              (Object) null,
              Empty.class);
          this.projectId =
              (String)
                  Preconditions.checkNotNull(
                      projectId, "Required parameter projectId must be specified.");
          this.deviceId =
              (String)
                  Preconditions.checkNotNull(
                      deviceId, "Required parameter deviceId must be specified.");
        }

        public Testing.Projects.Devices.Keepalive set$Xgafv(String $Xgafv) {
          return (Testing.Projects.Devices.Keepalive) super.set$Xgafv($Xgafv);
        }

        public Testing.Projects.Devices.Keepalive setAccessToken(String accessToken) {
          return (Testing.Projects.Devices.Keepalive) super.setAccessToken(accessToken);
        }

        public Testing.Projects.Devices.Keepalive setAlt(String alt) {
          return (Testing.Projects.Devices.Keepalive) super.setAlt(alt);
        }

        public Testing.Projects.Devices.Keepalive setBearerToken(String bearerToken) {
          return (Testing.Projects.Devices.Keepalive) super.setBearerToken(bearerToken);
        }

        public Testing.Projects.Devices.Keepalive setCallback(String callback) {
          return (Testing.Projects.Devices.Keepalive) super.setCallback(callback);
        }

        public Testing.Projects.Devices.Keepalive setFields(String fields) {
          return (Testing.Projects.Devices.Keepalive) super.setFields(fields);
        }

        public Testing.Projects.Devices.Keepalive setKey(String key) {
          return (Testing.Projects.Devices.Keepalive) super.setKey(key);
        }

        public Testing.Projects.Devices.Keepalive setOauthToken(String oauthToken) {
          return (Testing.Projects.Devices.Keepalive) super.setOauthToken(oauthToken);
        }

        public Testing.Projects.Devices.Keepalive setPp(Boolean pp) {
          return (Testing.Projects.Devices.Keepalive) super.setPp(pp);
        }

        public Testing.Projects.Devices.Keepalive setPrettyPrint(Boolean prettyPrint) {
          return (Testing.Projects.Devices.Keepalive) super.setPrettyPrint(prettyPrint);
        }

        public Testing.Projects.Devices.Keepalive setQuotaUser(String quotaUser) {
          return (Testing.Projects.Devices.Keepalive) super.setQuotaUser(quotaUser);
        }

        public Testing.Projects.Devices.Keepalive setUploadType(String uploadType) {
          return (Testing.Projects.Devices.Keepalive) super.setUploadType(uploadType);
        }

        public Testing.Projects.Devices.Keepalive setUploadProtocol(String uploadProtocol) {
          return (Testing.Projects.Devices.Keepalive) super.setUploadProtocol(uploadProtocol);
        }

        public String getProjectId() {
          return this.projectId;
        }

        public Testing.Projects.Devices.Keepalive setProjectId(String projectId) {
          this.projectId = projectId;
          return this;
        }

        public String getDeviceId() {
          return this.deviceId;
        }

        public Testing.Projects.Devices.Keepalive setDeviceId(String deviceId) {
          this.deviceId = deviceId;
          return this;
        }

        public Testing.Projects.Devices.Keepalive set(String parameterName, Object value) {
          return (Testing.Projects.Devices.Keepalive) super.set(parameterName, value);
        }
      }

      public class Get extends TestingRequest<Device> {
        private static final String REST_PATH = "v1/projects/{projectId}/devices/{deviceId}";
        @Key private String projectId;
        @Key private String deviceId;

        protected Get(String projectId, String deviceId) {
          super(
              Testing.this,
              "GET",
              "v1/projects/{projectId}/devices/{deviceId}",
              (Object) null,
              Device.class);
          this.projectId =
              (String)
                  Preconditions.checkNotNull(
                      projectId, "Required parameter projectId must be specified.");
          this.deviceId =
              (String)
                  Preconditions.checkNotNull(
                      deviceId, "Required parameter deviceId must be specified.");
        }

        public HttpResponse executeUsingHead() throws IOException {
          return super.executeUsingHead();
        }

        public HttpRequest buildHttpRequestUsingHead() throws IOException {
          return super.buildHttpRequestUsingHead();
        }

        public Testing.Projects.Devices.Get set$Xgafv(String $Xgafv) {
          return (Testing.Projects.Devices.Get) super.set$Xgafv($Xgafv);
        }

        public Testing.Projects.Devices.Get setAccessToken(String accessToken) {
          return (Testing.Projects.Devices.Get) super.setAccessToken(accessToken);
        }

        public Testing.Projects.Devices.Get setAlt(String alt) {
          return (Testing.Projects.Devices.Get) super.setAlt(alt);
        }

        public Testing.Projects.Devices.Get setBearerToken(String bearerToken) {
          return (Testing.Projects.Devices.Get) super.setBearerToken(bearerToken);
        }

        public Testing.Projects.Devices.Get setCallback(String callback) {
          return (Testing.Projects.Devices.Get) super.setCallback(callback);
        }

        public Testing.Projects.Devices.Get setFields(String fields) {
          return (Testing.Projects.Devices.Get) super.setFields(fields);
        }

        public Testing.Projects.Devices.Get setKey(String key) {
          return (Testing.Projects.Devices.Get) super.setKey(key);
        }

        public Testing.Projects.Devices.Get setOauthToken(String oauthToken) {
          return (Testing.Projects.Devices.Get) super.setOauthToken(oauthToken);
        }

        public Testing.Projects.Devices.Get setPp(Boolean pp) {
          return (Testing.Projects.Devices.Get) super.setPp(pp);
        }

        public Testing.Projects.Devices.Get setPrettyPrint(Boolean prettyPrint) {
          return (Testing.Projects.Devices.Get) super.setPrettyPrint(prettyPrint);
        }

        public Testing.Projects.Devices.Get setQuotaUser(String quotaUser) {
          return (Testing.Projects.Devices.Get) super.setQuotaUser(quotaUser);
        }

        public Testing.Projects.Devices.Get setUploadType(String uploadType) {
          return (Testing.Projects.Devices.Get) super.setUploadType(uploadType);
        }

        public Testing.Projects.Devices.Get setUploadProtocol(String uploadProtocol) {
          return (Testing.Projects.Devices.Get) super.setUploadProtocol(uploadProtocol);
        }

        public String getProjectId() {
          return this.projectId;
        }

        public Testing.Projects.Devices.Get setProjectId(String projectId) {
          this.projectId = projectId;
          return this;
        }

        public String getDeviceId() {
          return this.deviceId;
        }

        public Testing.Projects.Devices.Get setDeviceId(String deviceId) {
          this.deviceId = deviceId;
          return this;
        }

        public Testing.Projects.Devices.Get set(String parameterName, Object value) {
          return (Testing.Projects.Devices.Get) super.set(parameterName, value);
        }
      }

      public class Delete extends TestingRequest<Empty> {
        private static final String REST_PATH = "v1/projects/{projectId}/devices/{deviceId}";
        @Key private String projectId;
        @Key private String deviceId;

        protected Delete(String projectId, String deviceId) {
          super(
              Testing.this,
              "DELETE",
              "v1/projects/{projectId}/devices/{deviceId}",
              (Object) null,
              Empty.class);
          this.projectId =
              (String)
                  Preconditions.checkNotNull(
                      projectId, "Required parameter projectId must be specified.");
          this.deviceId =
              (String)
                  Preconditions.checkNotNull(
                      deviceId, "Required parameter deviceId must be specified.");
        }

        public Testing.Projects.Devices.Delete set$Xgafv(String $Xgafv) {
          return (Testing.Projects.Devices.Delete) super.set$Xgafv($Xgafv);
        }

        public Testing.Projects.Devices.Delete setAccessToken(String accessToken) {
          return (Testing.Projects.Devices.Delete) super.setAccessToken(accessToken);
        }

        public Testing.Projects.Devices.Delete setAlt(String alt) {
          return (Testing.Projects.Devices.Delete) super.setAlt(alt);
        }

        public Testing.Projects.Devices.Delete setBearerToken(String bearerToken) {
          return (Testing.Projects.Devices.Delete) super.setBearerToken(bearerToken);
        }

        public Testing.Projects.Devices.Delete setCallback(String callback) {
          return (Testing.Projects.Devices.Delete) super.setCallback(callback);
        }

        public Testing.Projects.Devices.Delete setFields(String fields) {
          return (Testing.Projects.Devices.Delete) super.setFields(fields);
        }

        public Testing.Projects.Devices.Delete setKey(String key) {
          return (Testing.Projects.Devices.Delete) super.setKey(key);
        }

        public Testing.Projects.Devices.Delete setOauthToken(String oauthToken) {
          return (Testing.Projects.Devices.Delete) super.setOauthToken(oauthToken);
        }

        public Testing.Projects.Devices.Delete setPp(Boolean pp) {
          return (Testing.Projects.Devices.Delete) super.setPp(pp);
        }

        public Testing.Projects.Devices.Delete setPrettyPrint(Boolean prettyPrint) {
          return (Testing.Projects.Devices.Delete) super.setPrettyPrint(prettyPrint);
        }

        public Testing.Projects.Devices.Delete setQuotaUser(String quotaUser) {
          return (Testing.Projects.Devices.Delete) super.setQuotaUser(quotaUser);
        }

        public Testing.Projects.Devices.Delete setUploadType(String uploadType) {
          return (Testing.Projects.Devices.Delete) super.setUploadType(uploadType);
        }

        public Testing.Projects.Devices.Delete setUploadProtocol(String uploadProtocol) {
          return (Testing.Projects.Devices.Delete) super.setUploadProtocol(uploadProtocol);
        }

        public String getProjectId() {
          return this.projectId;
        }

        public Testing.Projects.Devices.Delete setProjectId(String projectId) {
          this.projectId = projectId;
          return this;
        }

        public String getDeviceId() {
          return this.deviceId;
        }

        public Testing.Projects.Devices.Delete setDeviceId(String deviceId) {
          this.deviceId = deviceId;
          return this;
        }

        public Testing.Projects.Devices.Delete set(String parameterName, Object value) {
          return (Testing.Projects.Devices.Delete) super.set(parameterName, value);
        }
      }

      public class Create extends TestingRequest<Device> {
        private static final String REST_PATH = "v1/projects/{projectId}/devices";
        @Key private String projectId;
        @Key private String sshPublicKey;

        protected Create(String projectId, Device content) {
          super(Testing.this, "POST", "v1/projects/{projectId}/devices", content, Device.class);
          this.projectId =
              (String)
                  Preconditions.checkNotNull(
                      projectId, "Required parameter projectId must be specified.");
        }

        public Testing.Projects.Devices.Create set$Xgafv(String $Xgafv) {
          return (Testing.Projects.Devices.Create) super.set$Xgafv($Xgafv);
        }

        public Testing.Projects.Devices.Create setAccessToken(String accessToken) {
          return (Testing.Projects.Devices.Create) super.setAccessToken(accessToken);
        }

        public Testing.Projects.Devices.Create setAlt(String alt) {
          return (Testing.Projects.Devices.Create) super.setAlt(alt);
        }

        public Testing.Projects.Devices.Create setBearerToken(String bearerToken) {
          return (Testing.Projects.Devices.Create) super.setBearerToken(bearerToken);
        }

        public Testing.Projects.Devices.Create setCallback(String callback) {
          return (Testing.Projects.Devices.Create) super.setCallback(callback);
        }

        public Testing.Projects.Devices.Create setFields(String fields) {
          return (Testing.Projects.Devices.Create) super.setFields(fields);
        }

        public Testing.Projects.Devices.Create setKey(String key) {
          return (Testing.Projects.Devices.Create) super.setKey(key);
        }

        public Testing.Projects.Devices.Create setOauthToken(String oauthToken) {
          return (Testing.Projects.Devices.Create) super.setOauthToken(oauthToken);
        }

        public Testing.Projects.Devices.Create setPp(Boolean pp) {
          return (Testing.Projects.Devices.Create) super.setPp(pp);
        }

        public Testing.Projects.Devices.Create setPrettyPrint(Boolean prettyPrint) {
          return (Testing.Projects.Devices.Create) super.setPrettyPrint(prettyPrint);
        }

        public Testing.Projects.Devices.Create setQuotaUser(String quotaUser) {
          return (Testing.Projects.Devices.Create) super.setQuotaUser(quotaUser);
        }

        public Testing.Projects.Devices.Create setUploadType(String uploadType) {
          return (Testing.Projects.Devices.Create) super.setUploadType(uploadType);
        }

        public Testing.Projects.Devices.Create setUploadProtocol(String uploadProtocol) {
          return (Testing.Projects.Devices.Create) super.setUploadProtocol(uploadProtocol);
        }

        public String getProjectId() {
          return this.projectId;
        }

        public Testing.Projects.Devices.Create setProjectId(String projectId) {
          this.projectId = projectId;
          return this;
        }

        public String getSshPublicKey() {
          return this.sshPublicKey;
        }

        public Testing.Projects.Devices.Create setSshPublicKey(String sshPublicKey) {
          this.sshPublicKey = sshPublicKey;
          return this;
        }

        public Testing.Projects.Devices.Create set(String parameterName, Object value) {
          return (Testing.Projects.Devices.Create) super.set(parameterName, value);
        }
      }
    }
  }
}
