package de.turboman.ctf;

public enum GameState {
    NO_GAME(0),
    SET_FLAG(1),
    PREP(2),
    FIGHT(3);

    int id = 0;

    GameState(int id) {
        this.id = id;
    }
}
