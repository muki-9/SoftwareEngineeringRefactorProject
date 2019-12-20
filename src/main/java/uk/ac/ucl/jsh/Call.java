package uk.ac.ucl.jsh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PipedOutputStream;
import java.util.ArrayList;

public class Call extends Command implements CommandVisitable {
    private String application;
    private ArrayList<String> arguments;
    private String currArg; //this var is required to concatenate the different strings together after removal of quotations

    public Call(ArrayList<String> input) {
        this.application = input.get(0);
        this.arguments = extractArguments(input);
    }

    public Call(String newS) {
        this.currArg = newS;
    }

	public String getCurrArgs() {
        return currArg;
    }

	public String getApplication() {
        return application;
    }

    public ArrayList<String> getArguments() {
        return arguments;
    }

    @Override
    public void accept(CommandVisitor visitor) throws IOException {
        visitor.visitCall(this);
    }
}