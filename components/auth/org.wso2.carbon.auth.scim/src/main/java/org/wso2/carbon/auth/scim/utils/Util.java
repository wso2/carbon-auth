package org.wso2.carbon.auth.scim.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.auth.scim.impl.CarbonAuthSCIMUserManager;
import org.wso2.carbon.auth.scim.impl.constants.SCIMCommonConstants;
import org.wso2.carbon.auth.user.store.claim.DefaultClaimManager;
import org.wso2.carbon.auth.user.store.claim.DefaultClaimMetadataStore;
import org.wso2.carbon.auth.user.store.configuration.models.UserStoreConfiguration;
import org.wso2.carbon.auth.user.store.connector.UserStoreConnector;
import org.wso2.carbon.auth.user.store.connector.UserStoreConnectorFactory;
import org.wso2.carbon.auth.user.store.constant.UserStoreConstants;
import org.wso2.carbon.auth.user.store.exception.UserStoreConnectorException;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.ConflictException;
import org.wso2.charon3.core.exceptions.NotFoundException;
import org.wso2.charon3.core.exceptions.NotImplementedException;
import org.wso2.charon3.core.objects.Group;
import org.wso2.charon3.core.objects.User;
import org.wso2.charon3.core.protocol.endpoints.AbstractResourceManager;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMResourceSchemaManager;
import org.wso2.charon3.core.schema.SCIMResourceTypeSchema;
import org.wso2.charon3.core.utils.CopyUtil;
import org.wso2.charon3.core.utils.ResourceManagerUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * SCIIM related util functions
 */
public class Util {
    private static final Logger log = LoggerFactory.getLogger(Util.class);
    private static String scimURL = "https://localhost:9443/scim2";
    private static int count = 0;

    public static String getSCIMUserURL() {
        String scimUserLocation = scimURL + SCIMCommonConstants.USERS;
        return scimUserLocation;
    }

    public static String getSCIMGroupURL() {
        String scimGroupLocation = scimURL + SCIMCommonConstants.GROUPS;
        return scimGroupLocation;
    }

    public static String getSCIMServiceProviderConfigURL() {
        String scimServiceProviderConfig = scimURL + SCIMCommonConstants.SERVICE_PROVIDER_CONFIG;
        return scimServiceProviderConfig;
    }

    public static String getSCIMResourceTypeURL() {
        String scimResourceType = scimURL + SCIMCommonConstants.RESOURCE_TYPE;
        return scimResourceType;
    }

    public static Map<String, String> getEndpointURLs() {
        Map<String, String> endpointURLs = new HashMap<>();
        endpointURLs.put(SCIMConstants.USER_ENDPOINT, getSCIMUserURL());
        endpointURLs.put(SCIMConstants.GROUP_ENDPOINT, getSCIMGroupURL());
        endpointURLs.put(SCIMConstants.SERVICE_PROVIDER_CONFIG_ENDPOINT, getSCIMServiceProviderConfigURL());
        endpointURLs.put(SCIMConstants.RESOURCE_TYPE_ENDPOINT, getSCIMResourceTypeURL());
        return endpointURLs;
    }

    public static void addDefaultAdminUserAndRole(UserStoreConfiguration userStoreConfiguration) {
        if ((count++) != 0) {
            return;
        }
        try {
            UserStoreConnector userStoreConnector = UserStoreConnectorFactory.getUserStoreConnector();
            userStoreConnector.init(userStoreConfiguration);
            DefaultClaimManager defaultClaimManager = DefaultClaimManager.getInstance();
            DefaultClaimMetadataStore defaultClaimMetadataStore = new DefaultClaimMetadataStore(defaultClaimManager);
            CarbonAuthSCIMUserManager userManager =
                    new CarbonAuthSCIMUserManager(userStoreConnector, defaultClaimMetadataStore);
            AbstractResourceManager.setEndpointURLMap(Util.getEndpointURLs());
            Map<String, String> attributes = new HashMap<>();

            // adding admin user
            SCIMResourceTypeSchema userSchema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
            Map<String, Boolean> requiredAttributes = ResourceManagerUtil
                    .getOnlyRequiredAttributesURIs((SCIMResourceTypeSchema) CopyUtil.deepCopy(userSchema), null, null);

            char[] password = userStoreConfiguration.getSuperUserPass().toCharArray();
            String username = userStoreConfiguration.getSuperUser();
            attributes.put(UserStoreConstants.CLAIM_USERNAME, username);
            attributes.put(UserStoreConstants.CLAIM_PASSWORD, new String(password));
            User user =
                    (User) SCIMClaimResolver.constructSCIMObjectFromAttributes(attributes, SCIMCommonConstants.USER);
            user.setId(UUID.randomUUID().toString());
            User addedUser = userManager.createUser(user, requiredAttributes);
            if (addedUser != null) {
                log.debug("Admin user added.");
            } else {
                // need to avoid creating group
                return;
            }

            // adding admin role
            SCIMResourceTypeSchema groupSchema = SCIMResourceSchemaManager.getInstance().getGroupResourceSchema();
            Map<String, Boolean> requiredGroupAttributes = ResourceManagerUtil
                    .getOnlyRequiredAttributesURIs((SCIMResourceTypeSchema) CopyUtil.deepCopy(groupSchema), null, null);

            attributes.clear();
            String groupName = userStoreConfiguration.getSuperUserGroup();
            attributes.put(UserStoreConstants.GROUP_DISPLAY_NAME, groupName);
            Group group =
                    (Group) SCIMClaimResolver.constructSCIMObjectFromAttributes(attributes, SCIMCommonConstants.GROUP);
            group.setMember(addedUser.getId(), username);
            group.setId(UUID.randomUUID().toString());
            Group newGroup = userManager.createGroup(group, requiredGroupAttributes);
            if (newGroup != null) {
                log.debug("Admin Role added.");
            }
        } catch (UserStoreConnectorException | BadRequestException | CharonException
                | NotImplementedException | NotFoundException e) {
            String msg = "Error occurred while adding default Admin user or Role";
            log.error(msg, e);
        } catch (ConflictException e) {
            // todo: check user before insert
            String msg = "Error occurred while adding default Admin user or Role. Role or User already exist.";
            log.warn(msg);
        }
    }


}
