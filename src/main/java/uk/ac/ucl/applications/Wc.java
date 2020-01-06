package uk.ac.ucl.applications;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.ucl.jsh.Application;
import uk.ac.ucl.jsh.Globbing;
import uk.ac.ucl.jsh.Jsh;

public class Wc implements Application {

    private boolean totalNeeded = false;
    private boolean useInputStream = false;

    @Override
    public void exec(ArrayList<String> args, InputStream input, OutputStream output, ArrayList<Boolean> globbArray) throws IOException {
        Globbing g = new Globbing(globbArray);
        ArrayList<String> updatedArgs = g.globbing(args);

        validateArgs(updatedArgs, input);

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));

        if (useInputStream) {
            String s = new String(input.readAllBytes());
            String[] lines = s.split(System.getProperty("line.separator"));

            if (args.size() == 0) {
                writeUsingInputStream(writer, lines, "all");
            }
            else{
                writeUsingInputStream(writer, lines, args.get(0));
            }
        }
        else {
            int numOfFiles = args.size();
            int offset = 0;

            // if statement ensures that for loop in getFilePathArray doesn't treat option as a file
            if (args.get(0).equals("-m") || args.get(0).equals("-w") || args.get(0).equals("-l")) {
                offset = 1;
                numOfFiles -= 1;
            }

            if (numOfFiles > 1) {
                totalNeeded = true;
            }

            Path[] filePathArray = getFilePathArray(Jsh.getCurrentDirectory(), numOfFiles, offset, args);

            if (args.get(0).equals("-m") || args.get(0).equals("-w") || args.get(0).equals("-l")) {
                writeUsingFiles(writer, filePathArray, args.get(0));
            }
            else {
                writeUsingFiles(writer, filePathArray, "all");
            }
        }
    }

    /*

        Method loops through files and adds path to returned array if valid.

    */
    private Path[] getFilePathArray(String currentDirectory, int numOfFiles, int offset, ArrayList<String> args) {
        Path filePath;
        Path currentDir = Paths.get(currentDirectory);
        Path[] filePathArray = new Path[numOfFiles];

        // if file specified in command line by user is a real/usable file, it will return an array of the paths of the files
        for (int i = 0; i < numOfFiles; i++) {
            filePath = currentDir.resolve(args.get(i + offset));
            if (Files.isDirectory(filePath) || !Files.isReadable(filePath)){
                throw new RuntimeException("wc: wrong file argument");
            }
            else {
                filePathArray[i] = filePath;
            }
        }
        return filePathArray;
    }

    /*

        Method removes directory details to obtain only file name when given path as string.

    */
    private String getFileName(String string) {
        return string.split("/")[string.split("/").length-1];
    }

    /*

        Method stores word, char and newline counts for each file in hashmap.
        Writes count specified by option to OutputStream, and total if neccesary.

    */
    private void writeUsingFiles(BufferedWriter writer, Path[] filePathArray, String option) throws IOException {
        int totalLineCount = 0;
        int totalWordCount = 0;
        int totalCharCount = 0;
        int totalCount = 0;

        List<String> options = Arrays.asList("-l", "-w", "-m");
        
        for(Path path : filePathArray){
            Map<String, String> resultsHashMap  = new HashMap<>();

            resultsHashMap.put("-w", calcWords(path));
            resultsHashMap.put("-m", calcChars(path));
            resultsHashMap.put("-l", calcNewlines(path));
            
            if (option != "all") {
                String result = resultsHashMap.get(option);
                totalCount += Integer.parseInt(result);
                writer.write(result + '\t');

            }
            else {
                List<String> result = new ArrayList<>();

                for(String opt : options) {
                    result.add(resultsHashMap.get(opt));
                    writer.write(resultsHashMap.get(opt) + '\t');
                    writer.flush();
                }

                totalCharCount+= Integer.parseInt(result.get(2));
                totalWordCount+= Integer.parseInt(result.get(1));
                totalLineCount+= Integer.parseInt(result.get(0));
            }

            writer.write(getFileName(path.toString()));
            writer.write(System.getProperty("line.separator"));
            writer.flush();
        }

        Map<String, String> totalsHashMap  = new HashMap<>();
        totalsHashMap.put("-m", Integer.toString(totalCharCount));
        totalsHashMap.put("-w", Integer.toString(totalWordCount));
        totalsHashMap.put("-l", Integer.toString(totalLineCount));
        
        if (totalNeeded) {
            if(option != "all"){
                writer.write(Integer.toString(totalCount) + '\t');
            }
            else {
                for(String opt : options) {
                    writer.write(totalsHashMap.get(opt) + '\t');
                    writer.flush();
                }
            }
            writer.write("total");
            writer.write(System.getProperty("line.separator"));
            writer.flush();
        }
    }

    /*

        Method stores word, char and newline counts for stdin in hashmap.
        Writes count specified by option to OutputStream, and total if neccesary.

    */
    private void writeUsingInputStream(BufferedWriter writer, String[] lines, String option) throws IOException {

        List<String> options  = Arrays.asList("-l", "-w", "-m");
        Map<String, String> resultsHashMap  = new HashMap<>();

        resultsHashMap.put("-w", calcWords(lines));
        resultsHashMap.put("-m", calcChars(lines));
        resultsHashMap.put("-l", Integer.toString(lines.length));
        
        if(option != "all"){
            writer.write(resultsHashMap.get(option));
        }
        else {
            for(String opt : options){
                writer.write(resultsHashMap.get(opt) + '\t');
            }
        }

        writer.write(System.getProperty("line.separator"));
        writer.flush();
    }

    /*

        Method to calculate newline count when using Files.
        Increases a counter everytime '\n' is seen and returns counter as string.

    */
    private String calcNewlines(Path path) throws IOException {
        Charset encoding = StandardCharsets.UTF_8;
        int value;
        int newlineCount = 0;

        try (BufferedReader reader = Files.newBufferedReader(path, encoding)) {
            while((value = reader.read()) != -1) {
                char c = (char) value;
                if (c == '\n'){
                    newlineCount += 1;
                }
            }
            reader.close();
        }              
        return Integer.toString(newlineCount);
    }

    /*

        Method to calculate char count when using Files.
        Increases a counter by number of chars in line, accounting for newline char.
        Returns string form of counter.

    */
    private String calcChars(Path path) throws IOException {
        int charCount = Integer.parseInt(calcNewlines(path));
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                charCount += line.length();
            }
        }
        return Integer.toString(charCount);
    }

    /*

        Method to calculate char count when using stdin.
        Increases a counter by number of chars in line, accounting for newline char.
        Returns string form of counter.

    */
    private String calcChars(String[] lines) {
        int charCount = lines.length;
        for (String line : lines) {
            charCount += line.length();
        }
        return Integer.toString(charCount);
    }

    /*

        Method to calculate word count when using Files.
        Increases a counter by size of array of words in each line, for every line.
        Returns string form of counter.

    */
    private String calcWords(Path path) throws IOException {
        int wordCount = 0;
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                String[] preWords = line.split("[\\s]+");
                ArrayList<String> words = new ArrayList<>();

                for (int i = 0; i < preWords.length; i++) {
                    if (preWords[i].length() > 0) {
                        words.add(preWords[i]);
                    }
                }
                wordCount += words.size(); 
            }
        }
        return Integer.toString(wordCount);
    }

    /*

        Method to calculate word count when using stdin.
        Increases a counter by size of array of words in each line, for every line.
        Returns string form of counter.

    */
    private String calcWords(String[] lines){
        int wordCount = 0;
        for(String line : lines) {
            String[] preWords = line.split("[\\s]+");
            ArrayList<String> words = new ArrayList<>();

            for (int i = 0; i < preWords.length; i++) {
                if (preWords[i].length() > 0) {
                    words.add(preWords[i]);
                }
            }
            wordCount += words.size(); 
        }
        return Integer.toString(wordCount);
    }

    /*

        Method checks args using if statements to update useInputStream if stdin is to be used and ensures options valid.

    */
    private void validateArgs(ArrayList<String> args, InputStream input) {
        if (args.size() == 0) {
            if (input == null) {
                throw new RuntimeException("wc: wrong number of arguments");
            }
            else {
                useInputStream = true;
                return;
            }
        }

        if (args.get(0).startsWith("-") && !(args.get(0).equals("-m") || args.get(0).equals("-w") || args.get(0).equals("-l"))) {
            throw new RuntimeException("wc: illegal option given");
        }

        if (args.size() == 1 && args.get(0).startsWith("-")) {
            if (input != null) {
                useInputStream = true;           
            }
            else {
                throw new RuntimeException("wc: wrong number of arguments");
            }    
        }
    }
}