package uk.ac.ucl.applications;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
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

import javax.print.DocFlavor.STRING;

import uk.ac.ucl.jsh.Application;
import uk.ac.ucl.jsh.Jsh;

public class Sed implements Application {

    @Override
    public void exec(ArrayList<String> args, InputStream input, OutputStream output) throws IOException {
        String currentDirectory = Jsh.getCurrentDirectory();
        String file;
        String[] s;
        Boolean g = false;

        if (args.size() != 2) {
            throw new RuntimeException("sed: wrong number of arguments");
        }
        if ((args.get(0) != null) && (args.get(1) != null)) {
            s = args.get(0).split("/");
            if (s.length == 4) {
                g = true;
            }
            else if (s.length == 3) {
                g = false;
            }
            else {
                throw new RuntimeException("sed: regex enterred in incorrect format\nyour first argument should look like this s/regex/replacement/ or s/regex/replacement/g");
            }
            file = args.get(1);
        }
        else {
            throw new RuntimeException("sed: not enough arguments");
        }

        Path currentDir = Paths.get(currentDirectory);
        Path filePath = currentDir.resolve(file);
        if (Files.notExists(filePath) || Files.isDirectory(filePath) || !Files.exists(filePath) || !Files.isReadable(filePath)) {
            throw new RuntimeException("sed: wrong file argument");
        }
        ArrayList<String> fileLines = getLines(file);
        ArrayList<String> lines = new ArrayList<>();
        if (g) {
            lines = sedForAll(s[1], s[2], fileLines);
        }
        else {
            lines = sedFirstInstance(s[1], s[2], fileLines);
        }

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(output));
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

    public ArrayList<String> sedForAll(String regex, String replacement, ArrayList<String> fileLines) {
        ArrayList<String> changedlines = new ArrayList<>();
        Pattern sedPattern = Pattern.compile(regex);

        for(String line: fileLines) {
            Matcher matcher = sedPattern.matcher(line);
            if (matcher.find()) {
                line = line.replace(regex, replacement);
            }
            changedlines.add(line);
        }
        return changedlines;
    }

    public ArrayList<String> sedFirstInstance(String regex, String replacement, ArrayList<String> fileLines) {
        ArrayList<String> changedlines = new ArrayList<>();
        Pattern sedPattern = Pattern.compile(regex);
        Boolean first = true;
        for(String line: fileLines) {
            Matcher matcher = sedPattern.matcher(line);
            if (matcher.find() && first==true) {
                line = line.replaceFirst(regex, replacement);
                first = false;
            }
            changedlines.add(line);
        }
        return changedlines;
    }
}