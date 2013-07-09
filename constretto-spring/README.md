Constretto Spring Module
========================

Support for using Constretto in Spring contexts. It includes namespace support for XML-based Spring contexts as well 
as a PropertyPlaceHolder implementation (allowing property placeholders refering to to Constretto configuration keys 
to be used in bean definitions).

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
