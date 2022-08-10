package org.constretto.test.extender;

import org.junit.runner.Description;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zapodot
 */
public class Extenders implements AutoCloseable {

    private Set<? extends RuleExtender> extenders;

    public Extenders(final Set<? extends RuleExtender> extenders) {
        this.extenders = extenders;

    }

    static Extenders createFromClasspathUsingDescription(final Description testDescription) {
        final Set<RuleExtender> extenders = findRuleExtenderClasses().stream().filter(aClass -> {
            try {
                return aClass.getDeclaredConstructor() != null;
            } catch (NoSuchMethodException e) {
                return false;
            }
        }).map((Function<Class<? extends RuleExtender>, RuleExtender>) aClass -> {
            try {
                return aClass.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException("Could not invoke default constructor on RuleExtender", e);
            } catch (NoSuchMethodException e) {
                throw new  IllegalStateException("Could not instantiate RuleExtender", e);
            }
        }).collect(Collectors.toSet());
        extenders.forEach(ruleExtender -> ruleExtender.setup(testDescription));
        return new Extenders(extenders);
    }

    static Extenders createFromKnownSetWithDescription(Set<? extends RuleExtender> extenders, final Description description) {
        for (final RuleExtender ruleExtender : extenders) {
            ruleExtender.setup(description);
        }
        return new Extenders(extenders);
    }

    private static Set<Class<? extends RuleExtender>> findRuleExtenderClasses() {
        return new Reflections(RuleExtender.class.getPackage().getName()).getSubTypesOf(RuleExtender.class);
    }

    @Override
    public void close() {
        for (RuleExtender ruleExtender : extenders) {
            ruleExtender.close();
        }
    }
}
