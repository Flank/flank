/*
 * Decompiled with CFR 0_121.
 */
package com.google.api.services.testing;

import com.google.api.client.googleapis.GoogleUtils;
import com.google.api.client.googleapis.services.AbstractGoogleClient;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.util.Preconditions;
import com.google.api.services.testing.TestingRequest;
import com.google.api.services.testing.TestingRequestInitializer;
import com.google.api.services.testing.model.CancelTestMatrixResponse;
import com.google.api.services.testing.model.Device;
import com.google.api.services.testing.model.Empty;
import com.google.api.services.testing.model.ListDevicesResponse;
import com.google.api.services.testing.model.ListTestMatricesResponse;
import com.google.api.services.testing.model.TestMatrix;
import java.io.IOException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Testing
extends AbstractGoogleJsonClient {
    public static final String DEFAULT_ROOT_URL = "https://testing.googleapis.com/";
    public static final String DEFAULT_SERVICE_PATH = "";
    public static final String DEFAULT_BASE_URL = "https://testing.googleapis.com/";

    public Testing(HttpTransport transport, JsonFactory jsonFactory, HttpRequestInitializer httpRequestInitializer) {
        this(new Builder(transport, jsonFactory, httpRequestInitializer));
    }

    Testing(Builder builder) {
        super(builder);
    }

    @Override
    protected void initialize(AbstractGoogleClientRequest<?> httpClientRequest) throws IOException {
        super.initialize(httpClientRequest);
    }

    public Projects projects() {
        return new Projects();
    }

    public TestEnvironmentCatalog testEnvironmentCatalog() {
        return new TestEnvironmentCatalog();
    }

    static {
        Preconditions.checkState(GoogleUtils.MAJOR_VERSION == 1 && GoogleUtils.MINOR_VERSION >= 15, "You are currently running with version %s of google-api-client. You need at least version 1.15 of google-api-client to run version 1.20.0 of the Google Cloud Testing API library.", GoogleUtils.VERSION);
    }

    public static final class Builder
    extends AbstractGoogleJsonClient.Builder {
        public Builder(HttpTransport transport, JsonFactory jsonFactory, HttpRequestInitializer httpRequestInitializer) {
            super(transport, jsonFactory, "https://testing.googleapis.com/", "", httpRequestInitializer, false);
        }

        public Testing build() {
            return new Testing(this);
        }

        public Builder setRootUrl(String rootUrl) {
            return (Builder)super.setRootUrl(rootUrl);
        }

        public Builder setServicePath(String servicePath) {
            return (Builder)super.setServicePath(servicePath);
        }

        public Builder setHttpRequestInitializer(HttpRequestInitializer httpRequestInitializer) {
            return (Builder)super.setHttpRequestInitializer(httpRequestInitializer);
        }

        public Builder setApplicationName(String applicationName) {
            return (Builder)super.setApplicationName(applicationName);
        }

        public Builder setSuppressPatternChecks(boolean suppressPatternChecks) {
            return (Builder)super.setSuppressPatternChecks(suppressPatternChecks);
        }

        public Builder setSuppressRequiredParameterChecks(boolean suppressRequiredParameterChecks) {
            return (Builder)super.setSuppressRequiredParameterChecks(suppressRequiredParameterChecks);
        }

        public Builder setSuppressAllChecks(boolean suppressAllChecks) {
            return (Builder)super.setSuppressAllChecks(suppressAllChecks);
        }

        public Builder setTestingRequestInitializer(TestingRequestInitializer testingRequestInitializer) {
            return (Builder)super.setGoogleClientRequestInitializer(testingRequestInitializer);
        }

        public Builder setGoogleClientRequestInitializer(GoogleClientRequestInitializer googleClientRequestInitializer) {
            return (Builder)super.setGoogleClientRequestInitializer(googleClientRequestInitializer);
        }
    }

    public class TestEnvironmentCatalog {
        public Get get(String environmentType) throws IOException {
            Get result = new Get(environmentType);
            Testing.this.initialize(result);
            return result;
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public class Get
        extends TestingRequest<com.google.api.services.testing.model.TestEnvironmentCatalog> {
            private static final String REST_PATH = "v1/testEnvironmentCatalog/{environmentType}";
            @Key
            private String environmentType;

            protected Get(String environmentType) {
                super(Testing.this, "GET", "v1/testEnvironmentCatalog/{environmentType}", null, com.google.api.services.testing.model.TestEnvironmentCatalog.class);
                this.environmentType = Preconditions.checkNotNull(environmentType, "Required parameter environmentType must be specified.");
            }

            @Override
            public HttpResponse executeUsingHead() throws IOException {
                return super.executeUsingHead();
            }

            @Override
            public HttpRequest buildHttpRequestUsingHead() throws IOException {
                return super.buildHttpRequestUsingHead();
            }

            public Get set$Xgafv(String $Xgafv) {
                return (Get)super.set$Xgafv($Xgafv);
            }

            public Get setAccessToken(String accessToken) {
                return (Get)super.setAccessToken(accessToken);
            }

            public Get setAlt(String alt) {
                return (Get)super.setAlt(alt);
            }

            public Get setBearerToken(String bearerToken) {
                return (Get)super.setBearerToken(bearerToken);
            }

            public Get setCallback(String callback) {
                return (Get)super.setCallback(callback);
            }

            public Get setFields(String fields) {
                return (Get)super.setFields(fields);
            }

            public Get setKey(String key) {
                return (Get)super.setKey(key);
            }

            public Get setOauthToken(String oauthToken) {
                return (Get)super.setOauthToken(oauthToken);
            }

            public Get setPp(Boolean pp) {
                return (Get)super.setPp(pp);
            }

            public Get setPrettyPrint(Boolean prettyPrint) {
                return (Get)super.setPrettyPrint(prettyPrint);
            }

            public Get setQuotaUser(String quotaUser) {
                return (Get)super.setQuotaUser(quotaUser);
            }

            public Get setUploadType(String uploadType) {
                return (Get)super.setUploadType(uploadType);
            }

            public Get setUploadProtocol(String uploadProtocol) {
                return (Get)super.setUploadProtocol(uploadProtocol);
            }

            public String getEnvironmentType() {
                return this.environmentType;
            }

            public Get setEnvironmentType(String environmentType) {
                this.environmentType = environmentType;
                return this;
            }

            @Override
            public Get set(String parameterName, Object value) {
                return (Get)super.set(parameterName, value);
            }
        }

    }

    public class Projects {
        public Devices devices() {
            return new Devices();
        }

        public TestMatrices testMatrices() {
            return new TestMatrices();
        }

        public class TestMatrices {
            public Cancel cancel(String projectId, String testMatrixId) throws IOException {
                Cancel result = new Cancel(projectId, testMatrixId);
                Testing.this.initialize(result);
                return result;
            }

            public Create create(String projectId, TestMatrix content) throws IOException {
                Create result = new Create(projectId, content);
                Testing.this.initialize(result);
                return result;
            }

            public Delete delete(String projectId, String testMatrixId) throws IOException {
                Delete result = new Delete(projectId, testMatrixId);
                Testing.this.initialize(result);
                return result;
            }

            public Get get(String projectId, String testMatrixId) throws IOException {
                Get result = new Get(projectId, testMatrixId);
                Testing.this.initialize(result);
                return result;
            }

            public List list(String projectId) throws IOException {
                List result = new List(projectId);
                Testing.this.initialize(result);
                return result;
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public class List
            extends TestingRequest<ListTestMatricesResponse> {
                private static final String REST_PATH = "v1/projects/{projectId}/testMatrices";
                @Key
                private String projectId;

                protected List(String projectId) {
                    super(Testing.this, "GET", "v1/projects/{projectId}/testMatrices", null, ListTestMatricesResponse.class);
                    this.projectId = Preconditions.checkNotNull(projectId, "Required parameter projectId must be specified.");
                }

                @Override
                public HttpResponse executeUsingHead() throws IOException {
                    return super.executeUsingHead();
                }

                @Override
                public HttpRequest buildHttpRequestUsingHead() throws IOException {
                    return super.buildHttpRequestUsingHead();
                }

                public List set$Xgafv(String $Xgafv) {
                    return (List)super.set$Xgafv($Xgafv);
                }

                public List setAccessToken(String accessToken) {
                    return (List)super.setAccessToken(accessToken);
                }

                public List setAlt(String alt) {
                    return (List)super.setAlt(alt);
                }

                public List setBearerToken(String bearerToken) {
                    return (List)super.setBearerToken(bearerToken);
                }

                public List setCallback(String callback) {
                    return (List)super.setCallback(callback);
                }

                public List setFields(String fields) {
                    return (List)super.setFields(fields);
                }

                public List setKey(String key) {
                    return (List)super.setKey(key);
                }

                public List setOauthToken(String oauthToken) {
                    return (List)super.setOauthToken(oauthToken);
                }

                public List setPp(Boolean pp) {
                    return (List)super.setPp(pp);
                }

                public List setPrettyPrint(Boolean prettyPrint) {
                    return (List)super.setPrettyPrint(prettyPrint);
                }

                public List setQuotaUser(String quotaUser) {
                    return (List)super.setQuotaUser(quotaUser);
                }

                public List setUploadType(String uploadType) {
                    return (List)super.setUploadType(uploadType);
                }

                public List setUploadProtocol(String uploadProtocol) {
                    return (List)super.setUploadProtocol(uploadProtocol);
                }

                public String getProjectId() {
                    return this.projectId;
                }

                public List setProjectId(String projectId) {
                    this.projectId = projectId;
                    return this;
                }

                @Override
                public List set(String parameterName, Object value) {
                    return (List)super.set(parameterName, value);
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public class Get
            extends TestingRequest<TestMatrix> {
                private static final String REST_PATH = "v1/projects/{projectId}/testMatrices/{testMatrixId}";
                @Key
                private String projectId;
                @Key
                private String testMatrixId;

                protected Get(String projectId, String testMatrixId) {
                    super(Testing.this, "GET", "v1/projects/{projectId}/testMatrices/{testMatrixId}", null, TestMatrix.class);
                    this.projectId = Preconditions.checkNotNull(projectId, "Required parameter projectId must be specified.");
                    this.testMatrixId = Preconditions.checkNotNull(testMatrixId, "Required parameter testMatrixId must be specified.");
                }

                @Override
                public HttpResponse executeUsingHead() throws IOException {
                    return super.executeUsingHead();
                }

                @Override
                public HttpRequest buildHttpRequestUsingHead() throws IOException {
                    return super.buildHttpRequestUsingHead();
                }

                public Get set$Xgafv(String $Xgafv) {
                    return (Get)super.set$Xgafv($Xgafv);
                }

                public Get setAccessToken(String accessToken) {
                    return (Get)super.setAccessToken(accessToken);
                }

                public Get setAlt(String alt) {
                    return (Get)super.setAlt(alt);
                }

                public Get setBearerToken(String bearerToken) {
                    return (Get)super.setBearerToken(bearerToken);
                }

                public Get setCallback(String callback) {
                    return (Get)super.setCallback(callback);
                }

                public Get setFields(String fields) {
                    return (Get)super.setFields(fields);
                }

                public Get setKey(String key) {
                    return (Get)super.setKey(key);
                }

                public Get setOauthToken(String oauthToken) {
                    return (Get)super.setOauthToken(oauthToken);
                }

                public Get setPp(Boolean pp) {
                    return (Get)super.setPp(pp);
                }

                public Get setPrettyPrint(Boolean prettyPrint) {
                    return (Get)super.setPrettyPrint(prettyPrint);
                }

                public Get setQuotaUser(String quotaUser) {
                    return (Get)super.setQuotaUser(quotaUser);
                }

                public Get setUploadType(String uploadType) {
                    return (Get)super.setUploadType(uploadType);
                }

                public Get setUploadProtocol(String uploadProtocol) {
                    return (Get)super.setUploadProtocol(uploadProtocol);
                }

                public String getProjectId() {
                    return this.projectId;
                }

                public Get setProjectId(String projectId) {
                    this.projectId = projectId;
                    return this;
                }

                public String getTestMatrixId() {
                    return this.testMatrixId;
                }

                public Get setTestMatrixId(String testMatrixId) {
                    this.testMatrixId = testMatrixId;
                    return this;
                }

                @Override
                public Get set(String parameterName, Object value) {
                    return (Get)super.set(parameterName, value);
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public class Delete
            extends TestingRequest<Empty> {
                private static final String REST_PATH = "v1/projects/{projectId}/testMatrices/{testMatrixId}";
                @Key
                private String projectId;
                @Key
                private String testMatrixId;

                protected Delete(String projectId, String testMatrixId) {
                    super(Testing.this, "DELETE", "v1/projects/{projectId}/testMatrices/{testMatrixId}", null, Empty.class);
                    this.projectId = Preconditions.checkNotNull(projectId, "Required parameter projectId must be specified.");
                    this.testMatrixId = Preconditions.checkNotNull(testMatrixId, "Required parameter testMatrixId must be specified.");
                }

                public Delete set$Xgafv(String $Xgafv) {
                    return (Delete)super.set$Xgafv($Xgafv);
                }

                public Delete setAccessToken(String accessToken) {
                    return (Delete)super.setAccessToken(accessToken);
                }

                public Delete setAlt(String alt) {
                    return (Delete)super.setAlt(alt);
                }

                public Delete setBearerToken(String bearerToken) {
                    return (Delete)super.setBearerToken(bearerToken);
                }

                public Delete setCallback(String callback) {
                    return (Delete)super.setCallback(callback);
                }

                public Delete setFields(String fields) {
                    return (Delete)super.setFields(fields);
                }

                public Delete setKey(String key) {
                    return (Delete)super.setKey(key);
                }

                public Delete setOauthToken(String oauthToken) {
                    return (Delete)super.setOauthToken(oauthToken);
                }

                public Delete setPp(Boolean pp) {
                    return (Delete)super.setPp(pp);
                }

                public Delete setPrettyPrint(Boolean prettyPrint) {
                    return (Delete)super.setPrettyPrint(prettyPrint);
                }

                public Delete setQuotaUser(String quotaUser) {
                    return (Delete)super.setQuotaUser(quotaUser);
                }

                public Delete setUploadType(String uploadType) {
                    return (Delete)super.setUploadType(uploadType);
                }

                public Delete setUploadProtocol(String uploadProtocol) {
                    return (Delete)super.setUploadProtocol(uploadProtocol);
                }

                public String getProjectId() {
                    return this.projectId;
                }

                public Delete setProjectId(String projectId) {
                    this.projectId = projectId;
                    return this;
                }

                public String getTestMatrixId() {
                    return this.testMatrixId;
                }

                public Delete setTestMatrixId(String testMatrixId) {
                    this.testMatrixId = testMatrixId;
                    return this;
                }

                @Override
                public Delete set(String parameterName, Object value) {
                    return (Delete)super.set(parameterName, value);
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public class Create
            extends TestingRequest<TestMatrix> {
                private static final String REST_PATH = "v1/projects/{projectId}/testMatrices";
                @Key
                private String projectId;

                protected Create(String projectId, TestMatrix content) {
                    super(Testing.this, "POST", "v1/projects/{projectId}/testMatrices", content, TestMatrix.class);
                    this.projectId = Preconditions.checkNotNull(projectId, "Required parameter projectId must be specified.");
                }

                public Create set$Xgafv(String $Xgafv) {
                    return (Create)super.set$Xgafv($Xgafv);
                }

                public Create setAccessToken(String accessToken) {
                    return (Create)super.setAccessToken(accessToken);
                }

                public Create setAlt(String alt) {
                    return (Create)super.setAlt(alt);
                }

                public Create setBearerToken(String bearerToken) {
                    return (Create)super.setBearerToken(bearerToken);
                }

                public Create setCallback(String callback) {
                    return (Create)super.setCallback(callback);
                }

                public Create setFields(String fields) {
                    return (Create)super.setFields(fields);
                }

                public Create setKey(String key) {
                    return (Create)super.setKey(key);
                }

                public Create setOauthToken(String oauthToken) {
                    return (Create)super.setOauthToken(oauthToken);
                }

                public Create setPp(Boolean pp) {
                    return (Create)super.setPp(pp);
                }

                public Create setPrettyPrint(Boolean prettyPrint) {
                    return (Create)super.setPrettyPrint(prettyPrint);
                }

                public Create setQuotaUser(String quotaUser) {
                    return (Create)super.setQuotaUser(quotaUser);
                }

                public Create setUploadType(String uploadType) {
                    return (Create)super.setUploadType(uploadType);
                }

                public Create setUploadProtocol(String uploadProtocol) {
                    return (Create)super.setUploadProtocol(uploadProtocol);
                }

                public String getProjectId() {
                    return this.projectId;
                }

                public Create setProjectId(String projectId) {
                    this.projectId = projectId;
                    return this;
                }

                @Override
                public Create set(String parameterName, Object value) {
                    return (Create)super.set(parameterName, value);
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public class Cancel
            extends TestingRequest<CancelTestMatrixResponse> {
                private static final String REST_PATH = "v1/projects/{projectId}/testMatrices/{testMatrixId}:cancel";
                @Key
                private String projectId;
                @Key
                private String testMatrixId;

                protected Cancel(String projectId, String testMatrixId) {
                    super(Testing.this, "POST", "v1/projects/{projectId}/testMatrices/{testMatrixId}:cancel", null, CancelTestMatrixResponse.class);
                    this.projectId = Preconditions.checkNotNull(projectId, "Required parameter projectId must be specified.");
                    this.testMatrixId = Preconditions.checkNotNull(testMatrixId, "Required parameter testMatrixId must be specified.");
                }

                public Cancel set$Xgafv(String $Xgafv) {
                    return (Cancel)super.set$Xgafv($Xgafv);
                }

                public Cancel setAccessToken(String accessToken) {
                    return (Cancel)super.setAccessToken(accessToken);
                }

                public Cancel setAlt(String alt) {
                    return (Cancel)super.setAlt(alt);
                }

                public Cancel setBearerToken(String bearerToken) {
                    return (Cancel)super.setBearerToken(bearerToken);
                }

                public Cancel setCallback(String callback) {
                    return (Cancel)super.setCallback(callback);
                }

                public Cancel setFields(String fields) {
                    return (Cancel)super.setFields(fields);
                }

                public Cancel setKey(String key) {
                    return (Cancel)super.setKey(key);
                }

                public Cancel setOauthToken(String oauthToken) {
                    return (Cancel)super.setOauthToken(oauthToken);
                }

                public Cancel setPp(Boolean pp) {
                    return (Cancel)super.setPp(pp);
                }

                public Cancel setPrettyPrint(Boolean prettyPrint) {
                    return (Cancel)super.setPrettyPrint(prettyPrint);
                }

                public Cancel setQuotaUser(String quotaUser) {
                    return (Cancel)super.setQuotaUser(quotaUser);
                }

                public Cancel setUploadType(String uploadType) {
                    return (Cancel)super.setUploadType(uploadType);
                }

                public Cancel setUploadProtocol(String uploadProtocol) {
                    return (Cancel)super.setUploadProtocol(uploadProtocol);
                }

                public String getProjectId() {
                    return this.projectId;
                }

                public Cancel setProjectId(String projectId) {
                    this.projectId = projectId;
                    return this;
                }

                public String getTestMatrixId() {
                    return this.testMatrixId;
                }

                public Cancel setTestMatrixId(String testMatrixId) {
                    this.testMatrixId = testMatrixId;
                    return this;
                }

                @Override
                public Cancel set(String parameterName, Object value) {
                    return (Cancel)super.set(parameterName, value);
                }
            }

        }

        public class Devices {
            public Create create(String projectId, Device content) throws IOException {
                Create result = new Create(projectId, content);
                Testing.this.initialize(result);
                return result;
            }

            public Delete delete(String projectId, String deviceId) throws IOException {
                Delete result = new Delete(projectId, deviceId);
                Testing.this.initialize(result);
                return result;
            }

            public Get get(String projectId, String deviceId) throws IOException {
                Get result = new Get(projectId, deviceId);
                Testing.this.initialize(result);
                return result;
            }

            public Keepalive keepalive(String projectId, String deviceId) throws IOException {
                Keepalive result = new Keepalive(projectId, deviceId);
                Testing.this.initialize(result);
                return result;
            }

            public List list(String projectId) throws IOException {
                List result = new List(projectId);
                Testing.this.initialize(result);
                return result;
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public class List
            extends TestingRequest<ListDevicesResponse> {
                private static final String REST_PATH = "v1/projects/{projectId}/devices";
                @Key
                private String projectId;
                @Key
                private Integer pageSize;
                @Key
                private String pageToken;

                protected List(String projectId) {
                    super(Testing.this, "GET", "v1/projects/{projectId}/devices", null, ListDevicesResponse.class);
                    this.projectId = Preconditions.checkNotNull(projectId, "Required parameter projectId must be specified.");
                }

                @Override
                public HttpResponse executeUsingHead() throws IOException {
                    return super.executeUsingHead();
                }

                @Override
                public HttpRequest buildHttpRequestUsingHead() throws IOException {
                    return super.buildHttpRequestUsingHead();
                }

                public List set$Xgafv(String $Xgafv) {
                    return (List)super.set$Xgafv($Xgafv);
                }

                public List setAccessToken(String accessToken) {
                    return (List)super.setAccessToken(accessToken);
                }

                public List setAlt(String alt) {
                    return (List)super.setAlt(alt);
                }

                public List setBearerToken(String bearerToken) {
                    return (List)super.setBearerToken(bearerToken);
                }

                public List setCallback(String callback) {
                    return (List)super.setCallback(callback);
                }

                public List setFields(String fields) {
                    return (List)super.setFields(fields);
                }

                public List setKey(String key) {
                    return (List)super.setKey(key);
                }

                public List setOauthToken(String oauthToken) {
                    return (List)super.setOauthToken(oauthToken);
                }

                public List setPp(Boolean pp) {
                    return (List)super.setPp(pp);
                }

                public List setPrettyPrint(Boolean prettyPrint) {
                    return (List)super.setPrettyPrint(prettyPrint);
                }

                public List setQuotaUser(String quotaUser) {
                    return (List)super.setQuotaUser(quotaUser);
                }

                public List setUploadType(String uploadType) {
                    return (List)super.setUploadType(uploadType);
                }

                public List setUploadProtocol(String uploadProtocol) {
                    return (List)super.setUploadProtocol(uploadProtocol);
                }

                public String getProjectId() {
                    return this.projectId;
                }

                public List setProjectId(String projectId) {
                    this.projectId = projectId;
                    return this;
                }

                public Integer getPageSize() {
                    return this.pageSize;
                }

                public List setPageSize(Integer pageSize) {
                    this.pageSize = pageSize;
                    return this;
                }

                public String getPageToken() {
                    return this.pageToken;
                }

                public List setPageToken(String pageToken) {
                    this.pageToken = pageToken;
                    return this;
                }

                @Override
                public List set(String parameterName, Object value) {
                    return (List)super.set(parameterName, value);
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public class Keepalive
            extends TestingRequest<Empty> {
                private static final String REST_PATH = "v1/projects/{projectId}/devices/{deviceId}/keepalive";
                @Key
                private String projectId;
                @Key
                private String deviceId;

                protected Keepalive(String projectId, String deviceId) {
                    super(Testing.this, "POST", "v1/projects/{projectId}/devices/{deviceId}/keepalive", null, Empty.class);
                    this.projectId = Preconditions.checkNotNull(projectId, "Required parameter projectId must be specified.");
                    this.deviceId = Preconditions.checkNotNull(deviceId, "Required parameter deviceId must be specified.");
                }

                public Keepalive set$Xgafv(String $Xgafv) {
                    return (Keepalive)super.set$Xgafv($Xgafv);
                }

                public Keepalive setAccessToken(String accessToken) {
                    return (Keepalive)super.setAccessToken(accessToken);
                }

                public Keepalive setAlt(String alt) {
                    return (Keepalive)super.setAlt(alt);
                }

                public Keepalive setBearerToken(String bearerToken) {
                    return (Keepalive)super.setBearerToken(bearerToken);
                }

                public Keepalive setCallback(String callback) {
                    return (Keepalive)super.setCallback(callback);
                }

                public Keepalive setFields(String fields) {
                    return (Keepalive)super.setFields(fields);
                }

                public Keepalive setKey(String key) {
                    return (Keepalive)super.setKey(key);
                }

                public Keepalive setOauthToken(String oauthToken) {
                    return (Keepalive)super.setOauthToken(oauthToken);
                }

                public Keepalive setPp(Boolean pp) {
                    return (Keepalive)super.setPp(pp);
                }

                public Keepalive setPrettyPrint(Boolean prettyPrint) {
                    return (Keepalive)super.setPrettyPrint(prettyPrint);
                }

                public Keepalive setQuotaUser(String quotaUser) {
                    return (Keepalive)super.setQuotaUser(quotaUser);
                }

                public Keepalive setUploadType(String uploadType) {
                    return (Keepalive)super.setUploadType(uploadType);
                }

                public Keepalive setUploadProtocol(String uploadProtocol) {
                    return (Keepalive)super.setUploadProtocol(uploadProtocol);
                }

                public String getProjectId() {
                    return this.projectId;
                }

                public Keepalive setProjectId(String projectId) {
                    this.projectId = projectId;
                    return this;
                }

                public String getDeviceId() {
                    return this.deviceId;
                }

                public Keepalive setDeviceId(String deviceId) {
                    this.deviceId = deviceId;
                    return this;
                }

                @Override
                public Keepalive set(String parameterName, Object value) {
                    return (Keepalive)super.set(parameterName, value);
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public class Get
            extends TestingRequest<Device> {
                private static final String REST_PATH = "v1/projects/{projectId}/devices/{deviceId}";
                @Key
                private String projectId;
                @Key
                private String deviceId;

                protected Get(String projectId, String deviceId) {
                    super(Testing.this, "GET", "v1/projects/{projectId}/devices/{deviceId}", null, Device.class);
                    this.projectId = Preconditions.checkNotNull(projectId, "Required parameter projectId must be specified.");
                    this.deviceId = Preconditions.checkNotNull(deviceId, "Required parameter deviceId must be specified.");
                }

                @Override
                public HttpResponse executeUsingHead() throws IOException {
                    return super.executeUsingHead();
                }

                @Override
                public HttpRequest buildHttpRequestUsingHead() throws IOException {
                    return super.buildHttpRequestUsingHead();
                }

                public Get set$Xgafv(String $Xgafv) {
                    return (Get)super.set$Xgafv($Xgafv);
                }

                public Get setAccessToken(String accessToken) {
                    return (Get)super.setAccessToken(accessToken);
                }

                public Get setAlt(String alt) {
                    return (Get)super.setAlt(alt);
                }

                public Get setBearerToken(String bearerToken) {
                    return (Get)super.setBearerToken(bearerToken);
                }

                public Get setCallback(String callback) {
                    return (Get)super.setCallback(callback);
                }

                public Get setFields(String fields) {
                    return (Get)super.setFields(fields);
                }

                public Get setKey(String key) {
                    return (Get)super.setKey(key);
                }

                public Get setOauthToken(String oauthToken) {
                    return (Get)super.setOauthToken(oauthToken);
                }

                public Get setPp(Boolean pp) {
                    return (Get)super.setPp(pp);
                }

                public Get setPrettyPrint(Boolean prettyPrint) {
                    return (Get)super.setPrettyPrint(prettyPrint);
                }

                public Get setQuotaUser(String quotaUser) {
                    return (Get)super.setQuotaUser(quotaUser);
                }

                public Get setUploadType(String uploadType) {
                    return (Get)super.setUploadType(uploadType);
                }

                public Get setUploadProtocol(String uploadProtocol) {
                    return (Get)super.setUploadProtocol(uploadProtocol);
                }

                public String getProjectId() {
                    return this.projectId;
                }

                public Get setProjectId(String projectId) {
                    this.projectId = projectId;
                    return this;
                }

                public String getDeviceId() {
                    return this.deviceId;
                }

                public Get setDeviceId(String deviceId) {
                    this.deviceId = deviceId;
                    return this;
                }

                @Override
                public Get set(String parameterName, Object value) {
                    return (Get)super.set(parameterName, value);
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public class Delete
            extends TestingRequest<Empty> {
                private static final String REST_PATH = "v1/projects/{projectId}/devices/{deviceId}";
                @Key
                private String projectId;
                @Key
                private String deviceId;

                protected Delete(String projectId, String deviceId) {
                    super(Testing.this, "DELETE", "v1/projects/{projectId}/devices/{deviceId}", null, Empty.class);
                    this.projectId = Preconditions.checkNotNull(projectId, "Required parameter projectId must be specified.");
                    this.deviceId = Preconditions.checkNotNull(deviceId, "Required parameter deviceId must be specified.");
                }

                public Delete set$Xgafv(String $Xgafv) {
                    return (Delete)super.set$Xgafv($Xgafv);
                }

                public Delete setAccessToken(String accessToken) {
                    return (Delete)super.setAccessToken(accessToken);
                }

                public Delete setAlt(String alt) {
                    return (Delete)super.setAlt(alt);
                }

                public Delete setBearerToken(String bearerToken) {
                    return (Delete)super.setBearerToken(bearerToken);
                }

                public Delete setCallback(String callback) {
                    return (Delete)super.setCallback(callback);
                }

                public Delete setFields(String fields) {
                    return (Delete)super.setFields(fields);
                }

                public Delete setKey(String key) {
                    return (Delete)super.setKey(key);
                }

                public Delete setOauthToken(String oauthToken) {
                    return (Delete)super.setOauthToken(oauthToken);
                }

                public Delete setPp(Boolean pp) {
                    return (Delete)super.setPp(pp);
                }

                public Delete setPrettyPrint(Boolean prettyPrint) {
                    return (Delete)super.setPrettyPrint(prettyPrint);
                }

                public Delete setQuotaUser(String quotaUser) {
                    return (Delete)super.setQuotaUser(quotaUser);
                }

                public Delete setUploadType(String uploadType) {
                    return (Delete)super.setUploadType(uploadType);
                }

                public Delete setUploadProtocol(String uploadProtocol) {
                    return (Delete)super.setUploadProtocol(uploadProtocol);
                }

                public String getProjectId() {
                    return this.projectId;
                }

                public Delete setProjectId(String projectId) {
                    this.projectId = projectId;
                    return this;
                }

                public String getDeviceId() {
                    return this.deviceId;
                }

                public Delete setDeviceId(String deviceId) {
                    this.deviceId = deviceId;
                    return this;
                }

                @Override
                public Delete set(String parameterName, Object value) {
                    return (Delete)super.set(parameterName, value);
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public class Create
            extends TestingRequest<Device> {
                private static final String REST_PATH = "v1/projects/{projectId}/devices";
                @Key
                private String projectId;
                @Key
                private String sshPublicKey;

                protected Create(String projectId, Device content) {
                    super(Testing.this, "POST", "v1/projects/{projectId}/devices", content, Device.class);
                    this.projectId = Preconditions.checkNotNull(projectId, "Required parameter projectId must be specified.");
                }

                public Create set$Xgafv(String $Xgafv) {
                    return (Create)super.set$Xgafv($Xgafv);
                }

                public Create setAccessToken(String accessToken) {
                    return (Create)super.setAccessToken(accessToken);
                }

                public Create setAlt(String alt) {
                    return (Create)super.setAlt(alt);
                }

                public Create setBearerToken(String bearerToken) {
                    return (Create)super.setBearerToken(bearerToken);
                }

                public Create setCallback(String callback) {
                    return (Create)super.setCallback(callback);
                }

                public Create setFields(String fields) {
                    return (Create)super.setFields(fields);
                }

                public Create setKey(String key) {
                    return (Create)super.setKey(key);
                }

                public Create setOauthToken(String oauthToken) {
                    return (Create)super.setOauthToken(oauthToken);
                }

                public Create setPp(Boolean pp) {
                    return (Create)super.setPp(pp);
                }

                public Create setPrettyPrint(Boolean prettyPrint) {
                    return (Create)super.setPrettyPrint(prettyPrint);
                }

                public Create setQuotaUser(String quotaUser) {
                    return (Create)super.setQuotaUser(quotaUser);
                }

                public Create setUploadType(String uploadType) {
                    return (Create)super.setUploadType(uploadType);
                }

                public Create setUploadProtocol(String uploadProtocol) {
                    return (Create)super.setUploadProtocol(uploadProtocol);
                }

                public String getProjectId() {
                    return this.projectId;
                }

                public Create setProjectId(String projectId) {
                    this.projectId = projectId;
                    return this;
                }

                public String getSshPublicKey() {
                    return this.sshPublicKey;
                }

                public Create setSshPublicKey(String sshPublicKey) {
                    this.sshPublicKey = sshPublicKey;
                    return this;
                }

                @Override
                public Create set(String parameterName, Object value) {
                    return (Create)super.set(parameterName, value);
                }
            }

        }

    }

}
