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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.ucl.jsh.Application;
import uk.ac.ucl.jsh.Globbing;
import uk.ac.ucl.jsh.Jsh;

public class Grep implements Application {

    @Override
    public void exec(ArrayList<String> args, InputStream input, OutputStream output) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));
        Globbing g = new Globbing();
        ArrayList<String> updatedArgs = g.globbing(args);
        int updatedArgsSize = updatedArgs.size();

        if (updatedArgsSize < 2) {
            if (updatedArgsSize == 1) {
                if (input == null) {
                    throw new RuntimeException("grep: wrong number of arguments");
                }
                else {
                    Pattern grepPattern = Pattern.compile(updatedArgs.get(0));
                    String s = new String(input.readAllBytes());
                    String[] lines = s.split(System.getProperty("line.separator"));
                    
                    for(int i = 0; i < lines.length; i++) {
                        Matcher matcher = grepPattern.matcher(lines[i]);
                        if (matcher.find()) {
                            writer.write(lines[i]);
                            writer.write(System.getProperty("line.separator"));
                            writer.flush();
                        }
                    }
                }
            }
            else {
                throw new RuntimeException("grep: wrong number of arguments");
            }
            
        }
        Pattern grepPattern = Pattern.compile(updatedArgs.get(0));
        Path[] filePathArray = getPathArray(updatedArgs);

        writeOutput(writer, grepPattern, filePathArray);
    }

    /* 
    
        Method uses a for loop to check if each filename provided in the args array exists.
        If the file exists, it is added to a new array, which is returned by the method.
    
    */
    public Path[] getPathArray(ArrayList<String> args) {
        Path currentDir = Paths.get(Jsh.getCurrentDirectory());
        int numOfFiles = args.size() - 1;
        Path[] filePathArray = new Path[numOfFiles];
        Path filePath;

        for (int i = 0; i < numOfFiles; i++) {
            filePath = currentDir.resolve(args.get(i + 1));
            if (Files.notExists(filePath) || Files.isDirectory(filePath) || !Files.exists(filePath) || !Files.isReadable(filePath)) {
                throw new RuntimeException("grep: wrong file argument");
            }
            else {
                filePathArray[i] = filePath;
            }
        }
        return filePathArray;
    }

    /* 
    
        Method takes in the pattern and array of filepaths.
        A for loop goes through each file using the filepath array, and checks to see if the lines within the files match the pattern.
        Any lines which match the pattern are printed onto the OutputStream.
    
    */
    private void writeOutput(BufferedWriter writer, Pattern grepPattern, Path[] filePathArray) throws IOException {
        for (int j = 0; j < (filePathArray.length); j++) {
            Charset encoding = StandardCharsets.UTF_8;
            try (BufferedReader reader = Files.newBufferedReader(filePathArray[j], encoding)) {
                String line = null;
                while ((line = reader.readLine()) != null) {
                    Matcher matcher = grepPattern.matcher(line);
                    if (matcher.find()) {
                        writer.write(line);
                        writer.write(System.getProperty("line.separator"));
                        writer.flush();
                    }
                }
            } 
        }
    }
}