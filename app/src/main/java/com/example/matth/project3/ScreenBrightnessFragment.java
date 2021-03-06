package com.example.matth.project3;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Fragment that changes the window brightness
 * @author Matthias Falk
 */
public class ScreenBrightnessFragment extends Fragment {
    private View view;
    private SensorManager sensorManager;
    private Sensor lightSensor;
    boolean isListenerActive = false;
    private Window window;
    private Button buttonDark;
    private Button buttonLo;
    private Button buttonBright;
    private Button buttonVBright;
    private Button buttonBlinding;
    private TextView tvPreset;
    private float choice = -1f;
    private SensorListener sensorListener;
    private CameraCharacteristics parameters;
    private String cameraID;
    private CameraManager cameraManager;
    private Sensor proximitySensor;
    private boolean isFlashLightOn;
    private boolean activateFlashLight = false;
    private float lastLight = 0f;
    private float light = 0f;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_screen_brightness, container, false);
        initialize();
        setSensor();
        return view;
    }

    /**
     * Initializes all of the components used in the application
     */
    private void initialize(){
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        cameraManager = (CameraManager)getActivity().getSystemService(Context.CAMERA_SERVICE);
        buttonDark = view.findViewById(R.id.scB_ButtonDark);
        buttonLo = view.findViewById(R.id.syB_ButtonLowLight);
        buttonBright = view.findViewById(R.id.scB_ButtonBright);
        buttonVBright = view.findViewById(R.id.scB_ButtonVBright);
        buttonBlinding = view.findViewById(R.id.scB_ButtonBlinding);
        tvPreset = view.findViewById(R.id.scB_Preset);
        buttonDark.setOnClickListener(new ButtonListener());
        buttonLo.setOnClickListener(new ButtonListener());
        buttonBright.setOnClickListener(new ButtonListener());
        buttonVBright.setOnClickListener(new ButtonListener());
        buttonBlinding.setOnClickListener(new ButtonListener());
        try {cameraID = cameraManager.getCameraIdList()[0];
            parameters = cameraManager.getCameraCharacteristics(cameraID);}
        catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets up all of the sensors that is being used
     */
    private void setSensor(){
        if (sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            sensorListener = new SensorListener();
            sensorManager.registerListener(sensorListener,lightSensor,SensorManager.SENSOR_DELAY_NORMAL);
            isListenerActive = true;
            Toast.makeText(getContext(),"Listener registered", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(getContext(), "Warning: The light sensor is missing on your phone", Toast.LENGTH_LONG).show();
        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)!= null){
            proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            sensorManager.registerListener(sensorListener, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
            isListenerActive = true;
        }
        else {
            Toast.makeText(getContext(), "Warning: The proximity sensor is missing on your phone", Toast.LENGTH_LONG).show();
        }
        initScreenBrightness();
    }

    /**
     * Turns the flashlight on
     */
    private void turnFlashLightOn(){
        if (activateFlashLight) {
            if (parameters.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)) {
                try {
                    cameraManager.setTorchMode(cameraID, true);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
                isFlashLightOn = true;
            }
        }
    }

    /**
     * Turns the flashlight off
     */
    private void turnFlashLightOff(){
        if (parameters.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)){
            try {
                cameraManager.setTorchMode(cameraID, false);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
            isFlashLightOn = false;
        }
    }

    /**
     * Gets the window of the activity
     */
    private void initScreenBrightness() {
        window = getActivity().getWindow();
    }

    /**
     * Sets the textview
     * @param t - the text that will be displayed
     */
    private void setText(String t){
        tvPreset.setText(getText(R.string.current_preset) + " " + t);
    }

    /**
     * Inner class that implements the OnClickListener interface
     * Gives an value that will be multiplied with the value picked up by the lightsensor
     */
    private class ButtonListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            if (v.getId() == buttonDark.getId()){
                choice = 0.25f;
                setText("Dark");
            }
            if (v.getId() == buttonLo.getId()){
                choice = 2f;
                setText("Low light");
            }
            if (v.getId() == buttonBright.getId()){
                choice = 5f;
                setText("Bright");
            }
            if (v.getId() == buttonVBright.getId()){
                choice = 10f;
                setText("Very bright");
            }
            if (v.getId() == buttonBlinding.getId()){
                choice = 20f;
                setText("Blinding");
            }
            changeScreenBrightness(1/ light);
        }
    }

    /**
     * Inner class that implements the SensorEventListener
     * Listens for changes in the active sensors
     */
    private class SensorListener implements SensorEventListener{

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                light = event.values[0];
                float differenceInLight = 0f;
                if (lastLight > light){
                    differenceInLight = lastLight - light;
                }
                if (light > lastLight){
                    differenceInLight = light - lastLight;
                }
                if (light > 0 && light < 100 && differenceInLight > 4) {
                    changeScreenBrightness(1 / light);
                    lastLight = light;
                }
            }else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY){
                float distanceFromPhone = event.values[0];
                if(distanceFromPhone < proximitySensor.getMaximumRange()) {
                    if(!isFlashLightOn){
                        turnFlashLightOn();
                    }
                }
                else {
                    if(isFlashLightOn) {
                        turnFlashLightOff();
                    }
                }
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    }

    /**
     * Changes the windowbrightness
     * @param v - value that is given by the lightsensor
     */
    private void changeScreenBrightness(float v) {
        WindowManager.LayoutParams mLayoutParams = window.getAttributes();
        if (choice != -1){
            mLayoutParams.screenBrightness = v * choice;
        }
        else {
            mLayoutParams.screenBrightness = v;
        }
        window.setAttributes(mLayoutParams);
        if (v > 0.03){
            activateFlashLight = true;
        }
        else{
            activateFlashLight = false;
        }
    }

    /**
     * Registers the listener
     */
    @Override
    public void onResume() {
        super.onResume();
        if(!isListenerActive) {
            sensorManager.registerListener(sensorListener, lightSensor,SensorManager.SENSOR_DELAY_NORMAL);
            isListenerActive = true;
            Toast.makeText(getContext(),"Listener registered", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Deregisters the listener
     */
    @Override
    public void onPause() {
        super.onPause();
        if(isListenerActive) {
            sensorManager.unregisterListener(sensorListener);
            isListenerActive = false;
            Toast.makeText(getContext(),"Listener unregistered", Toast.LENGTH_LONG).show();
        }
    }
}