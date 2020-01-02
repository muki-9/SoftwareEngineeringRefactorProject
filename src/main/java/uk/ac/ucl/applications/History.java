package uk.ac.ucl.applications;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import uk.ac.ucl.jsh.Application;
import uk.ac.ucl.jsh.Jsh;

public class History implements Application {

    /*

        Method loops through list of commands retrieved from the Jsh class using a for loop and prints out each command.

    */
    @Override
    public void exec(ArrayList<String> args, InputStream input, OutputStream output) throws IOException {
        validateArgs(args);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));
        ArrayList<String> listOfCommands = Jsh.getCommands();
        if (listOfCommands.size() > 0) {
            for (String command : listOfCommands) {
                writer.write(command);
                writer.write(System.getProperty("line.separator"));
                writer.flush();
            }
        }
        else {
            writer.write("history: there are no commands in history");
            writer.write(System.getProperty("line.separator"));
            writer.flush();
        }
    }

    /*
    
        Method checks that some argument is passed to the application.
    
    */
    private void validateArgs(ArrayList<String> args) {
        if (args.size() != 0) {
            throw new RuntimeException("history: wrong number of arguments");
        }
    }
}