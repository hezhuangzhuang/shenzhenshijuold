package com.zxwl.szga.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxwl.szga.R;
import com.zxwl.szga.bean.pliceNumber;
import com.zxwl.szga.util.Utils;

import java.util.List;

public class NumberListAdapter extends BaseQuickAdapter<pliceNumber, BaseViewHolder> {
    private List<pliceNumber> selectlist;

    public NumberListAdapter(int layoutResId, @Nullable List<pliceNumber> data, List<pliceNumber> selectlist) {
        super(layoutResId, data);
        this.selectlist = selectlist;
    }

    @Override
    protected void convert(BaseViewHolder helper, pliceNumber item) {
        if (selectlist.contains(item)) {
            helper.setImageResource(R.id.checkState, R.drawable.ic_select);
        } else {
            helper.setImageResource(R.id.checkState, R.drawable.ic_disselect);
        }

        Bitmap bitmap = Utils.createTextImage(200, 200, 100, item.getUserName().substring(0, 1),"#2387b5","#FFFFFF");

        RequestOptions requestOptions = RequestOptions
                .circleCropTransform()
                .placeholder(new BitmapDrawable(bitmap))
                .error(new BitmapDrawable(bitmap))
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);

        Glide.with(mContext).load(item.getUserPhotoPath())
                .apply(requestOptions)
                .into((ImageView) helper
                        .getView(R.id.head));

        helper.setText(R.id.number_name, item.getUserName());
    }
}
