package uk.ac.ucl.applications;

import java.io.BufferedWriter;
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
    private boolean workDir = true;    

	@Override
	public void exec(ArrayList<String> args, InputStream input, OutputStream output, ArrayList<Boolean> globbArray) throws IOException {

        if(args.size() > 3 || args.size() <2){
            throw new RuntimeException("find: wrong number of arguments");

        }else{

            final String NAME = args.get(args.size()-2);
            final String PATTERN = args.get(args.size()-1);

            if(!NAME.equals("-name")){
                throw new RuntimeException("find: args not correct");
            }
            String pattern = PATTERN; 
            String updatedPatt = pattern.replace("*", "(.*)");
            String currDir = null;
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));

            if(args.size() == 2 ){
                 currDir = Jsh.getCurrentDirectory();

            }else{
                 currDir= args.get(0);
                 workDir = false;
            }
            filterPaths(currDir, updatedPatt, writer);
        }
    
    }
        public void filterPaths(String currDir, String pattern, BufferedWriter writer){
            
            try (Stream<Path> walk = Files.walk(Paths.get(currDir))){

                List<Path> fileNames  = walk.filter(f -> f.getFileName().toString().matches(pattern))
                                            .collect(Collectors.toList());

                fileNames.forEach((w) -> {
                    try{
                        /* If current dir then uses . 
                        Otherwise uses name of path */
                        String relPath = null;
                        if(workDir){
                             relPath  = w.toString().replaceFirst(currDir, ".");
                        }else{
                            relPath = w.toString();
                        }
                        writer.write(relPath);
                        writer.write(System.getProperty("line.separator"));
                        writer.flush();
        
                }catch(IOException e){
                    throw new RuntimeException("cannot write to output");
                }
                });

            } catch (IOException e) {
                throw new RuntimeException("find: " + currDir + " does not exist");          
            }
        }
}
