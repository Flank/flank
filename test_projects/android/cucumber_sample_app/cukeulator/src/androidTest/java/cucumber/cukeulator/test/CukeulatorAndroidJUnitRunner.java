package cucumber.cukeulator.test;

import android.os.Bundle;

import java.io.File;

import io.cucumber.android.runner.CucumberAndroidJUnitRunner;
import io.cucumber.junit.CucumberOptions;

/**
 * The CucumberOptions annotation is mandatory for exactly one of the classes in the test project.
 * Only the first annotated class that is found will be used, others are ignored. If no class is
 * annotated, an exception is thrown. This annotation does not have to placed in runner class
 */
@CucumberOptions(
        features = "features",
        strict = true
)
public class CukeulatorAndroidJUnitRunner extends CucumberAndroidJUnitRunner {

    @Override
    public void onCreate(final Bundle bundle) {
        bundle.putString("plugin", getPluginConfigurationString()); // we programmatically create the plugin configuration
        //it crashes on Android R without it
        new File(getAbsoluteFilesPath()).mkdirs();
        super.onCreate(bundle);
    }

    /**
     * Since we want to checkout the external storage directory programmatically, we create the plugin configuration
     * here, instead of the {@link CucumberOptions} annotation.
     *
     * @return the plugin string for the configuration, which contains XML, HTML and JSON paths
     */
    private String getPluginConfigurationString() {
        String cucumber = "cucumber";
        String separator = "--";
        return "junit:" + getCucumberXml(cucumber) + separator +
                "html:" + getCucumberHtml(cucumber);
    }

    private String getCucumberHtml(String cucumber) {
        return getAbsoluteFilesPath() + "/" + cucumber + ".html";
    }

    private String getCucumberXml(String cucumber) {
        return getAbsoluteFilesPath() + "/" + cucumber + ".xml";
    }

    /**
     * The path which is used for the report files.
     *
     * @return the absolute path for the report files
     */
    private String getAbsoluteFilesPath() {

        //sdcard/Android/data/cucumber.cukeulator
        File directory = getTargetContext().getExternalFilesDir(null);
        return new File(directory, "reports").getAbsolutePath();
    }
}
