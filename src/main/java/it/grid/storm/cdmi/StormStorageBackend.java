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
import it.grid.storm.cdmi.auth.impl.SecurityContextUserProvider;
import it.grid.storm.cdmi.capability.CapabilityManager;
import it.grid.storm.cdmi.capability.impl.DefaultCapabilityManager;
import it.grid.storm.cdmi.config.PluginConfiguration;
import it.grid.storm.cdmi.config.StormBackendCapability;
import it.grid.storm.cdmi.config.VirtualOrganization;
import it.grid.storm.gateway.StormBackendGateway;
import it.grid.storm.gateway.model.BackendGateway;
import it.grid.storm.gateway.model.BackendGatewayException;
import it.grid.storm.rest.metadata.model.StoriMetadata;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
  private List<VirtualOrganization> organizations;

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
    organizations = config.getVos();
    authManager = new DefaultAuthorizationManager(organizations);
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

    if (isRootPath(path)) {

      log.debug("Root path requested ...");
      return getRootCdmiObjectStatus();
    }

    User user = retrieveUserIfCanRead(path);

    StoriMetadata meta = backendGateway.getStoriMetadata(user, path);
    log.debug("StoRIMetadata: {}", meta);

    BackendCapability cap = capManager.getBackendCapability(meta);
    log.debug("BackendCapability: {}", cap);

    String currentCapabilitiesUri = capManager.getCapabilityUri(cap.getType(), cap.getName());
    String targetCapabilitiesUri = capManager.getTargetCapabilityUri(cap, meta);

    CdmiObjectStatus currentStatus =
        new CdmiObjectStatus(cap.getMetadata(), currentCapabilitiesUri, targetCapabilitiesUri);

    currentStatus.setChildren(meta.getChildren());
    currentStatus.setExportAttributes(exportAttributes);

    return currentStatus;
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
    for (VirtualOrganization vo : organizations) {
      children.add(vo.getPath());
    }
    currentStatus.setChildren(children);
    currentStatus.setExportAttributes(exportAttributes);
    return currentStatus;
  }

  @Override
  public void updateCdmiObject(String path, String targetCapabilitiesUri) throws BackEndException {

    log.info("Updating {} to QoS {} ... ", path, targetCapabilitiesUri);

    User user = retrieveUserIfCanRecall(path);

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

    String currentTargetCapabilitiesUri = capManager.getTargetCapabilityUri(cap, meta);

    if (currentTargetCapabilitiesUri != null) {

      if (currentTargetCapabilitiesUri.equals(targetCapabilitiesUri)) {

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
  }

  private User retrieveUserIfCanRead(String path) throws BackEndException {

    User user = null;
    try {
      log.debug("Retrieving user ...");
      user = userProvider.getUser();
      log.debug("Retrieved user {}", user);
    } catch (UserProviderException e) {
      log.warn("Cannot retrieve user");
      throw new BackEndException(e);
    }
    try {
      log.debug("Check read permissions for user {} on path {}", user.getUserId(), path);
      authManager.canRead(user, path);
      log.debug("User {} is authorized to read path {}", user.getUserId(), path);
    } catch (AuthorizationException e) {
      log.warn("User {} is not authorized to read path {}", user, path);
      throw new PermissionDeniedBackEndException(e);
    } catch (IOException e) {
      log.error(e.getMessage());
      throw new BackEndException(e);
    }
    return user;
  }

  private User retrieveUserIfCanRecall(String path) throws BackEndException {

    User user = null;
    try {
      log.debug("Retrieving user ...");
      user = userProvider.getUser();
      log.debug("Retrieved user {}", user);
    } catch (UserProviderException e) {
      log.warn("Cannot retrieve user");
      throw new BackEndException(e);
    }
    try {
      log.debug("Check read permissions for user {} on path {}", user.getUserId(), path);
      authManager.canRead(user, path);
      log.debug("User {} is authorized to read path {}", user.getUserId(), path);
    } catch (AuthorizationException e) {
      log.warn("User {} is not authorized to read path {}", user, path);
      throw new PermissionDeniedBackEndException(e);
    } catch (IOException e) {
      log.error(e.getMessage());
      throw new BackEndException(e);
    }
    return user;
  }
}
