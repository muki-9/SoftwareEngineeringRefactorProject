package uk.ac.ucl.applications;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.ucl.jsh.Application;
import uk.ac.ucl.jsh.Jsh;

public class Grep implements Application {

    @Override
    public void exec(ArrayList<String> args, InputStream input, OutputStream output) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));
        if (args.size() < 2) {
            if (input == null) {
                throw new RuntimeException("grep: wrong number of arguments");
            }
            else {
                Pattern grepPattern = Pattern.compile(args.get(0));
                String s = buildArg(input);
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
        Pattern grepPattern = Pattern.compile(args.get(0));
        Path[] filePathArray = getPathArray(args);
        writeOutput(writer, grepPattern, filePathArray, args);
    }

    private String buildArg(InputStream input) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(input));
        ArrayList<String> lines = new ArrayList<>();

        int count = input.available()-1;
        StringBuilder textBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, Charset.forName(StandardCharsets.UTF_8.name())))) {
            while (count-- != 0) {
                textBuilder.append((char) reader.read());
            }
        }
        return textBuilder.toString();
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

    public void writeOutput(BufferedWriter writer, Pattern grepPattern, Path[] filePathArray, ArrayList<String> args) {
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
            } catch (IOException e) {
                throw new RuntimeException("grep: cannot open " + args.get(j + 1));
            }
        }
    }
}