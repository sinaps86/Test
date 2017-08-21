package com.app.sinaps.tabtesting.List;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.app.sinaps.tabtesting.Main.MainActivity;
import com.app.sinaps.tabtesting.R;

/**
 * Created by Sinaps on 18.08.2017.
 */

/** Диалог запроса на сохранения при потенциально случайной отмены диалога
 *  добавления или редактирования элементов списка
 */
public class DialogSave extends DialogFragment {

    private String text;
    private String tag;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        tag = getTag();
        text = getArguments().getString(DialogAddEdit.SAVE_TAG_KEY);

        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.list_dialog_save_title)
                .setPositiveButton(R.string.list_dialog_save_ok_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        switch (tag) {
                            case ListFragment.ADD_DIALOG_TAG:
                                ((MainActivity) getActivity()).listFragment.addItem(text);
                                break;
                            case ListFragment.EDIT_DIALOG_TAG:
                                ((MainActivity) getActivity()).listFragment.editItem(text);
                                break;
                        }
                    }
                })
                .setNegativeButton(R.string.list_dialog_save_cancel_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setMessage(getActivity().getResources().getString(R.string.list_dialog_save_message) + "  " + text);

        return adb.create();
    }
}
