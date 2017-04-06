package it.grid.storm.spi.config;

public class DefaultConfiguration implements Configuration {

  private static final int DEFAULT_PORT = 9998;
  private static final String DEFAULT_HOSTNAME = "localhost";
  private static final String DEFAULT_TOKEN = "TEST-TOKEN";

  @Override
  public String getHostname() {
    return DEFAULT_HOSTNAME;
  }

  @Override
  public Integer getPort() {
    return DEFAULT_PORT;
  }

  @Override
  public String getToken() {
    return DEFAULT_TOKEN;
  }

}
