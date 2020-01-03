package uk.ac.ucl.applications;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import uk.ac.ucl.jsh.Application;

public class Echo implements Application {

    @Override
    public void exec(ArrayList<String> args, InputStream input, OutputStream output, ArrayList<Boolean> globbArray) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));
        boolean atLeastOnePrinted = !args.isEmpty();

        // arguments printed with space between them, ensuring no space printed after last element
        int count = 0;
        for (String arg : args) {
            writer.write(arg);
            if (count < args.size() - 1) {
                writer.write(" ");
            }
            writer.flush();
            count++;
        }

        // newline only printed if there are arguments called on echo
        if (atLeastOnePrinted) {
            writer.write(System.getProperty("line.separator"));
            writer.flush();
        }
    }
}