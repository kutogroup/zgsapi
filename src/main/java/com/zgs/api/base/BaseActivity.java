package com.zgs.api.base;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.*;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.zgs.api.R;
import com.zgs.api.http.HttpEngine;
import com.zgs.api.utils.LogUtils;
import com.zgs.api.utils.SysUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by simon on 15-11-24.
 */
public abstract class BaseActivity extends FragmentActivity {
    public static String lastTopActivity = null;
    public static int activityDepth = 0;
    public static int activityCount = 0;
    public static HashMap<String, Integer> processMap = new HashMap<>();

    public ArrayList<BaseActivityResult> activityResults = new ArrayList<>();
    public String processName = "";
    private ProgressDialog waitingDialog = null;
    public int lastRequestCode = 0;

    /**
     * @return get the instance of current activity
     */
    public FragmentActivity getActivity() {
        return BaseActivity.this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //hold font size when system font changed
        Configuration config = new Configuration();
        config.fontScale = 1.0f;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        setContentView(getResID());
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        initView(getWindow().getDecorView().getRootView());

        activityDepth++;

        int count = processMap.get(SysUtils.getProcessName(getActivity())) == null ? 0 : processMap.get(SysUtils.getProcessName(getActivity()));
        LogUtils.info("oncreate, process,name=" + SysUtils.getProcessName(getActivity()) + ", count=" + (count + 1));

        processName = SysUtils.getProcessName(getActivity());
        processMap.put(processName, count + 1);
    }

    @Override
    public void onPause() {
        super.onPause();

        activityCount--;
        lastRequestCode = 0;
        MobclickAgent.onPause(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        activityCount++;
        lastTopActivity = getClass().getName();
        MobclickAgent.onResume(this);
        LogUtils.info("set last activity=" + lastTopActivity);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        HttpEngine.getInstance().cancelAll(this);
        activityDepth--;

        if (processMap.get(processName) > 0) {
            processMap.put(processName, processMap.get(processName) - 1);
        } else {
            processMap.remove(processName);
        }

        int count = processMap.get(processName) == null ? 0 : processMap.get(processName);
        LogUtils.info("ondestory, process,name=" + processName + ", count=" + count);
        hideWaitingDialog();

        if (count == 0) {
            LogUtils.info("process count is 0, exit");
            MobclickAgent.onKillProcess(this);
            System.exit(0);
        }
    }

    public abstract int getResID();

    public void initView(View root) {
        ButterKnife.bind(this, root);
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.comm_slide_from_left, R.anim.comm_slide_to_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (BaseActivityResult result : activityResults) {
            result.onActivityResult(requestCode, resultCode, data);
        }

        lastRequestCode = requestCode;
    }

    private DialogInterface.OnDismissListener finishListener = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            finish();
        }
    };

    private DialogInterface.OnDismissListener noFinishListener = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            HttpEngine.getInstance().cancelAll(getActivity());
        }
    };

    public void showWaitingDialog() {
        if (waitingDialog == null) {
            waitingDialog = new ProgressDialog(getActivity());
            waitingDialog.setCanceledOnTouchOutside(false);
            waitingDialog.setMessage(getString(R.string.str_common_waiting));
            waitingDialog.setOnDismissListener(finishListener);
        }

        waitingDialog.show();
    }

    public void showWaitingDialogNoFinish() {
        if (waitingDialog == null) {
            waitingDialog = new ProgressDialog(getActivity());
            waitingDialog.setCanceledOnTouchOutside(false);
            waitingDialog.setMessage(getString(R.string.str_common_waiting));
            waitingDialog.setOnDismissListener(noFinishListener);
        }

        waitingDialog.show();
    }

    public void hideWaitingDialog() {
        if (waitingDialog != null && waitingDialog.isShowing()) {
            waitingDialog.setOnDismissListener(null);
            waitingDialog.dismiss();
        }
    }

    public void postEvent(BaseEvent event) {
        EventBus.getDefault().post(event);
    }
}
