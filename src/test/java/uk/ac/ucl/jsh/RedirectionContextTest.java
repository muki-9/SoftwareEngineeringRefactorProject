package uk.ac.ucl.jsh;
import java.util.ArrayList;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import uk.ac.ucl.jsh.AntlrGrammarParser.RedirectionContext;

public class RedirectionContextTest extends ParserT{

    public RedirectionContextTest(){

    }

    @Test

    public void testRedirectionWithNoWhiteSpace(){

        ArrayList<TestToken> tokens = new ArrayList<>(){
            /**
            *
            */
            private static final long serialVersionUID = -112447375692081252L;

            { 
            add(new TestToken("<", AntlrGrammarLexer.LT));
            add(new TestToken("text.txt", AntlrGrammarLexer.UQ));

        }};

        AntlrGrammarParser ap = createParserNoError(tokens);
        RedirectionContext redir = ap.redirection();

        assertThat(redir.LT().getText()).isEqualTo("<");
        assertThat(redir.argument().getText()).isEqualTo("text.txt");
        assertThat(redir.getRuleIndex()).isGreaterThan(0);
        assertThat(redir.exception).isNull();
    }

    @Test

    public void testRedirectionWithWhiteSpace(){

        ArrayList<TestToken> tokens = new ArrayList<>(){
            /**
            *
            */
            private static final long serialVersionUID = -112447375692081252L;

            { 
            add(new TestToken(">", AntlrGrammarLexer.GT));
            add(new TestToken(" ", AntlrGrammarLexer.WS));
            add(new TestToken("text.txt", AntlrGrammarLexer.UQ));

        }};

        AntlrGrammarParser ap = createParserNoError(tokens);
        RedirectionContext redir = ap.redirection();

        assertThat(redir.GT().getText()).isEqualTo(">");
        assertThat(redir.WS().getText()).isEqualTo(" ");
        assertThat(redir.exception).isNull();

    }
    @Test

    public void testRedirectionShouldThrowExceptionIfWrongInput(){

        ArrayList<TestToken> tokens = new ArrayList<>(){
            /**
            *
            */
            private static final long serialVersionUID = -112447375692081252L;

            { 
            add(new TestToken(";", AntlrGrammarLexer.SEMICOL));
            add(new TestToken(" ", AntlrGrammarLexer.WS));
            add(new TestToken("text.txt", AntlrGrammarLexer.UQ));

        }};

        AntlrGrammarParser ap = createParserNoError(tokens);
        RedirectionContext redir = ap.redirection();

        assertThat(redir.exception).isNotNull();

    }

    @Test

    public void testRdirContxWithEnterExitRules(){

        ArrayList<TestToken> tokens = new ArrayList<>(){

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            {

            add(new TestToken(">", AntlrGrammarLexer.GT));
            add(new TestToken(" ", AntlrGrammarLexer.WS));
            add(new TestToken("file.txt", AntlrGrammarLexer.UQ));
    
        }};

        AntlrGrammarListener agl = createAntlrGrammarListener();
        ParseTreeListener ptl = createParserListener();

        AntlrGrammarParser ap = createParserNoError(tokens);
        RedirectionContext rdir = ap.redirection();

        assertThatCode(() -> {

            rdir.enterRule(agl);
            rdir.exitRule(agl);

        }).doesNotThrowAnyException();

        assertThatCode(() ->{

            rdir.enterRule(ptl);
            rdir.exitRule(ptl);
        }).doesNotThrowAnyException();

    }
}