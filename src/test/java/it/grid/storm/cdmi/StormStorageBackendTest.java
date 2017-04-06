package it.grid.storm.cdmi;

import static it.grid.storm.cdmi.StormStorageBackendFactory.buildFromJsonFile;
import static it.grid.storm.rest.metadata.model.StoRIMetadata.ResourceStatus.ONLINE;
import static it.grid.storm.rest.metadata.model.StoRIMetadata.ResourceType.FILE;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.indigo.cdmi.BackEndException;
import org.indigo.cdmi.BackendCapability;
import org.indigo.cdmi.CdmiObjectStatus;
import org.junit.Test;
import org.mockito.Mockito;

import it.grid.storm.cdmi.StormStorageBackend;
import it.grid.storm.gateway.model.BackendGateway;
import it.grid.storm.gateway.model.User;
import it.grid.storm.rest.metadata.model.FileAttributes;
import it.grid.storm.rest.metadata.model.StoRIMetadata;

public class StormStorageBackendTest {

	private final static String FILE_STFN_PATH = "/metadata/test.vo/test.txt";
	private final static String FILE_ABSOLUTE_PATH = "/tmp/test.vo/test.txt";

	private BackendGateway getBackendGateway(StoRIMetadata meta) {
		BackendGateway gateway = Mockito.mock(BackendGateway.class);
		Mockito.when(gateway.getStoRIMetadata(Mockito.any(User.class), Mockito.eq(FILE_STFN_PATH)))
			.thenReturn(meta);
		return gateway;
	}

	private List<BackendCapability> getBackendCapabilities() {
		ClassLoader classLoader = getClass().getClassLoader();
		return buildFromJsonFile(classLoader.getResource("storm-capabilities.json").getFile());
	}

	@Test
	public void testGetCurrentStatusOfFile() throws BackEndException {

		StoRIMetadata meta = StoRIMetadata.builder()
			.absolutePath(FILE_ABSOLUTE_PATH)
			.status(ONLINE)
			.type(FILE)
			.attributes(FileAttributes.builder().migrated(false).pinned(false).premigrated(false).build())
			.build();
		StormStorageBackend backend =
				new StormStorageBackend(getBackendGateway(meta), getBackendCapabilities());
		CdmiObjectStatus status = backend.getCurrentStatus(FILE_STFN_PATH);
		assertThat(status.getCurrentCapabilitiesUri(), equalTo("/cdmi_capabilities/dataobject/Disk"));
	}

}
