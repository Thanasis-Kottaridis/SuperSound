package com.unipi.kottarido.supersound.supersound;

// kathe antikimeno autis tis klasis tha antiprosopeuei ena idos mousikis pou aresi ston xtisti

public class MusicPreference {
    private String MusicKind;
    private int TotalSongs;

    public MusicPreference(String musicKind) {
        MusicKind = musicKind;
        TotalSongs = 0;
    }

    public String getMusicKind() {
        return MusicKind;
    }

    public void setMusicKind(String musicKind) {
        MusicKind = musicKind;
    }

    public int getTotalSongs() {
        return TotalSongs;
    }

    public void setTotalSongs(int totalSongs) {
        TotalSongs = totalSongs;
    }
}
