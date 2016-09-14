/**
 * Copyright 2016 [ZTE] and others.
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
package org.eclipse.winery.common.propertydefinitionkv;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Constraint {

    @XmlElement(name = "validValue")
    private String validValue;

    @XmlElement(name = "greaterThan")
    private String greaterThan;

    @XmlElement(name = "greaterOrEqual")
    private String greaterOrEqual;

    @XmlElement(name = "lessThan")
    private String lessThan;

    @XmlElement(name = "leesOrEqual")
    private String leesOrEqual;

    @XmlElement(name = "length")
    private String length;

    @XmlElement(name = "minLength")
    private String minLength;

    @XmlElement(name = "maxLength")
    private String maxLength;

    //Constrains the property or parameter to a value that is allowed by the provided regular expression.
    @XmlElement(name = "pattern")
    private String pattern;
    
    //Constrains a property or parameter to a value in range of (inclusive) the two values declared.
    @XmlElement(name = "inRange")
    private String inRange;

    public String getValidValue() {
        return validValue;
    }

    public void setValidValue(String validValue) {
        this.validValue = validValue;
    }

    public String getGreaterThan() {
        return greaterThan;
    }

    public void setGreaterThan(String greaterThan) {
        this.greaterThan = greaterThan;
    }

    public String getGreaterOrEqual() {
        return greaterOrEqual;
    }

    public void setGreaterOrEqual(String greaterOrEqual) {
        this.greaterOrEqual = greaterOrEqual;
    }

    public String getLessThan() {
        return lessThan;
    }

    public void setLessThan(String lessThan) {
        this.lessThan = lessThan;
    }

    public String getLeesOrEqual() {
        return leesOrEqual;
    }

    public void setLeesOrEqual(String leesOrEqual) {
        this.leesOrEqual = leesOrEqual;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getMinLength() {
        return minLength;
    }

    public void setMinLength(String minLength) {
        this.minLength = minLength;
    }

    public String getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(String maxLength) {
        this.maxLength = maxLength;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getInRange() {
        return inRange;
    }

    public void setInRange(String inRange) {
        this.inRange = inRange;
    }

}
