package uk.ac.ucl.applications;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import uk.ac.ucl.jsh.Application;
import uk.ac.ucl.jsh.Jsh;

public class Rmdir implements Application {

    @Override
    public void exec(ArrayList<String> args, InputStream input, OutputStream output) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));
        String currentDirectory = Jsh.getCurrentDirectory();

        int numOfFiles = validateArgs(args);
        for (int i=0; i<numOfFiles; i++) {
            String path = currentDirectory + System.getProperty("file.separator") + args.get(i);
            File file = new File(path);
            if (!file.exists()) {
                throw new RuntimeException("mkdir: File does not exist");
            }
            else {
                if (file.delete()) {
                    writer.write("Folder removed sucessfully");
                    writer.write(System.getProperty("line.separator"));
                    writer.flush();
                } else {
                    writer.write("Folder could not be removed, please try again");
                    writer.write(System.getProperty("line.separator"));
                    writer.flush();
                }
            }
        }
    }

    public int validateArgs(ArrayList<String> args) {
        if (args.size()==0) {
            throw new RuntimeException("rmdir: no filename given");
        } else {
            return args.size();
        }
    }
}