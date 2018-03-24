package parser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * The Parser class for parsing raw documents and cleaning them up
 * @author  Karan Tyagi
 * @version 1.0
 * @since   2018-03-18
 */

public class Parser {

    private static int fileCount = 0;
    private static int currentfile = 1;
    private static String parsingOption = "default";
    private static String sourceDirectory = "";
    private static String outputDirectory = "";
    //private static String sourceDirectory = "E:\\1st - Career\\NEU_start\\@@Technical\\2 - sem\\IR\\assign3\\test docs";
    //private static String outputDirectory = "E:\\1st - Career\\NEU_start\\@@Technical\\2 - sem\\IR\\assign3\\test corpus";

    public static void main(String[] args){

        if(args.length==2){
            System.out.println(" 2 arguments");
            sourceDirectory = args[0];
            outputDirectory = args[1];
        }

        // parsingOption : default(apply both - case folding and de-punctuating), casefold, depunctuate

        else if(args.length==3){
            System.out.println("3 arguments");
            sourceDirectory = args[0];
            outputDirectory = args[1];
            parsingOption = args[2];
        }
        else
            System.out.println("Invalid Arguments. Enter arguments as specified");


        System.out.println("SOURCE DIR PATH : "+sourceDirectory );
        System.out.println("OUTPUT DIR PATH : "+outputDirectory );
        System.out.print("PARSING OPTION  : "+parsingOption);

        if (!new File(sourceDirectory).isDirectory())
        {
            try {
                throw new FileNotFoundException("Source Directory Not Found! Enter correct path for source directory");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        else
        {
            if (!new File(outputDirectory).isDirectory())
            {
                File dir = new File(outputDirectory);
                dir.mkdirs();
                System.out.print("\n\nOutput Directory Created");
            }

            // displayOption: 0  -> print only the file count in the given directory
            // displayOption: 1  -> print file count and all filenames in the given directory
            totalFiles(sourceDirectory,0);
            parseCollection(parsingOption, sourceDirectory);
            fileCount=0;
            totalFiles(outputDirectory,0); // 1 is for printing filenames in clean corpus directory

        }

    }
    /**
     * @param option -- specifies type of parsing
     * @param dirPath -path of directory having  raw docs (or collection)
     */
    public static void parseCollection(String option, String dirPath){

        try{
            currentfile = 1;
            Path path = Paths.get(dirPath);
            Files.list(path)
                    .forEach(p -> {
                        try {
                            parseFile(option, p.toString());
                            currentfile++;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // System.out.println(p.toString());

                    });
            System.out.printf("\r%-3d Files Parsed. %-58s              |      Files remaining : 0 ",(currentfile-1)," ");
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void totalFiles(String dirPath,int displayOption){

        String dirName = dirPath.substring(dirPath.lastIndexOf(File.separator) + 1);
        System.out.println("\n");
        currentfile =1;
        try{
            Path path = Paths.get(dirPath);
            Files.list(path)
                    .forEach(p -> {

                        fileCount++;

                        if(displayOption == 1){   // displayOption 1 : Print all files in clean Corpus directory

                            System.out.println(currentfile+"  "+
                                    p.toString().substring(p.toString().lastIndexOf(File.separator) + 1));
                            currentfile++;
                        }
                    });
            System.out.println("Total Files in "+dirName+" : "+fileCount +"\n");
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param option
     * @param filePath
     * @throws IOException
     */
    private static void parseFile(String option, String filePath) throws IOException {

        String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
        fileName = fileName.substring(0, fileName.indexOf('.'));

        System.out.printf("\r%-3d Parsing : %-60s                |      Files remaining : %d ",currentfile,fileName,(fileCount-currentfile));

        FileReader fr = new FileReader(filePath);
        BufferedReader br= new BufferedReader(fr);

        StringBuilder cleanText = new StringBuilder();
        String Line;
        while((Line = br.readLine()) != null) {
            cleanText.append(Line);
        }

        Document doc = Jsoup.parse(cleanText.toString());

        // REMOVING ALL IMAGES AND FORMULAS
        doc.getElementsByTag("img").remove();

        // REMOVING ALL TABLES
        doc.getElementsByTag("table").remove();

        // REMOVING FORMS AND INPUTS
        doc.getElementsByTag("forms").remove();
        doc.getElementsByTag("input").remove();

        // REMOVING ALL URLS
        doc.getElementsByAttributeValueStarting("href", "http").removeAttr("href");
        doc.getElementsByAttributeValueStarting("href", "/wiki").removeAttr("href");
        doc.getElementsByAttributeValueStarting("href", "//en.wikipedia.org").removeAttr("href");
        doc.getElementsByAttributeValueStarting("href", "//").removeAttr("href");
        doc.getElementsByAttributeValueStarting("href", "/w/index.php").remove();

        // REMOVING NAVIGATIONAL COMPONENTS
        doc.getElementsByAttributeValueStarting("href", "#").remove();
        doc.getElementsByAttributeValueStarting("id", "jump-to-nav").remove();
        doc.getElementsByAttributeValueStarting("role", "navigation").remove();
        doc.getElementsByAttributeValueStarting("id", "mw-navigation").remove();
        doc.getElementsByAttributeValueStarting("id", "left-navigation").remove();
        doc.getElementsByAttributeValueStarting("id", "right-navigation").remove();

        // REMOVING UNNECESSARY TAGS
        doc.getElementsByTag("script").remove();
        doc.getElementsByTag("link").remove();
        doc.getElementsByAttributeValueStarting("class", "mw-editsection").remove();
        doc.getElementsByAttributeValueStarting("class", "citation book").remove();
        doc.getElementsByAttributeValueStarting("id", "section_SpokenWikipedia").remove();
        doc.getElementsByAttributeValueStarting("id", "mw-hidden-catlinks").remove();
        doc.getElementsByAttributeValueStarting("class", "reflist").remove();
        doc.getElementsByAttributeValueStarting("class", "printfooter").remove();
        doc.getElementsByAttributeValueStarting("id", "footer").remove();
        doc.getElementsByAttributeValueStarting("content", "http").remove();

        //System.out.println(doc.toString());

        // Remove extra whitespaces
        String parsedText = parseText(option, new String(doc.text()));

        // Paring complete.
        // Creating clean file now
        createParsedFile(fileName, parsedText.toString());
        br.close();
        fr.close();
    }

    private static String parseText(String option, String text) {

        String noExtraSpaces = new String(text);
        // removing tabs(extra space) and replacing it by space
        noExtraSpaces= noExtraSpaces.replace("\t", " ");
        // removing newline and replacing it by space
       noExtraSpaces= noExtraSpaces.replace("\n", " ");

        text = new String(noExtraSpaces);

        //Case folding and removing irrelevant text
        if(option.equals("default")){
            text = new String(caseFolding(text));
            text = new String(dePunctuating(text));
        }

        else if(option.equals("de-punctuate"))
            text = new String(dePunctuating(text));
        else if(option.equals("casefold"))
            text = new String(caseFolding(text));

        return text;
    }

    /*
     * Return a text by transforming to input to lower case
     */
    /**
     * @param
     * @return
     */
    private static String caseFolding(String text) {

        return text.toString().toLowerCase();

    }

    /*
     * Method to handlerPunctuation of the given text
     * Returns a text after removing extra spaces, non ASCII characters, all irrelevant special characters,
     * and the urls
     */
    /**
     * @param text
     * @return
     */

    private static String dePunctuating(String text) {

        String newText = text.toString();
        newText = newText
                .replace("[", "")
                .replace("=", "")
                .replace("\"", "")
                .replace("\'", "")
                .replace("(", "")
                .replace(")", "")
                .replace("]", "")
                .replace("{", "")
                .replace("}", "")
                .replace("~", "")
                .replaceAll("[\\[]edit", "")
                .replaceAll("http.*?\\s", "")
                .replaceAll("\'", "")
                .replaceAll("\\s{2,}", " ")
                .replaceAll("[^\\p{ASCII}]", "")
                .replaceAll("(?<![0-9a-zA-Z])[\\p{Punct}]", "")
                .replaceAll("(?<![0-9])[^\\P{P}-](?![0-9])", "") // retain hyphens in text
                //.replaceAll("(?<![0-9])[\\p{Punct}](?![0-9])", "")
                .replaceAll("[\\p{Punct}](?![0-9a-zA-Z])", "");

        return newText;
    }

    /**
     * @param outputFileName
     * @param parsedText
     * @throws IOException
     */
    private static void createParsedFile(String outputFileName, String parsedText) throws IOException {
                FileWriter fw = new FileWriter(outputDirectory + "/" + outputFileName + ".txt");
        BufferedWriter bw= new BufferedWriter(fw);

        bw.append(parsedText.toString());
        bw.close();
        fw.close();
    }

}
