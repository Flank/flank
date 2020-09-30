package com.example.test_app.test

import cucumber.api.CucumberOptions

@CucumberOptions(
    features = ["features"],
    glue = ["com.example.test_app.steps"],
    tags = ["@e2e", "@smoke"]
)
@SuppressWarnings("unused")
class CucumberConfiguration {
    fun x() {
        CucumberJu
    }
}
