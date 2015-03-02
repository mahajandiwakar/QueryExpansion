import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

//class to store the inverted index
public class InvertedIndex {
        private TreeSet<Integer> documents = new TreeSet<Integer>();
        private HashMap<String,ArrayList<IndexElem>> index = new HashMap<String,ArrayList<IndexElem>>();

        private HashMap<String,Double> idfCache = new HashMap<String,Double>();
        private boolean idfFlag = true;
       
        public void clear() {
                index.clear();
                idfCache.clear();
                documents.clear();
        }
       
        // add document terms to the index
        public void addDocument (ResultElement r) {
                int docid = r.getDocId();
                ArrayList<String> terms = r.getTerms();
                for (int i=0; i<terms.size(); i++) {
                        String term = terms.get(i);
                        addTerm(term,docid);
                }
        }

        // to retrieve documents that have the word 'term'
        public ArrayList<IndexElem> getDocsWithTerm (String term) {
                if (index.containsKey(term)) {
                        return index.get(term);
                } else {
                        return null;
                }
        }

        // to retrieve term frequency information for doc with id 'doc'
        public IndexElem getIndexElem(String term, int doc) {
                ArrayList<IndexElem> list = getDocsWithTerm(term);
                return getIndexElem(list, doc);
        }

        // to retrieve doc with id 'doc' from document list
        public IndexElem getIndexElem(ArrayList<IndexElem> list, int doc) {
                if (list != null) {
                        for (IndexElem d: list) {
                                if (d.getDocId() == doc) {
                                        return d;
                                }
                        }
                }
                return null;
        }

        // adding new term to the index
        public void addTerm(String term, int doc) {
                ArrayList<IndexElem> list = getDocsWithTerm(term);
                IndexElem d;

                if (list == null) { // new term
                        list = new ArrayList<IndexElem>(); // create document list
                        d = new IndexElem(doc); 
                        list.add(d); // add this document to list
                        index.put(term, list);
                } else {
                        d = getIndexElem(list, doc); // term already present, retrieve doc with id 'doc'
                        if (d == null) {
                                d = new IndexElem(doc); // creat new if not already present
                                list.add(d);
                        }
                }
                d.addFrequency(); // increase tf
                documents.add(doc); 
                idfFlag = false;
        }

        // compute idf
        public double idf (String term) {

                if (idfFlag) { // term not added since last idf retrieval
                        if (idfCache.containsKey(term)) {
                                return idfCache.get(term);
                        }
                } else {
                        idfCache.clear();
                        idfFlag = true;
                }

                double N = documents.size();
                ArrayList<IndexElem> list = getDocsWithTerm(term);
                int df = list.size(); // document frequency

                if (df == 0) df = 1;
                double idf = Math.log10(N/df);
                idfCache.put(term, idf); // put in idf cache for faster access
                return idf;
        }

        //compute tfidf
        public double tfidf (String term, int doc) {
                IndexElem d = getIndexElem(term,doc);
                int tf = (d == null) ? 0 : d.tf();
                double idf = idf(term);
                double tfidf = tf * idf;
                return tfidf;
        }
}
