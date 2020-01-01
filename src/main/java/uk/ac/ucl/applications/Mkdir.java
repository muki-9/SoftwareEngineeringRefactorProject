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

public class Mkdir implements Application {

    @Override
    public void exec(ArrayList<String> args, InputStream input, OutputStream output) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));
        String currentDirectory = Jsh.getCurrentDirectory();

        int numOfFiles = validateArgs(args);
        for (int i=0; i<numOfFiles; i++) {
            String path = currentDirectory + System.getProperty("file.separator") + args.get(i);
            File file = new File(path);
            if (file.exists()) {
                throw new RuntimeException("mkdir: File already exists, choose different name");
            }
            else {
                if (file.mkdir()){
                    writer.write("Folder created sucessfully");
                    writer.write(System.getProperty("line.separator"));
                    writer.flush();
                } else {
                    writer.write("Folder could not be created, please try again");
                    writer.write(System.getProperty("line.separator"));
                    writer.flush();
                }
            }
        }
    }

    public int validateArgs(ArrayList<String> args) {
        if (args.size()==0) {
            throw new RuntimeException("mkdir: no filename given");
        } else {
            return args.size();
        }
    }
}