package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myapplication.databinding.WelcomePageSecondBinding;


public class WelcomeSecondFragment extends Fragment {

    private WelcomePageSecondBinding binding;




    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = WelcomePageSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.btnNext.setOnClickListener(view1 -> NavHostFragment.findNavController(WelcomeSecondFragment.this)
                .navigate(R.id.action_WelcomePageSecond_to_SoundCheckPage));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }





}