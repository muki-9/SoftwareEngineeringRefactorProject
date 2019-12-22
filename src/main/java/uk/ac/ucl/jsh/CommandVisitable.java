package uk.ac.ucl.jsh;

import java.io.IOException;

public interface CommandVisitable {
    void accept(CommandVisitor visitor) throws IOException;
}