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

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "PropertyDefinition")
public class PropertyDefinitionKV {

    private String key;
    private String type;
    private String value;
    private String tag;
    private String required;
    private String desc;
    private Constraint constraint;

    public PropertyDefinitionKV() {
        super();
    }

    public PropertyDefinitionKV(String key, String type) {
        super();
        this.setKey(key);
        this.setType(type);
    }

    public PropertyDefinitionKV(String key, String type, String value, String tag) {
        super();
        this.key = key;
        this.type = type;
        this.value = value;
        this.tag = tag;
    }

    public PropertyDefinitionKV(String key, String type, String value, String tag, String required,
            String desc) {
        super();
        this.key = key;
        this.type = type;
        this.value = value;
        this.tag = tag;
        this.required = required;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        if (type == null) {
            throw new IllegalArgumentException();
        }
        this.type = type;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public Constraint getConstraint() {
        return constraint;
    }

    public void setConstraint(Constraint constraint) {
        this.constraint = constraint;
    }

    @Override
    public int hashCode() {
        return this.key.hashCode();
    }
}
