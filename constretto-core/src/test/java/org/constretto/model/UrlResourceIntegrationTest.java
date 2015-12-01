package org.constretto.model;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * These tests will fail if run behind a enterprise proxy
 *
 * Created by zapodot  on 01.12.2015.
 */
public class UrlResourceIntegrationTest {


    @Test
    @Ignore("Disabled for now. Set up test container that makes it posible to test without requiring access to the Internet")
    public void validUrlsThatDoExistShouldWork() throws Exception {
        final UrlResource urlResource = new UrlResource("https://github.com");
        assertTrue(urlResource.exists());
    }

}
