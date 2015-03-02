import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.*;
import java.io.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.apache.commons.codec.binary.Base64;

public class BingTest {

	public static void main(String[] args) throws IOException {
		if (args.length < 3 || args.length > 3) {
            System.out.println("Invalid Arguments. Expecting three Arguments");
            System.exit(1);
        }

		String response, query = args[2];

		float precisionGoal = 0;
        try {
            precisionGoal = Float.parseFloat(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Precision must be a real number between 0 and 1");
            System.exit(1);
        }


        //formulating query structure for bing
		int[] userResponse = new int[10];
		String bingUrl = "https://api.datamarket.azure.com/Bing/Search/Web?Query=%27"+query+"%27&$top=10&$format=JSON";
		//account key. 
		String accountKey = args[0];//"6ogFrWjyeWmPufqcZFqcmTMvJDxvznvQIwGYXDwvZQo";
		
		byte[] accountKeyBytes = Base64.encodeBase64((accountKey + ":" + accountKey).getBytes());
		String accountKeyEnc = new String(accountKeyBytes);

		JSONParser parser = new JSONParser();		
	  	Object obj;
  		JSONObject object;
		JSONArray array;
		
		AugmentQuery q = new AugmentQuery(query);

		for (query = query ; ; query = q.augment())	 { // augment words to search indefinitely
			query = query.replace(" ", "+");
			bingUrl = "https://api.datamarket.azure.com/Bing/Search/Web?Query=%27"+query+"%27&$top=10&$format=JSON";
			URL url = new URL(bingUrl);
			URLConnection urlConnection = url.openConnection();
			urlConnection.setRequestProperty("Authorization", "Basic " + accountKeyEnc);
					
			InputStream inputStream = (InputStream) urlConnection.getContent();		
			byte[] contentRaw = new byte[urlConnection.getContentLength()];
			inputStream.read(contentRaw);
			String content = new String(contentRaw);

			parser = new JSONParser();		
		  	obj=JSONValue.parse(content);
	  		object = (JSONObject)obj;
			array = (JSONArray)((JSONObject)object.get("d")).get("results");
			
			SearchResults searchResults = new SearchResults (array); // store the results from bing
			searchResults.getUserFeedback(); // get feedback from user on relevance
			float precision = searchResults.getPrecision();

			// provide feedback to the user
            System.out.println("======================");
            System.out.println("FEEDBACK SUMMARY");
            System.out.println("Query " + query.replace("+", " "));
            System.out.println("Precision " + precision);

            if (precision < precisionGoal) {
                    if (precision <= 0) { // 0 precision. No information available to augment.
                            System.out.println("Below desired precision, but cannot augment the query");
                            break;
                    } else {
                            System.out.println("Still below the desired precision of " + precisionGoal);
                            q.setResult(searchResults); // need to improve precision. iterate
                    }
            } else {
                    System.out.println("Desired precision reached, done"); //desired precision reached
                    break;
            }
		}			
	}

}