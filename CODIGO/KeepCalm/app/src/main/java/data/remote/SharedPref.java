package data.remote;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import data.model.History;

import static android.content.Context.MODE_PRIVATE;

public class SharedPref {

    //Para construir esta clase: SharedPreference(getApplicationContext())
    public static final String PREFS_KEY = "sharedPref";
    private Context context;

    public SharedPref(Context context){
        this.context=context;
    }

    public void savePreference(History history) {
        int indice;
        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        indice = preferences.getInt("indice", 0);

        if(indice > 3 ){
            String algo = getHistoryPreference();
            Log.i("mayor que 3", algo);
        }


        String historyList = preferences.getString("historyList", "");

        SharedPreferences.Editor editor;
        editor = preferences.edit();

        editor.putString("historyList", historyList + (indice+1) + "-" + history);
        editor.putInt("indice", (indice+1));
        editor.commit();
    }
/*
    public void savePreference(Context context, History history) {
        int indice;
        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        indice = preferences.getInt("indice", 0);
        String historyList = preferences.getString("historyList", "");

        SharedPreferences settings = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();

        editor.putString("historyList", historyList + (indice+1) + "-" + history);
        editor.putInt("indice", (indice+1));
        editor.commit();
    }*/

    public String getHistoryPreference() {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        int indice = preferences.getInt("indice", 0);
        if(indice == 0)
            return "";
        String historyList = preferences.getString("historyList", "");
        return historyList;
    }

    public void ClearPreference() {
        SharedPreferences settings = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();

        editor.putString("historyList", "");
        editor.putInt("indice", 0);
        editor.commit();
    }

    public void saveUser(String mail, String password) {
        deleteUser();
        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = preferences.edit();

        editor.putString("user", mail);
        editor.putString("pass", password);
        editor.commit();
        Log.i("User guardado: ", mail);
        Log.i("pass guardado: ", password);
    }

    public void deleteUser() {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = preferences.edit();

        editor.putString("user", "");
        editor.putString("pass", "");
        editor.commit();
        Log.i("User y pass borrado: ", " ");
    }

    public String getUser() {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        String user = preferences.getString("user", "");

        Log.i("User Obtenido: ", user);

        return user;
    }
    public String getPass() {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        String pass = preferences.getString("pass", "");
        Log.i("pass Obtenido: ", pass);
        return pass;
    }
}
