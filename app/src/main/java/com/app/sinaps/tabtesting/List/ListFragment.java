package com.app.sinaps.tabtesting.List;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.app.sinaps.tabtesting.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by Sinaps on 18.08.2017.
 */
/** Фрагмент работы со списком
 *
 */
public class ListFragment extends Fragment {

    private ListView listView;
    private Button addBtn;
    private CountryAdapter adapter;
    private ArrayList<Country> countries;
    private SharedPreferences preferences;
    private DialogFragment dialogAddEdit;
    private Bundle bundle;
    private int position;
    public static final String DATA_KEY = "data";
    public static final String ADD_DIALOG_TAG = "add";
    public static final String EDIT_DIALOG_TAG = "edit";
    private final String SAVE_FILE_NAME = "save.txt";
    private final String CREATE_KEY = "create";
    final int CM_DELETE_ID = 101;
    final int CM_EDIT_ID = 102;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_fragment_layout, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onStart() {
        super.onStart();

        countries = new ArrayList<>();
        bundle = new Bundle();
        preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        listView = (ListView) getActivity().findViewById(R.id.list_lv);
        addBtn = (Button) getActivity().findViewById(R.id.list_add_btn);
        dialogAddEdit = new DialogAddEdit();
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAddEdit.show(getFragmentManager(), ADD_DIALOG_TAG);
            }
        });

        loadFile();

        adapter = new CountryAdapter(getActivity(), R.layout.list_item, countries);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                position = pos;
                showEditDialog();
            }
        });
        registerForContextMenu(listView);

    }

    @Override
    public void onResume() {
        super.onResume();

        //loadFile();
    }

    @Override
    public void onPause() {
        super.onPause();

        saveFile();
    }

    private void saveFile(){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(CREATE_KEY, true);
        editor.commit();
        try(PrintWriter pw = new PrintWriter(getActivity().openFileOutput(SAVE_FILE_NAME, Context.MODE_PRIVATE))) {
            for(Country country:countries) {
                pw.println(country.getName());
                pw.println(country.isVisited());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadFile(){

        Log.d("my", "Создан ?: " + preferences.getBoolean(CREATE_KEY, false));

        if(!preferences.getBoolean(CREATE_KEY, false)){
           fillList();
            Log.d("my", "Впервые");
        } else {
            Log.d("my", "Повторно");
            try (BufferedReader br = new BufferedReader(new InputStreamReader(getActivity().openFileInput(SAVE_FILE_NAME)))) {
                String temp = "";
                while ((temp = br.readLine()) != null){
                    countries.add(new Country(temp, Boolean.valueOf(br.readLine())));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.add(0, CM_EDIT_ID, 0, getActivity().getResources().getString(R.string.list_dialog_context_edit));
        menu.add(0, CM_DELETE_ID, 1, getActivity().getResources().getString(R.string.list_dialog_context_delete));

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo acmi =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        position = acmi.position;
        if(item.getItemId() == CM_DELETE_ID){
            countries.remove(position);
            adapter.notifyDataSetChanged();
        }else if(item.getItemId() == CM_EDIT_ID){
            showEditDialog();
        }

        return super.onContextItemSelected(item);
    }

    private void fillList(){
        Log.d("my", "Вызвали филл");
        countries.add(new Country("Африка", false));
        countries.add(new Country("Канада", true));
        countries.add(new Country("США", true));
        countries.add(new Country("Англия", false));
        countries.add(new Country("Франция", true));
        countries.add(new Country("Германия", false));
        countries.add(new Country("Финлянция", false));
        countries.add(new Country("Швеция", true));
        countries.add(new Country("Швецария", false));
        countries.add(new Country("Италия", true));
        countries.add(new Country("Испания", false));
        countries.add(new Country("Нидерланды", false));
        countries.add(new Country("Чехия", false));
        countries.add(new Country("Таиланд", true));
        countries.add(new Country("Турция", false));
    }

    private void showEditDialog(){
        bundle.putString(DATA_KEY, countries.get(position).getName());
        dialogAddEdit.setArguments(bundle);
        dialogAddEdit.show(getFragmentManager(), EDIT_DIALOG_TAG);
    }

    public void addItem(String name){
        countries.add(new Country(name, false));
        adapter.notifyDataSetChanged();
    }

    public void editItem(String name){
        countries.get(position).setName(name);
        adapter.notifyDataSetChanged();
    }

}
