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
            else if (args.size() == 1) {
                writeUsingInputStream(writer, lines, args.get(0));
            }
            else {
                throw new RuntimeException("wc: wrong arguments");
            }
        }
        else {
            int numOfFiles = args.size();
            int offset = 0;

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

    private Path[] getFilePathArray(String currentDirectory, int numOfFiles, int offset, ArrayList<String> args) {
        Path filePath;
        Path currentDir = Paths.get(currentDirectory);
        Path[] filePathArray = new Path[numOfFiles];

        // if file specified in command line by user is a real/usable file, it will return an array of the paths of the files
        for (int i = 0; i < numOfFiles; i++) {
            filePath = currentDir.resolve(args.get(i + offset));
            if (Files.isDirectory(filePath) || !Files.isReadable(filePath)) {
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

    private void writeUsingFiles(BufferedWriter writer, Path[] filePathArray, String option) throws IOException {
        int totalLineCount = 0;
        int totalWordCount = 0;
        int totalCharCount = 0;
        for(Path path : filePathArray){
            switch (option) {
                case "-m":
                    String chars = calcChars(path);
                    totalCharCount += Integer.parseInt(chars);
                    writer.write(chars);
                    writer.write("\t");
                    writer.write(getFileName(path.toString()));
                    writer.write(System.getProperty("line.separator"));
                    writer.flush();
                    break;
                case "-w":
                    String words = calcWords(path);
                    totalWordCount += Integer.parseInt(words);
                    writer.write(words);
                    writer.write("\t");
                    writer.write(getFileName(path.toString()));
                    writer.write(System.getProperty("line.separator"));
                    writer.flush();
                    break;
                case "-l":
                    String lines = calcNewlines(path);
                    totalLineCount += Integer.parseInt(lines);
                    writer.write(lines);
                    writer.write("\t");
                    writer.write(getFileName(path.toString()));
                    writer.write(System.getProperty("line.separator"));
                    writer.flush();            
                    break;
                case "all":
                    String chars1 = calcChars(path);
                    String words1 = calcWords(path);
                    String lines1 = calcNewlines(path);
                    totalCharCount += Integer.parseInt(chars1);
                    totalWordCount += Integer.parseInt(words1);
                    totalLineCount += Integer.parseInt(lines1);
                    writer.write(lines1);
                    writer.write("\t");
                    writer.write(words1);
                    writer.write("\t");
                    writer.write(chars1);
                    writer.write("\t");
                    writer.write(getFileName(path.toString()));
                    writer.write(System.getProperty("line.separator"));
                    writer.flush(); 
                    break;
            }
        }
        if (totalNeeded) {
            switch (option) {
                case "-m":
                    writer.write(Integer.toString(totalCharCount));
                    writer.write("\t");
                    writer.write("total");
                    writer.write(System.getProperty("line.separator"));
                    writer.flush();
                    break;
                case "-w":
                    writer.write(Integer.toString(totalWordCount));
                    writer.write("\t");
                    writer.write("total");
                    writer.write(System.getProperty("line.separator"));
                    writer.flush();
                    break;
                case "-l":
                    writer.write(Integer.toString(totalLineCount));
                    writer.write("\t");
                    writer.write("total");
                    writer.write(System.getProperty("line.separator"));
                    writer.flush();            
                    break;
                case "all":
                    writer.write(Integer.toString(totalLineCount)); 
                    writer.write("\t");
                    writer.write(Integer.toString(totalWordCount));
                    writer.write("\t");
                    writer.write(Integer.toString(totalCharCount));
                    writer.write("\t");
                    writer.write("total");
                    writer.write(System.getProperty("line.separator"));
                    writer.flush();
                    break;
            }
        }
    }

    private void writeUsingInputStream(BufferedWriter writer, String[] lines, String option) throws IOException {
        switch (option) {
            case "-m":
                writer.write(calcChars(lines));
                writer.write(System.getProperty("line.separator"));
                writer.flush();
                break;
            case "-w":
                writer.write(calcWords(lines));
                writer.write(System.getProperty("line.separator"));
                writer.flush();
                break;
            case "-l":
                writer.write(Integer.toString(lines.length));
                writer.write(System.getProperty("line.separator"));
                writer.flush();            
                break;
            case "all":
                writer.write(Integer.toString(lines.length));
                writer.write("\t");
                writer.write(calcWords(lines));
                writer.write("\t");
                writer.write(calcChars(lines));
                writer.write(System.getProperty("line.separator"));
                writer.flush(); 
                break;
        }
    }

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

    private String calcChars(String[] lines) {
        int charCount = lines.length;
        for (String line : lines) {
            charCount += line.length();
        }
        return Integer.toString(charCount);
    }

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
        else if (args.size() == 1) {
            if (args.get(0).equals("-m") || args.get(0).equals("-w") || args.get(0).equals("-l")) {
                if(input != null){
                    useInputStream = true; 
                }
                else {
                    throw new RuntimeException("wc: wrong number of arguments");
                }
            }
        }
    }
}