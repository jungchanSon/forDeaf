package com.example.myapplication;

import android.content.Context;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.util.Log;
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
import omrecorder.WriteAction;


public class AnalysisSoundFragment extends Fragment implements MediaRecorder.OnInfoListener {

    private AnalysisSoundPageBinding binding;
    private Vibrator vibrator;

    private TextView test_text;
    private String audioFileName;

    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;

    private MFCCManager mfccManager;
    private int duration;
    private boolean isRecord;
    private Recorder recorder;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = AnalysisSoundPageBinding.inflate(inflater, container, false);
        duration = 2000;

        mfccManager = new MFCCManager();

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        //오디오 녹음 시작
        isRecord = true;
        try {
            startRecord();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        startRecord();

        binding.btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(isRecord){
                   isRecord = false;
//                   mediaRecorder.stop();
//                   mediaRecorder.release();
                   try {
                       startRecord();
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
                   binding.textInfo.setText("분석을 중지합니다.");
               } else{
                   isRecord = true;
//                   startRecord();
                   try {
                       recorder.stopRecording();
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
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

    private void startRecord() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        audioFileName = getActivity().getExternalFilesDir("/").getAbsolutePath()+"/" + timeStamp+".wav";

        Recorder recorder = OmRecorder.wav(
                new PullTransport.Noise(new PullableSource.Default(
                        new AudioRecordConfig.Default(
                                MediaRecorder.AudioSource.MIC, AudioFormat.ENCODING_PCM_16BIT,
                                AudioFormat.CHANNEL_IN_MONO, 44100
                        )
                ) ,
                        new PullTransport.OnAudioChunkPulledListener() {
                            @Override public void onAudioChunkPulled(AudioChunk audioChunk) {
                                System.out.println("onAudioChunkPulled");
                            }
                        },
                        new WriteAction.Default(),
                        new Recorder.OnSilenceListener() {
                            @Override public void onSilence(long silenceTime) {
                                System.out.println("onSilence");
                            }
                        }, 200
                ), new File(audioFileName)
        );


//        @NonNull private File file() {
//            return new File(Environment.getExternalStorageDirectory(), "demo.wav");
//        }



//        mediaRecorder = new MediaRecorder();
//        mediaRecorder.setOnInfoListener(this);
//        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); //입력 형식
//        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS); // 출력 형식 지정
//        mediaRecorder.setMaxDuration(duration);
//        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC); //인코딩
//        mediaRecorder.setOutputFile(audioFileName); // 음성 데이터를 저장할 파일 지정

//        try{
//            mediaRecorder.prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        mediaRecorder.start();
        recorder.startRecording();
        System.out.println(audioFileName);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onInfo(MediaRecorder mediaRecorder, int i, int i1) {

        if(i == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED){
            System.out.println("listened Max Duration");

            mediaRecorder.stop();
            mediaRecorder.release();
//            mediaRecorder.reset();

//            mfccManager.addMFCC(audioFileName);
//            binding.testText.setText(""+ mfccManager);
            System.out.println(audioFileName);
            JLibrosa jLibrosa= new JLibrosa();
            int sampleRate = -1;
            int duration = -1;
            int samplerate = jLibrosa.getSampleRate();
            String path = getActivity().getExternalFilesDir("/").getAbsolutePath()+"/"+"001_children_playing.wav";


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


            System.out.println("Size of MFCC Feature Values: (" + mfccValues.length + " , " + mfccValues[0].length + " )");
            System.out.println("Size of MFCC Feature Values: (" + mfccValues.length + " , " + mfccValues[0].length + " )");

            for(int ii=0;ii<1;ii++) {
                for(int j=0;j<10;j++) {
                    System.out.printf("%.6f%n", mfccValues[ii][j]);
                }
            }

            if (isRecord){
                System.out.println("restart");
                try {
                    startRecord();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}