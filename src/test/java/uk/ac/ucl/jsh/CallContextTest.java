package uk.ac.ucl.jsh;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.ArrayList;

import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.junit.Test;

import uk.ac.ucl.jsh.AntlrGrammarParser.CallContext;

public class CallContextTest extends ParserT{

    public CallContextTest(){

    }

    @Test
    public void testCall(){
        ArrayList<TestToken> tokens = new ArrayList<>(){
            /**
            *
            */
            private static final long serialVersionUID = -112447375692081252L;
            {
            add(new TestToken("grep", AntlrGrammarLexer.UQ));
            add(new TestToken(" ", AntlrGrammarLexer.WS));
            add(new TestToken("\"Interesting string\"", AntlrGrammarLexer.UQ));
            add(new TestToken(" ", AntlrGrammarLexer.WS));
            add(new TestToken("<", AntlrGrammarLexer.LT));
            add(new TestToken(" ", AntlrGrammarLexer.WS));
            add(new TestToken("text.txt", AntlrGrammarLexer.UQ));
            add(new TestToken(" ", AntlrGrammarLexer.WS));
            add(new TestToken(">", AntlrGrammarLexer.GT));
            add(new TestToken(" ", AntlrGrammarLexer.WS));
            add(new TestToken("result.txt", AntlrGrammarLexer.UQ));
            
        }};
        AntlrGrammarParser ap = createParserNoError(tokens);
        CallContext call = ap.call();

        assertThat(call.argument().get(0).getText()).isEqualTo("grep");
        assertThat(call.WS(0)).isNotNull();
        assertThat(call.argument(1).getText()).isEqualTo("\"Interesting string\"");
        assertThat(call.WS().get(1)).isNotNull();
        assertThat(call.redirection().get(0).getText()).isEqualTo("< text.txt");
        assertThat(call.redirection(1).getText()).isEqualTo("> result.txt");
        assertThat(call.getRuleIndex()).isNotNull();
    }
            

    @Test
    public void callTakingRedirFromStart(){

        ArrayList<TestToken> tokens = new ArrayList<>(){
            /**
            *
            */
            private static final long serialVersionUID = -112447375692081252L;

            {
            add(new TestToken(">", AntlrGrammarLexer.GT));
            add(new TestToken(" ", AntlrGrammarLexer.WS));
            add(new TestToken("result.txt", AntlrGrammarLexer.UQ));
            add(new TestToken(" ", AntlrGrammarLexer.WS));
            add(new TestToken(";", AntlrGrammarLexer.UQ));
            add(new TestToken(" ", AntlrGrammarLexer.SEMICOL));
            add(new TestToken("cat", AntlrGrammarLexer.UQ));
            add(new TestToken(" ", AntlrGrammarLexer.WS));
            add(new TestToken("result.txt", AntlrGrammarLexer.UQ));
            
        }};

        AntlrGrammarParser ap = createParserNoError(tokens);
        CallContext call = ap.call();
        assertThat(call.exception).isNull();
    }

    @Test

    public void callDoesNothingIfEmptyArgLTRedirect(){
        ArrayList<TestToken> tokens = new ArrayList<>(){
            /**
            *
            */
            private static final long serialVersionUID = -112447375692081252L;
            { 
            add(new TestToken("<", AntlrGrammarLexer.LT));
            add(new TestToken(" ", AntlrGrammarLexer.WS));
            add(new TestToken("result.txt", AntlrGrammarLexer.UQ));
        }};

        AntlrGrammarParser ap = createParserNoError(tokens);

        assertThatCode(() -> {
            ap.call();
        }).doesNotThrowAnyException();
    }

    @Test

    public void testCallContxWithEnterExitRules(){

        ArrayList<TestToken> tokens = new ArrayList<>(){

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            {
            add(new TestToken("cat", AntlrGrammarLexer.UQ));
            add(new TestToken(" ", AntlrGrammarLexer.WS));
            add(new TestToken("articles/*", AntlrGrammarLexer.UQ));
            add(new TestToken(" ", AntlrGrammarLexer.WS));
    
        }};

        AntlrGrammarListener agl = createAntlrGrammarListener();
        ParseTreeListener ptl = createParserListener();

        AntlrGrammarParser ap = createParserNoError(tokens);
        CallContext call = ap.call();

        assertThatCode(() -> {

            call.enterRule(agl);
            call.exitRule(agl);

        }).doesNotThrowAnyException();


        assertThatCode(() ->{

            call.enterRule(ptl);
            call.exitRule(ptl);
        }).doesNotThrowAnyException();
    }
}