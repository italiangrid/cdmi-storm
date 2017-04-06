package it.grid.storm.cdmi;

import static org.indigo.cdmi.BackendCapability.CapabilityType.CONTAINER;
import static org.indigo.cdmi.BackendCapability.CapabilityType.DATAOBJECT;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.impl.client.HttpClients;
import org.indigo.cdmi.BackendCapability;
import org.indigo.cdmi.SubjectBasedStorageBackend;
import org.indigo.cdmi.spi.StorageBackend;
import org.indigo.cdmi.spi.StorageBackendFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.grid.storm.cdmi.config.PluginConfiguration;
import it.grid.storm.cdmi.config.ConfigurationException;
import it.grid.storm.cdmi.config.FilePluginConfiguration;
import it.grid.storm.cdmi.config.StormCapabilities;
import it.grid.storm.gateway.StormBackendGateway;
import it.grid.storm.gateway.model.BackendGateway;

public class StormStorageBackendFactory implements StorageBackendFactory {

	private static final Logger log = LoggerFactory.getLogger(StorageBackendFactory.class);

	public static final String type = "storm";
	public static final String description = "StoRM Storage Backend CDMI module";

	private PluginConfiguration config;
	private BackendGateway gateway;
	private List<BackendCapability> capabilities;
	
	public StormStorageBackendFactory() {

		config = getFilePluginConfiguration();
		gateway = new StormBackendGateway(HttpClients.createDefault(), config.getHostname(),
				config.getPort(), config.getToken());
		capabilities = getBackendCapabilitiesFromFile();
	}

	@Override
	public StorageBackend createStorageBackend(Map<String, String> args)
			throws IllegalArgumentException {

		return new SubjectBasedStorageBackend(new StormStorageBackend(gateway, capabilities));
	}

	@Override
	public String getDescription() {

		return description;
	}

	@Override
	public String getType() {

		return type;
	}

	private PluginConfiguration getFilePluginConfiguration()
			throws ConfigurationException, IllegalArgumentException {

		if (System.getProperties().containsKey("storm.configFile")) {
			return new FilePluginConfiguration(System.getProperty("storm.configFile"));
		}
		log.error("Null value for system property storm.configFile");
		throw new IllegalArgumentException("Null value for system property storm.configFile");
	}

	private List<BackendCapability> getBackendCapabilitiesFromFile()
			throws ConfigurationException, IllegalArgumentException {

		if (System.getProperties().containsKey("storm.capabilitiesFile")) {
			return buildFromJsonFile(System.getProperty("storm.capabilitiesFile"));
		}
		log.error("Null value for system property storm.capabilitiesFile");
		throw new IllegalArgumentException("Null value for system property storm.capabilitiesFile");
	}

	public static List<BackendCapability> buildFromJsonFile(String absoluteFilePath)
			throws ConfigurationException {

		log.debug("Load capabilities from file: {}", absoluteFilePath);
		StormCapabilities cap = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			cap = mapper.readValue(new File(absoluteFilePath), StormCapabilities.class);
			log.info("Capabilities: {}", cap);
		} catch (Throwable e) {
			throw new ConfigurationException(e.getMessage(), e);
		}
		return buildBackendCapabilities(cap);
	}

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
