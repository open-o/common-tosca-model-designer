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
package de.ustutt.iaas.bpmn2bpel.parser;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.ustutt.iaas.bpmn2bpel.model.AndGatewayMerge;
import de.ustutt.iaas.bpmn2bpel.model.AssignTask;
import de.ustutt.iaas.bpmn2bpel.model.DecisionTask;
import de.ustutt.iaas.bpmn2bpel.model.EndTask;
import de.ustutt.iaas.bpmn2bpel.model.ForkTask;
import de.ustutt.iaas.bpmn2bpel.model.LoopTask;
import de.ustutt.iaas.bpmn2bpel.model.ManagementFlow;
import de.ustutt.iaas.bpmn2bpel.model.ManagementTask;
import de.ustutt.iaas.bpmn2bpel.model.RestTask;
import de.ustutt.iaas.bpmn2bpel.model.StartTask;
import de.ustutt.iaas.bpmn2bpel.model.Task;
import de.ustutt.iaas.bpmn2bpel.model.param.ConcatParameter;
import de.ustutt.iaas.bpmn2bpel.model.param.DeploymentArtefactParameter;
import de.ustutt.iaas.bpmn2bpel.model.param.ExpressionParameter;
import de.ustutt.iaas.bpmn2bpel.model.param.ImplementationArtefactParameter;
import de.ustutt.iaas.bpmn2bpel.model.param.Parameter;
import de.ustutt.iaas.bpmn2bpel.model.param.PlanParameter;
import de.ustutt.iaas.bpmn2bpel.model.param.StringParameter;

/**
 * Copyright 2015 IAAS University of Stuttgart <br>
 * <br>
 * 
 * TODO describe expected JSON format here
 * 
 * @author Sebastian Wagner
 *
 */
public class BPMN4JsonParser extends Parser {

  private static Logger log = LoggerFactory.getLogger(BPMN4JsonParser.class);

  private List<Parameter> variables = new ArrayList<Parameter>();

  private List<String> iaResponseVariables = new ArrayList<String>();

  @Override
  public ManagementFlow parse(URI jsonFileUrl) throws ParseException {

    try {
      // general method, same as with data binding
      ObjectMapper mapper = new ObjectMapper();
      mapper.enable(SerializationFeature.INDENT_OUTPUT);
      // (note: can also use more specific type, like ArrayNode or
      // ObjectNode!)
      JsonNode rootNode = mapper.readValue(jsonFileUrl.toURL(), JsonNode.class);

      String prettyPrintedJson =
          mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
      log.debug("Creating management flow from following Json model:" + prettyPrintedJson);

      /*
       * Contains the ids (values) of the target nodes of a certain node (key is node id of this
       * node)
       */
      Map<String, Set<String>> nodeWithTargetsMap = new HashMap<String, Set<String>>();
      Map<String, Task> taskMap = new HashMap<String, Task>();
      String startEventId = null;

      /* Create model objects from Json nodes */
      log.debug("Creating node models...");
      Iterator<JsonNode> iter = rootNode.iterator();
      while (iter.hasNext()) {
        JsonNode jsonNode = (JsonNode) iter.next();

        /*
         * As top level elements just start events, end events and management tasks expected which
         * are transformed to tasks in our management model
         */
        Task task = createTaskFromJson(jsonNode);
        /*
         * Task may be null if it could not be created due to missing or incorrect fields/values in
         * the Json node
         */
        if (task != null) {
          taskMap.put(task.getId(), task);
          if (JsonKeys.TASK_TYPE_START_EVENT.equals(task.getTaskTypeDetail())) {
            startEventId = task.getId();
          }

          nodeWithTargetsMap.put(task.getId(), extractNodeTargetIds(jsonNode));
        } else {
          String ignoredJsonNode =
              mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
          log.warn("No model element could be created from following node due to missing or invalid keys/values :"
              + ignoredJsonNode);
        }
      }

      ManagementFlow managementFlow =
          new SortParser(taskMap, nodeWithTargetsMap).buildManagementFlow(startEventId);

      return managementFlow;

    } catch (Exception e) {
      log.error("Error while creating management flow : " + e.getMessage());
      throw new ParseException(e);
    }

  }

