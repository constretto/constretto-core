package org.constretto.spring.assembly.helper.service.scoped;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

import java.util.HashMap;
import java.util.Map;

public class MockSessionScope implements Scope {

    private Map<String, Object> scope = new HashMap<String, Object>();

    public Object get(String s, ObjectFactory objectFactory) {
        if (!scope.containsKey(s)) {
            scope.put(s, objectFactory.getObject());
        }
        return scope.get(s);
    }

    public Object remove(String s) {
        return null;
    }

    public void registerDestructionCallback(String s, Runnable runnable) {
    }

    public Object resolveContextualObject(String key) {
        return null; 
    }

    public String getConversationId() {
        return null;
    }

    public void reset() {
        scope.clear();
    }

}
