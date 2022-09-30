package com.example.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myapplication.databinding.WelcomePageFirstBinding;

public class WelcomeFirstFragment extends Fragment {

    private WelcomePageFirstBinding binding;
    private Handler handler;
    private Animation animation;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        handler = new Handler();
        animation = AnimationUtils.loadAnimation(inflater.getContext(), R.anim.welcome_page_first);
        binding = WelcomePageFirstBinding.inflate(inflater, container, false);

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.textviewFirst.setAnimation(animation);
        binding.logo.setAnimation(animation);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                NavHostFragment.findNavController(WelcomeFirstFragment.this)
                        .navigate(R.id.action_WelcomePageFirst_to_WelcomePageSecond);
            }
        }, 2000);

    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}