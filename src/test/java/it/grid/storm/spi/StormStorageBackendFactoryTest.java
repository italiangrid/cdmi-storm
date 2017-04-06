package it.grid.storm.spi;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Collections;

import org.indigo.cdmi.BackEndException;
import org.indigo.cdmi.spi.StorageBackend;
import org.indigo.cdmi.spi.StorageBackendFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StormStorageBackendFactoryTest {

  private static final Logger log = LoggerFactory.getLogger(StormStorageBackendFactoryTest.class);

  @Test
  public void testDecriptionAndType() {
    StorageBackendFactory factory = new StormStorageBackendFactory();
    assertThat(factory.getDescription(), equalTo("StoRM Storage Backend CDMI module"));
    assertThat(factory.getType(), equalTo("storm"));
  }

  @Test
  public void createStorageBackend() throws BackEndException {

    ClassLoader classLoader = getClass().getClassLoader();
    System.setProperty("storm.configFile", classLoader.getResource("storm.properties").getFile());
    System.setProperty("storm.capabilitiesFile",
        classLoader.getResource("storm-capabilities.json").getFile());

    StorageBackendFactory factory = new StormStorageBackendFactory();
    StorageBackend stormStorageBackend = factory.createStorageBackend(Collections.emptyMap());
    log.debug("StorageBackend capabilities: {}", stormStorageBackend.getCapabilities());
    assertThat(stormStorageBackend.getCapabilities().size(), equalTo(4));
  }

  @Test(expected = IllegalArgumentException.class)
  public void createStorageBackendNullConfigFile() {

    ClassLoader classLoader = getClass().getClassLoader();
    System.getProperties().remove("storm.configFile");
    System.setProperty("storm.capabilitiesFile",
        classLoader.getResource("storm-capabilities.json").getFile());

    StorageBackendFactory factory = new StormStorageBackendFactory();
    factory.createStorageBackend(Collections.emptyMap());
  }

  @Test(expected = IllegalArgumentException.class)
  public void createStorageBackendNullCapabilitiesFile() {

    ClassLoader classLoader = getClass().getClassLoader();
    System.getProperties().remove("storm.capabilitiesFile");
    System.setProperty("storm.configFile", classLoader.getResource("storm.properties").getFile());

    StorageBackendFactory factory = new StormStorageBackendFactory();
    factory.createStorageBackend(Collections.emptyMap());
  }

}
