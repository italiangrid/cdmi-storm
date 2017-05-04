package it.grid.storm.cdmi.auth;

public class AuthorizationException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public AuthorizationException(String message) {
    super(message);
  }

  public AuthorizationException(Throwable e) {
    super(e);
  }
}
