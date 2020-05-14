package com.example.test_app.test;

import java.util.Locale;

import cucumber.api.CucumberOptions;
import cucumber.api.TypeRegistry;
import cucumber.api.TypeRegistryConfigurer;

import static java.util.Locale.ENGLISH;

@CucumberOptions(features = "features")
public class TypeRegistryConfiguration implements TypeRegistryConfigurer {

    @Override
    public Locale locale() {
        return ENGLISH;
    }

    @Override
    public void configureTypeRegistry(TypeRegistry typeRegistry) {
    }
}
