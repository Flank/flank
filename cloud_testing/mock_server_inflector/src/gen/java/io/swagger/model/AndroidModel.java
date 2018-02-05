package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.model.Form;
import java.util.ArrayList;
import java.util.List;

/**
 * A description of an Android device tests may be run on.
 **/@ApiModel(description = "A description of an Android device tests may be run on.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaInflectorServerCodegen", date = "2018-02-04T13:58:34.765-05:00")
public class AndroidModel   {
  
  @JsonProperty("supportedAbis")
  private List<String> supportedAbis = null;
  
  @JsonProperty("name")
  private String name = null;
  
  @JsonProperty("id")
  private String id = null;
  
  @JsonProperty("tags")
  private List<String> tags = null;
  
  @JsonProperty("manufacturer")
  private String manufacturer = null;
  
  @JsonProperty("brand")
  private String brand = null;
  
  @JsonProperty("screenX")
  private Integer screenX = null;
  
  @JsonProperty("codename")
  private String codename = null;
  
  @JsonProperty("screenY")
  private Integer screenY = null;
  
  @JsonProperty("form")
  private Form form = null;
  
  @JsonProperty("screenDensity")
  private Integer screenDensity = null;
  
  @JsonProperty("supportedVersionIds")
  private List<String> supportedVersionIds = null;
  
  /**
   * The list of supported ABIs for this device. This corresponds to either android.os.Build.SUPPORTED_ABIS (for API level 21 and above) or android.os.Build.CPU_ABI/CPU_ABI2. The most preferred ABI is the first element in the list.  Elements are optionally prefixed by \"version_id:\" (where version_id is the id of an AndroidVersion), denoting an ABI that is supported only on a particular version. @OutputOnly
   **/
  public AndroidModel supportedAbis(List<String> supportedAbis) {
    this.supportedAbis = supportedAbis;
    return this;
  }

  @ApiModelProperty(value = "The list of supported ABIs for this device. This corresponds to either android.os.Build.SUPPORTED_ABIS (for API level 21 and above) or android.os.Build.CPU_ABI/CPU_ABI2. The most preferred ABI is the first element in the list.  Elements are optionally prefixed by \"version_id:\" (where version_id is the id of an AndroidVersion), denoting an ABI that is supported only on a particular version. @OutputOnly")
  @JsonProperty("supportedAbis")
  public List<String> getSupportedAbis() {
    return supportedAbis;
  }
  public void setSupportedAbis(List<String> supportedAbis) {
    this.supportedAbis = supportedAbis;
  }

  /**
   * The human-readable marketing name for this device model. Examples: \"Nexus 5\", \"Galaxy S5\" @OutputOnly
   **/
  public AndroidModel name(String name) {
    this.name = name;
    return this;
  }

  @ApiModelProperty(value = "The human-readable marketing name for this device model. Examples: \"Nexus 5\", \"Galaxy S5\" @OutputOnly")
  @JsonProperty("name")
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  /**
   * The unique opaque id for this model. Use this for invoking the TestExecutionService. @OutputOnly
   **/
  public AndroidModel id(String id) {
    this.id = id;
    return this;
  }

  @ApiModelProperty(value = "The unique opaque id for this model. Use this for invoking the TestExecutionService. @OutputOnly")
  @JsonProperty("id")
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Tags for this dimension. Examples: \"default\", \"preview\", \"deprecated\"
   **/
  public AndroidModel tags(List<String> tags) {
    this.tags = tags;
    return this;
  }

  @ApiModelProperty(value = "Tags for this dimension. Examples: \"default\", \"preview\", \"deprecated\"")
  @JsonProperty("tags")
  public List<String> getTags() {
    return tags;
  }
  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  /**
   * The manufacturer of this device. @OutputOnly
   **/
  public AndroidModel manufacturer(String manufacturer) {
    this.manufacturer = manufacturer;
    return this;
  }

  @ApiModelProperty(value = "The manufacturer of this device. @OutputOnly")
  @JsonProperty("manufacturer")
  public String getManufacturer() {
    return manufacturer;
  }
  public void setManufacturer(String manufacturer) {
    this.manufacturer = manufacturer;
  }

  /**
   * The company that this device is branded with. Example: \"Google\", \"Samsung\" @OutputOnly
   **/
  public AndroidModel brand(String brand) {
    this.brand = brand;
    return this;
  }

  @ApiModelProperty(value = "The company that this device is branded with. Example: \"Google\", \"Samsung\" @OutputOnly")
  @JsonProperty("brand")
  public String getBrand() {
    return brand;
  }
  public void setBrand(String brand) {
    this.brand = brand;
  }

