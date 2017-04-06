package it.grid.storm.spi;

import it.grid.storm.spi.rest.metadata.model.StoRIMetadata;

public interface BackendGateway {

  public StoRIMetadata getStoRIMetadata(User user, String stfnPath) throws BackendGatewayException;

  public void addRecallTask(User user, String stfnPath) throws BackendGatewayException;

}
