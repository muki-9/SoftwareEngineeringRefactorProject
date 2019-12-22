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
import uk.ac.ucl.jsh.AntlrGrammarParser.DoublequotedContext;
import uk.ac.ucl.jsh.AntlrGrammarParser.RedirectionContext;
import uk.ac.ucl.jsh.AntlrGrammarParser.SinglequotedContext;

public class MyTreeVisitor extends AntlrGrammarBaseVisitor<CommandVisitable> {

    @Override
    public CommandVisitable visitRedirection(AntlrGrammarParser.RedirectionContext ctx) {
        if (ctx.getChildCount() > 3) {
            throw new RuntimeException("antlr: too many arguments given to IO redirection symbol");
        }
        if (ctx.getChild(0).getText().matches(">")) {
            Call c = (Call) ctx.getChild(2).accept(this);
            return new Call(c.getCurrArgs(), ">");

        } else if (ctx.getChild(0).getText().matches("<")) {
            Call c = (Call) ctx.getChild(2).accept(this);
            return new Call(c.getCurrArgs(), "<");
        } else {
            throw new RuntimeException("antlr: invalid redirection arguments");
        }
    }

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
        return new Call(newS);
    }

    @Override
    public CommandVisitable visitSinglequoted(SinglequotedContext ctx) {
        String s = ctx.getText();
        String newS = s.substring(1, s.length() - 1);
        return new Call(newS);
    }

    @Override
    public CommandVisitable visitUnquoted(AntlrGrammarParser.UnquotedContext ctx) {
        return new Call(ctx.getChild(0).getText());
    }

    @Override
    public CommandVisitable visitArgument(AntlrGrammarParser.ArgumentContext ctx) {
        ArrayList<ParseTree> s = new ArrayList<>();
        String string = "";
        ArrayList<String> bqArgs = new ArrayList<>();
        boolean arg = false;

        for (int f = 0; f < ctx.getChildCount(); f++) {
            s.add(ctx.getChild(f));
        }

        for (int i = 0; i < s.size(); i++) {
            Call c = (Call) s.get(i).accept(this);
            if (c.getSplit()) {
                bqArgs = c.getBqArray();
                arg = true;
            } else {
                string = string.concat(c.getCurrArgs());
            }
        }
        if (arg) {
            return new Call(bqArgs);
        }
        return new Call(string);
    }

    @Override
    public CommandVisitable visitPipe(AntlrGrammarParser.PipeContext ctx) {
        return new Pipe(ctx.getChild(0).accept(this), ctx.getChild(2).accept(this));
    }

    @Override
    public CommandVisitable visitSeq(AntlrGrammarParser.SeqContext ctx) {
        CommandVisitable leftChild = ctx.getChild(0).accept(this);
        CommandVisitable rightChild = ctx.getChild(2).getChild(0).accept(this);
        return new Seq(leftChild, rightChild);
    }

    @Override
    public CommandVisitable visitCall(AntlrGrammarParser.CallContext ctx) {
        List<RedirectionContext> redirections = ctx.redirection();
        if (redirections.size() == 0) {
            ArrayList<String> args = new ArrayList<>();
            for (int i = 0; i < ctx.argument().size(); i++) {
                Call c = (Call) ctx.argument().get(i).accept(this);
                if (c.getCurrArgs() != null) {
                    args.add(c.getCurrArgs());
                }
                if (c.getSplit()) {
                    for (String s : c.getBqArray()) {
                        args.add(s);
                    }
                }
            }
            String app = args.get(0);
            args.remove(0);
            return new Call(app, args);
        } else {
            for (int i = 0; i < redirections.size(); i++) {
                Call c = (Call) redirections.get(i).accept(this);
                String filePath = Jsh.getCurrentDirectory() + System.getProperty("file.separator") + c.getCurrArgs();
                File file = new File(filePath);
                OutputStream os;
                if (c.getSymbol().matches(">")) {
                    if (!file.exists()) {
                        try {
                            if (!file.createNewFile()) {
                                throw new RuntimeException("redirection: file could not be created");
                            }
                        } catch (IOException e) {
                            throw new RuntimeException("redirection: file could not be created exception");
                        }
                    }
                    try {
                        os = new FileOutputStream(file);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException("redirection: file not found exception");
                    }

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
                    String app = args.get(0);
                    args.remove(0);
                    return new Call(app, args, os);

                }
                else if (c.getSymbol().matches("<")) {
                    if (!file.exists()) {
                        throw new RuntimeException("redirection: file could not found");
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
                    String app = args.get(0);
                    args.remove(0);
                    return new Call(app, args, is);
                }
                else {
                    throw new RuntimeException("antlr: parsing error- invalid character: "+c.getSymbol());
                }
            }

            // ArrayList<String> args = new ArrayList<>();
            // for(int i = 0; i<ctx.argument().size();i++) {
            //     Call c = (Call) ctx.argument().get(i).accept(this);
            //     if (c.getCurrArgs() != null) {
            //         args.add(c.getCurrArgs());
            //     }
            //     if (c.getSplit()) {
            //         for(String s:c.getBqArray()) {
            //             args.add(s);
            //         }
            //     }
            // }
            // String app = args.get(0);
            // args.remove(0);
            // return new Call(app, args);
        }
        return null;
    }
}