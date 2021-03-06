package it.grid.storm.rest.recall.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_EMPTY)
public class TaskInsertRequest {

  public static final String MAX_RETRY_ATTEMPTS = "4";

  private String stfn;
  private String userId;

  private int retryAttempts;

  private String voName;
  private Integer pinLifetime;

  /**
   * TaskInsertRequest constructor.
   * 
   * @param stfn The SRM stfn-Root.
   * @param userId The User ID.
   * @param retryAttempts The number of retry attempts to do.
   * @param voName The Virtual Organization name.
   * @param pinLifetime The pin lifetime to set to the file.
   */
  @JsonCreator
  public TaskInsertRequest(@JsonProperty("stfn") String stfn, @JsonProperty("userId") String userId,
      @JsonProperty("retryAttempts") int retryAttempts, @JsonProperty("voName") String voName,
      @JsonProperty("pinLifetime") Integer pinLifetime) {

    this.stfn = stfn;
    this.retryAttempts = retryAttempts;
    this.voName = voName;
    this.pinLifetime = pinLifetime;
    this.userId = userId;
  }

  /**
   * TaskInsertRequest constructor from @Builder.
   * 
   * @param builder The @Builder instance.
   */
  public TaskInsertRequest(Builder builder) {
    this.stfn = builder.stfn;
    this.retryAttempts = builder.retryAttempts;
    this.voName = builder.voName;
    this.pinLifetime = builder.pinLifetime;
    this.userId = builder.userId;
  }

  public String getStfn() {
    return stfn;
  }

  public String getUserId() {
    return userId;
  }

  public int getRetryAttempts() {
    return retryAttempts;
  }

  public String getVoName() {
    return voName;
  }

  public Integer getPinLifetime() {
    return this.pinLifetime;
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public String toString() {
    return "TaskInsertRequest [stfn=" + stfn + ", userId=" + userId + ", retryAttempts="
        + retryAttempts + ", voName=" + voName + ", pinLifetime=" + pinLifetime + "]";
  }

  public static class Builder {

    private String stfn;
    private String userId;
    private int retryAttempts;

    private String voName;
    private Integer pinLifetime;

    public Builder() {
      this.retryAttempts = 0;
    }

    public Builder stfn(String stfn) {
      this.stfn = stfn;
      return this;
    }

    public Builder userId(String userId) {
      this.userId = userId;
      return this;
    }

    public Builder retryAttempts(int retryAttempts) {
      this.retryAttempts = retryAttempts;
      return this;
    }

    public Builder voName(String voName) {
      this.voName = voName;
      return this;
    }

    public Builder pinLifetime(int pinLifetime) {
      this.pinLifetime = pinLifetime;
      return this;
    }

    public TaskInsertRequest build() {
      return new TaskInsertRequest(this);
    }
  }
}
