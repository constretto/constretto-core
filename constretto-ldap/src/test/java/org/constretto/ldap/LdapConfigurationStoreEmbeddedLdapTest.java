package org.constretto.ldap;

import com.sun.jndi.ldap.DefaultResponseControlFactory;
import com.sun.jndi.ldap.LdapCtxFactory;
import org.constretto.ConstrettoBuilder;
import org.constretto.ConstrettoConfiguration;
import org.constretto.annotation.Configuration;
import org.constretto.model.TaggedPropertySet;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.ldap.test.LdapTestUtils;
import org.springframework.ldap.test.TestContextSourceFactoryBean;

import javax.naming.Context;
import javax.naming.directory.InitialDirContext;
import javax.naming.ldap.LdapContext;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author sondre
 */

public class LdapConfigurationStoreEmbeddedLdapTest {

    public static class ConfigurableType {

        @Configuration("cn")
        public String name;

        @Configuration("sidekick.cn")
        public String sideKickName;

    }

    public static final int LDAP_PORT = 27389;

    @Before
    public void setUp() throws Exception {
        TestContextSourceFactoryBean testContextSourceFactoryBean = new TestContextSourceFactoryBean();
        testContextSourceFactoryBean.setLdifFile(new DefaultResourceLoader().getResource("classpath:constretto.ldif"));
        testContextSourceFactoryBean.setDefaultPartitionSuffix("dc=constretto,dc=org");
        testContextSourceFactoryBean.setDefaultPartitionName("constretto");
        testContextSourceFactoryBean.setSingleton(true);
        testContextSourceFactoryBean.setPrincipal(LdapTestUtils.DEFAULT_PRINCIPAL);
        testContextSourceFactoryBean.setPassword(LdapTestUtils.DEFAULT_PASSWORD);
        testContextSourceFactoryBean.setPort(LDAP_PORT);
        testContextSourceFactoryBean.afterPropertiesSet();

    }

    @Test
    public void testParseConfigurationUsingAddDsn() throws Exception {

        Hashtable<String, String> ldapEnvironment = createLdapEnvironment();

        final InitialDirContext dirContext = new InitialDirContext(ldapEnvironment);
        final LdapConfigurationStore configurationStore = LdapConfigurationStoreBuilder.usingDirContext(dirContext)
                .addDsn("cn=Kaare Nilsen,dc=constretto,dc=org")
                .addDsnWithKey("sidekick", "cn=Jon-Anders Teigen,dc=constretto,dc=org")
                .done();
        final Collection<TaggedPropertySet> propertySets = configurationStore.parseConfiguration();
        assertEquals(1, propertySets.size());
        dirContext.close();
        ConstrettoConfiguration constrettoConfiguration = createConfiguration(configurationStore);
        final ConfigurableType configurationObject = constrettoConfiguration.as(ConfigurableType.class);
        assertEquals("Kaare Nilsen", configurationObject.name);
        assertEquals("Jon-Anders Teigen", configurationObject.sideKickName);
    }

    @Test
    public void testDsnMultiValue() throws Exception {

        final InitialDirContext initialDirContext = new InitialDirContext(createLdapEnvironment());
        final LdapConfigurationStore ldapConfigurationStore = LdapConfigurationStoreBuilder.usingDirContext(initialDirContext)
                .addDsn("cn=role_developer,ou=groups,dc=constretto,dc=org")
                .done();
        final ConstrettoConfiguration configuration = createConfiguration(ldapConfigurationStore);
        final List<String> members = configuration.evaluateToList(String.class, "uniquemember");
        assertEquals(2, members.size());


    }

    @Test
    public void testParseConfigurationUsingSearch() throws Exception {
        final InitialDirContext initialDirContext = new InitialDirContext(createLdapEnvironment());
        final LdapConfigurationStore ldapConfigurationStore = LdapConfigurationStoreBuilder.usingDirContext(
                initialDirContext)
                .addUsingSearch(
                        "dc=constretto,dc=org",
                        "(&(cn=K*)(objectClass=inetOrgPerson))",
                        "uid")
                .done();
        initialDirContext.close();
        final ConstrettoConfiguration configuration = new ConstrettoBuilder(false)
                .addConfigurationStore(ldapConfigurationStore)
                .getConfiguration();
        assertEquals("Kaare Nilsen", configuration.evaluateToString("kaarenilsen.cn"));

    }

    private ConstrettoConfiguration createConfiguration(LdapConfigurationStore configurationStore) {
        return new ConstrettoBuilder(false).addConfigurationStore(
                configurationStore).getConfiguration();
    }

    private Hashtable<String, String> createLdapEnvironment() {
        Hashtable<String, String> ldapEnvironment = new Hashtable<String, String>();
        ldapEnvironment.put(LdapContext.CONTROL_FACTORIES, DefaultResponseControlFactory.class.getName());
        ldapEnvironment.put(Context.PROVIDER_URL, String.format("ldap://localhost:%1$s", LDAP_PORT));
        ldapEnvironment.put(Context.INITIAL_CONTEXT_FACTORY, LdapCtxFactory.class.getName());
        ldapEnvironment.put(Context.SECURITY_PRINCIPAL, LdapTestUtils.DEFAULT_PRINCIPAL);
        ldapEnvironment.put(Context.SECURITY_CREDENTIALS, LdapTestUtils.DEFAULT_PASSWORD);
        ldapEnvironment.put(Context.SECURITY_PROTOCOL, "simple");
        return ldapEnvironment;
    }

}
