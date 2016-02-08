package com.example.dam.ztts;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private TextToSpeech tts;
    private EditText et;
    private TextView tv;
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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS){
            //se puede reproducir
            ok = true;
        } else{
            //no se puede reproducir
            ok = false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== 0) {
            if(resultCode== TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                tts= new TextToSpeech(this, this);
                tts.setLanguage(Locale.getDefault());
            } else{
                Intent intent= new Intent();
                intent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(intent);
            }
        }
        if(requestCode== 1) {
            ArrayList<String> textos = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            tv.setText(textos.get(0));
            for (String s : textos) {
                tv.append(s+"\n");
            }
        }
    }

    public void init(){
        et = (EditText) findViewById(R.id.editText);
        tv = (TextView) findViewById(R.id.textView);
        Intent intent= new Intent();
        intent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(intent, 0);

    }

    public void leer(View v){
        if(ok) {
            tts.setLanguage(Locale.CHINA);
            tts.setPitch((float) 1.0);
            tts.speak(et.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
            Toast.makeText(this, "Se puede", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No se puede", Toast.LENGTH_SHORT).show();
        }
    }
    public void leer1(View v){
        if(ok) {
            tts.setLanguage(new Locale("es", "ES"));
            tts.setPitch((float) 100.0);
            tts.speak(et.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
            Toast.makeText(this, "Se puede", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No se puede", Toast.LENGTH_SHORT).show();
        }
    }
    public void leer2(View v){
        if(ok) {
            tts.setLanguage(new Locale("es", "ES"));
            tts.setPitch((float) 50.0);
            tts.speak(et.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
            Toast.makeText(this, "Se puede", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No se puede", Toast.LENGTH_SHORT).show();
        }
    }
    public void leer3(View v){
        if(ok) {
            tts.setLanguage(Locale.UK);
            tts.setPitch((float) 1.0);
            tts.speak(et.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
            Toast.makeText(this, "Se puede", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No se puede", Toast.LENGTH_SHORT).show();
        }
    }

    public void escribir(View v){
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "es-ES");
        i.putExtra(RecognizerIntent.EXTRA_PROMPT,"Habla ahora");
        i.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,3000);
        startActivityForResult(i, 1);
    }

}
