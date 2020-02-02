package com.zxwl.szga.view;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.szga.Constant.RequestUrls;
import com.zxwl.szga.R;
import com.zxwl.szga.adapter.NumberListAdapter;
import com.zxwl.szga.adapter.OrgListAdapter;
import com.zxwl.szga.bean.pliceNumber;
import com.zxwl.szga.bean.pliceOrg;
import com.zxwl.szga.util.MeetingControl;
import com.zxwl.szga.util.NetWorkUtils;
import com.zxwl.szga.util.OrgUtils;
import com.zxwl.szga.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PopAddMeetingNumber {
    private Gson gson;
    private String currsmcConfId;
    private Activity context;
    private PopupWindow popupWindow;
    private RecyclerView org_list;
    private RecyclerView number_list;
    private OrgListAdapter org_adapter;
    private NumberListAdapter number_adapter;
    private List<pliceOrg> data_org;
    private List<pliceNumber> data_number;
    private List<pliceNumber> selectlist;

    private TextView select_count;

    public PopAddMeetingNumber(Activity context, String currsmcConfId) {
        gson = new Gson();
        this.currsmcConfId = currsmcConfId;
        this.context = context;
        selectlist = new ArrayList<>();
        data_org = new ArrayList<>();
        data_number = new ArrayList<>();

        initPop();
    }

    public void show() {
        Map<String, String> params = new HashMap<>();
        params.put("deptId", "");
        NetWorkUtils.getServerforResult(RequestUrls.getOrg, params, new RxSubscriber<BaseData>() {
            @Override
            public void onSuccess(BaseData baseData) {
//                String str = "{\"code\":0,\"msg\":\"success\",\"data\":[{\"id\":3,\"name\":\"c分局\",\"parentId\":1},{\"id\":4,\"name\":\"d分局\",\"parentId\":1}]}";
//                baseData = gson.fromJson(str, BaseData.class);

                data_org.clear();
                if (baseData.getCode() == 0) {
                    List<LinkedTreeMap> dataList = (List<LinkedTreeMap>) baseData.getData();
                    for (LinkedTreeMap temp : dataList) {
//                        data_org.add(gson.fromJson(gson.toJson(temp), pliceOrg.class));
                        data_org.add(Utils.jsonOBJ_Resolve(temp,pliceOrg.class));
                    }
                    org_adapter.notifyDataSetChanged();
                } else {
                    Utils.showToast("子列表获取失败");
                }

                popupWindow.showAtLocation(context.getWindow().getDecorView(), Gravity.RIGHT, 0, 0);
            }

            @Override
            protected void onError(ResponeThrowable responeThrowable) {
                Utils.showToast("子列表获取失败");
            }
        });
    }

    /**
     * 初始化popwindow
     */
    public void initPop() {
        //初始化pop
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vPopupWindow = inflater.inflate(R.layout.popupwindow_addnumber, null, false);
        select_count = vPopupWindow.findViewById(R.id.select_count);
        final EditText search = vPopupWindow.findViewById(R.id.search);
        TextView cancel = vPopupWindow.findViewById(R.id.cancel);
        TextView confirm = vPopupWindow.findViewById(R.id.confirm);
        TextView selectAll = vPopupWindow.findViewById(R.id.selectAll);

        //初始化点击事件
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.cancel:
                        popupWindow.dismiss();
                        break;

                    case R.id.confirm:
                        if (selectlist == null || selectlist.size() == 0) {
                            Utils.showToast("新增与会人不能为空");
                            return;
                        }
                        String siteUrls = "";
                        for (pliceNumber temp : selectlist) {
                            siteUrls = siteUrls + "," + temp.getId();
                        }
                        MeetingControl.add(currsmcConfId, siteUrls.substring(1), new RxSubscriber<BaseData>() {
//                        MeetingControl.add(currsmcConfId, "123", new RxSubscriber<BaseData>() {
                            @Override
                            public void onSuccess(BaseData baseData) {
                                if (baseData.getCode() == 0) {
                                    Utils.showToast("成功添加与会人");
                                    popupWindow.dismiss();
                                } else {
                                    if (TextUtils.isEmpty(baseData.getMsg())){
                                        Utils.showToast("添加与会人失败，服务器内部错误");
                                    }else {
                                        Utils.showToast(baseData.getMsg());
                                    }
                                }
                            }

                            @Override
                            protected void onError(ResponeThrowable responeThrowable) {
                                Utils.showToast("添加与会人失败，请稍后重试");
                            }
                        });
                        break;

                    case R.id.selectAll:
                        boolean selectAll = false;
                        TextView tvselect = (TextView) v;
                        //判断需要进行哪种操作
                        for (pliceNumber temp : data_number) {
                            if (selectlist.indexOf(temp) == -1) {
                                selectAll = true;
                                break;
                            }
                        }
                        if (selectAll) {
                            tvselect.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_select),null,null,null);
                            //添加全部
                            for (pliceNumber temp : data_number) {
                                if (selectlist.indexOf(temp) == -1) {
                                    selectlist.add(temp);
                                }
                            }
                        } else {
                            tvselect.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_disselect),null,null,null);
                            //移除全部
                            for (pliceNumber temp : data_number) {
                                selectlist.remove(temp);
                            }
                        }
                        setCount(selectlist.size());
