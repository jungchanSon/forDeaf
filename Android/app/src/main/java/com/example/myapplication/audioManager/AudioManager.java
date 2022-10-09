package com.example.myapplication.audioManager;

import java.util.ArrayList;

public class AudioManager {
    private ArrayList<String> audioList = new ArrayList();

    public AudioManager(){

    }

    public int addAudioList(String uri){
        audioList.add(uri);
        return 1;
    }

    public String leftPopAudio() {
        if (!audioList.isEmpty())
            return audioList.remove(0);
        return "0";
    }

    @Override
    public String toString() {
        return "AudioManager{" +
                "audioList=" + audioList +
                '}';
    }
}
