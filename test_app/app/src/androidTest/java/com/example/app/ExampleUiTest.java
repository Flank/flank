package com.example.app;

import android.Manifest;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;

import com.instructure.espresso.EspressoScreenshot;
import com.instructure.espresso.ScreenshotTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ExampleUiTest {

    @Rule
    public GrantPermissionRule enableSavingScreenshots = GrantPermissionRule.grant(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE);

    @Rule
    public ScreenshotTestRule screenshotTestRule = new ScreenshotTestRule();

    @Test
    public void testPasses() {
        assertEquals(true, true);
    }

    @Test
    public void testFails() {
        assertEquals(true, false);
    }
}
