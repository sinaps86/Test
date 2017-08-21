package com.app.sinaps.tabtesting.Parser;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.sinaps.tabtesting.R;

/**
 * Created by Sinaps on 18.08.2017.
 */

/** Фрагмент осуществляющий загрузку, преобразование и отображение тескта
 *
 */
public class ParserFragment extends Fragment implements OnCompleteListener{

    private final String TEST_URL = "http://quotes.zennex.ru/api/v3/bash/quotes?sort=time";
    private Parser parser;
    private FileDownloadAsyncTask downloadingTask;
    private boolean isReady;
    private boolean isRunning;
    /** Флаг состояния завершеености загрузки файла */
    private final String IS_COMPLETE = "iscomplete";
    /** Флаг состояния выполнения загрузки файла */
    private final String IS_RUNNING = "isrunning";
    private TextView content;
    private SharedPreferences preferences;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.parser_fragment_layout, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        content = (TextView) getActivity().findViewById(R.id.parser_content_tv);
        parser = new Parser(getActivity());
        downloadingTask = new FileDownloadAsyncTask(getActivity());

        getActivity().findViewById(R.id.parser_dwnld_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isReady = false;
                downloadingTask = new FileDownloadAsyncTask(getActivity());
                downloadingTask.addOnCompleteListener(ParserFragment.this);
                downloadingTask.execute(TEST_URL);
            }
        });

        if(isReady){
            content.setText(parser.parse(FileDownloadAsyncTask.FILE_NAME));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isReady = preferences.getBoolean(IS_COMPLETE, false);
        isRunning = preferences.getBoolean(IS_RUNNING, false);
        if(isReady){
            content.setText(parser.parse(FileDownloadAsyncTask.FILE_NAME));
        }
        if(isRunning && !isReady){
            downloadingTask = new FileDownloadAsyncTask(getActivity());
            downloadingTask.addOnCompleteListener(ParserFragment.this);
            downloadingTask.execute(TEST_URL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = preferences.edit();
        String status = downloadingTask.getStatus().toString();
        if(status.equals("RUNNING")){
            downloadingTask.cancel(true);
            editor.putBoolean(IS_RUNNING, true);
        }
        editor.putBoolean(IS_COMPLETE, isReady);
        editor.commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onComplete() {
        content.setText(parser.parse(FileDownloadAsyncTask.FILE_NAME));
        isReady = true;
    }
}
