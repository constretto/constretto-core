package org.constretto.model;

import java.util.Set;

/**
 * @author <a href="mailto:kaare.nilsen@arktekk.no">Kaare Nilsen</a>
 */
public abstract class CValue {

    public abstract Set<String> referencedKeys();

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

    public abstract void replace(String key, String resolvedValue);


}
