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
import uk.ac.ucl.jsh.Jsh;

public class Head implements Application {

    private int headLines = 10;
    private boolean useIS = false;

    private BufferedWriter writer;

    @Override
    public void exec(ArrayList<String> args, InputStream input, OutputStream output, ArrayList<Boolean> globbArray) throws IOException {
        String currentDirectory = Jsh.getCurrentDirectory();
        writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));

        checkArgs(args, input);
        String file = getFile(args);

        if (!useIS) {
            File headFile = new File(currentDirectory + File.separator + file);
            writeOutput(file, headFile, writer, currentDirectory);
        }
        else {
            String line = new String(input.readAllBytes());
            String[] lines = line.split(System.getProperty("line.separator"));

            if (lines.length <= headLines) {
                headLines = lines.length;
            }

            for (int i = 0; i < headLines; i++) {
                writer.write(lines[i] + System.getProperty("line.separator"));
                writer.flush();
            }
        }
    }

    public String getFile(ArrayList<String> args){
        if (args.size() == 1) {
            return args.get(0);
        }
        else if (args.size() > 1) {
            if (!args.get(0).equals("-n")) {
                throw new RuntimeException("head: wrong argument: " + args.get(0));
            }
            try {
                headLines = Integer.parseInt(args.get(1));  
            }
            catch (RuntimeException e) {
                throw new RuntimeException("head: wrong argument: " + args.get(1));
            }

            if (args.size() == 3) {
                return args.get(2);
            }
        }
        return null;
    }

    /*
        
        Method takes in command line arguments and adjusts class state or throws exception depending on arguments using if statements.

    */
    public void checkArgs(ArrayList<String> args, InputStream input){
        if ((args.isEmpty() || args.size() == 2) && input == null || args.size() > 3) {
            throw new RuntimeException("head: wrong arguments");   
        }

        if(args.size() == 2 || args.isEmpty()){
            useIS = true;
        }
    }

    /*
    
        Method checks if file exists - if it does, for loop prints out specified number of lines from top of file.
     
    */
    private void writeOutput(String headArg, File headFile, BufferedWriter writer, String currentDirectory) throws IOException {
        if (!headFile.exists()) {
            throw new RuntimeException("head: " + headArg + " does not exist");
        }
      
        Charset encoding = StandardCharsets.UTF_8;
        Path filePath = Paths.get((String) currentDirectory + File.separator + headArg);
        try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {
            for (int i = 0; i < headLines; i++) {
                String line = null;
                if ((line = reader.readLine()) != null) {
                    writer.write(line + System.getProperty("line.separator"));
                    writer.flush();
                }
            }
        }
    }
  
}