//                        select_count.setText("已选：" + selectlist.size());
                        number_adapter.notifyDataSetChanged();
                        break;
                }
            }
        };
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (v.getId()) {
                    case R.id.search:
                        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                            String keyWord = v.getText().toString().trim();
                            Map<String, String> params = new HashMap<>();
                            params.put("keyWord", keyWord);
                            NetWorkUtils.getServerforResult(RequestUrls.search, params, new RxSubscriber<BaseData>() {
                                @Override
                                public void onSuccess(BaseData baseData) {
                                    if (baseData.getCode() == 0) {
                                        data_number.clear();
                                        List<LinkedTreeMap> dataList = (List<LinkedTreeMap>) baseData.getData();
                                        for (LinkedTreeMap temp : dataList) {
//                                            pliceNumber number = gson.fromJson(gson.toJson(temp), pliceNumber.class);
                                            pliceNumber number = Utils.jsonOBJ_Resolve(temp,pliceNumber.class);
                                            data_number.add(number);
                                        }
                                        number_adapter.notifyDataSetChanged();
                                    } else {
                                        Utils.showToast("搜索数据失败，可能不存在此类信息");
                                    }
                                }

                                @Override
                                protected void onError(ResponeThrowable responeThrowable) {
                                    Utils.showToast("搜索数据失败，请稍后重试");
                                }
                            });
                        }
                        break;
                }
                return false;
            }
        });

        cancel.setOnClickListener(clickListener);
        confirm.setOnClickListener(clickListener);
        selectAll.setOnClickListener(clickListener);


        //初始化列表显示
        initOrgList(vPopupWindow);
        initNumberList(vPopupWindow);

//        data_org.clear();
//        String str = "{\"code\":0,\"msg\":\"success\",\"data\":[{\"id\":2,\"name\":\"A分局\",\"parentId\":1},{\"id\":3,\"name\":\"B分局\",\"parentId\":1}]}";
//        BaseData baseData = gson.fromJson(str, BaseData.class);
//        List<LinkedTreeMap> dataList = (List<LinkedTreeMap>) baseData.getData();
//        for (LinkedTreeMap temp : dataList) {
//            data_org.add(gson.fromJson(gson.toJson(temp), pliceOrg.class));
//        }
//        org_adapter.notifyDataSetChanged();

        popupWindow = new PopupWindow(vPopupWindow, Utils.getScreenWidht() / 3 * 2, ActionBar.LayoutParams.MATCH_PARENT, true);
//        context.getResources().setDimensionPixelSize(R.dimen.orglist_width);
    }

    /**
     * 初始化组织列表
     *
     * @param vPopupWindow
     */
    private void initOrgList(View vPopupWindow) {
        org_list = vPopupWindow.findViewById(R.id.org_list);
        org_list.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        org_adapter = new OrgListAdapter(R.layout.list_org_item, data_org);

        org_adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final BaseQuickAdapter adapter, final View view, final int position) {
                //传递当前行数
                org_adapter.setCurrPos(position);

                //查询子列表
                Map<String, String> params = new HashMap<>();
                params.put("deptId", data_org.get(position).getId());
                NetWorkUtils.getServerforResult(RequestUrls.getOrg, params, new RxSubscriber<BaseData>() {
                    @Override
                    public void onSuccess(BaseData baseData) {
//                        String str = "{\"code\":0,\"msg\":\"success\",\"data\":[{\"id\":3,\"name\":\"c分局\",\"parentId\":1},{\"id\":4,\"name\":\"d分局\",\"parentId\":1}]}";
//                        baseData = gson.fromJson(str, BaseData.class);

                        if (baseData.getCode() == 0) {
                            List<LinkedTreeMap> dataList = (List<LinkedTreeMap>) baseData.getData();
                            for (LinkedTreeMap temp : dataList) {
//                                pliceOrg org = gson.fromJson(gson.toJson(temp), pliceOrg.class);
                                pliceOrg org = Utils.jsonOBJ_Resolve(temp,pliceOrg.class);
                                if (data_org.contains(org)) {
                                    OrgUtils.delete(data_org, org);
                                } else {
                                    data_org.add(position + 1, org);
                                }
                            }
                        }
                        org_adapter.notifyDataSetChanged();
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        Utils.showToast("子列表获取失败");
                    }
                });

                //根据当前的结构更新成员信息
                Map<String, String> params2 = new HashMap<>();
                params2.put("deptId", String.valueOf(data_org.get(position).getId()));
                NetWorkUtils.getServerforResult(RequestUrls.getNum, params2, new RxSubscriber<BaseData>() {
                    @Override
                    public void onSuccess(BaseData baseData) {
                        if (baseData.getCode() == 0) {
                            data_number.clear();
                            List<LinkedTreeMap> dataList = (List<LinkedTreeMap>) baseData.getData();
                            if (dataList == null) {
                                number_adapter.notifyDataSetChanged();
                                return;
                            }
                            for (LinkedTreeMap temp : dataList) {
//                                pliceNumber number = gson.fromJson(gson.toJson(temp), pliceNumber.class);
                                pliceNumber number = Utils.jsonOBJ_Resolve(temp,pliceNumber.class);
                                data_number.add(number);
                            }
                            number_adapter.notifyDataSetChanged();
                        } else {
                            Utils.showToast("查询警员信息失败，返回参数有误");
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        Utils.showToast("查询警员信息失败，请稍后重试");
                    }
                });
            }
        });

        org_list.setAdapter(org_adapter);
    }

    private void initNumberList(View vPopupWindow) {
        number_list = vPopupWindow.findViewById(R.id.number_list);
        number_list.setLayoutManager(new LinearLayoutManager(context));
        number_adapter = new NumberListAdapter(R.layout.list_number_item, data_number, selectlist);

        number_adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (selectlist.contains(data_number.get(position))) {
                    selectlist.remove(data_number.get(position));
                } else {
                    selectlist.add(data_number.get(position));
                }
                setCount(selectlist.size());
//                select_count.setText("（已选：" + selectlist.size() + ")");
                number_adapter.notifyDataSetChanged();
            }
        });

        number_list.setAdapter(number_adapter);
    }

    private void setCount(int size){
        select_count.setText("(已选：" + selectlist.size() + ")");
    }
}
