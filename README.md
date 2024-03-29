Constretto
==========

Constretto is as configuration management framework for Java applications.
It allows you to “tag” configuration values, so that Constretto could choose the correct value at runtime.

It also works as a bridge between different configuration formats, and currently Java property files, Ini files, and Java Beans are supported.
Starting with Constretto 3 all releases requires Java 8 or later   

[![Java CI with Maven](https://github.com/constretto/constretto-core/actions/workflows/maven.yml/badge.svg)](https://github.com/constretto/constretto-core/actions/workflows/maven.yml)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.constretto/constretto-core/badge.svg)](https://mvnrepository.com/artifact/org.constretto/constretto-core)
[![Coverage](https://codecov.io/gh/constretto/constretto-core/branch/master/graph/badge.svg?token=BQ9RdTTfL6)](https://codecov.io/gh/constretto/constretto-core)
[![Open Source Helpers](https://www.codetriage.com/constretto/constretto-core/badges/users.svg)](https://www.codetriage.com/constretto/constretto-core)

## How to Install?
Constretto is built with maven and deployed to the central maven repository, so if you are using maven, you can simply add Constretto as dependencies in your pom:

```xml
<dependency>
    <groupId>org.constretto</groupId>
    <artifactId>constretto-api</artifactId>
    <version>3.0.0-BETA4</version>
</dependency>
<dependency>
    <groupId>org.constretto</groupId>
    <artifactId>constretto-core</artifactId>
    <version>3.0.0-BETA4</version>
    <scope>runtime</scope>
</dependency>
```

If you would like to use the Spring support add:

```xml
<dependency>
    <groupId>org.constretto</groupId>
    <artifactId>constretto-spring</artifactId>
    <version>3.0.0-BETA1</version>
</dependency>
```
*Note*: The Spring extension is now a [separate project](//github.com/constretto/constretto-spring)

If you would like to use the Constretto Junit support add:

```xml
<dependency>
    <groupId>org.constretto</groupId>
    <artifactId>constretto-test</artifactId>
    <version>3.0.0-BETA4</version>
    <scope>test</scope>
</dependency>
```


## Changes in version 3

Changes in [previous versions](changelog.md) (pre version 3)

## Features

### Constructor injection
* only one constructor per class may be have the @Configure annotation or Constretto will complain

Given the following POJO:
```java
public class SimpleConstructorInjectableBean {

    private String key1;

    @Configure
    public SimpleConstructorInjectableBean(final String key1) {
        this.key1 = key1;
    }

    public String getKey1() {
        return key1;
    }
}
```
An instance may be Constructed using the "as" method:
```java
ConstrettoConfiguration configuration = ...
SimpleConstructorInjectableBean configBean = configuration.as(SimpleConstructorInjectableBean.class);
```

If you use Spring and have enabled Constretto-Spring support you can mix @Autowired and @Configure annotations:
```java
public class AutowiredAndConfiguredConstructorInjectionBean {

    private SimpleConstructorInjectableBean simpleConstructorInjectableBean;

    private String key2;

    @Autowired
    @Configure
    public AutowiredAndConfiguredConstructorInjectionBean(final SimpleConstructorInjectableBean simpleConstructorInjectableBean,
                                                          final String key2) {
        this.simpleConstructorInjectableBean = simpleConstructorInjectableBean;
        this.key2 = key2;
    }

    public SimpleConstructorInjectableBean getSimpleConstructorInjectableBean() {
        return simpleConstructorInjectableBean;
    }

    public String getKey2() {
        return key2;
    }
}
```
```xml
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:constretto="http://constretto.org/schema/constretto"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
       http://constretto.org/schema/constretto http://constretto.org/schema/constretto/constretto-1.2.xsd">

    <!--- annotation-config must be "true" for constructor injection to work --->
    <constretto:configuration annotation-config="true" property-placeholder="false">
        <constretto:stores>
            <constretto:properties-store>
                <constretto:resource location="classpath:properties/test1.properties"/>
            </constretto:properties-store>
        </constretto:stores>
    </constretto:configuration>
    <bean class="org.constretto.spring.configuration.helper.SimpleConstructorInjectableBean"/>
    <bean class="org.constretto.spring.configuration.helper.AutowiredAndConfiguredConstructorInjectionBean" />
</beans>    
```

### Using the LdapConfigurationStore

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
to be used as either a [@ClassRule](https://github.com/junit-team/junit/wiki/Rules#classrule) (running before class initialization like @BeforeClass and @AfterClass) or a [@Rule](https://github.com/junit-team/junit/wiki/Rules#rules) (wrapping a test method invocation like @Before and @After).

The @ClassRule use case is useful if you use a custom JUnitRunner such as the ones provided by Spring Test as it will ensure
that specified tags are set before the JUnitRunner is invoked (and reset to its previous value afterwords).
@ClassRule may also be used for JUnit Suites.

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
import org.constretto.ConstrettoConfiguration;
import org.constretto.spring.ConfigurationAnnotationConfigurer;
import org.constretto.spring.annotation.Environment;
import org.constretto.spring.internal.resolver.DefaultAssemblyContextResolver;
import org.constretto.test.Environment;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

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

  // ClassRules will be invoked before the class (like @BeforeClass/@AfterClass) and thus need to be static
  @ClassRule
  public static ConstrettoRule constrettoRule = new ConstrettoRule();

  @Test
  public void testApplyEnvironment() throws Exception {

    assertArrayEquals(new String[]{"junit"}, injectedEnvironment.toArray(new String[1]));

  }
}
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

To tell Spring to use Constretto:

#### XML Contexts
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
#### Java Contexts
```java
    @org.constretto.spring.annotation.Constretto
    @org.springframework.context.annotation.Configuration
    public class Context {

        // Add public static non-arg factory method that creates a ConstrettoConfiguration
        public static ConstrettoConfiguration constrettoConfiguration() {
            return new ConstrettoBuilder(true).getConfiguration();
        }

    }
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
        return ”default username";
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

[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/constretto/constretto-core/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

