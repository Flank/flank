/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://github.com/google/apis-client-generator/
 * Modify at your own risk.
 */

package com.google.testing;

/**
 * Service definition for Testing (v1).
 *
 * <p>
 * Allows developers to run automated tests for their mobile applications on Google infrastructure.
 * </p>
 *
 * <p>
 * For more information about this service, see the
 * <a href="https://developers.google.com/cloud-test-lab/" target="_blank">API Documentation</a>
 * </p>
 *
 * <p>
 * This service uses {@link TestingRequestInitializer} to initialize global parameters via its
 * {@link Builder}.
 * </p>
 *
 * @since 1.3
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public class Testing extends com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient {

  // Note: Leave this static initializer at the top of the file.
  static {
    com.google.api.client.util.Preconditions.checkState(
        com.google.api.client.googleapis.GoogleUtils.MAJOR_VERSION == 1 &&
        com.google.api.client.googleapis.GoogleUtils.MINOR_VERSION >= 15,
        "You are currently running with version %s of google-api-client. " +
        "You need at least version 1.15 of google-api-client to run version " +
        "1.22.0-SNAPSHOT of the Cloud Testing API library.", com.google.api.client.googleapis.GoogleUtils.VERSION);
  }

  /**
   * The default encoded root URL of the service. This is determined when the library is generated
   * and normally should not be changed.
   *
   * @since 1.7
   */
  public static final String DEFAULT_ROOT_URL = "https://testing.googleapis.com/";

  /**
   * The default encoded service path of the service. This is determined when the library is
   * generated and normally should not be changed.
   *
   * @since 1.7
   */
  public static final String DEFAULT_SERVICE_PATH = "";

  /**
   * The default encoded batch path of the service. This is determined when the library is
   * generated and normally should not be changed.
   *
   * @since 1.23
   */
  public static final String DEFAULT_BATCH_PATH = "batch";

  /**
   * The default encoded base URL of the service. This is determined when the library is generated
   * and normally should not be changed.
   */
  public static final String DEFAULT_BASE_URL = DEFAULT_ROOT_URL + DEFAULT_SERVICE_PATH;

  /**
   * Constructor.
   *
   * <p>
   * Use {@link Builder} if you need to specify any of the optional parameters.
   * </p>
   *
   * @param transport HTTP transport, which should normally be:
   *        <ul>
   *        <li>Google App Engine:
   *        {@code com.google.api.client.extensions.appengine.http.UrlFetchTransport}</li>
   *        <li>Android: {@code newCompatibleTransport} from
   *        {@code com.google.api.client.extensions.android.http.AndroidHttp}</li>
   *        <li>Java: {@link com.google.api.client.googleapis.javanet.GoogleNetHttpTransport#newTrustedTransport()}
   *        </li>
   *        </ul>
   * @param jsonFactory JSON factory, which may be:
   *        <ul>
   *        <li>Jackson: {@code com.google.api.client.json.jackson2.JacksonFactory}</li>
   *        <li>Google GSON: {@code com.google.api.client.json.gson.GsonFactory}</li>
   *        <li>Android Honeycomb or higher:
   *        {@code com.google.api.client.extensions.android.json.AndroidJsonFactory}</li>
   *        </ul>
   * @param httpRequestInitializer HTTP request initializer or {@code null} for none
   * @since 1.7
   */
  public Testing(com.google.api.client.http.HttpTransport transport, com.google.api.client.json.JsonFactory jsonFactory,
      com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
    this(new Builder(transport, jsonFactory, httpRequestInitializer));
  }

  /**
   * @param builder builder
   */
  Testing(Builder builder) {
    super(builder);
  }

  @Override
  protected void initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest<?> httpClientRequest) throws java.io.IOException {
    super.initialize(httpClientRequest);
  }

  /**
   * An accessor for creating requests from the ApplicationDetailService collection.
   *
   * <p>The typical use is:</p>
   * <pre>
   *   {@code Testing testing = new Testing(...);}
   *   {@code Testing.ApplicationDetailService.List request = testing.applicationDetailService().list(parameters ...)}
   * </pre>
   *
   * @return the resource collection
   */
  public ApplicationDetailService applicationDetailService() {
    return new ApplicationDetailService();
  }

  /**
   * The "applicationDetailService" collection of methods.
   */
  public class ApplicationDetailService {

    /**
     * Request the details of an Android application APK.
     *
     * Create a request for the method "applicationDetailService.getApkDetails".
     *
     * This request holds the parameters needed by the testing server.  After setting any optional
     * parameters, call the {@link GetApkDetails#execute()} method to invoke the remote operation.
     *
     * @param content the {@link com.google.testing.model.FileReference}
     * @return the request
     */
    public GetApkDetails getApkDetails(com.google.testing.model.FileReference content) throws java.io.IOException {
      GetApkDetails result = new GetApkDetails(content);
      initialize(result);
      return result;
    }

    public class GetApkDetails extends TestingRequest<com.google.testing.model.GetApkDetailsResponse> {

      private static final String REST_PATH = "v1/applicationDetailService/getApkDetails";

      /**
       * Request the details of an Android application APK.
       *
       * Create a request for the method "applicationDetailService.getApkDetails".
       *
       * This request holds the parameters needed by the the testing server.  After setting any optional
       * parameters, call the {@link GetApkDetails#execute()} method to invoke the remote operation. <p>
       * {@link GetApkDetails#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientR
       * equest)} must be called to initialize this instance immediately after invoking the constructor.
       * </p>
       *
       * @param content the {@link com.google.testing.model.FileReference}
       * @since 1.13
       */
      protected GetApkDetails(com.google.testing.model.FileReference content) {
        super(Testing.this, "POST", REST_PATH, content, com.google.testing.model.GetApkDetailsResponse.class);
      }

      @Override
      public GetApkDetails set$Xgafv(java.lang.String $Xgafv) {
        return (GetApkDetails) super.set$Xgafv($Xgafv);
      }

      @Override
      public GetApkDetails setAccessToken(java.lang.String accessToken) {
        return (GetApkDetails) super.setAccessToken(accessToken);
      }

      @Override
      public GetApkDetails setAlt(java.lang.String alt) {
        return (GetApkDetails) super.setAlt(alt);
      }

      @Override
      public GetApkDetails setBearerToken(java.lang.String bearerToken) {
        return (GetApkDetails) super.setBearerToken(bearerToken);
      }

      @Override
      public GetApkDetails setCallback(java.lang.String callback) {
        return (GetApkDetails) super.setCallback(callback);
      }

      @Override
      public GetApkDetails setFields(java.lang.String fields) {
        return (GetApkDetails) super.setFields(fields);
      }

      @Override
      public GetApkDetails setKey(java.lang.String key) {
        return (GetApkDetails) super.setKey(key);
      }

      @Override
      public GetApkDetails setOauthToken(java.lang.String oauthToken) {
        return (GetApkDetails) super.setOauthToken(oauthToken);
      }

      @Override
      public GetApkDetails setPp(java.lang.Boolean pp) {
        return (GetApkDetails) super.setPp(pp);
      }

      @Override
      public GetApkDetails setPrettyPrint(java.lang.Boolean prettyPrint) {
        return (GetApkDetails) super.setPrettyPrint(prettyPrint);
      }

      @Override
      public GetApkDetails setQuotaUser(java.lang.String quotaUser) {
        return (GetApkDetails) super.setQuotaUser(quotaUser);
      }

      @Override
      public GetApkDetails setUploadType(java.lang.String uploadType) {
        return (GetApkDetails) super.setUploadType(uploadType);
      }

      @Override
      public GetApkDetails setUploadProtocol(java.lang.String uploadProtocol) {
        return (GetApkDetails) super.setUploadProtocol(uploadProtocol);
      }

      @Override
      public GetApkDetails set(String parameterName, Object value) {
        return (GetApkDetails) super.set(parameterName, value);
      }
    }

  }

  /**
   * An accessor for creating requests from the Projects collection.
   *
   * <p>The typical use is:</p>
   * <pre>
   *   {@code Testing testing = new Testing(...);}
   *   {@code Testing.Projects.List request = testing.projects().list(parameters ...)}
   * </pre>
   *
   * @return the resource collection
   */
  public Projects projects() {
    return new Projects();
  }

  /**
   * The "projects" collection of methods.
   */
  public class Projects {

    /**
     * An accessor for creating requests from the TestMatrices collection.
     *
     * <p>The typical use is:</p>
     * <pre>
     *   {@code Testing testing = new Testing(...);}
     *   {@code Testing.TestMatrices.List request = testing.testMatrices().list(parameters ...)}
     * </pre>
     *
     * @return the resource collection
     */
    public TestMatrices testMatrices() {
      return new TestMatrices();
    }

    /**
     * The "testMatrices" collection of methods.
     */
    public class TestMatrices {

      /**
       * Cancels unfinished test executions in a test matrix. This call returns immediately and
       * cancellation proceeds asychronously. If the matrix is already final, this operation will have no
       * effect.
       *
       * May return any of the following canonical error codes:
       *
       * - PERMISSION_DENIED - if the user is not authorized to read project - INVALID_ARGUMENT - if the
       * request is malformed - NOT_FOUND - if the Test Matrix does not exist
       *
       * Create a request for the method "testMatrices.cancel".
       *
       * This request holds the parameters needed by the testing server.  After setting any optional
       * parameters, call the {@link Cancel#execute()} method to invoke the remote operation.
       *
       * @param projectId Cloud project that owns the test.
       * @param testMatrixId Test matrix that will be canceled.
       * @return the request
       */
      public Cancel cancel(java.lang.String projectId, java.lang.String testMatrixId) throws java.io.IOException {
        Cancel result = new Cancel(projectId, testMatrixId);
        initialize(result);
        return result;
      }

      public class Cancel extends TestingRequest<com.google.testing.model.CancelTestMatrixResponse> {

        private static final String REST_PATH = "v1/projects/{projectId}/testMatrices/{testMatrixId}:cancel";

        /**
         * Cancels unfinished test executions in a test matrix. This call returns immediately and
         * cancellation proceeds asychronously. If the matrix is already final, this operation will have
         * no effect.
         *
         * May return any of the following canonical error codes:
         *
         * - PERMISSION_DENIED - if the user is not authorized to read project - INVALID_ARGUMENT - if the
         * request is malformed - NOT_FOUND - if the Test Matrix does not exist
         *
         * Create a request for the method "testMatrices.cancel".
         *
         * This request holds the parameters needed by the the testing server.  After setting any optional
         * parameters, call the {@link Cancel#execute()} method to invoke the remote operation. <p> {@link
         * Cancel#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)} must
         * be called to initialize this instance immediately after invoking the constructor. </p>
         *
         * @param projectId Cloud project that owns the test.
         * @param testMatrixId Test matrix that will be canceled.
         * @since 1.13
         */
        protected Cancel(java.lang.String projectId, java.lang.String testMatrixId) {
          super(Testing.this, "POST", REST_PATH, null, com.google.testing.model.CancelTestMatrixResponse.class);
          this.projectId = com.google.api.client.util.Preconditions.checkNotNull(projectId, "Required parameter projectId must be specified.");
          this.testMatrixId = com.google.api.client.util.Preconditions.checkNotNull(testMatrixId, "Required parameter testMatrixId must be specified.");
        }

        @Override
        public Cancel set$Xgafv(java.lang.String $Xgafv) {
          return (Cancel) super.set$Xgafv($Xgafv);
        }

        @Override
        public Cancel setAccessToken(java.lang.String accessToken) {
          return (Cancel) super.setAccessToken(accessToken);
        }

        @Override
        public Cancel setAlt(java.lang.String alt) {
          return (Cancel) super.setAlt(alt);
        }

        @Override
        public Cancel setBearerToken(java.lang.String bearerToken) {
          return (Cancel) super.setBearerToken(bearerToken);
        }

        @Override
        public Cancel setCallback(java.lang.String callback) {
          return (Cancel) super.setCallback(callback);
        }

        @Override
        public Cancel setFields(java.lang.String fields) {
          return (Cancel) super.setFields(fields);
        }

        @Override
        public Cancel setKey(java.lang.String key) {
          return (Cancel) super.setKey(key);
        }

        @Override
        public Cancel setOauthToken(java.lang.String oauthToken) {
          return (Cancel) super.setOauthToken(oauthToken);
        }

        @Override
        public Cancel setPp(java.lang.Boolean pp) {
          return (Cancel) super.setPp(pp);
        }

        @Override
        public Cancel setPrettyPrint(java.lang.Boolean prettyPrint) {
          return (Cancel) super.setPrettyPrint(prettyPrint);
        }

        @Override
        public Cancel setQuotaUser(java.lang.String quotaUser) {
          return (Cancel) super.setQuotaUser(quotaUser);
        }

        @Override
        public Cancel setUploadType(java.lang.String uploadType) {
          return (Cancel) super.setUploadType(uploadType);
        }

        @Override
        public Cancel setUploadProtocol(java.lang.String uploadProtocol) {
          return (Cancel) super.setUploadProtocol(uploadProtocol);
        }

        /** Cloud project that owns the test. */
        @com.google.api.client.util.Key
        private java.lang.String projectId;

        /** Cloud project that owns the test.
         */
        public java.lang.String getProjectId() {
          return projectId;
        }

        /** Cloud project that owns the test. */
        public Cancel setProjectId(java.lang.String projectId) {
          this.projectId = projectId;
          return this;
        }

        /** Test matrix that will be canceled. */
        @com.google.api.client.util.Key
        private java.lang.String testMatrixId;

        /** Test matrix that will be canceled.
         */
        public java.lang.String getTestMatrixId() {
          return testMatrixId;
        }

        /** Test matrix that will be canceled. */
        public Cancel setTestMatrixId(java.lang.String testMatrixId) {
          this.testMatrixId = testMatrixId;
          return this;
        }

        @Override
        public Cancel set(String parameterName, Object value) {
          return (Cancel) super.set(parameterName, value);
        }
      }
      /**
       * Request to run a matrix of tests according to the given specifications. Unsupported environments
       * will be returned in the state UNSUPPORTED. Matrices are limited to at most 200 supported
       * executions.
       *
       * May return any of the following canonical error codes:
       *
       * - PERMISSION_DENIED - if the user is not authorized to write to project - INVALID_ARGUMENT - if
       * the request is malformed or if the matrix expands                      to more than 200 supported
       * executions
       *
       * Create a request for the method "testMatrices.create".
       *
       * This request holds the parameters needed by the testing server.  After setting any optional
       * parameters, call the {@link Create#execute()} method to invoke the remote operation.
       *
       * @param projectId The GCE project under which this job will run.
       * @param content the {@link com.google.testing.model.TestMatrix}
       * @return the request
       */
      public Create create(java.lang.String projectId, com.google.testing.model.TestMatrix content) throws java.io.IOException {
        Create result = new Create(projectId, content);
        initialize(result);
        return result;
      }

      public class Create extends TestingRequest<com.google.testing.model.TestMatrix> {

        private static final String REST_PATH = "v1/projects/{projectId}/testMatrices";

        /**
         * Request to run a matrix of tests according to the given specifications. Unsupported
         * environments will be returned in the state UNSUPPORTED. Matrices are limited to at most 200
         * supported executions.
         *
         * May return any of the following canonical error codes:
         *
         * - PERMISSION_DENIED - if the user is not authorized to write to project - INVALID_ARGUMENT - if
         * the request is malformed or if the matrix expands                      to more than 200
         * supported executions
         *
         * Create a request for the method "testMatrices.create".
         *
         * This request holds the parameters needed by the the testing server.  After setting any optional
         * parameters, call the {@link Create#execute()} method to invoke the remote operation. <p> {@link
         * Create#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)} must
         * be called to initialize this instance immediately after invoking the constructor. </p>
         *
         * @param projectId The GCE project under which this job will run.
         * @param content the {@link com.google.testing.model.TestMatrix}
         * @since 1.13
         */
        protected Create(java.lang.String projectId, com.google.testing.model.TestMatrix content) {
          super(Testing.this, "POST", REST_PATH, content, com.google.testing.model.TestMatrix.class);
          this.projectId = com.google.api.client.util.Preconditions.checkNotNull(projectId, "Required parameter projectId must be specified.");
        }

        @Override
        public Create set$Xgafv(java.lang.String $Xgafv) {
          return (Create) super.set$Xgafv($Xgafv);
        }

        @Override
        public Create setAccessToken(java.lang.String accessToken) {
          return (Create) super.setAccessToken(accessToken);
        }

        @Override
        public Create setAlt(java.lang.String alt) {
          return (Create) super.setAlt(alt);
        }

        @Override
        public Create setBearerToken(java.lang.String bearerToken) {
          return (Create) super.setBearerToken(bearerToken);
        }

        @Override
        public Create setCallback(java.lang.String callback) {
          return (Create) super.setCallback(callback);
        }

        @Override
        public Create setFields(java.lang.String fields) {
          return (Create) super.setFields(fields);
        }

        @Override
        public Create setKey(java.lang.String key) {
          return (Create) super.setKey(key);
        }

        @Override
        public Create setOauthToken(java.lang.String oauthToken) {
          return (Create) super.setOauthToken(oauthToken);
        }

        @Override
        public Create setPp(java.lang.Boolean pp) {
          return (Create) super.setPp(pp);
        }

        @Override
        public Create setPrettyPrint(java.lang.Boolean prettyPrint) {
          return (Create) super.setPrettyPrint(prettyPrint);
        }

        @Override
        public Create setQuotaUser(java.lang.String quotaUser) {
          return (Create) super.setQuotaUser(quotaUser);
        }

        @Override
        public Create setUploadType(java.lang.String uploadType) {
          return (Create) super.setUploadType(uploadType);
        }

        @Override
        public Create setUploadProtocol(java.lang.String uploadProtocol) {
          return (Create) super.setUploadProtocol(uploadProtocol);
        }

        /** The GCE project under which this job will run. */
        @com.google.api.client.util.Key
        private java.lang.String projectId;

        /** The GCE project under which this job will run.
         */
        public java.lang.String getProjectId() {
          return projectId;
        }

        /** The GCE project under which this job will run. */
        public Create setProjectId(java.lang.String projectId) {
          this.projectId = projectId;
          return this;
        }

        /**
         * A string id used to detect duplicated requests. Ids are automatically scoped to a
         * project, so users should ensure the ID is unique per-project. A UUID is recommended.
         *
         * Optional, but strongly recommended.
         */
        @com.google.api.client.util.Key
        private java.lang.String requestId;

        /** A string id used to detect duplicated requests. Ids are automatically scoped to a project, so users
       should ensure the ID is unique per-project. A UUID is recommended.

       Optional, but strongly recommended.
         */
        public java.lang.String getRequestId() {
          return requestId;
        }

        /**
         * A string id used to detect duplicated requests. Ids are automatically scoped to a
         * project, so users should ensure the ID is unique per-project. A UUID is recommended.
         *
         * Optional, but strongly recommended.
         */
        public Create setRequestId(java.lang.String requestId) {
          this.requestId = requestId;
          return this;
        }

        @Override
        public Create set(String parameterName, Object value) {
          return (Create) super.set(parameterName, value);
        }
      }
      /**
       * Check the status of a test matrix.
       *
       * May return any of the following canonical error codes:
       *
       * - PERMISSION_DENIED - if the user is not authorized to read project - INVALID_ARGUMENT - if the
       * request is malformed - NOT_FOUND - if the Test Matrix does not exist
       *
       * Create a request for the method "testMatrices.get".
       *
       * This request holds the parameters needed by the testing server.  After setting any optional
       * parameters, call the {@link Get#execute()} method to invoke the remote operation.
       *
       * @param projectId Cloud project that owns the test matrix.
       * @param testMatrixId Unique test matrix id which was assigned by the service.
       * @return the request
       */
      public Get get(java.lang.String projectId, java.lang.String testMatrixId) throws java.io.IOException {
        Get result = new Get(projectId, testMatrixId);
        initialize(result);
        return result;
      }

      public class Get extends TestingRequest<com.google.testing.model.TestMatrix> {

        private static final String REST_PATH = "v1/projects/{projectId}/testMatrices/{testMatrixId}";

        /**
         * Check the status of a test matrix.
         *
         * May return any of the following canonical error codes:
         *
         * - PERMISSION_DENIED - if the user is not authorized to read project - INVALID_ARGUMENT - if the
         * request is malformed - NOT_FOUND - if the Test Matrix does not exist
         *
         * Create a request for the method "testMatrices.get".
         *
         * This request holds the parameters needed by the the testing server.  After setting any optional
         * parameters, call the {@link Get#execute()} method to invoke the remote operation. <p> {@link
         * Get#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)} must be
         * called to initialize this instance immediately after invoking the constructor. </p>
         *
         * @param projectId Cloud project that owns the test matrix.
         * @param testMatrixId Unique test matrix id which was assigned by the service.
         * @since 1.13
         */
        protected Get(java.lang.String projectId, java.lang.String testMatrixId) {
          super(Testing.this, "GET", REST_PATH, null, com.google.testing.model.TestMatrix.class);
          this.projectId = com.google.api.client.util.Preconditions.checkNotNull(projectId, "Required parameter projectId must be specified.");
          this.testMatrixId = com.google.api.client.util.Preconditions.checkNotNull(testMatrixId, "Required parameter testMatrixId must be specified.");
        }

        @Override
        public com.google.api.client.http.HttpResponse executeUsingHead() throws java.io.IOException {
          return super.executeUsingHead();
        }

        @Override
        public com.google.api.client.http.HttpRequest buildHttpRequestUsingHead() throws java.io.IOException {
          return super.buildHttpRequestUsingHead();
        }

        @Override
        public Get set$Xgafv(java.lang.String $Xgafv) {
          return (Get) super.set$Xgafv($Xgafv);
        }

        @Override
        public Get setAccessToken(java.lang.String accessToken) {
          return (Get) super.setAccessToken(accessToken);
        }

        @Override
        public Get setAlt(java.lang.String alt) {
          return (Get) super.setAlt(alt);
        }

        @Override
        public Get setBearerToken(java.lang.String bearerToken) {
          return (Get) super.setBearerToken(bearerToken);
        }

        @Override
        public Get setCallback(java.lang.String callback) {
          return (Get) super.setCallback(callback);
        }

        @Override
        public Get setFields(java.lang.String fields) {
          return (Get) super.setFields(fields);
        }

        @Override
        public Get setKey(java.lang.String key) {
          return (Get) super.setKey(key);
        }

        @Override
        public Get setOauthToken(java.lang.String oauthToken) {
          return (Get) super.setOauthToken(oauthToken);
        }

        @Override
        public Get setPp(java.lang.Boolean pp) {
          return (Get) super.setPp(pp);
        }

        @Override
        public Get setPrettyPrint(java.lang.Boolean prettyPrint) {
          return (Get) super.setPrettyPrint(prettyPrint);
        }

        @Override
        public Get setQuotaUser(java.lang.String quotaUser) {
          return (Get) super.setQuotaUser(quotaUser);
        }

        @Override
        public Get setUploadType(java.lang.String uploadType) {
          return (Get) super.setUploadType(uploadType);
        }

        @Override
        public Get setUploadProtocol(java.lang.String uploadProtocol) {
          return (Get) super.setUploadProtocol(uploadProtocol);
        }

        /** Cloud project that owns the test matrix. */
        @com.google.api.client.util.Key
        private java.lang.String projectId;

        /** Cloud project that owns the test matrix.
         */
        public java.lang.String getProjectId() {
          return projectId;
        }

        /** Cloud project that owns the test matrix. */
        public Get setProjectId(java.lang.String projectId) {
          this.projectId = projectId;
          return this;
        }

        /** Unique test matrix id which was assigned by the service. */
        @com.google.api.client.util.Key
        private java.lang.String testMatrixId;

        /** Unique test matrix id which was assigned by the service.
         */
        public java.lang.String getTestMatrixId() {
          return testMatrixId;
        }

        /** Unique test matrix id which was assigned by the service. */
        public Get setTestMatrixId(java.lang.String testMatrixId) {
          this.testMatrixId = testMatrixId;
          return this;
        }

        @Override
        public Get set(String parameterName, Object value) {
          return (Get) super.set(parameterName, value);
        }
      }

    }
  }

  /**
   * An accessor for creating requests from the TestEnvironmentCatalog collection.
   *
   * <p>The typical use is:</p>
   * <pre>
   *   {@code Testing testing = new Testing(...);}
   *   {@code Testing.TestEnvironmentCatalog.List request = testing.testEnvironmentCatalog().list(parameters ...)}
   * </pre>
   *
   * @return the resource collection
   */
  public TestEnvironmentCatalog testEnvironmentCatalog() {
    return new TestEnvironmentCatalog();
  }

  /**
   * The "testEnvironmentCatalog" collection of methods.
   */
  public class TestEnvironmentCatalog {

    /**
     * Get the catalog of supported test environments.
     *
     * May return any of the following canonical error codes:
     *
     * - INVALID_ARGUMENT - if the request is malformed - NOT_FOUND - if the environment type does not
     * exist - INTERNAL - if an internal error occurred
     *
     * Create a request for the method "testEnvironmentCatalog.get".
     *
     * This request holds the parameters needed by the testing server.  After setting any optional
     * parameters, call the {@link Get#execute()} method to invoke the remote operation.
     *
     * @param environmentType The type of environment that should be listed.
    Required
     * @return the request
     */
    public Get get(java.lang.String environmentType) throws java.io.IOException {
      Get result = new Get(environmentType);
      initialize(result);
      return result;
    }

    public class Get extends TestingRequest<com.google.testing.model.TestEnvironmentCatalog> {

      private static final String REST_PATH = "v1/testEnvironmentCatalog/{environmentType}";

      /**
       * Get the catalog of supported test environments.
       *
       * May return any of the following canonical error codes:
       *
       * - INVALID_ARGUMENT - if the request is malformed - NOT_FOUND - if the environment type does not
       * exist - INTERNAL - if an internal error occurred
       *
       * Create a request for the method "testEnvironmentCatalog.get".
       *
       * This request holds the parameters needed by the the testing server.  After setting any optional
       * parameters, call the {@link Get#execute()} method to invoke the remote operation. <p> {@link
       * Get#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)} must be
       * called to initialize this instance immediately after invoking the constructor. </p>
       *
       * @param environmentType The type of environment that should be listed.
    Required
       * @since 1.13
       */
      protected Get(java.lang.String environmentType) {
        super(Testing.this, "GET", REST_PATH, null, com.google.testing.model.TestEnvironmentCatalog.class);
        this.environmentType = com.google.api.client.util.Preconditions.checkNotNull(environmentType, "Required parameter environmentType must be specified.");
      }

      @Override
      public com.google.api.client.http.HttpResponse executeUsingHead() throws java.io.IOException {
        return super.executeUsingHead();
      }

      @Override
      public com.google.api.client.http.HttpRequest buildHttpRequestUsingHead() throws java.io.IOException {
        return super.buildHttpRequestUsingHead();
      }

      @Override
      public Get set$Xgafv(java.lang.String $Xgafv) {
        return (Get) super.set$Xgafv($Xgafv);
      }

      @Override
      public Get setAccessToken(java.lang.String accessToken) {
        return (Get) super.setAccessToken(accessToken);
      }

      @Override
      public Get setAlt(java.lang.String alt) {
        return (Get) super.setAlt(alt);
      }

      @Override
      public Get setBearerToken(java.lang.String bearerToken) {
        return (Get) super.setBearerToken(bearerToken);
      }

      @Override
      public Get setCallback(java.lang.String callback) {
        return (Get) super.setCallback(callback);
      }

      @Override
      public Get setFields(java.lang.String fields) {
        return (Get) super.setFields(fields);
      }

      @Override
      public Get setKey(java.lang.String key) {
        return (Get) super.setKey(key);
      }

      @Override
      public Get setOauthToken(java.lang.String oauthToken) {
        return (Get) super.setOauthToken(oauthToken);
      }

      @Override
      public Get setPp(java.lang.Boolean pp) {
        return (Get) super.setPp(pp);
      }

      @Override
      public Get setPrettyPrint(java.lang.Boolean prettyPrint) {
        return (Get) super.setPrettyPrint(prettyPrint);
      }

      @Override
      public Get setQuotaUser(java.lang.String quotaUser) {
        return (Get) super.setQuotaUser(quotaUser);
      }

      @Override
      public Get setUploadType(java.lang.String uploadType) {
        return (Get) super.setUploadType(uploadType);
      }

      @Override
      public Get setUploadProtocol(java.lang.String uploadProtocol) {
        return (Get) super.setUploadProtocol(uploadProtocol);
      }

      /**
       * The type of environment that should be listed. Required
       */
      @com.google.api.client.util.Key
      private java.lang.String environmentType;

      /** The type of environment that should be listed. Required
       */
      public java.lang.String getEnvironmentType() {
        return environmentType;
      }

      /**
       * The type of environment that should be listed. Required
       */
      public Get setEnvironmentType(java.lang.String environmentType) {
        this.environmentType = environmentType;
        return this;
      }

      /**
       * For authorization, the cloud project requesting the TestEnvironmentCatalog. Optional
       */
      @com.google.api.client.util.Key
      private java.lang.String projectId;

      /** For authorization, the cloud project requesting the TestEnvironmentCatalog. Optional
       */
      public java.lang.String getProjectId() {
        return projectId;
      }

      /**
       * For authorization, the cloud project requesting the TestEnvironmentCatalog. Optional
       */
      public Get setProjectId(java.lang.String projectId) {
        this.projectId = projectId;
        return this;
      }

      @Override
      public Get set(String parameterName, Object value) {
        return (Get) super.set(parameterName, value);
      }
    }

  }

  /**
   * Builder for {@link Testing}.
   *
   * <p>
   * Implementation is not thread-safe.
   * </p>
   *
   * @since 1.3.0
   */
  public static final class Builder extends com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient.Builder {

    /**
     * Returns an instance of a new builder.
     *
     * @param transport HTTP transport, which should normally be:
     *        <ul>
     *        <li>Google App Engine:
     *        {@code com.google.api.client.extensions.appengine.http.UrlFetchTransport}</li>
     *        <li>Android: {@code newCompatibleTransport} from
     *        {@code com.google.api.client.extensions.android.http.AndroidHttp}</li>
     *        <li>Java: {@link com.google.api.client.googleapis.javanet.GoogleNetHttpTransport#newTrustedTransport()}
     *        </li>
     *        </ul>
     * @param jsonFactory JSON factory, which may be:
     *        <ul>
     *        <li>Jackson: {@code com.google.api.client.json.jackson2.JacksonFactory}</li>
     *        <li>Google GSON: {@code com.google.api.client.json.gson.GsonFactory}</li>
     *        <li>Android Honeycomb or higher:
     *        {@code com.google.api.client.extensions.android.json.AndroidJsonFactory}</li>
     *        </ul>
     * @param httpRequestInitializer HTTP request initializer or {@code null} for none
     * @since 1.7
     */
    public Builder(com.google.api.client.http.HttpTransport transport, com.google.api.client.json.JsonFactory jsonFactory,
        com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
      super(
          transport,
          jsonFactory,
          DEFAULT_ROOT_URL,
          DEFAULT_SERVICE_PATH,
          httpRequestInitializer,
          false);
      setBatchPath(DEFAULT_BATCH_PATH);
    }

    /** Builds a new instance of {@link Testing}. */
    @Override
    public Testing build() {
      return new Testing(this);
    }

    @Override
    public Builder setRootUrl(String rootUrl) {
      return (Builder) super.setRootUrl(rootUrl);
    }

    @Override
    public Builder setServicePath(String servicePath) {
      return (Builder) super.setServicePath(servicePath);
    }

    @Override
    public Builder setBatchPath(String batchPath) {
      return (Builder) super.setBatchPath(batchPath);
    }

    @Override
    public Builder setHttpRequestInitializer(com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
      return (Builder) super.setHttpRequestInitializer(httpRequestInitializer);
    }

    @Override
    public Builder setApplicationName(String applicationName) {
      return (Builder) super.setApplicationName(applicationName);
    }

    @Override
    public Builder setSuppressPatternChecks(boolean suppressPatternChecks) {
      return (Builder) super.setSuppressPatternChecks(suppressPatternChecks);
    }

    @Override
    public Builder setSuppressRequiredParameterChecks(boolean suppressRequiredParameterChecks) {
      return (Builder) super.setSuppressRequiredParameterChecks(suppressRequiredParameterChecks);
    }

    @Override
    public Builder setSuppressAllChecks(boolean suppressAllChecks) {
      return (Builder) super.setSuppressAllChecks(suppressAllChecks);
    }

    /**
     * Set the {@link TestingRequestInitializer}.
     *
     * @since 1.12
     */
    public Builder setTestingRequestInitializer(
        TestingRequestInitializer testingRequestInitializer) {
      return (Builder) super.setGoogleClientRequestInitializer(testingRequestInitializer);
    }

    @Override
    public Builder setGoogleClientRequestInitializer(
        com.google.api.client.googleapis.services.GoogleClientRequestInitializer googleClientRequestInitializer) {
      return (Builder) super.setGoogleClientRequestInitializer(googleClientRequestInitializer);
    }
  }
}
