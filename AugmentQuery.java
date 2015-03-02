import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Vector;
import java.util.HashMap;

public class AugmentQuery {
       
        private ArrayList<String> queryHistory = new ArrayList<String>();
        private SearchResults result = null;
        private String lastQuery;
        private InvertedIndex invIdx = new InvertedIndex();
       
        /**
         * Constructor
         * @param initalQuery The user's search string
         */
        public AugmentQuery (String initialQuery) {
                queryHistory.add(initialQuery);
        }
       
        
        public void updateResult (SearchResults r) {
                result = r;
        }
       

        
        public void buildIndex () {
                System.out.println("Indexing results ....");
                invIdx.clear();
                ArrayList<ResultElement> resultList = result.getResultElements();
                for (ResultElement rn: resultList) {
                        rn.setTerms();
                        invIdx.addDocument(rn);
                }
        }
       
        
        public String getNormalizedAugment() {
                // Build the TFIDF term vectors for the relevant documents
                ArrayList<TermVector> docVectors = new ArrayList<TermVector> ();
                for (ResultElement rn: result.getRelevantResultElements()) {
                        TermVector tv = new TermVector(rn, invIdx); // term vector for a document
                        docVectors.add(tv);
                }

                // Original algorithm truncates each vector to 50 highest scoring terms
                // Our vectors are short, no need to truncate
               
                // Find the Centroid of the L2 normalized term vectors
                // Centroid is a vector of all distinct terms in the relevant documents
                // The term weight is the sum of the normalized weight of the term in all relevant docs
                TermVector centroid = new TermVector();
                for (TermVector tv: docVectors) {
                        for (TermNode tn: tv.getTerms()) {
                                String term = tn.getTerm();
                                double weight = tn.getNormalizedWeight();
                                centroid.addTerm(term, weight);
                        }
                }
               
                // Normalize the Centroid
                centroid.l2normalize();
               
                // Sort descending order by normalized weight
                ArrayList<TermNode> termList = centroid.getTerms();
                Collections.sort(termList);

                // quick way to index all the previously used search terms
                // check the new found terms against previous query to make sure there are no duplicates
                HashSet<String> lastQueryWords = new HashSet<String>(Arrays.asList(lastQuery.split(" ")));

                // augment with up to 2 top tf-idf score terms
                double weightDiffThreshold = 0.2; // weight is less than 20% difference
                String augmentStr = null;
                double augmentWeight = 0;
                for (int i=0; i < termList.size(); i++) {
                        TermNode t = termList.get(i);
                        String term = t.getTerm();
                        double weight = t.getWeight();
                       
                        // do not re-query terms that are in the previous query
                        if (lastQueryWords.contains(term)) {
                                System.out.println("term "+ term +" ("+weight+") is in previous query. Skipping.");
                                continue;
                        }
                        //System.out.println("DEBUG: " + term + " weight=" + t.getWeight());
                       
                        // if the top 2 terms are close together in the score
                        // use both terms in the next search
                        if ((augmentStr != null) && (weight > 0)) {
                                // tie break results that are close
                                double weightDiff = (augmentWeight - weight) / augmentWeight;
                                if (weightDiff < weightDiffThreshold) {
                                        augmentStr += " " + term;
                                }
                                break;
                        } else {
                                augmentStr = term;
                                augmentWeight = weight;
                        }                      
                }
                return augmentStr;

        }

        
        public String augment() {
                lastQuery = queryHistory.get(queryHistory.size()-1);
               
                buildIndex();
                
                String augment2 = getNormalizedAugment(); // Normalized
                System.out.println("Augmented By: " + augment2);
                augment2 = augment2.replace(" ", "+");
                String newQuery = lastQuery + "+" + augment2;
                return newQuery;
        }
       
}
