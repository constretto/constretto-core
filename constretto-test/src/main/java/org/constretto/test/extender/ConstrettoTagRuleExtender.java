package org.constretto.test.extender;

import org.constretto.test.extractors.ConstrettoTagExtractor;
import org.junit.runner.Description;

import static org.constretto.internal.ConstrettoUtils.asCsv;
import static org.constretto.internal.resolver.DefaultConfigurationContextResolver.TAGS;

/**
 * @author sondre
 */
public class ConstrettoTagRuleExtender implements RuleExtender {

    private String originalValue;

    @Override
    public void setup(final Description testDescription) {
        final String[] tagValuesForTest = ConstrettoTagExtractor.findTagValueForDescription(testDescription);
        this.originalValue = changeTagsSystemProperty(tagValuesForTest);

    }

    private String changeTagsSystemProperty(String[] tags) {

        if (tags == null) {
            return System.getProperty(TAGS);
        } else {
            return System.setProperty(TAGS, asCsv(tags));
        }
    }


    @Override
    public void close() {
        if (originalValue == null) {
            System.clearProperty(TAGS);
        } else {
            System.setProperty(TAGS, originalValue);
        }
    }
}
