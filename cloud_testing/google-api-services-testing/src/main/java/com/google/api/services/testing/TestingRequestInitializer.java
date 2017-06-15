package com.google.api.services.testing;

import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest;
import com.google.api.client.googleapis.services.json.CommonGoogleJsonClientRequestInitializer;
import java.io.IOException;

public class TestingRequestInitializer extends CommonGoogleJsonClientRequestInitializer {
  public TestingRequestInitializer() {}

  public TestingRequestInitializer(String key) {
    super(key);
  }

  public TestingRequestInitializer(String key, String userIp) {
    super(key, userIp);
  }

  public final void initializeJsonRequest(AbstractGoogleJsonClientRequest<?> request)
      throws IOException {
    super.initializeJsonRequest(request);
    this.initializeTestingRequest((TestingRequest) request);
  }

  protected void initializeTestingRequest(TestingRequest<?> request) throws IOException {}
}
