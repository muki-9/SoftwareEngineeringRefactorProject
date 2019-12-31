package uk.ac.ucl.jsh;

import java.util.ArrayList;

import org.antlr.v4.runtime.tree.ParseTreeListener;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;


import uk.ac.ucl.jsh.AntlrGrammarParser.UnquotedContext;

public class UnquotedContextTest extends ParserT{

    public UnquotedContextTest(){

    }

    @Test

    public void testUnquoted(){

        ArrayList<TestToken> tokens = new ArrayList<>(){
            /**
            *
            */
            private static final long serialVersionUID = -112447375692081252L;

            { 
            add(new TestToken("hello world", AntlrGrammarLexer.UQ));

            
        }};

        AntlrGrammarParser ap = createParserNoError(tokens);
        UnquotedContext uq = ap.unquoted();

        assertThat(uq.UQ().getText()).isEqualTo("hello world");
        assertThat(uq.getRuleIndex()).isNotNull();

    }

    @Test

    public void testUnquotedShouldThrowExceptionIfWrongInput(){

        ArrayList<TestToken> tokens = new ArrayList<>(){
            /**
            *
            */
            private static final long serialVersionUID = -112447375692081252L;

            { 
            add(new TestToken("`hello world`", AntlrGrammarLexer.BQ));

        }};

        AntlrGrammarParser ap = createParserNoError(tokens);
        UnquotedContext uq = ap.unquoted();

        assertThat(uq.exception).isNotNull();
    }

    @Test

    public void testUQContxWithEnterExitRules(){

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
        UnquotedContext uq = ap.unquoted();

        assertThatCode(() -> {

            uq.enterRule(agl);
            uq.exitRule(agl);

        }).doesNotThrowAnyException();


        assertThatCode(() ->{

            uq.enterRule(ptl);
            uq.exitRule(ptl);
        }).doesNotThrowAnyException();

    }



}