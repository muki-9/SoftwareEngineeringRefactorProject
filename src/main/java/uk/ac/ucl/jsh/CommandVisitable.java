package uk.ac.ucl.jsh;

import java.io.IOException;

public interface CommandVisitable {
    public void accept(CommandVisitor visitor) throws IOException;
}