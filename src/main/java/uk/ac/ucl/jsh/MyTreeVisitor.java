package uk.ac.ucl.jsh;

import java.util.ArrayList;

public class MyTreeVisitor extends AntlrGrammarBaseVisitor<String> {
    private ArrayList<String> tokens = new ArrayList<>();

    public void visitCommandToken(AntlrGrammarParser.CtokenContext c) {
        tokens.add(c.getText());
    } 

    public String visitArgument(AntlrGrammarParser.ArgumentContext a) {
        for (int i = 0;i<a.getChildCount();i++) {
            tokens.add(a.getChild(i).getText());
        }
        return null;
    }

    public ArrayList<String> getTokens() {
        return tokens;
    }
}