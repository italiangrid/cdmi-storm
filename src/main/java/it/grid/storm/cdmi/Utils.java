package it.grid.storm.cdmi;

import java.util.ArrayList;
import java.util.List;

import org.indigo.cdmi.BackendCapability;

import it.grid.storm.cdmi.config.StormBackendCapability;

public class Utils {

  /**
   * Convert configuration to a list of @BackendCapability
   * 
   * @param configCapabilities The @List of the configured @StormBackendCapability.
   * @return the list of @BackendCapability
   */
  public static List<BackendCapability> buildBackendCapabilities(List<StormBackendCapability> configCapabilities) {

    List<BackendCapability> capabilities = new ArrayList<BackendCapability>();

    for (StormBackendCapability cap: configCapabilities) {
      
      BackendCapability bc = new BackendCapability(cap.getName(), cap.getType());
      bc.setMetadata(cap.getMetadata());
      bc.setCapabilities(cap.getCapabilities());
      capabilities.add(bc);
    }

    return capabilities;
  }

}
