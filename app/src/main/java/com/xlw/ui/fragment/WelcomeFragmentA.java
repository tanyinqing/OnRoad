package com.xlw.ui.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xlw.onroad.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WelcomeFragmentA extends Fragment {


    public WelcomeFragmentA() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome_a, container, false);
    }


}
