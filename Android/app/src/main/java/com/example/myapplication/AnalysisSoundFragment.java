package com.example.myapplication;

import static org.apache.commons.math3.util.Precision.round;

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
    private Handler handler;

    private MFCCManager mfccManager;
    private ModelManager modelManager;
    private RecordManager recordManager;

    private int audioDuration = 1000;
    private boolean isRecord = true;
    private String[] labels_test = {
            "air_conditioner",
            "car_horn",
            "children_playing",
            "dog_bark",
            "drilling",
            "engine_idling",
            "gun_shot",
            "jackhammer",
            "siren",
            "street_music"
    };

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

            handler = new Handler();
            binding = AnalysisSoundPageBinding.inflate(inflater, container, false);

        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        mfccManager = new MFCCManager();
        recordManager = new RecordManager(getActivity());

        //Apk 할때
//      String model_path = "file:///android_asset/fordeaf.tflite";
        String model_path = getActivity().getExternalFilesDir("/").getAbsolutePath() + "/" + "fordeaf.tflite";
        modelManager = new ModelManager(model_path);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


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
                            recordManager.stopRecord();

                            //테스트용
                            String audioFileName = recordManager.getAudioFileName();
                            mfccManager.addMFCC(audioFileName);

                            float [][][] mfcc3d = mfccManager.popMFCC3D();
                            Log.d("pop MFCC", "run: popMFCC3D() -> " + mfcc3d);


                            float[][] output = modelManager.run(mfcc3d);
                            for(int i=0; i<10; i++){
                                Log.i("Model outputs", ""+i+output[0][i]);
                            }

                            if(output[0][1] > 0.5){
                                vibrator.vibrate(500);
                            }
                            binding.testText.setText(
                                    labels_test[0]+": "+round(output[0][0], 3) + labels_test[1]+"   : "+round(output[0][1], 3) +"\n" +
                                    labels_test[2]+": "+round(output[0][2], 3) + labels_test[3]+"   : "+round(output[0][3], 3) +"\n" +
                                    labels_test[4]+": "+round(output[0][4], 3) + labels_test[5]+"   : "+round(output[0][5], 3) +"\n" +
                                    labels_test[6]+": "+round(output[0][6], 3) + labels_test[7]+"   : "+round(output[0][7], 3) +"\n" +
                                    labels_test[8]+": "+round(output[0][8], 3) + labels_test[9]+"   : "+round(output[0][9], 3) +"\n"
                            );

                            //test용
                            int maxI = 0;
                            float maxV = output[0][1];

                            for(int i=0; i<output[0].length; i++){
                                if (maxV < output[0][i]){
                                    maxV = output[0][i];
                                    maxI = i;
                                }
                            }

                            binding.textMostValueTest.setText(
                                    labels_test[maxI]+"\n" + round(maxV, 3)
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}