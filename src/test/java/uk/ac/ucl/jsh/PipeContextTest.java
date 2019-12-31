package uk.ac.ucl.jsh;

import java.util.ArrayList;

import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertEquals;

import uk.ac.ucl.jsh.AntlrGrammarParser.PipeContext;

public class PipeContextTest extends ParserT{

    public PipeContextTest(){

    }

    @Test

    public void testPipe(){

        ArrayList<TestToken> tokens = new ArrayList<>(){
            /**
            *
            */
            private static final long serialVersionUID = -112447375692081252L;

            {
            add(new TestToken("grep", AntlrGrammarLexer.UQ));
            add(new TestToken(" ", AntlrGrammarLexer.WS));
            add(new TestToken("a", AntlrGrammarLexer.UQ));
            add(new TestToken(" ", AntlrGrammarLexer.WS));
            add(new TestToken("file.txt", AntlrGrammarLexer.UQ));
            add(new TestToken(" ", AntlrGrammarLexer.WS));
            add(new TestToken("|", AntlrGrammarLexer.PIPEOP));
            add(new TestToken(" ", AntlrGrammarLexer.WS));
            add(new TestToken("cat", AntlrGrammarLexer.UQ));
            
        }};

        AntlrGrammarParser ap = createParserNoError(tokens);

        PipeContext pipe = ap.pipe();

        assertEquals(pipe.getChild(1).getText(), "|");
        assertEquals(pipe.call(0).getText(), "grep a file.txt ");
        assertEquals(pipe.call().get(1).getText(), " cat");
        assertEquals(pipe.PIPEOP().getText(), "|");
        assertThatThrownBy(() -> {
            pipe.pipe().getRuleContext();
        }).isInstanceOf(NullPointerException.class);
        assertEquals(pipe.getRuleIndex(), 2);
    }

    @Test

    public void testPipe2(){

        ArrayList<TestToken> tokens = new ArrayList<>(){
            /**
            *
            */
            private static final long serialVersionUID = -112447375692081252L;

            {
            add(new TestToken("grep", AntlrGrammarLexer.UQ));
            add(new TestToken(" ", AntlrGrammarLexer.WS));
            add(new TestToken("a", AntlrGrammarLexer.UQ));
            add(new TestToken(" ", AntlrGrammarLexer.WS));
            add(new TestToken("file.txt", AntlrGrammarLexer.UQ));
            add(new TestToken(" ", AntlrGrammarLexer.WS));
            add(new TestToken("|", AntlrGrammarLexer.PIPEOP));
            add(new TestToken(" ", AntlrGrammarLexer.WS));
            add(new TestToken("cat", AntlrGrammarLexer.UQ));
            add(new TestToken(" ", AntlrGrammarLexer.WS));
            add(new TestToken("|", AntlrGrammarLexer.PIPEOP));
            add(new TestToken(" ", AntlrGrammarLexer.WS));
            add(new TestToken("head", AntlrGrammarLexer.UQ));
            
        }};

        AntlrGrammarParser ap = createParserNoError(tokens);

        PipeContext pipe = ap.pipe();

        assertEquals(pipe.getChild(0).getText(), "grep a file.txt | cat ");
        assertEquals(pipe.getChild(1).getText(), "|");
        assertEquals(pipe.getChild(2).getText(), " head");

    }

    @Test

    public void pipeshouldThrowError(){

        ArrayList<TestToken> tokens = new ArrayList<>(){

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            {
            add(new TestToken(";", AntlrGrammarLexer.SEMICOL));

        }};

        AntlrGrammarParser ap = createParserNoError(tokens);
        assertThat(ap.pipe().exception).isNotNull();
    }

    @Test

    public void testPipeContxWithEnterExitRules(){

        ArrayList<TestToken> tokens = new ArrayList<>(){

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            {
            add(new TestToken("\"", AntlrGrammarLexer.DQ));
            add(new TestToken("hello world", AntlrGrammarLexer.UQ));
            add(new TestToken("\"", AntlrGrammarLexer.DQ));
    
        }};

        AntlrGrammarListener agl = createAntlrGrammarListener();
        ParseTreeListener ptl = createParserListener();

        AntlrGrammarParser ap = createParserNoError(tokens);
        PipeContext pipe = ap.pipe();

        assertThatCode(() -> {

            pipe.enterRule(agl);
            pipe.exitRule(agl);

        }).doesNotThrowAnyException();


        assertThatCode(() ->{

            pipe.enterRule(ptl);
            pipe.exitRule(ptl);
        }).doesNotThrowAnyException();

    }

}
