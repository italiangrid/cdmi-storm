package it.grid.storm.cdmi;

import static org.indigo.cdmi.BackendCapability.CapabilityType.CONTAINER;
import static org.indigo.cdmi.BackendCapability.CapabilityType.DATAOBJECT;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.indigo.cdmi.BackendCapability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.cdmi.config.StormCapabilities;

public class Utils {

  private static final Logger log = LoggerFactory.getLogger(Utils.class);

  /**
   * Load a JSON file to its relative Java class.
   * 
   * @param filePath The JSON file absolute path.
   * @param objectClass The class of the object to de-serialize.
   * @return An instance of @param objectClass.
   * @throws IOException In case file is not found or other IO errors.
   */
  public static <T> T loadObjectFromJsonFile(String filePath, Class<T> objectClass)
      throws IOException {

    T object = null;
    log.debug("Loading {} from file {} ...", objectClass.getName(), filePath);
    ObjectMapper mapper = new ObjectMapper();
    object = mapper.readValue(new File(filePath), objectClass);
    log.debug("Loaded: {}", object);
    return object;
  }

  /**
   * Convert configuration read from file to a list of @BackendCapability
   * 
   * @param config The configuration object.
   * @return the list of @BackendCapability
   */
  public static List<BackendCapability> buildBackendCapabilities(StormCapabilities config) {

    List<BackendCapability> capabilities = new ArrayList<BackendCapability>();

    for (String name : config.getContainerClasses().keySet()) {

      BackendCapability backendCapability = new BackendCapability(name, CONTAINER);

      Map<String, Object> meta = config.getContainerClasses().get(name);
      log.debug("Reading container capability class {}: {}", name, meta);

      backendCapability.setMetadata(meta);
      backendCapability.setCapabilities(config.getContainerCapabilities());
      log.debug("BackendCapability created: {}", backendCapability);

      capabilities.add(backendCapability);
    }

    for (String name : config.getDataobjectClasses().keySet()) {

      BackendCapability backendCapability = new BackendCapability(name, DATAOBJECT);

      Map<String, Object> meta = config.getDataobjectClasses().get(name);
      log.debug("Reading dataobject capability class {}: {}", name, meta);

      backendCapability.setMetadata(meta);
      backendCapability.setCapabilities(config.getDataobjectCapabilities());
      log.debug("BackendCapability created: {}", backendCapability);

      capabilities.add(backendCapability);
    }

    return capabilities;
  }
}
