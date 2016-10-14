package it.grid.storm.cdmi.backend.storm.configuration;

public class Auth {

  private User user;

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  @Override
  public String toString() {
    return "Auth [user=" + user + "]";
  }
}
