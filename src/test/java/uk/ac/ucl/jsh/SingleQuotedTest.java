package uk.ac.ucl.jsh;
import java.util.ArrayList;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import uk.ac.ucl.jsh.AntlrGrammarParser.SinglequotedContext;

public class SingleQuotedTest extends ParserT{

    public SingleQuotedTest(){

    }

    @Test

    public void testSingleQuote(){

        ArrayList<TestToken> tokens = new ArrayList<>(){

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            {

            add(new TestToken("\'hello world\'", AntlrGrammarLexer.SQ));
    
        }};

        AntlrGrammarParser ap = createParserNoError(tokens);

        SinglequotedContext sq = ap.singlequoted();

        assertThat(sq.SQ()).hasToString("\'hello world\'");
        assertThat(sq.getRuleIndex()).isNotNull();
      
    }

    @Test

    public void testSQWithWrongInputShouldThrowException(){


        ArrayList<TestToken> tokens = new ArrayList<>(){

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            {

            add(new TestToken("`hello world`", AntlrGrammarLexer.BQ));

        }};

        AntlrGrammarParser ap = createParserNoError(tokens);
        SinglequotedContext sq = ap.singlequoted();
        assertThat(sq.exception).isNotNull();

    }

    @Test

    public void testSQContxWithEnterExitRules(){

        ArrayList<TestToken> tokens = new ArrayList<>(){

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            {

            
            add(new TestToken("\'hello world\'", AntlrGrammarLexer.SQ));

        }};

        AntlrGrammarListener agl = createAntlrGrammarListener();
        ParseTreeListener ptl = createParserListener();

        AntlrGrammarParser ap = createParserNoError(tokens);
        SinglequotedContext sq = ap.singlequoted();

        assertThatCode(() -> {

            sq.enterRule(agl);
            sq.exitRule(agl);

        }).doesNotThrowAnyException();


        assertThatCode(() ->{

            sq.enterRule(ptl);
            sq.exitRule(ptl);
        }).doesNotThrowAnyException();

    }



}