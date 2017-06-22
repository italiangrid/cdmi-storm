package it.grid.storm.cdmi.auth.impl;

import static it.grid.storm.cdmi.auth.impl.PathUtils.getVirtualOrganizationFromPath;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import com.google.common.collect.Lists;

import it.grid.storm.cdmi.config.VirtualFileSystem;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

public class PathUtilsTest {

  private List<VirtualFileSystem> vos;

  private final String validPath = "/test/filename.dat";
  private final String validSubPath = "/test/sub/filename.dat";
  private final String invalidPath = "/othervo/path/to/filename.dat";
  private final String emptyPath = "";

  @Before
  public void initOrganizations() throws IOException {

    vos = Lists.newArrayList();
    vos.add(new VirtualFileSystem("test", "/test", "test:read", "test:recall", "test"));
    vos.add(new VirtualFileSystem("subtest", "/test/sub", "subtest:read", "subtest:recall",
        "subtest"));
  }

  @Test
  public void testMatchSuccess() throws IOException {

    Optional<VirtualFileSystem> vo = getVirtualOrganizationFromPath(vos, validSubPath);
    assertThat(vo.isPresent(), equalTo(true));
    assertThat(vo.get().getVoName(), equalTo("subtest"));
  }

  @Test
  public void testNotMatchingVirtualOrganization() throws IOException {

    Optional<VirtualFileSystem> vo = getVirtualOrganizationFromPath(vos, invalidPath);
    assertThat(vo.isPresent(), equalTo(false));
  }

  @Test
  public void testNotMatchingEmptyPath() throws IOException {

    Optional<VirtualFileSystem> vo = getVirtualOrganizationFromPath(vos, emptyPath);
    assertThat(vo.isPresent(), equalTo(false));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullListOfVirtualOrganizations() throws IOException {

    getVirtualOrganizationFromPath(null, validPath);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullPath() throws IOException {

    getVirtualOrganizationFromPath(vos, null);
  }

  @Test
  public void uselessTest() throws IOException {

    assertNotNull(new PathUtils());
  }
}
