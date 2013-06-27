package org.constretto.internal.store.ldap;

import org.constretto.exception.ConstrettoException;

import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.LdapName;

/**
 * Builder used to build an LdapConfigurationStore by reading LDAP entries from an DirContext
 *
 * @author <a href=mailto:zapodot@gmail.com>Sondre Eikanger Kval&oslash;</a>
 * @link
 * @see LdapConfigurationStore
 */
public class LdapConfigurationStoreBuilder {

    public static final String NULL_ARGUMENT = "The \"%1$s\" argument can not be null";
    private DirContext dirContext;
    private LdapConfigurationStore ldapConfigurationStore = new LdapConfigurationStore();

    private LdapConfigurationStoreBuilder(final DirContext dirContext) {
        this.dirContext = dirContext;
    }

    /**
     * Creates the LdapConfigurationStoreBuilder
     *
     * @param dirContext the Ldap context to be used for reading configuration entries.
     *                   This class will never ever close or change the DirContext instance provided to it
     * @return a LdapConfigurationStoreBuilder to be used for fluently defining the LDAP entries to read
     */
    public static LdapConfigurationStoreBuilder usingDirContext(final DirContext dirContext) {
        return new LdapConfigurationStoreBuilder(dirContext);
    }

    private Attributes readAttributesFromLdap(String distinguishedName) {
        try {
            return dirContext.getAttributes(createName(distinguishedName));
        } catch (NamingException e) {
            throw new ConstrettoException(String.format("Could not find LDAP attributes for DSN \"%1$s\"",
                                                        distinguishedName), e);
        }
    }

    /**
     * Add the given LDAP entry identified the DSN (distinguishedName).
     *
     * @param key               an prefix that will be applied as an prefix for all settings from the LDAP entry
     * @param distinguishedName the DSN of the LDAP entry
     * @param tags              the Constretto tags the entry should be valid for
     * @return
     */
    public LdapConfigurationStoreBuilder addDsnWithKey(final String key,
                                                       final String distinguishedName,
                                                       String... tags) {
        checkStringArgument("distinguishedName", distinguishedName);
        ldapConfigurationStore = new LdapConfigurationStore(ldapConfigurationStore,
                                                            key,
                                                            readAttributesFromLdap(distinguishedName),
                                                            tags);
        return this;
    }

    /**
     * Add the given LDAP entry identified the DSN (distinguishedName).
     *
     * @param distinguishedName the DSN of the LDAP entry
     * @param tags              the Constretto tags the entry should be valid for
     * @return
     */
    public LdapConfigurationStoreBuilder addDsn(final String distinguishedName, final String... tags) {
        checkStringArgument("distinguishedName", distinguishedName);
        ldapConfigurationStore = new LdapConfigurationStore(ldapConfigurationStore,
                                                            readAttributesFromLdap(distinguishedName),
                                                            tags);
        return this;
    }

    /**
     * Used to do an search in the Ldap and may thus return multiple entries that will be made available
     * as configuration entries
     *
     * @param searchBase   a valid LDAP id to search from (ex.: "dc=constretto,dc=org")
     * @param filter       a LDAP filter to be applied to the matching entries
     *                     (ex.: "(&(cn=John*)(objectClass=inetOrgPerson))")
     * @param keyAttribute name of an attribute that will be used to prefix the configuration settings (ex.: "uid")
     * @param tags         the tags the returned settings will be made available for in the ConstrettoConfiguration
     * @return
     */
    public LdapConfigurationStoreBuilder addUsingSearch(final String searchBase,
                                                        final String filter,
                                                        final String keyAttribute,
                                                        final String... tags) {

        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        try {
            final NamingEnumeration<SearchResult> searchResultNamingEnumeration = dirContext.search(createName(
                    searchBase), filter, searchControls);
            while (searchResultNamingEnumeration.hasMore()) {
                final SearchResult result = searchResultNamingEnumeration.next();
                final Attributes attributes = result.getAttributes();
                final Attribute attribute = attributes.get(keyAttribute);
                if (attribute == null) {
                    throw new ConstrettoException(String.format(
                            "The LDAP object \"%1$s\" has no attribute value for attribute \"%2$s\"",
                            result.getName(),
                            keyAttribute));
                }
                final String key = attribute.get().toString();
                ldapConfigurationStore = new LdapConfigurationStore(ldapConfigurationStore, key, attributes, tags);
            }
        } catch (NamingException e) {
            throw new ConstrettoException(
                    String.format("An error occurred while searching LDAP using searchBase \"%1$s\" and filter \"%2$s\"",
                                  searchBase,
                                  filter), e);
        }
        return this;
    }

    public LdapConfigurationStore done() {
        return ldapConfigurationStore;
    }

    private Name createName(final String distinguishedName) {
        try {
            return new LdapName(distinguishedName);
        } catch (InvalidNameException e) {
            throw new IllegalArgumentException(String.format("Provided value \"%1$s\" is not a valid LDAP DSN",
                                                             distinguishedName));
        }
    }

    private void checkStringArgument(final String fieldName, final String value) {
        if (value == null) {
            throw new IllegalArgumentException(String.format(NULL_ARGUMENT, fieldName));
        }
    }


}
