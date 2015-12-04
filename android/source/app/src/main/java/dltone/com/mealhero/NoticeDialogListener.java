package dltone.com.mealhero;

import android.app.DialogFragment;

/**
 * Created by costin on 12/4/2015.
 */
public interface NoticeDialogListener {
    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
}
