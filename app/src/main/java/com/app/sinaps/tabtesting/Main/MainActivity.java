package com.app.sinaps.tabtesting.Main;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.app.sinaps.tabtesting.List.ListFragment;
import com.app.sinaps.tabtesting.Maps.MapsActivity;
import com.app.sinaps.tabtesting.R;
import com.app.sinaps.tabtesting.Scaler.ScalerFragment;

import java.util.Locale;

/**Класс главной активити
 @author Котельников Николай
 @version 2.0
 */
public class MainActivity extends AppCompatActivity {

    private TabHost tabHost;
    /** Ключ для сохранения текущей вкладки */
    private final String TAB_TAG_KEY = "tabTag";

    private String currentLang = "en";
    /** Ключ для сохранения языковых настроек */
    final String LANG_OPTION_KEY = "langOption";
    private SharedPreferences preferences;
    /** Идентификатор диалогового окна смены локализации */
    private final String DIALOG_LANG = "dialogLang";
    /** Ключ для передачи текущего языка диалогу выбора локализации */
    public static final String CURRENT_LANG_KEY = "langKey";

    /** Теги вкладок */
    private final String TAB_TAG_1 = "tag1";
    private final String TAB_TAG_2 = "tag2";
    private final String TAB_TAG_3 = "tag3";
    private final String TAB_TAG_4 = "tag4";

    private View lastView;

    public ListFragment listFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadLang(); /////////////////?????


        tabHost = (TabHost)findViewById(R.id.tabhost);
        tabHost.setup();

        TabHost.TabSpec spec = tabHost.newTabSpec(TAB_TAG_1);
        spec.setIndicator(createTabView(
                getResources().getString(R.string.main_list_tab),
                R.drawable.list));
        spec.setContent(R.id.tab1);
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec(TAB_TAG_2);
        spec.setIndicator(createTabView(
                getResources().getString(R.string.main_scaling_tab),
                R.drawable.scalling));
        spec.setContent(R.id.tab2);
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec(TAB_TAG_3);
        spec.setIndicator(createTabView(
                getResources().getString(R.string.main_parsing_tab),
                R.drawable.parsing));
        spec.setContent(R.id.tab3);
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec(TAB_TAG_4);
        spec.setIndicator(createTabView(
                getResources().getString(R.string.main_maps_tab),
                R.drawable.map));
        spec.setContent(R.id.tab4);
        tabHost.addTab(spec);


        lastView = tabHost.getCurrentTabView();
        lastView.setBackgroundColor(ContextCompat.getColor(this, R.color.tab_backgr_selected));

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                lastView.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.tab_backgr));
                lastView = tabHost.getCurrentTabView();
                lastView.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.tab_backgr_selected));

                if(tabId.equals(TAB_TAG_4)){
                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                    startActivity(intent);
                    tabHost.setCurrentTabByTag(TAB_TAG_1);
                }
            }
        });

        listFragment = (ListFragment) getFragmentManager().findFragmentById(R.id.tab1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveLang();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLang();
    }
    /** Сохраняет последнюю выдранную вкладку перед поворотом экрана
     *
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(TAB_TAG_KEY, tabHost.getCurrentTabTag());
    }
    /** Загружает последнюю выдранную вкладку, после смены ориентации экрана
     *
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        tabHost.setCurrentTabByTag(savedInstanceState.getString(TAB_TAG_KEY));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == ScalerFragment.PERMISSION_REQUEST_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("scaling", "разрешение полученно!!)))");

                Log.d("scaling", "Запрос пошел");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /** Сохраняет текущие настройки локализации
     *
     */
    private void saveLang() {
        preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LANG_OPTION_KEY, currentLang);
        editor.commit();
    }
    /** Загружает текущие настройки локализации
     *
     */
    private void loadLang(){
        preferences = getPreferences(MODE_PRIVATE);
        currentLang = preferences.getString(LANG_OPTION_KEY, "en");
        setLocale(currentLang);
    }

    /** Уставливает переданный язык в качестве языка по умолчанию
     *
     */
    public void setLocale(String lang){
        currentLang = lang;
        Log.d("my", "Пытаемся переключить язык");
        Locale newLocale = new Locale(lang);
        Log.d("my", "Locale = " + newLocale);
        Locale.setDefault(newLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();

        config.setLocale(newLocale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        reloadResurces();
    }
    /** Переустанавливет переданный язык и пересоздает активити для немедленных изменений
     *
     */
    public void reloadLanguege(String lang){
        setLocale(lang);
        recreate();
    }
    /** Перезагружает часть языковых ресурсов, для немеденой смены языка
     *
     */
    private void reloadResurces(){

        ((Button) findViewById(R.id.main_lang_btn)).setText(getResources().getString(R.string.main_lang_btn));
        ((Button) findViewById(R.id.list_add_btn)).setText(getResources().getString(R.string.list_add_btn));
        ((TextView) findViewById(R.id.main_title_tv)).setText(getResources().getString(R.string.app_name));

        ((Button)findViewById(R.id.scaler_galler_btn)).setText(getResources().getString(R.string.scaler_gallery_btn));
        ((Button)findViewById(R.id.scaler_photo_btn)).setText(getResources().getString(R.string.scaler_photo_btn));
        ((Button)findViewById(R.id.parser_dwnld_btn)).setText(getResources().getString(R.string.parser_download_btn));

        ((TextView)findViewById(R.id.main_title_tv)).setText(getResources().getString(R.string.app_name));
    }
    /** Создает View используемый для наполнения вкладок TabView
     *
     */
    private View createTabView(String name, int icon){  ////////////////////////////Метод для создания view для вкладок
        View view = getLayoutInflater().inflate(R.layout.tab, null);
        TextView textView = (TextView) view.findViewById(R.id.tab_tv);
        textView.setText(name);
        ImageView imageView = (ImageView) view.findViewById(R.id.tab_iv);
        imageView.setImageResource(icon);
        return view;
    }
    /** Обработчик нажатия на кнопку смены языка
     *  Нажатие приводит к вызову диалогового окна
     *  с выбором доступных локализаций
     */
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.main_lang_btn:
                DialogFragment dialog = new DialogLang();
                Bundle bundle = new Bundle();
                bundle.putString(CURRENT_LANG_KEY, currentLang);
                dialog.setArguments(bundle);
                dialog.show(getFragmentManager(), DIALOG_LANG);
                break;
        }
    }
}
