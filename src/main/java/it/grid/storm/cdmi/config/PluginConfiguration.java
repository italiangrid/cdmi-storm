package it.grid.storm.cdmi.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(Include.NON_EMPTY)
public class PluginConfiguration {

  private BackendConfiguration backend;
  private List<VirtualFileSystem> vfs;

  /**
   * Constructor.
   * 
   * @param backend The @BackendConfiguration info.
   * @param vfs List of the supported @VirtualFileSystem.
   */
  @JsonCreator
  public PluginConfiguration(@JsonProperty("backend") BackendConfiguration backend,
      @JsonProperty("vfs") List<VirtualFileSystem> vfs) {

    this.backend = backend;
    this.vfs = vfs;
  }

  public BackendConfiguration getBackend() {
    return backend;
  }

  public List<VirtualFileSystem> getVfs() {
    return vfs;
  }

  @Override
  public String toString() {
    return "PluginConfiguration [backend=" + backend + ", vfs=" + vfs + "]";
  }

}
