package it.grid.storm.cdmi.capability;

import org.indigo.cdmi.BackendCapability;
import org.indigo.cdmi.BackendCapability.CapabilityType;

public interface CapabilityManager<T> {

  BackendCapability getBackendCapability(CapabilityType capType, String capName);

  BackendCapability getBackendCapability(T metadata);

  String getTargetCapabilityUri(BackendCapability cap, T metadata);

  String getCapabilityUri(CapabilityType type, String capName);

  boolean isAllowedToMove(BackendCapability cap, String targetCapabilityUri);

}
