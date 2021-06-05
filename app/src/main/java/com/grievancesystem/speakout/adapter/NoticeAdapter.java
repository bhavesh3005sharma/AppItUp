package com.grievancesystem.speakout.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.grievancesystem.speakout.databinding.LayoutNoticeBinding;
import com.grievancesystem.speakout.models.Notice;

import java.util.ArrayList;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.viewHolder> {

    ArrayList<Notice> notices;
    Context context;

    public NoticeAdapter(ArrayList<Notice> notices, Context context) {
        this.notices = notices;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        LayoutNoticeBinding layoutNoticeBinding = LayoutNoticeBinding.inflate(layoutInflater, parent, false);
        return new viewHolder(layoutNoticeBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Notice notice = notices.get(position);
        holder.layoutNoticeBinding.setNotice(notice);
    }

    @Override
    public int getItemCount() {
        return notices != null ? notices.size() : 0;
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        LayoutNoticeBinding layoutNoticeBinding;

        public viewHolder(@NonNull LayoutNoticeBinding layoutNoticeBinding) {
            super(layoutNoticeBinding.getRoot());
            this.layoutNoticeBinding = layoutNoticeBinding;
        }
    }
}
