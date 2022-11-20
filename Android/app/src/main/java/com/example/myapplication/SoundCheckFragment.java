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
    private int READ_EXTERNAL_STORAGE;
    private int WRITE_EXTERNAL_STORAGE;
    private int BLUETOOTH_CONNECT;
    private int BLUETOOTH_ADMIN;
    private int ACCESS_FINE_LOCATION;
    private int ACCESS_COARSE_LOCATION;
    private int ACCESS_BACKGROUND_LOCATION;
    private int BLUETOOTH;


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
        READ_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        WRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        BLUETOOTH = ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.BLUETOOTH);
        BLUETOOTH_CONNECT = ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.BLUETOOTH_CONNECT);
        BLUETOOTH_ADMIN = ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.BLUETOOTH_ADMIN);
        ACCESS_FINE_LOCATION = ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        ACCESS_COARSE_LOCATION = ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
        ACCESS_BACKGROUND_LOCATION = ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION);

        if(recordPermission == PackageManager.PERMISSION_DENIED){
            requestPermissions(new String[] {Manifest.permission.RECORD_AUDIO}, 123);
        }
        if(vibPermission == PackageManager.PERMISSION_DENIED){

            requestPermissions(new String[] {Manifest.permission.VIBRATE}, 123);
        }

        if(READ_EXTERNAL_STORAGE == PackageManager.PERMISSION_DENIED){
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
        }

        if(WRITE_EXTERNAL_STORAGE == PackageManager.PERMISSION_DENIED){
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
        }

        if(BLUETOOTH == PackageManager.PERMISSION_DENIED){
            requestPermissions(new String[] {Manifest.permission.BLUETOOTH}, 123);
        }

        if(BLUETOOTH_CONNECT == PackageManager.PERMISSION_DENIED){

            requestPermissions(new String[] {Manifest.permission.BLUETOOTH_CONNECT}, 123);
        }

        if(BLUETOOTH_ADMIN == PackageManager.PERMISSION_DENIED){
            requestPermissions(new String[] {Manifest.permission.BLUETOOTH_ADMIN}, 123);
        }

        if(ACCESS_FINE_LOCATION == PackageManager.PERMISSION_DENIED){

            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        }

        if(ACCESS_COARSE_LOCATION == PackageManager.PERMISSION_DENIED){

            requestPermissions(new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, 123);
        }

        if(ACCESS_BACKGROUND_LOCATION == PackageManager.PERMISSION_DENIED){

            requestPermissions(new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 123);
        }

        binding.checkingMic.setVisibility(View.GONE);
        binding.connectedMic.setVisibility(View.VISIBLE);
        binding.checkText.setText("만약 권한 수락 팝업이 나오지 않는다면, 어플리케이션 설정창에 들어가서 권한을 수락해주세요.");
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}