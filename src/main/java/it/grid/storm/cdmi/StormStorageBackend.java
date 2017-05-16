package it.grid.storm.cdmi;

import static it.grid.storm.cdmi.Utils.buildBackendCapabilities;
import static org.indigo.cdmi.BackendCapability.CapabilityType.CONTAINER;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import it.grid.storm.cdmi.auth.AuthorizationException;
import it.grid.storm.cdmi.auth.AuthorizationManager;
import it.grid.storm.cdmi.auth.User;
import it.grid.storm.cdmi.auth.UserProvider;
import it.grid.storm.cdmi.auth.UserProviderException;
import it.grid.storm.cdmi.auth.impl.DefaultAuthorizationManager;
import it.grid.storm.cdmi.auth.impl.PathUtils;
import it.grid.storm.cdmi.auth.impl.SecurityContextUserProvider;
import it.grid.storm.cdmi.capability.CapabilityManager;
import it.grid.storm.cdmi.capability.impl.DefaultCapabilityManager;
import it.grid.storm.cdmi.config.PluginConfiguration;
import it.grid.storm.cdmi.config.StormBackendCapability;
import it.grid.storm.cdmi.config.VirtualOrganization;
import it.grid.storm.gateway.BackendGateway;
import it.grid.storm.gateway.BackendGatewayException;
import it.grid.storm.gateway.impl.StormBackendGateway;
import it.grid.storm.rest.metadata.model.StoriMetadata;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.indigo.cdmi.BackEndException;
import org.indigo.cdmi.BackendCapability;
import org.indigo.cdmi.CdmiObjectStatus;
import org.indigo.cdmi.PermissionDeniedBackEndException;
import org.indigo.cdmi.spi.StorageBackend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StormStorageBackend implements StorageBackend {

  private static final Logger log = LoggerFactory.getLogger(StormStorageBackend.class);

  private List<BackendCapability> backendCapabilities;
  private BackendGateway backendGateway;
  private AuthorizationManager authManager;
  private UserProvider userProvider;
  private List<VirtualOrganization> vos;

  private CapabilityManager<StoriMetadata> capManager;
  private Map<String, Object> exportAttributes;

  /**
   * Constructor.
   * 
   */
  public StormStorageBackend(PluginConfiguration config, List<StormBackendCapability> capabilities,
      Map<String, Object> exports) {

    Preconditions.checkNotNull(config, "Invalid null plugin configuration");
    Preconditions.checkNotNull(capabilities, "Invalid null capabilities configuration");
    backendCapabilities = buildBackendCapabilities(capabilities);
    backendGateway = new StormBackendGateway(config.getBackend().getHostname(),
        config.getBackend().getPort(), config.getBackend().getToken());
    vos = config.getVos();
    authManager = new DefaultAuthorizationManager();
    userProvider = new SecurityContextUserProvider();
    capManager = new DefaultCapabilityManager(backendCapabilities);
    exportAttributes = exports;
  }

  /**
   * Set the @BackendGateway to use to contact StoRM Back-end.
   * 
   * @param backendGateway A valid @BackendGateway implementation.
   */
  public void setBackendGateway(BackendGateway backendGateway) {

    Preconditions.checkNotNull(backendGateway, "Invalid null backend gateway");
    this.backendGateway = backendGateway;
  }

  /**
   * Setter.
   * 
   * @param authManager The @AuthorizationManager implementation to set.
   */
  public void setAuthorizationManager(AuthorizationManager authManager) {

    Preconditions.checkNotNull(authManager, "Invalid null authorization manager");
    this.authManager = authManager;
  }

  /**
   * Setter.
   * 
   * @param userProvider The @UserProvider implementation to set.
   */
  public void setUserProvider(UserProvider userProvider) {

    Preconditions.checkNotNull(userProvider, "Invalid null user provider");
    this.userProvider = userProvider;
  }

  @Override
  public List<BackendCapability> getCapabilities() throws BackEndException {

    return backendCapabilities;
  }

  @Override
  public CdmiObjectStatus getCurrentStatus(String path) throws BackEndException {

    CdmiObjectStatus status;
    User user = getUser();
    Optional<VirtualOrganization> vo = getVirtualOrganizationFromPath(path);

    if (vo.isPresent()) {

      checkReadPermissions(user, vo.get());

      StoriMetadata meta = backendGateway.getStoriMetadata(user, path);
      log.debug("StoRIMetadata: {}", meta);

      BackendCapability cap = capManager.getBackendCapability(meta);
      log.debug("BackendCapability: {}", cap);

      String currentCapabilitiesUri = capManager.getCapabilityUri(cap.getType(), cap.getName());
      Optional<String> targetCapabilitiesUri = capManager.getTargetCapabilityUri(cap, meta);

      Map<String, Object> metadata = capManager.getMetadataProvided(cap);
      if (targetCapabilitiesUri.isPresent()) {
        metadata.put("cdmi_recommended_polling_interval", 50000);
      }

      status = new CdmiObjectStatus(metadata, currentCapabilitiesUri,
          targetCapabilitiesUri.orElse(null));

      status.setChildren(meta.getChildren());
      status.setExportAttributes(exportAttributes);

    } else if (isRootPath(path)) {

      log.debug("Root path requested ...");
      status = getRootCdmiObjectStatus();

    } else {

      log.debug("Bad path required: {}", path);
      throw new BackEndException("Bad path requested");
    }

    return status;
  }

  private Optional<VirtualOrganization> getVirtualOrganizationFromPath(String path)
      throws BackEndException {

    try {

      return PathUtils.getVirtualOrganizationFromPath(vos, path);

    } catch (IOException e) {

      log.error(e.getMessage());
      throw new BackEndException(e);
    }
  }

  private boolean isRootPath(String path) {

    return path.isEmpty() || path.equals("/");
  }

  private CdmiObjectStatus getRootCdmiObjectStatus() {

    BackendCapability cap = capManager.getBackendCapability(CONTAINER, "DiskOnly");
    String currentCapabilitiesUri = capManager.getCapabilityUri(CONTAINER, "DiskOnly");

    CdmiObjectStatus currentStatus =
        new CdmiObjectStatus(cap.getMetadata(), currentCapabilitiesUri, null);
    List<String> children = Lists.newArrayList();
    for (VirtualOrganization vo : vos) {
      children.add(vo.getPath());
    }
    currentStatus.setChildren(children);
    currentStatus.setExportAttributes(exportAttributes);
    return currentStatus;
  }

  @Override
  public void updateCdmiObject(String path, String targetCapabilitiesUri) throws BackEndException {

    User user = getUser();

    log.info("Transition to {} requested by {} for {}", targetCapabilitiesUri, user.getUserId(),
        path);

    Optional<VirtualOrganization> vo = getVirtualOrganizationFromPath(path);

    if (vo.isPresent()) {

      checkRecallPermissions(user, vo.get());

      StoriMetadata meta = backendGateway.getStoriMetadata(user, path);
      log.debug("StoRIMetadata: {}", meta);

      BackendCapability cap = capManager.getBackendCapability(meta);
      log.debug("BackendCapability: {}", cap);

      if (cap.getType().equals(CONTAINER)) {

        log.debug("{} is a container and cannot change QoS", path);
        throw new BackEndException("Containers QoS cannot change");
      }

      if (!capManager.isAllowedToMove(cap, targetCapabilitiesUri)) {

        log.debug("{} cannot change its QoS from {} to {}", path, cap.getName(),
            targetCapabilitiesUri);
        throw new BackEndException("QoS change not allowed");
      }

      Optional<String> currentTargetCapabilitiesUri = capManager.getTargetCapabilityUri(cap, meta);

      if (currentTargetCapabilitiesUri.isPresent()) {

        if (currentTargetCapabilitiesUri.get().equals(targetCapabilitiesUri)) {

          log.info("{} is already in transition to {}", path, targetCapabilitiesUri);
          return;
        }
        throw new BackEndException("Already in transition to another capability");
      }

      try {

        backendGateway.addRecallTask(user, path);

      } catch (BackendGatewayException e) {

        throw new BackEndException(e.getMessage(), e);
      }

      log.info("Transition to {} started successful for {}", targetCapabilitiesUri, path);

    } else {

      log.debug("Bad path required: {}", path);
      throw new BackEndException("Bad path requested");
    }
  }

  private User getUser() throws BackEndException {

    User user = null;
    log.debug("Retrieving user ...");

    try {

      user = userProvider.getUser();

    } catch (UserProviderException e) {

      log.warn("Cannot retrieve user");
      throw new BackEndException(e);

    }

    log.debug("Retrieved user {}", user);
    return user;
  }

  private void checkReadPermissions(User user, VirtualOrganization vo) throws BackEndException {

    log.debug("Check read permissions for user {} into vo {}", user.getUserId(), vo.getName());

    try {

      authManager.canRead(user, vo);

    } catch (AuthorizationException e) {

      log.warn("User {} is not authorized to read into vo {}", user.getUserId(), vo.getName());
      throw new PermissionDeniedBackEndException(e);

    } catch (IOException e) {

      log.error(e.getMessage());
      throw new BackEndException(e);

    }

    log.debug("User {} is authorized to read into {}", user.getUserId(), vo.getName());
  }

  private void checkRecallPermissions(User user, VirtualOrganization vo) throws BackEndException {

    log.debug("Check recall permissions for user {} into vo {}", user.getUserId(), vo.getName());

    try {

      authManager.canRecall(user, vo);

    } catch (AuthorizationException e) {

      log.warn("User {} is not authorized to recall into vo {}", user.getUserId(), vo.getName());
      throw new PermissionDeniedBackEndException(e);

    } catch (IOException e) {

      log.error(e.getMessage());
      throw new BackEndException(e);
    }

    log.debug("User {} is authorized to recall files into {}", user.getUserId(), vo.getName());
  }

}
