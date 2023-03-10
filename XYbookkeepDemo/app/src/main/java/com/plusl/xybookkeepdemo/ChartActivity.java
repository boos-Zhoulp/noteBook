package com.plusl.xybookkeepdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.plusl.xybookkeepdemo.Chart_frag.IncomChartFragment;
import com.plusl.xybookkeepdemo.Chart_frag.OutcomChartFragment;
import com.plusl.xybookkeepdemo.DataBase.DBManager;
import com.plusl.xybookkeepdemo.adapter.ChartVPAdapter;
import com.plusl.xybookkeepdemo.utils.CalendarDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChartActivity extends AppCompatActivity implements View.OnClickListener {
    FloatingActionButton float_account;
    Button inBtn,outBtn;
    TextView dateTv,inTv,outTv;
    ViewPager chartVp;
    int year;
    int month;
    int selectPos = -1,selectMonth =-1;
    List<Fragment> chartFragList;
    private IncomChartFragment incomChartFragment;
    private OutcomChartFragment outcomChartFragment;
    private ChartVPAdapter chartVPAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        initView();
        initTime();
        initStatistics(year,month);
        initFrag();
        setVPSelectListener();

        //Init bottomNavigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView_nav);

        //Set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.chart);

        //onClickListen
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.chart:
                        return true;
                    case R.id.detail:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.history:
                        startActivity(new Intent(getApplicationContext(), HistoryActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.my:
                        startActivity(new Intent(getApplicationContext(), CenterActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });


    }

    private void setVPSelectListener() {
        chartVp.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                setButtonStyle(position);
            }
        });
    }
    private void initFrag() {
        chartFragList = new ArrayList<>();
//        ??????Fragment?????????
        incomChartFragment = new IncomChartFragment();
        outcomChartFragment = new OutcomChartFragment();
//        ???????????????Fragment??????
        Bundle bundle = new Bundle();
        bundle.putInt("year",year);
        bundle.putInt("month",month);
        incomChartFragment.setArguments(bundle);
        outcomChartFragment.setArguments(bundle);
//        ???Fragment????????????????????????
        chartFragList.add(outcomChartFragment);
        chartFragList.add(incomChartFragment);
//        ???????????????
        chartVPAdapter = new ChartVPAdapter(getSupportFragmentManager(), chartFragList);
        chartVp.setAdapter(chartVPAdapter);
//        ???Fragment?????????Acitivy??????
    }

    /* ???????????????????????????????????????*/
    private void initStatistics(int year, int month) {
        float inMoneyOneMonth = DBManager.getSumMoneyOneMonth(year, month, 1);  //???????????????
        float outMoneyOneMonth = DBManager.getSumMoneyOneMonth(year, month, 0); //???????????????
        int incountItemOneMonth = DBManager.getCountItemOneMonth(year, month, 1);  //???????????????
        int outcountItemOneMonth = DBManager.getCountItemOneMonth(year, month, 0); //???????????????
        dateTv.setText(year+"???"+month+"?????????");
        inTv.setText("???"+incountItemOneMonth+"?????????, ??? "+inMoneyOneMonth);
        outTv.setText("???"+outcountItemOneMonth+"?????????, ??? "+outMoneyOneMonth);

    }

    /** ????????????????????????*/
    private void initTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH)+1;
    }

    /** ???????????????*/
    private void initView() {
        float_account = findViewById(R.id.fab);
        float_account.setOnClickListener(this);
        inBtn = findViewById(R.id.chart_btn_in);
        outBtn = findViewById(R.id.chart_btn_out);
        dateTv = findViewById(R.id.chart_tv_date);
        inTv = findViewById(R.id.chart_tv_in);
        outTv = findViewById(R.id.chart_tv_out);
        chartVp = findViewById(R.id.chart_vp);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                startActivity(new Intent(this , AccountActivity.class));
                break;
            case R.id.chart_iv_rili:
                showCalendarDialog();
                break;
            case R.id.chart_btn_in:
                setButtonStyle(1);
                chartVp.setCurrentItem(1);
                break;
            case R.id.chart_btn_out:
                setButtonStyle(0);
                chartVp.setCurrentItem(0);
                break;
        }
    }
    /* ?????????????????????*/
    private void showCalendarDialog() {
        CalendarDialog dialog = new CalendarDialog(this, selectPos, selectMonth);
        dialog.show();
        dialog.setDialogSize();
        dialog.setOnRefreshListener(new CalendarDialog.OnRefreshListener() {
            @Override
            public void onRefresh(int selPos, int year, int month) {
                ChartActivity.this.selectPos = selPos;
                ChartActivity.this.selectMonth = month;
                initStatistics(year,month);
                incomChartFragment.setDate(year,month);
                outcomChartFragment.setDate(year,month);
            }
        });
    }

    /* ???????????????????????????  ??????-0  ??????-1*/
    private void setButtonStyle(int kind){
        if (kind == 0) {
            outBtn.setBackgroundResource(R.drawable.main_recordbtn_bg);
            outBtn.setTextColor(Color.WHITE);
            inBtn.setBackgroundResource(R.drawable.dialog_btn_bg);
            inBtn.setTextColor(Color.BLACK);
        }else if (kind == 1){
            inBtn.setBackgroundResource(R.drawable.main_recordbtn_bg);
            inBtn.setTextColor(Color.WHITE);
            outBtn.setBackgroundResource(R.drawable.dialog_btn_bg);
            outBtn.setTextColor(Color.BLACK);
        }
    }
}
