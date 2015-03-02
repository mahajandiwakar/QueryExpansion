public class TermNode implements Comparable<TermNode> {

        private String term;                 // The term name
        private double weight;                   // Weight tf*idf
        private double normalizedWeight; // L2 normalized tf*idf
       
        private int termFrequency;               // Frequency of term in a document or set of documents
        private double score;                    // Score, calculated by weight and factor
       
        /**
         * Constructor
         * @param name the term
         */
        public TermNode(String name) {
                this.term = name;
                this.termFrequency = 1;
        }
       
        /**
         * Constructor
         * @param name the term
         * @param tfidf Term Frequency - Inverted Document Frequency
         */
        public TermNode (String name, double tfidf) {
                this.term = name;
                this.weight = tfidf;
                this.termFrequency = 1;
                this.score = tfidf; // @@@ test: score = tfidf
        }

        /**
         * Increments the frequency of the term
         */
        public void incrementFreq() {
                termFrequency++;
        }
       
        /**
         * Returns the term name
         * @return
         */
        public String getTerm() {
                return term;
        }
       
        /**
         * Sets the score of the term
         * @param score
         */
        public void setScore(double score) {
                this.score = score;
        }
       
        /**
         * Returns the score of the term
         * @return
         */
        public double getScore() {
                return score;
        }

        public double getWeight() {
                return weight;
        }
        public void setWeight(double tfidf) {
                this.weight = tfidf;
                this.score = tfidf; // @@@ test: set score = TFIDF
        }

        public double getNormalizedWeight() {
                return normalizedWeight;
        }
        public void setNormalizedWeight(double w) {
                this.normalizedWeight = w;
        }

        /**
         * Compares two nodes by score
         * in descending order
         */
        public int compareTo(TermNode tn) {
                if (this.normalizedWeight < tn.normalizedWeight)
                        return 1;
                else if (this.normalizedWeight == tn.normalizedWeight)
                        return 0;
                else
                        return -1;
        }
}
