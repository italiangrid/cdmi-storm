package it.grid.storm.cdmi.backend.storm;

import org.indigo.cdmi.BackEndException;

public interface BackendGateway {

  public String getQosProfiles() throws BackEndException;
  
  public String getFileStatus(String stfn) throws BackEndException;
  
  public void setFileStatus(String stfn, String status) throws BackEndException;
}
