/**
 * This file Copyright (c) 2016 Magnolia International
 * Ltd.  (http://www.magnolia-cms.com). All rights reserved.
 *
 *
 * This program and the accompanying materials are made
 * available under the terms of the Magnolia Network Agreement
 * which accompanies this distribution, and is available at
 * http://www.magnolia-cms.com/mna.html
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package info.magnolia.commercetools.integration.setup;

import static org.junit.Assert.*;

import info.magnolia.cms.security.MgnlRoleManager;
import info.magnolia.cms.security.Realm;
import info.magnolia.cms.security.SecuritySupport;
import info.magnolia.cms.security.SecuritySupportImpl;
import info.magnolia.cms.security.SystemUserManager;
import info.magnolia.cms.security.User;
import info.magnolia.cms.security.UserManager;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.module.ModuleVersionHandler;
import info.magnolia.module.ModuleVersionHandlerTestCase;
import info.magnolia.module.model.Version;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.test.ComponentsTestUtil;

import java.util.Arrays;
import java.util.List;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the {@link CommercetoolsIntegrationModuleVersionHandler}.
 */
public class CommercetoolsIntegrationModuleVersionHandlerTest extends ModuleVersionHandlerTestCase {

    private User anonymous;
    private User systemuser;
    private MgnlRoleManager roleManager;
    private SystemUserManager userManager;
    private Session users;

    @Override
    protected String getModuleDescriptorPath() {
        return "/META-INF/magnolia/magnolia-commercetools-integration.xml";
    }

    @Override
    protected ModuleVersionHandler newModuleVersionHandlerForTests() {
        return new CommercetoolsIntegrationModuleVersionHandler();
    }

    @Override
    protected List<String> getModuleDescriptorPathsForTests() {
        return Arrays.asList("/META-INF/magnolia/core.xml");
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        setupConfigNode("/server/filters/cms/modelExecution");
        setupConfigNode("/modules/commercetools-integration/apps/ctBrowser/subApps/browser/contentConnector");
        setupConfigNode("/modules/commercetools-integration/apps/ctBrowser/subApps/browser/workbench/contentViews/tree");
        setupConfigNode("/modules/commercetools-integration/apps/ctBrowser/subApps/browser/imageProvider");
        setupConfigNode("/modules/commercetools-integration/apps/ctBrowser/subApps/detail/editor/form/tabs/product/fields/variants/fields/images");
        setupConfigNode("/modules/commercetools-integration/apps/ctSetup/subApps/main");
        setupConfigNode("/modules/commercetools-integration/fieldTypes/commerceToolsImageField");
        setupConfigNode("/modules/commercetools-integration/rest-endpoints/ctCart");
        setupConfigNode("/modules/commercetools-integration/rest-endpoints/ctVariant");
        setupConfigNode("/modules/rendering/renderers/freemarker/contextAttributes/ctfn");
        setupConfigNode("/server/filters/cms/ctSignupLoginLogout");

        users = MgnlContext.getJCRSession(RepositoryConstants.USERS);
        NodeUtil.createPath(users.getRootNode(), "/system/superuser", NodeTypes.User.NAME);
        NodeUtil.createPath(users.getRootNode(), "/system/anonymous", NodeTypes.User.NAME);
        users.save();

        final SecuritySupportImpl securitySupport = new SecuritySupportImpl();
        userManager = new SystemUserManager();
        userManager.setRealmName(Realm.REALM_SYSTEM.getName());
        securitySupport.addUserManager(Realm.REALM_SYSTEM.getName(), userManager);
        systemuser = userManager.getUser(UserManager.SYSTEM_USER);
        anonymous = userManager.getUser(UserManager.ANONYMOUS_USER);
        roleManager = new MgnlRoleManager();
        securitySupport.setRoleManager(roleManager);
        ComponentsTestUtil.setInstance(SecuritySupport.class, securitySupport);
    }

    @Test
    public void updateTo11() throws Exception {
        // GIVEN
        assertFalse(anonymous.hasRole("commercetools-rest"));
        assertFalse(systemuser.hasRole("commercetools-rest"));

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("1.0"));

        // THEN
        assertTrue(anonymous.hasRole("commercetools-rest"));
        assertTrue(systemuser.hasRole("commercetools-rest"));
    }
}