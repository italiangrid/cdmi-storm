package it.grid.storm.cdmi;

import static it.grid.storm.cdmi.capability.impl.DefaultCapabilityManager.buildCapabilityUri;
import static it.grid.storm.rest.metadata.model.StoriMetadata.ResourceStatus.NEARLINE;
import static it.grid.storm.rest.metadata.model.StoriMetadata.ResourceStatus.ONLINE;
import static it.grid.storm.rest.metadata.model.StoriMetadata.ResourceType.FILE;
import static it.grid.storm.rest.metadata.model.StoriMetadata.ResourceType.FOLDER;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.indigo.cdmi.BackendCapability.CapabilityType.CONTAINER;
import static org.indigo.cdmi.BackendCapability.CapabilityType.DATAOBJECT;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import it.grid.storm.cdmi.auth.AuthorizationException;
import it.grid.storm.cdmi.auth.AuthorizationManager;
import it.grid.storm.cdmi.auth.User;
import it.grid.storm.cdmi.auth.UserProvider;
import it.grid.storm.cdmi.config.ExportIdentifier;
import it.grid.storm.cdmi.config.PluginConfiguration;
import it.grid.storm.cdmi.config.StormBackendCapability;
import it.grid.storm.cdmi.config.StormBackendContainerCapability;
import it.grid.storm.cdmi.config.StormBackendDatobjectCapability;
import it.grid.storm.gateway.model.BackendGateway;
import it.grid.storm.rest.metadata.model.FileAttributes;
import it.grid.storm.rest.metadata.model.StoriMetadata;

import java.io.File;
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

public class StormStorageBackendTest {

  private static final String ROOT_PATH = "/";

  private static final String FILE_STFN_PATH = "/test.vo/test.txt";
  private static final String FILE_ABSOLUTE_PATH = "/tmp/test.vo/test.txt";

  private static final String FOLDER_STFN_PATH = "/test.vo";
  private static final String FOLDER_ABSOLUTE_PATH = "/tmp/test.vo";

  private StormStorageBackend backend;
  private ObjectMapper mapper = new ObjectMapper();

  private BackendGateway getBackendGateway(String path, StoriMetadata meta) {
    BackendGateway gateway = Mockito.mock(BackendGateway.class);
    Mockito.when(gateway.getStoriMetadata(Mockito.any(User.class), Mockito.eq(path)))
        .thenReturn(meta);
    Mockito.doNothing().when(gateway).addRecallTask(Mockito.any(User.class), Mockito.eq(path));
    return gateway;
  }

  private List<StormBackendCapability> getStormCapabilities(ClassLoader classLoader)
      throws IOException {

    String dirPath = classLoader.getResource("capabilities").getFile();

    List<StormBackendCapability> capabilities = Lists.newArrayList();

    capabilities.add(mapper.readValue(new File(dirPath + "/container/diskonly.json"),
        StormBackendContainerCapability.class));
    capabilities.add(mapper.readValue(new File(dirPath + "/dataobject/diskonly.json"),
        StormBackendDatobjectCapability.class));
    capabilities.add(mapper.readValue(new File(dirPath + "/dataobject/diskandtape.json"),
        StormBackendDatobjectCapability.class));
    capabilities.add(mapper.readValue(new File(dirPath + "/dataobject/tapeonly.json"),
        StormBackendDatobjectCapability.class));

    return capabilities;
  }

  private PluginConfiguration getPluginConfiguration(ClassLoader classLoader) throws IOException {

    String filePath = classLoader.getResource("storm-properties.json").getFile();
    return mapper.readValue(new File(filePath), PluginConfiguration.class);
  }

  private Map<String, Object> getExports(ClassLoader classLoader) throws IOException {

    String filePath = classLoader.getResource("capabilities/exports.json").getFile();
    return mapper.readValue(new File(filePath),
        new TypeReference<Map<String, ExportIdentifier>>() {});
  }

  private User getMockedUser(String id) {

    User user = Mockito.mock(User.class);
    Mockito.when(user.getUserId()).thenReturn(id);
    return user;
  }

  private UserProvider getMockedUserProvider(User user) {

    UserProvider userProvider = Mockito.mock(UserProvider.class);
    Mockito.when(userProvider.getUser()).thenReturn(user);
    return userProvider;
  }

