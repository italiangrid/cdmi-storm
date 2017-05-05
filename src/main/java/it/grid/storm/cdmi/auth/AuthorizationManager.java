package it.grid.storm.cdmi.auth;

import java.io.IOException;

public interface AuthorizationManager {

  public void canRead(User u, String path) throws AuthorizationException, IOException;

  public void canRecall(User u, String path) throws AuthorizationException, IOException;

}
