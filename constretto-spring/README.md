Constretto Spring Module
========================

Support for using Constretto in Spring contexts. It includes namespace support for XML-based Spring contexts as well 
as a PropertyPlaceHolder implementation (allowing property placeholders refering to to Constretto configuration keys 
to be used in bean definitions).

### Compatibility
The Spring module is tested with Spring 3.2.X and Java 7. Most features will probably work fine with Spring 4.X and/or Java 8, but 100% compatibility can not be guaranteed.  


Spring XML Namespace support
----------------------------

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:constretto="http://constretto.org/schema/constretto"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
       http://constretto.org/schema/constretto http://constretto.org/schema/constretto/constretto-1.2.xsd">

    <constretto:configuration annotation-config="true" property-placeholder="true">
        <constretto:stores>
            <constretto:properties-store>
                <constretto:resource location="classpath:properties/test1.properties"/>
            </constretto:properties-store>
        </constretto:stores>
    </constretto:configuration>
    
    <bean class="org.constretto.beans.ExampleBean">
        <property name="field" value="${constretto.key}" />
    </bean>
</beans>
```

Spring JavaConfig support
----------------------------
```java
    @org.springframework.context.annotation.Configuration
    public class TestContext extends BasicConstrettoConfiguration {
    
        @Configuration(required = true) // Will be injected by Constretto's Configuration processor
        private String key1;
    
        @Value("${key1}") // Will be injected by Constretto's Property placeholder processor
        private String key1AsValue;
    
        @Override
        public org.constretto.ConstrettoConfiguration constrettoConfiguration() {
            return new ConstrettoBuilder()
                    .createPropertiesStore()
                    .addResource(Resource.create("classpath:properties/test1.properties"))
                    .done()
                    .getConfiguration();
        }
    }
```

