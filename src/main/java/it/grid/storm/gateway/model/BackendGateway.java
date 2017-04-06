package it.grid.storm.gateway.model;

import it.grid.storm.rest.metadata.model.StoRIMetadata;

public interface BackendGateway {

  public StoRIMetadata getStoRIMetadata(User user, String stfnPath) throws BackendGatewayException;

  public void addRecallTask(User user, String stfnPath) throws BackendGatewayException;

}
