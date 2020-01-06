package uk.ac.ucl.jsh;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Globbing {
    private ArrayList<Boolean> globbArray;

    public Globbing(ArrayList<Boolean> globbArray){
        this.globbArray = globbArray;
    }

    private String currentDirectory = Jsh.getCurrentDirectory();
    Path currDir = Paths.get(currentDirectory);

    public ArrayList<String> globbing(ArrayList<String> args) throws IOException {

        ArrayList<String> updatedArgsList = new ArrayList<>();
        for (int i = 0 ; i < args.size(); i++) {
            String arg = args.get(i);
            if(arg.contains("*") && globbArray.get(i)){
                int count = 1;
                count += arg.length() - arg.replace("/", "").length();
                String updateArg = arg.replace("*", "(.*)");

                try (Stream<Path> list =  Files.walk(currDir, count)) {

                    List<String> fileNames = list.map(x -> x.toString().replace(currentDirectory + "/", ""))
                                            .filter(f -> f.matches(updateArg))
                                            .collect(Collectors.toList());

                    fileNames.forEach(w -> updatedArgsList.add(w));
                    if(fileNames.isEmpty()){
                        updatedArgsList.add(arg);
                    }
                }
            }
            else {
                updatedArgsList.add(arg);
            }
        }
        return updatedArgsList;
    }
}