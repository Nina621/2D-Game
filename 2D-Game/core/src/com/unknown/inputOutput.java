package com.unknown;

import com.badlogic.gdx.Input;

public class inputOutput implements Input.TextInputListener {

    private Main game;

    inputOutput(Main game){
        this.game = game;
    }

    @Override
    public void input (String text) {
        game.playerName = text;
    }

    @Override
    public void canceled () {
        game.playerName = "UnknownPlayer";
    }
}