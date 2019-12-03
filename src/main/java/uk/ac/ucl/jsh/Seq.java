package uk.ac.ucl.jsh;

public class Seq implements CommandVisitable {

    @Override
    public void accept(CommandVisitor visitor) {
        visitor.visit(this);
    }
}