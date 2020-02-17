package com.raisac.lostandfound;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LostItemAdapter extends RecyclerView.Adapter<LostItemAdapter.MyViewHolder> {
    Context context;
    ArrayList<Item> mItemArrayList = new ArrayList<>();

    public LostItemAdapter(Context context, ArrayList<Item> itemArrayList) {
        this.context = context;
        mItemArrayList = itemArrayList;
    }

    @NonNull
    @Override
    public LostItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.homepage_model, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LostItemAdapter.MyViewHolder holder, int position) {
        holder.lostImageTitle.setText(mItemArrayList.get(position).getItemName());
        holder.lostImageDescription.setText(mItemArrayList.get(position).getItemDescription());
        Glide.with(context).load(mItemArrayList.get(position).getItemImage()).into(holder.lostImage);
        //holder.lostImage.setImageURI(Uri.parse(mItemArrayList.get(position).getItemImage()));

    }

    @Override
    public int getItemCount() {
        return mItemArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.postTitle)
        TextView lostImageTitle;

        @BindView(R.id.postDescription)
        TextView lostImageDescription;

        @BindView(R.id.postImageview)
        ImageView lostImage;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
