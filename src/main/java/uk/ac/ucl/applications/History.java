package uk.ac.ucl.applications;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import uk.ac.ucl.jsh.Application;
import uk.ac.ucl.jsh.Jsh;

public class History implements Application {

    @Override
    public void exec(ArrayList<String> args, InputStream input, OutputStream output) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));
        validateArgs(args);
        ArrayList<String> listOfCommands = Jsh.getCommands();
        if (listOfCommands.size()>0) {
            for (String command: listOfCommands) {
                writer.write(command);
                writer.write(System.getProperty("line.separator"));
                writer.flush();
            }
        } else {
            writer.write("There are no commands in history");
            writer.write(System.getProperty("line.separator"));
            writer.flush();
        }
        
    }

    public void validateArgs(ArrayList<String> args) {
        if (args.size()!=0) {
            throw new RuntimeException("history: wrong number of arguments");
        }
    }
}