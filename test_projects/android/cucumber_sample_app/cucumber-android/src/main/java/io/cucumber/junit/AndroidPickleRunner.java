package io.cucumber.junit;

import cucumber.runner.Runner;
import cucumber.runtime.model.CucumberFeature;
import gherkin.events.PickleEvent;
import gherkin.pickles.PickleStep;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

public class AndroidPickleRunner implements PickleRunners.PickleRunner {

	private final Runner runner;
	private final PickleEvent pickleEvent;
	private final JUnitOptions jUnitOptions;
	private Description description;
	private final CucumberFeature feature;
	private String scenarioName;

	public AndroidPickleRunner(Runner runner, PickleEvent pickleEvent, JUnitOptions jUnitOptions, CucumberFeature feature, String scenarioName) {
		this.runner = runner;
		this.pickleEvent = pickleEvent;
		this.jUnitOptions = jUnitOptions;
		this.feature = feature;
		this.scenarioName = scenarioName;
	}

	@Override
	public Description getDescription()	{
		if (description == null)
		{
			description = makeDescriptionFromPickle();
		}
		return description;
	}

	@Override
	public Description describeChild(PickleStep step) {
		throw new UnsupportedOperationException("This pickle runner does not wish to describe its children");
	}

	@Override
	public void run(final RunNotifier notifier)	{
		JUnitReporter jUnitReporter = new JUnitReporter(runner.getBus(), jUnitOptions);
		jUnitReporter.startExecutionUnit(this, notifier);
		runner.runPickle(pickleEvent);
		jUnitReporter.finishExecutionUnit();
	}

	private Description makeDescriptionFromPickle()	{
		return Description.createTestDescription(feature.getName(), scenarioName, new PickleRunners.PickleId(pickleEvent));
	}
}
