package dltone.com.mealhero;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;

/**
 * Created by costin on 12/4/2015.
 */
public class AddClientNoteDialog extends DialogFragment {

    String note;
    NoticeDialogListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_add_client_note, null))
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText noteBox = (EditText) getView().findViewById(R.id.dialog_add_note_edit_text);
                        if(noteBox.getText().toString().trim().isEmpty()) {
                            noteBox.setError("Note can't be empty.");
                            noteBox.requestFocus();
                        } else {
                            note = noteBox.getText().toString().trim();
                            mListener.onDialogPositiveClick(AddClientNoteDialog.this);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onDialogNegativeClick(AddClientNoteDialog.this);
                        getDialog().cancel();
                    }
                });
        return builder.create();
    }

    /*@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //Verify Interface implementation
        try {
            //Instantiate listener to send events to host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            //Activity doesn't implement the interface.
            throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener.");
        }
    }*/

    public final String getNote() {
        return note;
    }

    public void setNoticeDialogListener(NoticeDialogListener l) {
        mListener = l;
    }
}
