package com.nisaelek.servisimnerede;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


class DilekShareRecyclerAdapter extends RecyclerView.Adapter<DilekShareRecyclerAdapter.PostHolder> {
    private ArrayList<String> userEmailList;
    private ArrayList<String> userCommentList;
    private ArrayList<String> userNameList;

    public DilekShareRecyclerAdapter(ArrayList<String> userEmailList, ArrayList<String> userCommentList, ArrayList<String> userImageList) {
        this.userEmailList = userEmailList;
        this.userCommentList = userCommentList;
        this.userNameList = userNameList;
    }
    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_row,parent,false);
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        holder.userEmailText.setText(userEmailList.get(position));
        holder.commentText.setText(userCommentList.get(position));
        holder.commentText.setText(userNameList.get(position));

    }

    @Override
    public int getItemCount() {
        return userEmailList.size();
    }

    public class PostHolder extends RecyclerView.ViewHolder {
        TextView commentText2;
        TextView userEmailText;
        TextView commentText;
        public PostHolder(@NonNull View itemView) {
            super(itemView);
            commentText2 = itemView.findViewById(R.id.recyclerview_rowcomment_text);
            userEmailText = itemView.findViewById(R.id.recyclerview_rowuseremail_text);
            commentText = itemView.findViewById(R.id.recyclerview_rowcomment_text);
        }
    }
}
