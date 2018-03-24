CS-6200 Information Retrieval, Spring 2018
Assignment 3
Author : Karan Tyagi
=============================================================================================================================
Installation and Setup




Add jdk to path : ???
$ set path=C:\Program Files\Java\jdk1.8.0_151\bin

- Navigate Karan_Tyagi_HW3\code\ir
- open IntelliJ or eclipse IDE
- Import 'ir' as Maven Project  (??? CROSS CHECK in eclipse and IntelliJ)

=============================================================================================================================

Compiling and running the programs :
 _______________________
|			|
|    TASK 1 - PARSING	|
|_______________________|

- Open Terminal
- Navigate to Karan_Tyagi_HW3\code
????
- Navigate to:  ir\src\main\java\parser 
- Run the following command:

$ java -cp ir.jar parser.Parser "<sourceDirectoryPath>" "<outputDirectoryPath>" "<parsingOption>"

- source directory path should be valid and directory should have only text files
- output directory should be empty
- parsingOptions
  # "default"     : perform both (case folding and de-punctuating)	
  # "casefold"    : perform case folding only	
  # "de-punctuate" : perform de-punctuation only
- If you don't specify <parsingOption>, it is assumed to be "default".

--- EXAMPLE USAGE ---

$ java -cp ir.jar parser.Parser "E:\IR\HW3\test docs" "E:\IR\HW3\final corpus"
$ java -cp ir.jar parser.Parser "E:\IR\HW3\test docs" "E:\IR\HW3\final corpus" "default"

>> Above two examples are doing the same thing

$ java -cp ir.jar parser.Parser "E:\IR\HW3\test docs" "E:\IR\HW3\final corpus" "de-punctuate"
$ java -cp ir.jar parser.Parser "E:\IR\HW3\test_docs" "E:\IR\HW3\final corpus" "casefold"

--- EXPECTED OUTPUT ---

On running the Parser program, clean files are created in <outputDirectory>, corresponding to source files.
These files are the clean corpus.

=============================================================================================================================
 ____________________________________________
|					     |
|    TASK 2 - CREATING INVERTED INDEXES	     |
|____________________________________________|

- Open Terminal
- Navigate to Karan_Tyagi_HW3\code
- Navigate to:  ir\src\main\java\indexer
- Run the following command:

$ java -jar ir.jar "<CleanCorpusDirectoryPath>" "<OutputDirectoryPath>"<n-Gram> <PostingType>

- Clean corpus directory path should have only parsed (and clean) text files
- Output directory path is the path of directory where you want the outputs(indexes,tables)
- n-Gram (int)
  # 1 : Term as word unigram
  # 2 : Term as word bigram
  # 3 : Term as word trigram
- postingType
  # "termfreq"     : Posting format is [DocID(String), TermFrequency(int)]	
  # "tf+positions" : Posting format is [DocID(String), TermFrequency(int), TermPositions[List<Integer>]]

- If you don't specify <postingType>, default postingType "termfreq" is assumed

--- EXAMPLE USAGE ---

$ java -jar ir.jar "E:\IR\HW3\final corpus" "E:\IR\HW3\output" 1 
$ java -jar ir.jar "E:\IR\HW3\final corpus" "E:\IR\HW3\output" 1 "termfreq"

>> Above two examples are doing the same thing

============================================================================================================================
 _______________
|		|
|    TASK 2C	|   Generating 3 inverted indexes corresponding to terms as word n-grams (n=1,2,3)
|_______________|  

Run the indexer program thrice with program arguments as follows:

$ java -jar ir.jar  "<CleanCorpusDirectoryPath>" "<OutputDirectoryPath>" 1
$ java -jar ir.jar  "<CleanCorpusDirectoryPath>" "<OutputDirectoryPath>" 2
$ java -jar ir.jar  "<CleanCorpusDirectoryPath>" "<OutputDirectoryPath>" 3

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

$ java -jar ir.jar "<CleanCorpusDirectoryPath>" "<OutputDirectoryPath>" 1 "tf+positions"

--- EXPECTED OUTPUT ---

On running the Indexer program with the above given arguments,
the following file is created:

/Task2/2D/unigramInvertedIndexWithTermPositions.txt

This file contains inverted index of the format Term -> [docID, tf, [termPositions]]

============================================================================================================================
Sources used :

- jsoup
- javadoc oracle
- regex

============================================================================================================================
Submission : Directory Structure

code	| /ir	| Maven Project (source code)	      
			      
Task2	| /2B 	| 3 files as discussed above  
	| /2C	| 3 files as discussed above  
	| /2D   | 1 file  as discussed above

Task3	| /3-1 	| 3 files as discussed above  
	| /3-2	| 3 files as discussed above 
	|       |
	| /3-3	| StopListAnalysisFiles : 3 files (for n-grams n=1,2,3) - contains the following columns : Term, tf, df
	|	| term as fraction of total terms in corpus
	|   	| explaination.txt : explaining cutoff thresholds for stoplists
	|       | stopList_for_1-grams.txt 
	|       | stopList_for_2-grams.txt
	|       | stopList_for_3-grams.txt
_______________________________________________________

DESIGN CHOICES ????????

Map faster;
Interface ??
HashMap
_____________________________________________________________________________________________________________________