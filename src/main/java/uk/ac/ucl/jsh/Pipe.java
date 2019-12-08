package uk.ac.ucl.jsh;

import java.io.IOException;
import java.util.ArrayList;

public class Pipe implements CommandVisitable {
    CommandVisitable leftChild;
    CommandVisitable rightChild;

    public Pipe(CommandVisitable leftChild, CommandVisitable rightChild) {
        this.leftChild = leftChild;
        this.rightChild = rightChild;
    }

    @Override
    public void accept(CommandVisitor visitor) throws IOException {
        visitor.visitPipe(this);
    }

	public ArrayList<CommandVisitable> getPipeChildren() {
		ArrayList<CommandVisitable> childrenList = new ArrayList<>();
        childrenList.add(leftChild);
        childrenList.add(rightChild);
        return childrenList;
	}
}