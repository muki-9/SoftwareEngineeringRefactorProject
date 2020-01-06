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
                

            }

            @Override
            public void visitErrorNode(ErrorNode node) {
                

            }

            @Override
            public void exitEveryRule(ParserRuleContext ctx) {
                

            }

            @Override
            public void enterEveryRule(ParserRuleContext ctx) {
                

            }

            @Override
            public void exitUnquoted(UnquotedContext ctx) {
                

            }

            @Override
            public void exitStart(StartContext ctx) {
                

            }

            @Override
            public void exitSinglequoted(SinglequotedContext ctx) {
                

            }

            @Override
            public void exitSeq(SeqContext ctx) {
                

            }

            @Override
            public void exitRedirection(RedirectionContext ctx) {
                

            }

            @Override
            public void exitPipe(PipeContext ctx) {
                

            }

            @Override
            public void exitDoublequoted(DoublequotedContext ctx) {
                

            }

            @Override
            public void exitCommand(CommandContext ctx) {
                

            }

            @Override
            public void exitCall(CallContext ctx) {
                

            }

            @Override
            public void exitBackquoted(BackquotedContext ctx) {
                

            }

            @Override
            public void exitArgument(ArgumentContext ctx) {
                

            }

            @Override
            public void enterUnquoted(UnquotedContext ctx) {
                

            }

            @Override
            public void enterStart(StartContext ctx) {
                

            }

            @Override
            public void enterSinglequoted(SinglequotedContext ctx) {
                

            }

            @Override
            public void enterSeq(SeqContext ctx) {
                

            }

            @Override
            public void enterRedirection(RedirectionContext ctx) {
                

            }

            @Override
            public void enterPipe(PipeContext ctx) {
                

            }

            @Override
            public void enterDoublequoted(DoublequotedContext ctx) {
                

            }

            @Override
            public void enterCommand(CommandContext ctx) {
                

            }

            @Override
            public void enterCall(CallContext ctx) {
                

            }

            @Override
            public void enterBackquoted(BackquotedContext ctx) {
                

            }

            @Override
            public void enterArgument(ArgumentContext ctx) {
                

            }
        };

        return agl;

    }

    public ParseTreeListener createParserListener() {

        ParseTreeListener ptl = new ParseTreeListener() {

            @Override
            public void visitTerminal(TerminalNode node) {
                

            }

            @Override
            public void visitErrorNode(ErrorNode node) {
                

            }

            @Override
            public void exitEveryRule(ParserRuleContext ctx) {
                

            }

            @Override
            public void enterEveryRule(ParserRuleContext ctx) {
                

            }
        };

        return ptl;
    }



}