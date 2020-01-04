package uk.ac.ucl.jsh;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;

import uk.ac.ucl.jsh.AntlrGrammarParser.CallContext;
import uk.ac.ucl.jsh.AntlrGrammarParser.DoublequotedContext;
import uk.ac.ucl.jsh.AntlrGrammarParser.RedirectionContext;
import uk.ac.ucl.jsh.AntlrGrammarParser.SinglequotedContext;

/**
 * most methods in this class return an instance of the Call() object, for this
 * reason, the class 'Call' has many overloaded contructors that change the
 * class' instance variables accordingly.
 */
public class MyTreeVisitor extends AntlrGrammarBaseVisitor<CommandVisitable> {

    

    /**
    * The right-most child of a redirection node will always be an argument. The left most child will either
    * be an input or output redirection. Depending in which it is, a special symbol is given as an argument
    * to the Call object returned which will tell the Constructor in the Call class to make one of two specific instance variables true
    * (depending on the type of redirection required).
    *
    * @param ctx The context (node) the parser is at, will be a redirection node, which will
    * have 3 children. The left-most is the redirection symbol (either '>' or '<'), the right-most will
    * be the argument, usually a filename, and the middle-child will be a whitespace constant.
    *
    * @return a Call object with a string as an argument and the relevant redirection symbol. 
    * Later on, in the "visitCall" method of this class, 
    * a check is performed to see which, which of these instance variables is true to run the correct procedures
    * if neither are true, a normal Call instance is returned.
    */
    @Override
    public CommandVisitable visitRedirection(AntlrGrammarParser.RedirectionContext ctx) {
        if (ctx.getChildCount() > 3) {
            throw new RuntimeException("antlr: too many arguments given to IO redirection symbol");
        }
        if (ctx.getChild(0).getText().matches(">")) {
            Call c = (Call) ctx.argument().accept(this);
            return new Call(c.getCurrArgs(), ">");

        } else if (ctx.getChild(0).getText().matches("<")) {
            Call c = (Call) ctx.argument().accept(this);
            return new Call(c.getCurrArgs(), "<");
        } else {
            throw new RuntimeException("antlr: invalid redirection arguments");
        }
    }

