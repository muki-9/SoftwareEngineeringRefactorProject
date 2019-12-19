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

        Path filePath;
        Path currentDir = Paths.get(currentDirectory);
        Path[] filePathArray = new Path[numOfFiles];

        for (int i = 0; i < numOfFiles; i++) {
            filePath = currentDir.resolve(args.get(i + offset));

            if (Files.notExists(filePath) || Files.isDirectory(filePath) || !Files.exists(filePath) || !Files.isReadable(filePath)) {
                throw new RuntimeException("wc: wrong file argument");
            } else {
                filePathArray[i] = filePath;
            }
        }

        switch (args.get(0)) {
            case "-m":
                for (Path path : filePathArray) 
                { 
                    writer.write(calcChars(path));
                    writer.write("\t");
                    writer.write(getFileName(path.toString()));
                    writer.write(System.getProperty("line.separator"));
                    writer.flush();
                }
                break;
            case "-w":
                for (Path path : filePathArray) 
                { 
                    writer.write(calcWords(path));
                    writer.write("\t");
                    writer.write(getFileName(path.toString()));
                    writer.write(System.getProperty("line.separator"));
                    writer.flush();
                }
                break;
            case "-l":
                for (Path path : filePathArray) 
                { 
                    writer.write(calcNewlines(path));
                    writer.write("\t");
                    writer.write(getFileName(path.toString()));
                    writer.write(System.getProperty("line.separator"));
                    writer.flush();
                }      
                break;
            default:
                for (Path path : filePathArray) 
                { 
                    writer.write(calcNewlines(path));
                    writer.write("\t");
                    writer.write(calcWords(path));
                    writer.write("\t");
                    writer.write(calcBytes(path));
                    writer.write("\t");
                    writer.write(getFileName(path.toString()));
                    writer.write(System.getProperty("line.separator"));
                    writer.flush();
                }
        }
    }

    private void validateArgs(ArrayList<String> args) {
        if (args.size()==0) {
            throw new RuntimeException("wc: wrong number of arguments");
        }
    }

    private String getFileName(String string) {
        return string.split("/")[((string.split("/").length)-1)];
    }

    private String calcBytes(Path path) {
        File file = path.toFile();
        long length = file.length();
        return Long.toString(length);
    }

    private String calcWords(Path path){
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
        return Integer.toString(wordCount);
    }

    private String calcNewlines(Path path){
        int newlineCount = 0;
        try (Scanner scanner = new Scanner(path)) {
            while (scanner.hasNextLine()) {
                newlineCount += 1;
                scanner.nextLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("wc: cannot open " + path.toString());
        }
        newlineCount -= 1;
        return Integer.toString(newlineCount);
    }

    private String calcChars(Path path){
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
        return Integer.toString(charCount);
    }