  private AuthorizationManager getMockedAuthorizationManager()
      throws AuthorizationException, IOException {

    AuthorizationManager authManager = Mockito.mock(AuthorizationManager.class);
    Mockito.doNothing().when(authManager).canRead(Mockito.any(User.class),
        Mockito.any(String.class));
    Mockito.doNothing().when(authManager).canRecall(Mockito.any(User.class),
        Mockito.any(String.class));
    return authManager;
  }

  @Before
  public void initStormStorageBackend() throws IOException, AuthorizationException {

    ClassLoader classLoader = getClass().getClassLoader();
    backend = new StormStorageBackend(getPluginConfiguration(classLoader),
        getStormCapabilities(classLoader), getExports(classLoader));
    backend.setAuthorizationManager(getMockedAuthorizationManager());
    backend.setUserProvider(getMockedUserProvider(getMockedUser("test-user")));
  }

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
    assertThat(status.getCurrentCapabilitiesUri(),
        equalTo(buildCapabilityUri(DATAOBJECT, "DiskOnly")));
    assertThat(status.getExportAttributes().size(), equalTo(1));
    assertNotNull(status.getExportAttributes().get("Network/WebHTTP"));
    assertThat(
        ((ExportIdentifier) status.getExportAttributes().get("Network/WebHTTP")).getIdentifier(),
        equalTo("http://localhost/cdmi/browse"));
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
    assertThat(status.getCurrentCapabilitiesUri(),
        equalTo(buildCapabilityUri(CONTAINER, "DiskOnly")));
  }

  @Test
  public void testGetCurrentStatusOfFileOnDiskAndTape() throws IOException, BackEndException {

    StoriMetadata meta = StoriMetadata.builder().absolutePath(FILE_ABSOLUTE_PATH).status(ONLINE)
        .type(FILE).attributes(FileAttributes.builder().migrated(true).build()).build();
    backend.setBackendGateway(getBackendGateway(FILE_STFN_PATH, meta));
    CdmiObjectStatus status = backend.getCurrentStatus(FILE_STFN_PATH);
    assertThat(status.getCurrentCapabilitiesUri(),
        equalTo(buildCapabilityUri(DATAOBJECT, "DiskAndTape")));
  }

  @Test
  public void testGetCurrentStatusOfFileOnTape() throws IOException, BackEndException {

    StoriMetadata meta = StoriMetadata.builder().absolutePath(FILE_ABSOLUTE_PATH).status(NEARLINE)
        .type(FILE).attributes(FileAttributes.builder().migrated(true).build()).build();
    backend.setBackendGateway(getBackendGateway(FILE_STFN_PATH, meta));
    CdmiObjectStatus status = backend.getCurrentStatus(FILE_STFN_PATH);
    assertThat(status.getCurrentCapabilitiesUri(),
        equalTo(buildCapabilityUri(DATAOBJECT, "TapeOnly")));
    assertThat(status.getTargetCapabilitiesUri(), equalTo(null));
  }

  @Test
  public void testGetCurrentStatusOfFileOnTapeEmptyRecallTasks()
      throws IOException, BackEndException {

    StoriMetadata meta = StoriMetadata.builder().absolutePath(FILE_ABSOLUTE_PATH).status(NEARLINE)
        .type(FILE).attributes(FileAttributes.builder().migrated(true).tsmRecT("").build()).build();
    backend.setBackendGateway(getBackendGateway(FILE_STFN_PATH, meta));
    CdmiObjectStatus status = backend.getCurrentStatus(FILE_STFN_PATH);
    assertThat(status.getCurrentCapabilitiesUri(),
        equalTo(buildCapabilityUri(DATAOBJECT, "TapeOnly")));
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
    assertThat(status.getCurrentCapabilitiesUri(),
        equalTo(buildCapabilityUri(DATAOBJECT, "TapeOnly")));
    assertThat(status.getTargetCapabilitiesUri(),
        equalTo(buildCapabilityUri(DATAOBJECT, "DiskAndTape")));
  }

  @Test
  public void testGetCurrentStatusOfRootPath()
      throws IOException, ValidationException, BackEndException {

    CdmiObjectStatus status = backend.getCurrentStatus(ROOT_PATH);
    assertThat(status.getCurrentCapabilitiesUri(),
        equalTo(buildCapabilityUri(CONTAINER, "DiskOnly")));
    assertThat(status.getChildren().size(), equalTo(2));
    assertThat(status.getChildren(), containsInAnyOrder("/test.vo", "/cms"));
    assertThat(status.getExportAttributes().size(), equalTo(1));
    assertNotNull(status.getExportAttributes().get("Network/WebHTTP"));
    assertThat(
        ((ExportIdentifier) status.getExportAttributes().get("Network/WebHTTP")).getIdentifier(),
        equalTo("http://localhost/cdmi/browse"));
  }

  @Test
  public void testGetCurrentStatusOfEmptyPath()
      throws IOException, ValidationException, BackEndException {

    CdmiObjectStatus status = backend.getCurrentStatus("");
    assertThat(status.getCurrentCapabilitiesUri(),
        equalTo(buildCapabilityUri(CONTAINER, "DiskOnly")));
    assertThat(status.getChildren().size(), equalTo(2));
    assertThat(status.getChildren(), containsInAnyOrder("/test.vo", "/cms"));
  }

  @Test
  public void testPostSuccessfulRecallTask() throws BackEndException {

    StoriMetadata meta = StoriMetadata.builder().absolutePath(FILE_ABSOLUTE_PATH).status(NEARLINE)
        .type(FILE).attributes(FileAttributes.builder().migrated(true).build()).build();
    backend.setBackendGateway(getBackendGateway(FILE_STFN_PATH, meta));
    String targetCapabilitiesUri = buildCapabilityUri(DATAOBJECT, "DiskAndTape");
    backend.updateCdmiObject(FILE_STFN_PATH, targetCapabilitiesUri);
  }

  @Test
  public void testPostSuccessfulRecallTaskAlreadyInTransition() throws BackEndException {

    StoriMetadata meta =
        StoriMetadata.builder().absolutePath(FILE_ABSOLUTE_PATH).status(NEARLINE).type(FILE)
            .attributes(FileAttributes.builder().migrated(true).tsmRecT("taskId").build()).build();
    backend.setBackendGateway(getBackendGateway(FILE_STFN_PATH, meta));
    String targetCapabilitiesUri = buildCapabilityUri(DATAOBJECT, "DiskAndTape");
    backend.updateCdmiObject(FILE_STFN_PATH, targetCapabilitiesUri);
  }

  @Test
  public void testPostFailRecallTaskAlreadyInTransitionToAnother() throws BackEndException {

    StoriMetadata meta =
        StoriMetadata.builder().absolutePath(FILE_ABSOLUTE_PATH).status(NEARLINE).type(FILE)
            .attributes(FileAttributes.builder().migrated(true).tsmRecT("taskId").build()).build();
    backend.setBackendGateway(getBackendGateway(FILE_STFN_PATH, meta));
    String targetCapabilitiesUri = buildCapabilityUri(DATAOBJECT, "DiskOnly");
    try {
      backend.updateCdmiObject(FILE_STFN_PATH, targetCapabilitiesUri);
    } catch (BackEndException e) {
      assertThat(e.getMessage(), equalTo("Already in transition to another capability"));
    }
  }

  @Test
  public void testPostFailRecallTaskTransitionEmpty() throws BackEndException {

    StoriMetadata meta = StoriMetadata.builder().absolutePath(FILE_ABSOLUTE_PATH).status(ONLINE)
        .type(FILE).attributes(FileAttributes.builder().migrated(false).build()).build();
    backend.setBackendGateway(getBackendGateway(FILE_STFN_PATH, meta));
    String targetCapabilitiesUri = buildCapabilityUri(DATAOBJECT, "DiskAndTape");
    try {
      backend.updateCdmiObject(FILE_STFN_PATH, targetCapabilitiesUri);
    } catch (BackEndException e) {
      assertThat(e.getMessage(), equalTo("QoS change not allowed"));
    }
  }

  @Test
  public void testPostFailRecallOfContainer() throws BackEndException {

    StoriMetadata meta = StoriMetadata.builder().absolutePath(FOLDER_ABSOLUTE_PATH).status(ONLINE)
        .type(FOLDER).build();
    backend.setBackendGateway(getBackendGateway(FOLDER_STFN_PATH, meta));
    String targetCapabilitiesUri = buildCapabilityUri(DATAOBJECT, "DiskAndTape");
    try {
      backend.updateCdmiObject(FOLDER_STFN_PATH, targetCapabilitiesUri);
    } catch (BackEndException e) {
      assertThat(e.getMessage(), equalTo("Containers QoS cannot change"));
    }
  }
}
