package uk.ac.ucl.jsh;

import java.util.ArrayList;

import org.antlr.v4.runtime.tree.ParseTreeListener;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertEquals;

import uk.ac.ucl.jsh.AntlrGrammarParser.SeqContext;

public class SeqContextTest extends ParserT{

    public SeqContextTest(){

    }

    @Test
    public void testSeqWithPipe(){

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
            add(new TestToken(";", AntlrGrammarLexer.SEMICOL));
            add(new TestToken(" ", AntlrGrammarLexer.WS));
            add(new TestToken("head", AntlrGrammarLexer.UQ));
            
        }};

        AntlrGrammarParser ap = createParserNoError(tokens);

        SeqContext seq = ap.seq();

        assertEquals(seq.pipe().getText(), "grep a file.txt | cat ");
        assertEquals(seq.SEMICOL().getText(), ";");
        assertEquals(seq.command().getText(), " head");
        assertEquals(seq.getRuleIndex(), 3);

    }

    @Test

    public void testSeqWithCall(){
        
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
            add(new TestToken(";", AntlrGrammarLexer.SEMICOL));
            add(new TestToken(" ", AntlrGrammarLexer.WS));
            add(new TestToken("cat", AntlrGrammarLexer.UQ));
            add(new TestToken(" ", AntlrGrammarLexer.WS));
            add(new TestToken(";", AntlrGrammarLexer.SEMICOL));
            add(new TestToken(" ", AntlrGrammarLexer.WS));
            add(new TestToken("head", AntlrGrammarLexer.UQ));
            
        }};

        AntlrGrammarParser ap = createParserNoError(tokens);

        SeqContext seq = ap.seq();

        assertEquals(seq.call().getText(), "grep a file.txt ");
    
    }


    @Test

    public void seqShouldThrowErrorOnIncorrectInput(){

        ArrayList<TestToken> tokens = new ArrayList<>(){

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            {
            add(new TestToken("|", AntlrGrammarLexer.PIPEOP));

        }};

        AntlrGrammarParser ap = createParserNoError(tokens);
        SeqContext seq = ap.seq();
        assertThat(seq.exception).isNotNull();
    }

    @Test

    public void testSeqContxWithEnterExitRules(){

        ArrayList<TestToken> tokens = new ArrayList<>(){

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            {

            add(new TestToken("cat", AntlrGrammarLexer.UQ));
            add(new TestToken(" ", AntlrGrammarLexer.WS));
            add(new TestToken("file.txt", AntlrGrammarLexer.UQ));
            add(new TestToken(";", AntlrGrammarLexer.SEMICOL));
            add(new TestToken(" ", AntlrGrammarLexer.WS));
            add(new TestToken("ls", AntlrGrammarLexer.UQ));

        }};

        AntlrGrammarListener agl = createAntlrGrammarListener();
        ParseTreeListener ptl = createParserListener();

        AntlrGrammarParser ap = createParserNoError(tokens);
        SeqContext seq = ap.seq();

        assertThatCode(() -> {

            seq.enterRule(agl);
            seq.exitRule(agl);

        }).doesNotThrowAnyException();


        assertThatCode(() ->{

            seq.enterRule(ptl);
            seq.exitRule(ptl);
        }).doesNotThrowAnyException();

    }

    


}