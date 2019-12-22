package uk.ac.ucl.jsh;

import java.io.IOException;

public interface CommandVisitor {
    void visitCall(Call call) throws IOException;
    void visitPipe(Pipe pipe) throws IOException;
    void visitSeq(Seq seq) throws IOException;
}