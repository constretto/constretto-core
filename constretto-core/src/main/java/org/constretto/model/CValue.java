package org.constretto.model;

import com.sun.xml.internal.fastinfoset.algorithm.BooleanEncodingAlgorithm;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:kaare.nilsen@arktekk.no">Kaare Nilsen</a>
 */
public abstract class CValue {
    protected static final String VARIABLE_PREFIX = "#{";
    protected static final String VARIABLE_SUFFIX = "}";

    public Set<String> referencedKeys() {
        return new HashSet<String>();
    }

    public boolean containsVariables() {
        return !referencedKeys().isEmpty();
    }

    public boolean isArray(){
        return this instanceof CArray;
    }

    public boolean isObject(){
        return this instanceof CObject;
    }

    public boolean isPrimitive(){
        return this instanceof CPrimitive;
    }

    public abstract void replace(String key, CValue cValue);
}
