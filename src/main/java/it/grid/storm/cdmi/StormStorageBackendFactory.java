package it.grid.storm.cdmi;

import java.util.Map;

import org.indigo.cdmi.SubjectBasedStorageBackend;
import org.indigo.cdmi.spi.StorageBackend;
import org.indigo.cdmi.spi.StorageBackendFactory;

public class StormStorageBackendFactory implements StorageBackendFactory {

  public static final String type = "storm";
  public static final String description = "StoRM Storage Backend CDMI module";

  @Override
  public StorageBackend createStorageBackend(Map<String, String> args)
      throws IllegalArgumentException {

    return new SubjectBasedStorageBackend(new StormStorageBackend());
  }

  @Override
  public String getDescription() {

    return description;
  }

  @Override
  public String getType() {

    return type;
  }
}
