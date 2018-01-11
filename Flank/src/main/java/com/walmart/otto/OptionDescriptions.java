package com.walmart.otto;

public class OptionDescriptions {

  public static final String APP_APK_OPTION_DESCRIPTION = "Path to the app apk";

  public static final String TEST_APK_OPTION_DESCRIPTION = "Path to the instrumentation apk";

  public static final String CONFIG_FILE_OPTION_DESCRIPTION = "Path to the configuration file";

  public static final String TEST_FILTERS_DESCRIPTION =
      String.format(
          "Test filtering options. For instance:%n"
              + "- \"class foo.FooTest, bar.BarTest\" executes only the tests in the given class(es);%n"
              + "- \"class foo.FooTest#testFoo, bar.BarTest#testBar\" executes only the given test(s);%n"
              + "- \"package foo, bar\" executes only the tests in the given package(s);%n"
              + "- \"annotation foo.MyAnnotation, bar.MyAnnotation\" executes only the tests annotated with the given annotation(s);%n"
              + "- \"testFile path/to/file\" executes only the tests listed in the given file. The file should contain a list of line separated package names or test classes and optionally methods.%n"
              + "To invert the above operators, add the 'not' prefix. For example, 'notPackage' executes all the tests except the ones in the given package.%n"
              + "To specify multiple conditions, separate them with a semicolon.%n"
              + "Tests annotated with an annotation containing the 'Ignore' string are skipped by default.");
}
