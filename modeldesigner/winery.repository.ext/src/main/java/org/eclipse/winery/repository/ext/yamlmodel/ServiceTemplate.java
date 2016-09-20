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
package org.eclipse.winery.repository.ext.yamlmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceTemplate extends YAMLElement {
	private String tosca_definitions_version = "";
    private Map<String, Object> metadata = new HashMap<>();
    private Map<String, Object> dsl_definitions = new HashMap<>();
    private List<String> imports = new ArrayList<String>();
    private Map<String, ArtifactType> artifact_types = new HashMap<String, ArtifactType>();
    private Map<String, DataType> data_types = new HashMap<>();
    private Map<String, CapabilityType> capability_types = new HashMap<String, CapabilityType>();
    private Map<String, Object> interface_types = new HashMap<>();
    private Map<String, RelationshipType> relationship_types = new HashMap<String, RelationshipType>();
    private Map<String, NodeType> node_types = new HashMap<String, NodeType>();
    private Map<String, Object> group_types = new HashMap<>();
    private Map<String, PolicyType> policy_types = new HashMap<>();
    private Map<String, PolicyTemplate> policies = new HashMap<>();
    private TopologyTemplate topology_template;
  private Map<String, Plan> plans = new HashMap<>();

	private String tosca_default_namespace = "";
	private String template_name = "";
	private String template_author = "";
	private String template_version = "";

    public Map<String, PolicyTemplate> getPolicies() {
        return policies;
    }

    public void setPolicies(Map<String, PolicyTemplate> policies) {
        this.policies = policies;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public Map<String, Object> getDsl_definitions() {
        return dsl_definitions;
    }

    public void setDsl_definitions(Map<String, Object> dsl_definitions) {
        this.dsl_definitions = dsl_definitions;
    }

    public Map<String, DataType> getData_types() {
        return data_types;
    }

    public void setData_types(Map<String, DataType> data_types) {
        this.data_types = data_types;
    }

    public Map<String, Object> getInterface_types() {
        return interface_types;
    }

    public void setInterface_types(Map<String, Object> interface_types) {
        this.interface_types = interface_types;
    }

    public Map<String, Object> getGroup_types() {
        return group_types;
    }

    public void setGroup_types(Map<String, Object> group_types) {
        this.group_types = group_types;
    }

    public Map<String, PolicyType> getPolicy_types() {
        return policy_types;
    }

    public void setPolicy_types(Map<String, PolicyType> policy_types) {
        this.policy_types = policy_types;
    }
    
    public TopologyTemplate getTopology_template() {
        return topology_template;
    }

  public void setTopology_template(TopologyTemplate topology_template) {
    this.topology_template = topology_template;
  }

  public Map<String, Plan> getPlans() {
    return plans;
  }

  public void setPlans(Map<String, Plan> plans) {
    if (plans != null) {
      this.plans = plans;
    }
    }

    public void setTosca_definitions_version(String tosca_definitions_version) {
		if (tosca_definitions_version != null) {
			this.tosca_definitions_version = tosca_definitions_version;
		}
	}

    public String getTosca_definitions_version() {
		return this.tosca_definitions_version;
	}

	public void setTosca_default_namespace(String tosca_default_namespace) {
		if (tosca_default_namespace != null) {
			this.tosca_default_namespace = tosca_default_namespace;
		}
	}

	public String getTosca_default_namespace() {
		return this.tosca_default_namespace;
	}

	public void setTemplate_name(String template_name) {
		if (template_name != null) {
			this.template_name = template_name;
		}
	}

	public String getTemplate_name() {
		return this.template_name;
	}

	public void setTemplate_author(String template_author) {
		if (template_author != null) {
			this.template_author = template_author;
		}
	}

	public String getTemplate_author() {
		return this.template_author;
	}

	public void setTemplate_version(String template_version) {
		if (template_version != null) {
			this.template_version = template_version;
		}
	}

	public String getTemplate_version() {
		return this.template_version;
	}

	public void setImports(List<String> imports) {
		if (imports != null) {
			this.imports = imports;
		}
	}

	public List<String> getImports() {
		return this.imports;
	}

	public void setNode_types(Map<String, NodeType> node_types) {
		if (node_types != null) {
			this.node_types = node_types;
		}
	}

	public Map<String, NodeType> getNode_types() {
		return this.node_types;
	}

	public void setCapability_types(Map<String, CapabilityType> capability_types) {
		if (capability_types != null) {
			this.capability_types = capability_types;
		}
	}

	public Map<String, CapabilityType> getCapability_types() {
		return this.capability_types;
	}

	public void setRelationship_types(Map<String, RelationshipType> relationship_types) {
		if (relationship_types != null) {
			this.relationship_types = relationship_types;
		}
	}

	public Map<String, RelationshipType> getRelationship_types() {
		return this.relationship_types;
	}

	public void setArtifact_types(Map<String, ArtifactType> artifact_types) {
		if (artifact_types != null) {
			this.artifact_types = artifact_types;
		}
	}

	public Map<String, ArtifactType> getArtifact_types() {
		return this.artifact_types;
	}


    // @Override
    // public boolean equals(Object o) {
    // if (this == o) return true;
    // if (o == null || getClass() != o.getClass()) return false;
    // if (!super.equals(o)) return false;
    //
    // ServiceTemplate that = (ServiceTemplate) o;
    //
    // if (!artifact_types.equals(that.artifact_types)) return false;
    // if (!capability_types.equals(that.capability_types)) return false;
    // if (!groups.equals(that.groups)) return false;
    // if (!imports.equals(that.imports)) return false;
    // if (!inputs.equals(that.inputs)) return false;
    // if (!node_templates.equals(that.node_templates)) return false;
    // if (!node_types.equals(that.node_types)) return false;
    // if (!outputs.equals(that.outputs)) return false;
    // if (!relationship_types.equals(that.relationship_types)) return false;
    // if (!template_author.equals(that.template_author)) return false;
    // if (!template_name.equals(that.template_name)) return false;
    // if (!template_version.equals(that.template_version)) return false;
    // if (!tosca_default_namespace.equals(that.tosca_default_namespace)) return
    // false;
    // if (!tosca_definitions_version.equals(that.tosca_definitions_version))
    // return false;
    //
    // return true;
    // }
    //
    // @Override
    // public int hashCode() {
    // int result = super.hashCode();
    // result = 31 * result + tosca_definitions_version.hashCode();
    // result = 31 * result + tosca_default_namespace.hashCode();
    // result = 31 * result + template_name.hashCode();
    // result = 31 * result + template_author.hashCode();
    // result = 31 * result + template_version.hashCode();
    // result = 31 * result + imports.hashCode();
    // result = 31 * result + inputs.hashCode();
    // result = 31 * result + node_templates.hashCode();
    // result = 31 * result + node_types.hashCode();
    // result = 31 * result + capability_types.hashCode();
    // result = 31 * result + relationship_types.hashCode();
    // result = 31 * result + artifact_types.hashCode();
    // result = 31 * result + groups.hashCode();
    // result = 31 * result + outputs.hashCode();
    // return result;
    // }
}