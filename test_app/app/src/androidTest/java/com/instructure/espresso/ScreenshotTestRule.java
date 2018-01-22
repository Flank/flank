/*
 * Copyright (C) 2017 - present Instructure, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.instructure.espresso;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.FailureHandler;
import android.support.test.espresso.base.DefaultFailureHandler;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.concurrent.atomic.AtomicBoolean;

public class ScreenshotTestRule implements TestRule {
    // Note: Data seeding must happen before we run a test. As a result, retrying failed tests
    //       at the JUnit level doesn't make sense because we can't run data seeeding.
    //
    // Run all test methods tryCount times. Take screenshots on failure.
    // A method rule would allow targeting specific (method.getAnnotation(Retry.class))
    private int tryCount = 1;

    public ScreenshotTestRule() {
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Throwable error = null;

                final AtomicBoolean errorHandled = new AtomicBoolean(false);

                // Espresso failure handler will capture accurate UI screenshots.
                // if we wait for `try { base.evaluate() } catch ()` then the UI will be in a different state
                //
                // Only espresso failures trigger the espresso failure handlers. For JUnit assert errors,
                // those must be captured in `try { base.evaluate() } catch ()`
                Espresso.setFailureHandler(new FailureHandler() {
                    @Override public void handle(Throwable throwable, Matcher<View> matcher) {
                            EspressoScreenshot.takeScreenshot(description);
                        errorHandled.set(true);
                        new DefaultFailureHandler(InstrumentationRegistry.getTargetContext()).handle(throwable, matcher);
                    }
                });

                for (int i = 0; i < tryCount; i++) {
                    errorHandled.set(false);
                    try {
                        base.evaluate();
                        return;
                    } catch (Throwable t) {
                        if (!errorHandled.get()) {
                            EspressoScreenshot.takeScreenshot(description);
                        }
                        error = t;
                    }
                }

                throw error;
            }
        };
    }
}
