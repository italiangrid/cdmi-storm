package it.grid.storm.cdmi.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonInclude(Include.NON_NULL)
public class StormCapabilities {

  private Map<String, Object> containerCapabilities;
  private Map<String, Object> dataobjectCapabilities;
  private Map<String, Map<String, Object>> containerClasses;
  private Map<String, Map<String, Object>> dataobjectClasses;
  private Map<String, Map<String, Object>> containerExports;

  /**
   * Constructor.
   * 
   * @param containerCapabilities List of enabled container capabilities.
   * @param containerClasses List of container classes.
   * @param dataobjectCapabilities List of enabled data-object capabilities.
   * @param dataobjectClasses List of data-object classes.
   */
  @JsonCreator
  public StormCapabilities(
      @JsonProperty("container_capabilities") Map<String, Object> containerCapabilities,
      @JsonProperty("container_classes") Map<String, Map<String, Object>> containerClasses,
      @JsonProperty("dataobject_capabilities") Map<String, Object> dataobjectCapabilities,
      @JsonProperty("dataobject_classes") Map<String, Map<String, Object>> dataobjectClasses,
      @JsonProperty("container_exports") Map<String, Map<String, Object>> containerExports) {

    this.containerCapabilities = containerCapabilities;
    this.containerClasses = containerClasses;
    this.dataobjectCapabilities = dataobjectCapabilities;
    this.dataobjectClasses = dataobjectClasses;
    this.containerExports = containerExports;
  }

  public Map<String, Object> getContainerCapabilities() {
    return containerCapabilities;
  }

  public Map<String, Object> getDataobjectCapabilities() {
    return dataobjectCapabilities;
  }

  public Map<String, Map<String, Object>> getContainerClasses() {
    return containerClasses;
  }

  public Map<String, Map<String, Object>> getDataobjectClasses() {
    return dataobjectClasses;
  }

  public Map<String, Map<String, Object>> getContainerExports() {
    return containerExports;
  }

}
