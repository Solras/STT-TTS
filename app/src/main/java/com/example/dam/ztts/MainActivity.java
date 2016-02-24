package com.example.dam.ztts;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.dam.ztts.bot.ChatterBot;
import com.example.dam.ztts.bot.ChatterBotFactory;
import com.example.dam.ztts.bot.ChatterBotSession;
import com.example.dam.ztts.bot.ChatterBotType;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private TextToSpeech tts;
    private ScrollView sv;
    private LinearLayout lv;
    private boolean ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                escribir();

            }
        });
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tts.shutdown();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            //se puede reproducir
            ok = true;
        } else {
            //no se puede reproducir
            ok = false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                tts = new TextToSpeech(this, this);
                tts.setLanguage(Locale.getDefault());
            } else {
                Intent intent = new Intent();
                intent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(intent);
            }
        }
        if (requestCode == 1) {
            ArrayList<String> textos = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String texto = textos.get(0);
            /*--- Añadir textView ---*/
            TextView tv = new TextView(this);
            tv.setText(texto);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.gravity = Gravity.RIGHT;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tv.setBackground(getDrawable(R.drawable.shapetv));
            }
            lv.addView(tv,params);
            /*--- Añadir textView ---*/
            Respuesta r = new Respuesta();
            r.execute(texto);
        }
    }

    public void init() {
        Intent intent = new Intent();
        sv = (ScrollView) findViewById(R.id.sv);
        lv = (LinearLayout) findViewById(R.id.lv);

        intent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(intent, 0);

    }



    public void leer(String a) {
        if (ok) {
            tts.setLanguage(new Locale("es", "ES"));
            tts.setPitch((float) 1.0);
            tts.speak(a, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void escribir() {
        Escritura e = new Escritura();
        e.execute();
    }

    public class Escritura extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "es-ES");
            i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla ahora");
            i.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 2000);
            startActivityForResult(i, 1);
            return null;
        }

    }

    public class Respuesta extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            ChatterBotFactory factory = new ChatterBotFactory();

            ChatterBot bot1;
            try {
                bot1 = factory.create(ChatterBotType.CLEVERBOT);
                ChatterBotSession bot1session = bot1.createSession();
                String s = params[0];
                Log.v("CHAT", "background: " + s);
                return bot1session.think(s);
            } catch (Exception e) {
                Log.v("CHAT", "excepcion: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String out) {
            TextView tv = new TextView(MainActivity.this);
            out = corregirCaracteres(out);  
            leer(out);
            tv.setText(out);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.gravity = Gravity.LEFT;
            params.bottomMargin = 5;
            params.topMargin = 5;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tv.setBackground(getDrawable(R.drawable.shapetv));
            }
            lv.addView(tv,params);
        }
    }

    public String corregirCaracteres(String s) {
        s.replace("&aacute;", "a");
        s.replace("&eacute;", "e");
        s.replace("&iacute;", "i");
        s.replace("&oacute;", "o");
        s.replace("&uacute;", "u");
        s.replace("&ntilde;", "ñ");
        return s;
    }
}
