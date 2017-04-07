package it.grid.storm.cdmi;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.Map;

import org.indigo.cdmi.BackEndException;
import org.indigo.cdmi.spi.StorageBackend;
import org.indigo.cdmi.spi.StorageBackendFactory;
import org.junit.After;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StormStorageBackendFactoryTest {

	private static final Logger log = LoggerFactory.getLogger(StormStorageBackendFactoryTest.class);

	private static Map<String, String> EMPTY_ARGS = Collections.emptyMap();

	private void setConfigFileProperty() {

		ClassLoader classLoader = getClass().getClassLoader();
		setConfigFileProperty(classLoader.getResource("storm-properties.json").getFile());
	}

	private void setConfigFileProperty(String value) {

		System.setProperty("storm.configFile", value);
	}

	private void setCapabilitiesFileProperty() {

		ClassLoader classLoader = getClass().getClassLoader();
		setCapabilitiesFileProperty(classLoader.getResource("storm-capabilities.json").getFile());
	}

	private void setCapabilitiesFileProperty(String value) {

		ClassLoader classLoader = getClass().getClassLoader();
		System.setProperty("storm.capabilitiesFile",
				classLoader.getResource("storm-capabilities.json").getFile());
	}

	@Test
	public void testDecriptionAndType() {

		setConfigFileProperty();
		setCapabilitiesFileProperty();

		StorageBackendFactory factory = new StormStorageBackendFactory();
		assertThat(factory.getDescription(), equalTo("StoRM Storage Backend CDMI module"));
		assertThat(factory.getType(), equalTo("storm"));
	}

	@Test
	public void createStorageBackend() throws BackEndException {

		setConfigFileProperty();
		setCapabilitiesFileProperty();

		StorageBackendFactory factory = new StormStorageBackendFactory();
		StorageBackend stormStorageBackend = factory.createStorageBackend(EMPTY_ARGS);
		log.debug("StorageBackend capabilities: {}", stormStorageBackend.getCapabilities());
		assertThat(stormStorageBackend.getCapabilities().size(), equalTo(4));
	}

	@Test(expected = IllegalArgumentException.class)
	public void createStorageBackendNullConfigFile() {

		setCapabilitiesFileProperty();

		StorageBackendFactory factory = new StormStorageBackendFactory();
		factory.createStorageBackend(EMPTY_ARGS);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createStorageBackendNotFoundConfigFile() {

		setConfigFileProperty("/path/to/invalid/storm-properties.json");
		setCapabilitiesFileProperty();

		StorageBackendFactory factory = new StormStorageBackendFactory();
		factory.createStorageBackend(EMPTY_ARGS);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createStorageBackendNullCapabilitiesFile() {

		setConfigFileProperty();

		StorageBackendFactory factory = new StormStorageBackendFactory();
		factory.createStorageBackend(EMPTY_ARGS);
	}

	@After
	public void removeSystemProperties() {

		System.getProperties().remove("storm.configFile");
		System.getProperties().remove("storm.capabilitiesFile");
	}
}
