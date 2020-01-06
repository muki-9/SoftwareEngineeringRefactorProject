package uk.ac.ucl.jsh;
import org.junit.Test;
import uk.ac.ucl.jsh.TestToken;
import java.util.*;
import static org.assertj.core.api.Assertions.*;

public class ParserTest extends ParserT{

    @Test
    public void getTokenNamesShouldReturnTokens(){
        ArrayList<TestToken> tokens = new ArrayList<>();
        AntlrGrammarParser ap = createParserNoError(tokens);
        assertThat(ap.getTokenNames()).hasSize(13); //includes INVALID token
    }

    @Test

    public void GrammarfileName(){
        ArrayList<TestToken> tokens = new ArrayList<>();
        AntlrGrammarParser ap = createParserNoError(tokens);
        assertThat(ap.getGrammarFileName()).isEqualTo("AntlrGrammar.g4");
    }

    @Test

    public void rulenamesShouldReturnAllRules(){
        ArrayList<TestToken> tokens = new ArrayList<>();
        AntlrGrammarParser ap = createParserNoError(tokens);
        assertThat(ap.getRuleNames()).hasSize(11).contains("start", "command", "pipe").doesNotContainNull();
    }

    @Test

    public void serialisedATNShouldReturn(){
        ArrayList<TestToken> tokens = new ArrayList<>();
        AntlrGrammarParser ap = createParserNoError(tokens);
        assertThat(ap.getSerializedATN()).isNotNull();
    }

}

