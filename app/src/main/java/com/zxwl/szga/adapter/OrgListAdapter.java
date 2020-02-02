package com.zxwl.szga.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxwl.szga.R;
import com.zxwl.szga.bean.pliceOrg;
import com.zxwl.szga.util.OrgUtils;

import java.util.List;

public class OrgListAdapter extends BaseQuickAdapter<pliceOrg, BaseViewHolder> {
    private List<pliceOrg> data;
    private int pos = -10;

    public OrgListAdapter(int layoutResId, @Nullable List<pliceOrg> data) {
        super(layoutResId, data);
        this.data = data;
    }

    public void setCurrPos(int pos){
        this.pos = pos;
    }

    @Override
    protected void convert(BaseViewHolder helper, pliceOrg item) {

        int level = OrgUtils.getLevel(data, item);
        View holder = helper.getView(R.id.holer);
        ViewGroup.LayoutParams layoutParams = holder.getLayoutParams();
        layoutParams.width = 40 * level;
        holder.setLayoutParams(layoutParams);

        if (pos == helper.getPosition()){
            helper.setTextColor(R.id.org_name, Color.parseColor("#234CB5"));
            helper.setVisible(R.id.driver_choose, true);
        }else {
            helper.setTextColor(R.id.org_name, Color.parseColor("#ff999999"));
            helper.setVisible(R.id.driver_choose, false);
        }
        helper.setText(R.id.org_name, item.getName());

    }
}
