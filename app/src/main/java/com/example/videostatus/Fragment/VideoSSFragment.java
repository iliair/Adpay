package com.example.videostatus.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.videostatus.Activity.DownloadStatusDetail;
import com.example.videostatus.Activity.StatusDetail;
import com.example.videostatus.Adapter.ImageSSAdapter;
import com.example.videostatus.InterFace.InterstitialAdView;
import com.example.videostatus.R;
import com.example.videostatus.Util.Constant_Api;
import com.example.videostatus.Util.Events;
import com.example.videostatus.Util.GlobalBus;
import com.example.videostatus.Util.Method;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class VideoSSFragment extends Fragment {

    private Method method;
    private String mainType;
    private ProgressBar progressBar;
    private TextView textView_noData_found;
    private RecyclerView recyclerView;
    private ImageSSAdapter imageSSAdapter;
    private LayoutAnimationController animation;
    private InterstitialAdView interstitialAdView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.category_fragment, container, false);

        GlobalBus.getBus().register(this);

        assert getArguments() != null;
        mainType = getArguments().getString("type");

        assert mainType != null;
        if (mainType.equals("status")) {
            Constant_Api.videoFilesList = new ArrayList<>();
        } else {
            Constant_Api.downloadVideoFilesList = new ArrayList<>();
        }

        interstitialAdView = new InterstitialAdView() {
            @Override
            public void position(int position, String type, String id) {
                if (mainType.equals("status")) {
                    startActivity(new Intent(getActivity(), StatusDetail.class)
                            .putExtra("type", type)
                            .putExtra("position", position));
                } else {
                    startActivity(new Intent(getActivity(), DownloadStatusDetail.class)
                            .putExtra("type", type)
                            .putExtra("position", position));
                }
            }
        };
        method = new Method(getActivity(), interstitialAdView);

        int resId = R.anim.layout_animation_fall_down;
        animation = AnimationUtils.loadLayoutAnimation(getActivity(), resId);

        progressBar = view.findViewById(R.id.progressbar_category);
        textView_noData_found = view.findViewById(R.id.textView_category);
        recyclerView = view.findViewById(R.id.recyclerView_category);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        progressBar.setVisibility(View.GONE);
        if (mainType.equals("status")) {
            String path = Environment.getExternalStorageDirectory() + "/" + Constant_Api.status_path;
            Constant_Api.videoFilesList = getListFiles(new File(path));
        } else {
            Constant_Api.downloadVideoFilesList = getListFiles(new File(Constant_Api.download_status_path));
        }

        if (mainType.equals("status")) {
            if (Constant_Api.videoFilesList.size() == 0) {
                textView_noData_found.setVisibility(View.VISIBLE);
            } else {
                textView_noData_found.setVisibility(View.GONE);
                imageSSAdapter = new ImageSSAdapter(getActivity(), Constant_Api.videoFilesList, interstitialAdView, "video");
            }
        } else {
            if (Constant_Api.downloadVideoFilesList.size() == 0) {
                textView_noData_found.setVisibility(View.VISIBLE);
            } else {
                textView_noData_found.setVisibility(View.GONE);
                imageSSAdapter = new ImageSSAdapter(getActivity(), Constant_Api.downloadVideoFilesList, interstitialAdView, "video");
            }
        }
        recyclerView.setAdapter(imageSSAdapter);
        recyclerView.setLayoutAnimation(animation);

        return view;

    }

    @Subscribe
    public void getVideoNotify(Events.VideoStatusNotify videoStatusNotify) {
        if (imageSSAdapter != null) {
            imageSSAdapter.notifyDataSetChanged();
        }
    }

    private List<File> getListFiles(File parentDir) {
        List<File> inFiles = new ArrayList<>();
        try {
            Queue<File> files = new LinkedList<>(Arrays.asList(parentDir.listFiles()));
            while (!files.isEmpty()) {
                File file = files.remove();
                if (file.isDirectory()) {
                    files.addAll(Arrays.asList(file.listFiles()));
                } else if (file.getName().endsWith(".mp4") || file.getName().endsWith(".gif")) {
                    inFiles.add(file);
                }
            }
        } catch (Exception e) {
            Log.d("error", e.toString());
        }
        return inFiles;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Unregister the registered event.
        GlobalBus.getBus().unregister(this);
    }

}
