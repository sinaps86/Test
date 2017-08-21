package com.app.sinaps.tabtesting.Parser;

/**
 * Created by Sinaps on 19.08.2017.
 */

/** Интерфейс источника событий завершения асинхронного скачивания
 *
 */

public interface CompleteSource {

    public void addOnCompleteListener(OnCompleteListener listener);
    public void deleteOnCompleteListener(OnCompleteListener listener);
    public void notifyListeners();
}
