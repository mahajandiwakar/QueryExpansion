import java.util.ArrayList;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class SearchResults {
       
        // Array of results
        private ArrayList<ResultElement> arr = new ArrayList<ResultElement>();
        private int count = 0;
       
        /**
         * Constructor with result data
         */
        public SearchResults (JSONArray jsonArr) {
                for (int i=0; i<jsonArr.size(); i++) {
                        //URL
                        String temp = (jsonArr.get(i)).toString();
                        //System.out.println(temp);
                        temp = temp.replaceAll("\\\\/", "/");
                        int tempIndex = temp.indexOf("DisplayUrl");
                        String url = temp.substring(temp.indexOf(":", tempIndex)+2, temp.indexOf("ID", tempIndex)-3);
                        //System.out.println("Result "+(i+1)+":" + '\n' + "URL:  " + temp.substring(temp.indexOf(":", tempIndex)+2, temp.indexOf("ID", tempIndex)-3) );
                        //Title
                        tempIndex = temp.indexOf("Title");
                        String title = temp.substring(temp.indexOf(":", tempIndex)+2, temp.indexOf("}", tempIndex)-1);

                        //System.out.println("Title:  " + temp.substring(temp.indexOf(":", tempIndex)+2, temp.indexOf("}", tempIndex)-1) );
                        //Summary
                        tempIndex = temp.indexOf("Description");
                        String summary = temp.substring(temp.indexOf(":", tempIndex)+2, temp.indexOf("Url", tempIndex)-3);
                        //System.out.println("Summary:  " + temp.substring(temp.indexOf(":", tempIndex)+2, temp.indexOf("Url", tempIndex)-3) );
                        ResultElement elem = new ResultElement(url, title, summary, i);
                        arr.add(elem);
                }
                System.out.println(arr.size());
        }
       
        /**
         * Returns result nodes
         * @return
         */
        public ArrayList<ResultElement> getResultElements() {
                return arr;
        }
       
        public int getResultCount () {
                return arr.size();
        }
       
        /**
         * Returns result nodes marked relevant by user
         * @return relevant result nodes
         */
        public ArrayList<ResultElement> getRelevantResultElements() {
                ArrayList<ResultElement> relevantResults = new ArrayList<ResultElement> (arr.size());
                for (ResultElement r: arr) {
                        //System.out.print("node " + r.getDocId());                    
                        if (r.isRelevant()) {
                                //System.out.print(" is relevant");
                                relevantResults.add(r);
                        }
                        //System.out.println("");
                }
                return relevantResults;
        }
       
        /**
         * Prints the results and prompts for relevance judgment
         */
        public void getUserFeedback() {
               
                System.out.println("Total no of results : " + count);
                System.out.println("Bing Search Results:");
                System.out.println("=====================");
               
                Scanner in = new Scanner(System.in);
               
                // Iterate over the results
                int i = 0;
                for (ResultElement node : arr) {
                        System.out.println("Result "+ i);
                        System.out.println(node);
                        i++;
                        getRelevance(in, node); // Ask user if the result is relevant
                }
        }
       
        /**
         * Prompts user if result is relevant
         * @param in Scanner for getting user input
         * @param node The result node in question
         */
        public void getRelevance(Scanner in, ResultElement node) {
                System.out.println("Relevant (Y/N)?");
                String rel = in.nextLine();
               
                // Set the node's relevance
                node.setRelevance(rel);
        }
       
        /**
         * Calculates the precision score of results in array
         * @return a precision score
         */
        public float getPrecision() {
                float prec = 0;
               
                // Add up the number of relevant nodes
                for (ResultElement node : arr) {
                        if (node.isRelevant())
                                prec++;
                }
               
                // Divide by no. of results to get score
                return prec / arr.size();
        }
       
}