  protected Task createTaskFromJson(JsonNode jsonNode) {
    // TODO check if type attributes are set and are correct

    if (!hasRequiredFields(jsonNode, Arrays.asList(JsonKeys.TYPE, JsonKeys.NAME, JsonKeys.ID))) {
      log.warn("Ignoring task/event node: One of the fields '" + JsonKeys.TYPE + "', '"
          + JsonKeys.NAME + "' or '" + JsonKeys.ID + "' is missing");
      return null;
    }

    Task task = null;
    String taskType = jsonNode.get(JsonKeys.TYPE).asText();
    String taskName = jsonNode.get(JsonKeys.NAME).asText();
    String taskId = jsonNode.get(JsonKeys.ID).asText();

    log.debug("Parsing JSON task or event node with id '" + taskId + "', name '" + taskName
        + "', type '" + taskType + "'");

    switch (taskType) {
      case JsonKeys.TASK_TYPE_START_EVENT:
        task = createStartTaskFromJson(jsonNode);
        break;
      case JsonKeys.TASK_TYPE_MGMT_TASK:
        task = createManagementTaskFromJson(jsonNode);
        break;
      // add by qinlihan --start
      case JsonKeys.TASK_TYPE_REST_TASK:
        task = createRestTaskFromJson(jsonNode);
        break;
      case JsonKeys.TASK_TYPE_DECISION:
        task = createDecisionTaskFromJson(jsonNode);
        break;
      case JsonKeys.TASK_TYPE_MERGE:
        task = createMergeTaskFromJson(jsonNode);
        break;
      case JsonKeys.TASK_TYPE_FORK:
        task = createForkTaskFromJson(jsonNode);
        break;
      case JsonKeys.TASK_TYPE_JOIN:
        task = createJoinTasFromJson(jsonNode);
        break;
      case JsonKeys.TASK_TYPE_WHILE:
        task = createLoopStartTasFromJson(jsonNode);
        break;
      case JsonKeys.TASK_TYPE_REPEAT_UNTIL:
        task = createLoopStartTasFromJson(jsonNode);
        break;
      case JsonKeys.TASK_TYPE_LOOP:
        task = createLoopEndTasFromJson(jsonNode);
        break;
      case JsonKeys.TASK_TYPE_ASSIGN:
        task = createAssignTaskFromJson(jsonNode);
        break;
      // add by qinlihan --end
      case JsonKeys.TASK_TYPE_END_EVENT:
        task = createEndTaskFromJson(jsonNode);
        break;
      default:
        log.warn("Ignoring node: type '" + taskType + "' is unkown");
        return null;
    }

    // add by qinlihan
    if (null == task) {
      return null;
    }
    taskName = taskName.trim().replaceAll(" ", "_");

    /* Set generic task attributes */
    task.setId(taskId);
    task.setName(taskName);
    if (null == task.getTaskTypeDetail()) {
      task.setTaskTypeDetail(taskType);
    }

    /* Add input parameters to task */
    task.setInputParameters(getParams(jsonNode.get(JsonKeys.INPUT)));
    task.setOutputParameters(getParams(jsonNode.get(JsonKeys.OUTPUT)));

    return task;
  }

  private Task createAssignTaskFromJson(JsonNode jsonNode) {
    return new AssignTask(getParams(jsonNode.get(JsonKeys.ASSIGN_PARAMS)));
  }

  private void parseVariables(JsonNode jsonNode) {
    variables.addAll(getParams(jsonNode.get(JsonKeys.VARIABLES)));
  }

  private Task createLoopStartTasFromJson(JsonNode jsonNode) {
    if (!hasRequiredFields(jsonNode, Arrays.asList(JsonKeys.CONDITION))) {
      log.warn("Ignoring rest node: One of the fields '" + JsonKeys.CONDITION + "' is missing");
      return null;
    }
    String condition = jsonNode.findValue(JsonKeys.CONDITION).asText();
    return new LoopTask(condition);
  }

  private Task createLoopEndTasFromJson(JsonNode jsonNode) {
    return new AndGatewayMerge();
  }

  private Task createJoinTasFromJson(JsonNode jsonNode) {
    return new AndGatewayMerge();
  }

  private Task createForkTaskFromJson(JsonNode jsonNode) {
    return new ForkTask();
  }

  private Task createMergeTaskFromJson(JsonNode jsonNode) {
    return new AndGatewayMerge();
  }

  private Task createDecisionTaskFromJson(JsonNode jsonNode) {
    DecisionTask task = new DecisionTask();
    JsonNode conditionsNode = jsonNode.findValue(JsonKeys.DECISION_CONDITIONS);
    if (conditionsNode != null && conditionsNode.isArray()) {
      Iterator<JsonNode> iter = conditionsNode.iterator();
      while (iter.hasNext()) {
        JsonNode entry = (JsonNode) iter.next();
        if (hasRequiredFields(entry, Arrays.asList(JsonKeys.DECISION_CONDITION_ID,
            JsonKeys.DECISION_CONDITION_INDEX, JsonKeys.CONDITION))) {
          task.addIndex(Integer.valueOf(entry.get(JsonKeys.DECISION_CONDITION_INDEX).asText()),
              entry.get(JsonKeys.DECISION_CONDITION_ID).asText(), entry.get(JsonKeys.CONDITION)
                  .asText());
        }
      }
    }

    return task;
  }

