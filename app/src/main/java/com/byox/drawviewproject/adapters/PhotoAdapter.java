package com.byox.drawviewproject.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.byox.drawviewproject.R;
import com.byox.drawviewproject.listeners.OnClickListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by Ing. Oscar G. Medina Cruz on 05/09/2016.
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    // INTERFACE
    private OnClickListener mOnClickListener;

    // VARS
    private List<File> mFileList;
    private Context mContext;
    private int mItemOriginalHeight = -1;
//    boolean isVertical;

    public PhotoAdapter(List<File> fileList, OnClickListener onClickListener) {
        mFileList = fileList;
        mOnClickListener = onClickListener;
//        this.isVertical = isVertical;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView imageView;

        public ViewHolder(View v) {
            super(v);
            cardView = (CardView) v.findViewById(R.id.cv_photo_item);
            imageView = (ImageView) v.findViewById(R.id.iv_photo_item);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_photo, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Picasso.with(mContext).load(mFileList.get(position)).memoryPolicy(MemoryPolicy.NO_CACHE)
                .placeholder(R.color.colorBlackSemitrans).fit().centerCrop().into(holder.imageView);

        if (mOnClickListener != null) {
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnClickListener.onItemClickListener(view, mFileList.get(position), position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mFileList.size();
    }
}
