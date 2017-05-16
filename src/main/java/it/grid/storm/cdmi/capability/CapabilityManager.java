package it.grid.storm.cdmi.capability;

import java.util.Map;
import java.util.Optional;

import org.indigo.cdmi.BackendCapability;
import org.indigo.cdmi.BackendCapability.CapabilityType;

public interface CapabilityManager<T> {

  BackendCapability getBackendCapability(CapabilityType capType, String capName);

  BackendCapability getBackendCapability(T metadata);

  Optional<String> getTargetCapabilityUri(BackendCapability cap, T metadata);

  String getCapabilityUri(CapabilityType type, String capName);

  boolean isAllowedToMove(BackendCapability cap, String targetCapabilityUri);

  Map<String, Object> getMetadataProvided(BackendCapability cap);
}
