package uk.ac.ucl.applications;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import uk.ac.ucl.jsh.Application;
import uk.ac.ucl.jsh.Jsh;

public class Wc implements Application {

    private Boolean totalNeeded = false;

    @Override
    public void exec(ArrayList<String> args, InputStream input, OutputStream output) throws IOException {
        String currentDirectory = Jsh.getCurrentDirectory();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));
        validateArgs(args);
        int numOfFiles = args.size();
        int offset = 0;
        if (args.get(0).equals("-m")|| args.get(0).equals("-w") || args.get(0).equals("-l")) {
            offset = 1;
            numOfFiles -= 1;
        }
        if (numOfFiles > 1) {
            totalNeeded = true;
        }


        Path[] filePathArray = getFilePathArray(currentDirectory, numOfFiles, offset, args);
        switch (args.get(0)) {
            case "-m":
                wcWrite(getCharArray(filePathArray), getFileNameArray(filePathArray), writer);
                break;
            case "-w":
                wcWrite(getWordArray(filePathArray), getFileNameArray(filePathArray), writer);
                break;
            case "-l":
                wcWrite(getLineArray(filePathArray), getFileNameArray(filePathArray), writer);
                break;
            default:
                writeAll(getLineArray(filePathArray), getWordArray(filePathArray), getByteArray(filePathArray), getFileNameArray(filePathArray), writer);
        }
    }

    private void writeAll(ArrayList<Integer> lineArray, ArrayList<Integer> wordArray, ArrayList<Long> byteArray, ArrayList<String> fileNameArray, BufferedWriter writer) throws IOException {
        int totalLineCount = 0;
        int totalWordCount = 0;
        Long totalByteCount = (long) 0;
        for (int i = 0; i < lineArray.size(); i++) {
            totalByteCount += byteArray.get(i);
            totalWordCount += wordArray.get(i);
            totalLineCount += lineArray.get(i);
            writer.write(Integer.toString(lineArray.get(i)));
            writer.write("\t");
            writer.write(Integer.toString(wordArray.get(i)));
            writer.write("\t");
            writer.write(Long.toString(byteArray.get(i)));
            writer.write("\t");
            writer.write(fileNameArray.get(i));
            writer.write(System.getProperty("line.separator"));
            writer.flush();
        }
        if (totalNeeded) {
            writer.write(Integer.toString(totalLineCount));
            writer.write("\t");
            writer.write(Integer.toString(totalWordCount));
            writer.write("\t");
            writer.write(Long.toString(totalByteCount));
            writer.write("\t");
            writer.write("total ");
            writer.write(System.getProperty("line.separator"));
            writer.flush();
        }
    }

    private void wcWrite(ArrayList<Integer> arrayList, ArrayList<String> fileNames, BufferedWriter writer) throws IOException {
        int totalCount = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            totalCount += arrayList.get(i);
            writer.write(Integer.toString(arrayList.get(i)));
            writer.write("\t");
            writer.write(fileNames.get(i));
            writer.write(System.getProperty("line.separator"));
            writer.flush();
        }
        if (totalNeeded) {
            writer.write(Integer.toString(totalCount));
            writer.write("\t");
            writer.write("total ");
            writer.write(System.getProperty("line.separator"));
            writer.flush();
        }
    }

    private ArrayList<String> getFileNameArray(Path[] filePathArray) {
        ArrayList<String> fileNames = new ArrayList<>();
        for (Path path : filePathArray) {
            fileNames.add(getFileName(path.toString()));
        }
        return fileNames;
    }

    private ArrayList<Integer> getLineArray(Path[] filePathArray) {
        ArrayList<Integer> vals = new ArrayList<>();
        for (Path path : filePathArray) {
            vals.add(calcNewlines(path));
        }
        return vals;
    }

    private ArrayList<Integer> getWordArray(Path[] filePathArray) {
        ArrayList<Integer> vals = new ArrayList<>();
        for (Path path : filePathArray) {
            vals.add(calcWords(path));
        }
        return vals;
    }

    private ArrayList<Integer> getCharArray(Path[] filePathArray) {
        ArrayList<Integer> vals = new ArrayList<>();
        for (Path path : filePathArray) {
            vals.add(calcChars(path));
        }
        return vals;
    }

    private ArrayList<Long> getByteArray(Path[] filePathArray) {
        ArrayList<Long> vals = new ArrayList<>();
        for (Path path : filePathArray) {
            vals.add(calcBytes(path));
        }
        return vals;
    }

    private Path[] getFilePathArray(String currentDirectory, int numOfFiles, int offset, ArrayList<String> args) {
        Path filePath;
        Path currentDir = Paths.get(currentDirectory);
        Path[] filePathArray = new Path[numOfFiles];

        for (int i = 0; i < numOfFiles; i++) {
            filePath = currentDir.resolve(args.get(i+offset));
            if (Files.notExists(filePath) || Files.isDirectory(filePath) || !Files.exists(filePath) || !Files.isReadable(filePath)) {
                throw new RuntimeException("wc: wrong file argument");
            } else {
                filePathArray[i] = filePath;
            }
        }
        return filePathArray;
    }

    private void validateArgs(ArrayList<String> args) {
        if (args.size()==0) {
            throw new RuntimeException("wc: wrong number of arguments");
        }
    }

    private String getFileName(String string) {
        return string.split("/")[((string.split("/").length)-1)];
    }

    private Long calcBytes(Path path) {
        File file = path.toFile();
        long length = file.length();
        return length;
    }

    private int calcWords(Path path){
        int wordCount = 0;
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                String[] previsionalWords = line.split("[\\s]+");
                ArrayList<String> words = new ArrayList<>();
                
                for (int i=0; i<previsionalWords.length;i++) {
                    if (previsionalWords[i].length() > 0) {
                        words.add(previsionalWords[i]);
                    }
                }
                wordCount += words.size();
            }
        } catch (IOException e) {
            throw new RuntimeException("wc: cannot open " + path.toString());
        }
        return wordCount;
    }

    private int calcNewlines(Path path){
        int newlineCount = 0;
        try (Scanner scanner = new Scanner(path)) {
            while (scanner.hasNextLine()) {
                newlineCount += 1;
                scanner.nextLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("wc: cannot open " + path.toString());
        }
        // newlineCount -= 1;
        return newlineCount;
    }

    private int calcChars(Path path){
        int charCount = 0;
        try (Scanner scanner = new Scanner(path)) {
            String line = null;
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                charCount += line.length();
                if (scanner.hasNextLine()) {
                    charCount++; //+1 to account for new line character
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("wc: cannot open " + path.toString());
        }
        return charCount;
    }
}