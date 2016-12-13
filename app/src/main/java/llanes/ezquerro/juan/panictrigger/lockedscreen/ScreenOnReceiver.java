package llanes.ezquerro.juan.panictrigger.lockedscreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import llanes.ezquerro.juan.panictrigger.activities.PanicActivity;
import llanes.ezquerro.juan.panictrigger.constants.PanicTriggerConstants;

public class ScreenOnReceiver extends BroadcastReceiver {
    public ScreenOnReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
            if (PasswordFailsReceiver.triggerRipple) {
                PasswordFailsReceiver.triggerRipple = false;
                Intent panic = new Intent(context, PanicActivity.class);
                panic.putExtra(PanicTriggerConstants.RUN_FROM_LOGIN, true);
                context.startActivity(panic);
            }
        }
    }
}
