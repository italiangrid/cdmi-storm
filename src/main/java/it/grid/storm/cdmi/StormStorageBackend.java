package it.grid.storm.cdmi;

import java.util.List;

import org.indigo.cdmi.BackEndException;
import org.indigo.cdmi.BackendCapability;
import org.indigo.cdmi.CdmiObjectStatus;
import org.indigo.cdmi.spi.StorageBackend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.gateway.SimpleIdUser;
import it.grid.storm.gateway.model.BackendGateway;
import it.grid.storm.rest.metadata.model.StoRIMetadata;

public class StormStorageBackend implements StorageBackend {

	private static final Logger log = LoggerFactory.getLogger(StormStorageBackend.class);

	private List<BackendCapability> backendCapabilities;
	private BackendGateway backendGateway;
	private StormCdmi stormCdmi;

	public StormStorageBackend(BackendGateway backendGateway,
			List<BackendCapability> backendCapabilities) {

		log.info("Storm Storage Backend");
		this.backendGateway = backendGateway;
		this.backendCapabilities = backendCapabilities;
		log.debug("BackendCapabilities: {}", this.backendCapabilities);
		this.stormCdmi = new StormCdmi(backendCapabilities);
	}


	@Override
	public List<BackendCapability> getCapabilities() throws BackEndException {

		return backendCapabilities;
	}

	@Override
	public CdmiObjectStatus getCurrentStatus(String path) throws BackEndException {

		StoRIMetadata meta = backendGateway.getStoRIMetadata(new SimpleIdUser("cdmi"), path);
		log.info("StoRIMetadata: {}", meta);

		return stormCdmi.getStatus(meta);
	}

	@Override
	public void updateCdmiObject(String path, String targetCapabilitiesURI) throws BackEndException {

		CdmiObjectStatus objectStatus = getCurrentStatus(path);

    if (objectStatus.getTargetCapabilitiesUri() != null) {
      log.debug("object {} already in transition to {}", path,
          objectStatus.getTargetCapabilitiesUri());
      return;
    }
    log.debug("current object status {}", objectStatus.toString());

		throw new BackEndException("Not implemented");
	}

}
