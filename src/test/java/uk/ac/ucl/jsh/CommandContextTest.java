package uk.ac.ucl.jsh;
import java.util.ArrayList;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import uk.ac.ucl.jsh.AntlrGrammarParser.CommandContext;

public class CommandContextTest extends ParserT{

    public CommandContextTest(){

    }

    @Test

    public void testCommandWithPipe(){

        ArrayList<TestToken> tokens = new ArrayList<>(){

            /**
             *
             */
            private static final long serialVersionUID = 1L;

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
            add(new TestToken("\n", AntlrGrammarLexer.NL));


        }};

        AntlrGrammarParser ap = createParserNoError(tokens);

        CommandContext command = ap.command();

        assertThat(command.getText()).isEqualTo("grep a file.txt | cat");
        assertThat(command.pipe().getChild(0).getChild(0).getText()).isEqualTo("grep");
        assertThat(command.getRuleIndex()).isEqualTo(1);


    }


    @Test

    public void testCommandWithSeq(){

        ArrayList<TestToken> tokens = new ArrayList<>(){

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            {
            add(new TestToken("cd", AntlrGrammarLexer.UQ));
            add(new TestToken(" ", AntlrGrammarLexer.WS));
            add(new TestToken("src", AntlrGrammarLexer.UQ));
            add(new TestToken(" ", AntlrGrammarLexer.WS));
            add(new TestToken(";", AntlrGrammarLexer.SEMICOL));
            add(new TestToken(" ", AntlrGrammarLexer.WS));
            add(new TestToken("cat", AntlrGrammarLexer.UQ));
            add(new TestToken(" ", AntlrGrammarLexer.WS));
            add(new TestToken("file.txt", AntlrGrammarLexer.UQ));

        }};

        AntlrGrammarParser ap = createParserNoError(tokens);

        CommandContext command = ap.command();

        assertThat(command.seq().getChild(1).getText()).isEqualTo(";");
        assertThatThrownBy(() -> {
            command.call().getRuleContext();
        }).isInstanceOf(NullPointerException.class);


    }

    @Test

    public void testCommandWithNL(){

        ArrayList<TestToken> tokens = new ArrayList<>(){

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            {
            add(new TestToken("\n", AntlrGrammarLexer.NL));


        }};

        AntlrGrammarParser ap = createParserNoError(tokens);

        CommandContext command = ap.command();

        assertThat(command.NL()).isNotNull();


    }

    @Test

    public void testCommandShouldThrowExceptionIfWrongInput(){


        ArrayList<TestToken> tokens = new ArrayList<>(){

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            {
            add(new TestToken(";", AntlrGrammarLexer.SEMICOL));


        }};

        AntlrGrammarParser ap = createParserNoError(tokens);

        CommandContext command = ap.command();

        assertThat(command.exception).isNotNull();

    }
    @Test

    public void testCommContxWithEnterExitRules(){

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
        CommandContext command = ap.command();

        assertThatCode(() -> {

            command.enterRule(agl);
            command.exitRule(agl);

        }).doesNotThrowAnyException();


        assertThatCode(() ->{

            command.enterRule(ptl);
            command.exitRule(ptl);
        }).doesNotThrowAnyException();

        

    }






}