package com.app.sinaps.tabtesting.Scaler;

/**
 * Created by Sinaps on 13.08.2017.
 */

/** Интерфейс используемый для непрерывной перерисовки комопнента окна
 *
 */
public interface MiniGame {
    public void update(float deltaTime);
    public void present(float deltaTime);
}
