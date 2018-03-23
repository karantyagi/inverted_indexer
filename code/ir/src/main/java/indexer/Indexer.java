package indexer;

import com.sun.xml.internal.ws.api.ha.StickyFeature;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;


/**
 *  Indexer class creates an inverted index hashmap
 *
 * @author  Karan Tyagi
 * @version 1.0
 * @since   2018-03-21
 */

public class Indexer {

    private static String corpusPath;
    private static String outputDirectory;
    private static int termLength;  // term as word n-gram
    private static String postingType = "termfreq";
    private static long corpusSize;   // number of files in the Clean Corpus Directory
    private static int filesProcessed = 0;


    private HashMap<String, Integer> docTermCount;
    private HashMap<String, List<Posting>> invertedIndex;

    private static HashMap<String, Integer> sortedTermFreqTable;
    private static HashMap<String, List<String>> sortedDocFreqTable;

    /**
     * @Effects creates an Indexer object
     */
    public Indexer() {

        this.invertedIndex = new HashMap<>();
        this.docTermCount = new HashMap<>();

        try {
            Stream<Path> allFiles = Files.list(Paths.get(corpusPath));
            corpusSize = allFiles.count();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {

        boolean processFurther = false;

        if (args.length == 3) {
            //System.out.println("3 arguments");
            processFurther = true;
            corpusPath = args[0];
            outputDirectory = args[1];
            termLength = Integer.parseInt(args[2]);
        }

        // parsingOption : default(apply both - case folding and de-punctuating), casefold, depunctuate

        else if (args.length == 4) {
            //System.out.println("4 arguments");

            corpusPath = args[0];
            outputDirectory = args[1];
            termLength = Integer.parseInt(args[2]);
            postingType = args[3];
            if(postingType.equals("termFreq") || postingType.equals("tf+positions"))
                processFurther = true;
            else
                System.out.println("Invalid Argument <postingType>\n<postingType> : \"termfreq\" or \"tf+positions\"" );

        } else
            System.out.println(" Invalid Arguments. Enter arguments as specified.");


        if (processFurther) {
            Indexer i = new Indexer();

            System.out.println("CLEAN CORPUS DIR PATH     : " + corpusPath);
            System.out.println("OUTPUT DIR PATH           : " + outputDirectory);
            System.out.println("CORPUS SIZE (No. of Docs) : " + corpusSize);
            System.out.println("n-gram                    : " + termLength);
            if (postingType.equals("termfreq"))
                System.out.println("POSTING TYPE              : " + postingType + " [docID, termFrequency]");
            else if(postingType.equals("tf+positions"))
                System.out.println("POSTING TYPE              : " + postingType + " [docID, termFrequency, [Term positions]]");

            System.out.println();
            if (!new File(outputDirectory).isDirectory()) {
                File dir = new File(outputDirectory);
                dir.mkdirs();
                System.out.println("Output Directory Created  : " + outputDirectory);
            }
            System.out.println();


            i.createInvertedIndex();
            i.writeDocTermCountTable();
            i.writeInvertedIndex();

            sortedTermFreqTable = createTermFrequencyTable(i.invertedIndex);
            writeTermFrequencyTable(sortedTermFreqTable);

           sortedDocFreqTable = createDocFrequencyTable(i.invertedIndex);
           writeDocFrequencyTable(sortedDocFreqTable);

           /* analyze rate of change of tf */

           System.out.println();
           int counter =1;
           float totalterms = 0;
            List<String> l = new ArrayList<>();

            for (Map.Entry<String, Integer> entry : sortedTermFreqTable.entrySet()) {
                Integer value = entry.getValue();
                totalterms += value;
            }

            for (Map.Entry<String, Integer> entry : sortedTermFreqTable.entrySet()) {
                String key = entry.getKey().toString();
                Integer value = entry.getValue();
                //System.out.printf("%4d   %-40s  %5d  %5.3f", counter, key, value,(value*100.0)/totalterms);
                l.add(String.format("%4d  %-40s  %5d     %5.2f", counter, key, value,(value*100.0)/totalterms));
               counter++;
            }
            try{
                Path file = Paths.get(outputDirectory+"\\AnalyzeStopList_for_"+Integer.toString(termLength)+"-grams.txt");
                Files.write(file, l, Charset.forName("UTF-8"));

            }catch (IOException e)
            {
                e.printStackTrace();
            }


        }
    }

    /**
     * @return Length of each document
     */
    public HashMap<String, Integer> docWordCount() {

        return this.docTermCount;
    }


    /**
     * @return inverted Index
     */
    public HashMap<String, List<Posting>> createInvertedIndex() {

        try {
            Files.list(Paths.get(corpusPath))
                    .filter(filepath -> filepath.toString().endsWith(".txt"))
                    .forEach(p -> {
                        this.tokenize(p);
                    });
            System.out.printf("\rCorpus Tokenization Complete.");
            System.out.println("\r\nInverted Index Created.");
        } catch (IOException e) {

            e.printStackTrace();
        }

        return this.invertedIndex;
    }


    /**
     * @param filePath path of a file
     * @Effects reads the file and creates tokens in the form of term and Posting
     * for the inverted index
     */
    private void tokenize(Path filePath) {

        try {

            FileReader fr = new FileReader(filePath.toString());
            BufferedReader br = new BufferedReader(fr);

            // Earth.txt from Earth.txt 's path
            String docID = filePath.toString().substring(filePath.toString().lastIndexOf(File.separator) + 1);
            // Earth from earth.txt
            docID = docID.substring(0, docID.indexOf('.'));

            String text = br.readLine();

            //System.out.println(docID);
            //System.out.println(text);
            //this.documentWordTotal.put(doc, currentLine.split(" ").length);
            //System.out.println(doc);
            //String docID = docID;

            filesProcessed++;
            int docTermCount = this.updateTerms(docID, text);
            updateDocTermCount(docID, docTermCount);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param docID          document ID
     * @param totalTermCount total number of terms in a document
     */
    private void updateDocTermCount(String docID, int totalTermCount) {

        if (!this.docTermCount.containsKey(docID)) {
            this.docTermCount.put(docID, totalTermCount);
            //System.out.println(docID + " : "+ totalTermCount);
        }
    }


    private void writeDocTermCountTable() {
        ////System.out.println();
        ////System.out.println("  Printing Document Length Hashmap");
        ////System.out.println("     DocID                                               Number of Terms");
        ////System.out.println(" -----------------------------------------------------------------------");
        int i = 1;
        List<String> lines = new ArrayList<>();
        lines.add("DocID                                                    Number of Terms");
        lines.add("\n");

        HashMap<String, Integer> sortedDocTermCount = sortByLength(docTermCount);

        for (Map.Entry<String, Integer> entry : sortedDocTermCount.entrySet()) {
            String key = entry.getKey().toString();
            Integer value = entry.getValue();
            ////System.out.printf("%4d %-60s %5d%n",  i, key, value);
            lines.add(String.format("%-60s  %5d", key, value));
            i++;
        }

        try {
            if (!new File(outputDirectory + "\\Task2\\2B").isDirectory()) {
                File dir = new File(outputDirectory + "\\Task2\\2B");
                dir.mkdirs();
                System.out.println("\nDirectory created : " + outputDirectory + "\\Task2\\2B");
            }
            Path file = Paths.get(outputDirectory + "\\Task2\\2B\\DocLengths_for_" + Integer.toString(termLength) + "-grams.txt");
            Files.write(file, lines, Charset.forName("UTF-8"));
            System.out.println("Table created                : DocLengths_for_" + termLength + "-grams.txt (Document-TermCount Table)");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static HashMap<String, Integer> sortByLength(HashMap<String, Integer> docTermCount) {
        List list = new LinkedList(docTermCount.entrySet());

        // Defined Custom Comparator here
        Collections.sort(list, (Comparator) (o1, o2) -> ((Comparable) ((Map.Entry) (o2)).getValue())
                .compareTo(((Map.Entry) (o1)).getValue()));

        // Here I am copying the sorted list in HashMap
        // using LinkedHashMap to preserve the insertion order
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }


    /**
     * @param docID document ID
     * @param text  the entire document
     * @Effects adds terms from a document to the inverted index (terms are the keys)
     */

    // returns the no. of terms(unigram,bigram or trigram for a particular document)
    private int updateTerms(String docID, String text) {
        // System.out.println(text);

        float roughTotal = text.trim().split(" ").length;
        float complete;
        int termsInDocID = 0;
        //String bar = new String(new int[] { 0x2758  }, 0, 1);
        //StringBuffer progress = new StringBuffer(bar);
        //progress.append(bar);

        while (!text.isEmpty()) {


            String term = this.generateTerm(text);


            if (term.isEmpty()) {
                break;
            }


            this.addTermToIndex(docID, term, (termsInDocID+1));
            termsInDocID++;


            //System.out.println("TERM : " + term);

            // display progress

            complete = roughTotal - text.trim().split(" ").length + 1;
            System.out.printf("\rTokenizing : %-40s | Generating Terms :%-4.1f %s          | Files Remaining : %d  ",
                    docID, (complete * 100.0 / roughTotal), "%", corpusSize - filesProcessed);

            //System.out.print(progress);


            //System.out.print("▮");
            //System.out.print("Progress  \r "+(complete*100/total));
            //if(((complete*100)/total)%5.0 == 0)


            text = this.removeProcessedText(text);
            //System.out.println("TEXT : "+text);

            // Progress Bar


        }
        return termsInDocID;
    }


    /**
     * @param text any String of words
     * @return a term for the inverted index in the form of n-gram
     * @see <b>nGram:</b> Refer constructor of the Indexer
     */
    private String generateTerm(String text) {

        int wordsInTerm = 1;

        if (text.trim().split(" ").length < this.termLength)
            return "";
        StringBuilder newTerm = new StringBuilder();
        String txt = new String(text);
        try {
            while (wordsInTerm <= this.termLength) {
                txt = txt.trim();
                newTerm.append(txt.split(" ")[0]);
                if (wordsInTerm < this.termLength)
                    newTerm.append(" ");
                txt = txt.substring(txt.indexOf(" ") + 1);
                wordsInTerm++;
            }
        } catch (Exception e) {
            newTerm = null;
            e.printStackTrace();
        }
        try {
            return newTerm.toString();
        } catch (NullPointerException ne) {
            return "";
        }

    }

    /**
     * @param term  term for the inverted index (key)
     * @param docID document id
     * @Where the term is present in the given docID
     * @Effects adds the Posting corresponding to the given term in the inverted index
     */
    private void addTermToIndex(String docID, String term, int position) {

        if(postingType.equals("termfreq")){
            if (this.invertedIndex.containsKey(term)) {

                List<Posting> termPostingList = this.invertedIndex.get(term);
                Posting temp;

                if ((temp = docIDinList(docID, termPostingList)) != null) {  // null indicates there is no such posting
                    temp.updateTermFreq(temp.termFreq() + 1);
                } else
                    this.invertedIndex.get(term).add(new PostingType1(docID, 1));

            } else {
                this.invertedIndex.put(term, new ArrayList<>());
                this.invertedIndex.get(term).add(new PostingType1(docID, 1));
            }
        }

        if(postingType.equals("tf+positions")){
            List<Integer> pos = new ArrayList<>();
            pos.add(position);
            int flag = 0;

            if (this.invertedIndex.containsKey(term)) {

                List<Posting> termPostingList = this.invertedIndex.get(term);
                Posting temp = null;


                for(Posting p: termPostingList)
                {
                    if(p.docID().equals(docID)){
                        temp = p;
                        flag=1;
                        break;
                    }
                }
                if(flag==1){
                    temp.updateTermFreq(temp.termFreq() + 1);
                    ((PostingType2)temp).updateTermPositions(position);
                }
                else
                {
                    this.invertedIndex.get(term).add(new PostingType2(docID, 1,pos));
                }

            }
            else {

                this.invertedIndex.put(term, new ArrayList<>());
                this.invertedIndex.get(term).add(new PostingType2(docID, 1,pos));
            }
        }

    }

    private static Posting docIDinList(String docID, List<Posting> termPosting) {
        Posting p = null;
        for (Posting i : termPosting) {
            if (i.docID().equals(docID)) {
                p = i;
                break;
            }
        }
        return p;
    }

    //
    //* @params : sortType = sort by "term" or sort by "tf"
    //
    private void writeInvertedIndex() {

       ///// System.out.println();
        List<String> lines = new ArrayList<>();
        lines.add("Term                                         Postings");
        lines.add("\n");
        int i = 1;
        //// System.out.println("       Inverted Index");
        //// System.out.println(" ------------------------------------------------");
        //// System.out.println("   Term               Posting [DocID : Term Freq]");
        //// System.out.println(" ------------------------------------------------");

        HashMap<String, List<Posting>> sortedInvertedIndex = sortByTerms(invertedIndex);

        for (Map.Entry<String, List<Posting>> entry : sortedInvertedIndex.entrySet()) {
            String key = entry.getKey().toString();
            List<Posting> value = entry.getValue();
            //// System.out.printf("%9d %-25s %s%n",  i, key, value.toString());
            lines.add(String.format("%-40s  %s", key, value.toString()));
            i++;
        }
        if(postingType.equals("termfreq")) {
            try {
                if (!new File(outputDirectory + "\\Task2\\2C").isDirectory()) {
                    File dir = new File(outputDirectory + "\\Task2\\2C");
                    dir.mkdirs();
                    System.out.println("\nDirectory created : " + outputDirectory + "\\Task2\\2C");
                }
                Path file = Paths.get(outputDirectory + "\\Task2\\2C\\invertedIndex_for_" + Integer.toString(termLength) + "-grams.txt");
                Files.write(file, lines, Charset.forName("UTF-8"));
                System.out.println("Index created                : invertedIndex_for_" + termLength + "-grams.txt");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(postingType.equals("tf+positions")) {
            try {
                if (!new File(outputDirectory + "\\Task2\\2D").isDirectory()) {
                    File dir = new File(outputDirectory + "\\Task2\\2D");
                    dir.mkdirs();
                    System.out.println("\nDirectory created : " + outputDirectory + "\\Task2\\2D");
                }
                Path file = Paths.get(outputDirectory + "\\Task2\\2D\\unigramInvertedIndexWithTermPositions.txt");
                Files.write(file, lines, Charset.forName("UTF-8"));
                System.out.println("Index created                : unigramInvertedIndexWithTermPositions.txt");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }





    private static HashMap<String,Integer> createTermFrequencyTable(HashMap<String, List<Posting>> invertedIndex) {

        HashMap<String, Integer> freqTable= new HashMap<>();
        // adding all terms to table
        String term;
        int tf;
        List<Posting> postings;

        for (Map.Entry<String, List<Posting>> entry : invertedIndex.entrySet()) {
            tf =0;
            term = entry.getKey().toString();
            postings = entry.getValue();
            for (Posting p : postings){
                tf+=p.termFreq();
            }
            freqTable.put(term,tf);
        }

        return sortByTermFreq(freqTable);
        // TermFreq Table is sorted on tf (most frequent to least)

    }

    private static void writeTermFrequencyTable(HashMap<String,Integer> sortedInvertedIndex) {

        ////System.out.println();
        ////System.out.println("          Term Frequency Table");
        ////System.out.println("----------------------------------------");
        ////System.out.println("       Term                    Frequency");
        ////System.out.println("----------------------------------------");
        int i=1;
        List<String> lines = new ArrayList<>();
        lines.add("Term                                               Frequency");
        lines.add("\n");


        for (Map.Entry<String, Integer> entry : sortedInvertedIndex.entrySet()) {
            String key = entry.getKey().toString();
            Integer value = entry.getValue();
           //// System.out.printf("%4d   %-30s %5d%n",  i, key, value);
            lines.add(String.format("%-50s  %5d",key, value));
            i++;
        }

        try{
            if (!new File(outputDirectory+"\\Task3\\3-1").isDirectory())
            {
                File dir = new File(outputDirectory+"\\Task3\\3-1");
                dir.mkdirs();
                System.out.println("\nDirectory created            : "+outputDirectory+"\\Task3\\3-1");
            }
            Path file = Paths.get(outputDirectory+"\\Task3\\3-1\\termFreqTable_for_"+Integer.toString(termLength)+"-grams.txt");
            Files.write(file, lines, Charset.forName("UTF-8"));
            System.out.println("Term Frequency Table created : termFreqTable_for_"+termLength+"-grams.txt");

        }catch (IOException e)
        {
            e.printStackTrace();
        }

    }


    private static HashMap<String,List<String>> createDocFrequencyTable(HashMap<String, List<Posting>> invertedIndex) {

        HashMap<String, List<String>> docFreqTable= new HashMap<>();
        List<String> docs;
        // adding all terms to doc frequency table
        String term;
        List<Posting> postings;

        for (Map.Entry<String, List<Posting>> entry : invertedIndex.entrySet()) {
            term = entry.getKey().toString();
            docs = new ArrayList<>();
            postings = entry.getValue();
            for (Posting p : postings){
                docs.add(p.docID());
            }

            docFreqTable.put(term,docs);

        }

        return sortdfTableByTerm(docFreqTable);
        // TermFreq Table is sorted on tf (most frequent to least)

    }

    private static void writeDocFrequencyTable(HashMap<String,List<String>> sortedTable) {

        ////System.out.println();
        ////System.out.println("           Document Frequency Table");
        ////System.out.println("---------------------------------------------------------------");
        ////System.out.println("       Term                  df   docIDs       ");
        ////System.out.println("----------------------------------------------------------------");
        int i=1;
        List<String> lines = new ArrayList<>();
        lines.add("Term                                         df   docIDs ");
        lines.add("\n");


        for (Map.Entry<String, List<String>> entry : sortedTable.entrySet()) {
            String key = entry.getKey().toString();
            List<String> value = entry.getValue();
            ////System.out.printf("%4d   %-18s %5d   %s%n",  i, key, value.size(),value.toString());
            lines.add(String.format("%-40s  %5d   %s",key, value.size(),value.toString()));
            i++;
        }

        try{
            if (!new File(outputDirectory+"\\Task3\\3-2").isDirectory())
            {
                File dir = new File(outputDirectory+"\\Task3\\3-2");
                dir.mkdirs();
                System.out.println("\nDirectory created            : "+outputDirectory+"\\Task3\\3-2");
            }
            Path file = Paths.get(outputDirectory+"\\Task3\\3-2\\docFreqTable_for_"+Integer.toString(termLength)+"-grams.txt");
            Files.write(file, lines, Charset.forName("UTF-8"));
            System.out.println("Doc Frequency Table created  : docFreqTable_for_"+termLength+"-grams.txt");

        }catch (IOException e)
        {
            e.printStackTrace();
        }



    }


    private static HashMap<String, Integer> sortByTermFreq(HashMap<String,Integer> frequencyTable) {
        List list = new LinkedList(frequencyTable.entrySet());

        // Defined Custom Comparator here
        Collections.sort(list, (Comparator) (o1, o2) -> ((Comparable) ((Map.Entry) (o2)).getValue())
                .compareTo(((Map.Entry) (o1)).getValue()));

        // Here I am copying the sorted list in HashMap
        // using LinkedHashMap to preserve the insertion order
        HashMap sortedTable = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedTable.put(entry.getKey(), entry.getValue());
        }
        return sortedTable;
    }


    private static HashMap<String, List<Posting>> sortByTerms(HashMap<String, List<Posting>> invIndex) {
        List list = new LinkedList(invIndex.entrySet());

        // Defined Custom Comparator here
        Collections.sort(list, (Comparator) (o1, o2) -> ((Comparable) ((Map.Entry) (o1)).getKey())
                .compareTo(((Map.Entry) (o2)).getKey()));

        // Here I am copying the sorted list in HashMap
        // using LinkedHashMap to preserve the insertion order
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }

    private static HashMap<String, List<String>> sortdfTableByTerm(HashMap<String, List<String>> dfTable) {
        List list = new LinkedList(dfTable.entrySet());

        // Defined Custom Comparator here
        Collections.sort(list, (Comparator) (o1, o2) -> ((Comparable) ((Map.Entry) (o1)).getKey())
                .compareTo(((Map.Entry) (o2)).getKey()));

        // Here I am copying the sorted list in HashMap
        // using LinkedHashMap to preserve the insertion order
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }












    /**
     * @param text a string
     * @return removes the first word of the given string
     * 		if only one word is present in the given String
     * 		then an empty string is returned
     */

    private String removeProcessedText(String text) {

        String remainingText = text.trim();
        if(remainingText .contains(" "))
            remainingText = remainingText.substring(remainingText.indexOf(" ") + 1);
        else
            remainingText = "";
        return remainingText;
    }


}









