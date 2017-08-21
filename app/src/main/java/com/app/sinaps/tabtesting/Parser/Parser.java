package com.app.sinaps.tabtesting.Parser;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Sinaps on 18.08.2017.
 */

/** Класс осуществляющий дешифровку и форматирования текста
 *
 */

public class Parser {
    private Context context;
    /** Коллекчия шаблонов для поиска и замены */
    private ArrayList<Map<String, String>> patterns;
    private final String PATTERN = "pattern";
    private final String REPLACER = "replacer";

    public Parser(Context context) {
        this.context = context;

        patterns = new ArrayList<>();
        fiilPatterns();
    }
    /** Осуществляет набивку рабочих шаблонов
     *
     */
    private void fiilPatterns(){
        addPattern("<br>","\n");
        addPattern("&quot;","\"");
        addPattern("&gt;",">");
        addPattern("&lt;","<");

        addPattern("\"id\":[0-9]+,\"description\":\"",
                "\n-------------------------" +
                "---------------------------------\n");
        addPattern("\",\"time\":\"",
                "\n\n-------------------------------" +
                "---------------------------\n*date: ");
        addPattern("[}],[{]","");
        addPattern("[{]\"total","Total");
        addPattern(",\"quotes\":.[{]","\n");
        addPattern("[}].[}]","");
    }
    /** Добавляет новый шаблон
     *
     */
    public void addPattern(String pattern, String replacer){
        Map<String, String> temp = new HashMap<>();
        temp.put(PATTERN, pattern);
        temp.put(REPLACER, replacer);
        patterns.add(temp);
    }

    /** Производит поиск и замену текста в соответствии с рабочим набором шаблонов
     *
     */
    public String parse(String fileName) {
        StringBuilder builder = new StringBuilder();
        String res = " ";

        try (BufferedReader br = new BufferedReader(new InputStreamReader(context.openFileInput(fileName)))) {

            Pattern pattern;
            Matcher matcher;

            String temp = " ";
            while ((temp = br.readLine()) != null) {
                builder.append(temp);
            }

            res = String.valueOf(builder);


            for(Map<String, String> map:patterns){
                pattern = Pattern.compile(map.get(PATTERN));
                matcher = pattern.matcher(res);
                res = matcher.replaceAll(map.get(REPLACER));
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
}
