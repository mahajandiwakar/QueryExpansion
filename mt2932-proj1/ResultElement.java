import java.io.IOException;
import java.util.ArrayList;

/*
 * Each response is stored in one ResultElement
 * details of each result stored here
*/
public class ResultElement {

        private int docid = -1;
        private boolean relevant = false;
        private String title, url, summary;
        private ArrayList<String> terms = new ArrayList<String>();

        public ResultElement (String url, String title, String summary, int docid) {
                this.title = title;
                this.url = url;
                this.summary = summary;
                this.docid = docid;
                parseResult();
        }

        public int getDocId() {
                return docid;
        }
       
        public ArrayList<String> getTerms() {
                return terms;
        }

        // relevance to user
        public boolean setRelevance (String response) {
                response = response.trim();
                if (response.startsWith("y") || response.startsWith("Y")) {
                        relevant = true;
                        return true;
                }
                return false;
        }
       
        public boolean isRelevant () {
                return relevant;
        }

        // function to parse and retrieve words present in the result
        public void parseResult () {
                String text = title + " " + summary;
                
                String[] words = text.split(" ");
                for (int i = 0; i < words.length; i++) {
                        String temp = words[i];
                        temp = temp.replaceAll("^\\W+", "");
                        temp = temp.replaceAll("\\W+$", "");
                        temp = temp.toLowerCase();
                        if (temp.equals("")) continue;
                        terms.add(temp);
                }
        }

        public String toString() {
                String str = String.format("[\n URL: %s\n Title: %s\n Summary: %s\n]\n", url, title, summary);
                return str;
        }
}
