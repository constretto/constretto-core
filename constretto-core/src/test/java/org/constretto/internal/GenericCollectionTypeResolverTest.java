package org.constretto.internal;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author zapodot
 */
public class GenericCollectionTypeResolverTest {

    public static final String KEY = "key";
    public static final Integer VALUE = new Integer(1);
    private Map<String, Integer> mapToTest = new HashMap<String, Integer>();
    private final List<String> collectionToTest = Arrays.asList(KEY);

    public static class TestClass {
        public void configureMap(Map<String, Integer> map) {

        }

        public void configureList(List<String> list) {

        }
    }

    @Before
    public void setUp() throws Exception {
        mapToTest.put(KEY, VALUE);

    }

    private Field getMapField() {
        return getDeclaredField("mapToTest");
    }

    private Field getCollectionField() {
        return getDeclaredField("collectionToTest");
    }

    private Field getDeclaredField(final String fieldName) {
        try {
            return getClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    public void testGetMapKeyFieldType() throws Exception {
        final Class<?> mapKeyFieldType = GenericCollectionTypeResolver.getMapKeyFieldType(getMapField());
        assertEquals(String.class, mapKeyFieldType);
    }

    @Test
    public void testGetMapValueFieldType() throws Exception {
        final Class<?> mapValueFieldType = GenericCollectionTypeResolver.getMapValueFieldType(getMapField());
        assertEquals(Integer.class, mapValueFieldType);
    }

    @Test
    public void testGetMapValueParameterType() throws Exception {
        final MethodParameter methodParameter = getConfigureMapMethodParameter();
        final Class<?> mapValueParameterType = GenericCollectionTypeResolver.getMapValueParameterType(methodParameter);
        assertEquals(Integer.class, mapValueParameterType);

    }

    @Test
    public void testGetMapKeyParameterType() throws Exception {
        final MethodParameter methodParameter = getConfigureMapMethodParameter();
        final Class<?> mapValueParameterType = GenericCollectionTypeResolver.getMapKeyParameterType(methodParameter);
        assertEquals(String.class, mapValueParameterType);

    }



    @Test
    public void testCollectionFieldType() throws Exception {
        final Class<?> collectionFieldType = GenericCollectionTypeResolver.getCollectionFieldType(getCollectionField());
        assertEquals(String.class, collectionFieldType);

    }

    @Test
    public void testCollectionMethodParameterType() throws Exception
    {
        final MethodParameter methodParameter = new MethodParameter(TestClass.class.getDeclaredMethod("configureList",
                                                                                                    List.class), 0);
        final Class<?> collectionParameterType = GenericCollectionTypeResolver.getCollectionParameterType(
                methodParameter);
        assertEquals(String.class, collectionParameterType);
    }

    private MethodParameter getConfigureMapMethodParameter() throws NoSuchMethodException {
        return new MethodParameter(TestClass.class.getDeclaredMethod("configureMap",
                                                                     Map.class), 0);
    }
}
