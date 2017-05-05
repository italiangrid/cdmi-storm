package it.grid.storm.cdmi.auth.impl;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import com.google.common.collect.Lists;

import it.grid.storm.cdmi.config.VirtualOrganization;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class PathUtilsTest {

  private List<VirtualOrganization> vos;

  @Before
  public void initOrganizations() throws IOException {

    vos = Lists.newArrayList();
    vos.add(new VirtualOrganization("test", "/test", "test:read", "test:recall", "test"));
    vos.add(new VirtualOrganization("subtest", "/test/sub", "subtest:read", "subtest:recall",
        "subtest"));
  }

  @Test
  public void testMatchSuccess() throws IOException {

    VirtualOrganization vo =
        PathUtils.getVirtualOrganizationFromPath(vos, "/test/sub/filename.dat");
    assertNotNull(vo);
    assertThat(vo.getName(), equalTo("subtest"));
  }

  @Test
  public void uselessTest() throws IOException {

    assertNotNull(new PathUtils());
  }
}
