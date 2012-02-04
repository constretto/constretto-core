package org.constretto.model;

import org.constretto.exception.ConstrettoException;
import org.junit.Test;

/**
 * @author <a href=mailto:zapodot@gmail.com>Sondre Eikanger Kval&oslash;</a>
 */
public class FileResourceTest {

    @Test(expected = IllegalArgumentException.class)
    public void testCreateFileResourceForNullPath() throws Exception {
        new FileResource(null);
    }

    @Test(expected = ConstrettoException.class)
    public void testOpenFileResourceThatDoNotExist() throws Exception {
        final FileResource fileResource = new FileResource("devNullFile");
        fileResource.getInputStream();
    }

    @Test(expected = ConstrettoException.class)
    public void testOpenFileResourceForEmptyPath() throws Exception {
        final FileResource fileResource = new FileResource("");
        fileResource.getInputStream();
    }

    @Test(expected = ConstrettoException.class)
    public void testOpenFileResourceForFilePrefixOnlyPath() throws Exception {
        final FileResource fileResource = new FileResource("file:");
        fileResource.getInputStream();
    }
}
