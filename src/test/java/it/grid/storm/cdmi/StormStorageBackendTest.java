package it.grid.storm.cdmi;

import static it.grid.storm.cdmi.StormStorageBackend.BASE_CONTAINER;
import static it.grid.storm.cdmi.StormStorageBackend.BASE_DATAOBJECT;
import static it.grid.storm.cdmi.StormStorageBackend.ContainerClasses.FolderOnDisk;
import static it.grid.storm.cdmi.StormStorageBackend.DataobjectClasses.FileOnDisk;
import static it.grid.storm.cdmi.StormStorageBackend.DataobjectClasses.FileOnDiskAndTape;
import static it.grid.storm.cdmi.StormStorageBackend.DataobjectClasses.FileOnTape;
import static it.grid.storm.cdmi.Utils.loadObjectFromJsonFile;
import static it.grid.storm.rest.metadata.model.StoriMetadata.ResourceStatus.NEARLINE;
import static it.grid.storm.rest.metadata.model.StoriMetadata.ResourceStatus.ONLINE;
import static it.grid.storm.rest.metadata.model.StoriMetadata.ResourceType.FILE;
import static it.grid.storm.rest.metadata.model.StoriMetadata.ResourceType.FOLDER;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.ValidationException;

import org.indigo.cdmi.BackEndException;
import org.indigo.cdmi.CdmiObjectStatus;
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
  private final static String FILE_ABSOLUTE_PATH = "/tmp/test.vo/test.txt";

  private final static String FOLDER_STFN_PATH = "/test.vo";
  private final static String FOLDER_ABSOLUTE_PATH = "/tmp/test.vo";

  private final static String FOLDER_ON_DISK = BASE_CONTAINER + "/" + FolderOnDisk;
  private final static String FILE_ON_DISK = BASE_DATAOBJECT + "/" + FileOnDisk;
  private final static String FILE_ON_DISKANDTAPE = BASE_DATAOBJECT + "/" + FileOnDiskAndTape;
  private final static String FILE_ON_TAPE = BASE_DATAOBJECT + "/" + FileOnTape;

  private BackendGateway getBackendGateway(String path, StoriMetadata meta) {
    BackendGateway gateway = Mockito.mock(BackendGateway.class);
    Mockito.when(gateway.getStoriMetadata(Mockito.any(User.class), Mockito.eq(path)))
        .thenReturn(meta);
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

  // private List<BackendCapability> getBackendCapabilities(ClassLoader classLoader)
  // throws IOException {
  // String filePath = classLoader.getResource("storm-capabilities.json").getFile();
  // StormCapabilities cap = loadObjectFromJsonFile(filePath, StormCapabilities.class);
  // return Utils.buildBackendCapabilities(cap);
  // }

  @Test
  public void testGetCurrentStatusOfFile()
      throws IOException, ValidationException, BackEndException {

    ClassLoader classLoader = getClass().getClassLoader();
    StoriMetadata meta =
        StoriMetadata.builder().absolutePath(FILE_ABSOLUTE_PATH).status(ONLINE).type(FILE)
            .attributes(
                FileAttributes.builder().migrated(false).pinned(false).premigrated(false).build())
            .build();
    StormStorageBackend backend = new StormStorageBackend(getPluginConfiguration(classLoader),
        getStormCapabilities(classLoader));
    backend.setBackendGateway(getBackendGateway(FILE_STFN_PATH, meta));
    CdmiObjectStatus status = backend.getCurrentStatus(FILE_STFN_PATH);
    assertThat(status.getCurrentCapabilitiesUri(), equalTo(FILE_ON_DISK));
  }

  @Test
  public void testGetCurrentStatusOfFolder()
      throws IOException, ValidationException, BackEndException {

    ClassLoader classLoader = getClass().getClassLoader();
    List<String> children = new ArrayList<String>();
    children.add("file1.dat");
    children.add("file2.dat");
    StoriMetadata meta = StoriMetadata.builder().absolutePath(FOLDER_ABSOLUTE_PATH)
        .children(children).type(FOLDER).status(ONLINE).build();
    StormStorageBackend backend = new StormStorageBackend(getPluginConfiguration(classLoader),
        getStormCapabilities(classLoader));
    backend.setBackendGateway(getBackendGateway(FOLDER_STFN_PATH, meta));
    CdmiObjectStatus status = backend.getCurrentStatus(FOLDER_STFN_PATH);
    assertThat(status.getCurrentCapabilitiesUri(), equalTo(FOLDER_ON_DISK));
  }

  @Test
  public void testGetCurrentStatusOfFileOnDiskAndTape() throws IOException, BackEndException {

    ClassLoader classLoader = getClass().getClassLoader();
    StoriMetadata meta = StoriMetadata.builder().absolutePath(FILE_ABSOLUTE_PATH).status(ONLINE)
        .type(FILE).attributes(FileAttributes.builder().migrated(true).build()).build();
    StormStorageBackend backend = new StormStorageBackend(getPluginConfiguration(classLoader),
        getStormCapabilities(classLoader));
    backend.setBackendGateway(getBackendGateway(FILE_STFN_PATH, meta));
    CdmiObjectStatus status = backend.getCurrentStatus(FILE_STFN_PATH);
    assertThat(status.getCurrentCapabilitiesUri(), equalTo(FILE_ON_DISKANDTAPE));
  }

  @Test
  public void testGetCurrentStatusOfFileOnTape() throws IOException, BackEndException {

    ClassLoader classLoader = getClass().getClassLoader();
    StoriMetadata meta = StoriMetadata.builder().absolutePath(FILE_ABSOLUTE_PATH).status(NEARLINE)
        .type(FILE).attributes(FileAttributes.builder().migrated(true).build()).build();
    StormStorageBackend backend = new StormStorageBackend(getPluginConfiguration(classLoader),
        getStormCapabilities(classLoader));
    backend.setBackendGateway(getBackendGateway(FILE_STFN_PATH, meta));
    CdmiObjectStatus status = backend.getCurrentStatus(FILE_STFN_PATH);
    assertThat(status.getCurrentCapabilitiesUri(), equalTo(FILE_ON_TAPE));
    assertThat(status.getTargetCapabilitiesUri(), equalTo(null));
  }

  @Test
  public void testGetCurrentStatusOfFileOnTapeEmptyRecallTasks()
      throws IOException, BackEndException {

    ClassLoader classLoader = getClass().getClassLoader();
    StoriMetadata meta = StoriMetadata.builder().absolutePath(FILE_ABSOLUTE_PATH).status(NEARLINE)
        .type(FILE).attributes(FileAttributes.builder().migrated(true).tsmRecT("").build()).build();
    StormStorageBackend backend = new StormStorageBackend(getPluginConfiguration(classLoader),
        getStormCapabilities(classLoader));
    backend.setBackendGateway(getBackendGateway(FILE_STFN_PATH, meta));
    CdmiObjectStatus status = backend.getCurrentStatus(FILE_STFN_PATH);
    assertThat(status.getCurrentCapabilitiesUri(), equalTo(FILE_ON_TAPE));
    assertThat(status.getTargetCapabilitiesUri(), equalTo(null));
  }

  @Test
  public void testGetCurrentStatusOfFileOnTapeRecallInProgress()
      throws IOException, BackEndException {

    ClassLoader classLoader = getClass().getClassLoader();
    StoriMetadata meta =
        StoriMetadata.builder().absolutePath(FILE_ABSOLUTE_PATH).status(NEARLINE).type(FILE)
            .attributes(FileAttributes.builder().migrated(true).tsmRecT("taskId").build()).build();
    StormStorageBackend backend = new StormStorageBackend(getPluginConfiguration(classLoader),
        getStormCapabilities(classLoader));
    backend.setBackendGateway(getBackendGateway(FILE_STFN_PATH, meta));
    CdmiObjectStatus status = backend.getCurrentStatus(FILE_STFN_PATH);
    assertThat(status.getCurrentCapabilitiesUri(), equalTo(FILE_ON_TAPE));
    assertThat(status.getTargetCapabilitiesUri(), equalTo(FILE_ON_DISKANDTAPE));
  }

  @Test
  public void testGetCurrentStatusOfRootPath()
      throws IOException, ValidationException, BackEndException {

    ClassLoader classLoader = getClass().getClassLoader();
    StormStorageBackend backend = new StormStorageBackend(getPluginConfiguration(classLoader),
        getStormCapabilities(classLoader));
    CdmiObjectStatus status = backend.getCurrentStatus(ROOT_PATH);
    assertThat(status.getCurrentCapabilitiesUri(), equalTo(FOLDER_ON_DISK));
    assertThat(status.getChildren().size(), equalTo(1));
    assertThat(status.getChildren().get(0), equalTo("/test.vo"));
  }

  @Test
  public void testGetCurrentStatusOfEmptyPath()
      throws IOException, ValidationException, BackEndException {

    ClassLoader classLoader = getClass().getClassLoader();
    StormStorageBackend backend = new StormStorageBackend(getPluginConfiguration(classLoader),
        getStormCapabilities(classLoader));
    CdmiObjectStatus status = backend.getCurrentStatus("");
    assertThat(status.getCurrentCapabilitiesUri(), equalTo(FOLDER_ON_DISK));
    assertThat(status.getChildren().size(), equalTo(1));
    assertThat(status.getChildren().get(0), equalTo("/test.vo"));
  }
}
