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

  private final String absolutePath = "/storage/test.vo/test.txt";
  private final String vfsName = "TESTVO-FS";
  private final String vfsRoot = "/storage/test.vo";
  private final String lastModified = "2017-03-09 16:40 PM UTC";
  private final boolean migrated = false;
  private final boolean premigrated = true;
  private final boolean pinned = true;
  private final String checksum = "Adler32:912389012";
  private final long tsmRecD = 11000000;
  private final String tsmRecT = "TaskId1";
  private final int tsmRecR = 0;

  @Test
  public void testReadStoriMetadataFromFile() throws IOException, ParseException {

    String filePath = getClass().getClassLoader().getResource("FileMetadata.json").getFile();
    StoriMetadata metadata = mapper.readValue(new File(filePath), StoriMetadata.class);
    assertThat(metadata.getAbsolutePath(), equalTo(absolutePath));
    assertThat(metadata.getType(), equalTo(FILE));
    assertThat(metadata.getStatus(), equalTo(ONLINE));
    assertThat(metadata.getFilesystem().getName(), equalTo(vfsName));
    assertThat(metadata.getFilesystem().getRoot(), equalTo(vfsRoot));
    assertThat(metadata.getLastModified(), equalTo(dateFormat.parse(lastModified)));

  }

  @Test
  public void testStoriMetadataBuilder() throws ParseException, JsonProcessingException {

    StoriMetadata meta = StoriMetadata.builder().absolutePath(absolutePath)
        .lastModified(dateFormat.parse(lastModified))
        .filesystem(VirtualFsMetadata.builder().name(vfsName).root(vfsRoot).build())
        .attributes(
            FileAttributes.builder().pinned(pinned).migrated(migrated).premigrated(premigrated)
                .checksum(checksum).tsmRecD(tsmRecD).tsmRecT(tsmRecT).tsmRecR(tsmRecR).build())
        .build();
    mapper.writeValueAsString(meta);
  }
}
