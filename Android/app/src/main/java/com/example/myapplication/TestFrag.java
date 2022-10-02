package com.example.myapplication;

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

import com.example.myapplication.databinding.RecordTestBinding;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class TestFrag extends Fragment {

    private RecordTestBinding binding;
    private Vibrator vibrator;
    private boolean isRecord = false;
    private TextView record_status;
    private String audioFileName;
    private ArrayList<Uri> audioList;

    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;



    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        audioList = new ArrayList<>();
        binding = RecordTestBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        record_status = binding.recordStatus;

        binding.testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(TestFrag.this)
                        .navigate(R.id.action_RecordTest_to_AnalysisSoundPage);
            }
        });

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
        binding.imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer = new MediaPlayer();

                try{
                    mediaPlayer.setDataSource(getActivity().getExternalFilesDir("/").getAbsolutePath()+"/"+"test.mp3");
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
//        audioFileName = recordPath + "/" +"RecordExample_" + timeStamp + "_"+"audio.mp4";
        audioFileName = getActivity().getExternalFilesDir("/").getAbsolutePath()+"/"+"test.mp3";
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); //입력 형식
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS); // 출력 형식 지정
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

}