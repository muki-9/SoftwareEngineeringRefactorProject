package uk.ac.ucl.jsh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;

import uk.ac.ucl.jsh.AntlrGrammarParser.BackquotedContext;
import uk.ac.ucl.jsh.AntlrGrammarParser.DoublequotedContext;
import uk.ac.ucl.jsh.AntlrGrammarParser.SinglequotedContext;

public class MyTreeVisitor extends AntlrGrammarBaseVisitor<CommandVisitable> {

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
        for(int i=0;i<ctx.getChildCount();i++) {
            if (ctx.getChild(i).getText().contains("`")) {
                Call c = (Call) ctx.getChild(i).accept(this);
                for(String args:c.getBqArray()) {
                    s = s.concat(args);
                }
                c.getBqArray();
            }
            else {
                s = s.concat(ctx.getChild(i).getText());
            }
        }
        String newS = s.substring(1, s.length()-1);
        return new Call(newS);
    }

    @Override
    public CommandVisitable visitSinglequoted(SinglequotedContext ctx) {
        String s = ctx.getText();
        String newS = s.substring(1, s.length()-1);
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

        for(int f=0;f<ctx.getChildCount();f++) {
            s.add(ctx.getChild(f));
        }

        for(int i=0;i<s.size();i++) {
            Call c = (Call) s.get(i).accept(this);
            if (c.getSplit()) {
                bqArgs = c.getBqArray();
                arg = true;
            }
            else {
                string = string.concat(c.getCurrArgs());
            }
        }
        if (arg) {
            return new Call(bqArgs);
        }
        return new Call(string);
    }

    @Override 
    public CommandVisitable visitRedirection(AntlrGrammarParser.RedirectionContext ctx) {
        return visitChildren(ctx);
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
        ArrayList<String> args = new ArrayList<>();
        for(int i = 0; i<ctx.argument().size();i++) {
            Call c = (Call) ctx.argument().get(i).accept(this);
            if (c.getCurrArgs() != null) {
                args.add(c.getCurrArgs());
            }
            if (c.getSplit()) {
                for(String s:c.getBqArray()) {
                    args.add(s);
                }
            }
        }
        String app = args.get(0);
        args.remove(0);
        return new Call(app, args);
    }
}