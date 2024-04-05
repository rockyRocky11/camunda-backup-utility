package com.camunda.report.camundabackuputility.services;

import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class CamundaBackupService {
	
	@Value("${camundaHosturl}")
	private String camundaHostName;

	@Value("${fileStoragePath}")
	private String fileStoragePath;

	@Autowired
	RestCallService restCallService;

	@Autowired
	RestHelperService restHelperService;

	/*

	 * Fetching total number of completed or cancelled process instance before the given date
	 */
	public int fetchTotalInstanceCount(WebClient.Builder builder) throws Exception {
		
		String totalCountResponse = restCallService.makePOSTCall(builder,
				restHelperService.prepareProcessInstancesTotalCountPayload(new JSONArray()),
				camundaHostName + "/api/process-instances");

		JSONObject totalCountJson = new JSONObject(totalCountResponse);
		
		if(totalCountJson.getInt("totalCount") == 0  || totalCountJson.getInt("totalCount") < 0) {
			
			System.out.println("There is no process instance available for this search criteria ");
			
			throw new Exception();
			
		}
		
		return totalCountJson.getInt("totalCount");
		
	}
	
	/*

	 * Fetching process instance list with start date, end date and other information.
	 */
	public JSONArray fetchProcessInstanceList(WebClient.Builder builder, JSONArray searchAfterJsonArray)  {
		
		String processInstanceReponse = restCallService.makePOSTCall(builder,
				restHelperService.prepareProcessInstancesTotalCountPayload(searchAfterJsonArray),
				camundaHostName + "/api/process-instances");

		JSONObject processInstanceJson = new JSONObject(processInstanceReponse);

		JSONArray jsonArray = processInstanceJson.getJSONArray("processInstances");
		
		return jsonArray;
		
	}
	
	/*

	 * Fetching audit trail for the given instance.
	 */
	public JSONObject fetchFlowNodesForInstance(WebClient.Builder builder, String instanceId)  {
		
		String flowNodeReponse = restCallService.makePOSTCall(builder,
				restHelperService.prepareFlowNodePayload(instanceId),
				camundaHostName + "/api/flow-node-instances");

		JSONObject flowNodes = new JSONObject(flowNodeReponse);
		
		return flowNodes;
		
	}
	
	/*

	 * Fetching list variables for the given instance.
	 */
	public JSONArray fetchVariablesForInstance(WebClient.Builder builder, String instanceId)  {
		
		String fetchVariableReponse = restCallService.makePOSTCall(builder,
				restHelperService.prepareVariablesPayload(instanceId),
				camundaHostName + "/api/process-instances/" + instanceId + "/variables");

		JSONArray variables = new JSONArray(fetchVariableReponse);
		
		return variables;
		
	}
	
	/*

	 * Writing the data into file.
	 */
	public void writeDataToFile(String instanceID, String data) throws IOException {

		try {

			ObjectMapper objectMapper = new ObjectMapper();
			Object jsonObject = objectMapper.readValue(data, Object.class);
			String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);

			FileWriter fileWriter = new FileWriter(fileStoragePath + instanceID + ".txt");
			fileWriter.write(prettyJson);
			fileWriter.flush();
			fileWriter.close();

			System.out.println("File successfully created for : " + instanceID);

		} catch (IOException e) {

			System.out.println("File creation failed for : " + instanceID);

			throw new IOException();

		}

	}
	
}
