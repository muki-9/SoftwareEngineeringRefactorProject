package uk.ac.ucl.applications;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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

public class Cat implements Application {
    
    @Override
    public void exec(ArrayList<String> args, InputStream input, OutputStream output, ArrayList<Boolean> globbArray) throws IOException {
        Globbing g = new Globbing(globbArray);
        ArrayList<String> updatedArgs = g.globbing(args);

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));

        // decides whether stdin must be used when no args provided
        if (args.isEmpty()) {
            if (input == null) {
                throw new RuntimeException("cat: missing arguments");
            }
            else {
                String line = new String(input.readAllBytes());
                writer.write(line);
                writer.flush();
            }
        }
        else {
            performCat(updatedArgs, writer);
        }
    }

    /*

        Command line arguments passed into method and a for-each loop loops through contents of the array.
        File is created using the filename passed through command line - if file exists, contents written to the OutputStream.
        It does this for each file, resulting in a concatenation of each file.
       
    */
    private void performCat(ArrayList<String> args, BufferedWriter writer) throws IOException {
        String currentDirectory = Jsh.getCurrentDirectory();
        Charset encoding = StandardCharsets.UTF_8;

        for (String arg : args) {
            File currFile = new File(currentDirectory + File.separator + arg);

            if(currFile.isDirectory()){
                writer.write("cat: " + currFile.getName() + " is a directory");
                writer.write(System.getProperty("line.separator"));
                writer.flush();
                continue;
            }
    
            if (currFile.exists()) {
                Path filePath = Paths.get(currentDirectory + File.separator + arg);
                try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        writer.write(String.valueOf(line));
                        writer.write(System.getProperty("line.separator"));
                        writer.flush();
                    }
                }
            } else {
                throw new RuntimeException("cat: file does not exist");
            }
        }
    }
    
}