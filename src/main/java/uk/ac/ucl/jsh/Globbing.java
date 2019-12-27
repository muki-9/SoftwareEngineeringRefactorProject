package uk.ac.ucl.jsh;

import java.io.File;
import java.util.ArrayList;

public class Globbing {

    private String currentDirectory = Jsh.getCurrentDirectory();

    public ArrayList<String> globbing(ArrayList<String> args){

        ArrayList<String> updatedArgs = new ArrayList<>();
        String substring = null;
        for(String arg : args){
   
            if(arg.endsWith("/*")){
                
                substring = arg.substring(0, arg.length()-2);
                File test= new File(currentDirectory + "/" + substring);
                if(test.isDirectory()){

                    File[] listOfFiles = test.listFiles();
                    for(File file: listOfFiles){
                        if (!file.getName().startsWith(".")) {
                            updatedArgs.add(substring + "/" + file.getName());
                        }
                        
                    }
                }
            }else{
                updatedArgs.add(arg);
            }
            
        }

        return updatedArgs;

    }
}


