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
package org.eclipse.winery.repository.ext.imports.yaml.switchmapper.subswitches;

//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//
//import javax.xml.namespace.QName;
//
//import org.eclipse.winery.common.propertydefinitionkv.WinerysPropertiesDefinition;
//import org.eclipse.winery.model.tosca.TArtifactReference;
//import org.eclipse.winery.model.tosca.TArtifactTemplate;
//import org.eclipse.winery.model.tosca.TCapabilityDefinition;
//import org.eclipse.winery.model.tosca.TEntityTemplate;
//import org.eclipse.winery.model.tosca.TImplementationArtifacts;
//import org.eclipse.winery.model.tosca.TInterface;
//import org.eclipse.winery.model.tosca.TNodeType;
//import org.eclipse.winery.model.tosca.TNodeType.CapabilityDefinitions;
//import org.eclipse.winery.model.tosca.TNodeType.Interfaces;
//import org.eclipse.winery.model.tosca.TNodeType.RequirementDefinitions;
//import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
//import org.eclipse.winery.model.tosca.TOperation;
//import org.eclipse.winery.model.tosca.TRequirementDefinition;
//import org.eclipse.winery.repository.ext.common.CommonConst;
//import org.eclipse.winery.repository.ext.imports.yaml.switchmapper.Yaml2XmlSwitch;
//import org.eclipse.winery.repository.ext.imports.yaml.switchmapper.typemapper.ElementType;
//import org.eclipse.winery.repository.ext.yamlmodel.NodeType;
//
///**
// * This class supports processing of node types from a YAML service template.
// */
//public class NodeTypesYaml2XmlSubSwitch extends AbstractYaml2XmlSubSwitch {
//
//    public NodeTypesYaml2XmlSubSwitch(Yaml2XmlSwitch parentSwitch) {
//        super(parentSwitch);
//    }
//
//    /**
//     * Processes every YAML node type and creates a corresponding
//     * {@link org.opentosca.model.tosca.TNodeType}. Each node type is added to
//     * {@link #getDefinitions()} object as well as
//     * {@link org.opentosca.model.tosca.TArtifactTemplate} and
//     * {@link org.opentosca.model.tosca.TNodeTypeImplementation} which will be
//     * created in the process, too.
//     */
//    @Override
//    public void process() {
//        if (getServiceTemplate().getNode_types() != null) {
//            for (final Entry<String, NodeType> yamlNodeType : getServiceTemplate()
//                    .getNode_types().entrySet()) {
//                final TNodeType nodeType = createNodeType(
//                        yamlNodeType.getValue(), yamlNodeType.getKey());
//                getDefinitions()
//                        .getServiceTemplateOrNodeTypeOrNodeTypeImplementation()
//                        .add(nodeType);
//            }
//        }
//    }
//
//    /**
//     * Creates a node type, node type implementation and a list of artifact
//     * templates. Sets some basic attributes and calls
//     * {@link #parseNodeTypeAttributes(org.opentosca.yamlconverter.yamlmodel.yaml.element.NodeType, String, org.opentosca.model.tosca.TNodeType, java.util.List, org.opentosca.model.tosca.TImplementationArtifacts)}
//     * to process each node type attribute.
//     * 
//     * @param value
//     *            YAML node type
//     * @param name
//     *            name of YAML node type
//     * @return Tosca node type
//     */
//    private TNodeType createNodeType(NodeType value, String name) {
//        final TNodeType result = new TNodeType();
//        result.setName(name);
//        result.setTargetNamespace(CommonConst.NS);
//
//        final TNodeTypeImplementation nodeTypeImplementation = new TNodeTypeImplementation();
//        final List<TArtifactTemplate> artifactTemplates = new ArrayList<TArtifactTemplate>();
//        final TImplementationArtifacts implementationArtifacts = new TImplementationArtifacts();
//        nodeTypeImplementation
//                .setImplementationArtifacts(implementationArtifacts);
//        nodeTypeImplementation.setName(name + "Implementation");
//        nodeTypeImplementation.setNodeType(getTypeMapperUtil()
//                .getCorrectTypeReferenceAsQName(result.getName(),
//                        ElementType.NODE_TYPE));
//
//        parseNodeTypeAttributes(value, name, result, artifactTemplates,
//                implementationArtifacts);
//
//        getDefinitions().getServiceTemplateOrNodeTypeOrNodeTypeImplementation()
//                .addAll(artifactTemplates);
//        getDefinitions().getServiceTemplateOrNodeTypeOrNodeTypeImplementation()
//                .add(nodeTypeImplementation);
//
//        return result;
//    }
//
//    private void parseNodeTypeAttributes(final NodeType value,
//            final String name, final TNodeType result,
//            final List<TArtifactTemplate> artifactTemplates,
//            final TImplementationArtifacts implementationArtifacts) {
//        if (value.getArtifacts() != null && !value.getArtifacts().isEmpty()) {
//            // here are only artifact definitions!!
//            parseNodeTypeArtifacts(value.getArtifacts(), artifactTemplates,
//                    implementationArtifacts);
//        }
//        if (value.getCapabilities() != null
//                && !value.getCapabilities().isEmpty()) {
//            result.setCapabilityDefinitions(parseNodeTypeCapabilities(value
//                    .getCapabilities()));
//        }
//        if (value.getDerived_from() != null
//                && !value.getDerived_from().isEmpty()) {
//            result.setDerivedFrom(parseDerivedFrom(getTypeMapperUtil()
//                    .getCorrectTypeReferenceAsQName(value.getDerived_from(),
//                            ElementType.NODE_TYPE)));
//        }
//        if (value.getInterfaces() != null && !value.getInterfaces().isEmpty()) {
//            final Interfaces nodeTypeInterfaces = parseNodeTypeInterfaces(value
//                    .getInterfaces());
//            addInterfaceDefinitionsToImplementationArtifacts(
//                    implementationArtifacts, nodeTypeInterfaces);
//            result.setInterfaces(nodeTypeInterfaces);
//        }
//        List<WinerysPropertiesDefinition> wineryProperties = null;
//        if (value.getProperties() != null && !value.getProperties().isEmpty()) {
//            // result.setPropertiesDefinition(parsePropertiesDefinition(
//            // value.getProperties(), name));
//            wineryProperties = Yaml2XmlSwitchUtils
//                    .convert2PropertyDefinitions(value.getProperties());        
//            result.getAny().addAll(wineryProperties);
//        }
//        if (value.getAttributes() != null && !value.getAttributes().isEmpty()) {
//            wineryProperties = Yaml2XmlSwitchUtils
//                    .convert2AttritueDefinitions(value.getAttributes());
//            result.getAny().addAll(wineryProperties);
//        }
//        
//        if (value.getRequirements() != null
//                && !value.getRequirements().isEmpty()) {
//            // result.setRequirementDefinitions(parseNodeTypeRequirementDefinitions(value.getRequirements()));
//        }
//        if (value.getDescription() != null && !value.getDescription().isEmpty()) {
//            result.getDocumentation().add(
//                    toDocumentation(value.getDescription()));
//        }
//    }
//
//    @SuppressWarnings("unused")
//	private RequirementDefinitions parseNodeTypeRequirementDefinitions(
//            List<Map<String, String>> requirements) {
//        final RequirementDefinitions result = new RequirementDefinitions();
//        for (final Map<String, String> requirement : requirements) {
//            final TRequirementDefinition requirementDefinition = new TRequirementDefinition();
//            if (requirement.size() == 1) {
//                final String capability = (String) requirement.values()
//                        .toArray()[0];
//                final String requirementName = (String) requirement.keySet()
//                        .toArray()[0];
//                final String requirementTypeName = capability.replace(
//                        "Capability", "Requirement");
//                // if (!requirementTypeIsDefinied(requirementTypeName)) {
//                // createAndAddRequirementType(capability, requirementTypeName);
//                // defineRequirementType(requirementTypeName);
//                // }
//                requirementDefinition.setRequirementType(getTypeMapperUtil()
//                        .getCorrectTypeReferenceAsQName(requirementTypeName,
//                                ElementType.REQUIREMENT_TYPE));
//                requirementDefinition.setName(requirementName);
//            }
//            result.getRequirementDefinition().add(requirementDefinition);
//        }
//        return result;
//    }
//
//    private void addInterfaceDefinitionsToImplementationArtifacts(
//            TImplementationArtifacts implementationArtifacts,
//            Interfaces nodeTypeInterfaces) {
//        // TODO: find a better solution to map interfaces to implementation
//        // artifacts
//        for (final TInterface tInterface : nodeTypeInterfaces.getInterface()) {
//            for (final TOperation tOperation : tInterface.getOperation()) {
//                for (final TImplementationArtifacts.ImplementationArtifact implArtifact : implementationArtifacts
//                        .getImplementationArtifact()) {
//                    if (implArtifact.getArtifactRef().getLocalPart()
//                            .contains(tOperation.getName())) {
//                        implArtifact.setInterfaceName(tInterface.getName());
//                        implArtifact.setOperationName(tOperation.getName());
//                    }
//                }
//            }
//        }
//    }
//
//    private Interfaces parseNodeTypeInterfaces(
//            Map<String, Map<String, Map<String, String>>> interfaces) {
//        final Interfaces result = new Interfaces();
//        for (final Entry<String, Map<String, Map<String, String>>> entry : interfaces
//                .entrySet()) {
//            final TInterface inf = getInterfaceWithOperations(entry);
//            result.getInterface().add(inf);
//        }
//        return result;
//    }
//
//    /**
//     * Creates a
//     * {@link org.opentosca.model.tosca.TNodeType.CapabilityDefinitions} object.
//     * Eventually a default for the capability type is used.
//     * 
//     * @param capabilities
//     *            map containing capabilities with some definitions
//     * @return an object containing all capability definitions
//     */
//    private CapabilityDefinitions parseNodeTypeCapabilities(
//            Map<String, Object> capabilities) {
//        final CapabilityDefinitions result = new CapabilityDefinitions();
//        for (final Entry<String, Object> capabilityEntry : capabilities
//                .entrySet()) {
//            final TCapabilityDefinition capabilityDefinition = new TCapabilityDefinition();
//            capabilityDefinition.setName(capabilityEntry.getKey());
//            if (capabilityEntry.getValue() instanceof HashMap) {
//                final Map<?, ?> capability = (Map<?, ?>) capabilityEntry
//                        .getValue();
//                String capabilityType = null;
//                try {
//                    capabilityType = (String) capability.get("type");
//                } catch (final Exception e) {
//                    capabilityType = "CAPABILITY_TYPE";
//                }
//                capabilityDefinition.setCapabilityType(getTypeMapperUtil()
//                        .getCorrectTypeReferenceAsQName(capabilityType,
//                                ElementType.CAPABILITY_TYPE));
//            }
//            result.getCapabilityDefinition().add(capabilityDefinition);
//        }
//        return result;
//    }
//
//    /**
//     * Parse node type artifacts. For each artifact a name, file uri, type and
//     * properties must be set. Description and mime type are optional and not
//     * processed currently.
//     * 
//     * @param artifacts
//     * @param artifactTemplates
//     * @param implementationArtifacts
//     */
//    @SuppressWarnings({ "unchecked", "unused" })
//	private void parseNodeTypeArtifacts(List<Map<String, Object>> artifacts,
//            List<TArtifactTemplate> artifactTemplates,
//            TImplementationArtifacts implementationArtifacts) {
//        for (final Map<String, Object> artifact : artifacts) {
//            String artifactName = "";
//            String artifactFileUri = "";
//            QName artifactTypeQName = null;
//            String artifactDescription = "";
//            String artifactMimeType = "";
//            String artifactType = "";
//            Map<String, Object> additionalProperties = new HashMap<String, Object>();
//
//            for (final Entry<String, Object> artifactEntry : artifact
//                    .entrySet()) {
//                final Object value = artifactEntry.getValue();
//                switch (artifactEntry.getKey()) {
//                case "type":
//                    artifactType = (String) value;
//                    artifactTypeQName = getTypeMapperUtil()
//                            .getCorrectTypeReferenceAsQName(artifactType,
//                                    ElementType.ARTIFACT_TYPE);
//                    break;
//                case "description":
//                    artifactDescription = (String) value;
//                    break;
//                case "mime_type":
//                    artifactMimeType = (String) value;
//                    break;
//                case "properties":
//                    if (value instanceof Map<?, ?>) {
//                        additionalProperties = (Map<String, Object>) value;
//                    }
//                    break;
//                default:
//                    artifactName = artifactEntry.getKey();
//                    if (value instanceof String) {
//                        artifactFileUri = (String) value;
//                    }
//                    break;
//                }
//            }
//            addArtifactTemplate(artifactTemplates, artifactName,
//                    artifactFileUri, artifactTypeQName, artifactType,
//                    additionalProperties);
//            addImplementationArtifact(implementationArtifacts, artifactName,
//                    artifactTypeQName);
//        }
//    }
//
//    private void addImplementationArtifact(
//            TImplementationArtifacts implementationArtifacts,
//            String artifactName, QName artifactType) {
//        final TImplementationArtifacts.ImplementationArtifact implementationArtifact = new TImplementationArtifacts.ImplementationArtifact();
//        implementationArtifact.setArtifactRef(getNamespaceUtil().toTnsQName(
//                artifactName));
//        implementationArtifact.setArtifactType(artifactType);
//        implementationArtifacts.getImplementationArtifact().add(
//                implementationArtifact);
//    }
//
//    private TArtifactTemplate addArtifactTemplate(
//            List<TArtifactTemplate> artifactTemplates, String artifactName,
//            String artifactFileUri, QName artifactType,
//            String originalArtifactType,
//            Map<String, Object> additionalProperties) {
//        final TArtifactTemplate artifactTemplate = new TArtifactTemplate();
//
//        artifactTemplate.setName(artifactName);
//        artifactTemplate.setId(artifactName);
//        artifactTemplate.setType(artifactType);
//
//        setArtifactReferencesForArtifactTemplate(artifactFileUri,
//                artifactTemplate);
//
//        final TEntityTemplate.Properties properties = new TEntityTemplate.Properties();
//        properties
//                .setAny(getAnyMapForProperties(
//                        additionalProperties,
//                        getTypeMapperUtil()
//                                .getCorrectTypeReferenceAsQNameForProperties(
//                                        originalArtifactType,
//                                        ElementType.ARTIFACT_TYPE)));
//        artifactTemplate.setProperties(properties);
//
//        artifactTemplates.add(artifactTemplate);
//
//        return artifactTemplate;
//    }
//
//    private void setArtifactReferencesForArtifactTemplate(
//            final String artifactFileUri,
//            final TArtifactTemplate artifactTemplate) {
//        final TArtifactTemplate.ArtifactReferences artifactReferences = new TArtifactTemplate.ArtifactReferences();
//        final TArtifactReference artifactReference = new TArtifactReference();
//        final TArtifactReference.Include include = new TArtifactReference.Include();
//        final int lastIndexOfSlash = artifactFileUri.lastIndexOf("/");
//        if (lastIndexOfSlash > 0) {
//            artifactReference.setReference(artifactFileUri.substring(0,
//                    lastIndexOfSlash));
//            include.setPattern(artifactFileUri.substring(artifactFileUri
//                    .lastIndexOf("/")));
//        } else {
//            artifactReference.setReference("");
//            include.setPattern(artifactFileUri);
//        }
//        artifactReference.getIncludeOrExclude().add(include);
//        artifactReferences.getArtifactReference().add(artifactReference);
//        artifactTemplate.setArtifactReferences(artifactReferences);
//    }
//}
