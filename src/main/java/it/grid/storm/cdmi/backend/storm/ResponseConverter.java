package it.grid.storm.cdmi.backend.storm;

import java.util.List;

import org.indigo.cdmi.BackEndException;
import org.indigo.cdmi.BackendCapability;
import org.indigo.cdmi.CdmiObjectStatus;

public interface ResponseConverter {

  public List<BackendCapability> getBackendCapabilities(String path) throws BackEndException;

  public CdmiObjectStatus getStatus(String status) throws BackEndException;

}
