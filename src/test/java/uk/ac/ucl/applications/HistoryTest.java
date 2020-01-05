package uk.ac.ucl.applications;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.ucl.jsh.Jsh;


public class HistoryTest{

    PipedInputStream in;
    PipedOutputStream out;
    private History testHis;
    ArrayList<String> testArray;
    ByteArrayOutputStream outContent;
    private Jsh jsh;

    @Before
    public void init() throws IOException {

        in = new PipedInputStream();
        out = new PipedOutputStream(in);
        testHis= new History();
        testArray = new ArrayList<>();
        outContent  = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

    }

    @After

    public void tear(){
        Jsh.setCurrentDirectory(Jsh.getHomeDirectory());
    }

    @Test

    public void historyShouldPrintListOfPreviousCommands() throws IOException {        
        jsh = new Jsh();
        jsh.eval("cd src", System.out);
        jsh.eval("history", System.out);
        String actual = outContent.toString();
        assertThat(actual).contains("cd src", "history");
    }

    @Test
    
    public void historyShouldThrowErrorIfAnyArgGiven(){
        testArray.add("src");
        assertThatThrownBy(() ->{
            testHis.exec(testArray, null, out, null);

        }).isInstanceOf(RuntimeException.class)
        .hasMessage("history: wrong number of arguments");
    }

//history will always be a command 
    // @Test

    // public void historyShouldNothrowErrorIfNoCommandsInHistory() throws IOException {
    //     jsh = new Jsh();
    //     jsh.eval("", out);
    //     assertThat(outContent.toString()).isEqualTo("");
    // }
}