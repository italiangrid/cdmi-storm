package it.grid.storm.cdmi.auth.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import it.grid.storm.cdmi.config.VirtualOrganization;

public class PathUtils {

  public static VirtualOrganization getVirtualOrganizationFromPath(List<VirtualOrganization> vos,
      String path) throws IOException {

    VirtualOrganization vo = null;
    File targetBase = null;
    File child = new File(path);

    for (VirtualOrganization currentVo : vos) {

      File base = new File(currentVo.getPath());
      if (isSubDirectory(base, child)) {
        if (targetBase == null
            || (targetBase.getCanonicalPath().length() < base.getCanonicalPath().length())) {
          targetBase = base;
          vo = currentVo;
        }
      }
    }

    return vo;
  }

  public static boolean isSubDirectory(File base, File child) throws IOException {

    base = base.getCanonicalFile();
    child = child.getCanonicalFile();

    File parentFile = child;
    while (parentFile != null) {
      if (base.equals(parentFile)) {
        return true;
      }
      parentFile = parentFile.getParentFile();
    }
    return false;
  }
}
