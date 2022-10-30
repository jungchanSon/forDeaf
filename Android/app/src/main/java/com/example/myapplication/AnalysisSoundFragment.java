package com.example.myapplication;

import android.content.Context;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myapplication.audioManager.MFCCManager;
import com.example.myapplication.databinding.AnalysisSoundPageBinding;
import com.example.myapplication.jLibrosa.audio.JLibrosa;
import com.example.myapplication.jLibrosa.audio.exception.FileFormatNotSupportedException;
import com.example.myapplication.jLibrosa.audio.wavFile.WavFileException;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.TensorFlowLite;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import omrecorder.AudioChunk;
import omrecorder.AudioRecordConfig;
import omrecorder.OmRecorder;
import omrecorder.PullTransport;
import omrecorder.PullableSource;
import omrecorder.Recorder;


public class AnalysisSoundFragment extends Fragment{

    private AnalysisSoundPageBinding binding;
    private Vibrator vibrator;

    private TextView test_text;
    private String audioFileName;

    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;

    private MFCCManager mfccManager;
    private int audioDuration;
    private boolean isRecord;
    private Recorder recorder;
    private Handler handler;
    private JLibrosa jLibrosa;
    private ArrayList<float[][]> mfccList;

    private Interpreter tensorFlowLite;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
    //        모델 테스트코드 시작
            JLibrosa jLibrosa = new JLibrosa();

