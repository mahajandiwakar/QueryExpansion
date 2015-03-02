import java.util.*;

//class to store documents in vector form
public class DocVector {
        private HashMap<String,VectorElem> tfidfVector = new HashMap<String,VectorElem>();
        private double norm = 0;

        public DocVector () {
               
        }
       
       // constructor
        public DocVector (ResultElement r, InvertedIndex invIndex) {
                buildDocumentVector(r,invIndex);
                normalize();
        }
       
       // build a document vector of doc r
        public void buildDocumentVector (ResultElement r, InvertedIndex invIndex) {
                int docid = r.getDocId();
                for (String term: r.getTerms()) {
                        if (tfidfVector.containsKey(term))
                                continue;
                       
                        double tfidf = invIndex.tfidf(term, docid); // compute tfidf
                        VectorElem v = new VectorElem(term,tfidf);
                        tfidfVector.put(term, v); // store tfidf (weight) for each term (component)
                }
        }

        // add vector components to form a new vector
        public void addTerm (String term, double weight) {
                if (tfidfVector.containsKey(term)) {
                        VectorElem v = tfidfVector.get(term);
                        double w = v.getWeight() + weight;
                        v.setWeight(w);
                        v.setNormalizedWeight(w);
                } else {
                        VectorElem v = new VectorElem(term);
                        v.setWeight(weight);
                        v.setNormalizedWeight(weight);
                        tfidfVector.put(term, v);
                }
        }

        // compute vector norm
        public double computeNorm () {
                double sum = 0;
                for (String term: tfidfVector.keySet()) {
                        double termTFIDF = tfidfVector.get(term).getWeight();
                        sum += Math.pow(termTFIDF, 2);
                }
                return Math.sqrt(sum);
        }
       
       //to normalize the vector
        public void normalize () {
                if (norm == 0) 
                        norm = computeNorm();
                for (String term: tfidfVector.keySet()) {
                        VectorElem v = tfidfVector.get(term);
                        double normalized = v.getWeight() / norm;
                        v.setNormalizedWeight(normalized);
                }
        }

        public ArrayList<VectorElem> getTerms () {
                return new ArrayList<VectorElem>(tfidfVector.values());
        }
}
