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
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));
        String currentDirectory = Jsh.getCurrentDirectory();

        if (args.isEmpty()) {
            System.out.println("args empty");
            if (input == null) {
                System.out.println("null!!!!");
                throw new RuntimeException("cat: missing arguments");
            }
            else {
                System.out.println("elsed");
                // BufferedReader br = new BufferedReader(new InputStreamReader(input));
                // System.out.println("before we read");
                // int data = br.read();
                // System.out.println("after we read");
                // while(data != -1){
                //     char theChar = (char) data;
                //     System.out.println(theChar);
                //     data = br.read();
                // }
                // br.close();
                // System.out.println("left the loop");
            }
        } else {
            
            for (String arg : args) {
                Charset encoding = StandardCharsets.UTF_8;
                File currFile = new File(currentDirectory + File.separator + arg);
                System.out.println(currFile.getName());
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
                    }finally{

                  

                    }
                } else {
       
                    throw new RuntimeException("cat: file does not exist");
                }
            }
        }
        writer.close();
    }
}