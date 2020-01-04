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

public class Tail implements Application {

    private int tailLines = 10;
    private boolean useIS = false;
    private int index =0;
    BufferedWriter writer;
    private String currentDirectory = Jsh.getCurrentDirectory();

    @Override
    public void exec(ArrayList<String> args, InputStream input, OutputStream output, ArrayList<Boolean> globbArray)
            throws IOException {
        writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));
        if(input != null){
            useIS = true;
        }
        checkArgs(args);
        String file  = getFile(args);

        if (useIS) {
            String line = new String(input.readAllBytes());
            String[] lines = line.split(System.getProperty("line.separator"));
    
            if (tailLines <= lines.length) {
                index = lines.length - tailLines;
            }
            for (int i = index; i < lines.length; i++) {
                writer.write(lines[i] + System.getProperty("line.separator"));
                writer.flush();
            }

        }else{
            File tailFile = new File(currentDirectory + File.separator + file);
            writeOutput(file, tailFile, writer, currentDirectory);
            }
    }

    public String getFile(ArrayList<String> args){

        if(args.size()> 1){
            if(!args.get(0).equals("-n")){throw new RuntimeException("tail: wrong argument: " + args.get(0) );}
            try{
                tailLines = Integer.parseInt(args.get(1));  
            }catch(RuntimeException e){
                throw new RuntimeException("tail: wrong argument: " + args.get(1));
            }
            if(args.size() == 3){
                return args.get(2);
            }
           
        }else if(args.size()==1){
            return args.get(0);
        }
        return null;
    }

    public void checkArgs(ArrayList<String> args){

        if(((args.isEmpty() || args.size() ==2)&& !useIS)|| args.size() > 3 || ((args.size() ==3 || args.size()==1))&& useIS){
            throw new RuntimeException("tail: wrong arguments");   
        }
    }

    private void writeOutput(String tailArg, File tailFile, BufferedWriter writer, String currentDirectory)
            throws IOException {
        if (!tailFile.exists()) {
            throw new RuntimeException("tail: " + tailArg + " does not exist");
        }
        Charset encoding = StandardCharsets.UTF_8;
        Path filePath = Paths.get((String) currentDirectory + File.separator + tailArg);
        ArrayList<String> storage = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                storage.add(line);
            }
            if (tailLines <= storage.size()) {
                index = storage.size() - tailLines;
            }
            for (int i = index; i < storage.size(); i++) {
                writer.write(storage.get(i) + System.getProperty("line.separator"));
                writer.flush();
            }            
        }
    }
}