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
import uk.ac.ucl.jsh.Jsh;

public class Sed implements Application {

    private boolean g = false;
    private boolean useIS = false;

    @Override
    public void exec(ArrayList<String> args, InputStream input, OutputStream output, ArrayList<Boolean> globbArray) throws IOException {
        String[] s = validateArgs(args, input);

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));
        ArrayList<String> lines = new ArrayList<>();

        if (useIS) {
            lines = performSed(s, input, g);
            writeOutput(writer, lines);
        }
        else {
            String file = args.get(1);
            Path currentDir = Paths.get(Jsh.getCurrentDirectory());
            Path filePath = currentDir.resolve(file);
            if (Files.isDirectory(filePath)) {
                throw new RuntimeException("sed: wrong file argument");
            }

            lines = performSed(s, file, g);
            writeOutput(writer, lines);
        }
    }
    
    /*

        Method uses if statements to select the REPLACEMENT of the sed command.
        REPLACEMENT is split into array of strings separated by delimiter used in command line using split function.
        Array of Strings is returned.
        Method also checks if stdin is to be used by altering useIS, and if 'g' is at end of REPLACEMENT.

    */
    private String[] validateArgs(ArrayList<String> args, InputStream input) {
        String[] s;

        if (args.size() == 1 && input == null || args.size() > 2 || args.size() < 1) {
            throw new RuntimeException("sed: wrong arguments");
        }

        // use InputStream if replacement given with no file
        if (args.size() == 1) {
            useIS = true;
        }

        // splits REPLACEMENT into array by the delimiter used
        final String replacement = args.get(0);
        
        char delimiter = replacement.charAt(1);
        if(args.get(0).chars().filter(num -> num == delimiter).count() != 3){
            throw new RuntimeException("sed: wrong number of delimiters");
        }

        s = args.get(0).split(Pattern.quote(Character.toString(delimiter)));

        if (!"s".equals(s[0])) {
            throw new RuntimeException("sed: regex in incorrect form, first letter must be an 's'");
        }

        if (replacement.charAt(replacement.length() - 1) == 'g') {
            g = true;
        }
        else if (replacement.charAt(replacement.length() - 1) != delimiter) {
            throw new RuntimeException("sed: last char should be delimiter or g");
        }
        return s;
    }


    /*

        Overloaded version of method that takes in file.
        Lines of file are retrieved using getLines and updated depending on command line args using sedUpdateLines.
        Method returns updated lines.

    */
    private ArrayList<String> performSed(String[] s, String file, Boolean g) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        ArrayList<String> fileLines = getLines(file);
        
        if (g) {
            lines = sedUpdateLines(s[1], s[2], fileLines, true);
        }
        else {
            lines = sedUpdateLines(s[1], s[2], fileLines, false);
        }

        return lines;
    }


    /*

        Overloaded version of method that takes in stdin.
        Lines of file are retrieved using readAllBytes + split, and updated depending on command line args using sedUpdateLines.
        Method returns updated lines.

    */
    private ArrayList<String> performSed(String[] s, InputStream input, Boolean g) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        ArrayList<String> argLines = new ArrayList<>();

        String line = new String(input.readAllBytes());
        String[] splitLine = line.split(System.getProperty("line.separator"));
        
        for(String string : splitLine) {
            argLines.add(string);
        }

        if (g) {
            lines = sedUpdateLines(s[1], s[2], argLines, true);
        }
        else {
            lines = sedUpdateLines(s[1], s[2], argLines, false);
        }

        return lines;
    }


    /*

        Method uses BufferedWriter to write all lines in lines array to OutputStream.

    */
    private void writeOutput(BufferedWriter writer, ArrayList<String> lines) throws IOException {
        for(String str : lines) {
            writer.write(str);
            writer.write(System.getProperty("line.separator"));
            writer.flush();
        }
    }
    

    /*

        Method uses BufferedReader to add each line of file to array of lines.
        Lines array is returned.

    */
    private ArrayList<String> getLines(String file) throws IOException {
        ArrayList<String> lines = new ArrayList<>();

        Path currentDir = Paths.get(Jsh.getCurrentDirectory());
        Path filePath = currentDir.resolve(file);
        Charset encoding = StandardCharsets.UTF_8;

        try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException("sed: cannot open "+file);
        }

        return lines;
    }


    /*

        Method uses a for loop to go through each line.
        Each line is checked to see if it matches regex using Matcher.
        The 'all' parameter determines if all or first matches are updated.
        Updated lines are returned.

    */
    private ArrayList<String> sedUpdateLines(String regex, String replacement, ArrayList<String> fileLines, Boolean all) {
        ArrayList<String> changedlines = new ArrayList<>();
        Pattern sedPattern = Pattern.compile(regex);

        for(String line : fileLines) {
            Matcher matcher = sedPattern.matcher(line);
            if (matcher.find()) {
                if(all){
                    line = line.replaceAll(regex, replacement);
                }
                else {
                    line = line.replaceFirst(regex, replacement);
                }
            }
            changedlines.add(line);
        }
        return changedlines;
    }
}