package uk.ac.ucl.applications;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import uk.ac.ucl.jsh.Application;
import uk.ac.ucl.jsh.Jsh;

public class Find implements Application{

    private String workingDir = Jsh.getCurrentDirectory();

	@Override
	public void exec(ArrayList<String> args, InputStream input, OutputStream output) throws IOException {

        if(args.size() > 3 || args.size() < 2) {
            throw new RuntimeException("find: wrong number of arguments");
        }
        else {
            final String NAME = args.get(args.size() - 2);
            final String PATTERN = args.get(args.size() - 1);

            if(!NAME.equals("-name")) {
                throw new RuntimeException("find: arg " + NAME + " not correct");
            }

            String pattern = PATTERN; 
            String updatedPatt = pattern.replace("*", "(.*)");
            String currDir = null;
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));

            // sets currDir depending on whether a path is provided
            if(args.size() == 2){
                currDir = workingDir;

            }
            else {
                currDir = args.get(0);
            }

            filterPaths(currDir, updatedPatt, writer);
        }
    }

    /* Method performs the find command given the directory to look in and the pattern to match */
    private void filterPaths(String currDir, String pattern, BufferedWriter writer){
        // adjusts currDir path to allow for relative path to be found later
        String appendedCurrDir = currDir + '/';
        Path currDirPath = Paths.get(appendedCurrDir);

        // scans through directory and builds array of files matching the pattern
        try (Stream<Path> walk = Files.walk(Paths.get(currDir))) {

            List<Path> fileNames  = walk.filter(f -> f.getFileName().toString().matches(pattern))
                                        .collect(Collectors.toList());

            fileNames.forEach((w) -> {
                try{
                    // creates a relative path using currDir and current file
                    Path relativePath = currDirPath.relativize(w);

                    if(currDir.equals(workingDir)) {
                        // prepends "./" to relative path if working directory is being used
                        String tempDir = "./" + relativePath.toString();
                        relativePath = Paths.get(tempDir);
                    }
                    else {
                        // prepends name of specified directory to relative path if there is a directory specified
                        String tempDir = appendedCurrDir + relativePath.toString();
                        relativePath = Paths.get(tempDir);
                    }

                    writer.write(relativePath.toString());
                    writer.write(System.getProperty("line.separator"));
                    writer.flush();
    
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            });
        }
        catch (IOException e) {
            throw new RuntimeException("find: " + currDir + " does not exist");          
        }
    }
}

