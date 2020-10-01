package io.cucumber.junit;

import androidx.annotation.NonNull;

public interface CucumberArgumentsProvider {
    @NonNull
    CucumberAndroidJUnitArguments getArguments();
}
