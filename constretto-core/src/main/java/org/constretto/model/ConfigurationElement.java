/*
 * Copyright 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.constretto.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.constretto.exception.ConstrettoExpressionException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class ConfigurationElement {
    private ConfigurationElement parent;
    private final String name;
    private String value;
    private String tag;
    private int priority;
    private final List<ConfigurationElement> children = new ArrayList<ConfigurationElement>();

    public ConfigurationElement(String name) {
        this.name = name;
    }

    String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    void setValue(String value) {
        this.value = value;
    }

    public ConfigurationElement find(String expression) {
        ConfigurationElement currentElement = this;
        for (String subExpression : expression.split("\\.")) {
            if (currentElement.containsChild(subExpression)) {
                currentElement = currentElement.getChild(subExpression);
            } else {
                return null;
            }
        }
        return currentElement;
    }

    public void remove(String expression) throws ConstrettoExpressionException {
        ConfigurationElement currentElement = this;
        for (String subExpression : expression.split("\\.")) {
            if (currentElement.containsChild(subExpression)) {
                currentElement = currentElement.getChild(subExpression);
            } else {
                throw new ConstrettoExpressionException(expression, "not found");
            }
        }
        ConfigurationElement parentElement = currentElement.parent;
        parentElement.getChildren().remove(currentElement);
    }

    public void update(String expression, String value, String tag, int priority) {
        ConfigurationElement currentElement = this;
        if (expression.contains(".")) {
            for (String subExpression : expression.split("\\.")) {
                if (!currentElement.containsChild(subExpression)) {
                    ConfigurationElement newElement = new ConfigurationElement(subExpression);
                    currentElement.addChild(newElement);
                    currentElement = newElement;
                } else {
                    currentElement = currentElement.getChild(subExpression);
                }
            }
        } else {
            ConfigurationElement newElement = new ConfigurationElement(expression);
            currentElement.addChild(newElement);
            currentElement = newElement;
        }
        currentElement.setValue(value);
    }

    @Override
    public String toString() {
        return "ConfigurationElement{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(obj, this);
    }

    private boolean containsChild(String name) {
        for (ConfigurationElement currentElement : children) {
            if (currentElement.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private void addChild(ConfigurationElement configurationElement) {
        children.add(configurationElement);
        configurationElement.parent = this;
    }

    private List<ConfigurationElement> getChildren() {
        return children;
    }

    private ConfigurationElement getChild(String name) {
        ConfigurationElement foundElement = null;
        for (ConfigurationElement currentElement : children) {
            if (currentElement.getName().equals(name)) {
                foundElement = currentElement;
            }
        }
        return foundElement;
    }


}
