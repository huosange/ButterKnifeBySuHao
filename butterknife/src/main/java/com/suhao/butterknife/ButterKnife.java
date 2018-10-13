package com.suhao.butterknife;

import android.app.Activity;

public class ButterKnife {

    public static void bind(Activity activity){
        String className=activity.getClass().getName()+"$ViewBinder";
        try {
            Class viewBindClass=Class.forName(className);
            ViewBinder binder= (ViewBinder) viewBindClass.newInstance();
            binder.bind(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
