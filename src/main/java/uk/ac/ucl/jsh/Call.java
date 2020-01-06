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
    private InputStream is;
    private OutputStream os;
    private Boolean globb;
    private ArrayList<Boolean> gArray;

    /**
    * Special constructor required to pass string from one method in parse tree visitor to another.
    *
    * @param newS string passed from one method
    *
    */
    public Call(String newS, Boolean globb) {
        this.currArg = newS;
        this.globb = globb;
    }

    /**
    * Special constructor required to pass a list of strings from one method in the parse tree visitor to another.
    * Makes instance variable "split" true, so that in the receiving method a check can be undertaken so we know
    * we can extract a list, and we need to split it into its separate arguments.
    *
    * @param args list of strings passed from one method
    *
    */
    public Call(String[] args) {
        for (String s : args) {
            this.bqArray.add(s);
        }
        this.split = true;
    }

    /**
    * Special constructor required to pass an arraylist of strings from one method in the parse tree visitor to another.
    * Makes instance variable "split" true, so that in the receiving method a check can be undertaken so we know
    * we can extract a list, and we need to split it into its separate arguments.
    *
    * @param bqArgs arraylist of strings passed from one method- all the arguments received inside backquotations (hence the bq)
    *
    */
    public Call(ArrayList<String> bqArgs) {
        this.bqArray = bqArgs;
        this.split = true;
    }
    
    /**
    * Special constructor required to tell the receiving method in the parse tree visitor class that a redirection symbol was spotted.
    * The receiving method can then retrieve the correct arguments and symbol and then undertake relevant action.
    *
    * @param redirectionArg the argument that follows the redirection symbol
    * @param symbol the redirection symbol, either '>' or '<'
    *
    */
    public Call(String redirectionArg, String symbol) {
        this.symbol = symbol;
        this.currArg = redirectionArg;
	}
	
    /**
    * Special constructor required to pass an output stream to the receiving method. The instance variable "osRequired"
    * is set to true, so in the receiving method a check can be undertaken that lets us know an output stream can be extracted
    * as well as the other two arguments.
    *
    * @param app string, containing name of the applications
    * @param args arraylist containing string of all arguments supplied to app.
    * @param os an output stream, to which to write an input to.
    *
    */

    public Call(String app, ArrayList<String>args, OutputStream os, InputStream is, ArrayList<Boolean> gArray){
        this.application = app;
        this.arguments = args; 
        this.os = os;
        this.is = is;   
        this.gArray = gArray;
    }
    
    /**
    * The rest of the methods include getters for all the different instance variables described in the constructors above.
    * There is also the accept method, which visits the specific Call object that the accept command is called on.
    */

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

	public InputStream getIS() {
		return is;
    }

	public OutputStream getOS() {
		return os;
	}
}