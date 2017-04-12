package it.grid.storm.cdmi;

import static it.grid.storm.cdmi.StormStorageBackend.getCapabilityUri;
import static it.grid.storm.cdmi.StormStorageBackend.CapabilityClasses.DiskAndTape;
import static it.grid.storm.cdmi.StormStorageBackend.CapabilityClasses.DiskOnly;
import static it.grid.storm.cdmi.StormStorageBackend.CapabilityClasses.TapeOnly;
import static it.grid.storm.cdmi.Utils.loadObjectFromJsonFile;
import static it.grid.storm.rest.metadata.model.StoriMetadata.ResourceStatus.NEARLINE;
import static it.grid.storm.rest.metadata.model.StoriMetadata.ResourceStatus.ONLINE;
import static it.grid.storm.rest.metadata.model.StoriMetadata.ResourceType.FILE;
import static it.grid.storm.rest.metadata.model.StoriMetadata.ResourceType.FOLDER;
import static org.hamcrest.Matchers.equalTo;
import static org.indigo.cdmi.BackendCapability.CapabilityType.CONTAINER;
import static org.indigo.cdmi.BackendCapability.CapabilityType.DATAOBJECT;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.ValidationException;

import org.indigo.cdmi.BackEndException;
import org.indigo.cdmi.CdmiObjectStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import it.grid.storm.cdmi.config.PluginConfiguration;
import it.grid.storm.cdmi.config.StormCapabilities;
import it.grid.storm.gateway.model.BackendGateway;
import it.grid.storm.gateway.model.User;
import it.grid.storm.rest.metadata.model.FileAttributes;
import it.grid.storm.rest.metadata.model.StoriMetadata;

public class StormStorageBackendTest {

  private final static String ROOT_PATH = "/";

  private final static String FILE_STFN_PATH = "/test.vo/test.txt";
  private final static String FILE_DAV_IDENTIFIER = "https://webdav.local.io:8443/test.vo/test.txt";
  private final static String FILE_ABSOLUTE_PATH = "/tmp/test.vo/test.txt";
  private final static String FILE_SRM_IDENTIFIER = "srm://frontend.local.io:8444/test.vo/test.txt";

  private final static String FOLDER_STFN_PATH = "/test.vo";
  private final static String FOLDER_ABSOLUTE_PATH = "/tmp/test.vo";

  private StormStorageBackend backend;

  private BackendGateway getBackendGateway(String path, StoriMetadata meta) {
    BackendGateway gateway = Mockito.mock(BackendGateway.class);
    Mockito.when(gateway.getStoriMetadata(Mockito.any(User.class), Mockito.eq(path)))
        .thenReturn(meta);
    Mockito.doNothing().when(gateway).addRecallTask(Mockito.any(User.class), Mockito.eq(path));
    return gateway;
  }

  private StormCapabilities getStormCapabilities(ClassLoader classLoader) throws IOException {
    String filePath = classLoader.getResource("storm-capabilities.json").getFile();
    return loadObjectFromJsonFile(filePath, StormCapabilities.class);
  }

  private PluginConfiguration getPluginConfiguration(ClassLoader classLoader) throws IOException {

    String filePath = classLoader.getResource("storm-properties.json").getFile();
    return loadObjectFromJsonFile(filePath, PluginConfiguration.class);
  }

