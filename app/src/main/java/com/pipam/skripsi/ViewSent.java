package com.pipam.skripsi;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Pipam on 1/26/2017.
 */

public class ViewSent extends AppCompatActivity {
    private TextView message;
    private ImageView forward, delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewsent);

        message = (TextView) findViewById(R.id.tvMessage);
        forward = (ImageView) findViewById(R.id.btnForward);
        delete = (ImageView) findViewById(R.id.btnDelete);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent i = getIntent();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(i.getStringExtra("no"));
        toolbar.setSubtitle(i.getStringExtra("date"));
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

                        Uri deleteUri = Uri.parse("content://sms");
                        getContentResolver().delete(deleteUri, "thread_id=? and _id=?", new String[]{
                                String.valueOf(id_thread_hapus),
                                String.valueOf(id_pesan_hapus)
                        });
                        finish();
                        Toast.makeText(ViewSent.this, "Pesan Terhapus", Toast.LENGTH_SHORT).show();
                    }
                });
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
}
