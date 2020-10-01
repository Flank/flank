package io.cucumber.junit;

import cucumber.runtime.model.CucumberFeature;
import gherkin.ast.Feature;
import io.cucumber.junit.AndroidPickleRunner;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

import java.util.List;

public class AndroidFeatureRunner extends ParentRunner<AndroidPickleRunner> {

	private final List<AndroidPickleRunner> children;
	private final CucumberFeature cucumberFeature;

	public AndroidFeatureRunner(Class<?> testClass, CucumberFeature cucumberFeature, List<AndroidPickleRunner> children) throws InitializationError	{
		super(testClass);
		this.cucumberFeature = cucumberFeature;
		this.children = children;
	}

	@Override
	public String getName() {
		Feature feature = cucumberFeature.getGherkinFeature().getFeature();
		return feature.getKeyword() + ": " + feature.getName();
	}

	@Override
	protected List<AndroidPickleRunner> getChildren() {
		return children;
	}

	@Override
	protected Description describeChild(AndroidPickleRunner child) {
		return child.getDescription();
	}

	@Override
	protected void runChild(AndroidPickleRunner child, RunNotifier notifier) {
		child.run(notifier);
	}
}
