constretto-ldap
===============

Constretto - support for LDAP

To use the LdapConfigurationStore:

    import javax.naming.directory.InitialDirContext;
    import org.constretto.ConstrettoBuilder;
    import org.constretto.ConstrettoConfiguration;
    ...
    
    final InitialDirContext dirContext = ...
    final LdapConfigurationStore configurationStore = LdapConfigurationStoreBuilder
								.usingDirContext(dirContext)
								.addDsn("cn=Kaare Nilsen,dc=constretto,dc=org") // maps all attributes without prefix
                                .addDsn("cn=Jon-Anders Teigen,dc=constretto,dc=org", "teigenRules") // maps all attributes without prefix for tag "teigenRules"
								.addDsnWithKey("sidekick", "cn=Jon-Anders Teigen,dc=constretto,dc=org") // maps LDAP attributes with prefix "sidekick"
								.addDsnWithKey("sidekick", "cn=Kaare Nilsen,dc=constretto,dc=org") // maps LDAP attributes with prefix "sidekick" for tag "teigenRules"
								.addUsingSearch("dc=constretto,dc=org", "(&(cn=K*)(objectClass=inetOrgPerson))", "uid")
								    // Adds all LDAP objects matching the query to configuration attributes prefixed with the value of the "uid" attribute
								.done();

    ConstrettoConfiguration constrettoConfiguration =  new ConstrettoBuilder(false)
								.addConfigurationStore(configurationStore)
								.getConfiguration();
    final ConfigurableType configurationObject = constrettoConfiguration.as(ConfigurableType.class);
    ...

Credits
-------------
* The [Constretto project](http://constretto.org/) is created and maintained by Kåre Nilsen and his colleagues at [Arktekk AS](http://www.arktekk.no)
* Development of the LDAP module has been sponsored by [NextGenTel AS](http://www.nextgentel.no/)



