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
        // TODO Auto-generated method stub
        return text;
    }

    @Override
    public int getType() {
        // TODO Auto-generated method stub
        return tcod;
    }

    @Override
    public int getLine() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getCharPositionInLine() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getChannel() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getTokenIndex() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getStartIndex() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getStopIndex() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public TokenSource getTokenSource() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CharStream getInputStream() {
        // TODO Auto-generated method stub
        return null;
    }
    
    



}