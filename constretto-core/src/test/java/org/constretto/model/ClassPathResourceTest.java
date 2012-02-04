package org.constretto.model;

import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

/**
 * @author <a href=mailto:zapodot@gmail.com>Sondre Eikanger Kval&oslash;</a>
 */
public class ClassPathResourceTest {

    public static final String NON_EXISITING_CLASSPATH_RESOURCE = "ttt.properties";

    @Test
    public void testCreateClassPathResourceThatDoNotExist() throws Exception {
        final ClassPathResource classPathResource = new ClassPathResource(NON_EXISITING_CLASSPATH_RESOURCE);
        assertFalse(classPathResource.exists());
    }

    @Test
    public void testOpenNoneExistingClassPathResource() throws Exception {
        final ClassPathResource classPathResource = new ClassPathResource(NON_EXISITING_CLASSPATH_RESOURCE);
        assertFalse(classPathResource.exists());
        InputStream inputStream = classPathResource.getInputStream();
        assertNull(inputStream);
    }
}