            String path = getActivity().getExternalFilesDir("/").getAbsolutePath()+"/" + "001_children_playing"+".wav";
        System.out.println("path = " + path);
            int sampleRate = -1;
            int duration = -1;
            int samplerate = jLibrosa.getSampleRate();
            System.out.println("samplerate = " + samplerate);
            float audioFeat [] = new float[0];
            try {
                audioFeat = jLibrosa.loadAndRead(path, samplerate, duration);
                System.out.println("audioFeat = " + audioFeat);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (WavFileException e) {
                e.printStackTrace();
            } catch (FileFormatNotSupportedException e) {
                e.printStackTrace();
            }

            float mfccValues[][] = jLibrosa.generateMFCCFeatures(audioFeat, sampleRate, 40);

        System.out.println("Size of MFCC Feature Values: (" + mfccValues.length + " , " + mfccValues[0].length + " )");
        System.out.println("Size of MFCC Feature Values: (" + mfccValues.length + " , " + mfccValues[0].length + " )");
        float[][][][] tempmfcc = {{ mfccValues }};
        System.out.println("Size of MFCC Feature Values: (" + tempmfcc.length + " , " + tempmfcc[0].length + "," +tempmfcc[0][0].length+", "+ tempmfcc[0][0][0].length + " )");

        for(int i=0;i<1;i++) {
            for(int j=0;j<10;j++) {
                System.out.printf("%.6f%n", mfccValues[i][j]);
            }
        }
        String model_path = getActivity().getExternalFilesDir("/").getAbsolutePath() + "/" + "For_Deaf.tflite";
        FileInputStream f_input_stream= null;
        try {
            f_input_stream = new FileInputStream(new File(model_path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        FileChannel f_channel = f_input_stream.getChannel();
        MappedByteBuffer tflite_model = null;
        try {
            tflite_model = f_channel.map(FileChannel.MapMode.READ_ONLY, 0, f_channel.size());
        } catch (IOException e) {
            e.printStackTrace();
        }

        float[] outputs = new float[0];
        Interpreter interpreter = new Interpreter(tflite_model);
        interpreter.allocateTensors();
        int outputTensorCount = interpreter.getOutputTensorCount();
        int inputTensorCount = interpreter.getInputTensorCount();
        System.out.println("in = " + inputTensorCount);
        System.out.println("out = " + outputTensorCount);
        interpreter.run(tempmfcc, outputs);
        System.out.println("interpreter = " + interpreter);
        System.out.println("outputs = " + outputs);
        //        모델 테스트코드 끝
            mfccList = new ArrayList();

            handler = new Handler();
            binding = AnalysisSoundPageBinding.inflate(inflater, container, false);
            audioDuration = 2000;

    //        try{
    //
    //            File modelFile = new File("For_Deaf.tflite");
    //
    //            tensorFlowLite = new Interpreter( modelFile);
    //
    //            File inputFile = new File(getActivity().getExternalFilesDir("/").getAbsolutePath() + "/" + "20221011_071310.wav");
    //            float output = 0;
    //            tensorFlowLite.run(inputFile, output);
    //
    //            System.out.println(output);
    //
    //        }catch(Exception e){
    //            e.printStackTrace();
    //        }

            return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        mfccManager = new MFCCManager();
        jLibrosa= new JLibrosa();
//        int sampleRate = -1;
//        int duration = -1;
//        int samplerate = jLibrosa.getSampleRate();
//
//        String path =   getActivity().getExternalFilesDir("/").getAbsolutePath()+"/"+"20221011_062041.wav";
//
//        float audioFeat [] = new float[0];
//        try {
//            audioFeat = jLibrosa.loadAndRead(path, sampleRate, duration);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (WavFileException e) {
//            e.printStackTrace();
//        } catch (FileFormatNotSupportedException e) {
//            e.printStackTrace();
//        }
//
//        float mfccValues[][] = jLibrosa.generateMFCCFeatures(audioFeat, sampleRate, 40);
//
//
//        System.out.println("Size of MFCC Feature Values: (" + mfccValues.length + " , " + mfccValues[0].length + " )");
//        System.out.println("Size of MFCC Feature Values: (" + mfccValues.length + " , " + mfccValues[0].length + " )");
//        System.out.println(">>>>>");
//        for(int ii=0;ii<1;ii++) {
//            for(int j=0;j<10;j++) {
//                System.out.printf("%.6f%n", mfccValues[ii][j]);
//            }
//        }
//        System.out.println("<<<<<<");

//        오디오 녹음 시작
        isRecord = true;
        //위 테스트 끝나면 주석 풀기!
//        repeatRecord(audioDuration);

        binding.btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(isRecord){
                       isRecord = false;
                   try {
                       recorder.stopRecording();
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
                   binding.textInfo.setText("분석을 중지합니다.");
               } else{
                   isRecord = true;
                   repeatRecord(audioDuration);
                   binding.textInfo.setText("분석을 시작합니다.");

               }
            }
        });

        binding.btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(AnalysisSoundFragment.this)
                        .navigate(R.id.action_AnalysisSoundPage_to_SoundCheckPage);
                mediaRecorder.stop();
                mediaRecorder.release();
            }
        });

        binding.analysisTextBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.analysisTextBox.setVisibility(View.GONE);
                binding.detectCarBox.setVisibility(View.VISIBLE);
                vibrator.vibrate(500);
            }
        });

        binding.detectCarBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.detectCarBox.setVisibility(View.GONE);
                binding.analysisTextBox.setVisibility(View.VISIBLE);
            }
        });
    }

    private void repeatRecord(int duration) {
        try {
            startRecord();
            isRecord = true;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(isRecord){
                        try {
                            stopRecord();
                            System.out.println("audioFileName :: ::  >> "+audioFileName);
                            mfccManager.addMFCC(audioFileName);
                            //                            ===
//
//                            int sampleRate = -1;
//                            int duration = -1;
//                            int samplerate = jLibrosa.getSampleRate();
//
//                            String path =   audioFileName;
//
//                            float audioFeat [] = new float[0];
//                            try {
//                                audioFeat = jLibrosa.loadAndRead(path, sampleRate, duration);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            } catch (WavFileException e) {
//                                e.printStackTrace();
//                            } catch (FileFormatNotSupportedException e) {
//                                e.printStackTrace();
//                            }
//
//                            float mfccValues[][] = jLibrosa.generateMFCCFeatures(audioFeat, sampleRate, 40);
//                            ---
//                          mfcc 값 테스트 출력

                            float [][] data = mfccManager.popMFCC();
                            System.out.println(">>>>>>>");
                            for(int ii=0;ii<1;ii++) {
                                for (int j = 0; j < 10; j++) {
                                    System.out.printf("%.6f%n", data[ii][j]);
                                }
                            }
                            System.out.println("<<<<<>>>>>");


                            binding.testText.setText(
                                    "5초 단위 mfcc 값 "+ new SimpleDateFormat("HH시mm분ss초").format(new Date())+"\n"+data[0][0]
                            );
                            repeatRecord(duration);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, duration);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void startRecord() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        audioFileName = getActivity().getExternalFilesDir("/").getAbsolutePath()+"/" + timeStamp+".wav";

        PullableSource.Default mic = new PullableSource.Default(
                new AudioRecordConfig.Default(
                        MediaRecorder.AudioSource.MIC,
                        AudioFormat.ENCODING_PCM_16BIT,
                        AudioFormat.CHANNEL_IN_MONO,
                        44100
                )
        );


        File file = new File(audioFileName);

        recorder = OmRecorder.wav(
                new PullTransport.Default(mic),
                file
        );

        recorder.startRecording();
        System.out.println(mfccManager);
        System.out.println(audioFileName);
    }

    private void stopRecord() throws IOException {
        recorder.stopRecording();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}