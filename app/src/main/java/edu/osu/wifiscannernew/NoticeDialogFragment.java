package edu.osu.wifiscannernew;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

/**
 * DialogFragment asking user to allow permission to do Wi-Fi scans.
 * Code based on http://www.codingdemos.com/android-custom-alertdialog/,
 * but using DialogFragment.
 *
 * Created by adamcchampion on 2018/01/05.
 */

public class NoticeDialogFragment extends DialogFragment {

    private void setDialogStatus(boolean isChecked) {
        Context context = getActivity();
        if (context != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                    context.getApplicationContext());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(getResources().getString(R.string.suppress_dialog_key), isChecked);
            editor.apply();
        }
    }


    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_permission, null);
        CheckBox checkBox = view.findViewById(R.id.checkbox);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setDialogStatus(true);
                }
                else {
                    setDialogStatus(false);
                }
            }
        });

        final Activity activity = getActivity();
        AlertDialog dialog = new AlertDialog.Builder(activity).create();

        if (activity != null) {
            dialog = new AlertDialog.Builder(activity)
                    .setTitle("Notice")
                    .setMessage("This app asks for location permission, which Wi-Fi scans require. Please grant this permission.")
                    .setView(view)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            activity.finish();
                        }
                    }).create();
        }

        return dialog;
    }
}
