package uk.ac.ucl.jsh;

import java.io.IOException;
import java.io.OutputStream;

public class Eval implements CommandVisitor {
    private SafeApplicationFactory safeFactory = new SafeApplicationFactory();
    private OutputStream writer;

    public Eval(OutputStream writer) {
        this.writer = writer;
    }

    @Override
    public void visitPipe(Pipe pipe) {
        // TODO Auto-generated method stub
    }

    @Override
    public void visitSeq(Seq seq) {
        // TODO Auto-generated method stub
    }

    @Override
    public void visitCall(Call call) throws IOException {
        Application application = safeFactory.mkSafeApplication(call.getApplication());
        application.exec(call.getArguments(), null, writer);
    }
}
