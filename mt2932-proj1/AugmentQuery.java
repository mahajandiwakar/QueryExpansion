import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Vector;
import java.util.HashMap;

/*
 * This class implements the algorithm for query expansion
 * Result docs are indexed, analyzed and the query is augmented
*/
public class AugmentQuery {
       
        private String queryString;
        private SearchResults result = null;
        private String lastQuery;
        private InvertedIndex invIndex;
       

        public AugmentQuery (String initialQuery) {
                queryString = initialQuery;
                invIndex = new InvertedIndex();
        }
       
        // results provided to the class
        public void setResult (SearchResults r) {
                result = r;
        }
        
        // result are passed to build an inverted index
        public void indexResults () {
                System.out.println("Indexing results ....");
                invIndex.clear();
                ArrayList<ResultElement> resultList = result.getResultElements();
                for (ResultElement r: resultList) {
                        r.parseResult();
                        invIndex.addDocument(r);
                }
        }
       
        // rocchios algorithm used to augment and improve the initial query
        public String augment() {
                lastQuery = queryString;
               
                indexResults();
                
                String augmentBy = runRocchio();
                System.out.println("Augmented By: " + augmentBy);
                queryString = lastQuery + " " + augmentBy;
                return queryString.replace(" ", "+");
        }


        // centroids computed for cluster of relevant and non-relevant documents respectively
        public String runRocchio() {
                ArrayList<DocVector> rdocs = new ArrayList<DocVector> ();
                double N = 0, b = 0.8;
                for (ResultElement r: result.getRelevantResultElements()) {
                        DocVector d = new DocVector(r, invIndex);
                        rdocs.add(d);
                        N++;
                }
                N = b/N;

                DocVector rCentroid = new DocVector();
                for (DocVector d: rdocs) {
                        for (VectorElem v: d.getTerms()) {
                                String term = v.getTerm();
                                double weight = v.getNormalizedWeight();
                                rCentroid.addTerm(term, weight/N);
                        }
                }
                
                // centroid of non-relevant cluster computed and then weighted before being subtracted from relevant centroid
                ArrayList<DocVector> idocs = new ArrayList<DocVector> ();
                double NR = 0, g = 0.15;
                for (ResultElement r: result.getNRelevantResultElements()) {
                        DocVector d = new DocVector(r, invIndex);
                        idocs.add(d);
                        NR++;
                }
                N = g/NR;

                for (DocVector d: idocs) {
                        for (VectorElem v: d.getTerms()) {
                                String term = v.getTerm();
                                double weight = v.getNormalizedWeight();
                                rCentroid.addTerm(term, -weight/N);
                        }
                }
                //rCentroid.normalize();
               
                // result is sorted to pick the highest weighted terms for next iteration
                ArrayList<VectorElem> termList = rCentroid.getTerms();
                Collections.sort(termList);

                // top weighted terms checked for duplication by comparing with last query
                HashSet<String> lastQueryTerms = new HashSet<String>(Arrays.asList(lastQuery.split(" ")));

                double diffThreshold = 0.2;
                String augmentBy = null;
                double topWeight = 0;
                for (int i=0; i < termList.size(); i++) {
                        VectorElem v = termList.get(i);
                        String term = v.getTerm();
                        double weight = v.getWeight();
                       
                        if (lastQueryTerms.contains(term)) {
                                continue;
                        }

                        if (augmentBy == null) {
                                augmentBy = term;
                                topWeight = weight;
                        } else { // if top two terms are less than 20% apart then both added, otherwise only one
                                double diff = (topWeight - weight) / topWeight;
                                if (diff < diffThreshold) {
                                        augmentBy = augmentBy + " " + term;
                                }
                                break;
                        }                      
                }
                return augmentBy;

        }
       
}
