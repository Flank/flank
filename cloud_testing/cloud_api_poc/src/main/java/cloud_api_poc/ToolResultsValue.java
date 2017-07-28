package cloud_api_poc;

import static cloud_api_poc.Billing.billableMinutes;
import static cloud_api_poc.Constants.projectId;

import com.google.api.services.toolresults.model.Step;
import com.google.api.services.toolresults.model.StepDimensionValueEntry;
import com.google.api.services.toolresults.model.TestExecutionStep;
import com.google.testing.model.ToolResultsStep;
import java.util.List;

class ToolResultsValue {
  public String webLink;
  public long billableMinutes;
  public long testDurationSeconds;
  public long runDurationSeconds;
  public String name;
  public String targets;
  public List<StepDimensionValueEntry> dimensions;
  public String outcome;
  ToolResultsStep toolResultsStep;

  ToolResultsValue(Step step, ToolResultsStep toolResultsStep) {
    this.toolResultsStep = toolResultsStep;

    updateWebLink();
    billableMinutes = billableMinutes(testDurationSeconds);

    TestExecutionStep executionStep = step.getTestExecutionStep();
    testDurationSeconds = executionStep.getTestTiming().getTestProcessDuration().getSeconds();

    runDurationSeconds = step.getRunDuration().getSeconds();
    name = step.getName();
    targets = step.getDescription();
    dimensions = step.getDimensionValue();
    // "failure" or "success"
    outcome = step.getOutcome().getSummary();
  }

  private void updateWebLink() {
    webLink =
        "https://console.firebase.google.com/project/"
            + projectId
            + "/testlab/histories/"
            + toolResultsStep.getHistoryId()
            + "/matrices/"
            + toolResultsStep.getExecutionId();
  }

  @Override
  public String toString() {
    StringBuilder dimensionSb = new StringBuilder();

    for (StepDimensionValueEntry dimension : dimensions) {
      dimensionSb
          .append("  ")
          .append(dimension.getKey())
          .append(": ")
          .append(dimension.getValue())
          .append("\n");
    }

    String dimensionString = dimensionSb.toString();

    return "Billable minutes: "
        + billableMinutes
        + "m \n"
        + "Test duration: "
        + testDurationSeconds
        + "s \n"
        + "Run duration: "
        + runDurationSeconds
        + "s \n"
        + "Name: "
        + name
        + "\n"
        + "Targets: "
        + targets
        + "\n"
        + "Dimensions: \n"
        + dimensionString
        + "\n"
        + "Outcome: "
        + outcome
        + "\n"
        + webLink;
  }
}
