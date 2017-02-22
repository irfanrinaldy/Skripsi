package com.pipam.skripsi;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

/**
 * Created by Pipam on 12/22/2016.
 */
public class ViewMessage extends AppCompatActivity {
    private TextToSpeech tts;
    private TextView number, date, message;
    private ImageView forward, delete, speak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewmessage);


//        tvTitleMessage = (TextView) findViewById(R.id.tv_title_message);
//        tvSubtitleMessage = (TextView) findViewById(R.id.tv_subtitle_message);
//        number = (TextView) findViewById(R.id.tvNumber);
//        date = (TextView) findViewById(R.id.tvDate);
        message = (TextView) findViewById(R.id.tvMessage);
        forward = (ImageView) findViewById(R.id.btnForward);
        delete = (ImageView) findViewById(R.id.btnDelete);
        speak = (ImageView) findViewById(R.id.btnSpeak);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();

        setTitle(i.getStringExtra("no"));
        toolbar.setSubtitle(i.getStringExtra("date"));
        message.setText(i.getStringExtra("msg"));

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent click = new Intent(ViewMessage.this, New.class);
                click.putExtra("message", message.getText());
                startActivity(click);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialogs.showConfirmation(ViewMessage.this, R.string.deletemessagedialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int which) {
                        Intent i = getIntent();
                        String id_pesan_hapus = i.getStringExtra("idpesan");
                        String id_thread_hapus = i.getStringExtra("idthread");

                        Uri deleteUri = Uri.parse("content://sms");
                        getContentResolver().delete(deleteUri, "thread_id=? and _id=?", new String[]{
                                String.valueOf(id_thread_hapus),
                                String.valueOf(id_pesan_hapus)
                        });
                        finish();
                        Toast.makeText(ViewMessage.this, "Pesan Terhapus", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(new Locale("id", "ID"));
                }
            }
        });
        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toSpeak = message.getText().toString();
                Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
                tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home btn_bg
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onPause() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onPause();
    }
}
