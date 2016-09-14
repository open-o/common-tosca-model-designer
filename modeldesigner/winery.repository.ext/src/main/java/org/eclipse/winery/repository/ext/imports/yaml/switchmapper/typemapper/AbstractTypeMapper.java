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
package org.eclipse.winery.repository.ext.imports.yaml.switchmapper.typemapper;


/**
 * @author Sebi
 */
public abstract class AbstractTypeMapper {

	/**
	 * Base names for YAML types.
	 */
	protected static final String TOSCA_RELATIONSHIPS = "tosca.relationships.";
	protected static final String TOSCA_CAPABILITIES = "tosca.capabilities.";
	protected static final String TOSCA_NODES = "tosca.nodes.";
	protected static final String TOSCA_ARTIFACTS = "tosca.artifacts.";

	/**
	 * Selects the relationship type name from XML for {@code yamlRelationshipType}.
	 *
	 * @param yamlRelationshipType name of the relationship type
	 * @return the name of its XML representation
	 * @throws org.opentosca.yamlconverter.main.exceptions.NoTypeMappingException if no mapping exists for {@code yamlRelationshipType}
	 */
	public abstract String getXmlRelationshipType(String yamlRelationshipType) throws NoTypeMappingException;

	/**
	 * Selects the capability type name from XML for {@code yamlCapabilityType}.
	 *
	 * @param yamlCapabilityType name of the capability type
	 * @return the name of its XML representation
	 * @throws org.opentosca.yamlconverter.main.exceptions.NoTypeMappingException if no mapping exists for {@code yamlCapabilityType}
	 */
	public abstract String getXmlCapabilityType(String yamlCapabilityType) throws NoTypeMappingException;

	/**
	 * Selects the interface name from XML for {@code yamlInterfaceName}.
	 *
	 * @param yamlInterfaceName name of the interface
	 * @return the name of its XML representation
	 * @throws org.opentosca.yamlconverter.main.exceptions.NoTypeMappingException if no mapping exists for {@code yamlInterfaceName}
	 */
	public abstract String getXmlInterface(String yamlInterfaceName) throws NoTypeMappingException;

	/**
	 * Selects the node type name from XML for {@code yamlNodeType}.
	 *
	 * @param yamlNodeType name of the node type
	 * @return the name of its XML representation
	 * @throws org.opentosca.yamlconverter.main.exceptions.NoTypeMappingException if no mapping exists for {@code yamlNodeType}
	 */
	public abstract String getXmlNodeType(String yamlNodeType) throws NoTypeMappingException;

	/**
	 * Selects the artifact type name from XML for {@code yamlArtifactType}.
	 *
	 * @param yamlArtifactType name of the artifact type
	 * @return the name of its XML representation
	 * @throws org.opentosca.yamlconverter.main.exceptions.NoTypeMappingException if no mapping exists for {@code yamlArtifactType}
	 */
	public abstract String getXmlArtifactType(String yamlArtifactType) throws NoTypeMappingException;

	/**
	 * Selects the requirement type name from XML for {@code yamlRequirementType}.
	 *
	 * @param yamlRequirementType name of the requirement type
	 * @return the name of its XML representation
	 * @throws org.opentosca.yamlconverter.main.exceptions.NoTypeMappingException if no mapping exists for {@code yamlRequirementType}
	 */
	public abstract String getXmlRequirementType(String yamlRequirementType) throws NoTypeMappingException;
}
