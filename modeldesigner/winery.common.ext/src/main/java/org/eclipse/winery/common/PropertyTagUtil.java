/**
 * Copyright 2016 ZTE Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eclipse.winery.common;

public class PropertyTagUtil {
    // private static final int READ_ONLY = 1;
    // private static final int ATTRIBUTE = 2;
    // private static final int PROPERTY = 4;
    // private static final int INPUT = 8;
    // private static final int METATDATA = 16;

    public enum PropertyTag {
        DEFAULT(0), READ_ONLY(1), ATTRIBUTE(2), PROPERTY(4), INPUT(8), METATDATA(16), PASSWORD(32);

        int value;

        PropertyTag(int value) {
            this.value = value;
        }
    }

    public static boolean hasType(int value, int validTag) {
        return (value & validTag) == validTag;
    }

    public static int addTag(int value, int tag) {
        return value | tag;
    }

    public static String addTag(String value, PropertyTag tag) {
        int binValue = 0;
        try {
            binValue = Integer.parseInt(value, 2);
        } catch (Exception e) {
            return value;
        }
        int result = addTag(binValue, tag.value);
        return Integer.toBinaryString(result);
    }

    public static boolean hasType(String value, PropertyTag validTag) {
        int binValue = 0;
        try {
            binValue = Integer.parseInt(value, 2);
        } catch (Exception e) {
            return false;
        }
        return hasType(binValue, validTag.value);
    }

}
