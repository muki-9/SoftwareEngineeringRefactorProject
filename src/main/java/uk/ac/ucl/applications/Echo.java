package uk.ac.ucl.applications;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import uk.ac.ucl.jsh.Application;

public class Echo implements Application {

    @Override
    public void exec(ArrayList<String> args, InputStream input, OutputStream output) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));
        boolean atLeastOnePrinted = false;
        for (String arg : args) {
            writer.write(arg);
            writer.write(" ");
            writer.flush();
            atLeastOnePrinted = true;
        }
        if (atLeastOnePrinted) {
            writer.write(System.getProperty("line.separator"));
            writer.flush();
        }
    }
}