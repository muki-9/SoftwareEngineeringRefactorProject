package uk.ac.ucl.jsh;

public interface CommandVisitable {
    public void accept(CommandVisitor visitor);
}