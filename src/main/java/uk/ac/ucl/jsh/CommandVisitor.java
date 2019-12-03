package uk.ac.ucl.jsh;

public interface CommandVisitor {
    public void visit(Pipe pipe);
    public void visit(Seq seq);
	public void visit(Call call);
}