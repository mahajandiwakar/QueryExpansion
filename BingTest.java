import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.net.*;
import java.io.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//Download and add this library to the build path.
import org.apache.commons.codec.binary.Base64;

public class BingTest {

	public static void main(String[] args) throws IOException {

		String response, query = args[1];

		float precisionGoal = 0;
        try {
                    precisionGoal = Float.parseFloat(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("Precision must be a real number between 0 and 1");
            System.exit(1);
        }

		int[] userResponse = new int[10];
		String bingUrl = "https://api.datamarket.azure.com/Bing/Search/Web?Query=%27"+query+"%27&$top=10&$format=JSON";
		//Provide your account key here. 
		String accountKey = "6ogFrWjyeWmPufqcZFqcmTMvJDxvznvQIwGYXDwvZQo";
		
		byte[] accountKeyBytes = Base64.encodeBase64((accountKey + ":" + accountKey).getBytes());
		String accountKeyEnc = new String(accountKeyBytes);

		JSONParser parser = new JSONParser();		
	  	Object obj;
  		JSONObject object;
		JSONArray array;
		
		AugmentQuery q = new AugmentQuery(query);

		for (query = query ; ; query = q.augment())	 {
			bingUrl = "https://api.datamarket.azure.com/Bing/Search/Web?Query=%27"+query+"%27&$top=10&$format=JSON";
			URL url = new URL(bingUrl);
			URLConnection urlConnection = url.openConnection();
			urlConnection.setRequestProperty("Authorization", "Basic " + accountKeyEnc);
					
			InputStream inputStream = (InputStream) urlConnection.getContent();		
			byte[] contentRaw = new byte[urlConnection.getContentLength()];
			inputStream.read(contentRaw);
			String content = new String(contentRaw);

			//System.out.println(content);
			//Scanner input = new Scanner(System.in);

			parser = new JSONParser();		
		  	obj=JSONValue.parse(content);
	  		object = (JSONObject)obj;
			array = (JSONArray)((JSONObject)object.get("d")).get("results");
			
			SearchResults searchResults = new SearchResults (array);
			searchResults.getUserFeedback();
			float precision = searchResults.getPrecision();         // Calculate result precision

            // Return feedback summary to user
            System.out.println("======================");
            System.out.println("FEEDBACK SUMMARY");
            System.out.println("Query " + query);
            System.out.println("Precision " + precision);

            // Check the precision result
            if (precision < precisionGoal) {
                    // No relevant results
                    if (precision <= 0) {
                            System.out.println("Below desired precision, but can no longer augment the query");
                            break;
                    }
                    // Below desired precision, expand query
                    else {
                            System.out.println("Still below the desired precision of " + precisionGoal);
                            q.updateResult(searchResults);
                    }
            // Desired precision reached
            } else {
                    System.out.println("Desired precision reached, done");
                    break;
            }


		}	
		
	}


	public static String urlGet (String urlstr) throws MalformedURLException, IOException{
        URL url = new URL(urlstr);
        URLConnection urlconn = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(urlconn.getInputStream()));
        String inputLine;
        StringBuilder sb = new StringBuilder(1000);
        while ((inputLine = in.readLine()) != null) {
            sb.append(inputLine + "\n");
        }
        in.close();
        return sb.toString();
        }



}