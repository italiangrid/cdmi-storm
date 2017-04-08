package it.grid.storm.gateway.model;

import it.grid.storm.rest.metadata.model.StoriMetadata;

public interface BackendGateway {

  public StoriMetadata getStoriMetadata(User user, String stfnPath) throws BackendGatewayException;

  public void addRecallTask(User user, String stfnPath) throws BackendGatewayException;

}
