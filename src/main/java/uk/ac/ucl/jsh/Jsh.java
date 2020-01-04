package uk.ac.ucl.jsh;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Scanner;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Jsh {
    private static String currentDirectory = System.getProperty("user.dir");
    final private static String homeDirectory = System.getProperty("user.dir");
    private static ArrayList<String> commands = new ArrayList<>();
    private static boolean default_constr = true;
    public Jsh(){

    }
    public Jsh(boolean bool) {
        default_constr = bool;

    }
    
    public void eval(String cmdline, OutputStream output) throws IOException {
        Jsh.commands.add(cmdline);
        CharStream cs = CharStreams.fromString(cmdline);
        AntlrGrammarLexer lexer = new AntlrGrammarLexer(cs);
        CommonTokenStream cts = new CommonTokenStream(lexer);
        AntlrGrammarParser parser = new AntlrGrammarParser(cts);
        ParseTree tree = parser.start();
        MyTreeVisitor myVisitor = new MyTreeVisitor();
        CommandVisitable command = myVisitor.visit(tree);
        CommandVisitor commandVisitor = new Eval(output);
        command.accept(commandVisitor);
    }

    public static String getHomeDirectory() {
        return homeDirectory;
    }

    public static ArrayList<String> getCommands() {
		return commands;
	}

    public static String getCurrentDirectory() {
        return currentDirectory;
    }
    
    public static void setCurrentDirectory(String dir) {
        Jsh.currentDirectory = dir;
    }

    /**
     * 
     * @param s - this is the string the user enters at the command line
     * @return boolean value- true when enterred text is blank or contains nothing but space (ascii value 32)
     */
    public static boolean blankShell(String s) {
        if (s.length() == 0) {
            return true;
        }
        for (int i = 0; i < s.length(); i++) {
            if ((int) s.charAt(i) != 32) {
                return false;
            }
        }
        return true;
    }

    public void takesInput(){
        Scanner input= new Scanner(System.in);
        do{
            String prompt = currentDirectory + "> ";
            System.out.print(prompt);
            try{
                String cmdline = input.nextLine();
                if(blankShell(cmdline)){
                    continue;
                }
                eval(cmdline, System.out);
            }catch(Exception e){
                System.out.println("jsh: "+e.getMessage());

            }

        }while(default_constr);
        input.close();


        // while (true) {
        //     String prompt = currentDirectory + "> ";
        //     System.out.print(prompt);
        //     try {
        //         String cmdline = input.nextLine();
        //         if (blankShell(cmdline)) {
        //             continue;
        //         }
        //         eval(cmdline, System.out);
        //     } catch (Exception e) {
        //         System.out.println("jsh: here" + e.getMessage());
        //     }
        // }

    }
    
    public static void main(String[] args) throws IOException{


        Jsh newShell = new Jsh();
        if (args.length > 0) {

            if (args.length != 2) {
                throw new RuntimeException("jsh: wrong number of arguments");

            }
            if (!args[0].equals("-c")) {
                throw new RuntimeException("jsh: " + args[0] + ": unexpected argument");
            }
    
            newShell.eval(args[1], System.out);
   
            } 
            else {
                newShell.takesInput();
                
                // Scanner input = new Scanner(System.in);
                // try {
                //     do {
                //         String prompt = currentDirectory + "> ";
                //         System.out.println(prompt);
              
                //         try {
                //             String cmdline = "okay";
                //             if (blankShell(cmdline)) {
                //                 continue;
                //             }
                //             newShell.eval(cmdline, System.out);
                //         } catch (Exception e) {
                //             System.out.println("jsh: " + e.getMessage());
                //         }
                //     }while(default_constr);
                // } finally {
                //     input.close();
                // }
 
            }
        }
}