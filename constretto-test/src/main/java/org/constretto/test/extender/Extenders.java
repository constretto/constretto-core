package org.constretto.test.extender;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import org.junit.runner.Description;
import org.reflections.Reflections;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

/**
 * @author zapodot
 */
public class Extenders implements AutoCloseable {

    private Set<? extends RuleExtender> extenders;

    public Extenders(final Set<? extends RuleExtender> extenders) {
        this.extenders = extenders;

    }

    static Extenders createFromClasspathUsingDescription(final Description testDescription) {
        final Set<Class<? extends RuleExtender>> extenders = findRuleExtenderClasses();
        Set<? extends RuleExtender> extenderInstances = Sets.newHashSet(Collections2.transform(Sets.filter(extenders, new Predicate<Class<? extends RuleExtender>>() {
            @Override
            public boolean apply(final Class<? extends RuleExtender> input) {
                try {
                    return input.getDeclaredConstructor() != null;
                } catch (NoSuchMethodException e) {
                    return false;
                }
            }
        }), new Function<Class<? extends RuleExtender>, RuleExtender>() {
            @Nullable
            @Override
            public RuleExtender apply(final Class<? extends RuleExtender> input) {
                try {
                    return input.getDeclaredConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    throw new IllegalStateException("Could not instantiate RuleExtender", e);
                }
            }
        }));
        for (final RuleExtender ruleExtender : extenderInstances) {
            ruleExtender.setup(testDescription);
        }
        return new Extenders(extenderInstances);
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
