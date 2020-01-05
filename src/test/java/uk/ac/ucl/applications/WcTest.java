package uk.ac.ucl.applications;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import static org.assertj.core.api.Assertions.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import uk.ac.ucl.applications.Wc;
import uk.ac.ucl.jsh.Jsh;


public class WcTest{


    public WcTest(){

    }

    PipedInputStream in;
    PipedOutputStream out;
    Wc testWc;
    ArrayList<String> testArray;


    @Before 
    public void init() throws IOException {
        in = new PipedInputStream();
        out = new PipedOutputStream(in);
        testWc = new Wc();
        testArray = new ArrayList<>();

    }

    @Rule
    public TemporaryFolder folder  = new TemporaryFolder(new File(Jsh.getHomeDirectory()));

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
        testWc.exec(testArray, inputStream, out, null);
        Scanner scn = new Scanner(in);
        int linesAct = Integer.parseInt(scn.next());
        int wordAct = Integer.parseInt(scn.next());
        int byteAct = Integer.parseInt(scn.next());
        scn.close();

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
        File file = folder.newFile();
        writeToFile(file);
        testArray.add("-m");
        testArray.add(file.getName());

        Jsh.setCurrentDirectory(folder.getRoot().toString());

        testWc.exec(testArray, null, out, null);
        Scanner scn = new Scanner(in);
        String output = scn.nextLine();
        String split = output.split("\\s")[0];
        assertThat(split).isEqualTo(Integer.toString(bytesNo));
        scn.close();

    }

    @Test
    public void testWcFromFileL() throws IOException {
        int lineNo = 3;
        File file = folder.newFile();
        writeToFile(file);
        testArray.add("-l");
        testArray.add(file.getName());

        Jsh.setCurrentDirectory(folder.getRoot().toString());

        testWc.exec(testArray, null, out, null);

        Scanner scn = new Scanner(in);
        String output = scn.nextLine();
        String split = output.split("\\s")[0];
        assertThat(split).isEqualTo(Integer.toString(lineNo));
        scn.close();

    }

    @Test
    public void testWcFromFileW() throws IOException {
        int wordNo = 6;
        File file = folder.newFile();
        writeToFile(file);
        testArray.add("-w");
        testArray.add(file.getName());

        Jsh.setCurrentDirectory(folder.getRoot().toString());

        testWc.exec(testArray, null, out, null);

        Scanner scn = new Scanner(in);
        String output = scn.nextLine();
        String split = output.split("\\s")[0];
        assertThat(split).isEqualTo(Integer.toString(wordNo));
        scn.close();
    }

    @Test
    public void testWcFromFileWTotalNeeded() throws IOException {
        int wordNo = 6;

        File file = folder.newFile();
        writeToFile(file);
        File file2 = folder.newFile();
        writeToFile(file2);

        testArray.add("-w");
        testArray.add(file.getName());
        testArray.add(file2.getName());

        Jsh.setCurrentDirectory(folder.getRoot().toString());
        testWc.exec(testArray, null, out, null);
        Scanner scn = new Scanner(in);
        String output = scn.nextLine();
        for (int i=0; i<2; i++) {
            output = scn.nextLine();
        }
        String split = output.split("\\s")[0];
        assertThat(split).isEqualTo(Integer.toString(wordNo*2));
        scn.close();
    }

    @Test
    public void testWcFromFileLTotalNeeded() throws IOException {
        int lineNo = 3;

        File file = folder.newFile();
        writeToFile(file);
        File file2 = folder.newFile();
        writeToFile(file2);

        testArray.add("-l");
        testArray.add(file.getName());
        testArray.add(file2.getName());

        Jsh.setCurrentDirectory(folder.getRoot().toString());
        testWc.exec(testArray, null, out, null);
        Scanner scn = new Scanner(in);
        String output = scn.nextLine();
        for (int i=0; i<2; i++) {
            output = scn.nextLine();
        }
        String split = output.split("\\s")[0];
        assertThat(split).isEqualTo(Integer.toString(lineNo*2));
        scn.close();
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
        testWc.exec(testArray, null, out, null);
        Scanner scn = new Scanner(in);
        String output = scn.nextLine();
        for (int i=0; i<2; i++) {
            output = scn.nextLine();
        }
        String split = output.split("\\s")[0];
        assertThat(split).isEqualTo(Integer.toString(bytesNo*2));
        scn.close();
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
        testWc.exec(testArray, null, out, null);

        Scanner scn = new Scanner(in);
        String line = scn.nextLine();
        String[] split = line.split("\\s");
        int bytesNo = "Line 1\nLine 2\nLine 3\n".getBytes().length;
         
        assertThat(Integer.parseInt(split[0])).isEqualTo(bytesNo);

        String line1= scn.nextLine();
        String[] split1 = line1.split("\\s");

        assertThat(Integer.parseInt(split1[0])).isEqualTo(bytesNo);

        String line2= scn.nextLine();
        String[] split2 = line2.split("\\s");

        assertThat(Integer.parseInt(split2[0])).isEqualTo(2*bytesNo);
        scn.close();
    }

    @Test
    public void testWcWithoutOptFor2orMoreArgs() throws IOException {
        File file = folder.newFile();
        writeToFile(file);
        File file2 = folder.newFile();
        writeToFile(file2);
        testArray.add(file.getName());
        testArray.add(file2.getName());

        Jsh.setCurrentDirectory(folder.getRoot().toString());
        testWc.exec(testArray, null, out, null);

        Scanner scn = new Scanner(in);
        String line = scn.nextLine();
        String[] split = line.split("\\s");

        testOutput(false, split);

        String line1 = scn.nextLine();
        String[] split1= line1.split("\\s");

        testOutput(false, split1);

        String line2 = scn.nextLine();
        String[] split2= line2.split("\\s");

        testOutput(true, split2);

        scn.close();

    }

    private void testOutput(boolean last, String[] split){
        int bytesNo = "Line 1\nLine 2\nLine 3\n".getBytes().length;
        int wordsNo = 6;
        int linesNo = 3;
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

        String originalString = "test line absolute\n2nd line!\nabsent"; //if only print new lie counts then 
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