import java.io.IOException;
import java.util.ArrayList;


public class ResultElement {

        private int docid = -1; // tracks the document id
        private String title;
        private String url;
        private String summary;
        private boolean relevant = false; // Tracks if the result is relevant
        private ArrayList<String> terms = new ArrayList<String>();

        /**
         * Constructor for result node
         */
        public ResultElement (String url, String title, String summary, int docid) {
                this.docid = docid;
                this.title = title;
                this.url = url;
                this.summary = summary;
                setTerms(); // create the keywords list
        }
       
        /**
         * Sets the relevance of a result
         */
        public void setRelevance (String response) {
                response = response.trim();
                if (response.startsWith("y") || response.startsWith("Y")) {
                        relevant = true;
                }
        }
       
        /**
         * Returns if a result is relevant or not
         * @return A boolean false or true
         */
        public boolean isRelevant () {
                return relevant;
        }
       
        /**
         * Returns result's url
         * @return
         */
        public String getUrl() {
                return url;
        }
       
        public String getSummary() {
                return summary;
        }
       
        public String getTitle() {
                return title;
        }

        public int getDocId() {
                return docid;
        }
       
        public ArrayList<String> getTerms() {
                return terms;
        }
       
        /**
         * Returns result's web page
         * @return
         */
        public String getWebPage() throws IOException{
                return BingTest.urlGet(url);
        }
       
        /**
         * Removes HTML code and replaces escaped characters
         * @param str A string from Yahoo
         * @return A clean string
         */
        public static String trim(String str) {
                String newStr = null;

                newStr = str.replaceAll("\\<.*?\\>", "");
                newStr = newStr.replaceAll("\\\\/", "/");
                newStr = newStr.replaceAll("\\\\\"", "\"");

                return newStr;
        }
       
        /**
         * Tokenize the title and summary
         * Store the terms in keywords
         */
        public void setTerms () {
                String text = "";
                try { text = trim(getWebPage()); }
                catch (IOException e) { text = title + " " + summary; }
                String[] words = text.split(" ");
                for (int pos = 0; pos < words.length; pos++) {
                        String word = words[pos];
                        word = word.replaceAll("^\\W+", ""); // trim non-word characters before word
                        word = word.replaceAll("\\W+$", ""); // trim non-word characters after word
                        word = word.toLowerCase();  // lower case the word
                        if (word.equals("")) continue;
                        terms.add(word);
                }
        }

        /**
         * Prints the node
         */
        public String toString() {
                String str = String.format("[\n URL: %s\n Title: %s\n Summary: %s\n]\n", url, title, summary);
                return str;
        }
}
