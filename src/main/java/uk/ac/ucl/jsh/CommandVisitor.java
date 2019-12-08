package uk.ac.ucl.jsh;

import java.io.IOException;

public interface CommandVisitor {
    public void visitCall(Call call) throws IOException;
    public void visitPipe(Pipe pipe) throws IOException;
    public void visitSeq(Seq seq) throws IOException;
}