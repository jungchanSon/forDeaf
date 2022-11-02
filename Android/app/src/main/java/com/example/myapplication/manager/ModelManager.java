package com.example.myapplication.manager;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class ModelManager {

    private Interpreter interpreter;
    private MappedByteBuffer tflite_mdoel;
    private float[][] output;


    public ModelManager(Activity activity, String model_path) throws IOException {

        this.tflite_mdoel = loadModelFile(activity, model_path);
        this.output = new float[1][10];
        this.interpreter = new Interpreter(tflite_mdoel);
    }

    public float[][] run(float[][][] mfcc){
        float[][] outputs = new float[1][10];
        this.interpreter.allocateTensors();
        interpreter.run(mfcc, outputs);

        return outputs;
    }
    private MappedByteBuffer loadModelFile(Activity activity, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();

        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();

        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
}
