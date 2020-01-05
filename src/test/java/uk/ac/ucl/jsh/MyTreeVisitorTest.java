package uk.ac.ucl.jsh;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.from;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import uk.ac.ucl.jsh.AntlrGrammarParser.ArgumentContext;
import uk.ac.ucl.jsh.AntlrGrammarParser.BackquotedContext;
import uk.ac.ucl.jsh.AntlrGrammarParser.CallContext;
import uk.ac.ucl.jsh.AntlrGrammarParser.DoublequotedContext;
import uk.ac.ucl.jsh.AntlrGrammarParser.RedirectionContext;
import uk.ac.ucl.jsh.AntlrGrammarParser.SeqContext;
import uk.ac.ucl.jsh.AntlrGrammarParser.SinglequotedContext;
import uk.ac.ucl.jsh.AntlrGrammarParser.UnquotedContext;

public class MyTreeVisitorTest extends ParserT{

    public MyTreeVisitorTest(){

    }

    @Rule
    public TemporaryFolder folder  = new TemporaryFolder(new File(Jsh.getHomeDirectory()));

    @AfterClass
    public static void tear(){
        File f = new File("randomfile.txt");
        f.delete();
    }

    @Test

    public void testRedirectionWithLT(){

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
        MyTreeVisitor tree = new MyTreeVisitor();
        Call c = (Call) tree.visitRedirection(redir);
        assertThat(c).returns("text.txt", from(Call::getCurrArgs));
        assertThat(c).returns("<", from(Call::getSymbol));

    }

    @Test

    public void testRedirectionWithGT(){

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

        MyTreeVisitor tree = new MyTreeVisitor();
        Call c = (Call) tree.visitRedirection(redir);
        assertThat(c).returns("text.txt", from(Call::getCurrArgs));
        assertThat(c).returns(">", from(Call::getSymbol));
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
        MyTreeVisitor tree= new MyTreeVisitor();

        assertThatThrownBy(() ->{

         tree.visitRedirection(redir);
        }).isInstanceOf(RuntimeException.class)
        .hasMessageContaining("antlr: invalid redirection arguments");

    }

    @Test

    public void testRedirectionShouldThrowExceptionIfTooManyInputs(){

        ArrayList<TestToken> tokens = new ArrayList<>(){
            /**
            *
            */
            private static final long serialVersionUID = -112447375692081252L;

            { 
            add(new TestToken("<", AntlrGrammarLexer.SEMICOL));
            add(new TestToken(" ", AntlrGrammarLexer.WS));
            add(new TestToken("text.txt", AntlrGrammarLexer.UQ));
            add(new TestToken("|", AntlrGrammarLexer.PIPEOP));

        }};

        AntlrGrammarParser ap = createParserNoError(tokens);
        RedirectionContext redir = ap.redirection();
        MyTreeVisitor tree= new MyTreeVisitor();

        assertThatThrownBy(() ->{

         tree.visitRedirection(redir);
        }).isInstanceOf(RuntimeException.class)
        .hasMessageContaining("antlr: too many arguments given to IO redirection symbol");


    }

    @Test

    public void testBackQuote(){

        ArrayList<TestToken> tokens = new ArrayList<>(){
            /**
            *
            */
            private static final long serialVersionUID = -112447375692081252L;

            { 
            add(new TestToken("`echo hello world`", AntlrGrammarLexer.BQ));

            
        }};

        AntlrGrammarParser ap = createParserNoError(tokens);
        BackquotedContext bq = ap.backquoted();

        MyTreeVisitor tree= new MyTreeVisitor();
        Call c = (Call) tree.visitBackquoted(bq);
        ArrayList<String> expected = new ArrayList<>();
        expected.add("hello");
        expected.add("world");
        assertThat(c).returns(expected, from(Call::getBqArray));

    }

    // @Test

    // public void testBackquotedShouldThrowExceptionIfWrongInput(){

    //     ArrayList<TestToken> tokens = new ArrayList<>(){
    //         /**
    //         *
    //         */
    //         private static final long serialVersionUID = -112447375692081252L;

    //         { 
    //         add(new TestToken("hello world", AntlrGrammarLexer.UQ));

    //     }};

    //     AntlrGrammarParser ap = createParserNoError(tokens);
    //     BackquotedContext bq = ap.backquoted();

    // }
    @Test

    public void testDQ(){

        ArrayList<TestToken> tokens = new ArrayList<>(){

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            {

            add(new TestToken("\"", AntlrGrammarLexer.DQ));
            add(new TestToken("this is space:", AntlrGrammarLexer.T__0));
            add(new TestToken("`echo \"test\"`", AntlrGrammarLexer.BQ));
            add(new TestToken("\"", AntlrGrammarLexer.DQ));
            
    
        }};

        AntlrGrammarParser ap = createParserNoError(tokens);
        DoublequotedContext dq = ap.doublequoted();

        MyTreeVisitor tree= new MyTreeVisitor();
        Call c = (Call) tree.visitDoublequoted(dq);
        assertThat(c).returns("this is space:test", from(Call::getCurrArgs));

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

        MyTreeVisitor tree= new MyTreeVisitor();
        Call c = (Call) tree.visitUnquoted(uq);
        assertThat(c).returns("hello world", from(Call::getCurrArgs));
        assertThat(c).returns(true, from(Call::getGlobb));


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

        MyTreeVisitor tree= new MyTreeVisitor();
        Call c = (Call) tree.visitSinglequoted(sq);
        assertThat(c).returns("hello world", from(Call::getCurrArgs));
        assertThat(c).returns(false, from(Call::getGlobb));

      
    }

