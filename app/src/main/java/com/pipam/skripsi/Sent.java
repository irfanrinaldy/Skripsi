package com.pipam.skripsi;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

public class Sent extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(getString(R.string.sent));

        listView = (ListView) findViewById(R.id.listViewSent);

        getSentMessage();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cslihatpesan = (Cursor) listView.getItemAtPosition(position);
                String view_penerima = cslihatpesan.getString(cslihatpesan.getColumnIndexOrThrow("address"));
                String view_isipesan = cslihatpesan.getString(cslihatpesan.getColumnIndexOrThrow("body"));
                String waktu = cslihatpesan.getString(cslihatpesan.getColumnIndexOrThrow("date"));

                Long l = Long.parseLong(waktu);
                Date d = new Date(l);
                String date = DateFormat.getDateInstance(DateFormat.LONG).format(d);
                String time = DateFormat.getTimeInstance().format(d);
                String view_waktu = date + " " + time;
                String view_idpesan = cslihatpesan.getString(cslihatpesan.getColumnIndexOrThrow("_id"));
                String view_thread = cslihatpesan.getString(cslihatpesan.getColumnIndexOrThrow("thread_id"));
                Intent clickIntent = new Intent(Sent.this, ViewSent.class);

                Uri conctactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(view_penerima));
                Cursor cursor1 = getContentResolver().query(conctactUri, null, null, null, null);
                ContentResolver contentResolver = getContentResolver();
                int size = cursor1.getCount();
                if (size > 0 && cursor1 != null) {
                    Log.d("SMS", "masuk seleksi");
                    for (int i = 0; i < size; i++) {
                        cursor1.moveToPosition(i);
                        String id1 = cursor1.getString(cursor1.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                        Log.d("SMS", id1);
                        Cursor phoneCursor = contentResolver.query(conctactUri, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =? ", new String[]{id1}, null);

                        if (phoneCursor.moveToFirst()) {
                            String namaKontak = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
//                                String isiPesan = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA.toLowerCase()));
                            phoneCursor.close();
                            clickIntent.putExtra("no", namaKontak);
                        } else {
                            clickIntent.putExtra("no", view_penerima);
//                                tvMessage.setText(pesanDB);
                        }
                    }
                    cursor1.close();
                } else {
                    clickIntent.putExtra("no", view_penerima);
                }
//                send data to viewmessage
                clickIntent.putExtra("msg", view_isipesan);
                clickIntent.putExtra("idpesan", view_idpesan);
                clickIntent.putExtra("idthread", view_thread);
                clickIntent.putExtra("date", view_waktu);
                startActivity(clickIntent);
            }
        });

    }

    private void getSentMessage() {
        Uri uriSMSURI = Uri.parse("content://sms/sent");
        final String[] reqCols = new String[]{"address", "body"};
        Cursor cursor = getContentResolver().query(uriSMSURI, null, null, null, null);
        int[] to = new int[]{R.id.penerima, R.id.isi};
        SimpleCursorAdapter simpleCursorAdapter;
        simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.content_outbox, cursor, reqCols, to, 0);
        simpleCursorAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

                if (columnIndex == 2) {
                    TextView tvContact = (TextView) view;
                    String penerimaDB = cursor.getString(cursor.getColumnIndex("address"));
                    Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(penerimaDB));
                    Cursor cursor1 = getContentResolver().query(contactUri, null, null, null, null);
                    ContentResolver contentResolver = getContentResolver();

                    int size = cursor1.getCount();
                    if (size > 0 && cursor1 != null) {
                        for (int i = 0; i < size; i++) {
                            cursor1.moveToPosition(i);
                            String id1 = cursor1.getString(cursor1.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                            Cursor phoneCursor = contentResolver.query(contactUri, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =? ", new String[]{id1}, null);

                            if (phoneCursor.moveToFirst()) {
                                String namaKontak = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                                phoneCursor.close();
                                tvContact.setText(namaKontak);
                            } else {
                                tvContact.setText(penerimaDB);
                            }
                        }
                        cursor1.close();
                    } else {
                        tvContact.setText(penerimaDB);
                    }
                    return true;
                }
                return false;
            }
        });
        listView.setAdapter(simpleCursorAdapter);
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

    @Override
    protected void onResume() {
        super.onResume();
        getSentMessage();
    }
}