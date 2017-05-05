package org.constretto.model;

public class RemoteProperty {

    private final String prop;

    private RemoteProperty(String property) {
        this.prop = property;
    }

    public String getProperty() {
        if (!exists()) {
            throw new NullPointerException("Property is not found");
        }
        return prop;
    }

    public boolean exists() {
        return prop != null;
    }
    
    public static RemoteProperty property(String property) {
        return new RemoteProperty(property);
    }
    
    public static RemoteProperty propertyNotFound() {
        return new RemoteProperty(null);
    }

}
