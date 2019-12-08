package uk.ac.ucl.jsh;

import java.io.IOException;
import java.util.ArrayList;

public class Call extends Command implements CommandVisitable {
    private String application;
    private ArrayList<String> arguments;

    public Call(ArrayList<String> input) {
        this.application = input.get(0);
        this.arguments = extractArguments(input);
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