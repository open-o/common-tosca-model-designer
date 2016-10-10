/*******************************************************************************
 * Copyright (c) 2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
/*
 * Modifications Copyright 2016 ZTE Corporation.
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
