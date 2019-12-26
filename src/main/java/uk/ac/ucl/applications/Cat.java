package uk.ac.ucl.applications;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
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
import java.util.List;
import java.util.stream.Collectors;

import uk.ac.ucl.jsh.Application;
import uk.ac.ucl.jsh.Globbing;
import uk.ac.ucl.jsh.Jsh;

public class Cat implements Application {
    
    @Override
    public void exec(ArrayList<String> args, InputStream input, OutputStream output) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));
        String currentDirectory = Jsh.getCurrentDirectory();
        
        Globbing g = new Globbing();
        ArrayList<String> updatedArgs = g.globbing(args);

        if (args.isEmpty()) {
            if (input == null) {
                throw new RuntimeException("cat: missing arguments");
            }
            else {
                String line = new String(input.readAllBytes());
                writer.write(line);
                writer.flush();
            }
        }else {
            for (String arg : updatedArgs) {
                Charset encoding = StandardCharsets.UTF_8;
                File currFile = new File(currentDirectory + File.separator + arg);

                // try{
                //     if(currFile.isDirectory()){
                //         throw new RuntimeException("cat:" + currFile + " is a directory");
                //     }
                // }catch(RuntimeException r){

                //     continue;
                // }
                if(currFile.isDirectory()){
                    writer.write("cat: " + currFile.getName() + " is a directory");
                    writer.write(System.getProperty("line.separator"));
                    writer.flush();
                    continue;
                }
        
                System.out.println(currFile.getName());
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
    
}