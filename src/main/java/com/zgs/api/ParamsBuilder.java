package com.zgs.api;

import android.os.Bundle;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by simon on 1/5/16.
 */
public class ParamsBuilder {
    JSONObject json;
    Bundle bundle;

    public ParamsBuilder put(String key, Object object) {
        if (object == null) {
            return this;
        }

        if (bundle == null) {
            bundle = new Bundle();
        }

        if (object instanceof Integer) {
            bundle.putInt(key, (Integer) object);
            return this;
        } else if (object instanceof String) {
            bundle.putString(key, (String) object);
            return this;
        } else if (object instanceof String[]) {
            bundle.putStringArray(key, (String[]) object);
            return this;
        } else if (object instanceof Float) {
            bundle.putFloat(key, (Float) object);
            return this;
        } else {
            throw new IllegalArgumentException("unsupported type");
        }
    }

    public ParamsBuilder putJson(String key, Object object) {
        if (object == null) {
            return this;
        }

        if (json == null) {
            json = new JSONObject();
        }

        json.put(key, object);
        return this;
    }


    public Bundle build() {
        return bundle;
    }

    public JSONObject buildJson() {
        return json;
    }
}