    @Test

    public void testArgumentWithUnquoted(){

        ArrayList<TestToken> tokens = new ArrayList<>(){
            /**
            *
            */
            private static final long serialVersionUID = -112447375692081252L;

            { 
            add(new TestToken("echo ", AntlrGrammarLexer.UQ));
            add(new TestToken("hello", AntlrGrammarLexer.UQ));

            
        }};

        AntlrGrammarParser ap = createParserNoError(tokens);
        ArgumentContext ac = ap.argument();

        MyTreeVisitor tree= new MyTreeVisitor();
        Call c = (Call) tree.visitArgument(ac);
        assertThat(c).returns("echo hello", from(Call::getCurrArgs));


    }
    @Test

    public void testArgumentWithBackquoteAndUnquoted(){
        ArrayList<TestToken> tokens = new ArrayList<>(){
            /**
            *
            */
            private static final long serialVersionUID = -112447375692081252L;
            {
                add(new TestToken("wc", AntlrGrammarLexer.UQ));
                add(new TestToken("-l", AntlrGrammarLexer.UQ));
                add(new TestToken("`find -name \'*.java\'`", AntlrGrammarLexer.BQ));
        }};

        AntlrGrammarParser ap = createParserNoError(tokens);
        ArgumentContext ac = ap.argument();

        MyTreeVisitor tree= new MyTreeVisitor();
        Call c = (Call) tree.visitArgument(ac);
        assertThat(c).returns(true, from(Call::getGlobb));
    }

    @Test

    public void testArgumentWithBackQuoted(){

        ArrayList<TestToken> tokens = new ArrayList<>(){
            /**
            *
            */
            private static final long serialVersionUID = -112447375692081252L;

            { 
            add(new TestToken("`echo hello world`", AntlrGrammarLexer.BQ));

        }};

        AntlrGrammarParser ap = createParserNoError(tokens);
        ArgumentContext ac = ap.argument();

        MyTreeVisitor tree= new MyTreeVisitor();
        Call c = (Call) tree.visitArgument(ac);
        ArrayList<String> expected = new ArrayList<>();
        expected.add("hello");
        expected.add("world");
        assertThat(c).returns(expected, from(Call::getBqArray));

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
        MyTreeVisitor tree= new MyTreeVisitor();
        Seq s = (Seq) tree.visitSeq(seq);
        assertNotNull(s);

    }

    // @Test

    // public void testCallWithRedir() throws IOException {

    //     String file1 = createTempFile();


    //     ArrayList<TestToken> tokens = new ArrayList<>(){
    //         /**
    //         *
    //         */
    //         private static final long serialVersionUID = -112447375692081252L;

    //         {
    //         add(new TestToken("grep", AntlrGrammarLexer.UQ));
    //         add(new TestToken(" ", AntlrGrammarLexer.WS));
    //         add(new TestToken("\"Interesting string\"", AntlrGrammarLexer.UQ));
    //         add(new TestToken(" ", AntlrGrammarLexer.WS));
    //         add(new TestToken("<", AntlrGrammarLexer.LT));
    //         add(new TestToken(" ", AntlrGrammarLexer.WS));
    //         add(new TestToken(file1, AntlrGrammarLexer.UQ));
    //         add(new TestToken(" ", AntlrGrammarLexer.WS));
    //         add(new TestToken(">", AntlrGrammarLexer.GT));
    //         add(new TestToken(" ", AntlrGrammarLexer.WS));
    //         add(new TestToken("newfile.txt", AntlrGrammarLexer.UQ));
            
    //     }};

    //     AntlrGrammarParser ap = createParserNoError(tokens);
    //     CallContext call = ap.call();
    //     MyTreeVisitor tree= new MyTreeVisitor();
    //     Call c = (Call) tree.visitCall(call);
    //     ArrayList<String> exp = new ArrayList<>();
    //     exp.add("\"Interesting string\"");
    //     assertThat(c).returns("grep", Call::getApplication);
    //     assertThat(c).returns(exp, Call::getArguments);
        
        
    // }

    //bug doesnt work as of now

    // @Test

    // public void callTakingRedirFromStartCreatesFile(){

    //     ArrayList<TestToken> tokens = new ArrayList<>(){
    //         /**
    //         *
    //         */
    //         private static final long serialVersionUID = -112447375692081252L;

