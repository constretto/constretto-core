package org.constretto.internal.store;

import java.io.IOException;
import java.io.InputStream;
import junit.framework.Assert;
import org.constretto.exception.ConstrettoException;
import org.constretto.model.Resource;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.mockito.internal.stubbing.answers.ThrowsException;

/**
 *
 * @author <a href="mailto:asbjorn@aarrestad.com>Asbj&oslash;rn Aarrestad</a>
 */
public class PropertiesStoreInputStreamCloseTest {

    @Test
    public void verifyInputStreamIsClosed() throws IOException {
        Resource resource = mock(Resource.class);
        InputStream is = mock(InputStream.class);
        when(resource.exists()).thenReturn(true);
        when(resource.getInputStream()).thenReturn(is);
        PropertiesStore ps = new PropertiesStore();
        ps.addResource(resource);
        verify(is).close();
    }

    @Test
    public void verifyInputStreamIsClosedOnException() throws IOException {
        InputStream is = null;
        try {
            Resource resource = mock(Resource.class);
            is = mock(InputStream.class, new ThrowsException(new IOException()));
            when(resource.exists()).thenReturn(true);
            when(resource.getInputStream()).thenReturn(is);
            PropertiesStore ps = new PropertiesStore();
            ps.addResource(resource);
            Assert.fail();;
        } catch (ConstrettoException ce) {
            verify(is).close();
        }

    }
}