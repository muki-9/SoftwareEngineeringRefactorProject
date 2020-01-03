package uk.ac.ucl.jsh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public interface Application {
    void exec(ArrayList<String> args, InputStream input, OutputStream output, ArrayList<Boolean> globbArray) throws IOException;
}
