package indexer;

/**
 * Posting represents the posting of a term
 * docID         : name of the document which contains the term
 * termFreq: number of times a term occurs in the given document
 *
 */
public interface Posting {

    /**
     * @return Document ID (document filename)
     */
    String docID();

    /**
     * @return frequency of the term in the given the document
     */
    int termFreq(); //

    /**
     * @param updatedtf number of time the term has occurred in the given document till now
     */
    void updateTermFreq(int updatedtf);
}
