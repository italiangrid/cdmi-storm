package it.grid.storm.cdmi.auth.impl;

import com.google.common.base.Preconditions;

import it.grid.storm.cdmi.config.VirtualOrganization;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class PathUtils {

  /**
   * Find the best VirtualOrganization which fit path.
   * 
   * @param vos @List of @VirtualOrganization to parse.
   * @param path The path to check if supported.
   * @return The @VirtualOrganization within the path is.
   * @throws IOException In case of IO problems.
   */
  public static Optional<VirtualOrganization> getVirtualOrganizationFromPath(
      List<VirtualOrganization> vos, String path) throws IOException {

    Preconditions.checkArgument(vos != null, "Null list of VOs");
    Preconditions.checkArgument(path != null, "Null path");

    if (path.isEmpty()) {
      return Optional.empty();
    }

    Optional<VirtualOrganization> vo = Optional.empty();

    File targetBase = null;
    File child = new File(path);

    for (VirtualOrganization currentVo : vos) {

      File base = new File(currentVo.getPath());
      if (isSubDirectory(base, child)) {
        if (targetBase == null
            || (targetBase.getCanonicalPath().length() < base.getCanonicalPath().length())) {
          targetBase = base;
          vo = Optional.of(currentVo);
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
