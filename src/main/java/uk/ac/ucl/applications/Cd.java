package uk.ac.ucl.applications;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import uk.ac.ucl.jsh.Application;
import uk.ac.ucl.jsh.Jsh;

public class Cd implements Application {
    @Override
    public void exec(ArrayList<String> args, InputStream input, OutputStream output) throws IOException {
        String currentDirectory = Jsh.getCurrentDirectory();

        if (args.isEmpty()) {
            //implement functionality to take you to the home directory if cd is inputted alone
            throw new RuntimeException("cd: missing argument");
        } else if (args.size() > 1) {
            throw new RuntimeException("cd: too many arguments");
        }
        String dirString = args.get(0);
        File dir = new File(currentDirectory, dirString);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new RuntimeException("cd: " + dirString + " is not an existing directory");
        }
        currentDirectory = dir.getCanonicalPath();
        Jsh.setCurrentDirectory(currentDirectory);
    }
}