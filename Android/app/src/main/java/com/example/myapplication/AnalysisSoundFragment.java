package com.example.myapplication;

import android.content.Context;

import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myapplication.manager.MFCCManager;
import com.example.myapplication.databinding.AnalysisSoundPageBinding;
import com.example.myapplication.manager.ModelManager;
import com.example.myapplication.manager.RecordManager;
import java.io.IOException;

public class AnalysisSoundFragment extends Fragment{

    private AnalysisSoundPageBinding binding;
    private Vibrator vibrator;


    private MFCCManager mfccManager;
    private int audioDuration;
    private boolean isRecord;
    private Handler handler;

    private RecordManager recordManager;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

            handler = new Handler();
            binding = AnalysisSoundPageBinding.inflate(inflater, container, false);
            audioDuration = 1000;



            return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        mfccManager = new MFCCManager();
        recordManager = new RecordManager(getActivity());

//        오디오 녹음 시작
        isRecord = true;
        //위 테스트 끝나면 주석 풀기!
        repeatRecord(audioDuration);

        binding.btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(isRecord){
                       isRecord = false;
                   try {
                       recordManager.stopRecord();
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
                   binding.btnStop.setText("시작");
                   binding.textInfo.setText("분석을 중지합니다.");
               } else{
                   isRecord = true;
                   binding.textInfo.setText("분석을 시작합니다.");
                   binding.btnStop.setText("중지");
                   repeatRecord(audioDuration);

               }
            }
        });



        binding.btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(AnalysisSoundFragment.this)
                        .navigate(R.id.action_AnalysisSoundPage_to_SoundCheckPage);

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
            recordManager.startRecord();
            isRecord = true;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(isRecord){
                        try {
//                            String model_path = "file:///android_asset/fordeaf.tflite";
                            recordManager.stopRecord();
                            String audioFileName = recordManager.getAudioFileName();
                            mfccManager.addMFCC(audioFileName);

                            float [][][] mfcc3d = mfccManager.popMFCC3D();
                            Log.d("pop MFCC", "run: popMFCC3D() -> " + mfcc3d);

                            String model_path = getActivity().getExternalFilesDir("/").getAbsolutePath() + "/" + "fordeaf.tflite";
                            ModelManager modelManager = new ModelManager(model_path);

                            float[][] output = modelManager.run(mfcc3d);
                            for(int i=0; i<10; i++){
                                Log.i("Model outputs", ""+i+output[0][i]);
                            }
//                            class ID:
//                            0 - air_conditioner
//                            1 - car_horn
//                            2 - children_playing
//                            3 - dog_bark
//                            4 - drilling
//                            5 - engine_idling
//                            6 - gun_shot
//                            7 - jackhammer
//                            8 - siren
//                            9 - street_music
                            if(output[0][1] > 0.3){
                                vibrator.vibrate(500);
                            }
                            binding.testText.setText(
                                    "0: "+output[0][0] + "  1: "+output[0][1] +"\n" +
                                    "2: "+output[0][2] + "  1: "+output[0][3] +"\n" +
                                    "4: "+output[0][4] + "  1: "+output[0][5] +"\n" +
                                    "6: "+output[0][6] + "  1: "+output[0][7] +"\n" +
                                    "8: "+output[0][8] + "  1: "+output[0][9] +"\n"
                            );
                            System.out.println("aasdasdadssasasasasadsa");
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}