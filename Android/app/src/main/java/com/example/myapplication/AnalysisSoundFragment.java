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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
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

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        handler = new Handler();
        binding = AnalysisSoundPageBinding.inflate(inflater, container, false);
        audioDuration = 5000;

        mfccManager = new MFCCManager();

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
//        JLibrosa jLibrosa= new JLibrosa();
//        int sampleRate = -1;
//        int duration = -1;
//        int samplerate = jLibrosa.getSampleRate();
//        String path = getActivity().getExternalFilesDir("/").getAbsolutePath()+"/"+"20221010_110344.wav";
//
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
//
//        for(int ii=0;ii<1;ii++) {
//            for(int j=0;j<10;j++) {
//                System.out.printf("%.6f%n", mfccValues[ii][j]);
//            }
//        }

//        오디오 녹음 시작
        isRecord = true;
        repeatRecord(audioDuration);

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
                            mfccManager.addMFCC(audioFileName);

//                          mfcc 값 테스트 출력
                            float mfccV[][] = mfccManager.popMFCC();
                            String s = "";
                            for(int ii=0;ii<1;ii++) {
                                for(int j=0;j<10;j++) {
                                    s += "\n";
                                    s +=  mfccV[ii][j];
                                }
                            }

                            binding.testText.setText(
                                    "5초 단위 mfcc 값 "+ new SimpleDateFormat("HH시mm분ss초").format(new Date()) + s
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