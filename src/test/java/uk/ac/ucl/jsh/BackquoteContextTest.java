package uk.ac.ucl.jsh;
import java.util.ArrayList;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import uk.ac.ucl.jsh.AntlrGrammarParser.BackquotedContext;

public class BackquoteContextTest extends ParserT{

    public BackquoteContextTest(){

    }

    @Test

    public void testBackQuote(){

        ArrayList<TestToken> tokens = new ArrayList<>(){
            /**
            *
            */
            private static final long serialVersionUID = -112447375692081252L;

            { 
            add(new TestToken("`hello world`", AntlrGrammarLexer.BQ));

            
        }};

        AntlrGrammarParser ap = createParserNoError(tokens);
        BackquotedContext bq = ap.backquoted();

        assertThat(bq.BQ().getText()).isEqualTo("`hello world`");
        assertThat(bq.getRuleIndex()).isNotNull();


    }

    @Test

    public void testBackquotedShouldThrowExceptionIfWrongInput(){

        ArrayList<TestToken> tokens = new ArrayList<>(){
            /**
            *
            */
            private static final long serialVersionUID = -112447375692081252L;

            { 
            add(new TestToken("hello world", AntlrGrammarLexer.UQ));

        }};

        AntlrGrammarParser ap = createParserNoError(tokens);
        BackquotedContext bq = ap.backquoted();

        assertThat(bq.exception).isNotNull();
    }

    @Test

    public void testBackquoteWithEnterExitRules(){

        ArrayList<TestToken> tokens = new ArrayList<>(){

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            {
            add(new TestToken("`hello world`", AntlrGrammarLexer.BQ));
    
        }};

        AntlrGrammarListener agl = createAntlrGrammarListener();
        ParseTreeListener ptl = createParserListener();

        AntlrGrammarParser ap = createParserNoError(tokens);
        BackquotedContext bq = ap.backquoted();

        assertThatCode(() -> {

            bq.enterRule(agl);
            bq.exitRule(agl);

        }).doesNotThrowAnyException();


        assertThatCode(() ->{

            bq.enterRule(ptl);
            bq.exitRule(ptl);
        }).doesNotThrowAnyException();

        

    }



}