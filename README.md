Constretto
==========

Constretto is as configuration management framework for Java applications.
It allows you to “tag” configuration values, so that Constretto could
choose the correct value at runtime.

It also works as a bridge between different configuration formats, and currently Java property files, Ini files, and Java Beans are supported.

[![Build Status](https://travis-ci.org/constretto/constretto-core.png)](https://travis-ci.org/constretto/constretto-core)

## What’s new in 2.1
* Improved support for Junit 4.X by after refactoring the [JUnit Rule](https://github.com/junit-team/junit/wiki/Rules) *ConstrettoRule* added in 2.0.4 to be used as a @ClassRule.
  As a consequence the constretto-test-junit4 module has been merged into the constretto-test module.
  Look at the [example](#using-the-constrettorule-in-a-junit-test) for details
* LDAP configuration support. You can add configuration either by using DSN or by providing a LDAP search
    * NOTE: Constretto will not close or even handle LDAP connection issues for you. The ease usage with Spring LDAP and other third-party libraries

### Examples of using LDAP:

Add LDAP entry either by using its distinguished name or by providing LDAP search parameters:
```java
final ConstrettoConfiguration configuration = new ConstrettoBuilder()
    .createLdapConfigurationStore(initialDirContext)
        .addDsn("cn=Kaare Nilsen,dc=constretto,dc=org")
        // all attributes in the given entry will be available (ie. configuration key "cn" will have the value \"Kaare Nilsen")
        .addDsnWithKey("sidekick", "cn=Jon-Anders Teigen,dc=constretto,dc=org")
        // maps LDAP attributes with prefix "sidekick". (ie. "sidekick.cn" will have the value "Jon-Anders Teigen")
        .addUsingSearch("dc=constretto,dc=org", "(&(cn=K*)(objectClass=inetOrgPerson))", "uid")
         // Adds all LDAP objects matching the query to configuration attributes prefixed with the value of the "uid" attribute
         // ie. if the uid attribute for "cn=Kaare Nilsen,dc=constretto,dc=org" is "kaare", its "cn" attribute will be available as "kaare.cn"
    .done()
    .getConfiguration();
```

### Using the ConstrettoRule in a JUnit test

Constretto will help you setting the specified tags or environment settings for a Junit test by providing ConstrettoRule
to be used as either a `ClassRule (running before class initialization) or a `Rule

```java
@Tags({"purejunit", "test"})
public class ConstrettoRuleTest {
    @Rule
    public ConstrettoRule constrettoRule = new ConstrettoRule();

    @Test
    public void someTestRequiringEnvironmentTagsToBeSet() {
        ConstrettoConfiguration configuration = new ConstrettoBuilder().createSystemPropertiesStore().getConfiguration();
        // current tags will be "purejunit" and "test"
        // more logic here...
    }
}
```
#### Integration with Constretto-Spring

You may also use ConstrettoRule with the @Environment annotation from Constretto Spring

```java
@Environment("junit")
@RunWith(ConstrettoSpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MyTest.TestConfiguration.class)
public class MyTest {

  // Using Spring Java config for this example
  @Configuration
  public static class TestConfiguration {

    @Bean
    public static ConstrettoConfiguration constrettoConfiguration() {
      return new ConstrettoBuilder(true).getConfiguration();
    }

    // Using Spring Java config for this example
    @Bean
    public static ConfigurationAnnotationConfigurer configurationAnnotationConfigurer(final ConstrettoConfiguration configuration) {
      return new ConfigurationAnnotationConfigurer(configuration, new DefaultAssemblyContextResolver());
    }
  }


  // The current environment value will be injected by the ConfigurationAnnotationConfigurer
  // It is enabled by default in Spring XML configurations if you include the "<constretto:configuration/>" tag
  @Environment
  private List<String> injectedEnvironment;

  @ClassRule
  public static ConstrettoRule constrettoRule = new ConstrettoRule();

  @Test
  public void testApplyEnvironment() throws Exception {

    assertArrayEquals(new String[]{"junit"}, injectedEnvironment.toArray(new String[1]));

  }
}
```

What’s new in 2.0
-----------------

-   Json configuration support. You can now in your config files
    configure rich objects, arrays and maps
-   JsonStore added. Now you are able to register a new file with
    complex json content, and register it on a key and list of tags
-   New GenericConverter interface allowing you to create custom parsing
    for rich objects
-   Spring dependencies for the core api and implementation is removed
-   You can now query constretto for the currently used configuration
    tags
-   Overriding configuration by using System properties and System env
    are now enabled by default.
-   Cursored navigation with the “at” method is removed. Will break any
    clients using this feature.
-   `Configuration now uses value instead of expression so that instead of writing `Configuration(expression="aKey")
    now you simply say `Configuration("aKey")

### Examples of the new json format

```javascript
#
# Usage in java code :
# List<String> strings = config.evaluateToList(String.class,"anArrayOfStrings")
#
anArrayOfStrings = ["one","two","three"]

#
# Usage in java code :
# List<Integer> ints = config.evaluateToList(Integer.class,"anArrayOfInts")
#
anArrayOfInts = [1,2,3]

#
# Usage in java code :
# Map<String,Integer> map = config.evaluateToMap(String.class,Integer.class,"mapOfStringAndInt")
#
mapOfStringAndInt = {"keyOne":1,"keyTwo":2}

#
# Usage in java code :
# Person p = config.evaluateWith(new PersonConverter(),"aPerson")
# Where PersonConverter here is an implementation of GenericConverter
#
aPerson={"name":"Kaare",age:29,"adress",{"street":"somewhere","city":"over the rainbow"}}

```


How to Install?
---------------

Constretto is built with maven and deployed to the central maven repository, so if you are using maven, you can simply add Constretto as dependencies in your pom:

```xml
<dependency>
    <groupId>org.constretto</groupId>
    <artifactId>constretto-api</artifactId>
    <version>2.0.4</version>
</dependency>
<dependency>
    <groupId>org.constretto</groupId>
    <artifactId>constretto-core</artifactId>
    <version>2.0.4</version>
    <scope>runtime</scope>
</dependency>
```

If you would like to use the Spring support add:

```xml
<dependency>
    <groupId>org.constretto</groupId>
    <artifactId>constretto-spring</artifactId>
    <version>2.0.4</version>
</dependency>
```

If you would like to use the Constretto Junit support add:

```xml
<dependency>
    <groupId>org.constretto</groupId>
    <artifactId>constretto-test</artifactId>
    <version>2.0.4</version>
    <scope>test</scope>
</dependency>
```


## How to configure Constretto?

### Using Java API

Constretto provides a fluent API to be used in any Java application. Its main interface is ConstrettoConfiguration, that supplies methods to query for values in your configuration. To initialize the ConstrettoConfiguration interface, use the supplied ConstrettoBuilder as shown in the example below:

```java
ConstrettoConfiguration config =
                new ConstrettoBuilder()
                        .createPropertiesStore()
                            .addResource(new DefaultResourceLoader().getResource("classpath:test.properties"))
                            .addResource(new DefaultResourceLoader().getResource("file:test2.properties"))
                        .done()
                        .createIniFileConfigurationStore()
                            .addResource(new DefaultResourceLoader().getResource("classpath:test.ini"))
                        .done()
                        .createObjectConfigurationStore()
                            .addObject(new Object())
                        .done()
                        .createSystemPropertiesStore()
                        .getConfiguration();
```

### Using Spring framework

Constretto works very well in a Spring environment, It provides a namespace for Spring xml configuration files, to build a ConstrettoConfiguration object, and also provides a ProperyPlaceHolder to allow values in Spring xml files to be resolved from Constretto, and also a BeanPostProcessor that enables ConfigurationInjection.

To tell spring to use Constretto:

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:constretto="http://constretto.org/schema/constretto"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
           http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
           http://constretto.org/schema/constretto
           http://constretto.org/schema/constretto/constretto-1.2.xsd">

    <constretto:configuration annotation-config="true" property-placeholder="true">
        <constretto:stores>
            <constretto:properties-store>
                <constretto:resource location="classpath:properties/test1.properties"/>
            </constretto:properties-store>
        </constretto:stores>
    </constretto:configuration>
</beans>
```

## How to use Constretto in your application?

### Using Java API

Now that you've configured Constretto, by Java API or Spring, you may query your configuration using the methods in the ConstrettoConfiguration interface like in the examples below:

```java
// Simple lookup
String aDataSourceUrl = configuration.evaluateToString("datasources.customer.url");
```

### Configuration Injection - Annotation Based

In much the same way as dependency injection work in e.g. Spring and Guice, Constretto allows you to inject configuration into your classes.
It supports injection in fields, and methods as seen in the example below:

Java class to be injected with configuration:

```java
public class DataSourceConfiguration {

    private String myUrl;
    private String myPassword;
    private Integer version;

    // When no expression is explicitly given Constretto will use field name as key
    @Configuration
    private String vendor;

    @Configuration("username")
    private String myUsername;

    @Configure
    public void configure(String url, @Configuration("password") String secret) {
        this.myUrl = url;
        this.myPassword = secret;
    }

    @Configure
    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getUrl() {
        return myUrl;
    }

    public String getUsername() {
        return myUsername;
    }

    public String getPassword() {
        return myPassword;
    }

    public String getVendor() {
        return vendor;
    }

    public Integer getVersion() {
        return version;
    }
}
```
A test that shows this feature used with the Java API:

```java
public class ConfigurationAnnotationsTest {
    private ConstrettoConfiguration configuration;

    @Before
    public void prepareTests() {
        setProperty("datasources.customer.url", "jdbc://url");
        setProperty("datasources.customer.username", "username");
        setProperty("datasources.customer.password", "password");
        setProperty("datasources.customer.vendor", "derby");
        setProperty("datasources.vendor", "derby");
        setProperty("datasources.customer.version", "10");

        configuration = new ConstrettoBuilder().createSystemPropertiesStore().getConfiguration();

    }

    @Test
    public void createNewAnnotatedConfigurationObject() {
        DataSourceConfiguration customerDataSource = configuration.at("datasources").from("customer").as(DataSourceConfiguration.class);
        assertEquals("jdbc://url", customerDataSource.getUrl());
        assertEquals("username", customerDataSource.getUsername());
        assertEquals("password", customerDataSource.getPassword());
        assertEquals("derby", customerDataSource.getVendor());
        assertEquals(new Integer(10), customerDataSource.getVersion());
    }

    @Test
    public void applyConfigrationToAnnotatedConfigurationObject() {
        DataSourceConfiguration customerDataSource = new DataSourceConfiguration();
        configuration.at("datasources").from("customer").on(customerDataSource);
        assertEquals("derby", customerDataSource.getVendor());
        assertEquals("username", customerDataSource.getUsername());
        assertEquals("jdbc://url", customerDataSource.getUrl());
        assertEquals("password", customerDataSource.getPassword());
        assertEquals(new Integer(10), customerDataSource.getVersion());
    }
}
```

Configuration Formats.
----------------------

Constretto currently supports four Configuration sources, and the
following sections shows how these are used, and how you may tag
your configuration values.

### Java Property Files

When using Java Property files, you may tag your entry with
“[tag]." if a key does not have a tag, it will be considered a default, and always be available

Example:

```INI
somedb.username=default username
@production.somedb.username=username in production
@systest.somedb.username=username in system test
```

###Ini Files

Constretto also supports Ini files and here, sections are used as tags

Example:

```INI
[default]
somedb.username=default username

[production]
somedb.username=username in production

[systest]
somedb.username=username in system test
```

### Java Objects used as configuration sources

Constretto are able to use Java objects as configuration sources, and then annotations are used to indicate which tags are used.
Also the ConfigurationSource annotation can use an optional basePath attribute, that are prepended to the JavaBean property names found in the class resulting in "somedb.username" in the example below.

Example:

```java
@ConfigurationSource(basePath ="somedb")
public class DefaultDataSourceConfigurer {

    public String getUsername() {
        return”default username;
    }
}

@ConfigurationSource(basePath = "somedb", tag = "production")
public class ProductionDataSourceConfigurer {

    public String getUsername() {
        return "username in production";
    }
}
```

### System properties

Constretto also allows values to be retrieved from System properties,
but here tags are not supported. Support for system properties are per
default enabled

How to tell Constretto what tags to look up?
--------------------------------------------

Constretto uses a System property, or System environment property to
know what tags to look up. this property is called “CONSTRETTO_TAGS”

Example:

    $java MyApp -DCONSTRETTO_TAGS=tag1,tag2,tag3

    Or

    $export CONSTRETTO_TAGS=tag1,tag2,tag3
    $java Myapp

How to report errors or request features?
-----------------------------------------

Please use the [GitHub issue tracker](http://github.com/constretto/constretto-core/issues)

For further information and documentation
-----------------------------------------

Constretto has several more nice features, and they are covered in the reference manual at the Constretto official website: [http://constretto.github.io](http://constretto.github.io/)
