package com.ymht.library.picker.address;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.ymht.library.R;

import java.util.ArrayList;

public class AddressPickerRecyclerAdapter extends RecyclerView.Adapter<AddressPickerRecyclerAdapter.AddressPickerRecyclerViewHolder> {

    private ArrayList<String> list;
    private OnItemClickListener listener;
    private View selectView;
    private ImageView selectImageView;
    private int selectPosition = -1;

    private int textSelectedColor = Color.parseColor("#FDD23C");//item的text选中的颜色
    private int textUnselectedColor = Color.parseColor("#343434");//item的text未选中的颜色
    private int imageResourceId = R.drawable.address_select;

    public AddressPickerRecyclerAdapter(ArrayList<String> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public AddressPickerRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_picker_recycler_item, parent, false);
        AddressPickerRecyclerViewHolder viewHolder = new AddressPickerRecyclerViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AddressPickerRecyclerViewHolder holder, int position) {
        holder.mAddressPickerItemText.setText(list.get(position));
        holder.mAddressPickerItemText.setTextColor(createColorStateList(textSelectedColor, textUnselectedColor));
        holder.mAddressPickerItemImage.setImageResource(imageResourceId);
        if (position == selectPosition) {
            holder.itemView.setSelected(true);
            holder.mAddressPickerItemImage.setVisibility(View.VISIBLE);
            selectView = holder.itemView;
            selectImageView = holder.mAddressPickerItemImage;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class AddressPickerRecyclerViewHolder extends RecyclerView.ViewHolder {
        public TextView mAddressPickerItemText;
        private ImageView mAddressPickerItemImage;

        public AddressPickerRecyclerViewHolder(View itemView) {
            super(itemView);
            mAddressPickerItemText = itemView.findViewById(R.id.address_picker_item_text);
            mAddressPickerItemImage = itemView.findViewById(R.id.address_picker_item_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(getLayoutPosition(), mAddressPickerItemText.getText().toString());
                    }
                    if (selectPosition != getLayoutPosition() && selectPosition >= 0) {
                        selectView.setSelected(false);
                        selectImageView.setVisibility(View.GONE);
                    }
                    //设置点击的item选中
                    v.setSelected(true);
                    //设置对号显示
                    mAddressPickerItemImage.setVisibility(View.VISIBLE);
                    selectPosition = getLayoutPosition();
                    selectView = v;
                    selectImageView = mAddressPickerItemImage;
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * 对TextView设置不同状态时其文字颜色。用ColorStateList实现替代selector
     */
    private ColorStateList createColorStateList(int selected, int normal) {
        int[] colors = new int[]{selected, normal};
        int[][] states = new int[2][];
        states[0] = new int[]{android.R.attr.state_selected};
        states[1] = new int[]{-android.R.attr.state_selected};
        ColorStateList colorList = new ColorStateList(states, colors);
        return colorList;
    }

    /**
     * 设置列表中item名字的选中和未选中颜色
     * @param textSelectedColor
     * @param textUnselectedColor
     */
    public void setTextColor(int textSelectedColor, int textUnselectedColor) {
        this.textSelectedColor = textSelectedColor;
        this.textUnselectedColor = textUnselectedColor;
    }

    /**
     * 设置列表中item对号的资源图片
     * @param imageResourceId
     */
    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }
}
