package com.camunda.report.camundabackuputility;

import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.camunda.report.camundabackuputility.services.*;

@SpringBootApplication
public class CamundaBackupUtilityApplication {

	@Autowired
	CamundaBackupService camundaBackupService;

	public static void main(String[] args) throws IOException {

		SpringApplication.run(CamundaBackupUtilityApplication.class, args);

	}

	@EventListener(ApplicationReadyEvent.class)
	public void camundaBackup() {
		try {

			JSONArray searchAfterJsonArray = new JSONArray();

			System.out.println("Camunda backup utility started");

			WebClient.Builder builder = WebClient.builder();

			int pageNumer = 0;

			/* Fetching total number of completed or cancelled process instance before the given date */
			int totalInstance = camundaBackupService.fetchTotalInstanceCount(builder);

			while (pageNumer * 50 < totalInstance) {

				System.out.println("searchAfterJsonArray " + searchAfterJsonArray);

				/* Fetching 50 Instances detailed data here */
				JSONArray jsonArray = camundaBackupService.fetchProcessInstanceList(builder, searchAfterJsonArray);

				for (int i = 0; i < jsonArray.length(); i++) {

					JSONObject objectInArray = jsonArray.getJSONObject(i);

					JSONObject instanceObject = new JSONObject();
					
					/*

					 * Creating JSON with process instance , audit trial and variables data.
					 */
					instanceObject.put("flowNodes",
							camundaBackupService.fetchFlowNodesForInstance(builder, objectInArray.getString("id")));
					instanceObject.put("variables",
							camundaBackupService.fetchVariablesForInstance(builder, objectInArray.getString("id")));
					instanceObject.put("processInstance", objectInArray);
					
		
					/* Writing the final JSON into file here */
					camundaBackupService.writeDataToFile(objectInArray.getString("id"), instanceObject.toString());

					searchAfterJsonArray = objectInArray.getJSONArray("sortValues");

				}

				pageNumer = pageNumer + 1;

				System.out.println("pageNumer" + pageNumer);

			}

			System.out.println("Camunda backup utility completed successfully");

		} catch (Exception e) {

			System.out.println("Camunda backup utility failed " + e);

			e.printStackTrace();

		}

	}

}