  /**
   * add by qinlihan
   * 
   * @param jsonNode
   * @return
   */
  protected RestTask createRestTaskFromJson(JsonNode restNode) {
    if (!hasRequiredFields(restNode, Arrays.asList(JsonKeys.REST_METHOD, JsonKeys.REST_URL))) {
      log.warn("Ignoring rest node: One of the fields '" + JsonKeys.REST_METHOD + "' or '"
          + JsonKeys.REST_URL + "' is missing");
      return null;
    }
    String accept = restNode.get(JsonKeys.REST_ACCEPT).asText();
    String methodType = restNode.get(JsonKeys.REST_METHOD).asText();
    String url = restNode.get(JsonKeys.REST_URL).asText();
    String contentType = restNode.get(JsonKeys.REST_CONTENT_TYPE).asText();

    RestTask restTask = new RestTask();
    restTask.setMethodType(methodType);
    restTask.setAccept(accept);
    restTask.setContentType(contentType);
    restTask.setUrl(url);
    restTask.setTaskTypeDetail(JsonKeys.TASK_TYPE_DETAIL_REST);

    JsonNode reqBody = restNode.get(JsonKeys.REST_REQUEST_BODY);
    if (null != reqBody) {
      restTask.setRequestBody(reqBody.toString());
    }

    List<Parameter> responseStatus = getParams(restNode.get(JsonKeys.REST_RESPONSE_STATUS));
    if (null != responseStatus && !responseStatus.isEmpty()) {
      restTask.setResponseStatus(responseStatus.get(0));
    }

    restTask.setPathParameters(getParams(restNode.get(JsonKeys.PATH_PARAMS)));
    List<Parameter> bodyParams = getParams(restNode.get(JsonKeys.BODY_PARAMS));
    rmEmptyQueryParams(bodyParams);
    restTask.setBodyParameters(bodyParams);
    List<Parameter> queryParams = getParams(restNode.get(JsonKeys.QUERY_PARAMS));
    rmEmptyQueryParams(queryParams);
    restTask.setQueryParameters(queryParams);

    if (hasOutput(restNode)) {
      String restName = restNode.get(JsonKeys.NAME).asText();
      String varName = restName.trim().replaceAll(" ", "_") + "Response";
      variables.add(new StringParameter(varName, null));
    }

    return restTask;
  }

  private void rmEmptyQueryParams(List<Parameter> queryParams) {
    for (Iterator<Parameter> iterator = queryParams.iterator(); iterator.hasNext();) {
      if (null == iterator.next().getValue()) {
        iterator.remove();
      }
    }
  }

  private boolean hasOutput(JsonNode json) {
    boolean result = false;
    JsonNode outputParams = json.get(JsonKeys.OUTPUT);
    if (outputParams != null) {
      Iterator<Map.Entry<String, JsonNode>> outputParamIter = outputParams.fields();
      if (outputParamIter.hasNext()) {
        result = true;
      }
    }

    return result;
  }

  private List<Parameter> getParams(JsonNode json) {
    List<Parameter> params = new ArrayList<Parameter>();
    if (json != null) {
      Iterator<Map.Entry<String, JsonNode>> paramIter = json.fields();
      while (paramIter.hasNext()) {
        Map.Entry<String, JsonNode> paramEntry = (Map.Entry<String, JsonNode>) paramIter.next();
        Parameter param = createParameterFromJson(paramEntry.getKey(), paramEntry.getValue());
        params.add(param);
      }
    }

    return params;
  }

  protected StartTask createStartTaskFromJson(JsonNode startTaskNode) {
    parseVariables(startTaskNode);
    return new StartTask();
  }

  protected EndTask createEndTaskFromJson(JsonNode endTaskNode) {
    return new EndTask();
  }

  protected ManagementTask createManagementTaskFromJson(JsonNode managementTaskNode) {

    if (!hasRequiredFields(managementTaskNode,
        Arrays.asList(JsonKeys.NODE_TEMPLATE, JsonKeys.NODE_OPERATION))) {
      log.warn("Ignoring mangement node: One of the fields '" + JsonKeys.NODE_TEMPLATE + "' or '"
          + JsonKeys.NODE_OPERATION + "' is missing");
      return null;
    }
    String nodeTemplate = managementTaskNode.get(JsonKeys.NODE_TEMPLATE).asText();
    String nodeInterfaceName = managementTaskNode.get(JsonKeys.NODE_INTERFACE_NAME).asText();
    String nodeOperation = managementTaskNode.get(JsonKeys.NODE_OPERATION).asText();

    log.debug("Creating management task with id '" + managementTaskNode.get(JsonKeys.ID)
        + "', name '" + managementTaskNode.get(JsonKeys.NAME) + "', node template '" + nodeTemplate
        + "', node operation '" + "', node operation '" + nodeOperation + "'");

    // add by qinlihan
    ManagementTask task = new ManagementTask();
    JsonNode operTypeJson = managementTaskNode.get(JsonKeys.OPERATION_TYPE);
    if (null != operTypeJson && JsonKeys.TASK_TYPE_DETAIL_REST.equals(operTypeJson.asText())) {
      task = createRestTaskFromJson(managementTaskNode);
      if (null == task) {
        return null;
      }
    } else {
      task.setTaskTypeDetail(JsonKeys.TASK_TYPE_DETAIL_IA);
      if (hasOutput(managementTaskNode)) {
        iaResponseVariables.add(managementTaskNode.get(JsonKeys.NAME).asText().trim()
            .replaceAll(" ", "_")
            + "Response");
      }
    }

    // ManagementTask task = new ManagementTask();
    task.setNodeTemplateId(QName.valueOf(nodeTemplate));
    task.setNodeOperation(nodeOperation);
    task.setInterfaceName(nodeInterfaceName);

    return task;
  }

