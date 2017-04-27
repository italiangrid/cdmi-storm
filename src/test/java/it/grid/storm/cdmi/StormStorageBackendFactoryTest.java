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

  private String getStormPropertiesPath() {

    ClassLoader classLoader = getClass().getClassLoader();
    return classLoader.getResource("storm-properties.json").getFile();
  }

  private String getStormCapabilitiesPath() {

    ClassLoader classLoader = getClass().getClassLoader();
    return classLoader.getResource("storm-capabilities.json").getFile();
  }

  @Test
  public void testDecriptionAndType() {

    StorageBackendFactory factory =
        new StormStorageBackendFactory(getStormPropertiesPath(), getStormCapabilitiesPath());
    assertThat(factory.getDescription(), equalTo("StoRM Storage Backend CDMI module"));
    assertThat(factory.getType(), equalTo("storm"));
  }

  @Test
  public void createStorageBackend() throws BackEndException {

    StorageBackendFactory factory =
        new StormStorageBackendFactory(getStormPropertiesPath(), getStormCapabilitiesPath());
    StorageBackend stormStorageBackend = factory.createStorageBackend(EMPTY_ARGS);
    log.debug("StorageBackend capabilities: {}", stormStorageBackend.getCapabilities());
    assertThat(stormStorageBackend.getCapabilities().size(), equalTo(4));
  }

  @Test(expected = IllegalArgumentException.class)
  public void createStorageBackendNullConfigFile() {

    StorageBackendFactory factory =
        new StormStorageBackendFactory(null, getStormCapabilitiesPath());
    factory.createStorageBackend(EMPTY_ARGS);
  }

  @Test(expected = IllegalArgumentException.class)
  public void createStorageBackendNotFoundConfigFile() {

    new StormStorageBackendFactory(
        "/path/to/invalid/storm-properties.json", getStormCapabilitiesPath());
  }

  @Test(expected = IllegalArgumentException.class)
  public void createStorageBackendEmptyConfigFile() {

    ClassLoader classLoader = getClass().getClassLoader();
    new StormStorageBackendFactory(
        classLoader.getResource("empty-properties.json").getFile(), getStormCapabilitiesPath());
  }

  @Test(expected = IllegalArgumentException.class)
  public void createStorageBackendNullCapabilitiesFile() {

    new StormStorageBackendFactory(getStormPropertiesPath(), null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void createDefaultStorageBackend() {

    new StormStorageBackendFactory();
  }

  /**
   * Reset system properties used by all the tests.
   */
  @After
  public void removeSystemProperties() {

    System.getProperties().remove("storm.configFile");
    System.getProperties().remove("storm.capabilitiesFile");
  }
}
