package uk.ac.ucl.applications;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import uk.ac.ucl.jsh.Application;

public class Exit implements Application {

    @Override
    public void exec(ArrayList<String> args, InputStream input, OutputStream output, ArrayList<Boolean> globbArray) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));
        
        if (args.size() != 0) {
            throw new RuntimeException("exit: wrong number of arguments");
        }
        else {
            writer.write("logging out");
            writer.write(System.getProperty("line.separator"));
            writer.flush();
            System.exit(0);
        }
    }
}