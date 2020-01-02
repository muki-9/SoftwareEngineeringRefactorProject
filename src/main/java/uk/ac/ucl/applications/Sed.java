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

import javax.management.RuntimeErrorException;

import uk.ac.ucl.jsh.Application;
import uk.ac.ucl.jsh.Jsh;

public class Sed implements Application {

    private boolean g = false;
    private boolean useIS = false;

    @Override
    public void exec(ArrayList<String> args, InputStream input, OutputStream output) throws IOException {
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));
        String currentDirectory = Jsh.getCurrentDirectory();
        String file;
        String[] s = validateArgs(args, input);
        if (useIS) {
            ArrayList<String> lines = new ArrayList<>();
            lines = performSed(s, input, g);
            writeOutput(out, lines);
        }
        else {
            file = args.get(1);
            Path currentDir = Paths.get(currentDirectory);
            Path filePath = currentDir.resolve(file);
            if (Files.notExists(filePath) || Files.isDirectory(filePath) || !Files.exists(filePath) || !Files.isReadable(filePath)) {
                throw new RuntimeException("sed: wrong file argument");
            }
            ArrayList<String> lines = new ArrayList<>();
            lines = performSed(s, file, g);
            writeOutput(out, lines);
        }
    }

    private String[] validateArgs(ArrayList<String> args, InputStream input) {
        String[] s;
        if (args.size() != 2) {
            if (args.size() == 1 && input != null) {
                useIS = true;
            }
            else {
                throw new RuntimeException("sed: wrong number of arguments");
            }
        }
        if (args.get(0) != null && useIS || args.get(1) != null) {
            String delimiter = Character.toString(args.get(0).charAt(1));
            if (delimiter.matches("\\|")) {
                delimiter = "\\|";
            }
            s = args.get(0).split(delimiter);
            if (!s[0].matches("s")) {
                throw new RuntimeException("sed: regex in incorrect form");
            }
            if (args.get(0).lastIndexOf(delimiter) < args.get(0).length()-1) {
                if (args.get(0).charAt(args.get(0).lastIndexOf(delimiter)+1) == 'g') {
                    g = true;
                }
                else if (args.get(0).lastIndexOf(delimiter) == args.get(0).length()-1) {
                    g = false;
                }
                else {
                    throw new RuntimeException("sed: regex must end in either a delimiter or 'g'");
                }
            }
        }
        else {
            throw new RuntimeException("sed: not enough arguments");
        }
        return s;
    }

    private ArrayList<String> performSed(String[] s, String file, Boolean g) throws IOException {
        ArrayList<String> fileLines = getLines(file);
        ArrayList<String> lines = new ArrayList<>();
        if (g) {
            lines = sedForAll(s[1], s[2], fileLines);
        }
        else {
            lines = sedFirstInstance(s[1], s[2], fileLines);
        }
        return lines;
    }

    private ArrayList<String> performSed(String[] s, InputStream input, Boolean g) throws IOException {
        String line = new String(input.readAllBytes());
        String[] splitLine = line.split(System.getProperty("line.separator"));
        ArrayList<String> argLines = new ArrayList<>();
        ArrayList<String> lines = new ArrayList<>();
        for(String string:splitLine) {
            argLines.add(string);
        }

        if (g) {
            lines = sedForAll(s[1], s[2], argLines);
        }
        else {
            lines = sedFirstInstance(s[1], s[2], argLines);
        }
        return lines;
    }

    private void writeOutput(BufferedWriter out, ArrayList<String> lines) throws IOException {
        for(String str : lines) {
            out.write(str);
            out.write(System.getProperty("line.separator"));
            out.flush();
        }
    }
    
    public ArrayList<String> getLines(String file) throws IOException {
        Path currentDir = Paths.get(Jsh.getCurrentDirectory());
        Path filePath = currentDir.resolve(file);
        Charset encoding = StandardCharsets.UTF_8;
        String line;
        ArrayList<String> lines = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException("sed: cannot open "+file);
        }
        return lines;
    }

    private ArrayList<String> sedForAll(String regex, String replacement, ArrayList<String> fileLines) {
        ArrayList<String> changedlines = new ArrayList<>();
        Pattern sedPattern = Pattern.compile(regex);

        for(String line: fileLines) {
            Matcher matcher = sedPattern.matcher(line);
            if (matcher.find()) {
                line = line.replaceAll(regex, replacement);
            }
            changedlines.add(line);
        }
        return changedlines;
    }

    private ArrayList<String> sedFirstInstance(String regex, String replacement, ArrayList<String> fileLines) {
        ArrayList<String> changedlines = new ArrayList<>();
        Pattern sedPattern = Pattern.compile(regex);
        for(String line: fileLines) {
            Matcher matcher = sedPattern.matcher(line);
            if (matcher.find()) {
                line = line.replaceFirst(regex, replacement);
            }
            changedlines.add(line);
        }
        return changedlines;
    }
}