package amidst.mojangapi.world.player;

import amidst.mojangapi.file.SaveGame;

public class SingleplayerWorldPlayerType {
    public SingleplayerWorldPlayerType from(SaveGame saveGame) {
        return new SingleplayerWorldPlayerType();
    }
}
