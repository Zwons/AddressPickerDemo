package com.ymht.library.picker.address;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.ymht.library.R;

import java.util.ArrayList;

public class AddressPicker extends Dialog implements View.OnClickListener {
    private static final int TAB_INDEX_PROVINCE = 0;//省份标志
    private static final int TAB_INDEX_CITY = 1;//城市标志
    private static final int TAB_INDEX_DISTRICT = 2;//区标志
    private static final int TAB_INDEX_STREET = 3;//街道标志
    private int tabIndex = TAB_INDEX_PROVINCE; //默认是省份

    private Context context;
    private ImageView mAddressCloseImage;
    private TextView mAddressProvinceText;
    private TextView mAddressCityText;
    private TextView mAddressDistrictText;
    private TextView mAddressStreetText;
    private View mAddressIndicator;
    private RecyclerView mAddressRecycler;
    private ProgressBar mAddressProgressBar;

    private OnAddressPickerListener listener;//操作监听

    private AddressPickerRecyclerAdapter provinceAdapter;
    private AddressPickerRecyclerAdapter cityAdapter;
    private AddressPickerRecyclerAdapter districtAdapter;
    private AddressPickerRecyclerAdapter streetAdapter;

    private ArrayList<String> provinceList = new ArrayList<>();
    private ArrayList<String> cityList = new ArrayList<>();
    private ArrayList<String> districtList = new ArrayList<>();
    private ArrayList<String> streetList = new ArrayList<>();

    private ArrayList<TextView> textViewArrayList = new ArrayList<>();

    private int tabSelectedColor = Color.parseColor("#FDD23C");//tab选择的颜色
    private int tabUnselectedColor = Color.parseColor("#000000");//tab未选择的颜色
    private int indicatorColor = Color.parseColor("#FDD23C");//指示器的颜色
    private int itemTextSelectedColor = Color.parseColor("#FDD23C");//item的text选中的颜色
    private int itemTextUnselectedColor = Color.parseColor("#343434");//item的text未选中的颜色
    private int itemImageResourceId = R.drawable.address_select;//item对号的资源图片
    private LinearLayoutManager layoutManager;//TopScrollLinearLayoutManager是自定义的LinearLayoutManager，可以使点击的item置顶

    public AddressPicker(@NonNull Context context) {
        super(context, R.style.AddressPickerStyle);

        this.context = context;
        initViews();
    }

    private void initViews() {
        setContentView(R.layout.address_picker_layout);

        mAddressCloseImage = findViewById(R.id.address_close_image);
        mAddressProvinceText = findViewById(R.id.address_province_text);
        mAddressCityText = findViewById(R.id.address_city_text);
        mAddressDistrictText = findViewById(R.id.address_district_text);
        mAddressStreetText = findViewById(R.id.address_street_text);
        mAddressIndicator = findViewById(R.id.address_indicator);
        mAddressRecycler = findViewById(R.id.address_recycler);
        mAddressProgressBar = findViewById(R.id.address_progress_bar);

        Window window = getWindow();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.address_picker_animation);
        lp.width = d.widthPixels;
        lp.height = (int) (d.heightPixels * 0.7);
        window.setAttributes(lp);

        mAddressCloseImage.setOnClickListener(this);
        mAddressProvinceText.setOnClickListener(this);
        mAddressCityText.setOnClickListener(this);
        mAddressDistrictText.setOnClickListener(this);
        mAddressStreetText.setOnClickListener(this);

