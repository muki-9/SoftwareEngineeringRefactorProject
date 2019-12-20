package uk.ac.ucl.jsh;

import java.util.ArrayList;
import org.antlr.v4.runtime.tree.ParseTree;

public class MyTreeVisitor extends AntlrGrammarBaseVisitor<CommandVisitable> {

    @Override
    public CommandVisitable visitBackquoted(AntlrGrammarParser.BackquotedContext ctx) {
        String s = ctx.getChild(0).getText();
        String newS = s.substring(1, s.length()-1);
        
        return new Call(newS);
    }

    @Override
    public CommandVisitable visitQuoted(AntlrGrammarParser.QuotedContext ctx) {
        String s = ctx.getChild(0).getText();
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

        for(int f=0;f<ctx.getChildCount();f++) {
            s.add(ctx.getChild(f));
        }
        for(int i=0;i<s.size();i++) {
            Call c = (Call) s.get(i).accept(this);
            string = string.concat(c.getCurrArgs());
        }
        return new Call(string);
    }

    @Override 
    public CommandVisitable visitRedirection(AntlrGrammarParser.RedirectionContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public CommandVisitable visitPipe(AntlrGrammarParser.PipeContext ctx) {
        // return new Pipe(ctx.call().get(0).accept(this), ctx.call().get(1).accept(this));
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
            args.add(c.getCurrArgs());
        }
        return new Call(args);
    }
}