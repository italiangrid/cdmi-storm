package it.grid.storm.cdmi.auth.impl;

import it.grid.storm.cdmi.config.VirtualOrganization;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PathUtils {

  /**
   * Find the best VirtualOrganization which fit path.
   * 
   * @param vos @List of @VirtualOrganization to parse.
   * @param path The path to check if supported.
   * @return The @VirtualOrganization within the path is.
   * @throws IOException In case of IO problems.
   */
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

  /**
   * Check if child is contained in a sub-directory of base.
   * 
   * @param base File path to check if contains child.
   * @param child File path to check.
   * @return True if child has base within its parents.
   * @throws IOException In case of IO problems.
   */
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
