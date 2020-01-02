package uk.ac.ucl.applications;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import uk.ac.ucl.jsh.Application;
import uk.ac.ucl.jsh.Jsh;

public class Rmdir implements Application {

    @Override
    public void exec(ArrayList<String> args, InputStream input, OutputStream output) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));
        String currentDirectory = Jsh.getCurrentDirectory();

        int numOfFiles = validateArgs(args);
        for (int i=0; i<numOfFiles; i++) {
            String path = currentDirectory + System.getProperty("file.separator") + args.get(i);

            File file = new File(path);
            if (!file.exists()) {
                throw new RuntimeException("rmdir: File does not exist");
            }
            else if (!isEmpty(file.toPath())) {
                throw new RuntimeException("rmdir: Cannot remove non-empty file");
            }
            else {
                if (file.delete()) {
                    writer.write("Folder removed sucessfully");
                    writer.write(System.getProperty("line.separator"));
                    writer.flush();
                } else {
                    writer.write("Deletion failed due to unknown error");
                    writer.write(System.getProperty("line.separator"));
                    writer.flush();
                }
            }
        }
    }

    private int validateArgs(ArrayList<String> args) {
        if (args.size()==0) {
            throw new RuntimeException("rmdir: no filename given");
        } else {
            return args.size();
        }
    }

    private boolean isEmpty(Path path) throws IOException {
        try(DirectoryStream<Path> dirStream = Files.newDirectoryStream(path)) {
            return !dirStream.iterator().hasNext();
        }
    }
}