        //在布局还未加载完成时，设置布局的监听，设置Indicator在mAddressProvinceText下面
        mAddressProvinceText.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //要及时remove掉listener
                mAddressProvinceText.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                ViewGroup.LayoutParams params = mAddressIndicator.getLayoutParams();
                params.width = mAddressProvinceText.getWidth();
                mAddressIndicator.setLayoutParams(params);
            }
        });

        //设置RecyclerView的LayoutManager
        layoutManager = new LinearLayoutManager(context);
        mAddressRecycler.setLayoutManager(layoutManager);

        textViewArrayList.add(mAddressCityText);
        textViewArrayList.add(mAddressDistrictText);
        textViewArrayList.add(mAddressStreetText);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (listener != null) {
            listener.onProvinceStart();
        }
        mAddressIndicator.setBackgroundColor(indicatorColor);
        mAddressProvinceText.setTextColor(tabUnselectedColor);
        mAddressCityText.setTextColor(tabUnselectedColor);
        mAddressDistrictText.setTextColor(tabUnselectedColor);
        mAddressStreetText.setTextColor(tabUnselectedColor);
    }

    @Override
    public void show() {
        super.show();
        if (tabIndex == TAB_INDEX_STREET) {
            ViewGroup.LayoutParams params = mAddressIndicator.getLayoutParams();
            params.width = mAddressStreetText.getMeasuredWidth();
            mAddressIndicator.setLayoutParams(params);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.address_close_image) {
            hide();
        } else if (i == R.id.address_province_text) {
            updateIndicator(TAB_INDEX_PROVINCE);
        } else if (i == R.id.address_city_text) {
            updateIndicator(TAB_INDEX_CITY);
        } else if (i == R.id.address_district_text) {
            updateIndicator(TAB_INDEX_DISTRICT);
        } else if (i == R.id.address_street_text) {
            updateIndicator(TAB_INDEX_STREET);
        }
    }


    private void updateIndicator(int index) {
        if (tabIndex == index) {
            return;
        }
        switch (index) {
            case TAB_INDEX_PROVINCE: //省份
                indicatorTranslateAnimator(mAddressProvinceText);
                mAddressRecycler.setAdapter(provinceAdapter);
                provinceAdapter.scrollToSelectedPosition();//置顶
                break;
            case TAB_INDEX_CITY: //城市
                indicatorTranslateAnimator(mAddressCityText);
                mAddressRecycler.setAdapter(cityAdapter);
                cityAdapter.scrollToSelectedPosition();//置顶
                break;
            case TAB_INDEX_DISTRICT: //区
                indicatorTranslateAnimator(mAddressDistrictText);
                mAddressRecycler.setAdapter(districtAdapter);
                districtAdapter.scrollToSelectedPosition();//置顶
                break;
            case TAB_INDEX_STREET: //街道
                indicatorTranslateAnimator(mAddressStreetText);
                mAddressRecycler.setAdapter(streetAdapter);
                streetAdapter.scrollToSelectedPosition();//置顶
                break;
        }
        tabIndex = index;
    }

    /**
     * tab 来回切换的动画
     *
     * @param tab
     * @return
     */
    private void indicatorTranslateAnimator(TextView tab) {
        ObjectAnimator xAnimator = ObjectAnimator.ofFloat(mAddressIndicator, "X", mAddressIndicator.getX(), tab.getX());

        final ViewGroup.LayoutParams params = mAddressIndicator.getLayoutParams();
        ValueAnimator widthAnimator = ValueAnimator.ofInt(params.width, tab.getMeasuredWidth());
        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                params.width = (int) animation.getAnimatedValue();
                mAddressIndicator.setLayoutParams(params);
            }
        });

        AnimatorSet set = new AnimatorSet();
        set.setInterpolator(new FastOutSlowInInterpolator());
        set.playTogether(xAnimator, widthAnimator);

        set.start();

        /**
         * 以下方法也可实现Indicator的移动，只是Indicator的长度变化时比较生硬而不是平滑过度
         * */
        /*final ViewGroup.LayoutParams params = mAddressIndicator.getLayoutParams();
        params.width = tab.getMeasuredWidth();
        mAddressIndicator.animate().setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mAddressIndicator.setLayoutParams(params);
            }
        }).x(tab.getX()).setDuration(1000).start();*/
    }

    private void doItemClick(int tabIndex, TextView textView, int position, String textString) {
        //设置选中的省/市/区/街道的名称
        textView.setText(textString);
        //设置选中的省/市/区/街道的字体颜色
        textView.setTextColor(tabSelectedColor);

        switch (tabIndex) {
            case TAB_INDEX_PROVINCE: //省份
                listener.onCityStart(position);
                for (int i = 0; i < textViewArrayList.size(); i++) {
                    textViewArrayList.get(i).setText("请选择");
                    textViewArrayList.get(i).setVisibility(View.GONE);
                    textViewArrayList.get(i).setTextColor(Color.BLACK);
                }
                mAddressProgressBar.setVisibility(View.VISIBLE);
                break;
            case TAB_INDEX_CITY: //城市
                listener.onDistrictStart(position);
                for (int i = 1; i < textViewArrayList.size(); i++) {
                    textViewArrayList.get(i).setText("请选择");
                    textViewArrayList.get(i).setVisibility(View.GONE);
                    textViewArrayList.get(i).setTextColor(Color.BLACK);
                }
                mAddressProgressBar.setVisibility(View.VISIBLE);
                break;
            case TAB_INDEX_DISTRICT: //区
                listener.onStreetStart(position);
                textViewArrayList.get(2).setText("请选择");
                textViewArrayList.get(2).setVisibility(View.GONE);
                textViewArrayList.get(2).setTextColor(Color.BLACK);
                mAddressProgressBar.setVisibility(View.VISIBLE);
                break;
            case TAB_INDEX_STREET: //街道
                listener.onEnsure(position, mAddressProvinceText.getText().toString() + mAddressCityText.getText().toString() + mAddressDistrictText.getText().toString() + mAddressStreetText.getText().toString());
                hide();
                break;
        }
    }

    public void setOnAddressPickerListener(OnAddressPickerListener listener) {
        this.listener = listener;
    }

    public void provinceSuccess(ArrayList<String> list) {

        provinceList.addAll(list);

        mAddressProgressBar.setVisibility(View.GONE);
        if (list != null) {
            //先置空再初始化，节省内存
            provinceAdapter = null;
            provinceAdapter = new AddressPickerRecyclerAdapter(provinceList, layoutManager);
            mAddressRecycler.setAdapter(provinceAdapter);
            provinceAdapter.setTextColor(itemTextSelectedColor, itemTextUnselectedColor);
            provinceAdapter.setImageResourceId(itemImageResourceId);

            //设置省列表的点击监听
            provinceAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position, String text) {
                    doItemClick(TAB_INDEX_PROVINCE, mAddressProvinceText, position, text);
                }
            });
        }
    }

    public void citySuccess(ArrayList<String> list) {
        cityList.clear();
        cityList.addAll(list);

        //加载框隐藏
        mAddressProgressBar.setVisibility(View.GONE);
        if (mAddressCityText.getVisibility() == View.GONE) {
            mAddressCityText.setVisibility(View.VISIBLE);
        }
        //该方法会在控件测量完成后进行操作，防止获得的控件宽高为0
        mAddressCityText.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //要及时remove掉listener
                mAddressCityText.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                updateIndicator(TAB_INDEX_CITY);
            }
        });


        if (list != null) {
            cityAdapter = null;
            cityAdapter = new AddressPickerRecyclerAdapter(cityList, layoutManager);
            mAddressRecycler.setAdapter(cityAdapter);
            cityAdapter.setTextColor(itemTextSelectedColor, itemTextUnselectedColor);
            cityAdapter.setImageResourceId(itemImageResourceId);

            cityAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position, String text) {
                    doItemClick(TAB_INDEX_CITY, mAddressCityText, position, text);
                }
            });
        }
    }

    public void districtSuccess(ArrayList<String> list) {
        districtList.clear();
        districtList.addAll(list);

        //加载框隐藏
        mAddressProgressBar.setVisibility(View.GONE);
        if (mAddressDistrictText.getVisibility() == View.GONE) {
            mAddressDistrictText.setVisibility(View.VISIBLE);
        }
        //该方法会在控件测量完成后进行操作，防止获得的控件宽高为0
        mAddressDistrictText.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //要及时remove掉listener
                mAddressDistrictText.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                updateIndicator(TAB_INDEX_DISTRICT);
            }
        });

        if (list != null) {
            districtAdapter = null;
            districtAdapter = new AddressPickerRecyclerAdapter(districtList, layoutManager);
            mAddressRecycler.setAdapter(districtAdapter);
            districtAdapter.setTextColor(itemTextSelectedColor, itemTextUnselectedColor);
            districtAdapter.setImageResourceId(itemImageResourceId);

            districtAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position, String text) {
                    doItemClick(TAB_INDEX_DISTRICT, mAddressDistrictText, position, text);
                }
            });
        }
    }

    public void streetSuccess(ArrayList<String> list) {
        streetList.clear();
        streetList.addAll(list);

        //加载框隐藏
        mAddressProgressBar.setVisibility(View.GONE);
        if (mAddressStreetText.getVisibility() == View.GONE) {
            mAddressStreetText.setVisibility(View.VISIBLE);
        }
        //该方法会在控件测量完成后进行操作，防止获得的控件宽高为0
        mAddressStreetText.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //要及时remove掉listener
                mAddressStreetText.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                updateIndicator(TAB_INDEX_STREET);
            }
        });

        if (list != null) {
            streetAdapter = null;
            streetAdapter = new AddressPickerRecyclerAdapter(streetList, layoutManager);
            mAddressRecycler.setAdapter(streetAdapter);
            streetAdapter.setTextColor(itemTextSelectedColor, itemTextUnselectedColor);
            streetAdapter.setImageResourceId(itemImageResourceId);

            streetAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position, String text) {
                    doItemClick(TAB_INDEX_STREET, mAddressStreetText, position, text);
                }
            });
        }
    }

    /***
     * 设置标签选择之后字体的颜色
     */
    public void setTabSelectedColor(int tabSelectedColor) {
        this.tabSelectedColor = tabSelectedColor;
    }

    /**
     * 设置标签未选择字体的颜色
     */
    public void setTabUnselectedColor(int tabUnselectedColor) {
        this.tabUnselectedColor = tabUnselectedColor;
    }

    /**
     * 设置指示器的颜色
     */
    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
    }

    /**
     * 设置列表中item名字的选中和未选中颜色
     */
    public void setItemTextColor(int itemTextSelectedColor, int itemTextUnselectedColor) {
        this.itemTextSelectedColor = itemTextSelectedColor;
        this.itemTextUnselectedColor = itemTextUnselectedColor;
    }

    /**
     * 设置列表中item对号的资源图片
     */
    public void setItemImageResourceId(int itemImageResourceId) {
        this.itemImageResourceId = itemImageResourceId;
    }

}
