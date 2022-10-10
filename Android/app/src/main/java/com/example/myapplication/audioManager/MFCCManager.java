package com.example.myapplication.audioManager;

import com.example.myapplication.jLibrosa.audio.JLibrosa;
import com.example.myapplication.jLibrosa.audio.exception.FileFormatNotSupportedException;
import com.example.myapplication.jLibrosa.audio.wavFile.WavFileException;

import java.io.IOException;
import java.util.ArrayList;

public class MFCCManager {
    ArrayList<float[][]> mfccList = new ArrayList<>();
    JLibrosa jLibrosa = new JLibrosa();

    int sampleRate;
    int duration;
    int samplerate = jLibrosa.getSampleRate();

    public MFCCManager(){
        samplerate = -1;
        duration = -1;
    }

    public MFCCManager(int sampleRate, int duration){
        this.sampleRate = sampleRate;
        this.duration = duration;
    }

    /**
     * 
     * @param path mfcc 변환할 파일 경로
     *             ㄹㄹ
     */
    public void addMFCC(String path){

        float audioFeat [] = new float[0];

        try {
            audioFeat = jLibrosa.loadAndRead(path, sampleRate, duration);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WavFileException e) {
            e.printStackTrace();
        } catch (FileFormatNotSupportedException e) {
            e.printStackTrace();
        }

        float mfccValues[][] = jLibrosa.generateMFCCFeatures(audioFeat, sampleRate, 40);

        this.mfccList.add(mfccValues);

        System.out.println(mfccValues);
    }
    public float[][] popMFCC(){
        if(!this.mfccList.isEmpty()){
            System.out.println("pop MFCC");
            return this.mfccList.remove(0);
        }
        System.out.println("mfcc 리스트가 비어 있음!");
        return null;
    }

    @Override
    public String toString() {
        return "MFCCManager{" +
                "mfccList=" + mfccList +
                '}';
    }
}
