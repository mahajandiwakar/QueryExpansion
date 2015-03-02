
// class to store each vector component
public class VectorElem implements Comparable<VectorElem> {

        private String term; // the term      
        private double weight; //tfidf     
        private double normalizedWeight; //normalized tfidf

        public VectorElem(String name) {
                this.term = name;
        }
       
        public VectorElem (String name, double tfidf) {
                this.term = name;
                this.weight = tfidf;
        }

        public String getTerm() {
                return term;
        }
       
        public double getWeight() {
                return weight;
        }
        public void setWeight(double tfidf) {
                this.weight = tfidf;
        }

        public double getNormalizedWeight() {
                return normalizedWeight;
        }
        public void setNormalizedWeight(double w) {
                this.normalizedWeight = w;
        }

        public int compareTo(VectorElem tn) { // comparison metrix to sort terms by relevance
                if (this.normalizedWeight < tn.normalizedWeight)
                        return 1;
                else if (this.normalizedWeight == tn.normalizedWeight)
                        return 0;
                else
                        return -1;
        }
}
