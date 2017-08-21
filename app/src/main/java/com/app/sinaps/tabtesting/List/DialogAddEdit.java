package com.app.sinaps.tabtesting.List;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.app.sinaps.tabtesting.Main.MainActivity;
import com.app.sinaps.tabtesting.R;

/**
 * Created by Sinaps on 18.08.2017.
 */


/** Диалог для добавления и редактирования элементов списка
 *
 */
public class DialogAddEdit extends DialogFragment implements View.OnClickListener{
    /** Тэг для разделения диалога создания от диалога редактирования */
    private String tag;
    /** Текстовое поле для ввода данных */
    private EditText nameField;
    /** Текст который по ошибек может быть не сохранен */
    private static String notSaveData;
    /** Ключ для передачи тега */
    public static final String SAVE_TAG_KEY = "savetag";
    private Bundle bundle;
    /** Диалог запроса на сохранения при потенциально случайной отмены диалога */
    private DialogSave dialogSave;
    /** Первоначальный текст редактируемого поля ввода, определения были ли произведены изменения */
    private String primalText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        getFragmentManager().findFragmentById(R.id.tab1);

        tag = getTag();
        bundle = new Bundle();
        dialogSave = new DialogSave();
        View view = inflater.inflate(R.layout.list_add_edit_dialog, null);
        view.findViewById(R.id.list_add_edit_dialog_ok_btn).setOnClickListener(this);
        view.findViewById(R.id.list_add_edit_dialog_cancel_btn).setOnClickListener(this);
        nameField = (EditText) view.findViewById(R.id.dialog_et);


        switch (tag){
            case ListFragment.ADD_DIALOG_TAG:
                getDialog().setTitle(R.string.list_dialog_add_title);
               // nameField.setText("");
                break;
            case ListFragment.EDIT_DIALOG_TAG:
                getDialog().setTitle(R.string.list_dialog_edit_title);
                break;
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        switch (tag) {
            case ListFragment.ADD_DIALOG_TAG:
                nameField.setText("");
                break;
            case ListFragment.EDIT_DIALOG_TAG:
                primalText = getArguments().getString(ListFragment.DATA_KEY);
                nameField.setText(primalText);
                break;
        }
    }
    /** Обработка нажатий кнопок диалога
     *
     */
    @Override
    public void onClick(View v) {
        switch (tag){
            case ListFragment.ADD_DIALOG_TAG:
                switch (v.getId()){
                    case R.id.list_add_edit_dialog_ok_btn:
                        Log.d("my", "Ок в диалоге Добавления");
                        if(nameField.getText().length() > 0) {
                            ((MainActivity) getActivity()).listFragment.addItem(String.valueOf(nameField.getText()));
                        }
                        else{
                            Toast toast = Toast.makeText(getActivity(), "Введите название", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        break;
                    case R.id.list_add_edit_dialog_cancel_btn:
                        Log.d("my", "Кансел в диалоге Добавления");
                        break;
                }
                break;
            case ListFragment.EDIT_DIALOG_TAG:
                switch (v.getId()){
                    case R.id.list_add_edit_dialog_ok_btn:
                        Log.d("my", "Ок в диалоге Редактирования");
                        ((MainActivity)getActivity()).listFragment.editItem(String.valueOf(nameField.getText()));
                        break;
                    case R.id.list_add_edit_dialog_cancel_btn:
                        Log.d("my", "Кансел в диалоге Редактирования");
                        break;
                }
                break;
        }
        dismiss();
    }
    /** Обработка отмены диалога.
     *  Попытка сохранить введеные данные, если отмена была случайной
     */
    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

        notSaveData = String.valueOf(nameField.getText());
        if(notSaveData.length() > 0){
            if(tag.equals(ListFragment.EDIT_DIALOG_TAG) && (notSaveData.equals(primalText))){
                return;
            }
            bundle.putString(SAVE_TAG_KEY, notSaveData);
            dialogSave.setArguments(bundle);
            dialogSave.show(getFragmentManager(), tag);
        }
    }

}
