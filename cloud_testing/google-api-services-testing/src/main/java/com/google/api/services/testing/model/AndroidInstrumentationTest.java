/*
 * Decompiled with CFR 0_121.
 */
package com.google.api.services.testing.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.services.testing.model.FileReference;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class AndroidInstrumentationTest
extends GenericJson {
    @Key
    private FileReference appApk;
    @Key
    private String appPackageId;
    @Key
    private FileReference testApk;
    @Key
    private String testPackageId;
    @Key
    private String testRunnerClass;
    @Key
    private List<String> testTargets;

    public FileReference getAppApk() {
        return this.appApk;
    }

    public AndroidInstrumentationTest setAppApk(FileReference appApk) {
        this.appApk = appApk;
        return this;
    }

    public String getAppPackageId() {
        return this.appPackageId;
    }

    public AndroidInstrumentationTest setAppPackageId(String appPackageId) {
        this.appPackageId = appPackageId;
        return this;
    }

    public FileReference getTestApk() {
        return this.testApk;
    }

    public AndroidInstrumentationTest setTestApk(FileReference testApk) {
        this.testApk = testApk;
        return this;
    }

    public String getTestPackageId() {
        return this.testPackageId;
    }

    public AndroidInstrumentationTest setTestPackageId(String testPackageId) {
        this.testPackageId = testPackageId;
        return this;
    }

    public String getTestRunnerClass() {
        return this.testRunnerClass;
    }

    public AndroidInstrumentationTest setTestRunnerClass(String testRunnerClass) {
        this.testRunnerClass = testRunnerClass;
        return this;
    }

    public List<String> getTestTargets() {
        return this.testTargets;
    }

    public AndroidInstrumentationTest setTestTargets(List<String> testTargets) {
        this.testTargets = testTargets;
        return this;
    }

    @Override
    public AndroidInstrumentationTest set(String fieldName, Object value) {
        return (AndroidInstrumentationTest)super.set(fieldName, value);
    }

    @Override
    public AndroidInstrumentationTest clone() {
        return (AndroidInstrumentationTest)super.clone();
    }
}
