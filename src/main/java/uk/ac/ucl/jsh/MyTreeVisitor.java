package uk.ac.ucl.jsh;

import java.util.ArrayList;
import java.util.List;

import uk.ac.ucl.jsh.AntlrGrammarParser.ArgumentContext;

public class MyTreeVisitor extends AntlrGrammarBaseVisitor<CommandVisitable> {

    // @Override
    // public CommandVisitable visitRedirect(AntlrGrammarParser.PipeContext ctx) {
    // return null;
    // }

    @Override
    public CommandVisitable visitPipe(AntlrGrammarParser.PipeContext ctx) {
        CommandVisitable leftChild = ctx.getChild(0).accept(this);
        CommandVisitable rightChild = ctx.getChild(2).accept(this);
        return new Pipe(leftChild, rightChild);
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
            args.add(ctx.argument(i).getChild(0).getText());
        }
        return new Call(args);
    }
}