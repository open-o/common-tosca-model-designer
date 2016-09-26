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
/**
 * 
 */
package org.eclipse.winery.repository.ext.utils;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.boundaryproperty.PolicyExtInfo;
import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.repository.Utils;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.ext.export.yaml.ExportCommonException;
import org.eclipse.winery.repository.ext.yamlmodel.ArtifactType;
import org.eclipse.winery.repository.ext.yamlmodel.AttributeDefinition;
import org.eclipse.winery.repository.ext.yamlmodel.Capability;
import org.eclipse.winery.repository.ext.yamlmodel.CapabilityType;
import org.eclipse.winery.repository.ext.yamlmodel.Group;
import org.eclipse.winery.repository.ext.yamlmodel.Input;
import org.eclipse.winery.repository.ext.yamlmodel.NodeTemplate;
import org.eclipse.winery.repository.ext.yamlmodel.NodeType;
import org.eclipse.winery.repository.ext.yamlmodel.Output;
import org.eclipse.winery.repository.ext.yamlmodel.PolicyTemplate;
import org.eclipse.winery.repository.ext.yamlmodel.PolicyType;
import org.eclipse.winery.repository.ext.yamlmodel.PropertyDefinition;
import org.eclipse.winery.repository.ext.yamlmodel.RelationshipType;
import org.eclipse.winery.repository.ext.yamlmodel.ServiceTemplate;
import org.eclipse.winery.repository.ext.yamlmodel.TopologyTemplate;
import org.eclipse.winery.repository.resources.entitytypes.policytypes.PolicyTypesResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlWriter;

/**
 * @author 10090474
 *
 */
public class YamlBeansUtils {
    private static final Logger logger = LoggerFactory.getLogger(YamlBeansUtils.class);

    public static String convertToYamlStr(ServiceTemplate st) throws ExportCommonException {
        if (st == null) {
            throw new ExportCommonException("Root element may not be null.");
        }
        Writer output = new StringWriter();
        YamlWriter writer = new YamlWriter(output);
        writer.getConfig().writeConfig.setWriteRootTags(false);
        writer.getConfig().writeConfig.setWriteRootElementTags(false);
        // writer.getConfig().writeConfig.setAutoAnchor(false);
        writer.getConfig().writeConfig.setIndentSize(2);
        setPropertyDefaultType(writer.getConfig());

        try {
            writer.write(st);
        } catch (YamlException e) {
            throw new ExportCommonException("Convert to yaml string failed.", e);
        } finally {
            try {
                writer.close();
            } catch (YamlException e) {
                logger.warn(e.getMessage(), e);
            }
        }

        return output.toString();
    }

    /**
     * @param config
     */
    public static void setPropertyDefaultType(YamlConfig config) {
        config.setPropertyElementType(ServiceTemplate.class, "artifact_types", ArtifactType.class);
        config.setPropertyElementType(ServiceTemplate.class, "capability_types",
                CapabilityType.class);
        config.setPropertyElementType(ServiceTemplate.class, "relationship_types",
                RelationshipType.class);
        config.setPropertyElementType(ServiceTemplate.class, "node_types", NodeType.class);

        config.setPropertyElementType(NodeType.class, "properties", PropertyDefinition.class);
        config.setPropertyElementType(NodeType.class, "attributes", AttributeDefinition.class);
        config.setPropertyElementType(CapabilityType.class, "properties", PropertyDefinition.class);
        config.setPropertyElementType(RelationshipType.class, "properties",
                PropertyDefinition.class);

        config.setPropertyElementType(TopologyTemplate.class, "inputs", Input.class);
        config.setPropertyElementType(TopologyTemplate.class, "node_templates", NodeTemplate.class);
        config.setPropertyElementType(TopologyTemplate.class, "groups", Group.class);
        config.setPropertyElementType(TopologyTemplate.class, "outputs", Output.class);

        config.setPropertyElementType(NodeTemplate.class, "capabilities", Capability.class);
        config.setPropertyElementType(ServiceTemplate.class, "policy_types", PolicyType.class);
        config.setPropertyElementType(PolicyType.class, "properties", PropertyDefinition.class);
        config.setPropertyElementType(ServiceTemplate.class, "policies", PolicyTemplate.class);
        // config.setPropertyElementType(NodeTemplate.class, "requirements",
        // (new HashMap<String, Requirement>()).getClass());
        // config.setPropertyDefaultType(NodeTemplate.class, "requirements",
        // Requirement.class);

        // config.setPropertyElementType(NodeFilter.class, "capabilities",
        // (new HashMap<String, CapabilityFilter>()).getClass());
        // config.setPropertyDefaultType(NodeFilter.class, "capabilities",
        // CapabilityFilter.class);
    }

    public static PolicyTemplate convertPolicyTemplate(TPolicy policy) {
        PolicyExtInfo policyInfo = Utils.buildTPolicy(policy);
        if (null == policyInfo) {
            return null;
        }

        PolicyTemplate template = new PolicyTemplate();

        String desc = policyInfo.getDesc();
        if (null != desc && !desc.isEmpty()) {
            template.setDescription(desc);
        }

        QName derived_from = policyInfo.getDerived_from();
        if (null != derived_from) {
            template.setDerived_from(derived_from.getLocalPart());
        }

        template.setProperties(policyInfo.getProperties());

        List<String> target = policyInfo.getTarget();
        if (null != target && !target.isEmpty()) {
            template.setTargets(target.toArray(new String[target.size()]));
        }

        if (null != policyInfo.getType()) {
            template.setType(policyInfo.getType().getLocalPart());
        }

        return template;
    }

    public static TPolicy convertTPolicy(String name, PolicyTemplate template) {
        if (null == template) {
            return null;
        }
        PolicyExtInfo policyInfo = new PolicyExtInfo();
        policyInfo.setName(name);
        policyInfo.setDesc(template.getDescription());
        policyInfo.setTarget(Arrays.asList(template.getTargets()));
        policyInfo.setProperties(template.getProperties());

        Class<? extends TOSCAComponentId> idClass =
                Utils.getComponentIdClassForComponentContainer(PolicyTypesResource.class);
        SortedSet<? extends TOSCAComponentId> allTOSCAcomponentIds =
                Repository.INSTANCE.getAllTOSCAComponentIds(idClass);
        for (TOSCAComponentId id : allTOSCAcomponentIds) {
            if (id.getXmlId().equals(template.getType())) {
                policyInfo.setType(id.getQName());
            }
            if (id.getXmlId().equals(template.getDerived_from())) {
                policyInfo.setDerived_from(id.getQName());
            }
        }

        return Utils.buildPolicyInfo(policyInfo);
    }
}
