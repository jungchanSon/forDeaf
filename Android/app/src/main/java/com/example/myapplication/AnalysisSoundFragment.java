package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myapplication.databinding.AnalysisSoundPageBinding;


public class AnalysisSoundFragment extends Fragment {

    private AnalysisSoundPageBinding binding;
    private Vibrator vibrator;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = AnalysisSoundPageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        binding.btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(AnalysisSoundFragment.this)
                        .navigate(R.id.action_AnalysisSoundPage_to_SoundCheckPage);
            }
        });

//        binding.btnNext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NavHostFragment.findNavController(SoundCheckPage.this)
//                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
//            }
//        });

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}