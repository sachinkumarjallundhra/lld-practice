package lld.designpattren.Iterator.problem;

class MusicPlayer {
    public void playAll(Playlist playlist) {
        for (String song : playlist.getSongs()) {
            System.out.println("Playing: " + song);
        }
    }
}
