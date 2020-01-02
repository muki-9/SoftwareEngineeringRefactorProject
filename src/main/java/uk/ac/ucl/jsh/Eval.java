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

    @Override
    public void visitSeq(Seq seq) throws IOException {
        osList.add(osList.get(osList.size()-1));
        isList.add(isList.get(isList.size()-1));
        seq.getSeqChildren().get(0).accept(this);
        seq.getSeqChildren().get(1).accept(this);
    }

    @Override
    public void visitCall(Call call) throws IOException {
        if (call.getISRequired()) {
            isList.add(call.getIS());
        }
        if (call.getOSRequired()) {
            osList.add(call.getOS());
        }
        InputStream is = isList.get(isList.size()-1);
        OutputStream os = osList.get(osList.size()-1);
        isList.remove(isList.size()-1);
        osList.remove(osList.size()-1);
        if (call.getApplication() != null) {
            Application application = safeFactory.mkApplication(call.getApplication());
            application.exec(call.getArguments(), is, os);
        }
        return;
    }
}
