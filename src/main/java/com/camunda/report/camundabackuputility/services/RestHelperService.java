package com.camunda.report.camundabackuputility.services;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RestHelperService {

	@Value("${endDateAfter}")
	private String endDateAfter;

	@Value("${endDateBefore}")
	private String endDateBefore;

	public String prepareProcessInstancesTotalCountPayload(JSONArray searchAfterJsonArray) {

		JSONObject rootJSON = new JSONObject();

		JSONObject queryJSON = new JSONObject();
		queryJSON.put("completed", true);
		queryJSON.put("finished", true);
		queryJSON.put("canceled", true);
		queryJSON.put("endDateBefore", endDateBefore + "T23:59:59.000-0400");

		JSONObject sortingJSON = new JSONObject();
		sortingJSON.put("sortBy", "startDate");
		sortingJSON.put("sortOrder", "desc");

		rootJSON.put("query", queryJSON);
		rootJSON.put("sorting", sortingJSON);
		rootJSON.put("pageSize", 50);

		if (searchAfterJsonArray != null && searchAfterJsonArray.length() > 0) {

			JSONArray searchAfterJSON = new JSONArray();
			searchAfterJSON = searchAfterJsonArray;

			rootJSON.put("searchAfter", searchAfterJSON);

		}

		return rootJSON.toString();

	}

	public String prepareFlowNodePayload(String instanceID) {

		JSONObject rootJSON = new JSONObject();

		JSONObject queryJSON = new JSONObject();
		queryJSON.put("processInstanceId", instanceID);
		queryJSON.put("treePath", instanceID);
		queryJSON.put("pageSize", 50);
		
		JSONArray rootArrayJSON = new JSONArray();
		rootArrayJSON.put(queryJSON);
		
		rootJSON.put("queries", rootArrayJSON);

		return rootJSON.toString();

	}

	public String prepareVariablesPayload(String instanceID) {
		
		JSONObject rootJSON = new JSONObject();
		rootJSON.put("scopeId", instanceID);

		return rootJSON.toString();

	}

}
