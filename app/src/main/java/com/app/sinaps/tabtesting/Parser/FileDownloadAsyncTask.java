package com.app.sinaps.tabtesting.Parser;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import com.app.sinaps.tabtesting.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Sinaps on 15.08.2017.
 */

/** Класс выполнения асинхронной загрузки файла из сети
 *
 */
public class FileDownloadAsyncTask extends AsyncTask<String, Void, Boolean> implements CompleteSource{

    private ArrayList<OnCompleteListener> listeners;
    private Context context;
    private ProgressDialog progressDialog;
    /** Имя файла сохранения скаченной информации */
    public static final String FILE_NAME = "rawtest.txt";

    public FileDownloadAsyncTask(Context context){
        this.context = context;
        listeners = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getResources().getString(R.string.parser_wait_dialor_message));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);  ///////????????
        progressDialog.show();

    }

    @Override
    protected Boolean doInBackground(String... params) {
        char[] chars = new char[4];
        char uniChar = ' ';
        BufferedReader br = null;
        PrintWriter pw = null;
        HttpURLConnection httpConn = null;
        try{
            URL url = new URL(params[0]);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestMethod("GET");
            httpConn.connect();

            if(httpConn.getResponseCode() == HttpURLConnection.HTTP_OK){
                br = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
                pw = new PrintWriter(new OutputStreamWriter(
                        context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)));
                int temp = 0;
                while ((temp = br.read()) != -1){
                    switch(temp){
                        case '\\':
                            if(br.read() == 'u'){
                                chars[0] = (char)br.read();
                                chars[1] = (char)br.read();
                                chars[2] = (char)br.read();
                                chars[3] = (char)br.read();
                                String uniWord = String.valueOf(chars);
                                uniChar = (char)Integer.parseInt(uniWord, 16);
                            }else {
                                pw.write("\\");
                                pw.write(uniChar);
                                continue;
                            }
                            break;
                        default:
                            uniChar = (char) temp;
                            break;
                    }
                    pw.write(uniChar);
                }
            }  else {
                Toast toast = Toast.makeText(context,
                        context.getResources().getString(R.string.parser_download_fail_connect_message),
                        Toast.LENGTH_SHORT);
                toast.show();
                return false;
            }

        }catch (IOException e){
            e.printStackTrace();
            return false;
        }finally {
            if(httpConn != null)
                httpConn.disconnect();
            if(br != null){
                try {
                    br.close();
                    pw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean resault) {
        super.onPostExecute(resault);
        if(resault)
            notifyListeners();
        this.progressDialog.dismiss();
    }

    @Override
    public void addOnCompleteListener(OnCompleteListener listener) {
        listeners.add(listener);
    }

    @Override
    public void deleteOnCompleteListener(OnCompleteListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void notifyListeners() {
        for(OnCompleteListener listener:listeners){
            listener.onComplete();
        }
    }
}
