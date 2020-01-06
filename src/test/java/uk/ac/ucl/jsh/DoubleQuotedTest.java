package uk.ac.ucl.jsh;
import java.util.ArrayList;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import uk.ac.ucl.jsh.AntlrGrammarParser.DoublequotedContext;

public class DoubleQuotedTest extends ParserT{

    public DoubleQuotedTest(){

    }

    @Test

    public void testDQ(){

        ArrayList<TestToken> tokens = new ArrayList<>(){
            private static final long serialVersionUID = 1L;

            {

            add(new TestToken("\"", AntlrGrammarLexer.DQ));
            add(new TestToken("this is space:", AntlrGrammarLexer.UQ));
            add(new TestToken("`echo \"\"`", AntlrGrammarLexer.BQ));
            add(new TestToken("\"", AntlrGrammarLexer.DQ));
            
    
        }};

        AntlrGrammarParser ap = createParserNoError(tokens);

        DoublequotedContext dq = ap.doublequoted();

        assertThat(dq.backquoted(0).getText()).isEqualTo("`echo \"\"`");
        assertThat(dq.getRuleIndex()).isNotNull();
        assertThat(dq.backquoted().get(0).getText()).isEqualTo("`echo \"\"`");
        assertThat(dq.DQ().get(0)).hasToString("\"");
        assertThat(dq.DQ(0)).hasToString("\"");
        assertThat(dq.NL()).isEmpty();
        assertThat(dq.NL(0)).isNull();
    }

    @Test

    public void testDQShouldThrowException(){


        ArrayList<TestToken> tokens = new ArrayList<>(){
            private static final long serialVersionUID = 1L;

            {

            add(new TestToken("this is test:", AntlrGrammarLexer.DQ));

        }};

        AntlrGrammarParser ap = createParserNoError(tokens);

        DoublequotedContext dq = ap.doublequoted();
        assertThat(dq.exception).isNotNull();


    }
    @Test

    public void testDQContxWithEnterExitRules(){

        ArrayList<TestToken> tokens = new ArrayList<>(){
            private static final long serialVersionUID = 1L;

            {
            add(new TestToken("\"", AntlrGrammarLexer.DQ));
            add(new TestToken("hello world", AntlrGrammarLexer.UQ));
            add(new TestToken("\"", AntlrGrammarLexer.DQ));
    
        }};

        AntlrGrammarListener agl = createAntlrGrammarListener();
        ParseTreeListener ptl = createParserListener();

        AntlrGrammarParser ap = createParserNoError(tokens);
        DoublequotedContext dq = ap.doublequoted();

        assertThatCode(() -> {

            dq.enterRule(agl);
            dq.exitRule(agl);

        }).doesNotThrowAnyException();


        assertThatCode(() ->{

            dq.enterRule(ptl);
            dq.exitRule(ptl);
        }).doesNotThrowAnyException();

        

    }



}