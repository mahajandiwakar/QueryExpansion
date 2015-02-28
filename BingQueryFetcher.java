import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

public class BingQueryFetcher {
	
	public static final float REQUIRED_PRECISION=1.0f;
	public static final String BING_KEY="6ogFrWjyeWmPufqcZFqcmTMvJDxvznvQIwGYXDwvZQo";
	public static Set<String> STOPWORDS = new HashSet<String>();
	
	public static List<String> dictionary;
	
	public static void init() throws IOException{
		BufferedReader reader  = new BufferedReader( new FileReader("StopWords.txt"));
		String s= "";
		while((s=reader.readLine())!=null){
			STOPWORDS.add(s.trim().toLowerCase());
		}
		reader.close();
	}
	
	public static void buildDictionary(JSONArray array){
		Set<String> set = new HashSet<String>();
		for (int i=0; i<array.size(); i++) {
			//URL
			JSONObject resultItem= (JSONObject)array.get(i);
			String desc = (String)resultItem.get("Description");
			String title = (String)resultItem.get("Title");
			String[] strs =getWordsFromText((desc+" "+title));
			set.addAll(Arrays.asList(strs));
		}
		set.removeAll(STOPWORDS);
		dictionary = new ArrayList<String>(set);
	}
	
	
	public static String[] getWordsFromText(String s){
		String str = s.toLowerCase().replaceAll("[!?,]", "");
		return str.split("\\s+");
	}
	
	public static JSONArray getResults(String query) throws IOException{
		String bingUrl = "https://api.datamarket.azure.com/Bing/Search/Web?Query=%27"+query+"%27&$top=10&$format=JSON";
		System.out.println("Query: " + bingUrl);
		byte[] accountKeyBytes = Base64.encodeBase64((BING_KEY + ":" + BING_KEY).getBytes());
		String accountKeyEnc = new String(accountKeyBytes);

		URL url = new URL(bingUrl);
		URLConnection urlConnection = url.openConnection();
		urlConnection.setRequestProperty("Authorization", "Basic " + accountKeyEnc);
				
		InputStream inputStream = (InputStream) urlConnection.getContent();		
		byte[] contentRaw = new byte[urlConnection.getContentLength()];
		inputStream.read(contentRaw);
		String content = new String(contentRaw);
		JSONParser parser = new JSONParser();		
	  	Object obj=JSONValue.parse(content);
  		JSONObject object = (JSONObject)obj;
		JSONArray array = ((JSONArray)((JSONObject)object.get("d")).get("results"));
		
		return array;
		
	}
	
	public static int[] getDocVector(String s){
		String[] wordsFromText = getWordsFromText(s);
		int[] vector = new int[dictionary.size()];
		for (String word : wordsFromText) {
			vector[dictionary.indexOf(word)]++;
		}
		return vector;
	}
	
	public static String getStringFromVector(int [] vector){
		
		return "";
	}
	
	
	public static String runRocchio(String query,int[] userResponse, JSONArray results,float alpha,float beta, float gamma){
		int[] queryVector = getDocVector(query);
		
	}
	
    public static void main(String[] args) throws IOException {

    	String response;
		int[] userResponse = new int[10];
		if(args.length ==0 ){
			args= new String[1];
			args[0]="test";
		}
			
		//System.out.println(array);
		Scanner input = new Scanner(System.in);
		String query = args[0];
		float currPrecision=0.0f;
		int noOfIteration=0;
		while(true){
			noOfIteration++;
			JSONArray array = getResults(query);
			
			System.out.println("Number of results: " + array.size() + '\n' + "Bing Search Results:" + '\n' + "=================");
			
		for (int i=0; i<array.size(); i++) {
			//URL
			
			String temp = (array.get(i)).toString();
			temp = temp.replaceAll("\\\\/", "/");
			int tempIndex = temp.indexOf("DisplayUrl");
			System.out.println("Result "+(i+1)+":" + '\n' + "URL:  " + temp.substring(temp.indexOf(":", tempIndex)+2, temp.indexOf("ID", tempIndex)-3) );
			//Title
			tempIndex = temp.indexOf("Title");
			System.out.println("Title:  " + temp.substring(temp.indexOf(":", tempIndex)+2, temp.indexOf("}", tempIndex)-1) );
			//Summary
			tempIndex = temp.indexOf("Description");
			System.out.println("Summary:  " + temp.substring(temp.indexOf(":", tempIndex)+2, temp.indexOf("Url", tempIndex)-3) );
			System.out.println("Relevant? (Y/N)");
			response = input.nextLine();
			response = response.trim();
			if (response.startsWith("y") || response.startsWith("Y")) {
				userResponse[i] = 1;
				currPrecision++;
			}
			else
				userResponse[i] = 0;
			System.out.println();
		}
		
		if(currPrecision>=REQUIRED_PRECISION)
			break;
		else{
			currPrecision=0.0f;
			init();
			buildDictionary(array);
			query = runRocchio(args[0], userResponse, array, 1, 0.75f, 0.15f);
		}
	}

    }

}
