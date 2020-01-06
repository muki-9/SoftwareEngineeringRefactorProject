package uk.ac.ucl.jsh;
import org.antlr.v4.runtime.*;


public class TestToken implements Token {


    private int tcod;
    private String text;

    public TestToken(String text, int tcod){

        this.tcod = tcod;
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public int getType() {
        return tcod;
    }

    @Override
    public int getLine() {
        return 0;
    }

    @Override
    public int getCharPositionInLine() {
        return 0;
    }

    @Override
    public int getChannel() {
        return 0;
    }

    @Override
    public int getTokenIndex() {
        return 0;
    }

    @Override
    public int getStartIndex() {
        return 0;
    }

    @Override
    public int getStopIndex() {
        return 0;
    }

    @Override
    public TokenSource getTokenSource() {
        return null;
    }

    @Override
    public CharStream getInputStream() {
        return null;
    }
    
    



}