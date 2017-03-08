package it.grid.storm.spi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.indigo.cdmi.BackEndException;
import org.indigo.cdmi.BackendCapability;
import org.indigo.cdmi.CdmiObjectStatus;
import org.indigo.cdmi.spi.StorageBackend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StormStorageBackend implements StorageBackend {

  private static final Logger log = LoggerFactory.getLogger(StormStorageBackend.class);

  public StormStorageBackend(Map<String, String> args) {

    log.info("StormStorageBackend");
  }

  @Override
  public List<BackendCapability> getCapabilities() throws BackEndException {

    return new ArrayList<BackendCapability>();
  }

  @Override
  public CdmiObjectStatus getCurrentStatus(String stfn) throws BackEndException {

    throw new BackEndException("not implemented");
  }

  @Override
  public void updateCdmiObject(String path, String targetCapabilitiesURI) throws BackEndException {

    throw new BackEndException("Not implemented");
  }

}
