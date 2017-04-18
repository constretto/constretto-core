package org.constretto.model;

import org.constretto.exception.ConstrettoException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href=mailto:zapodot@gmail.com>Sondre Eikanger Kval&oslash;</a>
 */
public class UrlResourceTest {

    @Test(expected = ConstrettoException.class)
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

    @Test
    public void validUrlsThatDoNotExistShouldWork() throws Exception {
        final UrlResource urlResource = new UrlResource("http://loocalhost/notHere.html");
        assertFalse(urlResource.exists());
    }


    @Test
    public void testToString() throws Exception {
        final UrlResource urlResource = new UrlResource("http://vg.no");
        assertEquals("UrlResource{path='http://vg.no'}", urlResource.toString());
    }


    @Test
    public void testGetPath() throws Exception {
        final UrlResource urlResource = new UrlResource("http://vg.no");
        assertEquals(urlResource.getPath(), "http://vg.no");
    }

}