  protected Parameter createParameterFromJson(String paramName, JsonNode paramNode) {

    if (!hasRequiredFields(paramNode, Arrays.asList(JsonKeys.TYPE, JsonKeys.VALUE))) {
      log.warn("Ignoring parameter node: One of the fields '" + JsonKeys.TYPE + "' or '"
          + JsonKeys.VALUE + "' is missing");
      return null;
    }
    String paramType = paramNode.get(JsonKeys.TYPE).asText();
    String paramValue = paramNode.get(JsonKeys.VALUE).asText();

    log.debug("Parsing JSON parameter node with of type '" + paramType + "' and value '"
        + paramValue + "'");

    Parameter param = null;
    switch (paramType) {
      case JsonKeys.PARAM_TYPE_VALUE_CONCAT:
        param = new ConcatParameter(); // TODO add concat operands
        break;
      case JsonKeys.PARAM_TYPE_VALUE_DA:
        param = new DeploymentArtefactParameter();
        break;
      case JsonKeys.PARAM_TYPE_VALUE_IA:
        param = new ImplementationArtefactParameter();
        break;
      case JsonKeys.PARAM_TYPE_VALUE_PLAN:
        param = new PlanParameter(); // TODO add task name
        break;
      case JsonKeys.PARAM_TYPE_VALUE_STRING:
        param = new StringParameter();
        break;
      case JsonKeys.PARAM_TYPE_VALUE_TOPOLOGY:
        // param = new TopologyParameter();
        param = new StringParameter();
        break;
      case JsonKeys.PARAM_TYPE_VALUE_EXPRESSION:
        param = new ExpressionParameter();
        break;
      default:
        log.warn("JSON parameter type '" + paramType + "' unknown");
        return null;
    }

    /* Set generic parameter attributes */
    param.setName(paramName.trim().replaceAll(" ", "_"));
    if (null != paramValue && !paramValue.isEmpty()) {
      param.setValue(paramValue);
    }

    if (null != paramNode.get(JsonKeys.EXTENSION)) {
      String extension = paramNode.get(JsonKeys.EXTENSION).asText();
      if (null != extension && !"".equals(extension)) {
        param.setExtension(extension);
      }
    }

    return param;

  }

  protected Set<String> extractNodeTargetIds(JsonNode node) {
    Set<String> linkTargetIds = new HashSet<String>();
    /* Look for the 'connections' element within the node or its children */
    JsonNode connectionsNode = node.findValue(JsonKeys.CONNECTIONS);
    /*
     * The connection node hosts an array of all outgoing connections to other nodes
     */
    if (connectionsNode != null && connectionsNode.isArray()) {
      Iterator<JsonNode> iter = connectionsNode.iterator();
      while (iter.hasNext()) {
        JsonNode connectionEntry = (JsonNode) iter.next();
        /*
         * Should always be true as the connection entry is the id of the target node
         */
        if (connectionEntry.isTextual()) {
          linkTargetIds.add(connectionEntry.asText());
        } else {
          // TODO warn
        }

      }
    } else {
      log.debug("Node with id '" + node.get(JsonKeys.ID) + "' has no connections to other nodes");
      return null;
    }
    return linkTargetIds;
  }

  protected boolean hasRequiredFields(JsonNode jsonNode, List<String> reqFieldNames) {
    Iterator<String> fieldNAmeIter = reqFieldNames.iterator();

    /* Returns false if one of the field names is missing */
    while (fieldNAmeIter.hasNext()) {
      String reqField = (String) fieldNAmeIter.next();
      if (jsonNode.get(reqField) == null) {
        return false;
      }

    }
    return true;
  }

  public List<Parameter> getVarList() {
    return this.variables;
  }

  public List<String> getIAVarList() {
    return this.iaResponseVariables;
  }
}
