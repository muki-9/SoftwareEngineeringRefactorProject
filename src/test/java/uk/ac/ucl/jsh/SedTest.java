package uk.ac.ucl.jsh;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.Scanner;
import static org.assertj.core.api.Assertions.*;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ucl.applications.Sed;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.contentOf;


public class SedTest{
    public SedTest(){

    }
    PipedInputStream in;
    PipedOutputStream out;
    Sed testSed;
    ArrayList<String> testArray;

    @Before
    public void init() throws IOException {

        in = new PipedInputStream();
        out = new PipedOutputStream(in);
        testSed = new Sed();
        testArray = new ArrayList<>();

    }

    @Test
    public void ifSedArgsis1AndInputNotNullThenNoException(){
        testArray.add("s/a/b");

        assertThatCode(() -> {
            testSed.validateArgs(testArray, in);
        }).doesNotThrowAnyException();

    }

    @Test
    public void ifSedArgs1AndInputNullThenThrowException(){

        testArray.add("s/a/b");

        assertThatThrownBy(() -> {
            testSed.exec(testArray, null, out);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("sed: wrong number of arguments");

    }
    @Test
    public void checkRegexInCorrectForm(){

        testArray.add("c/a/b");
        testArray.add("test.txt");

        assertThatThrownBy(() -> {
            testSed.exec(testArray, null, out);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("sed: regex in incorrect form");

    }
    @Test
    public void testSedWithGGivesCorrectOutput(){

        testArray.add("This is a test file");
        testArray.add("!Should replace some chars?");
        ArrayList<String> actual = testSed.sedForAll("a", "b", testArray);
        assertThat(actual.get(0)).isEqualTo("This is b test file");
        assertThat(actual.get(1)).isEqualTo("!Should replbce some chbrs?");

    }
    @Test
    public void testSedWithoutGGivesCorrectOutput(){

        testArray.add("This is a test file but a should still be here");
        testArray.add("!Should replace some chars but not a?");
        ArrayList<String> actual = testSed.sedFirstInstance("a", "b", testArray);
        assertThat(actual.get(0)).isEqualTo("This is b test file but a should still be here");
        assertThat(actual.get(1)).isEqualTo("!Should replbce some chars but not a?");

    }
    @Test
    public void sedThrowsExceptionIfFileCantOpen(){

        assertThatThrownBy(() -> {
            testSed.getLines("doesntexist.txt");
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("sed: cannot open doesntexist.txt");
        


    }

    @Test
    public void sedThrowsExceptionIfWrongFile(){

        testArray.add("s/a/b");
        testArray.add("doesntexist.txt");

        assertThatThrownBy(() -> {
            testSed.exec(testArray, null, out);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("sed: wrong file argument");
    

    }
    @Test

    public void sedReturnsCorrectLinesFromFileRead() throws IOException {

        ArrayList<String> actual = testSed.getLines("test.txt");
        assertThat(contentOf(new File("test.txt"))).isEqualTo(actual.get(0)+'\n'+actual.get(1)+'\n'+actual.get(2));

    }
    @Test
    public void outputCorrectResult() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));

        testArray.add("this is what is getting outputed");
        testArray.add("!@?^");
        
        testSed.writeOutput(writer, testArray);
        byte[] actualResult = stream.toByteArray();
        String expected = "this is what is getting outputed\n!@?^\n";
        byte[] expectedResult = expected.getBytes();

        assertArrayEquals(actualResult, expectedResult);


    }





    

    


}