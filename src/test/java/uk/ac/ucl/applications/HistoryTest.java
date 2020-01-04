package uk.ac.ucl.applications;

import uk.ac.ucl.jsh.Jsh;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;


public class HistoryTest{

    private Path currDir = Paths.get(Jsh.getCurrentDirectory());

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

        Jsh.setCurrentDirectory("/workspaces/jsh-team-44");
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