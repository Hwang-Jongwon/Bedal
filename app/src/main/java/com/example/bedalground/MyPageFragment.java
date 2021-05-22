package com.example.bedalground;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MyPageFragment extends Fragment {
    private View view;
    private Context context;

    private Button btn_logout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        context = getActivity();
        view = inflater.inflate(R.layout.activity_my_page_fragment, container, false);

        

        btn_logout = view.findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(v -> {
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
            
        });

        return view;
    }
}