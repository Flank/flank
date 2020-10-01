package io.cucumber.junit;

import org.junit.runner.Runner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

public class CucumberJUnitRunnerBuilder extends RunnerBuilder {
    @Override
    public Runner runnerForClass(Class<?> testClass) throws InitializationError {
        if (testClass.equals(getClass())) {
            return new CucumberJUnitRunner(testClass);
        }

        return null;
    }
}