    //         {
    //         add(new TestToken(">", AntlrGrammarLexer.GT));
    //         add(new TestToken(" ", AntlrGrammarLexer.WS));
    //         add(new TestToken("result.txt", AntlrGrammarLexer.UQ));
    //         add(new TestToken(" ", AntlrGrammarLexer.WS));

            
    //     }};

    //     AntlrGrammarParser ap = createParserNoError(tokens);
    //     CallContext call = ap.call();
    //     MyTreeVisitor tree= new MyTreeVisitor();
     

    //     assertThatCode(() ->{
    //         tree.visitCall(call);
    //     }).doesNotThrowAnyException();
    // }

    @Test

    public void testGTRedir(){


        ArrayList<TestToken> tokens = new ArrayList<>(){
            /**
            *
            */
            private static final long serialVersionUID = -112447375692081252L;

            {
            add(new TestToken("echo", AntlrGrammarLexer.UQ));
            add(new TestToken(" ", AntlrGrammarLexer.WS));
            add(new TestToken("foo", AntlrGrammarLexer.UQ));
            add(new TestToken(" ", AntlrGrammarLexer.WS));
            add(new TestToken(">", AntlrGrammarLexer.GT));
            add(new TestToken(" ", AntlrGrammarLexer.WS));
            add(new TestToken("randomfile.txt", AntlrGrammarLexer.UQ));
            add(new TestToken(" ", AntlrGrammarLexer.WS));
        }};

        AntlrGrammarParser ap = createParserNoError(tokens);
        CallContext call = ap.call();
        MyTreeVisitor tree= new MyTreeVisitor();

        assertThatCode(() ->{
            tree.visitCall(call);
        }).doesNotThrowAnyException();
    }

    @Test

    public void testCallWithBackQuotes(){

          ArrayList<TestToken> tokens = new ArrayList<>(){
            /**
            *
            */
            private static final long serialVersionUID = -112447375692081252L;

            {   add(new TestToken("echo", AntlrGrammarLexer.UQ));
                add(new TestToken(" ", AntlrGrammarLexer.WS));
                add(new TestToken("`echo hello world`", AntlrGrammarLexer.BQ));

        }};

        AntlrGrammarParser ap = createParserNoError(tokens);
        CallContext call = ap.call();

        MyTreeVisitor tree= new MyTreeVisitor();
        Call c = (Call) tree.visitCall(call);
        ArrayList<String> expected = new ArrayList<>();
        expected.add("hello");
        expected.add("world");
        assertThat(c).returns(expected, from(Call::getArguments));
    
    }

    @Test

    public void testCallWithRedirAndBackQ() throws IOException {

        File file = folder.newFile();

        ArrayList<TestToken> tokens = new ArrayList<>(){
            /**
            *
            */
            private static final long serialVersionUID = -112447375692081252L;

            {
                add(new TestToken("grep", AntlrGrammarLexer.UQ));
                add(new TestToken(" ", AntlrGrammarLexer.WS));
                add(new TestToken("`echo \"Interesting string\"`", AntlrGrammarLexer.BQ));
                add(new TestToken(" ", AntlrGrammarLexer.WS));
                add(new TestToken("<", AntlrGrammarLexer.LT));
                add(new TestToken(" ", AntlrGrammarLexer.WS));
                add(new TestToken(file.getName(), AntlrGrammarLexer.UQ));
        }};

        Jsh.setCurrentDirectory(folder.getRoot().toString());

        AntlrGrammarParser ap = createParserNoError(tokens);
        CallContext call = ap.call();

        MyTreeVisitor tree= new MyTreeVisitor();
        Call c = (Call) tree.visitCall(call);

        ArrayList<String> exp = new ArrayList<>();
        exp.add("Interesting");
        exp.add("string");
        assertThat(c).returns("grep", Call::getApplication);
        assertThat(c).returns(exp, Call::getArguments);

    }

    @Test

    public void callShouldThrowErrorIfFileNotPresentForReadLT(){

        ArrayList<TestToken> tokens = new ArrayList<>(){
            /**
            *
            */
            private static final long serialVersionUID = -112447375692081252L;

            {

                add(new TestToken("<", AntlrGrammarLexer.LT));
                add(new TestToken(" ", AntlrGrammarLexer.WS));
                add(new TestToken("randomfile.txt", AntlrGrammarLexer.UQ));
                
        }};

        AntlrGrammarParser ap = createParserNoError(tokens);
        CallContext call = ap.call();

        MyTreeVisitor tree= new MyTreeVisitor();

        assertThatThrownBy(() ->{
            tree.visitCall(call);
        }).isInstanceOf(RuntimeException.class)
        .hasMessage("redirection: file could not be found");



    }

    private String createTempFile() throws IOException{
        File temp1 = File.createTempFile("input", ".txt", new File("/workspaces/jsh-team-44"));
        temp1.deleteOnExit();
        writeToFile(temp1.getName());
        return temp1.getName();
    }

    private void writeToFile(String filename) throws IOException{
        BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
        for(int i =0; i<2; i++){
            bw.write("Line"+ i);
            bw.write(System.getProperty("line.separator"));
        }
        bw.close();
    }



}