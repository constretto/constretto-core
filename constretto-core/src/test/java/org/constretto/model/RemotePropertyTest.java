package org.constretto.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RemotePropertyTest {
    
    @Test(expected = NullPointerException.class)
    public void throwNullPointerWhenPropertyNotFound() {
        RemoteProperty property = RemoteProperty.propertyNotFound();
        
        assertEquals(false, property.exists());
        property.getProperty();
    }
}
