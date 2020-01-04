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
    public void exec(ArrayList<String> args, InputStream input, OutputStream output, ArrayList<Boolean> globbArray) throws IOException { 
        if (args.isEmpty()) {
            // takes user to home directory when 'cd' is called alone
            Jsh.setCurrentDirectory(Jsh.getHomeDirectory());
        }
        else if (args.size() > 1) {
            throw new RuntimeException("cd: too many arguments");
        }
        else {
            // takes user to directory specified in arg
            String currentDirectory = Jsh.getCurrentDirectory();
            String dirString = args.get(0);
            File dir = new File(currentDirectory, dirString);
            
            if (!dir.exists() || !dir.isDirectory()) {
                throw new RuntimeException("cd: " + dirString + " is not an existing directory");
            }
    
            currentDirectory = dir.getCanonicalPath();
            Jsh.setCurrentDirectory(currentDirectory);
        }
    }
}