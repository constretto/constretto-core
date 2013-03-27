package org.constretto.ldap;

import com.sun.jndi.ldap.DefaultResponseControlFactory;
import com.sun.jndi.ldap.LdapCtxFactory;
import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.annotations.CreateDS;
import org.apache.directory.server.core.annotations.CreatePartition;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.server.core.integ.FrameworkRunner;
import org.constretto.ConstrettoBuilder;
import org.constretto.ConstrettoConfiguration;
import org.constretto.annotation.Configuration;
import org.constretto.model.TaggedPropertySet;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.naming.Context;
import javax.naming.directory.InitialDirContext;
import javax.naming.ldap.LdapContext;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author zapodot at gmail dot com
 */
@RunWith(FrameworkRunner.class)
@CreateLdapServer(
        transports = {
                @CreateTransport(protocol = "LDAP", port = LdapConfigurationStoreEmbeddedLdapTest.LDAP_PORT)
        }
)
@CreateDS( name = "LdapConfigurationStoreEmbeddedLdapTest", partitions = @CreatePartition(name = "constretto", suffix = "dc=constretto,dc=org"))
@ApplyLdifFiles("constretto.ldif")
public class LdapConfigurationStoreEmbeddedLdapTest extends AbstractLdapTestUnit {

    public static final int LDAP_PORT = 27389;

    public static class ConfigurableType {

        @Configuration("cn")
        public List<String> names;

        @Configuration("sidekick.cn")
        public String sideKickName;
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
        assertTrue(configurationObject.names.containsAll(Arrays.asList("Kaare Nilsen", "Kåre Nilsen")));
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
        assertTrue(configuration.evaluateToList(String.class, "kaarenilsen.cn").containsAll(Arrays.asList("Kaare Nilsen", "Kåre Nilsen")));
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
        ldapEnvironment.put(Context.SECURITY_PRINCIPAL, "uid=admin,ou=system");
        ldapEnvironment.put(Context.SECURITY_CREDENTIALS, "secret");
        ldapEnvironment.put(Context.SECURITY_PROTOCOL, "simple");
        return ldapEnvironment;
    }

}
