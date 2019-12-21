package uk.ac.ucl.jsh;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Call implements CommandVisitable {
    private String application;
    private ArrayList<String> arguments;
    private ArrayList<String> bqArray = new ArrayList<>();
    private String currArg; //this var is required to concatenate the different strings together after removal of quotations
    private boolean split = false;

    public Call(String app, ArrayList<String> args) {
        this.application = app;
        this.arguments = args;
    }

    public Call(String newS) {
        this.currArg = newS;
    }

    public Call(String[] args) {
        for (String s : args) {
            this.bqArray.add(s);
        }
        this.split = true;
    }

    public Call(ArrayList<String> bqArgs) {
        this.bqArray = bqArgs;
        this.split = true;
	}

	public ArrayList<String> getBqArray() {
        return bqArray;
    }
    
    public boolean getSplit(){
        return split;
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