package it.grid.storm.cdmi;

import static it.grid.storm.cdmi.Utils.loadObjectFromJsonFile;
import static it.grid.storm.rest.metadata.model.StoRIMetadata.ResourceStatus.NEARLINE;
import static it.grid.storm.rest.metadata.model.StoRIMetadata.ResourceStatus.ONLINE;
import static it.grid.storm.rest.metadata.model.StoRIMetadata.ResourceType.FILE;
import static it.grid.storm.rest.metadata.model.StoRIMetadata.ResourceType.FOLDER;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.ValidationException;

import org.indigo.cdmi.BackEndException;
import org.indigo.cdmi.BackendCapability;
import org.indigo.cdmi.CdmiObjectStatus;
import org.junit.Test;
import org.mockito.Mockito;

import it.grid.storm.cdmi.config.StormCapabilities;
import it.grid.storm.gateway.model.BackendGateway;
import it.grid.storm.gateway.model.User;
import it.grid.storm.rest.metadata.model.FileAttributes;
import it.grid.storm.rest.metadata.model.StoRIMetadata;

public class StormStorageBackendTest {

	private final static String FILE_STFN_PATH = "/metadata/test.vo/test.txt";
	private final static String FILE_ABSOLUTE_PATH = "/tmp/test.vo/test.txt";

	private final static String FOLDER_STFN_PATH = "/metadata/test.vo";
	private final static String FOLDER_ABSOLUTE_PATH = "/tmp/test.vo";

	private BackendGateway getBackendGateway(String path, StoRIMetadata meta) {
		BackendGateway gateway = Mockito.mock(BackendGateway.class);
		Mockito.when(gateway.getStoRIMetadata(Mockito.any(User.class), Mockito.eq(path)))
			.thenReturn(meta);
		return gateway;
	}

	private List<BackendCapability> getBackendCapabilities(ClassLoader classLoader) throws IOException {
		String filePath = classLoader.getResource("storm-capabilities.json").getFile();
		StormCapabilities cap = loadObjectFromJsonFile(filePath, StormCapabilities.class);
		return Utils.buildBackendCapabilities(cap);
	}

	@Test
	public void testGetCurrentStatusOfFile()
			throws IOException, ValidationException, BackEndException {

		ClassLoader classLoader = getClass().getClassLoader();
		StoRIMetadata meta = StoRIMetadata.builder()
			.absolutePath(FILE_ABSOLUTE_PATH)
			.status(ONLINE)
			.type(FILE)
			.attributes(FileAttributes.builder().migrated(false).pinned(false).premigrated(false).build())
			.build();
		StormStorageBackend backend = new StormStorageBackend(getBackendCapabilities(classLoader),
				getBackendGateway(FILE_STFN_PATH, meta));
		CdmiObjectStatus status = backend.getCurrentStatus(FILE_STFN_PATH);
		assertThat(status.getCurrentCapabilitiesUri(), equalTo("/cdmi_capabilities/dataobject/Disk"));
	}

	@Test
	public void testGetCurrentStatusOfFolder()
			throws IOException, ValidationException, BackEndException {

		ClassLoader classLoader = getClass().getClassLoader();
		List<String> children = new ArrayList<String>();
		children.add("file1.dat");
		children.add("file2.dat");
		StoRIMetadata metadata = StoRIMetadata.builder()
			.absolutePath(FOLDER_ABSOLUTE_PATH)
			.children(children)
			.type(FOLDER)
			.status(ONLINE)
			.build();
		StormStorageBackend backend = new StormStorageBackend(getBackendCapabilities(classLoader),
				getBackendGateway(FOLDER_STFN_PATH, metadata));
		CdmiObjectStatus status = backend.getCurrentStatus(FOLDER_STFN_PATH);
		assertThat(status.getCurrentCapabilitiesUri(), equalTo("/cdmi_capabilities/container/Disk"));
	}

	@Test
	public void testGetCurrentStatusOfFileOnDiskAndTape() throws IOException, BackEndException {

		ClassLoader classLoader = getClass().getClassLoader();
		StoRIMetadata meta = StoRIMetadata.builder()
			.absolutePath(FILE_ABSOLUTE_PATH)
			.status(ONLINE)
			.type(FILE)
			.attributes(FileAttributes.builder().migrated(true).build())
			.build();
		StormStorageBackend backend = new StormStorageBackend(getBackendCapabilities(classLoader),
				getBackendGateway(FILE_STFN_PATH, meta));
		CdmiObjectStatus status = backend.getCurrentStatus(FILE_STFN_PATH);
		assertThat(status.getCurrentCapabilitiesUri(),
				equalTo("/cdmi_capabilities/dataobject/DiskAndTape"));
	}

	@Test
	public void testGetCurrentStatusOfFileOnTape() throws IOException, BackEndException {

		ClassLoader classLoader = getClass().getClassLoader();
		StoRIMetadata meta = StoRIMetadata.builder()
			.absolutePath(FILE_ABSOLUTE_PATH)
			.status(NEARLINE)
			.type(FILE)
			.attributes(FileAttributes.builder().migrated(true).build())
			.build();
		StormStorageBackend backend = new StormStorageBackend(getBackendCapabilities(classLoader),
				getBackendGateway(FILE_STFN_PATH, meta));
		CdmiObjectStatus status = backend.getCurrentStatus(FILE_STFN_PATH);
		assertThat(status.getCurrentCapabilitiesUri(), equalTo("/cdmi_capabilities/dataobject/Tape"));
		assertThat(status.getTargetCapabilitiesUri(), equalTo(null));
	}

	@Test
	public void testGetCurrentStatusOfFileOnTapeRecallInProgress() throws IOException, BackEndException {

		ClassLoader classLoader = getClass().getClassLoader();
		StoRIMetadata meta = StoRIMetadata.builder()
			.absolutePath(FILE_ABSOLUTE_PATH)
			.status(NEARLINE)
			.type(FILE)
			.attributes(FileAttributes.builder().migrated(true).TSMRecT("taskId").build())
			.build();
		StormStorageBackend backend = new StormStorageBackend(getBackendCapabilities(classLoader),
				getBackendGateway(FILE_STFN_PATH, meta));
		CdmiObjectStatus status = backend.getCurrentStatus(FILE_STFN_PATH);
		assertThat(status.getCurrentCapabilitiesUri(), equalTo("/cdmi_capabilities/dataobject/Tape"));
		assertThat(status.getTargetCapabilitiesUri(), equalTo("/cdmi_capabilities/dataobject/DiskAndTape"));
	}
}
