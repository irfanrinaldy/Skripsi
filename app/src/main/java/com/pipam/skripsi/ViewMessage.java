package com.pipam.skripsi;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

/**
 * Created by Pipam on 12/22/2016.
 */
public class ViewMessage extends AppCompatActivity {
    TextToSpeech tts;
    TextView number, date, message;
    Button forward, delete, speak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewmessage);

        number = (TextView) findViewById(R.id.tvNumber);
        date = (TextView) findViewById(R.id.tvDate);
        message = (TextView) findViewById(R.id.tvMessage);
        forward = (Button) findViewById(R.id.btnForward);
        delete = (Button) findViewById(R.id.btnDelete);
        speak = (Button) findViewById(R.id.btnSpeak);
    }
    @Override
    protected void onStart() {
        super.onStart();
        Intent i = getIntent();
        number.setText(i.getStringExtra("no"));
        date.setText(i.getStringExtra("date"));
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

                        Uri deleteUri =Uri.parse("content://sms");
                        getContentResolver().delete(deleteUri, "thread_id=? and _id=?", new String[] {
                                String.valueOf(id_thread_hapus),
                                String.valueOf(id_pesan_hapus)
                        });
                        finish();
                        Toast.makeText(ViewMessage.this, "Pesan Terhapus", Toast.LENGTH_SHORT).show();
                        onBackPressed();
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
                Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
                tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }

    public void onPause() {
        if (tts !=null) {
            tts.stop();
            tts.shutdown();
        }
        super.onPause();
    }
}
