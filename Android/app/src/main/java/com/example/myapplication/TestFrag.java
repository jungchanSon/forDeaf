package com.example.myapplication;

import static android.media.MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myapplication.audioManager.AudioManager;
import com.example.myapplication.databinding.RecordTestBinding;
import com.example.myapplication.jLibrosa.audio.JLibrosa;
import com.example.myapplication.jLibrosa.audio.exception.FileFormatNotSupportedException;
import com.example.myapplication.jLibrosa.audio.wavFile.WavFileException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class TestFrag extends Fragment implements MediaRecorder.OnInfoListener {

    private RecordTestBinding binding;
    private Vibrator vibrator;
    private boolean isRecord = false;
    private TextView record_status;
    private String audioFileName;
    private ArrayList<Uri> audioList;

    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;

    private AudioManager audioManager;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        audioManager = new AudioManager();
        audioList = new ArrayList<>();
        binding = RecordTestBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        jlibrosa 테스트
        System.out.println("hello");
        JLibrosa jLibrosa = new JLibrosa();

        String path = getActivity().getExternalFilesDir("/").getAbsolutePath()+"/"+"001_children_playing.wav";

        int sampleRate = -1;
        int duration = -1;
        int samplerate = jLibrosa.getSampleRate();

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

        for(int i=0;i<1;i++) {
            for(int j=0;j<10;j++) {
                System.out.printf("%.6f%n", mfccValues[i][j]);
            }
        }
//        jlibrosa 테스트 end

        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        record_status = binding.recordStatus;

        // 뒤 페이지로 이동
        binding.testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(TestFrag.this)
                        .navigate(R.id.action_RecordTest_to_AnalysisSoundPage);
            }
        });

        // 녹음 테스트
        binding.recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isRecord){  //녹음 중
                    isRecord = false;
                    record_status.setText("녹음 끝");
                    stopRecord();
                } else { //녹음 x
                    isRecord = true;
                    record_status.setText("녹음 중~");
                    startRecord();

                }
            }
        });
        // 임시 재생 테스트.
        binding.imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer = new MediaPlayer();

                try{

                    mediaPlayer.setDataSource(getActivity().getExternalFilesDir("/").getAbsolutePath()+"/"+"001_children_playing.wav");
                    System.out.println(getActivity().getExternalFilesDir("/").getAbsolutePath()+"/"+"001_children_playing.wav");
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e){
                    e.printStackTrace();
                }

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        System.out.println("complete");
                    }
                });
            }
        });
    }

    private void startRecord(){
        String recordPath = getActivity().getExternalFilesDir("/").getAbsolutePath();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        audioFileName = recordPath + "/" +"RecordExample_" + timeStamp + "_"+"audio.mp4";
        audioFileName = getActivity().getExternalFilesDir("/").getAbsolutePath()+"/" + timeStamp+".wav";


        mediaRecorder = new MediaRecorder();
        mediaRecorder.setOnInfoListener(this);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); //입력 형식
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS); // 출력 형식 지정
        mediaRecorder.setMaxDuration(2000);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC); //인코딩
        mediaRecorder.setOutputFile(audioFileName); // 음성 데이터를 저장할 파일 지정


        try {
            mediaRecorder.prepare(); //준비
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaRecorder.start(); // 레코딩 시작

    }

    private void stopRecord(){
        mediaRecorder.stop(); // 레코딩 중지
//        mediaRecorder.reset(); // setAudioSource () 단계로 돌아가서 객체를 재사용 할 수 있습니다.
        mediaRecorder.release(); // 이제 object를 재사용 할 수 없습니다.
        mediaRecorder = null;

        Uri audioUri = Uri.parse(audioFileName);

        audioList.add(audioUri);

        binding.currnetRec.setText(audioFileName);
        System.out.println(audioUri);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // max 녹화 시간에 따른 콜백함수
    @Override
    public void onInfo(MediaRecorder mediaRecorder, int i, int i1) {
        if (i == MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
            System.out.println("Rec onInfo..");
            mediaRecorder.stop();

            audioManager.addAudioList(audioFileName);
            System.out.println("save :" +audioFileName);
            binding.currnetRec.setText(audioFileName);
            System.out.println("audioManager : " + audioManager);
            mediaRecorder.release();

            mediaRecorder.start();
        }
    }
}