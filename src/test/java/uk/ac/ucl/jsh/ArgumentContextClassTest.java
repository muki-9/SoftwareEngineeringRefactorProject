package uk.ac.ucl.jsh;
import java.util.ArrayList;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import uk.ac.ucl.jsh.AntlrGrammarParser.ArgumentContext;


public class ArgumentContextClassTest extends ParserT{

    public ArgumentContextClassTest(){
    }

    @Test

    public void testArgumentWithUnquoted(){

        ArrayList<TestToken> tokens = new ArrayList<>(){
            /**
            *
            */
            private static final long serialVersionUID = -112447375692081252L;

            { 
            add(new TestToken("hello world", AntlrGrammarLexer.UQ));
            add(new TestToken("Random", AntlrGrammarLexer.UQ));

            
        }};

        AntlrGrammarParser ap = createParserNoError(tokens);
        ArgumentContext ac = ap.argument();

        assertThat(ac.unquoted().get(0).getText()).isEqualTo("hello world");
        assertThat(ac.unquoted(1).getText()).isEqualTo("Random");
        assertThat(ac.getRuleIndex()).isNotNull();


    }
    @Test

    public void testArgumentWithSingleQuoted(){

        ArrayList<TestToken> tokens = new ArrayList<>(){
            /**
            *
            */
            private static final long serialVersionUID = -112447375692081252L;

            { 
            add(new TestToken("\'hello world\'", AntlrGrammarLexer.SQ));
            add(new TestToken("\'Random\'", AntlrGrammarLexer.SQ));

            
        }};

        AntlrGrammarParser ap = createParserNoError(tokens);
        ArgumentContext ac = ap.argument();

        assertThat(ac.singlequoted().get(0).getText()).isEqualTo("\'hello world\'");
        assertThat(ac.singlequoted(1).getText()).isEqualTo("\'Random\'");;

    }

    @Test

    public void testArgumentWithDoubleQuoted(){

        ArrayList<TestToken> tokens = new ArrayList<>(){
            /**
            *
            */
            private static final long serialVersionUID = -112447375692081252L;

            { 
            add(new TestToken("\"", AntlrGrammarLexer.DQ));
            add(new TestToken("hello world", AntlrGrammarLexer.UQ));
            add(new TestToken("\"", AntlrGrammarLexer.DQ));
            add(new TestToken("\"", AntlrGrammarLexer.DQ));
            add(new TestToken("Random", AntlrGrammarLexer.UQ));
            add(new TestToken("\"", AntlrGrammarLexer.DQ));
            
        }};

        AntlrGrammarParser ap = createParserNoError(tokens);
        ArgumentContext ac = ap.argument();

        assertThat(ac.doublequoted().get(0).getText()).isEqualTo("\"hello world\"");
        assertThat(ac.doublequoted(1).getText()).isEqualTo("\"Random\"");



    }
    @Test

    public void testArgumentWithBackQuoted(){

        ArrayList<TestToken> tokens = new ArrayList<>(){
            /**
            *
            */
            private static final long serialVersionUID = -112447375692081252L;

            { 
            add(new TestToken("`hello world`", AntlrGrammarLexer.BQ));
            add(new TestToken("`Random`", AntlrGrammarLexer.BQ));

        }};

        AntlrGrammarParser ap = createParserNoError(tokens);
        ArgumentContext ac = ap.argument();

        assertThat(ac.backquoted().get(0).getText()).isEqualTo("`hello world`");
        assertThat(ac.backquoted(1).getText()).isEqualTo("`Random`");

    }
    @Test


    public void testArgumentShouldThrowExceptionOnWrongInput(){

        ArrayList<TestToken> tokens = new ArrayList<>(){
            /**
            *
            */
            private static final long serialVersionUID = -112447375692081252L;

            { 
            add(new TestToken(";", AntlrGrammarLexer.SEMICOL));
            
        }};

        AntlrGrammarParser ap = createParserNoError(tokens);
        ArgumentContext ac = ap.argument();

        assertThat(ac.exception).isNotNull();

    }

    @Test

    public void testArgumentWithEnterExitRules(){

        ArrayList<TestToken> tokens = new ArrayList<>(){

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            {
            add(new TestToken("cat", AntlrGrammarLexer.UQ));
    
        }};

        AntlrGrammarListener agl = createAntlrGrammarListener();
        ParseTreeListener ptl = createParserListener();

        AntlrGrammarParser ap = createParserNoError(tokens);
        ArgumentContext arg = ap.argument();

        assertThatCode(() -> {

            arg.enterRule(agl);
            arg.exitRule(agl);

        }).doesNotThrowAnyException();


        assertThatCode(() ->{

            arg.enterRule(ptl);
            arg.exitRule(ptl);
        }).doesNotThrowAnyException();

        

    }
}