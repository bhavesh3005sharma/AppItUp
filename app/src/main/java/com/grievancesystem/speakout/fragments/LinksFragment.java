package com.grievancesystem.speakout.fragments;

import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grievancesystem.speakout.Database.Prefs;
import com.grievancesystem.speakout.R;
import com.grievancesystem.speakout.adapter.UsersAdapter;
import com.grievancesystem.speakout.utility.Helper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class LinksFragment extends Fragment {
    @BindView(R.id.webview)
    WebView webView;
    Unbinder unbinder;
    public LinksFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null)
            unbinder.unbind();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        String url = getArguments().getString("url");
        View view = inflater.inflate(R.layout.fragment_links, container, false);
        unbinder = ButterKnife.bind(this, view);

        //String url=getActivity().getIntent().getStringExtra("url");
        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings=webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(url);

        return view;
    }
}
