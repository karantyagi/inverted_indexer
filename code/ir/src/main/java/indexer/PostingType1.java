package indexer;


/**
 * PostingType1  contains
 * docID         : name of the document which contains the term
 * termFreq: number of times a term occurs in the given document
 *
 */

public class PostingType1 implements Posting {

    private String docID;
    private int termFreq;

    /**
     * @param docID        : file name of the document
     * @param tf           : term frequency - number of times a term occurs in the document
     *
     */
    public PostingType1(String docID, int tf) {

        this.docID = docID;
        this.termFreq = tf;
    }

    /**
     * @return Document ID (document filename)
     */
    @Override
    public String docID() {

        return this.docID;
    }

    /**
     * @return frequency of the term in the given the document
     */
    @Override
    public int termFreq() {
        return this.termFreq;
    }

    /**
     * @param updatedtf number of time the term has occurred in the given document till now
     */
    @Override
    public void updateTermFreq(int updatedtf) {

        this.termFreq = updatedtf;

    }

    @Override
    public String toString() {

        return "[ " + this.docID() + " : " + Integer.toString(this.termFreq()) + " ]";
    }

}