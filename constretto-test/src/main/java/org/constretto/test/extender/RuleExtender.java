package org.constretto.test.extender;

import org.junit.runner.Description;

/**
 * @author zapodot
 */
public interface RuleExtender extends AutoCloseable {

    /**
     * Pre-test hook
     */
    void setup(final Description testDescription);

    /**
     * Run post tests to do cleanup
     */
    @Override
    void close();
}
