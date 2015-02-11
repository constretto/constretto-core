package org.constretto.internal.store;

import org.constretto.ConstrettoBuilder;
import org.constretto.ConstrettoConfiguration;
import org.constretto.GenericConverter;
import org.constretto.exception.ConstrettoConversionException;
import org.constretto.model.CObject;
import org.constretto.model.CPrimitive;
import org.constretto.model.CValue;
import org.constretto.model.Resource;
import org.junit.Test;

import java.util.Map;

import static org.constretto.internal.converter.ValueConverterRegistry.convertPrimitive;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class YamlStoreTest {

    @Test
    public void shouldParseCorrectly() {
        ConstrettoConfiguration conf = new ConstrettoBuilder()
                .createYamlConfigurationStore()
                .addResource(Resource.create("classpath:yamlTest.yaml"), "person")
                .done()
                .getConfiguration();
        Person person = conf.evaluateWith(new PersonJsonConverter(), "person");
        assertEquals(new Person("Kaare", 29), person);
    }

    @Test
    public void shouldParseCorrectly2() {
        ConstrettoConfiguration conf = new ConstrettoBuilder()
                .createYamlConfigurationStore()
                .addResource(Resource.create("classpath:yamlTest2.yaml"), "person")
                .done()
                .getConfiguration();
        Person person = conf.evaluateWith(new PersonJsonConverter(), "person");
        assertEquals(new Person("Erlend", 34), person);
    }

    @Test
    public void shouldAllowNonExistingResources() {
        ConstrettoConfiguration conf = new ConstrettoBuilder()
                .createYamlConfigurationStore()
                .addResource(Resource.create("classpath:nonExisiting.yaml"), "person")
                .addResource(Resource.create("file:/nonExisiting.yaml"), "person")
                .addResource(Resource.create("http:/nonExisiting.com"), "person")
                .done()
                .getConfiguration();

        assertFalse(conf.hasValue("person"));
    }


    private class PersonJsonConverter implements GenericConverter<Person> {
        public Person fromValue(CValue value) throws ConstrettoConversionException {
            if (value instanceof CObject) {
                Map<String, CValue> data = ((CObject) value).data();
                String name = convertPrimitive(String.class, (CPrimitive) data.get("name"));
                Integer age = convertPrimitive(Integer.class, (CPrimitive) data.get("age"));
                return new Person(name, age);

            } else {
                throw new ConstrettoConversionException(value.toString(), Person.class, "Exptected Json Object but found: " + value.getClass().getSimpleName());
            }
        }
    }

    private class Person {
        public final String name;
        public final Integer age;

        private Person(String name, Integer age) {
            this.age = age;
            this.name = name;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Person person = (Person) o;

            if (age != null ? !age.equals(person.age) : person.age != null) return false;
            if (name != null ? !name.equals(person.name) : person.name != null) return false;

            return true;
        }

        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (age != null ? age.hashCode() : 0);
            return result;
        }

        public String toString() {
            return "Person{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }
}
