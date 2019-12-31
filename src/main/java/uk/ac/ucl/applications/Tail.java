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

public class Tail implements Application {

    private int tailLines = 10;
    private boolean useIS = false;
    BufferedWriter writer;
    boolean default_constr = true;

    public Tail(){
    }

    public Tail(BufferedWriter w){
        default_constr =false;
        writer = w;
    }

    @Override
    public void exec(ArrayList<String> args, InputStream input, OutputStream output) throws IOException {
        String currentDirectory = Jsh.getCurrentDirectory();
        if(default_constr){
             writer = new BufferedWriter(new OutputStreamWriter(output));
        }
        validateArguments(args, input);
        String tailArg = getTailArgs(args);
        if (useIS) {
            String line = new String(input.readAllBytes());
            String[] lines = line.split(System.getProperty("line.separator"));
            int index = 0;
            if (tailLines > lines.length) {
                index = 0;
            } else {
                index = lines.length - tailLines;
            }
            for (int i = index; i < lines.length; i++) {
                writer.write(lines[i] + System.getProperty("line.separator"));
                writer.flush();
            }
        }
        else {
            File tailFile = new File(currentDirectory + File.separator + tailArg);
            writeOutput(tailArg, tailFile, writer, currentDirectory);
        }
    }

    private void writeOutput(String tailArg, File tailFile, BufferedWriter writer, String currentDirectory)
            throws IOException {
        if (tailFile.exists()) {
            Charset encoding = StandardCharsets.UTF_8;
            Path filePath = Paths.get((String) currentDirectory + File.separator + tailArg);
            ArrayList<String> storage = new ArrayList<>();
            try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {
                String line = null;
                while ((line = reader.readLine()) != null) {
                    storage.add(line);
                }
                int index = 0;
                if (tailLines > storage.size()) {
                    index = 0;
                } else {
                    index = storage.size() - tailLines;
                }
                for (int i = index; i < storage.size(); i++) {
                    writer.write(storage.get(i) + System.getProperty("line.separator"));
                    writer.flush();
                }            
            }
        } else {
            throw new RuntimeException("tail: " + tailArg + " does not exist");
        }
    }

    private String getTailArgs(ArrayList<String> args) {
        String tailArg;
        if (useIS) {
            if (args.size()==0) {
                return null;
            }
            else if (args.size()==2) {
                tailLines = Integer.parseInt(args.get(1));
                return null;
            }
            else {
                throw new RuntimeException("tail: wrong argument " + args.get(0));
            }
        }
        if (args.size() == 3) {
            try {
                tailLines = Integer.parseInt(args.get(1));
            } catch (Exception e) {
                throw new RuntimeException("tail: wrong argument " + args.get(1));
            }
            tailArg = args.get(2);
        } else {
            tailArg = args.get(0);
        }
        return tailArg;
    }

    private void validateArguments(ArrayList<String> args, InputStream input) {
        if (args.isEmpty()) {
            if (input != null) {
                useIS = true;
                return;
            }
            else {
                throw new RuntimeException("tail: missing arguments");
            }
        }
        if (args.size()==2 && input != null) {
            useIS = true;
            return;
        }
        if (args.size() != 1 && args.size() != 3) {
            throw new RuntimeException("tail: wrong arguments");
        }
        if (args.size() == 3 && !args.get(0).equals("-n")) {
            throw new RuntimeException("tail: wrong argument " + args.get(0));
        }
    }
}