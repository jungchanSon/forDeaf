package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myapplication.databinding.SoundCheckPageBinding;

public class SoundCheckPage extends Fragment {

    private SoundCheckPageBinding binding;

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
                NavHostFragment.findNavController(SoundCheckPage.this)
                        .navigate(R.id.action_SoundCheckPage_to_WelcomePageSecond);
            }
        });

        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SoundCheckPage.this)
                        .navigate(R.id.action_SoundCheckPage_to_AnalysisSoundPage);
            }
        });


        //test 마이크 체크, 연결, 연결해제
        binding.checkingMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.checkingMic.setVisibility(View.GONE);
                binding.connectedMic.setVisibility(View.VISIBLE);
            }
        });

        binding.connectedMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.connectedMic.setVisibility(View.GONE);
                binding.disconnectedMic.setVisibility(View.VISIBLE);
            }
        });

        binding.disconnectedMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.disconnectedMic.setVisibility(View.GONE);
                binding.checkingMic.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}