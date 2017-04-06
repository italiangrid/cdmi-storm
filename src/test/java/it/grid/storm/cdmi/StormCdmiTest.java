package it.grid.storm.cdmi;

import static it.grid.storm.cdmi.StormStorageBackendFactory.buildFromJsonFile;
import static it.grid.storm.rest.metadata.model.StoRIMetadata.ResourceStatus.NEARLINE;
import static it.grid.storm.rest.metadata.model.StoRIMetadata.ResourceStatus.ONLINE;
import static it.grid.storm.rest.metadata.model.StoRIMetadata.ResourceType.FILE;
import static it.grid.storm.rest.metadata.model.StoRIMetadata.ResourceType.FOLDER;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isIn;
import static org.indigo.cdmi.BackendCapability.CapabilityType.CONTAINER;
import static org.indigo.cdmi.BackendCapability.CapabilityType.DATAOBJECT;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.indigo.cdmi.BackendCapability;
import org.indigo.cdmi.CdmiObjectStatus;
import org.junit.Test;

import it.grid.storm.cdmi.StormCdmi;
import it.grid.storm.rest.metadata.model.FileAttributes;
import it.grid.storm.rest.metadata.model.StoRIMetadata;

public class StormCdmiTest {

	private List<BackendCapability> getBackendCapabilities() {
		ClassLoader classLoader = getClass().getClassLoader();
		return buildFromJsonFile(classLoader.getResource("storm-capabilities.json").getFile());
	}

	@Test
	public void testGetFolderCapability() {

		StormCdmi cdmi = new StormCdmi(getBackendCapabilities());
		List<String> children = new ArrayList<String>();
		children.add("file1.dat");
		children.add("file2.dat");
		StoRIMetadata metadata = StoRIMetadata.builder()
			.absolutePath("/tmp/test.vo")
			.children(children)
			.type(FOLDER)
			.status(ONLINE)
			.build();
		BackendCapability cap = cdmi.getBackendCapability(metadata);
		assertThat(cap.getName(), equalTo("Disk"));
		assertThat(cap.getType(), equalTo(CONTAINER));
	}

	@Test
	public void testGetDiskCapability() {

		StormCdmi cdmi = new StormCdmi(getBackendCapabilities());
		FileAttributes attributes =
				FileAttributes.builder().migrated(false).build();
		StoRIMetadata metadata = StoRIMetadata.builder()
			.absolutePath("/tmp/test.vo/test.txt")
			.attributes(attributes)
			.type(FILE)
			.status(ONLINE)
			.build();
		BackendCapability cap = cdmi.getBackendCapability(metadata);
		assertThat(cap.getName(), equalTo("Disk"));
		assertThat(cap.getType(), equalTo(DATAOBJECT));
	}

	@Test
	public void testGetDiskAndTapeCapability() {

		StormCdmi cdmi = new StormCdmi(getBackendCapabilities());
		FileAttributes attributes =
				FileAttributes.builder().migrated(true).build();
		StoRIMetadata metadata = StoRIMetadata.builder()
			.absolutePath("/tmp/test.vo/test.txt")
			.attributes(attributes)
			.type(FILE)
			.status(ONLINE)
			.build();
		BackendCapability cap = cdmi.getBackendCapability(metadata);
		assertThat(cap.getName(), equalTo("DiskAndTape"));
		assertThat(cap.getType(), equalTo(DATAOBJECT));
	}

	@Test
	public void testGetTapeCapability() {

		StormCdmi cdmi = new StormCdmi(getBackendCapabilities());
		FileAttributes attributes =
				FileAttributes.builder().migrated(true).build();
		StoRIMetadata metadata = StoRIMetadata.builder()
			.absolutePath("/tmp/test.vo/test.txt")
			.attributes(attributes)
			.type(FILE)
			.status(NEARLINE)
			.build();
		BackendCapability cap = cdmi.getBackendCapability(metadata);
		assertThat(cap.getName(), equalTo("Tape"));
		assertThat(cap.getType(), equalTo(DATAOBJECT));
	}

	@Test
	public void testGetFolderStatus() {

		StormCdmi cdmi = new StormCdmi(getBackendCapabilities());
		List<String> children = new ArrayList<String>();
		children.add("file1.dat");
		children.add("file2.dat");
		StoRIMetadata metadata = StoRIMetadata.builder()
			.absolutePath("/tmp/test.vo")
			.children(children)
			.type(FOLDER)
			.status(ONLINE)
			.build();
		CdmiObjectStatus status = cdmi.getStatus(metadata);
		status.getChildren().forEach(child -> assertThat(child, isIn(children)));
		assertThat(status.getCurrentCapabilitiesUri(), endsWith("/container/Disk"));
	}

	@Test
	public void testGetFileOnDiskStatus() {

		StormCdmi cdmi = new StormCdmi(getBackendCapabilities());
		FileAttributes attributes =
				FileAttributes.builder().migrated(false).build();
		StoRIMetadata metadata = StoRIMetadata.builder()
			.absolutePath("/tmp/test.vo/test.txt")
			.attributes(attributes)
			.type(FILE)
			.status(ONLINE)
			.build();
		CdmiObjectStatus status = cdmi.getStatus(metadata);
		assertThat(status.getCurrentCapabilitiesUri(), endsWith("/dataobject/Disk"));
	}

	@Test
	public void testGetFileOnDiskAndTapeStatus() {

		StormCdmi cdmi = new StormCdmi(getBackendCapabilities());
		FileAttributes attributes =
				FileAttributes.builder().migrated(true).build();
		StoRIMetadata metadata = StoRIMetadata.builder()
			.absolutePath("/tmp/test.vo/test.txt")
			.attributes(attributes)
			.type(FILE)
			.status(ONLINE)
			.build();
		CdmiObjectStatus status = cdmi.getStatus(metadata);
		assertThat(status.getCurrentCapabilitiesUri(), endsWith("/dataobject/DiskAndTape"));
	}

	@Test
	public void testGetFileOnTapeStatus() {

		StormCdmi cdmi = new StormCdmi(getBackendCapabilities());
		FileAttributes attributes =
				FileAttributes.builder().migrated(true).build();
		StoRIMetadata metadata = StoRIMetadata.builder()
			.absolutePath("/tmp/test.vo/test.txt")
			.attributes(attributes)
			.type(FILE)
			.status(NEARLINE)
			.build();
		CdmiObjectStatus status = cdmi.getStatus(metadata);
		assertThat(status.getCurrentCapabilitiesUri(), endsWith("/dataobject/Tape"));
	}

	@Test
	public void testGetFileOnTapeAndRecalledStatus() {

		StormCdmi cdmi = new StormCdmi(getBackendCapabilities());
		FileAttributes attributes =
				FileAttributes.builder().migrated(true).TSMRecT("taskId").build();
		StoRIMetadata metadata = StoRIMetadata.builder()
			.absolutePath("/tmp/test.vo/test.txt")
			.attributes(attributes)
			.type(FILE)
			.status(NEARLINE)
			.build();
		CdmiObjectStatus status = cdmi.getStatus(metadata);
		assertThat(status.getCurrentCapabilitiesUri(), endsWith("/dataobject/Tape"));
		assertNotNull(status.getTargetCapabilitiesUri());
		assertThat(status.getTargetCapabilitiesUri(), endsWith("/dataobject/DiskAndTape"));
	}
}