  /**
   * Screen size in the horizontal (X) dimension measured in pixels. @OutputOnly
   **/
  public AndroidModel screenX(Integer screenX) {
    this.screenX = screenX;
    return this;
  }

  @ApiModelProperty(value = "Screen size in the horizontal (X) dimension measured in pixels. @OutputOnly")
  @JsonProperty("screenX")
  public Integer getScreenX() {
    return screenX;
  }
  public void setScreenX(Integer screenX) {
    this.screenX = screenX;
  }

  /**
   * The name of the industrial design. This corresponds to android.os.Build.DEVICE @OutputOnly
   **/
  public AndroidModel codename(String codename) {
    this.codename = codename;
    return this;
  }

  @ApiModelProperty(value = "The name of the industrial design. This corresponds to android.os.Build.DEVICE @OutputOnly")
  @JsonProperty("codename")
  public String getCodename() {
    return codename;
  }
  public void setCodename(String codename) {
    this.codename = codename;
  }

  /**
   * Screen size in the vertical (Y) dimension measured in pixels. @OutputOnly
   **/
  public AndroidModel screenY(Integer screenY) {
    this.screenY = screenY;
    return this;
  }

  @ApiModelProperty(value = "Screen size in the vertical (Y) dimension measured in pixels. @OutputOnly")
  @JsonProperty("screenY")
  public Integer getScreenY() {
    return screenY;
  }
  public void setScreenY(Integer screenY) {
    this.screenY = screenY;
  }

  /**
   **/
  public AndroidModel form(Form form) {
    this.form = form;
    return this;
  }

  @ApiModelProperty(value = "")
  @JsonProperty("form")
  public Form getForm() {
    return form;
  }
  public void setForm(Form form) {
    this.form = form;
  }

  /**
   * Screen density in DPI. This corresponds to ro.sf.lcd_density @OutputOnly
   **/
  public AndroidModel screenDensity(Integer screenDensity) {
    this.screenDensity = screenDensity;
    return this;
  }

  @ApiModelProperty(value = "Screen density in DPI. This corresponds to ro.sf.lcd_density @OutputOnly")
  @JsonProperty("screenDensity")
  public Integer getScreenDensity() {
    return screenDensity;
  }
  public void setScreenDensity(Integer screenDensity) {
    this.screenDensity = screenDensity;
  }

  /**
   * The set of Android versions this device supports. @OutputOnly
   **/
  public AndroidModel supportedVersionIds(List<String> supportedVersionIds) {
    this.supportedVersionIds = supportedVersionIds;
    return this;
  }

  @ApiModelProperty(value = "The set of Android versions this device supports. @OutputOnly")
  @JsonProperty("supportedVersionIds")
  public List<String> getSupportedVersionIds() {
    return supportedVersionIds;
  }
  public void setSupportedVersionIds(List<String> supportedVersionIds) {
    this.supportedVersionIds = supportedVersionIds;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AndroidModel androidModel = (AndroidModel) o;
    return Objects.equals(supportedAbis, androidModel.supportedAbis) &&
        Objects.equals(name, androidModel.name) &&
        Objects.equals(id, androidModel.id) &&
        Objects.equals(tags, androidModel.tags) &&
        Objects.equals(manufacturer, androidModel.manufacturer) &&
        Objects.equals(brand, androidModel.brand) &&
        Objects.equals(screenX, androidModel.screenX) &&
        Objects.equals(codename, androidModel.codename) &&
        Objects.equals(screenY, androidModel.screenY) &&
        Objects.equals(form, androidModel.form) &&
        Objects.equals(screenDensity, androidModel.screenDensity) &&
        Objects.equals(supportedVersionIds, androidModel.supportedVersionIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(supportedAbis, name, id, tags, manufacturer, brand, screenX, codename, screenY, form, screenDensity, supportedVersionIds);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AndroidModel {\n");
    
    sb.append("    supportedAbis: ").append(toIndentedString(supportedAbis)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
    sb.append("    manufacturer: ").append(toIndentedString(manufacturer)).append("\n");
    sb.append("    brand: ").append(toIndentedString(brand)).append("\n");
    sb.append("    screenX: ").append(toIndentedString(screenX)).append("\n");
    sb.append("    codename: ").append(toIndentedString(codename)).append("\n");
    sb.append("    screenY: ").append(toIndentedString(screenY)).append("\n");
    sb.append("    form: ").append(toIndentedString(form)).append("\n");
    sb.append("    screenDensity: ").append(toIndentedString(screenDensity)).append("\n");
    sb.append("    supportedVersionIds: ").append(toIndentedString(supportedVersionIds)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}



