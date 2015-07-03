package org.constretto.model;

import org.constretto.exception.ConstrettoException;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void checkThatFileResourceExists() throws Exception {
        final FileResource nonExisting = new FileResource("devNullFile");
        assertFalse(nonExisting.exists());
        final FileResource existing = new FileResource("file:src/test/resources/cache1.ini");
        assertTrue(existing.exists());
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

    @Test
    public void testToString() throws Exception {
        final FileResource fileResource = new FileResource("file:src/test/resources/cache1.ini");
        assertEquals("FileResource{path='file:src/test/resources/cache1.ini'}", fileResource.toString());

    }

    /**
     * If file name starts with file: chances are it is a file url.
     * Constretto should decode this url since it uses new File(String) which does not support
     * url encoding.
     */
    @Test
    public void testWithSpaces() throws Exception {
        Path path = Paths.get("src/test/resources/dir with spaces/test.properties");

        String string = path.toFile().toURI().toURL().toString();
        final FileResource existing = new FileResource(string);
        assertTrue(existing.exists());
    }
}