    /**
    * The child of a backquoted node will contain a command wrapped in backquote-quotes.
    * We instantiate a new Jsh object, to pipe the output of that shell into a variable which we
    * return as an argument in the Call object. The constructor in the call class will make an 
    * instance variable in the Call class true. If this instance variable is true
    * in a Call received by another node, then we know to extract this string using getters.
    *
    * @param ctx The context (node) the parser is at, will be a backquoted node, which will
    * have a command, wrapped in back-quotes as a child.
    *
    * @return a Call object with a string as an argument. The string is the result of the command evaluated
    * by the new shell instance.
    */
    @Override
    public CommandVisitable visitBackquoted(AntlrGrammarParser.BackquotedContext ctx) {
        OutputStream writer;
        String s = null;
        try {
            Jsh j = new Jsh();
            InputStream input = new PipedInputStream(90000);
            writer = new PipedOutputStream((PipedInputStream) input);
            String temp = ctx.getText();
            j.eval(temp.substring(1, temp.length() - 1), writer);
            writer.close();
            s = new String(input.readAllBytes());
            s = s.replaceAll(System.getProperty("line.separator"), " ");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Call(s.split(" "));
    }

    /**
    * The child of a doublequoted node will contain the raw text we want to extract wrapped in double-quotes or it can contain a
    * back-quoted command.

    * If it is simply text in double quotes, we remove these quotes, by removing the first and last chracter and the remianing substring is passed into the
    * Call method and the constructor will make an instance variable in the Call class true. If this instance variable is true
    * in a Call received by any node, then we know to extract this string using getters.
    *
    * If a back-quote is found within the text, then the backquoted node is visited first, which returns the correct arguments to be passed instead.
    *
    * @param ctx The context (node) the parser is at, will be a backquoted node, which will
    * can have the raw text as its child wrapped in double-quotes.
    *
    * @return a Call object, with unquoted text as an argument.
    */
    @Override
    public CommandVisitable visitDoublequoted(DoublequotedContext ctx) {
        String s = "";
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i).getText().contains("`")) {
                Call c = (Call) ctx.getChild(i).accept(this);
                for (String args : c.getBqArray()) {
                    s = s.concat(args);
                }
                c.getBqArray();
            } else {
                s = s.concat(ctx.getChild(i).getText());
            }
        }
        String newS = s.substring(1, s.length() - 1);
        return new Call(newS, false);
    }

    /**
    * The child of a singlequoted node will contain just the raw text we want to extract wrapped in single-quotes. 
    * We remove these quotes, by removing the first and last chracter and the remianing substring is passed into the
    * Call method and the constructor will make an instance variable in the Call class true. If this instance variable is true
    * in a Call received by any node, then we know to extract this string using getters.
    *
    * @param ctx The context (node) the parser is at, will be a singlequoted node, which will
    * can have the raw text as its child wrapped in single-quotes.
    *
    * @return a Call object, with unquoted text as an argument.
    */
    @Override
    public CommandVisitable visitSinglequoted(SinglequotedContext ctx) {
        String s = ctx.getText();
        String newS = s.substring(1, s.length() - 1);
        return new Call(newS, false);
    }


    /**
    * When at an unquoted node, the child will contain just the raw text we want to extract, this is passed into the
    * Call method and the constructor will make an instance variable in the Call class true. If this instance variable is true
    * in a Call received by any node, then we know to extract this string using getters.
    *
    * @param ctx The context (node) the parser is at, will be an unquoted node, which will
    * can have the raw text as its child.
    *
    * @return a Call object, with the unquoted text as an argument.
    */
    @Override
    public CommandVisitable visitUnquoted(AntlrGrammarParser.UnquotedContext ctx) {
        return new Call(ctx.getChild(0).getText(), true);
    }

    /**
    * When the parser is at an argument node, it needs to correctly extract the string
    * from that node. This function visits its children, and then depending on whether the given
    * arguments are quoted or not, extracts the correct strings.
    *
    * This method sets an instance variable (arg) in the Call class to true, so that when this instance of Class
    * is later extracted in the Call function, a test for whether arg==true will let us know whether to treat the given Call
    * object as an actual application Call or simply a messenger for the (list of) String(s) it carries
    *
    * @param ctx The context (node) the parser is at, will be an argument node, which will
    * can have many different types of children- either unquoted, single-quoted, back-quoted
    * or double-quoted. Visiting these nodes using the .accept method will return other instances of call, where we can get other
    * arguments from.
    *
    * @return a Call object, with either a string as the argument or an ArrayList of strings- depending on which it is,
    * the constructor in the Call class will make a certain instance variable true. Then, in the visitCall method, a test
    * for whether this instance variable is true, will let us know whether to treat the given Call object as an actual application Call 
    * or simply a messenger for the (list of) String(s) which we can extract using getters.
    */
    @Override
    public CommandVisitable visitArgument(AntlrGrammarParser.ArgumentContext ctx) {
        ArrayList<ParseTree> s = new ArrayList<>();
        String string = "";
        ArrayList<String> bqArgs = new ArrayList<>();
        boolean arg = false;
        boolean integrate = false;
        boolean globb = false;
        if (ctx.unquoted().size() > 0 && ctx.backquoted().size() > 0) {
            integrate = true;
        }

        for (int f = 0; f < ctx.getChildCount(); f++) {
            s.add(ctx.getChild(f));
        }
        for (int i = 0; i < s.size(); i++) {
            Call c = (Call) s.get(i).accept(this);
            if (c.getSplit()) {
                if (!integrate) {
                    bqArgs = c.getBqArray();
                    arg = true;
                } else {
                    for (String vals : c.getBqArray()) {
                        string = string.concat(vals);
                    }
                }
            } else {
                globb = c.getGlobb();
                string = string.concat(c.getCurrArgs());
            }
        }
        if (integrate) {
            return new Call(string, globb);
        }
        if (arg) {
            return new Call(bqArgs);
        }
        return new Call(string, globb);
    }

    /**
    * Creates an instance of the Pipe Command, creates two other commands from the context given.
    *
    * @param ctx The context (node) the parser is at, will be a Pipe node, which will
    * have 3 children, left-most being one command, right-most being another, and the middle
    * child is the "|" delimiter.
    *
    * @return a Pipe object, with the correct parameters required by the eval function to
    * separate the two commands between the "|" delimiter and then register them as two
    * separate commands.
    */
    @Override
    public CommandVisitable visitPipe(AntlrGrammarParser.PipeContext ctx) {
        return new Pipe(ctx.getChild(0).accept(this), ctx.getChild(2).accept(this));
    }

    /**
    * Creates an instance of the Seq Command, creates two other commands from the context given.
    *
    * @param ctx The context (node) the parser is at, will be a Seq node, which will
    * have 3 children, left-most being one command, right-most being another, and the middle
    * child is the ";" delimiter.
    *
    * @return a Seq object, with the correct parameters required by the eval function to
    * separate the two commands between the ";" delimiter and then register them as two
    * separate commands.
    */
    @Override
    public CommandVisitable visitSeq(AntlrGrammarParser.SeqContext ctx) {
        CommandVisitable leftChild = ctx.getChild(0).accept(this);
        CommandVisitable rightChild = ctx.getChild(2).getChild(0).accept(this);
        return new Seq(leftChild, rightChild);
    }


    /**
    * Figures out whether or not input/output redirection is required.
    * If it is, the input/output streams is configured accordingly and,
    * the correct application and arguments are configured for redirection.
    * If IO redirection is not required, the correct application and argument 
    * is simply returned with no streams.
    *
    * @param ctx The context (node) the parser is at, will be a call node
    * whose children are the application and arguments (with other nooes in between
    * such as quoted, unquoted, argument and so forth)
    *
    * @return a Call object, with the correct parameters required by the eval function to
    * create an instance of the Application from the factory.
    */
    @Override
    public CommandVisitable visitCall(AntlrGrammarParser.CallContext ctx) {
        List<RedirectionContext> redirections = ctx.redirection();
        if (redirections.size() == 0) {
            ArrayList<String> args = getCallArgs(ctx);
            ArrayList<Boolean> globb = getGlobbArray(ctx);
            String app = args.get(0);
            globb.remove(0);
            args.remove(0);
            return new Call(app, args, globb);

        } else {
            for (int i = 0; i < redirections.size();) {
                Call c = (Call) redirections.get(i).accept(this);
                String filePath = Jsh.getCurrentDirectory() + System.getProperty("file.separator") + c.getCurrArgs();
                File file = new File(filePath);
                OutputStream os;

                if (c.getSymbol().matches(">")) {
                    try {
                        if (file.createNewFile()) {
                            System.out.println("redirection: file created successfully");
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("redirection: file could not be created");
                    }
                    try {
                        os = new FileOutputStream(file);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException("redirection: error writing to file");
                    }
                    ArrayList<String> args = getCallArgs(ctx);
                    ArrayList<Boolean> globb = getGlobbArray(ctx);
                    String app = null;
                    if (args.size()>0) {
                        app = args.get(0);
                        args.remove(0);
                    }
                    return new Call(app, args, os, globb);

                } else if (c.getSymbol().matches("<")) {
                    if (!file.exists()) {
                        throw new RuntimeException("redirection: file could not be found");
                    }
                    Charset encoding = StandardCharsets.UTF_8;
                    Path path = Paths.get(filePath);
                    InputStream is;
                    try (BufferedReader reader = Files.newBufferedReader(path, encoding)) {
                        is = new PipedInputStream(90000);

                        OutputStream outputstream = new PipedOutputStream((PipedInputStream) is);
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputstream));
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            writer.write(String.valueOf(line));
                            writer.write(System.getProperty("line.separator"));
                            writer.flush();
                        }
                        writer.close();
                    } catch (IOException e) {
                        throw new RuntimeException("redirection: error reading input from file");
                    }

                    ArrayList<String> args = getCallArgs(ctx);
                    ArrayList<Boolean> globb = getGlobbArray(ctx);
                    String app = null;
                    if (args.size()>0) {
                        app = args.get(0);
                        args.remove(0);
                        globb.remove(0);
                    }
                    return new Call(app, args, is, globb);
                }
            }
        }
        return null;
    }


    /**
    * auxilliary function for call method
    * performs visits all the nodes required to get the application and correct arguments
    * separated from Call() function for reusability.
    *
    * @param ctx The context (node) the parser is at, will be call node
    * whose children are the application and arguments (with other nooes in between
    * such as quoted, unquoted, argument and so forth)
    *
    * @return An array list where the first element is the name of the aplication, as a string,
    * and the rest of the elements are the arguments
    */
    private ArrayList<String> getCallArgs(CallContext ctx) {
        ArrayList<String> args = new ArrayList<>();
        for(int n = 0; n<ctx.argument().size();n++) {
            Call call = (Call) ctx.argument().get(n).accept(this);
            if (call.getCurrArgs() != null) {
                args.add(call.getCurrArgs());
            }
            if (call.getSplit()) {
                for(String s:call.getBqArray()) {
                    args.add(s);
                }
            }
        }
        return args;
    }

    public ArrayList<Boolean> getGlobbArray(CallContext ctx){

        ArrayList<Boolean> globb = new ArrayList<>();
        for(int n = 0; n<ctx.argument().size();n++) {
            Call call = (Call) ctx.argument().get(n).accept(this);
            if (call.getCurrArgs() != null) {
                globb.add(call.getGlobb());
            }
            if (call.getSplit()) {
                for(String s:call.getBqArray()) {
                    globb.add(false);
                }
            }
        }
        return globb;
    }

}