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

    @Override
    public void exec(ArrayList<String> args, InputStream input, OutputStream output) throws IOException {
        try {
            app.exec(args, input, output);
        } catch (Exception e) {
            OutputStreamWriter writer = new OutputStreamWriter(output);
            writer.write(e.getMessage());
            writer.write(System.getProperty("line.separator"));
            writer.flush();
        }
    }
}
