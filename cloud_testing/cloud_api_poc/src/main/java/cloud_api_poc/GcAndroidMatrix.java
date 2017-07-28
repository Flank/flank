package cloud_api_poc;

import com.google.common.collect.Lists;
import com.google.testing.model.AndroidMatrix;

public abstract class GcAndroidMatrix {

  private GcAndroidMatrix() {}

  public static AndroidMatrix build(
      String modelIds, String versionIds, String locales, String orientations) {
    AndroidMatrix androidMatrix = new AndroidMatrix();
    androidMatrix.setAndroidModelIds(Lists.newArrayList(modelIds));
    androidMatrix.setAndroidVersionIds(Lists.newArrayList(versionIds));
    androidMatrix.setLocales(Lists.newArrayList(locales));
    androidMatrix.setOrientations(Lists.newArrayList(orientations));
    return androidMatrix;
  }
}
