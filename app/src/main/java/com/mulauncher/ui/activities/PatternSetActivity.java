package com.mulauncher.ui.activities;

import android.app.Activity;
import android.content.Intent;

import java.util.List;

import me.zhanghai.android.patternlock.PatternUtils;
import me.zhanghai.android.patternlock.PatternView;
import me.zhanghai.android.patternlock.SetPatternActivity;

public class PatternSetActivity extends SetPatternActivity {

    @Override
    protected void onSetPattern(List<PatternView.Cell> pattern) {
        String patternSha1 = PatternUtils.patternToSha1String(pattern);
        Intent result = new Intent();
        result.putExtra("pattern", patternSha1);
        setResult(Activity.RESULT_OK, result);
        finish();
    }
}
