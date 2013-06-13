package org.constretto.internal.store.ldap;

import org.constretto.exception.ConstrettoException;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author zapodot
 */
@RunWith(MockitoJUnitRunner.class)
public class LdapConfigurationStoreBuilderTest {

    @Mock
    private DirContext dirContext;

    @Mock
    private Attributes attributes;

    @Mock
    private Attribute attribute;

    @Mock
    private NamingEnumeration<SearchResult> searchResults;

    @Mock
    private SearchResult searchResult;

    @InjectMocks
    private LdapConfigurationStoreBuilder ldapConfigurationStoreBuilder;

    @Test(expected = ConstrettoException.class)
    public void testNamingException() throws Exception {

        when(dirContext.getAttributes(any(Name.class))).thenThrow(new NamingException("test"));
        try {
            ldapConfigurationStoreBuilder.addDsn("cn=me,dc=here,dc=com");
        } finally {
            verify(dirContext).getAttributes(any(Name.class));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddDsnIllegalName() throws Exception {

        ldapConfigurationStoreBuilder.addDsn("This must surely be an illegal DSN");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddDsnNull() throws Exception {
        ldapConfigurationStoreBuilder.addDsn(null);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddKeyAndDsnNull() throws Exception {
        ldapConfigurationStoreBuilder.addDsn(null, null);

    }

    @Test(expected = ConstrettoException.class)
    public void testAddUsingSearchKeyAttributeNotFound() throws Exception {

        final String filter = "(objectClass=person)";
        when(dirContext.search(any(Name.class), eq(filter), any(SearchControls.class))).thenReturn(searchResults);
        when(searchResults.hasMore()).thenReturn(true);
        when(searchResults.next()).thenReturn(searchResult);
        when(searchResult.getAttributes()).thenReturn(attributes);
        final String noneExistingKey = "NoneExistingKey";
        when(attributes.get(eq(noneExistingKey))).thenReturn(null);
        when(searchResult.getName()).thenReturn("cn=me,dc=here,dc=com");
        try {
            ldapConfigurationStoreBuilder.addUsingSearch("dc=here,dc=com", filter, noneExistingKey);
        } finally {
            verify(dirContext).search(any(Name.class), eq(filter), any(SearchControls.class));
            verify(searchResults).hasMore();
            verify(searchResults).next();
            verify(searchResult).getAttributes();
            verify(attributes).get(eq(noneExistingKey));
            verify(searchResult).getName();
        }

    }

    @Test(expected = ConstrettoException.class)
    public void testAddUsingSearchKeyFailes() throws Exception {

        final String filter = "(objectClass=person)";
        when(dirContext.search(any(Name.class),
                eq(filter),
                any(SearchControls.class))).thenThrow(new NamingException());
        final String noneExistingKey = "NoneExistingKey";
        try {
            ldapConfigurationStoreBuilder.addUsingSearch("dc=here,dc=com", filter, noneExistingKey);
        } finally {
            verify(dirContext).search(any(Name.class), eq(filter), any(SearchControls.class));
        }

    }


    @After
    public void tearDown() throws Exception {
        verifyNoMoreInteractions(dirContext, searchResults, searchResult, attributes, attribute);

    }
}
