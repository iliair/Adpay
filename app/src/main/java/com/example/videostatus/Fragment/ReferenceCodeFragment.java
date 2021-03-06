package com.example.videostatus.Fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.videostatus.InterFace.VideoAd;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.example.videostatus.Activity.MainActivity;
import com.example.videostatus.Activity.UploadActivity;
import com.example.videostatus.R;
import com.example.videostatus.Util.API;
import com.example.videostatus.Util.Constant_Api;
import com.example.videostatus.Util.Method;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static android.content.Context.CLIPBOARD_SERVICE;


public class ReferenceCodeFragment extends Fragment {

    private Method method;
    private VideoAd videoAd;
    private String user_code;
    private TextView textView;
    private TextView textView_noData;
    private Menu menu;
    private ProgressBar progressBar;
    private LinearLayout linearLayout_copy;
    private RelativeLayout relativeLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.reference_code_fragment, container, false);

        MainActivity.toolbar.setTitle(getResources().getString(R.string.reference_code));

        videoAd = new VideoAd() {
            @Override
            public void videoAdClick(String type) {
                startActivity(new Intent(getActivity(), UploadActivity.class));
            }
        };
        method = new Method(getActivity(), videoAd);

        progressBar = view.findViewById(R.id.progressbar_reference_code);
        relativeLayout = view.findViewById(R.id.relativeLayout_reference_code_fragment);
        linearLayout_copy = view.findViewById(R.id.linearLayout_copy_reference_code);
        textView = view.findViewById(R.id.textView_reference_code);
        textView_noData = view.findViewById(R.id.textView_noDataFound_reference_code);

        if (Method.isNetworkAvailable(getActivity())) {
            if (method.pref.getBoolean(method.pref_login, false)) {
                relativeLayout.setVisibility(View.VISIBLE);
                profile(method.pref.getString(method.profileId, null));
                textView_noData.setVisibility(View.GONE);
            } else {
                textView_noData.setText(getResources().getString(R.string.you_have_not_login));
                relativeLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }
        } else {
            textView_noData.setText(getResources().getString(R.string.no_data_found));
            relativeLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

        setHasOptionsMenu(true);
        return view;

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        this.menu = menu;
        menu.clear();
        inflater.inflate(R.menu.share_menu, menu);
        MenuItem share = menu.findItem(R.id.action_share);
        if (method.pref.getBoolean(method.pref_login, false)) {
            share.setVisible(true);
        } else {
            share.setVisible(false);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected

            case R.id.action_share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.your_reference_code) + ":-" + user_code
                        + "\n" + "\n" + "https://play.google.com/store/apps/details?id=" + getActivity().getApplication().getPackageName());
                startActivity(Intent.createChooser(shareIntent, "Share link using"));
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    public void profile(String id) {

        progressBar.setVisibility(View.VISIBLE);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "user_profile");
        jsObj.addProperty("user_id", id);
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant_Api.url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                if (getActivity() != null) {

                    Log.d("Response", new String(responseBody));
                    String res = new String(responseBody);

                    try {
                        JSONObject jsonObject = new JSONObject(res);

                        JSONArray jsonArray = jsonObject.getJSONArray(Constant_Api.tag);

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject object = jsonArray.getJSONObject(i);
                            user_code = object.getString("user_code");

                            textView.setText(user_code);

                        }
                        progressBar.setVisibility(View.GONE);

                        linearLayout_copy.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("label", user_code);
                                assert clipboard != null;
                                clipboard.setPrimaryClip(clip);
                                Toast.makeText(getActivity(), getResources().getString(R.string.copy_text), Toast.LENGTH_SHORT).show();
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                    }

                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

}
