package com.example.myapplication.audioManager;

import com.example.myapplication.jLibrosa.audio.JLibrosa;
import com.example.myapplication.jLibrosa.audio.exception.FileFormatNotSupportedException;
import com.example.myapplication.jLibrosa.audio.wavFile.WavFileException;

import java.io.IOException;
import java.util.ArrayList;

public class MFCCManager {
    private ArrayList<float[][]> mfccList = new ArrayList<>();
    private JLibrosa jLibrosa = new JLibrosa();

    private int sampleRate = -1;
    private int duration = -1;
    private int samplerate = jLibrosa.getSampleRate();

    public MFCCManager(){
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
        System.out.println("path  == " + path);
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

//        for(int ii=0;ii<1;ii++) {
//            for(int j=0;j<10;j++) {
//                System.out.printf("%.6f%n", mfccValues[ii][j]);
//            }
//        }
    }
    public float[][] popMFCC(){
        if(!this.mfccList.isEmpty()){
            System.out.println("pop MFCC");
//            float[][] item = this.mfccList.remove(0);
//            for(int i = 0; i < item[0].length; i++){
//                for(int j = 0; j <item[0=].length; j++ ){
//                    System.out.println(item[i][j]);
//                }
//                System.out.println();
//            }
            return this.mfccList.remove(0);
        }
        System.out.println("mfcc 리스트가 비어 있음!");
        return null;
    }



    @Override
    public String toString() {
        return  " " + mfccList ;
    }
}
