package uk.ac.ucl.jsh;

import java.io.IOException;
import java.util.ArrayList;

public class Seq implements CommandVisitable {
    CommandVisitable leftChild;
    CommandVisitable rightChild;

    public Seq(CommandVisitable leftChild, CommandVisitable rightChild) {
        this.leftChild = leftChild;
        this.rightChild = rightChild;
    }

    public ArrayList<CommandVisitable> getSeqChildren() {
        ArrayList<CommandVisitable> childrenList = new ArrayList<>();
        childrenList.add(leftChild);
        childrenList.add(rightChild);
        return childrenList;
    }

    @Override
    public void accept(CommandVisitor visitor) throws IOException {
        visitor.visitSeq(this);
    }
}