package it.grid.storm.cdmi.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(Include.NON_EMPTY)
public class Organization {

  private String name;
  private List<String> paths;

  /**
   * Constructor.
   * 
   * @param name Virtual File-system name.
   * @param paths The list of access points.
   */
  @JsonCreator
  public Organization(@JsonProperty("name") String name,
      @JsonProperty("paths") List<String> paths) {

    this.name = name;
    this.paths = paths;
  }

  public String getName() {
    return name;
  }

  public List<String> getPaths() {
    return paths;
  }

  @Override
  public String toString() {
    return "VOConfiguration [name=" + name + ", roots=" + paths + "]";
  }
}
