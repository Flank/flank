package cloud_api_poc;

import static cloud_api_poc.Utils.fatalError;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Parallel {
  private List<Callable<Object>> callables;
  private ExecutorService executorService;

  public Parallel(int shardCount) {
    callables = new ArrayList<>(shardCount);
    executorService = Executors.newFixedThreadPool(shardCount);
  }

  public void addCallable(Callable callable) {
    callables.add(callable);
  }

  public void run() {
    try {
      executorService
          .invokeAll(callables)
          .stream()
          .map(
              future -> {
                try {
                  return future.get();
                } catch (Exception e) {
                  throw new IllegalStateException(e);
                }
              });
    } catch (InterruptedException e) {
      fatalError(e);
    } finally {
      executorService.shutdown();
    }
  }
}
