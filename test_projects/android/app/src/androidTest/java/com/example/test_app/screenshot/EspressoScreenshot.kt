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

package com.example.test_app.screenshot

import android.util.Log
import androidx.test.runner.screenshot.Screenshot
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger
import org.junit.runner.Description

/**
 * Used to automatically capture screenshots of failed tests.
 */
object EspressoScreenshot {
    private val imageCounter = AtomicInteger(0)
    private val dotPNG = ".png"
    private val underscore = "_"
    // Firebase Test Lab requires screenshots to be saved to /sdcard/screenshots
    // https://github.com/firebase/firebase-testlab-instr-lib/blob/f0a21a526499f051ac5074dc382cf79e237d2f4e/firebase-testlab-instr-lib/testlab-instr-lib/src/main/java/com/google/firebase/testlab/screenshot/FirebaseScreenCaptureProcessor.java#L36
    private val screenshotFolder = File("/sdcard/screenshots")
    private val TAG = EspressoScreenshot::class.java.simpleName

    private fun getScreenshotName(description: Description): String {
        val className = description.className
        val methodName = description.methodName

        val imageNumberInt = imageCounter.incrementAndGet()
        var number = imageNumberInt.toString()
        if (imageNumberInt < 10) number = "0$number"

        val components = arrayOf(className, underscore, methodName, underscore, number, dotPNG)

        var length = 0

        for (component in components) {
            length += component.length
        }

        val result = StringBuilder(length)

        for (component in components) {
            result.append(component)
        }

        return result.toString()
    }

    private fun prepareScreenshotPath() {
        try {
            screenshotFolder.mkdirs()
        } catch (ignored: Exception) {
            Log.e(TAG, "Failed to make screenshot folder $screenshotFolder")
        }
    }

    fun takeScreenshot(description: Description) {
        prepareScreenshotPath()

        val screenshotName = getScreenshotName(description)
        val capture = Screenshot.capture() // default format is PNG

        // based on BasicScreenCaptureProcessor#process
        val imageFile = File(screenshotFolder, screenshotName)
        var out: BufferedOutputStream? = null
        try {
            Log.i(TAG, "Saving screenshot to " + imageFile.absolutePath)
            out = BufferedOutputStream(FileOutputStream(imageFile))
            capture.bitmap.compress(capture.format, 100, out)
            out.flush()
            Log.i(TAG, "Screenshot exists? " + imageFile.exists())
        } catch (ignored: Exception) {
            Log.e(TAG, ignored.toString())
            ignored.printStackTrace()
        } finally {
            try {
                out?.close()
            } catch (ignored: IOException) {
                Log.e(TAG, ignored.toString())
                ignored.printStackTrace()
            }
        }
    }
}
