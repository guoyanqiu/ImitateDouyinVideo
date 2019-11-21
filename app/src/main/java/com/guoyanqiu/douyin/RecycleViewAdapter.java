package com.guoyanqiu.douyin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;
/**
 *
 */
public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ItemViewHolder> {

    private List<Integer> data = new ArrayList<>();
    public RecycleViewAdapter() {
        data.add(R.drawable.one);
        data.add(R.drawable.two);
        data.add(R.drawable.three);
        data.add(R.drawable.four);
        data.add(R.drawable.five);
        data.add(R.drawable.six);
        data.add(R.drawable.seven);
        data.add(R.drawable.one);
        data.add(R.drawable.two);
        data.add(R.drawable.three);
        data.add(R.drawable.four);
        data.add(R.drawable.five);
        data.add(R.drawable.six);
        data.add(R.drawable.seven);
    }


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.items, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        final int imgId = data.get(position);
        holder.txt.setText(""+position);
        Glide.with(holder.image.getContext()).load(holder.image.getResources().getDrawable(imgId)).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    static class ItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView txt;
        private ItemViewHolder(final View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.img);
            txt = itemView.findViewById(R.id.pageNum);

        }
    }

}
