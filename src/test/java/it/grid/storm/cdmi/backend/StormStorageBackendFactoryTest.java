package it.grid.storm.cdmi.backend;

import java.util.HashMap;

import org.apache.http.util.Asserts;
import org.indigo.cdmi.spi.StorageBackend;
import org.junit.Test;

public class StormStorageBackendFactoryTest {

  @Test
  public void testStormStorageBackendFactoryCreation() {
    StormStorageBackendFactory factory = new StormStorageBackendFactory();
    Asserts.check(factory.getDescription().equals(StormStorageBackendFactory.description), "StormStorageBackendFactory wrong description returned");
    Asserts.check(factory.getType().equals(StormStorageBackendFactory.type), "StormStorageBackendFactory wrong type returned");
  }

  @Test
  public void testStormStorageBackendCreation() {
    StormStorageBackendFactory factory = new StormStorageBackendFactory();
    StorageBackend be = factory.createStorageBackend(new HashMap<String, String>());
    Asserts.check(be instanceof StormStorageBackend, "Wrong type of StorageBackend created");
  }
}
