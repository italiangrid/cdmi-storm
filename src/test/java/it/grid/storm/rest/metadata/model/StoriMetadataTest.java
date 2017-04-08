package it.grid.storm.rest.metadata.model;

import static it.grid.storm.rest.metadata.model.StoriMetadata.ResourceStatus.ONLINE;
import static it.grid.storm.rest.metadata.model.StoriMetadata.ResourceType.FILE;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Test;

public class StoriMetadataTest {

  private ObjectMapper mapper = new ObjectMapper();
  private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm a z");

  private final String ABSOLUTE_PATH = "/storage/test.vo/test.txt";
  private final String VFS_NAME = "TESTVO-FS";
  private final String VFS_ROOT = "/storage/test.vo";
  private final String LAST_MODIFIED = "2017-03-09 16:40 PM UTC";
  private final boolean MIGRATED = false;
  private final boolean PREMIGRATED = true;
  private final boolean PINNED = true;
  private final String CHECKSUM = "Adler32:912389012";
  private final long TSMRECD = 11000000;
  private final String TSMRECT = "TaskId1";
  private final int TSMRECR = 0;

  @Test
  public void testReadStoriMetadataFromFile() throws IOException, ParseException {

    String filePath = getClass().getClassLoader().getResource("FileMetadata.json").getFile();
    StoriMetadata metadata = mapper.readValue(new File(filePath), StoriMetadata.class);
    assertThat(metadata.getAbsolutePath(), equalTo(ABSOLUTE_PATH));
    assertThat(metadata.getType(), equalTo(FILE));
    assertThat(metadata.getStatus(), equalTo(ONLINE));
    assertThat(metadata.getFilesystem().getName(), equalTo(VFS_NAME));
    assertThat(metadata.getFilesystem().getRoot(), equalTo(VFS_ROOT));
    assertThat(metadata.getLastModified(), equalTo(dateFormat.parse(LAST_MODIFIED)));

  }

  @Test
  public void testStoriMetadataBuilder() throws ParseException, JsonProcessingException {

    StoriMetadata meta = StoriMetadata.builder().absolutePath(ABSOLUTE_PATH)
        .lastModified(dateFormat.parse(LAST_MODIFIED))
        .filesystem(VirtualFsMetadata.builder().name(VFS_NAME).root(VFS_ROOT).build())
        .attributes(
            FileAttributes.builder().pinned(PINNED).migrated(MIGRATED).premigrated(PREMIGRATED)
                .checksum(CHECKSUM).tsmRecD(TSMRECD).tsmRecT(TSMRECT).tsmRecR(TSMRECR).build())
        .build();
    mapper.writeValueAsString(meta);
  }
}
