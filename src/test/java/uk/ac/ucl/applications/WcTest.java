package uk.ac.ucl.applications;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import uk.ac.ucl.jsh.Jsh;


public class WcTest{


    public WcTest(){

    }

    PipedInputStream in;
    PipedOutputStream out;
    Wc testWc;
    ArrayList<String> testArray;
    ByteArrayOutputStream outContent;

    @Before 
    public void init() throws IOException {
        in = new PipedInputStream();
        out = new PipedOutputStream(in);
        testWc = new Wc();
        testArray = new ArrayList<>();
        outContent  = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        Jsh.setCurrentDirectory(folder.getRoot().toString());

    }

    @Rule
    public TemporaryFolder folder  = new TemporaryFolder();

    @After
    public void tear() throws IOException {
        in.close();
        out.close();
        testWc = null;
        Jsh.setCurrentDirectory(Jsh.getHomeDirectory());
    }

    @Test
    public void wcShouldThrowExceptionifNoArgsAndNoInputGiven(){

        assertThatThrownBy(() -> {
            testWc.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("wc: wrong number of arguments");
    }

    @Test
    public void wcShouldThrowExceptionIfDirGivenAsInput() throws IOException {
        File file = folder.newFolder();
        testArray.add(file.getName());
        Jsh.setCurrentDirectory(folder.getRoot().toString());
        assertThatThrownBy(() -> {
            testWc.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("wc: wrong file argument");
    } 

    @Test
    public void wcShouldNotThrowExceptionIfInputGivenAndNoArgs(){
        String originalString = "test line absolute\n2nd line!\nabsent";
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

        assertThatCode(() -> {
            testWc.exec(testArray, inputStream, out, null);
        }).doesNotThrowAnyException();

    }

    @Test
    public void wcShouldThrowExcpetionifOneArgWithOptionsAndNoInput(){
        testArray.add("-m");

        assertThatThrownBy(() -> {
            testWc.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("wc: wrong number of arguments");
    }

    @Test

    public void testOutputWithNoArgsButInput() throws IOException {

        String originalString = "test line absolute\n2nd line!\nabsent"; //if only print new lie counts then 
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());
        int byteNo = originalString.getBytes().length +1;
        int wordNo = 6;
        int linesNo = 3;
        testWc.exec(testArray, inputStream, System.out, null);
        String[] output = outContent.toString().split("\\s");
        int linesAct = Integer.parseInt(output[0]);
        int wordAct = Integer.parseInt(output[1]);
        int byteAct = Integer.parseInt(output[2]);

        assertThat(byteAct).isEqualTo(byteNo);
        assertThat(wordAct).isEqualTo(wordNo);
        assertThat(linesAct).isEqualTo(linesNo);

    }

    @Test

    public void testOutputWithOneArgAndInputM() throws IOException {
        int byteNo = 36;
        testArray.add("-m");
        int result = optionResult(testArray);
        assertThat(result).isEqualTo(byteNo);
    }

    @Test
    public void testOutputWithOneArgAndInputL() throws IOException {
        int linesNo = 3;
        testArray.add("-l");
        int result = optionResult(testArray);
        assertThat(result).isEqualTo(linesNo);
    }

    @Test
    public void testOutputWithOneArgAndInputW() throws IOException {
        int wordNo = 6;
        testArray.add("-w");
        int result = optionResult(testArray);
        assertThat(result).isEqualTo(wordNo);
    }

    @Test
    public void testWcFromFileM() throws IOException {
        int bytesNo = "Line 1\nLine 2\nLine 3\n".getBytes().length;


        File tmp1 = folder.newFile();
        writeToFile(tmp1);
        testArray.add("-m");
        testArray.add(tmp1.getName());

        testWc.exec(testArray, null, System.out, null);

        String split = outContent.toString().split("\\s")[0];

        assertThat(split).isEqualTo(Integer.toString(bytesNo));

    }

    @Test
    public void testWcFromFileL() throws IOException {
        int lineNo = 3;
        File tmp1 = folder.newFile();
        writeToFile(tmp1);
        testArray.add("-l");
        testArray.add(tmp1.getName());

        testWc.exec(testArray, null, System.out, null);

        String split = outContent.toString().split("\\s")[0];
        assertThat(split).isEqualTo(Integer.toString(lineNo));
    }

    @Test

    public void shouldThrowExcIfWrongOptionArg(){
        testArray.add("-n");
        testArray.add("file.txt");

        assertThatThrownBy(() -> {
            testWc.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("wc: illegal option given");


    }

    @Test

    public void testWcWithEmptyFileShouldNotThrowExc() throws IOException {
        File empty = folder.newFile();

        testArray.add(empty.getName());

        assertThatCode(() -> {
            testWc.exec(testArray, null, out, null);

        }).doesNotThrowAnyException();

    }

    @Test
    public void testWcFromFileW() throws IOException {
        int wordNo = 6;
        File tmp1 = folder.newFile();
        writeToFile(tmp1);
        testArray.add("-w");
        testArray.add(tmp1.getName());

        testWc.exec(testArray, null, System.out, null);

        String output = outContent.toString();
        String split = output.split("\\s")[0];
        assertThat(split).isEqualTo(Integer.toString(wordNo));
    }

    @Test

    public void shouldThrowExcIfDirGiven() throws IOException {

        File f = folder.newFolder();
        testArray.add("-m");
        testArray.add(f.getName());

        assertThatThrownBy(() -> {
            testWc.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("wc: wrong file argument");

    }

    @Test

    public void shouldThrowErrorIfWrongfileGiven(){

        testArray.add("-m");
        testArray.add("random.txt");

        assertThatThrownBy(() -> {
            testWc.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("wc: wrong file argument");


    }

    @Test
    public void testWcFromFileWTotalNeeded() throws IOException {
        int wordNo = 6;
        File tmp1 = folder.newFile();
        writeToFile(tmp1);
        File tmp2 = folder.newFile();
        writeToFile(tmp2);
        testArray.add("-w");
        testArray.add(tmp1.getName());
        testArray.add(tmp2.getName());

        testWc.exec(testArray, null, System.out, null);

        String output = outContent.toString();
        String split = output.split("\\s")[4];
        assertThat(split).isEqualTo(Integer.toString(wordNo*2));
    }

    @Test
    public void testWcFromFileLTotalNeeded() throws IOException {
        int lineNo = 3;

        File tmp1 = folder.newFile();
        writeToFile(tmp1);
        File tmp2 = folder.newFile();
        writeToFile(tmp2);
        testArray.add("-l");
        testArray.add(tmp1.getName());
        testArray.add(tmp2.getName());

        testWc.exec(testArray, null, System.out, null);
        String output = outContent.toString().split("\\s")[4];

        assertThat(output).isEqualTo(Integer.toString(lineNo*2));
    }

    @Test
    public void testWcFromFileMTotalNeeded() throws IOException {
        int bytesNo = "Line 1\nLine 2\nLine 3\n".getBytes().length;

        File file = folder.newFile();
        writeToFile(file);
        File file2 = folder.newFile();
        writeToFile(file2);

        testArray.add("-m");
        testArray.add(file.getName());
        testArray.add(file2.getName());

        Jsh.setCurrentDirectory(folder.getRoot().toString());
        testWc.exec(testArray, null, System.out, null);
        String output = outContent.toString().split("\\s")[4];
        assertThat(output).isEqualTo(Integer.toString(bytesNo*2));

    }

    @Test
    public void testWcfor2orMoreArgsShouldProduceCorrectOutput() throws IOException {
        testArray.add("-m");

        File file = folder.newFile();
        writeToFile(file);
        File file2 = folder.newFile();
        writeToFile(file2);

        testArray.add(file.getName());
        testArray.add(file2.getName());

        Jsh.setCurrentDirectory(folder.getRoot().toString());
        testWc.exec(testArray, null, System.out, null);
        String[] split = outContent.toString().split("\\s");
        int bytesNo = "Line 1\nLine 2\nLine 3\n".getBytes().length;
         

        assertThat(Integer.parseInt(split[0])).isEqualTo(bytesNo);
        assertThat(Integer.parseInt(split[2])).isEqualTo(bytesNo);
        assertThat(Integer.parseInt(split[4])).isEqualTo(2*bytesNo);
    }

    @Test
    public void testWcWithoutOptFor2orMoreArgs() throws IOException {
        File tmp1 = folder.newFile();
        writeToFile(tmp1);
        File tmp2 = folder.newFile();
        writeToFile(tmp2);
        testArray.add(tmp1.getName());
        testArray.add(tmp2.getName());

        testWc.exec(testArray, null, System.out, null);
        String output = outContent.toString();
        String[] split = output.split("\n");


        testOutput(false, split[0]);
        testOutput(false, split[1]);
        testOutput(true, split[2]);


    }

    private void testOutput(boolean last, String s){
        int bytesNo = "Line 1\nLine 2\nLine 3\n".getBytes().length;
        int wordsNo = 6;
        int linesNo = 3;
        String[] split = s.split("\\s");
        Integer[] intArray= {linesNo, wordsNo, bytesNo};
        if(!last){
            for(int i=0; i<3; i++){
                assertThat(Integer.parseInt(split[i])).isEqualTo(intArray[i]);
            }
        }else{
            for(int i=0; i<3; i++){
                assertThat(Integer.parseInt(split[i])).isEqualTo(intArray[i]*2);
            }
        }
    }

    private int optionResult(ArrayList<String> test) throws IOException {
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out = new PipedOutputStream(in);

        String originalString = "test line absolute\n2nd line!\nabsent";
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());
    
        testWc.exec(test, inputStream, out, null);
        Scanner scn = new Scanner(in);
        int optAct = Integer.parseInt(scn.next());
        scn.close();
        return optAct;
    }

    private void writeToFile(File file) throws IOException {
        PrintWriter writer = new PrintWriter(file);
        for(int i =0; i<3; i++){
            writer.write("Line "+ i);
            writer.write(System.getProperty("line.separator"));
            writer.flush();
        }
        writer.close();
     }
}