package it.grid.storm.spi.config;

public class ConfigurationException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ConfigurationException(String message, Throwable e) {
    super(message, e);
  }
}
