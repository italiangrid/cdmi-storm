package it.grid.storm.spi.config;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class StormCapabilities {

  private Map<String, Object> containerCapabilities;
  private Map<String, Object> dataobjectCapabilities;
  private Map<String, Map<String, Object>> containerClasses;
  private Map<String, Map<String, Object>> dataobjectClasses;

  @JsonCreator
  public StormCapabilities(
      @JsonProperty("container_capabilities") Map<String, Object> containerCapabilities,
      @JsonProperty("container_classes") Map<String, Map<String, Object>> containerClasses,
      @JsonProperty("dataobject_capabilities") Map<String, Object> dataobjectCapabilities,
      @JsonProperty("dataobject_classes") Map<String, Map<String, Object>> dataobjectClasses) {

    this.containerCapabilities = containerCapabilities;
    this.containerClasses = containerClasses;
    this.dataobjectCapabilities = dataobjectCapabilities;
    this.dataobjectClasses = dataobjectClasses;
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

  @Override
  public String toString() {
    return "CdmiCapabilitiesConfiguration [containerCapabilities=" + containerCapabilities
        + ", dataobjectCapabilities=" + dataobjectCapabilities + ", containerClasses="
        + containerClasses + ", dataobjectClasses=" + dataobjectClasses + "]";
  }

}
