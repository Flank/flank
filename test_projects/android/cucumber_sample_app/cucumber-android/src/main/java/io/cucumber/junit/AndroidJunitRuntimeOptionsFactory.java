package io.cucumber.junit;

import android.content.Context;
import android.util.Log;

import cucumber.runtime.ClassFinder;
import cucumber.runtime.CucumberException;
import cucumber.runtime.Env;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import io.cucumber.core.model.GluePath;
import io.cucumber.core.options.CucumberOptionsAnnotationParser;
import io.cucumber.core.options.EnvironmentOptionsParser;
import io.cucumber.core.options.RuntimeOptions;

class AndroidJunitRuntimeOptionsFactory {
    private static final String TAG = "cucumber-android";

    static class Options {
        final RuntimeOptions runtimeOptions;
        final JUnitOptions jUnitOptions;

        Options(RuntimeOptions runtimeOptions, JUnitOptions jUnitOptions) {
            this.runtimeOptions = runtimeOptions;
            this.jUnitOptions = jUnitOptions;
        }
    }

    static Options createRuntimeOptions(Context context, ClassFinder classFinder, ClassLoader classLoader) {
        for (final Class<?> clazz : classFinder.getDescendants(Object.class, GluePath.parse(context.getPackageName()))) {
            if (clazz.isAnnotationPresent(CucumberOptions.class)) {
                Log.d(TAG, "Found CucumberOptions in class " + clazz.getName());
                ResourceLoader resourceLoader = new MultiLoader(classLoader);

                RuntimeOptions runtimeOptions = new EnvironmentOptionsParser(resourceLoader)
                        .parse(Env.INSTANCE)
                        .build(new CucumberOptionsAnnotationParser(resourceLoader)
                                .withOptionsProvider(new JUnitCucumberOptionsProvider())
                                .parse(clazz)
                                .build()
                        );

                JUnitOptions junitOptions = new JUnitOptionsParser()
                        .parse(runtimeOptions.getJunitOptions())
                        .setStrict(runtimeOptions.isStrict())
                        .build(new JUnitOptionsParser()
                                .parse(clazz)
                                .build()
                        );

                return new Options(runtimeOptions, junitOptions);
            }
        }

        throw new CucumberException("No CucumberOptions annotation");
    }
}
