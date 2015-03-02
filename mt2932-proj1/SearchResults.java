import java.util.ArrayList;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/* 
 * class to parse Bing response
 * and get user feedback
 */ 
public class SearchResults {
       
        // Array of results
        private ArrayList<ResultElement> arr = new ArrayList<ResultElement>();
        private float numRelevant;

        // parse search results for relevant information and store it in an array
        public SearchResults (JSONArray jsonArr) {
                for (int i=0; i<jsonArr.size(); i++) {
                        //URL
                        String temp = (jsonArr.get(i)).toString();

                        temp = temp.replaceAll("\\\\/", "/"); // remove escape characters
                        int tempIndex = temp.indexOf("DisplayUrl");
                        String url = temp.substring(temp.indexOf(":", tempIndex)+2, temp.indexOf("ID", tempIndex)-3);

                        //Title
                        tempIndex = temp.indexOf("Title");
                        String title = temp.substring(temp.indexOf(":", tempIndex)+2, temp.indexOf("}", tempIndex)-1);

                        //Summary
                        tempIndex = temp.indexOf("Description");
                        String summary = temp.substring(temp.indexOf(":", tempIndex)+2, temp.indexOf("Url", tempIndex)-3);

                        ResultElement elem = new ResultElement(url, title, summary, i);
                        arr.add(elem);
                }

        }

        public void getUserFeedback() {
                numRelevant = 0;
                System.out.println("Total no of results : " + arr.size());
                System.out.println("Bing Search Results:");
                System.out.println("=====================");
               
                Scanner in = new Scanner(System.in);
               
                int i = 1;
                for (ResultElement node : arr) {
                        System.out.println("Result "+ i);
                        System.out.println(node);
                        i++;
                        System.out.println("Relevant (Y/N)?");
                        String rel = in.nextLine();
                        if (node.setRelevance(rel))
                                numRelevant++;
                }
        }

        public float getPrecision() {
                return numRelevant / arr.size();
        }

        public ArrayList<ResultElement> getResultElements() {
                return arr;
        }
       
        //function to retrieve relevant results
        public ArrayList<ResultElement> getRelevantResultElements() {
                ArrayList<ResultElement> relevantResults = new ArrayList<ResultElement> (arr.size());
                for (ResultElement r: arr) {
                        if (r.isRelevant()) {
                                relevantResults.add(r);
                        }
                }
                return relevantResults;
        }

        //function to retrieve non relevant results
        public ArrayList<ResultElement> getNRelevantResultElements() {
                ArrayList<ResultElement> nonRelevantResults = new ArrayList<ResultElement> (arr.size());
                for (ResultElement r: arr) {
                        if (!r.isRelevant()) {
                                nonRelevantResults.add(r);
                        }
                }
                return nonRelevantResults;
        }
}

