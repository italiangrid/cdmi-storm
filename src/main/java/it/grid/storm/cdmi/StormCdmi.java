package it.grid.storm.cdmi;

import static it.grid.storm.rest.metadata.model.StoRIMetadata.ResourceStatus.ONLINE;
import static it.grid.storm.rest.metadata.model.StoRIMetadata.ResourceType.FOLDER;
import static org.indigo.cdmi.BackendCapability.CapabilityType.CONTAINER;
import static org.indigo.cdmi.BackendCapability.CapabilityType.DATAOBJECT;

import java.util.List;

import org.indigo.cdmi.BackendCapability;
import org.indigo.cdmi.CdmiObjectStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.rest.metadata.model.StoRIMetadata;

public class StormCdmi {

	private static final Logger log = LoggerFactory.getLogger(StormCdmi.class);
	
	private List<BackendCapability> capabilities;

	public StormCdmi(List<BackendCapability> capabilities) {
		this.capabilities = capabilities;
	}

	public BackendCapability getBackendCapability(StoRIMetadata metadata) {

		if (metadata.getType().equals(FOLDER)) {
			return capabilities.stream()
				.filter(cap -> cap.getType().equals(CONTAINER) && cap.getName().equals("Disk"))
				.findFirst()
				.get();
		}
		if (metadata.getStatus().equals(ONLINE)) {
			if (metadata.getAttributes().getMigrated()) {
				return capabilities.stream()
						.filter(cap -> cap.getType().equals(DATAOBJECT) && cap.getName().equals("DiskAndTape"))
						.findFirst()
						.get();
			}
			return capabilities.stream()
					.filter(cap -> cap.getType().equals(DATAOBJECT) && cap.getName().equals("Disk"))
					.findFirst()
					.get();
		}
		return capabilities.stream()
				.filter(cap -> cap.getType().equals(DATAOBJECT) && cap.getName().equals("Tape"))
				.findFirst()
				.get();
	}

	public CdmiObjectStatus getStatus(StoRIMetadata metadata) {

		BackendCapability cap = getBackendCapability(metadata);
		log.debug("BackendCapability: {}", cap);

		String currentCapabilitiesUri = getCapabilityURI(cap);
		String targetCapabilitiesUri = getTargetCapabilityURI(cap, metadata);

		CdmiObjectStatus currentStatus =
				new CdmiObjectStatus(cap.getCapabilities(), currentCapabilitiesUri, targetCapabilitiesUri);

		currentStatus.setChildren(metadata.getChildren());

		return currentStatus;
	}

	private String getTargetCapabilityURI(BackendCapability cap, StoRIMetadata metadata) {
		if (cap.getType().equals(CONTAINER)) {
			return null;
		}
		if (!cap.getName().equals("Tape")) {
			return null;
		}
		String recTasks = metadata.getAttributes().getTSMRecT();
		if (recTasks != null && !recTasks.isEmpty()) {
			return "/cdmi_capabilities/dataobject/DiskAndTape";
		}
		return null;
	}

	private String getCapabilityURI(BackendCapability cap) {
		return "/cdmi_capabilities/" + cap.getType().name().toLowerCase() + "/" + cap.getName();
	}
}
