package org.constretto.internal.store.ldap;

import org.constretto.ConstrettoBuilder;
import org.constretto.ConstrettoConfiguration;
import org.constretto.annotation.Configuration;
import org.constretto.model.TaggedPropertySet;
import org.junit.Rule;
import org.junit.Test;
import org.zapodot.junit.ldap.EmbeddedLdapRule;
import org.zapodot.junit.ldap.EmbeddedLdapRuleBuilder;

import javax.naming.directory.DirContext;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author zapodot at gmail dot com
 */
public class LdapConfigurationStoreEmbeddedLdapTest {

    @Rule
    public EmbeddedLdapRule embeddedLdapRule = EmbeddedLdapRuleBuilder.newInstance()
                                                                      .bindingToPort(LDAP_PORT)
                                                                      .usingDomainDsn("dc=constretto,dc=org")
                                                                      .importingLdifs("constretto.ldif")
                                                                      .build();
    public static final int LDAP_PORT = 27389;

    public static class ConfigurableType {

        @Configuration("cn")
        public List<String> names;

        @Configuration("sidekick.cn")
        public String sideKickName;
    }

    @Test
    public void testParseConfigurationUsingAddDsn() throws Exception {

        final DirContext dirContext = embeddedLdapRule.dirContext();
        final LdapConfigurationStore configurationStore = LdapConfigurationStoreBuilder.usingDirContext(dirContext)
                                                                                       .addDsn("cn=Kaare Nilsen,dc=constretto,dc=org")
                                                                                       .addDsnWithKey("sidekick",
                                                                                                      "cn=Jon-Anders Teigen,dc=constretto,dc=org")
                                                                                       .done();
        final Collection<TaggedPropertySet> propertySets = configurationStore.parseConfiguration();
        assertEquals(1, propertySets.size());
        ConstrettoConfiguration constrettoConfiguration = createConfiguration(configurationStore);
        final ConfigurableType configurationObject = constrettoConfiguration.as(ConfigurableType.class);
        assertTrue(configurationObject.names.containsAll(Arrays.asList("Kaare Nilsen", "Kåre Nilsen")));
        assertEquals("Jon-Anders Teigen", configurationObject.sideKickName);
    }

    @Test
    public void testDsnMultiValue() throws Exception {

        final DirContext initialDirContext = embeddedLdapRule.dirContext();
        final ConstrettoConfiguration configuration = new ConstrettoBuilder(false)
                .createLdapConfigurationStore(initialDirContext)
                .addDsn("cn=role_developer,ou=groups,dc=constretto,dc=org")
                .done()
                .getConfiguration();
        final List<String> members = configuration.evaluateToList(String.class, "uniqueMember");
        assertEquals(2, members.size());


    }

    @Test
    public void testParseConfigurationUsingSearch() throws Exception {

        final DirContext initialDirContext = embeddedLdapRule.dirContext();
        final ConstrettoConfiguration configuration = new ConstrettoBuilder(false)
                .createLdapConfigurationStore(initialDirContext)
                .addUsingSearch(
                        "dc=constretto,dc=org",
                        "(&(cn=K*)(objectClass=inetOrgPerson))",
                        "uid")
                .done()
                .getConfiguration();
        assertTrue(configuration.evaluateToList(String.class, "kaarenilsen.cn")
                                .containsAll(Arrays.asList("Kaare Nilsen", "Kåre Nilsen")));

    }

    private ConstrettoConfiguration createConfiguration(LdapConfigurationStore configurationStore) {
        return new ConstrettoBuilder(false).addConfigurationStore(
                configurationStore).getConfiguration();
    }

}
