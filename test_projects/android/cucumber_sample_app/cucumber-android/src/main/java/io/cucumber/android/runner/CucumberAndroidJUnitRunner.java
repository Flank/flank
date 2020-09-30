package io.cucumber.android.runner;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.test.runner.AndroidJUnitRunner;

import io.cucumber.junit.CucumberArgumentsProvider;
import io.cucumber.junit.CucumberAndroidJUnitArguments;

/**
 * {@link AndroidJUnitRunner} for cucumber tests. It supports running tests from Android Tests Orchestrator
 */
public class CucumberAndroidJUnitRunner extends AndroidJUnitRunner implements CucumberArgumentsProvider {

    private CucumberAndroidJUnitArguments cucumberJUnitRunnerCore;

    @Override
    public void onCreate(final Bundle bundle) {
        cucumberJUnitRunnerCore = new CucumberAndroidJUnitArguments(bundle);
        super.onCreate(cucumberJUnitRunnerCore.processArgs());
    }

    @NonNull
    @Override
    public CucumberAndroidJUnitArguments getArguments() {
        return cucumberJUnitRunnerCore;
    }
}
