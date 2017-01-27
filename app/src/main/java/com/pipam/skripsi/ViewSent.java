package com.pipam.skripsi;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Pipam on 1/26/2017.
 */

public class ViewSent extends AppCompatActivity {
    TextView number, date, message;
    Button forward, delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewsent);

        number = (TextView) findViewById(R.id.tvNumber);
        date = (TextView) findViewById(R.id.tvDate);
        message = (TextView) findViewById(R.id.tvMessage);
        forward = (Button) findViewById(R.id.btnForward);
        delete = (Button) findViewById(R.id.btnDelete);
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
                Intent click = new Intent(ViewSent.this, New.class);
                click.putExtra("message", message.getText());
                startActivity(click);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialogs.showConfirmation(ViewSent.this, R.string.deletemessagedialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int which) {
                        Intent i = getIntent();
                        String id_pesan_hapus = i.getStringExtra("idpesan");
                        String id_thread_hapus = i.getStringExtra("idthread");

                        Uri deleteUri = Uri.parse("content://sms/sent");
                        getContentResolver().delete(deleteUri, "thread_id=? and _id=?", new String[]{
                                String.valueOf(id_thread_hapus),
                                String.valueOf(id_pesan_hapus)
                        });
                        finish();
                        Toast.makeText(ViewSent.this, "Pesan Terhapus", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                });
            }
        });
    }
}
