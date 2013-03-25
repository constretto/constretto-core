package org.constretto.ldap;

import org.constretto.exception.ConstrettoException;

import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.LdapName;

/**
 * @author sondre
 */
public class LdapConfigurationStoreBuilder {

    public static final String NULL_ARGUMENT = "The \"%1$s\" argument can not be null";
    private DirContext dirContext;
    private LdapConfigurationStore ldapConfigurationStore = new LdapConfigurationStore();

    private LdapConfigurationStoreBuilder(final DirContext dirContext) {
        this.dirContext = dirContext;
    }

    public static LdapConfigurationStoreBuilder usingDirContext(final DirContext dirContext) {
        return new LdapConfigurationStoreBuilder(dirContext);
    }

    private Attributes readAttributesFromLdap(String distinguishedName) {
        try {
            return dirContext.getAttributes(createName(distinguishedName));
        } catch (NamingException e) {
            throw new ConstrettoException(String.format("Could not find LDAP attributes for DSN \"%1$s\"", distinguishedName), e);
        }
    }

    public LdapConfigurationStoreBuilder addDsnWithKey(final String key, final String distinguishedName, String... tags) {
        checkStringArgument("distinguishedName", distinguishedName);
        ldapConfigurationStore = new LdapConfigurationStore(ldapConfigurationStore, key, readAttributesFromLdap(distinguishedName),
                tags);
        return this;
    }

    public LdapConfigurationStoreBuilder addDsn(final String distinguishedName, final String... tags) {
        checkStringArgument("distinguishedName", distinguishedName);
        ldapConfigurationStore = new LdapConfigurationStore(ldapConfigurationStore, readAttributesFromLdap(distinguishedName),
                tags);
        return this;
    }

    public LdapConfigurationStoreBuilder addUsingSearch(final String searchBase, final String filter, final String keyAttribute, final String... tags) {

        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        try {
            final NamingEnumeration<SearchResult> searchResultNamingEnumeration = dirContext.search(createName(searchBase), filter, searchControls);
            while(searchResultNamingEnumeration.hasMore()) {
                final SearchResult result = searchResultNamingEnumeration.next();
                final Attributes attributes = result.getAttributes();
                final Attribute attribute = attributes.get(keyAttribute);
                if(attribute == null) {
                    throw new ConstrettoException(String.format("The LDAP object \"%1$s\" has no attribute value for attribute \"%2$s\"", result.getName(), keyAttribute));
                }
                final String key = attribute.get().toString();
                ldapConfigurationStore = new LdapConfigurationStore(ldapConfigurationStore, key, attributes, tags);
            }
        } catch (NamingException e) {
            throw new ConstrettoException(
                    String.format("An error occurred while searching LDAP using searchBase \"%1$s\" and filter \"%2$s\"", searchBase, filter), e);
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
            throw new IllegalArgumentException(String.format("Provided value \"%1$s\" is not a valid LDAP DSN", distinguishedName));
        }
    }

    private void checkStringArgument(final String fieldName, final String value) {
        if(value == null) {
            throw new IllegalArgumentException(String.format(NULL_ARGUMENT, fieldName));
        }
    }


}
