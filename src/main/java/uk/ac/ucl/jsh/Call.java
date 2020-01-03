package uk.ac.ucl.jsh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class Call implements CommandVisitable {
    private String application;
    private ArrayList<String> arguments;
    private ArrayList<String> bqArray = new ArrayList<>();
    private String currArg; //this var is required to concatenate the different strings together after removal of quotations
    private boolean split = false;
    private String symbol;
    private boolean isRequired = false;
    private InputStream is;
    private boolean osRequired = false;
    private OutputStream os;
    private Boolean globb;
    private ArrayList<Boolean> gArray;

    public Call(String app, ArrayList<String> args, ArrayList<Boolean> gArray) {
        this.application = app;
        this.arguments = args;
        this.gArray = gArray;
    }

    public Call(String newS, Boolean globb) {
        this.currArg = newS;
        this.globb = globb;
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
    
    public Call(String currArgs, String symbol) {
        this.symbol = symbol;
        this.currArg = currArgs;
	}

	public Call(String app, ArrayList<String> args, InputStream is, ArrayList<Boolean> gArray) {
        this.application = app;
        this.arguments = args;
        isRequired = true;
        this.is = is;
        this.gArray = gArray;
	}

	public Call(String app, ArrayList<String> args, OutputStream os, ArrayList<Boolean> gArray) {
        this.application = app;
        this.arguments = args;
        osRequired = true;
        this.os = os;
        this.gArray = gArray;
    }
    
    public boolean getGlobb(){
        return globb;
    }

    public ArrayList<Boolean> getGlobbArray(){
        return gArray;
    }

	public String getSymbol() {
        return symbol;
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

	public boolean getISRequired() {
		return isRequired;
	}

	public InputStream getIS() {
		return is;
    }
    public boolean getOSRequired() {
		return osRequired;
	}

	public OutputStream getOS() {
		return os;
	}
}