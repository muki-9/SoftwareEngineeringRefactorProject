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

    /*

        Method uses a for loop to go through each file specified.
        If file exists and is empty, file is deleted using file.delete().

    */
    @Override
    public void exec(ArrayList<String> args, InputStream input, OutputStream output, ArrayList<Boolean> globbArray) throws IOException {
        int numOfFiles = validateArgs(args);
        for (int i = 0; i < numOfFiles; i++) {
            String path = buildString(Jsh.getCurrentDirectory(), args.get(i));
            File file = new File(path);

            if (!file.exists()) {
                throw new RuntimeException("rmdir: File does not exist");
            }
            else if (!isEmpty(file.toPath())) {
                throw new RuntimeException("rmdir: Cannot remove non-empty file");
            }
            else {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));
                file.delete();
                writer.write("Folder removed sucessfully");
                writer.write(System.getProperty("line.separator"));
                writer.flush();
              
            }
        }
    }

    /*

        Method uses StringBuilder to avoid using string concatenation in loop, to prevent excess garbage and array copying.
        Builds string path using currentdirectory and name of specified directory.

    */
    private String buildString(String currentDirectory, String arg){
        StringBuilder sb = new StringBuilder();
        sb.append(currentDirectory);
        sb.append(System.getProperty("file.separator"));
        sb.append(arg);
        return sb.toString();
    }

    /*

        Method returns the number of arguments unless none are provided, using an if statement.

    */
    private int validateArgs(ArrayList<String> args) {
        if (args.size() == 0) {
            throw new RuntimeException("rmdir: no filename given");
        }
        else {
            return args.size();
        }
    }

    /*

        Method uses a DirectoryStream to see if specified directory contains files.
        Returns true if directory is empty.

    */
    private boolean isEmpty(Path path) throws IOException {
        try(DirectoryStream<Path> dirStream = Files.newDirectoryStream(path)) {
            return !dirStream.iterator().hasNext();
        }
    }
}