package uk.ac.ucl.jsh;

public class Pipe implements CommandVisitable {

    @Override
    public void accept(CommandVisitor visitor) {
        visitor.visit(this);
    }
}