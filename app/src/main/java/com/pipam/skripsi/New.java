package com.pipam.skripsi;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.util.ArrayList;

public class New extends AppCompatActivity {

    private EditText nomorkontak, text;
    private ImageView speechToText;
    private static final int CONTACT_PICKER_RESULT = 1001;


    public static void start(Context context) {
        Intent intent = new Intent(context, New.class);
        context.startActivity(intent);
    }

    public void doLaunchContactPicker(View view) {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, uri);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String phone = "";
        Cursor contacts = null;
        try {
            if (resultCode == RESULT_OK) {
                switch (requestCode) {
                    case CONTACT_PICKER_RESULT:
                        Uri result = data.getData();
                        String id = result.getLastPathSegment();
                        contacts = getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone._ID + "=?", new String[]{id}, null);
                        int phoneIdx = contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA);
                        if (contacts.moveToFirst()) {
                            phone = contacts.getString(phoneIdx);
                            EditText phoneTxt = (EditText) findViewById(R.id.editTextPhoneNo);
                            phoneTxt.setText(phone);
                        } else {
                            Toast.makeText(this, "error", Toast.LENGTH_LONG).show();
                        }
                        break;

                    case 1: {
                        if (resultCode == RESULT_OK && null != data) {
                            ArrayList<String> teks = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                            text.setText(teks.get(0));
                        }
                        break;
                    }
                }
            } else {
                Toast.makeText(New.this, "Belum dipilih",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (contacts != null) {
                contacts.close();
            }
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        speechToText = (ImageView) findViewById(R.id.btnVoiceInput);
        ImageView send = (ImageView) findViewById(R.id.buttonSend);
        nomorkontak = (EditText) findViewById(R.id.editTextPhoneNo);
        text = (EditText) findViewById(R.id.editTextMessage);

        //mengambil isi pesan jika pesan diteruskan
        Intent intent = getIntent();
        if (intent.getStringExtra("message") != null) {
            text.setText(intent.getStringExtra("message"));
        }
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String pesan = text.getText().toString();
                String nomor = nomorkontak.getText().toString();
                if (pesan.length() > 0 && nomor.length() > 0) {
                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(nomor, null, pesan, null, null);
                        Log.d("SMS", "nomor : " + nomor + " pesan : " + pesan);
                        ContentValues values = new ContentValues();
                        values.put("address", nomor);
                        values.put("body", pesan);
                        getContentResolver().insert(
                                Uri.parse("content://sms/sent"), values);
                        Toast.makeText(New.this,
                                "Pesan berhasil dikirim", Toast.LENGTH_SHORT)
                                .show();
                        finish();
                    } catch (Exception e) {
                        Toast.makeText(New.this,
                                "Nomor atau isi pesan masih kosong",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(New.this,
                            "Nomor atau isi pesan masih kosong",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        speechToText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent1.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "id-ID");

                try {
                    startActivityForResult(intent1, 1);
                    text.setText("");
                } catch (ActivityNotFoundException a) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Perangkat tidak mendukung", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }
}