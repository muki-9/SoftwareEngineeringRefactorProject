package uk.ac.ucl.jsh;

import java.util.ArrayList;

public abstract class Command {
    public ArrayList<String> extractArguments(ArrayList<String> input) {
        ArrayList<String> arguments = new ArrayList<>();
        for(int i=1;i<input.size();i++){
            arguments.add(input.get(i));
        }
        return arguments;
    }
}