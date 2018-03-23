CS-6200 Information Retrieval, Spring 2018
Assignment 3
Author : Karan Tyagi
-----------------------------------------------------------------------
Installation 

Install java Or jdk 1.8.0_151

java 8 ??

RUN as maven project 

Library : Maven jsoup

<dependency>
        <groupId>org.jsoup</groupId>
        <artifactId>jsoup</artifactId>
        <version>1.11.2</version>
    </dependency>

and API uasage - issue with lambda expressions etc.  etc.

Add jdk to path : ???
$ set path=C:\Program Files\Java\jdk1.8.0_151\bin

Go to Karan_Tyagi_HW3\code\ir
Open 'ir' in KaranTyagi  as Maven Project  (??? in IntellJ or Eclipse)
-----------------------------------------------------------------------

Compiling and running the programs :
 _______________________
|			|
|    TASK 1 - PARSING	|
|_______________________|

- Navigate to:  ir\src\main\java\parser 
- Add the following to Program Arguments (command line arguments)

"<sourceDirectoryPath>" "<outputDirectoryPath>" "<parsingOption>"

- source directory path should be valid and directory should have only text files
- output directory should be empty
- parsingOptions
  # "default"     : perform both (case folding and de-punctuating)	
  # "casefold"    : perform case folding only	
  # "de-punctuate" : perform de-punctuation only
- If you don't specify <parsingOption>, it is assumed to be "default".

Example usage:

"E:\IR\HW3\test docs" "E:\IR\HW3\final corpus"
"E:\IR\HW3\test docs" "E:\IR\HW3\final corpus" "default"

>> Above two examples are doing the same thing

"E:\IR\HW3\test docs" "E:\IR\HW3\final corpus" "de-punctuate"
"E:\IR\HW3\test_docs" "E:\IR\HW3\final corpus" "casefold"

EXPECTED OUTPUT:
On running the Parser program, clean files are created in <outputDirectory>, corresponding to source files.
These files are the clean corpus.

 ____________________________________________
|					     |
|    TASK 2 - CREATING INVERTED INDEXES	     |
|____________________________________________|

- Navigate to:  ir\src\main\java\indexer
- Add the following to Program Arguments (command line arguments)

"<CleanCorpusDirectoryPath>" "<OutputDirectoryPath>"<n-Gram> <PostingType>

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

Example usage:

"E:\IR\HW3\final corpus" "E:\IR\HW3\output" 1 
"E:\IR\HW3\final corpus" "E:\IR\HW3\output" 1 "termfreq"

>> Above two examples are doing the same thing

==============================================================================================================
 _______________
|		|
|    TASK 2C	|
|_______________|

Run the indexer program thrice with program arguments as follows:
 commands for generating 3 inverted indexes corresponding to terms as word n-grams (n=1,2,3)

$ java indexer "<CleanCorpusDirectoryPath>" "<OutputDirectoryPath>" 1
$ java indexer "<CleanCorpusDirectoryPath>" "<OutputDirectoryPath>" 2
$ java indexer "<CleanCorpusDirectoryPath>" "<OutputDirectoryPath>" 3

Expected Output	:
--- files woulb be saved in directory ... / / ...

==============================================================================================================
TASK 2-D 
Run the following command for generating inverted index with term positions (for term as unigram)
==============================================================================================================
$ java indexer "E:\IR\HW3\final corpus" 1 "tf+positions"

Expected Output: ???

___________________________________________________________________________________________________________________

Sources used :

https://docs.python.org/3/library/
____________________________________________________________________________________________________________________

DIRECTORY STRUCTURE : 

FOLDER	|	DELIVERABLES		|									|
--------|-------------------------------|------------------------------------------------------------------ 	|
TASK1	|	G1.txt			|    Graph for Task 1-A							|
	|	G2.txt			|    Graph for Task 1-A							|
	|       STATISTICS_REPORT_TASK1	|    Simple Statistics for G1 and G2					|
	|				|									|
TASK 2	|	src 			|    Folder containing source code for page rank implementation		|
	|	G1-Perplexities.txt	|    Perxplexity values in each round, till convergence(for G1) 	|
	|   	G2-Perplexities.txt   	|    Perxplexity values in each round, till convergence(for G2) 	|
	|   	G1_Top50PageRanks.txt  	|    Top 50 pages from G1 - docID and pageRank 				|
	|   	G2_Top50PageRanks.txt  	|    Top 50 pages from G2 - docID and pageRank				|
	|   	G1_Top50Inlinks.txt   	|    Top 50 pages from G1 - docID and inlink count 			|
	|   	G2_Top50Inlinks.txt   	|    Top 50 pages from G2 - docID and inlink count			|
	|   	Task-2C_report.txt   	|    Comparison report for Task 2-C					|
______________________________________________________________________________________________________________________

DESCRIPTION :

G1.txt and G2.txt are the graphs generated from BFS and DFS respectively. 
The graph follows the pattern 

D1 D2 D3 D4
D2 D5 D6
D3 D7 D8

Where, D1 is the webpage docID which is the article title directly extracted from the URL (e.g., Solar_Eclipse is the docID for https://en.wikipedia.org/wiki/Solar_Eclipse). Each line indicates the in-link relationship, which means that D1 has three in-coming links from D2, D3, and D4 respectively.

The file "STATISTICS_REPORT_TASK1" has the statistics for G1 and G2 with the no. of pages with no inLinks and no. of pages with no outLinks.

The file "Task-2C_report.txt" examines the Top 10 page rank and the Top 10 by inLink counts for G1 and G2. 
It also discusses the effect on ranking upon changing the damping factor.

______________________________________________________________________________________________________________________