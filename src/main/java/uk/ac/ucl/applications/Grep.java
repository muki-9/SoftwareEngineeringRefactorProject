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
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));
        Globbing g = new Globbing();
        ArrayList<String> updatedArgs = g.globbing(args);

        if (updatedArgs.size() < 2) {
            if (updatedArgs.size() == 1) {
                if (input == null) {
                    throw new RuntimeException("grep: wrong number of arguments");
                }
                else {
                    Pattern grepPattern = Pattern.compile(updatedArgs.get(0));
                    String s = new String(input.readAllBytes());
                    String[] lines = s.split(System.getProperty("line.separator"));
                    
                    for(int i=0; i<lines.length; i++) {
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
        writeOutput(writer, grepPattern, filePathArray, updatedArgs);
    }

    public Path[] getPathArray(ArrayList<String> args) {
        String currentDirectory = Jsh.getCurrentDirectory();
        Path currentDir = Paths.get(currentDirectory);
        int numOfFiles = args.size() - 1;
        Path[] filePathArray = new Path[numOfFiles];
        Path filePath;

        for (int i = 0; i < numOfFiles; i++) {
            filePath = currentDir.resolve(args.get(i + 1));
            if (Files.notExists(filePath) || Files.isDirectory(filePath) || !Files.exists(filePath)
                    || !Files.isReadable(filePath)) {
                throw new RuntimeException("grep: wrong file argument");
            } else {
                filePathArray[i] = filePath;
            }
        }
        return filePathArray;
    }

    public void writeOutput(BufferedWriter writer, Pattern grepPattern, Path[] filePathArray, ArrayList<String> args)
            throws IOException {
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