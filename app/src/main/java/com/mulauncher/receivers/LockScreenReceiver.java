package com.mulauncher.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mulauncher.ui.activities.LockScreenActivity;

import java.util.Objects;

public class LockScreenReceiver extends BroadcastReceiver {

    public static boolean wasScreenOn = true;

    @Override
    public void onReceive(Context context, Intent intent) {

        switch (Objects.requireNonNull(intent.getAction())) {
            case Intent.ACTION_SCREEN_OFF: {
                wasScreenOn = false;
                Intent intent11 = new Intent(context, LockScreenActivity.class);
                intent11.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent11);
                break;
            }
            case Intent.ACTION_SCREEN_ON: {
                wasScreenOn = true;
                Intent intent11 = new Intent(context, LockScreenActivity.class);
                intent11.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                break;
            }
            case Intent.ACTION_BOOT_COMPLETED: {
                Intent intent11 = new Intent(context, LockScreenActivity.class);
                intent11.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent11);
                break;
            }
        }
    }
}
