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
import uk.ac.ucl.jsh.Application;
import uk.ac.ucl.jsh.Jsh;

public class Wc implements Application {

    private boolean totalNeeded = false;
    private boolean useInputStream = false;
    private int newlineCount = 0;

    @Override
    public void exec(ArrayList<String> args, InputStream input, OutputStream output) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));
        validateArgs(args, input); // ensures that stdin is being used if required instead of command line arguments

        if (useInputStream) {
            // Below performs wc application using stdin
            String s = new String(input.readAllBytes());
            String[] lines = s.split(System.getProperty("line.separator"));

            ArrayList<String> lineArray = new ArrayList<>();
            ArrayList<ArrayList<String>> fileLines = new ArrayList<>();

            for(String temp : lines) {
                lineArray.add(temp);
            }

            fileLines.add(lineArray);

            if (args.size()==0) {
                // performs all functions of wc when no option is specified using IS methods
                writeAll(getNewlineArrayIS(fileLines), getWordArray(fileLines), getCharArrayIS(fileLines), null, writer);
            }
            else if (args.size() == 1) {
                // performs function specified by option
                performWc(args.get(0), fileLines, null, writer);
            }
            else {
                throw new RuntimeException("wc: wrong arguments");
            }
        }
        else {
            String currentDirectory = Jsh.getCurrentDirectory();
            int numOfFiles = args.size();
            int offset = 0;
            
            // this if statement ensures that the for loop in getFilePathArray doesn't treat an option as a file
            if (args.get(0).equals("-m") || args.get(0).equals("-w") || args.get(0).equals("-l")) {
                offset = 1;
                numOfFiles -= 1;
            }

            // if multiple files are being used, a total count is also printed
            if (numOfFiles > 1) {
                totalNeeded = true;
            }

            Path[] filePathArray = getFilePathArray(currentDirectory, numOfFiles, offset, args);
            ArrayList<ArrayList<String>> fileLines = getLines(filePathArray);

            performWc(args.get(0), fileLines, filePathArray, writer);
        }
    }


    private void performWc(String optionType, ArrayList<ArrayList<String>> fileLines, Path[] filePathArray, BufferedWriter writer) throws IOException {
        ArrayList<String> pathArray;

        if (filePathArray == null) {
            pathArray = null;
        }
        else {
            pathArray = getFileNameArray(filePathArray);
        }

        switch (optionType) {
            case "-m":
                // uses different methods depending on whether IS is used or not
                if(useInputStream){
                    ArrayList<Long> tempNewLineArray = getNewlineArrayIS(fileLines); // ensures global Newline Count is accurate
                    wcWrite(getCharArrayIS(fileLines), pathArray, writer);
                }
                else{
                    wcWrite(getCharArray(fileLines, getNewlineArray(filePathArray)), pathArray, writer);
                }
                break;
            case "-w":
                wcWrite(getWordArray(fileLines), pathArray, writer);
                break;
            case "-l":
                // uses different methods depending on whether IS is used or not
                if(useInputStream){
                    wcWrite(getNewlineArrayIS(fileLines), pathArray, writer);
                }
                else{
                    wcWrite(getNewlineArray(filePathArray), pathArray, writer);
                }
                break;
            default:
                writeAll(getNewlineArray(filePathArray), getWordArray(fileLines), getCharArray(fileLines, getNewlineArray(filePathArray)), pathArray, writer);
        }
    }

    private ArrayList<ArrayList<String>> getLines(Path[] filePathArray) throws IOException {
        Charset encoding = StandardCharsets.UTF_8;
        ArrayList<ArrayList<String>> multipleFileLineArray = new ArrayList<>();
        String line;

        // reads in all lines of each file and returns these in form of ArrayList
        for(Path filePath : filePathArray) {
            ArrayList<String> lines = new ArrayList<>();
            try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
                reader.close();
            } catch (IOException e) {
                throw new RuntimeException("wc: cannot open " + getFileName(filePathArray.toString()));
            }
            multipleFileLineArray.add(lines);           
        }
        return multipleFileLineArray;
    }

    private void validateArgs(ArrayList<String> args, InputStream input) {
        if (args.size()==0) {
            if (input == null) {
                throw new RuntimeException("wc: wrong number of arguments");
            }
            else {
                useInputStream = true;
                return;
            }
        }
        else if (args.size()==1) {
            if ((args.get(0).equals("-m")|| args.get(0).equals("-w") || args.get(0).equals("-l")) && input != null) {
                useInputStream = true;
            }
        }
    }

    private void writeAll(ArrayList<Long> newlineArray, ArrayList<Long> wordArray, ArrayList<Long> charArray, ArrayList<String> fileNameArray, BufferedWriter writer) throws IOException {
        boolean piping = false;
        if (fileNameArray == null) {
            piping = true;
        }

        int totalLineCount = 0;
        int totalWordCount = 0;
        Long totalCharCount = (long) 0;

        for (int i = 0; i < newlineArray.size(); i++) {
            totalCharCount += charArray.get(i);
            totalWordCount += wordArray.get(i);
            totalLineCount += newlineArray.get(i);

            // writes the number of newLines, words, chars, and if multiple files, totals of them
            writer.write(Long.toString(newlineArray.get(i)));
            writer.write("\t");
            writer.write(Long.toString(wordArray.get(i)));
            writer.write("\t");
            writer.write(Long.toString(charArray.get(i)));
            writer.write("\t");

            if (!piping) {
                writer.write(fileNameArray.get(i));
            }

            writer.write(System.getProperty("line.separator"));
            writer.flush();
        }

        if (totalNeeded) {
            writer.write(Integer.toString(totalLineCount));
            writer.write("\t");
            writer.write(Integer.toString(totalWordCount));
            writer.write("\t");
            writer.write(Long.toString(totalCharCount));
            writer.write("\t");
            writer.write("total");
            writer.write(System.getProperty("line.separator"));
            writer.flush();
        }
    }

    private void wcWrite(ArrayList<Long> countList, ArrayList<String> fileNames, BufferedWriter writer) throws IOException {
        // writes the number of either newLines, words, chars depending on which option was used
        // writes totals of them if multiple files

        boolean piping = false;
        if (fileNames == null) {
            piping = true;
        }

        int totalCount = 0;
        for (int i = 0; i < countList.size(); i++) {
            totalCount += countList.get(i);

            writer.write(Long.toString(countList.get(i)));
            writer.write("\t");

            if (!piping) {
                writer.write(fileNames.get(i));
            }

            writer.write(System.getProperty("line.separator"));
            writer.flush();
        }

        if (totalNeeded) {
            writer.write(Long.toString(totalCount));
            writer.write("\t");
            writer.write("total");
            writer.write(System.getProperty("line.separator"));
            writer.flush();
        }
    }

    private ArrayList<String> getFileNameArray(Path[] filePathArray) {
        // returns the filenames given their paths using getFileName function for each path in array

        ArrayList<String> fileNames = new ArrayList<>();
        for (Path path : filePathArray) {
            fileNames.add(getFileName(path.toString()));
        }
        return fileNames;
    }

    private ArrayList<Long> getNewlineArray(Path[] filePathArray) throws IOException {
        Charset encoding = StandardCharsets.UTF_8;
        ArrayList<Long> newlineArray = new ArrayList<>();
        int value;

        // reads in each file char by char to count the number of newline characters seen
        for(Path filePath : filePathArray) {
            long newlineTempCount = 0;
            try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {
                while((value = reader.read()) != -1) {
                    char c = (char) value;
                    if (c == '\n'){
                        newlineTempCount += 1;
                    }
                }
                reader.close();
            } catch (IOException e) {
                throw new RuntimeException("wc: cannot open " + getFileName(filePathArray.toString()));
            }    
            newlineArray.add(newlineTempCount);           
        }
        return newlineArray;
        
    }

    private ArrayList<Long> getNewlineArrayIS(ArrayList<ArrayList<String>> fileLines) {
        ArrayList<Long> vals = new ArrayList<>();
        for (ArrayList<String> lines : fileLines) {
            vals.add(calcNewlinesIS(lines));
        }
        return vals;
    }

    private ArrayList<Long> getWordArray(ArrayList<ArrayList<String>> fileLines) {
        ArrayList<Long> vals = new ArrayList<>();
        for (ArrayList<String> lines : fileLines) {
            vals.add(calcWords(lines));
        }
        return vals;
    }

    private ArrayList<Long> getCharArray(ArrayList<ArrayList<String>> fileLines, ArrayList<Long> newlineArray) {
        ArrayList<Long> vals = new ArrayList<>();
        int count = 0;
        for (ArrayList<String> lines : fileLines) {
            vals.add(calcChars(lines, newlineArray.get(count)));
            count++;
        }
        return vals;
    }

    private ArrayList<Long> getCharArrayIS(ArrayList<ArrayList<String>> fileLines) {
        ArrayList<Long> vals = new ArrayList<>();
        for (ArrayList<String> lines : fileLines) {
            vals.add(calcCharsIS(lines));
        }
        return vals;
    }

    private Path[] getFilePathArray(String currentDirectory, int numOfFiles, int offset, ArrayList<String> args) {
        Path filePath;
        Path currentDir = Paths.get(currentDirectory);
        Path[] filePathArray = new Path[numOfFiles];

        // if file specified in command line by user is a real/usable file, it will return an array of the paths of the files
        for (int i = 0; i < numOfFiles; i++) {
            filePath = currentDir.resolve(args.get(i + offset));
            if (Files.notExists(filePath) || Files.isDirectory(filePath) || !Files.exists(filePath) || !Files.isReadable(filePath)) {
                throw new RuntimeException("wc: wrong file argument");
            } else {
                filePathArray[i] = filePath;
            }
        }
        return filePathArray;
    }

    private String getFileName(String string) {
        return string.split("/")[string.split("/").length-1];
    }

    private Long calcWords(ArrayList<String> lines){
        // splits each line into an array of words in the line, and returns the number of words in the array
        int wordCount = 0;
        for(String s : lines) {
            String[] previsionalWords = s.split("[\\s]+");
            ArrayList<String> words = new ArrayList<>();

            for (int i=0; i < previsionalWords.length; i++) {
                if (previsionalWords[i].length() > 0) {
                    words.add(previsionalWords[i]);
                }
            }
            wordCount += words.size(); 
        }
        return (long) wordCount;
    }

    private Long calcNewlinesIS(ArrayList<String> lines){
        // returns array size as number of newlines if IS is used
        newlineCount = lines.size();
        return (long) (newlineCount);
    }

    private Long calcChars(ArrayList<String> lines, Long newlineCountArg) {
        // calculates number of chars in each line and accounts for number of newline characters
        long charCount = newlineCountArg;
        for (String line : lines) {
            charCount += line.length();
        }
        return (long) charCount;
    }

    private Long calcCharsIS(ArrayList<String> lines) {
        // calculates number of chars in each line and accounts for number of newline characters
        int charCount = newlineCount;
        for (String line : lines) {
            charCount += line.length();
        }
        return (long) charCount;
    }
}