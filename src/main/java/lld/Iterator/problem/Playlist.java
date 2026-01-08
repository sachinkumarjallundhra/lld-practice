package lld.Iterator.problem;

import java.util.ArrayList;
import java.util.List;

class Playlist {
    private List<String> songs = new ArrayList<>();

    public void addSong(String song) {
        songs.add(song);
    }

    public List<String> getSongs() {
        return songs;
    }
}
