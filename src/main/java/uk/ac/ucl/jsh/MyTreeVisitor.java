package uk.ac.ucl.jsh;

import java.util.ArrayList;

public class MyTreeVisitor extends AntlrGrammarBaseVisitor<CommandVisitable> {

    @Override
    public CommandVisitable visitPipe(AntlrGrammarParser.PipeContext ctx) {
        // System.out.println("came to pipe");
        return visitChildren(ctx);
    }

    @Override
    public CommandVisitable visitSeq(AntlrGrammarParser.SeqContext ctx) {
        CommandVisitable leftChild = ctx.getChild(0).accept(this);
        CommandVisitable rightChild = ctx.getChild(1).accept(this);
        return new Seq(leftChild, rightChild);
    }

    @Override
    public CommandVisitable visitCall(AntlrGrammarParser.CallContext ctx) {
        ArrayList<String> args = new ArrayList<>();
        for(int i = 0; i<ctx.getChild(0).getChildCount();i++) {
            args.add(ctx.getChild(0).getChild(i).getText());
        }
        return new Call(args);
    }
}