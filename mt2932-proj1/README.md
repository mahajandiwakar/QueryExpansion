# QueryExpansion

Mahd Tauseef	mt2932
Diwakar Mahajan	dm3084

List of Files:
README.md           
transcript.doc
AugmentQuery.java  
DocVector.java  
InvertedIndex.java     
BingTest.java      
IndexElem.java  
ResultElement.java  
SearchResults.java
VectorElem.java
run.sh              
compile.sh              
commons-codec-1.10.jar  
json-simple-1.1.1.jar  

Compile Instructions: Execute compile.sh
Execute Instructions: Execute run.sh with precision as first argument and search string as second (bing key hard-coded in shell script)
e.g. ./run.sh 0.9 gates

Bing Key: 6ogFrWjyeWmPufqcZFqcmTMvJDxvznvQIwGYXDwvZQo

Internal Design:
1) The main class is BingTest.java. It configures Bing parameters, forms the connection and sends the query.
It initiates feedback from the user and calls the expansion algorithm.
2) The search results in their basic form are handled by the SearchResults class. It receives the JSON output and parses it to retrieve and store relevant information.
3) Each result element is stored in the ResultElement class. It stores information about the content as well as the relevance feedback provided by the user.
An array list of ResultElement containing all results from the latest iteration is maintained by the SearchResults class mentioned before.
4) The major chunk of the algorithm is in AugmentQuery class. Its function augment is called by the BingTest class to start the algorithm.
5) InvertedIndex class maintains the inverted index of all terms as well as the tfidf.
6) The classes IndexElem, DocVector and VectorElem act as helper classes and provide the storage structure to maintain the inverted index and vector respresentations of the documents. IndexElem is the node of the InvertedIndex class and vectorElem is a node of the DocVector class which computes and stores the vector representation.

Query Modification Algorithm:
The Title, URL and Summary of each result is retrieved and rest of the information discarded.
The title and summary of each result forms the document.
First an inverted index is created by sequentially parsing through all the results. The inverted index is implemented using a Hashmap.
The term itself forms the key and an index node forms the value. The index node stores two pieces of information; the term document id and the term frequency.
As each term is processed we first check if it already exists in the HashMap. If yes, then we find if the term entry present is for this particular document. If an entry for that document exists then it is retrieved and the term frequency incremented for that entry. If an entry does not exist then a new node is added to represent that document. if a term simply does not exist in the HashMap the index is expanded to include one.
Once an inverted index is created we go on to compute the tfidf for each document. Thed inverse index is used to build the vector representation/tfidf.
Then we use the normalized tfidf to compute the centroid for the relevant documents cluster. the centroid is modified by scaling the centroid of the non relevant cluster and subtracting it from the centroid of relevant cluster similar to as is done in Rocchios algorithm.
Finally we take the highest weighted terms, make sure they arent present already in the query and augment them into the query.
One or two terms are added depending on whether the top two weighted terms were within 20% of each other or not.

The processis repeated till the desired precision is reached.


