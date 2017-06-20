package cloud_api_poc;

public class Utils {

  public static void sleep(int milliseconds) {
    try {
      Thread.sleep(milliseconds);
    } catch (Exception e) {
    }
  }

  public static void fatalError(Exception e) {
    e.printStackTrace();
    System.exit(-1);
  }

  public static void fatalError(String e) {
    System.err.println(e);
    System.exit(-1);
  }
}
