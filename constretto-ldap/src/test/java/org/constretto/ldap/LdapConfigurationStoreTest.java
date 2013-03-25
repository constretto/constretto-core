package org.constretto.ldap;

import org.constretto.exception.ConstrettoException;
import org.constretto.model.ConfigurationValue;
import org.constretto.model.TaggedPropertySet;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author zapodot
 */
@RunWith(MockitoJUnitRunner.class)
public class LdapConfigurationStoreTest {

    @Mock
    private Attributes attributes;
    @Mock
    private Attribute attribute;
    @Mock
    private NamingEnumeration attributesNamingEnumeration;
    private LdapConfigurationStore parentLdapConfigurationStore = new LdapConfigurationStore();

    @Test(expected = ConstrettoException.class)
    public void testParseConfigurationAttributesReadFailed() throws Exception {

        when(attributes.getAll()).thenReturn(attributesNamingEnumeration);
        when(attributesNamingEnumeration.hasMore()).thenThrow(new NamingException());
        try {
            final LdapConfigurationStore ldapConfigurationStore = new LdapConfigurationStore(
                    parentLdapConfigurationStore,
                    attributes);
            ldapConfigurationStore.parseConfiguration();

        } finally {
            verify(attributes).getAll();
            verify(attributesNamingEnumeration).hasMore();
        }
    }

    @Test
    public void testParseConfigurationDefaultConstructor() throws Exception {
        final Collection<TaggedPropertySet> taggedPropertySets = new LdapConfigurationStore().parseConfiguration();
        assertEquals(1, taggedPropertySets.size());
        assertEquals(ConfigurationValue.DEFAULT_TAG, taggedPropertySets.iterator().next().tag());

    }

    @Test
    public void testParseConfigurationTagsNoValues() throws Exception {
        when(attributes.getAll()).thenReturn(attributesNamingEnumeration);
        when(attributesNamingEnumeration.hasMore()).thenReturn(false);

        final Collection<TaggedPropertySet> taggedPropertySets = new LdapConfigurationStore(new LdapConfigurationStore(),
                attributes).parseConfiguration();

        assertEquals(1, taggedPropertySets.size());
        assertEquals(ConfigurationValue.DEFAULT_TAG, taggedPropertySets.iterator().next().tag());

        verify(attributes).getAll();
        verify(attributesNamingEnumeration).hasMore();
    }

    @Test
    public void testParseConfigurationDefaultTagPassword() throws Exception {
        when(attributes.getAll()).thenReturn(attributesNamingEnumeration);
        when(attributesNamingEnumeration.hasMore()).thenReturn(true, false);
        when(attributesNamingEnumeration.next()).thenReturn(attribute);
        when(attribute.getID()).thenReturn("password");

        final Collection<TaggedPropertySet> taggedPropertySets = new LdapConfigurationStore(new LdapConfigurationStore(),
                attributes).parseConfiguration();
        assertEquals(1, taggedPropertySets.size());
        assertEquals(ConfigurationValue.DEFAULT_TAG, taggedPropertySets.iterator().next().tag());

        verify(attributes).getAll();
        verify(attributesNamingEnumeration, times(2)).hasMore();
        verify(attributesNamingEnumeration).next();
        verify(attribute).getID();
    }

    @Test
    public void testParseConfigurationTagsPassword() throws Exception {
        when(attributes.getAll()).thenReturn(attributesNamingEnumeration);
        when(attributesNamingEnumeration.hasMore()).thenReturn(true, false);
        when(attributesNamingEnumeration.next()).thenReturn(attribute);
        when(attribute.getID()).thenReturn("password");

        final String tag = "tag";
        final Collection<TaggedPropertySet> taggedPropertySets = new LdapConfigurationStore(new LdapConfigurationStore(),
                attributes, tag).parseConfiguration();
        assertEquals(1, taggedPropertySets.size());
        final TaggedPropertySet taggedPropertySet = taggedPropertySets.iterator().next();
        assertEquals(tag, taggedPropertySet.tag());
        assertEquals(0, taggedPropertySet.getProperties().size());

        verify(attributes).getAll();
        verify(attributesNamingEnumeration, times(2)).hasMore();
        verify(attributesNamingEnumeration).next();
        verify(attribute).getID();
    }

    @Test
    public void testParseConfigurationTags() throws Exception {
        when(attributes.getAll()).thenReturn(attributesNamingEnumeration);
        when(attributesNamingEnumeration.hasMore()).thenReturn(true, false);
        when(attributesNamingEnumeration.next()).thenReturn(attribute);
        when(attribute.getID()).thenReturn("uid");
        when(attribute.get()).thenReturn("some value");

        final String tag = "tag";
        final Collection<TaggedPropertySet> taggedPropertySets = new LdapConfigurationStore(new LdapConfigurationStore(),
                attributes, tag).parseConfiguration();
        assertEquals(1, taggedPropertySets.size());
        assertEquals(tag, taggedPropertySets.iterator().next().tag());

        verify(attributes).getAll();
        verify(attributesNamingEnumeration, times(2)).hasMore();
        verify(attributesNamingEnumeration).next();
        verify(attribute, times(2)).getID();
        verify(attribute).get();
    }

    @After
    public void tearDown() throws Exception {
        verifyNoMoreInteractions(attributes, attributesNamingEnumeration, attribute);

    }
}
