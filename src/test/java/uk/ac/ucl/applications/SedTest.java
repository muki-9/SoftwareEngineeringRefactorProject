package uk.ac.ucl.applications;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.Scanner;
import static org.assertj.core.api.Assertions.*;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ucl.applications.Sed;
import static org.assertj.core.api.Assertions.assertThat;


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
    public void sedShouldProduceCorrectOutputwithInput() throws IOException {
        
        String originalString = "test line absolute\n2nd line!\nabsent\n"; 
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());
        testArray.add("s/t/b/");

        testSed.exec(testArray, inputStream, out, null);

        Scanner scn = new Scanner(in);
        String line1 = scn.nextLine();
        String line2 = scn.nextLine();
        String line3 = scn.nextLine();

        assertThat(line1).isEqualTo("best line absolute");
        assertThat(line2).isEqualTo("2nd line!");
        assertThat(line3).isEqualTo("absenb");

        scn.close();

    }
    @Test
    public void sedShouldProduceCorrectOutputwithInputAndG() throws IOException {
        
        String originalString = "test line absolute\n2nd line!\nabsent\n"; 
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());
        testArray.add("s/t/b/g");

        testSed.exec(testArray, inputStream, out, null);

        Scanner scn = new Scanner(in);
        String line1 = scn.nextLine();
        String line2 = scn.nextLine();
        String line3 = scn.nextLine();

        assertThat(line1).isEqualTo("besb line absolube");
        assertThat(line2).isEqualTo("2nd line!");
        assertThat(line3).isEqualTo("absenb");

        scn.close();

    }
    //needs to be fixed

    // @Test

    // public void anySymbolCanBeUsedAsDelimeterShouldNotThrowExceptionUnlessInArgs() throws IOException {
    //     String tmp1 = createTempFile();
    //     testArray.add("s$a$b$");

    //     testArray.add(tmp1);

    //     assertThatCode(() ->{
    //         testSed.exec(testArray, null, out, null);
    //     }).doesNotThrowAnyException();
    // }


    @Test

    public void sedShouldProduceCorrectOutputWithoutInput() throws IOException {

        testArray.add("s/L[a-z]+/b/");
        String tmp1 = createTempFile();
        writeToFile(tmp1);
        testArray.add(tmp1);
        
        testSed.exec(testArray, null, out, null);
        Scanner scn = new Scanner(in);
        String line = "";
        for(int i=0; i<3; i++){
            line+=scn.nextLine()+'\n';

        }
        assertThat(line).isEqualTo("b0\nb1\nb2\n");
        scn.close();

    }


    @Test
    public void ifSedArgsis1AndInputNotNullThenNoException(){

        String originalString = "test line absolute\n2nd line!\nabsent\n"; 
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

        testArray.add("s/a/b/");

        assertThatCode(() -> {
            testSed.exec(testArray, inputStream, out, null);
        }).doesNotThrowAnyException();
        
        testArray.add("s/a/b/");
        testArray.add("src");

        assertThatThrownBy(() -> {
            testSed.exec(testArray, inputStream, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("sed: wrong number of arguments");

    }

    @Test
    public void ifSedArgs1OrMoreAndInputNullThenThrowException(){

        testArray.add("s/a/b/");

        assertThatThrownBy(() -> {
            testSed.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("sed: wrong number of arguments");
    }

    @Test
    public void ifRegexEndsWithoutDelimiterOrG() throws IOException {
        testArray.add("s/a/b");
        testArray.add("filename");

        assertThatThrownBy(() -> {
            testSed.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("sed: regex must end in either a delimiter or 'g'");
    }

    @Test
    public void checkRegexInCorrectForm(){

        testArray.add("c/a/b/");
        testArray.add("test.txt");

        assertThatThrownBy(() -> {
            testSed.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("sed: regex in incorrect form");

    }
    // @Test
    // public void testSedWithGGivesCorrectOutput() throws IOException {
    //     String tmp1 = createTempFile();
    //     BufferedWriter wr = new BufferedWriter(new FileWriter(tmp1));

    //     wr.write("This is a test file");
    //     wr.write(System.getProperty("line.separator"));
    //     wr.write("!Should replace some chars?");

    //     ArrayList<String> actual = testSed.sedForAll("a", "b", testArray);
    //     assertThat(actual.get(0)).isEqualTo("This is b test file");
    //     assertThat(actual.get(1)).isEqualTo("!Should replbce some chbrs?");

    // }
    // @Test
    // public void testSedWithoutGGivesCorrectOutput(){

    //     testArray.add("This is a test file but a should still be here");
    //     testArray.add("!Should replace some chars but not a?");
    //     ArrayList<String> actual = testSed.sedFirstInstance("a", "b", testArray);
    //     assertThat(actual.get(0)).isEqualTo("This is b test file but a should still be here");
    //     assertThat(actual.get(1)).isEqualTo("!Should replbce some chars but not a?");

    // }
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

        testArray.add("s/a/b/");
        testArray.add("src");

        assertThatThrownBy(() -> {
            testSed.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("sed: wrong file argument");
    

    }
    // @Test

    // public void sedReturnsCorrectLinesFromFileRead() throws IOException {

    //     String tmp = createTempFile();
    //     ArrayList<String> actual = testSed.getLines(tmp);
    //     assertThat(contentOf(new File(tmp))).isEqualTo(actual.get(0)+'\n'+actual.get(1)+'\n'+actual.get(2)+'\n');

    // }
    // @Test
    // public void outputCorrectResult() throws IOException {
    //     ByteArrayOutputStream stream = new ByteArrayOutputStream();
    //     BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));

    //     testArray.add("this is what is getting outputed");
    //     testArray.add("!@?^");
        
    //     testSed.writeOutput(writer, testArray);
    //     byte[] actualResult = stream.toByteArray();
    //     String expected = "this is what is getting outputed\n!@?^\n";
    //     byte[] expectedResult = expected.getBytes();

    //     assertArrayEquals(actualResult, expectedResult);


    // }

    private String createTempFile() throws IOException{
        File temp1 = File.createTempFile("input", ".txt", new File("/workspaces/jsh-team-44"));
        temp1.deleteOnExit();
        return temp1.getName();
    }
    private void writeToFile(String filename) throws IOException{
        BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
        for(int i =0; i<3; i++){
            bw.write("Line"+ i);
            bw.write(System.getProperty("line.separator"));
        }
        bw.close();
    }
}