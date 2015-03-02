
// element of the inverted index for each term
public class IndexElem {
        private int docid;
        private int tf = 0; // term frequency

        public IndexElem (int d) {
                docid = d;
        }
        public int getDocId () {
                return docid;
        }
        public int tf() {
                return tf;
        }
        public void addFrequency() {
                tf++;
        }
}