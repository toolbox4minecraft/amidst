package amidst.mojangapi.world.player;

import amidst.mojangapi.file.SaveGame;

public class NoneWorldPlayerType {
    public NoneWorldPlayerType from(SaveGame saveGame) {
        return new NoneWorldPlayerType();
    }
}
