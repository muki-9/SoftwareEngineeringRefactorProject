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
    private boolean useIS = false;

    @Override
    public void exec(ArrayList<String> args, InputStream input, OutputStream output) throws IOException {
        String currentDirectory = Jsh.getCurrentDirectory();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));
        validateArgs(args, input);
        int numOfFiles;

        if (useIS) {
            String s = new String(input.readAllBytes());
            String[] lines = s.split(System.getProperty("line.separator"));
            ArrayList<String> tempArray = new ArrayList<>();
            ArrayList<ArrayList<String>> lineArray = new ArrayList<>();
            for(String temp:lines) {
                tempArray.add(temp);
            }
            lineArray.add(tempArray);
            if (args.size()==0) {
                writeAll(getLineArray(lineArray), getWordArray(lineArray), getCharArray(lineArray), null, writer);
            }
            else if (args.size() == 1) {
                performWc(args.get(0), lineArray, null, writer);
            }
            else {
                throw new RuntimeException("wc: wrong arguments");
            }
        }
        else {
            numOfFiles = args.size();
            int offset = 0;
            
            if (args.get(0).equals("-m")|| args.get(0).equals("-w") || args.get(0).equals("-l")) {
                offset = 1;
                numOfFiles -= 1;
            }
            if (numOfFiles > 1) {
                totalNeeded = true;
            }
            Path[] filePathArray = getFilePathArray(currentDirectory, numOfFiles, offset, args);
            ArrayList<ArrayList<String>> fileLines = getLines(filePathArray);
            performWc(args.get(0), fileLines, filePathArray, writer);
        }
    }


    private void performWc(String string, ArrayList<ArrayList<String>> fileLines, Path[] filePathArray,
            BufferedWriter writer) throws IOException {
                ArrayList<String> pathArray;
        if (filePathArray == null) {
            pathArray = null;
        }
        else {
            pathArray = getFileNameArray(filePathArray);
        }
        switch (string) {
            case "-m":
                wcWrite(getCharArray(fileLines), pathArray, writer);
                break;
            case "-w":
                wcWrite(getWordArray(fileLines), pathArray, writer);
                break;
            case "-l":
                wcWrite(getLineArray(fileLines), pathArray, writer);
                break;
            default:
                writeAll(getLineArray(fileLines), getWordArray(fileLines), getCharArray(fileLines), pathArray, writer);
        }
    }

    private ArrayList<ArrayList<String>> getLines(Path[] filePathArray) throws IOException {
        Charset encoding = StandardCharsets.UTF_8;
        String line;
        ArrayList<ArrayList<String>> multipleFileLineArray = new ArrayList<>();
        for(Path filePath:filePathArray) {
            ArrayList<String> lines = new ArrayList<>();
            try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
                reader.close();
            } catch (IOException e) {
                throw new RuntimeException("wc: cannot open " + filePathArray.toString());
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
                useIS = true;
                return;
            }
        }
        else if (args.size()==1) {
            if ((args.get(0).equals("-m")|| args.get(0).equals("-w") || args.get(0).equals("-l")) && input != null) {
                useIS = true;
                return;
            }
            else {
                    throw new RuntimeException("wc: wrong number of arguments"); 
            }
        }
    }

    private void writeAll(ArrayList<Long> arrayList, ArrayList<Long> arrayList2, ArrayList<Long> arrayList3,
            ArrayList<String> fileNameArray, BufferedWriter writer) throws IOException {
        boolean piping = false;
        if (fileNameArray == null) {
            piping = true;
        }
        int totalLineCount = 0;
        int totalWordCount = 0;
        Long totalByteCount = (long) 0;
        for (int i = 0; i < arrayList.size(); i++) {
            totalByteCount += arrayList3.get(i);
            totalWordCount += arrayList2.get(i);
            totalLineCount += arrayList.get(i);
            writer.write(Long.toString(arrayList.get(i)));
            writer.write("\t");
            writer.write(Long.toString(arrayList2.get(i)));
            writer.write("\t");
            writer.write(Long.toString(arrayList3.get(i)));
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
            writer.write(Long.toString(totalByteCount));
            writer.write("\t");
            writer.write("total ");
            writer.write(System.getProperty("line.separator"));
            writer.flush();
        }
    }

    private void wcWrite(ArrayList<Long> arrayList, ArrayList<String> fileNames, BufferedWriter writer)
            throws IOException {
        boolean piping = false;
        if (fileNames == null) {
            piping = true;
        }
        int totalCount = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            totalCount += arrayList.get(i);
            writer.write(Long.toString(arrayList.get(i)));
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

    private ArrayList<Long> getLineArray(ArrayList<ArrayList<String>> fileLines) {
        ArrayList<Long> vals = new ArrayList<>();
        for (ArrayList<String> lines : fileLines) {
            vals.add(calcNewlines(lines));
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

    private ArrayList<Long> getCharArray(ArrayList<ArrayList<String>> fileLines) {
        ArrayList<Long> vals = new ArrayList<>();
        for (ArrayList<String> lines : fileLines) {
            vals.add(calcChars(lines));
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

    private String getFileName(String string) {
        return string.split("/")[string.split("/").length-1];
    }

    private Long calcWords(ArrayList<String> lines){
        int wordCount = 0;
        for(String s:lines) {
            String[] previsionalWords = s.split("[\\s]+");
            ArrayList<String> words = new ArrayList<>();
            for (int i=0; i<previsionalWords.length;i++) {
                if (previsionalWords[i].length() > 0) {
                    words.add(previsionalWords[i]);
                }
            }
            wordCount += words.size(); 
        }
        return (long) wordCount;
    }

    private Long calcNewlines(ArrayList<String> lines){
        return (long) (lines.size());
    }

    private Long calcChars(ArrayList<String> lines) {
        int charCount = 0;
        int count = 0;
        for (String line:lines) {
            count++;
            charCount+= line.length();
            if (count <= (lines.size())) {
                charCount++;
            }
        }
        return (long) charCount;
    }
}