package com.zgs.api.base;

import android.content.Intent;

/**
 * Created by simon on 15-12-24.
 */
public interface BaseActivityResult {
    void onActivityResult(int requestCode, int resultCode, Intent data);
}