  @Before
  public void initStormStorageBackend() throws IOException {

    ClassLoader classLoader = getClass().getClassLoader();
    backend = new StormStorageBackend(getPluginConfiguration(classLoader),
        getStormCapabilities(classLoader));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testGetCurrentStatusOfFile()
      throws IOException, ValidationException, BackEndException {

    StoriMetadata meta =
        StoriMetadata.builder().absolutePath(FILE_ABSOLUTE_PATH).status(ONLINE).type(FILE)
            .attributes(
                FileAttributes.builder().migrated(false).pinned(false).premigrated(false).build())
            .build();
    backend.setBackendGateway(getBackendGateway(FILE_STFN_PATH, meta));
    CdmiObjectStatus status = backend.getCurrentStatus(FILE_STFN_PATH);
    assertThat(status.getCurrentCapabilitiesUri(), equalTo(getCapabilityUri(DATAOBJECT, DiskOnly)));
    assertThat(status.getExportAttributes().size(), equalTo(2));
    assertNotNull(status.getExportAttributes().get("Network/WebDAV"));
    assertThat(((Map<String, Object>) status.getExportAttributes().get("Network/WebDAV"))
        .get("identifier"), equalTo(FILE_DAV_IDENTIFIER));
    assertNotNull(status.getExportAttributes().get("Network/SRM"));
    assertThat(
        ((Map<String, Object>) status.getExportAttributes().get("Network/SRM")).get("identifier"),
        equalTo(FILE_SRM_IDENTIFIER));

  }

  @Test
  public void testGetCurrentStatusOfFolder()
      throws IOException, ValidationException, BackEndException {

    List<String> children = new ArrayList<String>();
    children.add("file1.dat");
    children.add("file2.dat");
    StoriMetadata meta = StoriMetadata.builder().absolutePath(FOLDER_ABSOLUTE_PATH)
        .children(children).type(FOLDER).status(ONLINE).build();
    backend.setBackendGateway(getBackendGateway(FOLDER_STFN_PATH, meta));
    CdmiObjectStatus status = backend.getCurrentStatus(FOLDER_STFN_PATH);
    assertThat(status.getCurrentCapabilitiesUri(), equalTo(getCapabilityUri(CONTAINER, DiskOnly)));
  }

  @Test
  public void testGetCurrentStatusOfFileOnDiskAndTape() throws IOException, BackEndException {

    StoriMetadata meta = StoriMetadata.builder().absolutePath(FILE_ABSOLUTE_PATH).status(ONLINE)
        .type(FILE).attributes(FileAttributes.builder().migrated(true).build()).build();
    backend.setBackendGateway(getBackendGateway(FILE_STFN_PATH, meta));
    CdmiObjectStatus status = backend.getCurrentStatus(FILE_STFN_PATH);
    assertThat(status.getCurrentCapabilitiesUri(),
        equalTo(getCapabilityUri(DATAOBJECT, DiskAndTape)));
  }

  @Test
  public void testGetCurrentStatusOfFileOnTape() throws IOException, BackEndException {

    StoriMetadata meta = StoriMetadata.builder().absolutePath(FILE_ABSOLUTE_PATH).status(NEARLINE)
        .type(FILE).attributes(FileAttributes.builder().migrated(true).build()).build();
    backend.setBackendGateway(getBackendGateway(FILE_STFN_PATH, meta));
    CdmiObjectStatus status = backend.getCurrentStatus(FILE_STFN_PATH);
    assertThat(status.getCurrentCapabilitiesUri(), equalTo(getCapabilityUri(DATAOBJECT, TapeOnly)));
    assertThat(status.getTargetCapabilitiesUri(), equalTo(null));
  }

  @Test
  public void testGetCurrentStatusOfFileOnTapeEmptyRecallTasks()
      throws IOException, BackEndException {

    StoriMetadata meta = StoriMetadata.builder().absolutePath(FILE_ABSOLUTE_PATH).status(NEARLINE)
        .type(FILE).attributes(FileAttributes.builder().migrated(true).tsmRecT("").build()).build();
    backend.setBackendGateway(getBackendGateway(FILE_STFN_PATH, meta));
    CdmiObjectStatus status = backend.getCurrentStatus(FILE_STFN_PATH);
    assertThat(status.getCurrentCapabilitiesUri(), equalTo(getCapabilityUri(DATAOBJECT, TapeOnly)));
    assertThat(status.getTargetCapabilitiesUri(), equalTo(null));
  }

  @Test
  public void testGetCurrentStatusOfFileOnTapeRecallInProgress()
      throws IOException, BackEndException {

    StoriMetadata meta =
        StoriMetadata.builder().absolutePath(FILE_ABSOLUTE_PATH).status(NEARLINE).type(FILE)
            .attributes(FileAttributes.builder().migrated(true).tsmRecT("taskId").build()).build();
    backend.setBackendGateway(getBackendGateway(FILE_STFN_PATH, meta));
    CdmiObjectStatus status = backend.getCurrentStatus(FILE_STFN_PATH);
    assertThat(status.getCurrentCapabilitiesUri(), equalTo(getCapabilityUri(DATAOBJECT, TapeOnly)));
    assertThat(status.getTargetCapabilitiesUri(),
        equalTo(getCapabilityUri(DATAOBJECT, DiskAndTape)));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testGetCurrentStatusOfRootPath()
      throws IOException, ValidationException, BackEndException {

    CdmiObjectStatus status = backend.getCurrentStatus(ROOT_PATH);
    assertThat(status.getCurrentCapabilitiesUri(), equalTo(getCapabilityUri(CONTAINER, DiskOnly)));
    assertThat(status.getChildren().size(), equalTo(1));
    assertThat(status.getChildren().get(0), equalTo("/test.vo"));
    assertThat(status.getExportAttributes().size(), equalTo(2));
    assertNotNull(status.getExportAttributes().get("Network/WebDAV"));
    assertThat(((Map<String, Object>) status.getExportAttributes().get("Network/WebDAV"))
        .get("identifier"), equalTo("https://webdav.local.io:8443/"));
    assertNotNull(status.getExportAttributes().get("Network/SRM"));
    assertThat(
        ((Map<String, Object>) status.getExportAttributes().get("Network/SRM")).get("identifier"),
        equalTo("srm://frontend.local.io:8444/"));
  }

  @Test
  public void testGetCurrentStatusOfEmptyPath()
      throws IOException, ValidationException, BackEndException {

    CdmiObjectStatus status = backend.getCurrentStatus("");
    assertThat(status.getCurrentCapabilitiesUri(), equalTo(getCapabilityUri(CONTAINER, DiskOnly)));
    assertThat(status.getChildren().size(), equalTo(1));
    assertThat(status.getChildren().get(0), equalTo("/test.vo"));
  }

  @Test
  public void testPostSuccessfulRecallTask() throws BackEndException {

    StoriMetadata meta = StoriMetadata.builder().absolutePath(FILE_ABSOLUTE_PATH).status(NEARLINE)
        .type(FILE).attributes(FileAttributes.builder().migrated(true).build()).build();
    backend.setBackendGateway(getBackendGateway(FILE_STFN_PATH, meta));
    String targetCapabilitiesUri = getCapabilityUri(DATAOBJECT, DiskAndTape);
    backend.updateCdmiObject(FILE_STFN_PATH, targetCapabilitiesUri);
  }

  @Test
  public void testPostSuccessfulRecallTaskAlreadyInTransition() throws BackEndException {

    StoriMetadata meta =
        StoriMetadata.builder().absolutePath(FILE_ABSOLUTE_PATH).status(NEARLINE).type(FILE)
            .attributes(FileAttributes.builder().migrated(true).tsmRecT("taskId").build()).build();
    backend.setBackendGateway(getBackendGateway(FILE_STFN_PATH, meta));
    String targetCapabilitiesUri = getCapabilityUri(DATAOBJECT, DiskAndTape);
    backend.updateCdmiObject(FILE_STFN_PATH, targetCapabilitiesUri);
  }

  @Test
  public void testPostFailRecallTaskAlreadyInTransitionToAnother() throws BackEndException {

    StoriMetadata meta =
        StoriMetadata.builder().absolutePath(FILE_ABSOLUTE_PATH).status(NEARLINE).type(FILE)
            .attributes(FileAttributes.builder().migrated(true).tsmRecT("taskId").build()).build();
    backend.setBackendGateway(getBackendGateway(FILE_STFN_PATH, meta));
    String targetCapabilitiesUri = getCapabilityUri(DATAOBJECT, DiskOnly);
    try {
      backend.updateCdmiObject(FILE_STFN_PATH, targetCapabilitiesUri);
    } catch (BackEndException e) {
      assertThat(e.getMessage(), equalTo("Already in transition to another capability"));
    }
  }

  @Test
  public void testPostFailRecallTaskTransitionEmpty() throws BackEndException {

    StoriMetadata meta =
        StoriMetadata.builder().absolutePath(FILE_ABSOLUTE_PATH).status(ONLINE).type(FILE)
            .attributes(FileAttributes.builder().migrated(false).build()).build();
    backend.setBackendGateway(getBackendGateway(FILE_STFN_PATH, meta));
    String targetCapabilitiesUri = getCapabilityUri(DATAOBJECT, DiskAndTape);
    try {
      backend.updateCdmiObject(FILE_STFN_PATH, targetCapabilitiesUri);
    } catch (BackEndException e) {
      assertThat(e.getMessage(), equalTo("No transitions allowed from current capability"));
    }
  }

  @Test
  public void testPostFailRecallOfContainer() throws BackEndException {

    StoriMetadata meta = StoriMetadata.builder().absolutePath(FOLDER_ABSOLUTE_PATH).status(ONLINE)
        .type(FOLDER).build();
    backend.setBackendGateway(getBackendGateway(FOLDER_STFN_PATH, meta));
    String targetCapabilitiesUri = getCapabilityUri(DATAOBJECT, DiskAndTape);
    try {
      backend.updateCdmiObject(FOLDER_STFN_PATH, targetCapabilitiesUri);
    } catch (BackEndException e) {
      assertThat(e.getMessage(), equalTo("Containers QoS cannot change"));
    }
  }
}
