package uk.ac.ucl.jsh;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
// import java.util.ArrayList;
import java.util.ArrayList;

public class Eval implements CommandVisitor {
    private ApplicationFactory safeFactory = new ApplicationFactory();
    private InputStream input = null;
    private OutputStream writer;
    private int pipeList = 0;

    public Eval(OutputStream writer) {
        this.writer = writer;
    }

    @Override
    public void visitPipe(Pipe pipe) throws IOException {
        input = new PipedInputStream();
        writer = new PipedOutputStream((PipedInputStream) input);
        pipeList++;
        pipe.getPipeChildren().get(0).accept(this);
        OutputStreamWriter os = new OutputStreamWriter(writer);
        // os.write((char) 0);
        BufferedWriter bf = new BufferedWriter(os);
        bf.flush();
        if (pipeList == 1) {
            writer = System.out;
        }
        pipe.getPipeChildren().get(1).accept(this);
        pipeList--;
    }

    @Override
    public void visitSeq(Seq seq) throws IOException {
        seq.getSeqChildren().get(0).accept(this);
        seq.getSeqChildren().get(1).accept(this);
    }

    @Override
    public void visitCall(Call call) throws IOException {
        Application application = safeFactory.mkApplication(call.getApplication());
        application.exec(call.getArguments(), input, writer);
    }
}
