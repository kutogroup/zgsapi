package com.zgs.api.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zgs.api.http.HttpEngine;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by simon on 15-11-27.
 */
public abstract class BaseFragment extends Fragment implements BaseActivityResult {
    protected static String lastTopFragment = null;

    @Override
    public void onDestroy() {
        super.onDestroy();

        HttpEngine.getInstance().cancelAll(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getResID(), container, false);
        initView(view);

        ((BaseActivity) getActivity()).activityResults.add(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        lastTopFragment = getClass().getName();
    }

    @Override
    public void onPause() {
        super.onPause();

        /**
         * 清除activity的上一个requestCode
         */
        getBaseActivity().lastRequestCode = 0;
    }

    @Override
    public void onDestroyView() {
        HttpEngine.getInstance().cancelAll(this);
        ((BaseActivity) getActivity()).activityResults.remove(this);

        super.onDestroyView();
    }

    public abstract int getResID();

    public void initView(View root) {
        ButterKnife.bind(this, root);
    }

    public BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    public void postEvent(BaseEvent event) {
        EventBus.getDefault().post(event);
    }
}
