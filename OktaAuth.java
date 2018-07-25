/* THIS IS SAMPLE CODE PROVIDED WITH THE UNDERSTANDING THAT
 * IT IS TO BE USED FOLLOWING LICENSE COMPLIANCE. THE SAMPLE
 * CAN ONLY BE USED WITH A TRIAL OR LICENSED VERSION OF 
 * HYBRID DATA PIPELINE. FOR MORE INFORMATION ON HYBRID
 * DATA PIPELINE, PLEASE VISIT:
 * https://www.progress.com/cloud-and-hybrid-data-integration
 * 
 * Hybrid Data Pipeline External Authentication Example
 * 
 * Developer: Julien Mansier
 * Date: 7/25/2018
 * 
 * Note from the developer: I am no programming expert, so 
 * please forgive any programming mistakes. 
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ddtek.cloudservice.plugins.auth.javaplugin.JavaAuthPluginException;

public class OktaAuth implements com.ddtek.cloudservice.plugins.auth.javaplugin.JavaAuthPluginInterface{
	
	Logger oktaLogger;
	String apiURL, apiToken;

	public boolean authenticate(String arg0, String arg1, String arg2){

	
		//Using the HDP Logger
		oktaLogger.log(Level.CONFIG, "Authenticate Called.\n");
		
		
		String response ="";
		JSONArray jsonResp;
		JSONObject jsonBody;
		String status = "";
		String id = "";
		
		
		
		try {
			// apiURL is passed by HDP and is set in the init method
			URL userUrl = new URL(apiURL+"/api/v1/users?q="+arg0);

			// Standard REST call
			HttpsURLConnection conn = (HttpsURLConnection) userUrl.openConnection();
			conn.setDoInput(true);
			conn.setRequestMethod("GET");
			conn.setDoOutput(false);
			//conn.setInstanceFollowRedirects(false);
			conn.addRequestProperty("Accept", "Application/JSON");
			conn.addRequestProperty("Content-Type", "Application/JSON");
			conn.addRequestProperty("Authorization", "SSWS "+ apiToken);
			conn.connect();

			
			int statusCode = conn.getResponseCode();
			
			// We accept any response except 400 and up
			if(statusCode >= 200 && statusCode < 400) {
	     		BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

	     		
	     		String output;
	     		while ((output = br.readLine()) != null) {
	     			response+=output;
	     		}

	     		jsonResp = new JSONArray(response);
	     		
	     		conn.disconnect();
	     		
	     		if(jsonResp.length() > 0) {
	     			status=jsonResp.getJSONObject(0).getString("status");
	     			id=jsonResp.getJSONObject(0).getString("id");
	     		}
	     		else {
	     			// If no response then user doesn't exist and/or has been deactivated
	     			oktaLogger.log(Level.CONFIG, "No response. Return false.\n");
	     			return false;
	     		}
	     		
	     		
			}
			else {
				// If Status Codee 400+, log which one it was.
				oktaLogger.log(Level.CONFIG, "Status Code:"+statusCode+". Return false.\n");
	     		return false;
			}
			
		
			
		} catch (MalformedURLException e) {

			oktaLogger.log(Level.CONFIG, "URLException: "+e.toString()+"\n");
			return false;
		} catch (IOException e) {

			oktaLogger.log(Level.CONFIG, "IOException: "+e.toString()+"\n");
			return false;
		}

	
		
		
		
		if(status.equalsIgnoreCase("ACTIVE") || status.equalsIgnoreCase("PROVISIONED")) {
			// We are ok to validate both Active users and Provisioned users
			oktaLogger.log(Level.CONFIG, "User active. Return true.\n");
			return true;
		}
		else {
			// This is basically a catch all
			oktaLogger.log(Level.CONFIG, "User not active. Return false.\n");
			return false;
		}
	

	}


	public void destroy() {
		System.out.println("destroy() called");
		
	}


	public void init(HashMap<String, Object> arg0, Logger arg1) {
		// Grab the URL and API token from the HDP attributes
		apiURL = arg0.get("url").toString();
		apiToken = arg0.get("token").toString();
		oktaLogger = arg1;
		
		oktaLogger.log(Level.CONFIG, "init() called.\n");
		oktaLogger.log(Level.CONFIG, "url = "+apiURL+"\n");
	}

}
