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
    private BufferedWriter writer;
    boolean default_constr = true;
    private boolean useIS = false;

    public Head(){

    }

    public Head(BufferedWriter w){
        default_constr = false;
        writer = w;
    }

    @Override
    public void exec(ArrayList<String> args, InputStream input, OutputStream output) throws IOException {
        String currentDirectory = Jsh.getCurrentDirectory();
        if(default_constr){
            writer = new BufferedWriter(new OutputStreamWriter(output));
        }
        validateArguments(args, input);
        String headArg = getHeadArgs(args);
        if (useIS) {
            String line = new String(input.readAllBytes());
            String[] lines = line.split(System.getProperty("line.separator"));
            for(int i=0;i<headLines;i++) {
                if (i>=lines.length) {
                    break;
                }
                writer.write(lines[i]);
                writer.write(System.getProperty("line.separator"));
                writer.flush();
            }
        }
        else {
            File headFile = new File(currentDirectory + File.separator + headArg);
            writeOutput(headArg, headFile, writer, currentDirectory);
        }
    }

    private void writeOutput(String headArg, File headFile, BufferedWriter writer, String currentDirectory)
            throws IOException {
        if (headFile.exists()) {
            Charset encoding = StandardCharsets.UTF_8;
            Path filePath = Paths.get((String) currentDirectory + File.separator + headArg);
            try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {
                for (int i = 0; i < headLines; i++) {
                    String line = null;
                    if ((line = reader.readLine()) != null) {
                        writer.write(line);
                        writer.write(System.getProperty("line.separator"));
                        writer.flush();
                    }
                }
            }
        } else {
            throw new RuntimeException("head: " + headArg + " does not exist");
        }
    }

    private String getHeadArgs(ArrayList<String> args) {
        String headArg;
        if (useIS) {
            if (args.size()==0) {
                return null;
            }
            else if (args.size()==2) {
                headLines = Integer.parseInt(args.get(1));
                return null;
            }
            else {
                throw new RuntimeException("head: wrong argument " + args.get(0));
            }
        }
        if (args.size() == 3) {
            try {
                headLines = Integer.parseInt(args.get(1));
            } catch (Exception e) {
                throw new RuntimeException("head: wrong argument " + args.get(1));
            }
            headArg = args.get(2);
        } else {
            headArg = args.get(0);
        }
        return headArg;
    }

    private void validateArguments(ArrayList<String> args, InputStream input) {
        if (args.isEmpty()) {
            if (input == null) {
                throw new RuntimeException("head: missing arguments");
            }
            else {
                useIS = true;
                return;
            }
        }
        if (args.size()==2 && input != null) {
            useIS = true;
            return;
        }
        if (args.size() != 1 && args.size() != 3) {
            throw new RuntimeException("head: wrong arguments");
        }
        if (args.size() == 3 && !args.get(0).equals("-n")) {
            throw new RuntimeException("head: wrong argument " + args.get(0));
        }
    }
}