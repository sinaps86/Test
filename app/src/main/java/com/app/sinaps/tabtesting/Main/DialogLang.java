package com.app.sinaps.tabtesting.Main;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.app.sinaps.tabtesting.R;

/**
 * Created by Sinaps on 19.08.2017.
 */

public class DialogLang extends DialogFragment {

    private int position;
    private String currentLang;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String[] langs = getActivity().getResources().getStringArray(R.array.langs);
        currentLang = getArguments().getString(MainActivity.CURRENT_LANG_KEY);

       switch (currentLang){
           case "en":
               position = 0;
               Log.d("my", "pos " + position);
               break;
           case "ru":
               position = 1;
               Log.d("my", "pos " + position);
               break;
       }

        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_lang_title)
                .setSingleChoiceItems(langs, position, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        position = which;
                    }
                })
                .setPositiveButton(R.string.dialog_lang_ok_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (position){
                            case 0:
                                currentLang = "en";
                                break;
                            case 1:
                                currentLang = "ru";
                                break;
                        }

                        ((MainActivity)getActivity()).reloadLanguege(currentLang);
                        dismiss();
                    }
                })
                .setNegativeButton(R.string.dialog_lang_cancel_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                ;

        return adb.create();
    }
}
