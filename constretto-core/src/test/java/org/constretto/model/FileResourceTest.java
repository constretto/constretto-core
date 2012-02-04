package org.constretto.model;

import junit.framework.Assert;
import org.constretto.exception.ConstrettoException;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;

/**
 * @author <a href=mailto:zapodot@gmail.com>Sondre Eikanger Kval&oslash;</a>
 */
public class FileResourceTest {

    @Test(expected = ConstrettoException.class)
    public void testCreateFileResourceForNullPath() throws Exception {
        new FileResource(null);
    }

    @Test(expected = ConstrettoException.class)
    public void testOpenFileResourceThatDoNotExist() throws Exception {
        final FileResource fileResource = new FileResource("devNullFile");
        assertFalse(fileResource.exists());
        fileResource.getInputStream();
    }

    @Test(expected = ConstrettoException.class)
    public void testOpenFileResourceForEmptyPath() throws Exception {
        final FileResource fileResource = new FileResource("");
        assertFalse(fileResource.exists());
        fileResource.getInputStream();
    }

    @Test(expected = ConstrettoException.class)
    public void testOpenFileResourceForFilePrefixOnlyPath() throws Exception {
        final FileResource fileResource = new FileResource("file:");
        assertFalse(fileResource.exists());
        fileResource.getInputStream();
    }
}
