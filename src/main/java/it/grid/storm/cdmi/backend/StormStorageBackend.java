package it.grid.storm.cdmi.backend;

import java.util.List;

import org.indigo.cdmi.BackEndException;
import org.indigo.cdmi.BackendCapability;
import org.indigo.cdmi.CdmiObjectStatus;
import org.indigo.cdmi.spi.StorageBackend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.cdmi.backend.storm.BackendGateway;
import it.grid.storm.cdmi.backend.storm.ResponseConverter;

public class StormStorageBackend implements StorageBackend {

  private static final Logger log = LoggerFactory
      .getLogger(StormStorageBackend.class);

  /*
   * dependencies
   */
  private BackendGateway backendGateway = null;
  private ResponseConverter responseTranslator = null;

  public StormStorageBackend(BackendGateway backendGateway,
      ResponseConverter responseTranslator) {

    this.backendGateway = backendGateway;
    this.responseTranslator = responseTranslator;
  }

  public List<BackendCapability> getCapabilities() throws BackEndException {

    String qosProfiles = backendGateway.getQosProfiles();
    log.debug("Retrieved QoS profiles from remote storage: {}", qosProfiles);
    return responseTranslator.getBackendCapabilities(qosProfiles);
  }

  public CdmiObjectStatus getCurrentStatus(String path)
	throws BackEndException {

	throw new BackEndException("Not implemented");
  }

  public void updateCdmiObject(String path, String targetCapabilitiesURI)
	throws BackEndException {

    throw new BackEndException("Not implemented");
  }

}
