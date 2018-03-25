CS-6200 Information Retrieval, Spring 2018
Author: Karan Tyagi

=============================================================================================================================
Compiling and running the programs:
 _______________________
|			|
|    TASK 1 - PARSING	|
|_______________________|

- Open Terminal
- Navigate to \code
- Use the jar provided by running:

$ java -cp hw3-1.0-SNAPSHOT-jar-with-dependencies.jar parser.Parser "<sourceDirectoryPath>" "<outputDirectoryPath>" "<parsingOption>"

- source directory path should be valid and directory should have only text files
- output directory should be empty
- parsingOptions
  # "default"     : perform both (case folding and de-punctuating)	
  # "casefold"    : perform case folding only	
  # "de-punctuate" : perform de-punctuation only
- If you don't specify <parsingOption>, it is assumed to be "default".

--- EXAMPLE USAGE ---

$ java -cp hw3-1.0-SNAPSHOT-jar-with-dependencies.jar parser.Parser "E:\IR\HW3\test docs" "E:\IR\HW3\final corpus"
$ java -cp hw3-1.0-SNAPSHOT-jar-with-dependencies.jar parser.Parser "E:\IR\HW3\test docs" "E:\IR\HW3\final corpus" "default"

>> Above two examples are doing the same thing

$ java -cp hw3-1.0-SNAPSHOT-jar-with-dependencies.jar parser.Parser "E:\IR\HW3\test docs" "E:\IR\HW3\final corpus" "de-punctuate"
$ java -cp hw3-1.0-SNAPSHOT-jar-with-dependencies.jar parser.Parser "E:\IR\HW3\test_docs" "E:\IR\HW3\final corpus" "casefold"

--- EXPECTED OUTPUT ---

On running the Parser program, clean files are created in <outputDirectory>, corresponding to source files.
These files are the clean corpus.
=============================================================================================================================
 ____________________________________________
|					     |
|    TASK 2 - CREATING INVERTED INDEXES	     |
|____________________________________________|

- Open Terminal
- Navigate to \code
- Run the following command:

$ java -jar hw3-1.0-SNAPSHOT-jar-with-dependencies.jar <"CleanCorpusDirectoryPath"> <"OutputDirectoryPath"> <n-Gram> <"PostingType">

- "CleanCorpusDirectoryPath" : Path to the directory having cleaned files
   (clean corpus directory should have only parsed (and clean) text files)

- "OutputDirectoryPath" :  path to the directory where you want the outputs(indexes,tables)

- n-Gram (int)
  # 1 : Term as word unigram
  # 2 : Term as word bigram
  # 3 : Term as word trigram

- postingType(String) : Optional argument, default is "termFreq" 
  # "termFreq"     : Posting format is [DocID(String), TermFrequency(int)]	
  # "tf+positions" : Posting format is [DocID(String), TermFrequency(int), TermPositions[List<Integer>]]

--- EXAMPLE USAGE ---

$ java -jar hw3-1.0-SNAPSHOT-jar-with-dependencies.jar "E:\IR\HW3\final corpus" "E:\IR\HW3\output" 1 
$ java -jar hw3-1.0-SNAPSHOT-jar-with-dependencies.jar "E:\IR\HW3\final corpus" "E:\IR\HW3\output" 1 "termFreq"
>> Above two examples are doing the same thing
============================================================================================================================
 _______________
|		|
|    TASK 2C	|   Generating 3 inverted indexes corresponding to terms as word n-grams (n=1,2,3)
|_______________|  

Run the indexer program thrice with program arguments as follows:

$ java -jar hw3-1.0-SNAPSHOT-jar-with-dependencies.jar <"CleanCorpusDirectoryPath"> <"OutputDirectoryPath"> 1
$ java -jar hw3-1.0-SNAPSHOT-jar-with-dependencies.jar <"CleanCorpusDirectoryPath"> <"OutputDirectoryPath"> 2
$ java -jar hw3-1.0-SNAPSHOT-jar-with-dependencies.jar <"CleanCorpusDirectoryPath"> <"OutputDirectoryPath"> 3

--- EXPECTED OUTPUT ---

On running the Indexer program with the above arguments, following files will be created in <outputDirectoryPath>

/Task2/2B
1) DocLengths_for_1-grams.txt
2) DocLengths_for_2-grams.txt
3) DocLengths_for_3-grams.txt
Tables containing number of terms(unigram, bigram and trigram respectively) in each document.

/Task2/2C/
1) invertedIndex_for_1-grams.txt
2) invertedIndex_for_2-grams.txt
3) invertedIndex_for_3-grams.txt
Inverted Indexes corresponding to 3 runs (unigram, bigram and trigram respectively).

/Task3/3-1
1) termFreqTable_for_1-grams.txt
2) termFreqTable_for_2-grams.txt
3) termFreqTable_for_3-grams.txt
Term frequency tables sorted from most to least frequent terms.

/Task3/3-2
1) docFreqTable_for_1-grams.txt
2) docFreqTable_for_2-grams.txt
3) docFreqTable_for_3-grams.txt
Document frequency tables sorted lexiographically by terms.
============================================================================================================================
 _______________
|		|
|    TASK 2D	|   Generating Unigram inverted index with term positions
|_______________|  

Run the indexer program with program arguments as follows:

$ java -jar hw3-1.0-SNAPSHOT-jar-with-dependencies.jar <"CleanCorpusDirectoryPath"> <"OutputDirectoryPath"> 1 "tf+positions"

--- EXPECTED OUTPUT ---

On running the Indexer program with the above given arguments,
the following file is created:

/Task2/2D/unigramInvertedIndexWithTermPositions.txt

This file contains inverted index of the format Term -> [docID, tf, [termPositions]]
============================================================================================================================
DESIGN

To maintain clarity, I have solved the assignment in two logical parts.
There are two modules - a parser and an indexer.
The parser consumes raw documents and performs text preprocessing and cleans the documents.
The clean corpus is fed to the indexer which tokenizes each doc and creates an inverted index and other relevant tables.
Tables created by the indexer are used for analyzing terms and their tf, dfs for creating a stop list.

The indexer module can be extened to generate inverted indexes of any posting format becuase of the Posting interface.
The two posting types (docId, termfreq) and  (docId, termfreq, termpositions) are very easily implemented by making relevant 
classes which implement Posting interface.

============================================================================================================================
