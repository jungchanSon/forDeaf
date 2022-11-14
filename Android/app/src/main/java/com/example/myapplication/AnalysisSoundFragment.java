package com.example.myapplication;

import static org.apache.commons.math3.util.Precision.round;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myapplication.manager.MFCCManager;
import com.example.myapplication.databinding.AnalysisSoundPageBinding;
import com.example.myapplication.manager.ModelManager;
import com.example.myapplication.manager.RecordManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AnalysisSoundFragment extends Fragment{

    private AnalysisSoundPageBinding binding;
    private Vibrator vibrator;
    private Handler handler;
    private BluetoothDevice bluetoothDevice;
    private Set<BluetoothDevice> devices;
    private BluetoothGatt socket;
    private BluetoothGattCharacteristic pwmChar = null;
    private BluetoothGattCharacteristic pwmChar1 = null;
    private MFCCManager mfccManager;
    private ModelManager modelManager;
    private RecordManager recordManager;

    private int audioDuration = 1000;
    private boolean isRecord = true;
    String model_path = "model.tflite";

    private double threshhold_low = 0.3;
    private double threshhold_high = 0.5;

    private String[] labels_test = {
            "car_horn",
            "siren",
            "Other"
    };



    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if( status == BluetoothGatt.GATT_FAILURE ) {
                gatt.disconnect();
                gatt.close();
                return;
            }
            else if (newState == BluetoothProfile.STATE_CONNECTED) {
                List<BluetoothGattService> services = gatt.getServices();
                socket.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            }
        }


        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if( status == BluetoothGatt.GATT_SUCCESS)
            {
                List<BluetoothGattService> services = gatt.getServices();
                BluetoothGattService bluetoothGattService = services.get(4);

                List<BluetoothGattCharacteristic> characteristics = bluetoothGattService.getCharacteristics();

                //처음 시작시, 확인용 진동
                if(characteristics.get(2) != null){
                    pwmChar= characteristics.get(2);

                    bluevib(gatt, (byte)100);
                }
                if(characteristics.get(3) != null){
                    pwmChar1= characteristics.get(3);

                    bluevib1(gatt, (byte)100);
                }



                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        byte[] data = new byte[1];
                        data[0] = (byte)0;

                        pwmChar.setValue(data);
                        gatt.writeCharacteristic(pwmChar);
                        pwmChar1.setValue(data);
                        gatt.writeCharacteristic(pwmChar1);
                    }
                }, 500);


                for (BluetoothGattService service : services) {
                    // "Found service : " + service.getUuid()
                    for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                        //"Found characteristic : " + characteristic.getUuid()
                        if( hasProperty(characteristic, BluetoothGattCharacteristic.PROPERTY_READ))
                        {
                            // "Read characteristic : " + characteristic.getUuid());

                            gatt.readCharacteristic(characteristic);
                        }

                        if( hasProperty(characteristic, BluetoothGattCharacteristic.PROPERTY_NOTIFY)) {
                            // "Register notification for characteristic : " + characteristic.getUuid());

                            gatt.setCharacteristicNotification(characteristic, true);

                        }
                    }
                }
            }
        }

        private boolean hasProperty(BluetoothGattCharacteristic characteristic, int property) {
            int prop = characteristic.getProperties() & property;
            return prop == property;
        }


        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
        }
    };

    private void bluevib(BluetoothGatt gatt, byte i) {
        byte[] data = new byte[1];
        data[0] = i;

        pwmChar.setValue(data);
        gatt.writeCharacteristic(pwmChar);
    }
    private void bluevib1(BluetoothGatt gatt, byte i) {
        byte[] data = new byte[1];
        data[0] = i;

        pwmChar1.setValue(data);
        gatt.writeCharacteristic(pwmChar1);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
        ){

            handler = new Handler();
            binding = AnalysisSoundPageBinding.inflate(inflater, container, false);

            vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            mfccManager = new MFCCManager();
            recordManager = new RecordManager(getActivity());

        try {
            modelManager = new ModelManager(AnalysisSoundFragment.this.getActivity(), model_path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //블루투스 선택창
        //      test
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        devices = defaultAdapter.getBondedDevices();
        int size = devices.size();

        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        List<String> list = new ArrayList<>();

        // 모든 디바이스의 이름을 리스트에 추가

        for(BluetoothDevice bluetoothDevice : devices) {
            list.add(bluetoothDevice.getName());
        }
        // List를 CharSequence 배열로 변경
        final CharSequence[] charSequences = list.toArray(new CharSequence[list.size()]);
        list.toArray(new CharSequence[list.size()]);

        // 해당 아이템을 눌렀을 때 호출 되는 이벤트 리스너
        builder.setItems(charSequences, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 해당 디바이스와 연결하는 함수 호출
                try {
                    connectDevice(charSequences[which].toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();


        repeatRecord(audioDuration);
        binding.lowBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                binding.lowText.setText("약한 임계점 : " + seekBar.getProgress());
                threshhold_low = (double)seekBar.getProgress()/10.0;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        binding.highBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                binding.highText.setText("강한 임계점 : " + seekBar.getProgress());
                threshhold_high = (double)seekBar.getProgress()/10.0;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
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
                            //
                            String audioFileName = recordManager.getAudioFileName();
                            //아래 지우기
//                            audioFileName = getActivity().getExternalFilesDir("/").getAbsolutePath()+"/" + "AI_carhorn_testfile_1.wav";
                            mfccManager.addMFCC(audioFileName);

                            float [][][][] mfcc3d = mfccManager.popMFCC3D();
                            Log.d("pop MFCC", "run: popMFCC3D() -> " + mfcc3d);


                            float[][] output = modelManager.run(mfcc3d);
                            for(int i=0; i<output[0].length; i++){
                                Log.i("Model outputs", ""+i+output[0][i]);
                            }

                            if(output[0][0] > threshhold_high){
                                vibrator.vibrate(500);

                                if(pwmChar != null){
                                    bluevib(socket, (byte) 200);

                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            bluevib(socket, (byte) 0);
                                        }
                                    }, 500);
                                }
                                if(pwmChar1 != null){
                                    bluevib1(socket, (byte) 200);

                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            bluevib1(socket, (byte) 0);
                                        }
                                    }, 500);
                                }
                            }
                            else if(output[0][0] > threshhold_low){
                                vibrator.vibrate(300);

                                if(pwmChar != null){
                                    bluevib(socket, (byte) 100);

                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            bluevib(socket, (byte) 0);
                                        }
                                    }, 500);
                                }
                                if(pwmChar1 != null){
                                    bluevib1(socket, (byte) 100);

                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            bluevib1(socket, (byte) 0);
                                        }
                                    }, 500);
                                }
                            }
                            else if(output[0][1] > threshhold_high){
                                vibrator.vibrate(500);

                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        vibrator.vibrate(500);
                                    }

                                }, 700);

                                if(pwmChar != null){
                                    bluevib(socket, (byte) 200);
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            bluevib(socket, (byte) 0);
                                        }

                                    }, 300);
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            bluevib(socket, (byte) 200);
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    bluevib(socket, (byte) 0);
                                                }

                                            }, 300);
                                        }
                                    }, 600);
                                }
                                if(pwmChar1 != null){
                                    bluevib1(socket, (byte) 200);
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            bluevib1(socket, (byte) 0);
                                        }

                                    }, 300);
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            bluevib1(socket, (byte) 200);
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    bluevib1(socket, (byte) 0);
                                                }

                                            }, 300);
                                        }
                                    }, 600);
                                }
                            }

                            else if(output[0][1] > threshhold_low){
                                vibrator.vibrate(300);

                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        vibrator.vibrate(300);
                                    }

                                }, 500);

                                if(pwmChar != null){
                                    bluevib(socket, (byte) 100);
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            bluevib(socket, (byte) 0);
                                        }

                                    }, 300);
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            bluevib(socket, (byte) 100);
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    bluevib(socket, (byte) 0);
                                                }

                                            }, 300);
                                        }
                                    }, 600);
                                }
                                if(pwmChar1 != null){
                                    bluevib1(socket, (byte) 100);
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            bluevib1(socket, (byte) 0);
                                        }

                                    }, 300);
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            bluevib1(socket, (byte) 100);
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    bluevib1(socket, (byte) 0);
                                                }

                                            }, 300);
                                        }
                                    }, 600);
                                }
                            }


                            binding.testText.setText(
                                    labels_test[0]+": "+round(output[0][0], 3)+"\n" + labels_test[1]+"   : "+round(output[0][1], 3) +"\n" +
                                    labels_test[2]+": "+round(output[0][2], 3)
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

                            new File(audioFileName).delete();
                            Log.d("delete", "run: delete file");

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
    public void connectDevice(String deviceName) throws IOException {
        for(BluetoothDevice tempDevice : devices) {
            if(deviceName.equals(tempDevice.getName())) {
                binding.testText.setText(tempDevice.getName() + tempDevice);
                bluetoothDevice = tempDevice;
                break;
            }
        }

        socket = bluetoothDevice.connectGatt(this.getContext(), false, mGattCallback);
        socket.connect();
    }
}