package com.example.myapplication.manager;

import android.content.res.AssetManager;

import androidx.annotation.NonNull;

import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class ModelManager {

    private Interpreter interpreter;
    private FileInputStream f_input_stream;
    private FileChannel f_channel;
    private MappedByteBuffer tflite_mdoel;
    private float[][] output;
    public float[][] getOutput() {
        return output;
    }

    public Interpreter getInterpreter() {
        return this.interpreter;
    }


    public ModelManager(String model_path) {

        this.f_input_stream = getModelInputStream(model_path);
        this.f_channel = f_input_stream.getChannel();
        this.tflite_mdoel = getMap();
        this.output = new float[1][10];
        this.interpreter = new Interpreter(tflite_mdoel);
    }

    private MappedByteBuffer getMap() {
        try {
            return f_channel.map(FileChannel.MapMode.READ_ONLY, 0, f_channel.size());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @NonNull
    private FileInputStream getModelInputStream(String model_path) {
        try {
            return new FileInputStream(new File(model_path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public float[][] run(float[][][] mfcc){
        float[][] outputs = new float[1][10];
        this.interpreter.allocateTensors();
        interpreter.run(mfcc, outputs);

        return outputs;
    }
}
