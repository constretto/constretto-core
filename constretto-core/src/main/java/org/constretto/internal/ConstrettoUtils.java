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
package org.constretto.internal;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class ConstrettoUtils {

    public static String asCsv(String[] arr) {
        StringBuffer tagcsv = new StringBuffer();
        for (String tag : arr) {
            if (tagcsv.length() > 0) {
                tagcsv.append(",");
            }
            tagcsv.append(tag);
        }
        return tagcsv.toString();
    }

    public static List<String> fromCSV(String csv) {
        List<String> elements = new ArrayList<String>();
        for (String element : csv.split(","))
            elements.add(element);
        return elements;
    }
}
