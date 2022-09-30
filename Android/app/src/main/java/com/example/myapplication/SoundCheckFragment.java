package com.example.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myapplication.databinding.SoundCheckPageBinding;

public class SoundCheckFragment extends Fragment {

    private SoundCheckPageBinding binding;
    private int recordPermission;
    private int vibPermission;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = SoundCheckPageBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SoundCheckFragment.this)
                        .navigate(R.id.action_SoundCheckPage_to_WelcomePageSecond);
            }
        });

        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SoundCheckFragment.this)
                        .navigate(R.id.action_SoundCheckPage_to_AnalysisSoundPage);
            }
        });


        //test 마이크 체크, 연결, 연결해제
        //권한 X
        recordPermission = ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.RECORD_AUDIO);
        vibPermission = ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.VIBRATE);

        if(recordPermission == PackageManager.PERMISSION_DENIED){
            binding.checkText.setText("마이크 권한을 허락해주세요. ❌");

            requestPermissions(new String[] {Manifest.permission.RECORD_AUDIO}, 123);
        } else {

            binding.checkText.setText("마이크가 연결되었습니다. ✔");
        }

        if(vibPermission == PackageManager.PERMISSION_DENIED){

            binding.checkText.setText(binding.checkText.getText() + "\n진동 권한을 허락해주세요. ❌");
            requestPermissions(new String[] {Manifest.permission.VIBRATE}, 123);

            System.out.println("NO");
        }else{
            binding.checkText.setText(binding.checkText.getText() + "\n진동이 연결되었습니다. ✔");
            System.out.println("YES");

        }

        binding.checkingMic.setVisibility(View.GONE);
        binding.connectedMic.setVisibility(View.VISIBLE);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}