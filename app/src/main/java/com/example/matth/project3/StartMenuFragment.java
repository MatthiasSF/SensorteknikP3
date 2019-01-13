package com.example.matth.project3;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Fragment that displays the start menu
 * @author Matthias Falk
 */
public class StartMenuFragment extends Fragment {
    private View view;
    private Button scB;
    private Button syB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_start_menu, container, false);
        initialize();
        return view;
    }

    /**
     * Initializes all of the component in the fragment
     */
    private void initialize() {
        scB = view.findViewById(R.id.startMenuScB);
        syB = view.findViewById(R.id.startMenuSyB);
        scB.setOnClickListener(new ButtonListener());
        syB.setOnClickListener(new ButtonListener());
    }

    /**
     * Inner class that implements the OnClickListener interface.
     * Changes the fragment depending on the users choice
     */
    private class ButtonListener implements View.OnClickListener{
        private StartActivity startActivity = (StartActivity) getActivity();
        private static final String SCREENBRIGHTNESSFRAGMENT = "ScreenBrightnessFragment";
        private static final String SYSTEMBRIGHTNESSFRAGMENT = "SystemBrightnessFragment";
        @Override
        public void onClick(View v) {
            if (v.getId() == scB.getId()){
                startActivity.setFragment(new ScreenBrightnessFragment(), true, SCREENBRIGHTNESSFRAGMENT);
            }
            else if (v.getId() == syB.getId()){
                startActivity.setFragment(new SystemBrightnessFragment(), true, SYSTEMBRIGHTNESSFRAGMENT);
            }
        }
    }
}
