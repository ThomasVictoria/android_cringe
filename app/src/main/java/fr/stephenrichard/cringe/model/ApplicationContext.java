package fr.stephenrichard.cringe.model;

import android.app.Application;

/**
 * Created by thomas on 09/06/16.
 */
public class ApplicationContext extends Application {

    private static ApplicationContext context;

    public void onCreate() {
        super.onCreate();
        ApplicationContext.context = getAppContext();
    }

    public static ApplicationContext getAppContext() {
        return ApplicationContext.context;
    }


}
