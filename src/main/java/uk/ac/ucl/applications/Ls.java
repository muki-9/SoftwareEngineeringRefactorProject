package uk.ac.ucl.applications;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import uk.ac.ucl.jsh.Application;
import uk.ac.ucl.jsh.Jsh;

public class Ls implements Application {

    @Override
    public void exec(ArrayList<String> args, InputStream input, OutputStream output, ArrayList<Boolean> globbArray) throws IOException {
        String currentDirectory = Jsh.getCurrentDirectory();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));
        File currDir = validateArgs(args, currentDirectory);
        writeOutput(currDir, writer);
    }


    /*

        Method gets array of files in specified directory and writes names of files not beginning with '.' to OutputStream.

    */
    private void writeOutput(File currDir, BufferedWriter writer) throws IOException {
        try {
            File[] listOfFiles = currDir.listFiles();
            boolean atLeastOnePrinted = false;
            for (File file : listOfFiles) {
                if (!file.getName().startsWith(".")) {
                    writer.write(file.getName());
                    writer.write("\t");
                    writer.flush();
                    atLeastOnePrinted = true;
                }
            }
            if (atLeastOnePrinted) {
                writer.write(System.getProperty("line.separator"));
                writer.flush();
            }
        } catch (NullPointerException e) {
            throw new RuntimeException("ls: no such directory");
        }
    }

    /*

        Method returns current directory path if no argument specified or returns specified path using if statements.

    */
    private File validateArgs(ArrayList<String> args, String currentDirectory) {
        File currDir;
        if (args.isEmpty()) {
            currDir = new File(currentDirectory);
        }
        else if (args.size() == 1) {
            currDir = new File(args.get(0));
        }
        else {
            throw new RuntimeException("ls: too many arguments");
        }
        return currDir;
    }
}