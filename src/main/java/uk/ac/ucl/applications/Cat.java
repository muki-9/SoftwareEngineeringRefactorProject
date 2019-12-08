package uk.ac.ucl.applications;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import uk.ac.ucl.jsh.Application;
import uk.ac.ucl.jsh.Jsh;

public class Cat implements Application {

    @Override
    public void exec(ArrayList<String> args, InputStream input, OutputStream output) throws IOException {
        String currentDirectory = Jsh.getCurrentDirectory();

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));
        if (args.isEmpty()) {
            throw new RuntimeException("cat: missing arguments");
        } else {
            for (String arg : args) {
                Charset encoding = StandardCharsets.UTF_8;
                File currFile = new File(currentDirectory + File.separator + arg);
                if (currFile.exists()) {
                    Path filePath = Paths.get(currentDirectory + File.separator + arg);
                    try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            writer.write(String.valueOf(line));
                            writer.write(System.getProperty("line.separator"));
                            writer.flush();
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("cat: cannot open " + arg);
                    }
                } else {
                    throw new RuntimeException("cat: file does not exist");
                }
            }
        }
    }
}