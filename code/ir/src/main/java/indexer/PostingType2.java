package indexer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Posting represents the posting of a term
 * docID         : name of the document which contains the term
 * termFreq: number of times a term occurs in the given document
 * termPositions  : list of positions where the term occurs in the document of the given term in the document
 *
 */

public class PostingType2 implements Posting {

    private String docID;
    private int termFreq;
    private List<Integer> termPositions;

    /**
     * @param docID        : file name of the given document
     * @param termPosition : positions of a term in the document
     * @param tf           : term frequency - number of times a term occurs in the document
     *
     */
    public PostingType2(String docID, int tf, List<Integer> termPosition) {

        this.docID = docID;
        this.termPositions = termPosition;
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
     * @return Term positions of the given term in a document
     */
    public List<Integer> termPosList()
    {
        return this.termPositions;
    }

    /**
     * @param updatedtf number of time the term has occurred in the given document till now
     */
    @Override
    public void updateTermFreq(int updatedtf) {
        this.termFreq = updatedtf;
    }

    /**
     * @param pos position of term
     */
    public void updateTermPositions(int pos) {
        this.termPositions.add(pos);
    }



    @Override
    public String toString() {

        /* Sort statement*/
        Collections.sort(termPositions);
        return " (" + this.docID() + " , " + Integer.toString(this.termFreq()) + " , "+this.termPositions.toString() +") ";
    }

}
