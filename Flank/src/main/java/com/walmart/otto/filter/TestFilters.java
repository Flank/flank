package com.walmart.otto.filter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TestFilters {

  private static final Pattern FILTER_ARGUMENT = Pattern.compile("(.+) (.+)");

  public static TestFilter fromCommandLineArguments(String arguments) {
    String[] args = arguments.split(";");

    List<TestFilter> filters =
        Arrays.stream(args)
            .map(String::trim)
            .map(TestFilters::parseSingleFilter)
            .collect(Collectors.toList());

    Collections.addAll(filters, notIgnored());

    return allOf(filters);
  }

  public static TestFilter createDefault() {
    return notIgnored();
  }

  private static TestFilter parseSingleFilter(String argument) {
    Matcher matcher = FILTER_ARGUMENT.matcher(argument);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid argument: " + argument);
    }

    Collection<String> args =
        Arrays.stream(matcher.group(2).split(",")).collect(Collectors.toSet());

    String command = matcher.group(1).toLowerCase();
    String canonicalCommand = command.replaceFirst("^not", "");

    TestFilter filter;

    switch (canonicalCommand) {
      case "class":
        filter = withClassName(args);
        break;
      case "package":
        filter = withPackageName(args);
        break;
      case "annotation":
        filter = withAnnotation(args);
        break;
      case "testfile":
        Path filePath = readFilePath(args);
        filter = createFilterFromTestFile(filePath);
        break;
      default:
        throw new IllegalArgumentException("Filtering option " + command + " not supported");
    }

    if (command.startsWith("not")) {
      return not(filter);
    } else {
      return filter;
    }
  }

  private static Path readFilePath(Collection<String> args) {
    if (args.size() > 1) {
      throw new IllegalArgumentException("Invalid file path");
    }
    return Paths.get(args.iterator().next());
  }

  private static TestFilter createFilterFromTestFile(Path filePath) {
    try {
      List<String> lines = Files.readAllLines(filePath);
      // this is really an implementation detail:
      // being the package name most generic one, it is able to filter properly if you pass the package name, the fully qualified class name or the fully qualified method name.
      return withPackageName(lines);
    } catch (IOException e) {
      throw new RuntimeException("Unable to read testFile", e);
    }
  }

  static TestFilter withPackageName(Collection<String> packageNames) {
    return method ->
        packageNames.stream().anyMatch(packageName -> method.getTestName().startsWith(packageName));
  }

  static TestFilter withClassName(Collection<String> classNames) {
    return withPackageName(classNames);
  }

  static TestFilter withAnnotation(Collection<String> annotations) {
    return method -> method.getAnnotationNames().stream().anyMatch(annotations::contains);
  }

  static TestFilter notIgnored() {
    return method -> method.getAnnotationNames().stream().noneMatch(s -> s.contains("Ignore"));
  }

  static TestFilter not(TestFilter filter) {
    return method -> !filter.shouldRun(method);
  }

  static TestFilter allOf(Collection<TestFilter> filters) {
    return method -> filters.stream().allMatch(testFilter -> testFilter.shouldRun(method));
  }
}
