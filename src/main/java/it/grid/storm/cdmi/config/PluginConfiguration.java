package it.grid.storm.cdmi.config;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_EMPTY)
public class PluginConfiguration {

  private BackendConfiguration backend;
  private List<VOConfiguration> vos;

  @JsonCreator
  public PluginConfiguration(@JsonProperty("backend") BackendConfiguration backend,
      @JsonProperty("vos") List<VOConfiguration> vos) {

    this.backend = backend;
    this.vos = vos;
  }

  public BackendConfiguration getBackend() {
    return backend;
  }

  public List<VOConfiguration> getVos() {
    return vos;
  }

  @Override
  public String toString() {
    return "PluginConfiguration [backend=" + backend + ", vos=" + vos + "]";
  }

}
