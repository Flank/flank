package cloud_api_poc;

import static cloud_api_poc.Utils.fatalError;

import com.google.api.services.toolresults.model.Step;
import com.google.testing.model.ToolResultsStep;
import java.util.List;
import java.util.concurrent.Callable;

class ToolResultsCallable implements Callable {
  private List<ToolResultsValue> toolResultsValue;
  private ToolResultsStep toolResultsStep;

  ToolResultsCallable(List<ToolResultsValue> toolResultsValue, ToolResultsStep toolResultsStep) {
    this.toolResultsValue = toolResultsValue;
    this.toolResultsStep = toolResultsStep;
  }

  private void run() {
    try {
      Step step =
          GcToolResults.get()
              .projects()
              .histories()
              .executions()
              .steps()
              .get(
                  toolResultsStep.getProjectId(),
                  toolResultsStep.getHistoryId(),
                  toolResultsStep.getExecutionId(),
                  toolResultsStep.getStepId())
              .execute();

      toolResultsValue.add(new ToolResultsValue(step, toolResultsStep));
    } catch (Exception e) {
      fatalError(e);
    }
  }

  @Override
  public Object call() throws Exception {
    run();
    return null;
  }
}
