package it.grid.storm.cdmi.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_EMPTY)
public class VirtualOrganization {

  private String name;
  private String path;
  private String readScope;
  private String recallScope;
  private String iamGroup;

  /**
   * Constructor.
   * 
   * @param name Virtual File-system name.
   * @param path The access point.
   * @param readScope The OIDC scope used to be authorized to read.
   * @param recallScope The OIDC scope used to be authorized to recall.
   * @param iamGroup The OIDC group used to be authorized to read/recall.
   */
  @JsonCreator
  public VirtualOrganization(@JsonProperty("name") String name, @JsonProperty("path") String path,
      @JsonProperty("read-scope") String readScope,
      @JsonProperty("recall-scope") String recallScope,
      @JsonProperty("iam-group") String iamGroup) {

    this.name = name;
    this.path = path;
    this.readScope = readScope;
    this.recallScope = recallScope;
    this.iamGroup = iamGroup;
  }

  /**
   * Constructor with Builder.
   * 
   * @param builder instance.
   */
  public VirtualOrganization(Builder builder) {

    this.name = builder.name;
    this.path = builder.path;
    this.readScope = builder.readScope;
    this.recallScope = builder.recallScope;
    this.iamGroup = builder.iamGroup;
  }

  public String getName() {
    return name;
  }

  public String getPath() {
    return path;
  }

  public String getReadScope() {
    return readScope;
  }

  public String getRecallScope() {
    return recallScope;
  }

  public String getIamGroup() {
    return iamGroup;
  }

  @Override
  public String toString() {
    return "VirtualOrganization [name=" + name + ", path=" + path + ", readScope=" + readScope
        + ", recallScope=" + recallScope + ", iamGroup=" + iamGroup + "]";
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private String name;
    private String path;
    private String readScope;
    private String recallScope;
    private String iamGroup;

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder path(String path) {
      this.path = path;
      return this;
    }

    public Builder readScope(String readScope) {
      this.readScope = readScope;
      return this;
    }

    public Builder recallScope(String recallScope) {
      this.recallScope = recallScope;
      return this;
    }

    public Builder iamGroup(String iamGroup) {
      this.iamGroup = iamGroup;
      return this;
    }

    public VirtualOrganization build() {
      return new VirtualOrganization(this);
    }
  }
}
