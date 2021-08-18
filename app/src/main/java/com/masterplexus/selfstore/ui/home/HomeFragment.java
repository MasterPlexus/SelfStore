package com.masterplexus.selfstore.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.masterplexus.selfstore.R;
import com.masterplexus.selfstore.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private static View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    public static void setText(String text) {
        TextView t = (TextView) root.findViewById(R.id.text_home);
        t.setText(text);
    }

    public static boolean installationIsRunning() {
        Button t = (Button) root.findViewById(R.id.showRunner);
        return t.isShown();
    }

    public static void setRunning() {
        Button t = (Button) root.findViewById(R.id.showRunner);
        t.setVisibility(View.VISIBLE);
    }

    public static void unsetRunning() {
        Button t = (Button) root.findViewById(R.id.showRunner);
        t.setVisibility(View.GONE);
    }

    public static void setNewApptoInstall(String newApp) {
        TextView t = (TextView) root.findViewById(R.id.text_buffer);
        t.setVisibility(View.VISIBLE);
        String content =t.getText().toString();
        if (content.isEmpty()) {
            content = newApp;
        } else {
            content = content + ";" + newApp;
        }
        t.setText(content);
    }

    public static String getNextApptoInstall() {
        TextView t = (TextView) root.findViewById(R.id.text_buffer);
        String content =t.getText().toString();
        if (content.isEmpty()) {
            return "";
        } else {
            content ="";
            String[] all = content.split(";");
            for (int xx=1; xx<all.length; xx++) {
                if (content.isEmpty()) {
                    content = all[xx];
                } else {
                    content = content + ";" + all[xx];
                }
            }
            t.setText(content);
            return all[0];
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}