package com.walmart.otto.filter;

import com.linkedin.dex.parser.TestMethod;

public interface TestFilter {

  boolean shouldRun(TestMethod method);
}
