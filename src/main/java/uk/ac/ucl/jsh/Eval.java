package uk.ac.ucl.jsh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;

public class Eval implements CommandVisitor {

    private ApplicationFactory safeFactory = new ApplicationFactory();

    /*  lists of input and output streams- required primarily for pipe functionality
        so that when multiple pipes are called, the input streams are simply appended
        to this list and then popped off the beginning when executed.  */
    private ArrayList<OutputStream> osList = new ArrayList<>();
    private ArrayList<InputStream> isList = new ArrayList<>();

    public Eval(OutputStream writer) {
        this.osList.add(writer);
        this.isList.add(null);
    }

    /**
    * To execute a pipe command, the first command is executed with a null input stream and the output stream is piped into
    * another input stream that is passed into the next command. This next command may well be another pipe, so instead of 
    * simply passing input/output streams, they are stored in an arraylist, so that if pipes are stacked, so are the streams.
    *
    * @param pipe the pipe object whose children contain the commands that need to be executed.
    *
    * @throws IOException if input stream could not be piped into the output stream or any other error comes about as a result of 
    * using the stream, or closing it.
    */
    @Override
    public void visitPipe(Pipe pipe) throws IOException {
        InputStream input = new PipedInputStream(90000);
        OutputStream pipeWriter = new PipedOutputStream((PipedInputStream) input);
        osList.add(pipeWriter);
        pipe.getPipeChildren().get(0).accept(this);
        pipeWriter.close();
        isList.add(input);
        pipe.getPipeChildren().get(1).accept(this);
    }

    /**
    * To execute a seq command, the first command is executed and then the second, in that order. Since the last input and output stream
    * is always popped off after execution, we duplicate it here as we need it for 2 commands.
    *
    * @param seq the seq object whose children contain the commands that need to be executed.
    *
    * @throws IOException the '.accept' method throws an unhandled IOException which carries over.
    */
    @Override
    public void visitSeq(Seq seq) throws IOException {
        osList.add(osList.get(osList.size()-1));
        isList.add(isList.get(isList.size()-1));
        seq.getSeqChildren().get(0).accept(this);
        seq.getSeqChildren().get(1).accept(this);
    }

    /**
    * The call object passed through the parameter will carry its relevant input and output streams if they are required.
    * The inital two if statements check if they are needed, and if so, they are appended to the end of the list.
    * They are then popped off after being passed to the Application that is created using the factory.
    *
    * @param call the call object which will have instance variables ready with the application and arguments after it has been visited once.
    *
    * @throws IOException the '.exec' method throws an unhandled IOException which carries over.
    */
    @Override
    public void visitCall(Call call) throws IOException {
        if (call.getIS() != null) {
            isList.add(call.getIS());
        }
        if (call.getOS() != null) {
            osList.add(call.getOS());
        }
        InputStream is = isList.get(isList.size()-1);
        OutputStream os = osList.get(osList.size()-1);
        isList.remove(isList.size()-1);
        osList.remove(osList.size()-1);
        if(call.getApplication() != null){
            Application application = safeFactory.mkApplication(call.getApplication());
            application.exec(call.getArguments(), is, os, call.getGlobbArray());
        }
    }
}