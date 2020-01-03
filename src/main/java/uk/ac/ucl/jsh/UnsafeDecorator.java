package uk.ac.ucl.jsh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class UnsafeDecorator implements Application {

    private Application app;

    public UnsafeDecorator(Application newApplication) {
        app = newApplication;
    }

    /**
    * Unsafe decorator that catched all exceptions here, and prints them out instead of throwing it,
    * allows the program to continue and doesn't force immediate termination.
    */
    @Override
    public void exec(ArrayList<String> args, InputStream input, OutputStream output, ArrayList<Boolean> globbArray) throws IOException {
        try {
            app.exec(args, input, output, globbArray);
        } catch (Exception e) {
            OutputStreamWriter writer = new OutputStreamWriter(output);
            writer.write(e.getMessage());
            writer.write(System.getProperty("line.separator"));
            writer.flush();
        }
    }
}
