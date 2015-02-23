import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//Download and add this library to the build path.
import org.apache.commons.codec.binary.Base64;

public class BingTest {

	public static void main(String[] args) throws IOException {
		String bingUrl = "https://api.datamarket.azure.com/Bing/Search/Web?Query=%27"+args[0]+"%27&$top=10&$format=JSON";
		//Provide your account key here. 
		String accountKey = "6ogFrWjyeWmPufqcZFqcmTMvJDxvznvQIwGYXDwvZQo";
		
		byte[] accountKeyBytes = Base64.encodeBase64((accountKey + ":" + accountKey).getBytes());
		String accountKeyEnc = new String(accountKeyBytes);

		URL url = new URL(bingUrl);
		URLConnection urlConnection = url.openConnection();
		urlConnection.setRequestProperty("Authorization", "Basic " + accountKeyEnc);
				
		InputStream inputStream = (InputStream) urlConnection.getContent();		
		byte[] contentRaw = new byte[urlConnection.getContentLength()];
		inputStream.read(contentRaw);
		String content = new String(contentRaw);

		//System.out.println(content);
		JSONParser parser = new JSONParser();		
	  	Object obj=JSONValue.parse(content);
  		JSONObject object = (JSONObject)obj;
		JSONArray array = (JSONArray)((JSONObject)object.get("d")).get("results");
		System.out.println("Query: " + bingUrl);
		System.out.println("Number of results: " + array.size() + '\n' + "Bing Search Results:" + '\n' + "=================");
		
		for (int i=0; i<array.size(); i++) {
			//URL
			String temp = (array.get(i)).toString();
			int tempIndex = temp.indexOf("DisplayUrl");
			System.out.println("Result "+(i+1)+":" + '\n' + "URL:  " + temp.substring(temp.indexOf(":", tempIndex)+1, temp.indexOf("ID", tempIndex)-2) );
			//Title
			tempIndex = temp.indexOf("Title");
			System.out.println("Title:  " + temp.substring(temp.indexOf(":", tempIndex)+2, temp.indexOf("}", tempIndex)-1) );
			//Summary
			tempIndex = temp.indexOf("Description");
			System.out.println("Summary:  " + temp.substring(temp.indexOf(":", tempIndex)+2, temp.indexOf("Url", tempIndex)-3) );
			System.out.println();
		}
			
	}

}