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

import android.support.test.runner.screenshot.ScreenCapture;
import android.support.test.runner.screenshot.Screenshot;
import android.util.Log;

import org.junit.runner.Description;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Used to automatically capture screenshots of failed tests.
 **/
public abstract class EspressoScreenshot {
    private static final AtomicInteger imageCounter = new AtomicInteger(0);
    private static final String dotPNG = ".png";
    private static final String underscore = "_";
    // Firebase Test Lab requires screenshots to be saved to /sdcard/screenshots
    // https://github.com/firebase/firebase-testlab-instr-lib/blob/f0a21a526499f051ac5074dc382cf79e237d2f4e/firebase-testlab-instr-lib/testlab-instr-lib/src/main/java/com/google/firebase/testlab/screenshot/FirebaseScreenCaptureProcessor.java#L36
    private static final File screenshotFolder = new File("/sdcard/screenshots");
    private static final String TAG = EspressoScreenshot.class.getSimpleName();

    private static String getScreenshotName(Description description) {
        String className = description.getClassName();
        String methodName = description.getMethodName();

        int imageNumberInt = imageCounter.incrementAndGet();
        String number = String.valueOf(imageNumberInt);
        if (imageNumberInt < 10) number = "0" + number;

        String[] components = new String[]{className, underscore, methodName, underscore, number, dotPNG};

        int length = 0;

        for (String component : components) {
            length += component.length();
        }

        StringBuilder result = new StringBuilder(length);

        for (String component : components) {
            result.append(component);
        }

        return result.toString();
    }

    public static void deleteAllScreenshots() {
        try {
            File[] screenshots = screenshotFolder.listFiles();
            if (screenshots == null) return;

            for (File screenshot : screenshots) {
                screenshot.delete();
            }
        } catch (Exception ignored) {
        }
    }

    private static void prepareScreenshotPath() {
        try {
            screenshotFolder.mkdirs();
        } catch (Exception ignored) {
        }
    }

    public static void takeScreenshot(Description description) {
        prepareScreenshotPath();

        String screenshotName = getScreenshotName(description);
        ScreenCapture capture = Screenshot.capture(); // default format is PNG

        // based on BasicScreenCaptureProcessor#process
        File imageFile = new File(screenshotFolder, screenshotName);
        BufferedOutputStream out = null;
        try {
            Log.i(TAG, "Starting to save screenshot");
            out = new BufferedOutputStream(new FileOutputStream(imageFile));
            capture.getBitmap().compress(capture.getFormat(), 100, out);
            out.flush();
            Log.i(TAG, "Finished saving screenshot");
        } catch (Exception ignored) {
            Log.e(TAG, ignored.toString());
            ignored.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ignored) {
                Log.e(TAG, ignored.toString());
                ignored.printStackTrace();
            }
        }
    }
}
