package com.example.matth.project3;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Activity that holds the fragments used in the application
 * @author Matthias Falk
 */
public class StartActivity extends AppCompatActivity {
    private static final String START_MENU_FRAGMENT = "StartMenuFragment";
    private StartMenuFragment startMenuFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        initializeStartMenuFrag();
    }

    /**
     * Initializes the StartMenuFragment
     */
    private void initializeStartMenuFrag(){
        startMenuFragment = (StartMenuFragment) this.getSupportFragmentManager().findFragmentByTag(START_MENU_FRAGMENT);
        if (startMenuFragment == null){
            startMenuFragment = new StartMenuFragment();
        }
        this.setFragment(startMenuFragment, false, START_MENU_FRAGMENT);
    }

    /**
     * Changes fragment that will be displayed
     * @param fragment - the fragment it will change to
     * @param backStack - true or false
     * @param tag - The tag of the fragment
     */
    public void setFragment(Fragment fragment, boolean backStack, String tag){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragmentHolder,fragment,tag);
        if (backStack){
            ft.addToBackStack(null);
        }
        ft.commit();
    }
}
