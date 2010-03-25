/*
 * Copyright 2008 the original author or authors. Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package org.constretto.spring.assembly;

import org.constretto.spring.assembly.helper.service.concreteclasses.CommonInterface;
import org.constretto.spring.assembly.helper.service.concreteclasses.CommonInterfaceDefault;
import org.constretto.spring.assembly.helper.service.concreteclasses.CommonInterfaceStub;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static java.lang.System.setProperty;
import static org.constretto.spring.internal.resolver.DefaultAssemblyContextResolver.ASSEMBLY_KEY;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class AssemblyWithConcreteClassesTest {


    @Test
    public void injectionUsingConstrettoWithEnvironmeentSetAndUsinginterface(){
        class MyConsumer{
            @Autowired
            CommonInterface commonInterface;
        }
        setProperty(ASSEMBLY_KEY,"stub");
        ApplicationContext ctx = loadContextAndInjectWithConstretto();

        MyConsumer consumer = new MyConsumer();
        ctx.getAutowireCapableBeanFactory().autowireBean(consumer);
        Assert.assertEquals(consumer.commonInterface.getClass(),CommonInterfaceStub.class);
    }

    @Test
    public void injectionUsingConstrettoWithEnvironmeentSetAndUsingConcreteClass(){
        class MyConsumer{
            @Autowired
            CommonInterfaceDefault commonInterface;
        }
        setProperty(ASSEMBLY_KEY,"stub");
        ApplicationContext ctx = loadContextAndInjectWithConstretto();

        MyConsumer consumer = new MyConsumer();
        ctx.getAutowireCapableBeanFactory().autowireBean(consumer);
        Assert.assertEquals(consumer.commonInterface.getClass(),CommonInterfaceDefault.class);
    }


    @Test
    public void injectionUsingConstrettoWithUnknownEnvironmentSetAndUsingInterface(){
        class MyConsumer{
            @Autowired
            CommonInterface commonInterface;
        }
        setProperty(ASSEMBLY_KEY,"unknown");
        ApplicationContext ctx = loadContextAndInjectWithConstretto();

        MyConsumer consumer = new MyConsumer();
        ctx.getAutowireCapableBeanFactory().autowireBean(consumer);
        Assert.assertEquals(consumer.commonInterface.getClass(),CommonInterfaceDefault.class);
    }

    
    @Test
    public void injectionNotUsingConstrettoUsingConcreteClass(){
        class MyConsumer{
            @Autowired
            CommonInterfaceDefault commonInterface;
        }
        ApplicationContext ctx = loadContextAndInjectWithoutConstretto();

        MyConsumer consumer = new MyConsumer();
        ctx.getAutowireCapableBeanFactory().autowireBean(consumer);
        Assert.assertEquals(consumer.commonInterface.getClass(),CommonInterfaceDefault.class);
    }


    @Test(expected = BeanCreationException.class)
    public void injectionNotUsingConstrettoUsingInterface(){
        class MyConsumer{
            @Autowired
            CommonInterface commonInterface;
        }
        ApplicationContext ctx = loadContextAndInjectWithoutConstretto();

        MyConsumer consumer = new MyConsumer();
        ctx.getAutowireCapableBeanFactory().autowireBean(consumer);
        Assert.assertEquals(consumer.commonInterface.getClass(),CommonInterfaceDefault.class);
    }


    private ApplicationContext loadContextAndInjectWithConstretto() {
        return new ClassPathXmlApplicationContext(
                "org/constretto/spring/assembly/AssemblyWithConcreteClassesTestWithConstretto-context.xml");

    }

    private ApplicationContext loadContextAndInjectWithoutConstretto() {
        return new ClassPathXmlApplicationContext(
                "org/constretto/spring/assembly/AssemblyWithConcreteClassesTestWithoutConstretto-context.xml");

    }
}
