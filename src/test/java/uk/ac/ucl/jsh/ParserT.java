package uk.ac.ucl.jsh;
import java.util.ArrayList;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;


import uk.ac.ucl.jsh.AntlrGrammarParser.ArgumentContext;
import uk.ac.ucl.jsh.AntlrGrammarParser.BackquotedContext;
import uk.ac.ucl.jsh.AntlrGrammarParser.CallContext;
import uk.ac.ucl.jsh.AntlrGrammarParser.CommandContext;
import uk.ac.ucl.jsh.AntlrGrammarParser.DoublequotedContext;
import uk.ac.ucl.jsh.AntlrGrammarParser.PipeContext;
import uk.ac.ucl.jsh.AntlrGrammarParser.RedirectionContext;
import uk.ac.ucl.jsh.AntlrGrammarParser.SeqContext;
import uk.ac.ucl.jsh.AntlrGrammarParser.SinglequotedContext;
import uk.ac.ucl.jsh.AntlrGrammarParser.StartContext;
import uk.ac.ucl.jsh.AntlrGrammarParser.UnquotedContext;

public class ParserT {

    public AntlrGrammarParser createParserNoError(ArrayList<TestToken> tokens) {

        ListTokenSource l = new ListTokenSource(tokens);
        CommonTokenStream c = new CommonTokenStream(l);
        AntlrGrammarParser p = new AntlrGrammarParser(c);

        // NoErrorListener n = new NoErrorListener();
        // p.addErrorListener(n);
        return p;
    }

    public AntlrGrammarListener createAntlrGrammarListener() {

        AntlrGrammarListener agl = new AntlrGrammarListener() {

            @Override
            public void visitTerminal(TerminalNode node) {
                // TODO Auto-generated method stub

            }

            @Override
            public void visitErrorNode(ErrorNode node) {
                // TODO Auto-generated method stub

            }

            @Override
            public void exitEveryRule(ParserRuleContext ctx) {
                // TODO Auto-generated method stub

            }

            @Override
            public void enterEveryRule(ParserRuleContext ctx) {
                // TODO Auto-generated method stub

            }

            @Override
            public void exitUnquoted(UnquotedContext ctx) {
                // TODO Auto-generated method stub

            }

            @Override
            public void exitStart(StartContext ctx) {
                // TODO Auto-generated method stub

            }

            @Override
            public void exitSinglequoted(SinglequotedContext ctx) {
                // TODO Auto-generated method stub

            }

            @Override
            public void exitSeq(SeqContext ctx) {
                // TODO Auto-generated method stub

            }

            @Override
            public void exitRedirection(RedirectionContext ctx) {
                // TODO Auto-generated method stub

            }

            @Override
            public void exitPipe(PipeContext ctx) {
                // TODO Auto-generated method stub

            }

            @Override
            public void exitDoublequoted(DoublequotedContext ctx) {
                // TODO Auto-generated method stub

            }

            @Override
            public void exitCommand(CommandContext ctx) {
                // TODO Auto-generated method stub

            }

            @Override
            public void exitCall(CallContext ctx) {
                // TODO Auto-generated method stub

            }

            @Override
            public void exitBackquoted(BackquotedContext ctx) {
                // TODO Auto-generated method stub

            }

            @Override
            public void exitArgument(ArgumentContext ctx) {
                // TODO Auto-generated method stub

            }

            @Override
            public void enterUnquoted(UnquotedContext ctx) {
                // TODO Auto-generated method stub

            }

            @Override
            public void enterStart(StartContext ctx) {
                // TODO Auto-generated method stub

            }

            @Override
            public void enterSinglequoted(SinglequotedContext ctx) {
                // TODO Auto-generated method stub

            }

            @Override
            public void enterSeq(SeqContext ctx) {
                // TODO Auto-generated method stub

            }

            @Override
            public void enterRedirection(RedirectionContext ctx) {
                // TODO Auto-generated method stub

            }

            @Override
            public void enterPipe(PipeContext ctx) {
                // TODO Auto-generated method stub

            }

            @Override
            public void enterDoublequoted(DoublequotedContext ctx) {
                // TODO Auto-generated method stub

            }

            @Override
            public void enterCommand(CommandContext ctx) {
                // TODO Auto-generated method stub

            }

            @Override
            public void enterCall(CallContext ctx) {
                // TODO Auto-generated method stub

            }

            @Override
            public void enterBackquoted(BackquotedContext ctx) {
                // TODO Auto-generated method stub

            }

            @Override
            public void enterArgument(ArgumentContext ctx) {
                // TODO Auto-generated method stub

            }
        };

        return agl;

    }

    public ParseTreeListener createParserListener() {

        ParseTreeListener ptl = new ParseTreeListener() {

            @Override
            public void visitTerminal(TerminalNode node) {
                // TODO Auto-generated method stub

            }

            @Override
            public void visitErrorNode(ErrorNode node) {
                // TODO Auto-generated method stub

            }

            @Override
            public void exitEveryRule(ParserRuleContext ctx) {
                // TODO Auto-generated method stub

            }

            @Override
            public void enterEveryRule(ParserRuleContext ctx) {
                // TODO Auto-generated method stub

            }
        };

        return ptl;
    }



}