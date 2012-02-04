package org.constretto.model;

import org.constretto.exception.ConstrettoException;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertNull;

/**
 * @author <a href=mailto:zapodot@gmail.com>Sondre Eikanger Kval&oslash;</a>
 */
public class UrlResourceTest {

    @Test(expected = IllegalArgumentException.class)
    public void testUrlResourceForNullPath() throws Exception {
        UrlResource urlResource = new UrlResource(null);
    }

    @Test(expected = ConstrettoException.class)
    public void testOpenNonExistingUrlResource() throws Exception {
        final UrlResource urlResource = new UrlResource("http://donotexist/");
        urlResource.getInputStream();
    }

    @Test(expected = ConstrettoException.class)
    public void testOpenMalformedUrlResource() throws Exception {
        final UrlResource urlResource = new UrlResource("++malformed:url:resource");
        urlResource.getInputStream();
    }
}
