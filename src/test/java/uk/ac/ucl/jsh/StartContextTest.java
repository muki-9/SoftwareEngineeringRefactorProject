package uk.ac.ucl.jsh;

import java.util.ArrayList;

import org.antlr.v4.runtime.tree.ParseTreeListener;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

// import org.apache.poi.ss.formula.functions.T;
import uk.ac.ucl.jsh.AntlrGrammarParser.StartContext;


public class StartContextTest<T> extends ParserT {
    
    public StartContextTest(){

        ArrayList<TestToken> tokens = new ArrayList<>(){
            /**
            *
            */
            private static final long serialVersionUID = -112447375692081252L;

            { 
            add(new TestToken("", AntlrGrammarLexer.EOF));

        }};

        AntlrGrammarParser ap = createParserNoError(tokens);
        StartContext uq = ap.start();

        assertThat(uq.exception).isNull();

    }

    @Test

    public void testStartWithEOFShouldNotThrowException(){


    }

    @Test

    public void testStart(){

        ArrayList<TestToken> tokens = new ArrayList<>(){
            /**
            *
            */
            private static final long serialVersionUID = -112447375692081252L;

            { 
            add(new TestToken("hello world", AntlrGrammarLexer.UQ));

            
        }};

        AntlrGrammarParser ap = createParserNoError(tokens);
        StartContext uq = ap.start();

        assertThat(uq.command().getText()).isEqualTo("hello world");
        assertThat(uq.getRuleIndex()).isNotNull();

    }

    @Test

    public void testStartShouldThrowExceptionIfWrongInput(){

        ArrayList<TestToken> tokens = new ArrayList<>(){
            /**
            *
            */
            private static final long serialVersionUID = -112447375692081252L;

            { 
            add(new TestToken(";", AntlrGrammarLexer.SEMICOL));

        }};

        AntlrGrammarParser ap = createParserNoError(tokens);
        StartContext uq = ap.start();

        assertThat(uq.exception).isNotNull();
    }

    @Test

    public void testStartContxWithEnterExitRules(){

        ArrayList<TestToken> tokens = new ArrayList<>(){

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            {

            add(new TestToken("hello world", AntlrGrammarLexer.UQ));


        }};

        AntlrGrammarListener agl = createAntlrGrammarListener();
        ParseTreeListener ptl = createParserListener();
        AntlrGrammarParser ap = createParserNoError(tokens);
        StartContext start = ap.start();

        assertThatCode(() -> {

            start.enterRule(agl);
            start.exitRule(agl);

        }).doesNotThrowAnyException();


        assertThatCode(() ->{

            start.enterRule(ptl);
            start.exitRule(ptl);


        }).doesNotThrowAnyException();

    }
}