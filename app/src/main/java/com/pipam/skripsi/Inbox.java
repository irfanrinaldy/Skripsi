package com.pipam.skripsi;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.text.DateFormat;
import java.util.Date;


public class Inbox extends AppCompatActivity {

    String[] m;
    ListView listView;
    String pesanTerpilih;
    TextView lblMsg, lblNo;
    private FloatingActionButton fab;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        listView = (ListView) findViewById(R.id.listViewSMS);
        Uri uriSMSURI = Uri.parse("content://sms/inbox");
        final String[] reqCols = new String[]{"address", "body"};
        Cursor cursor = getContentResolver().query(uriSMSURI, null, null, null, null);
        int[] to = new int[]{R.id.pengirim, R.id.isi};
        SimpleCursorAdapter simpleCursorAdapter;
        simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.content_inbox, cursor, reqCols, to, 0);
        simpleCursorAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                //ubah nomor dengan contact name
                if (columnIndex == 2) {

                    TextView tvContact = (TextView) view;
//                    TextView tvMessage = (TextView) view;
                    String pengirimDB = cursor.getString(cursor.getColumnIndex("address"));
//                    String pesanDB = cursor.getString(cursor.getColumnIndex("message"));

                    //get contact name
                    Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(pengirimDB));
                    Cursor cursor1 = getContentResolver().query(contactUri, null, null, null, null);
                    ContentResolver contentResolver = getContentResolver();

                    int size = cursor1.getCount();
                    if (size > 0 && cursor1 != null) {
                        Log.d("SMS", "masuk seleksi");
                        for (int i = 0; i < size; i++) {
                            cursor1.moveToPosition(i);
                            String id1 = cursor1.getString(cursor1.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                            Log.d("SMS", id1);
                            Cursor phoneCursor = contentResolver.query(contactUri, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =? ", new String[]{id1}, null);

                            if (phoneCursor.moveToFirst()) {
                                String namaKontak = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
//                                String isiPesan = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA.toLowerCase()));
                                phoneCursor.close();
                                tvContact.setText(namaKontak);
                            } else {
                                tvContact.setText(pengirimDB);
//                                tvMessage.setText(pesanDB);
                            }

                        }
                        cursor1.close();
                    } else {
                        tvContact.setText(pengirimDB);
                    }
                    return true;
                }
                return false;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cslihatpesan = (Cursor) listView.getItemAtPosition(position);
                String view_pengirim = cslihatpesan.getString(cslihatpesan.getColumnIndexOrThrow("address"));
                String view_isipesan = cslihatpesan.getString(cslihatpesan.getColumnIndexOrThrow("body"));
                String waktu = cslihatpesan.getString(cslihatpesan.getColumnIndexOrThrow("date"));

                Long l = Long.parseLong(waktu);
                Date d = new Date(l);
                String date = DateFormat.getDateInstance(DateFormat.LONG).format(d);
                String time = DateFormat.getTimeInstance().format(d);
                String view_waktu = date + " " + time;
                String view_idpesan = cslihatpesan.getString(cslihatpesan.getColumnIndexOrThrow("_id"));
                String view_thread = cslihatpesan.getString(cslihatpesan.getColumnIndexOrThrow("thread_id"));
                Intent click = new Intent(Inbox.this, ViewMessage.class);

//                get contact name
                Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(view_pengirim));
                Cursor cursor1 = getContentResolver().query(contactUri, null, null, null, null);
                ContentResolver contentResolver = getContentResolver();

                int size = cursor1.getCount();
                if (size > 0 && cursor1 != null) {
                    Log.d("SMS", "masuk seleksi");
                    for (int i = 0; i < size; i++) {
                        cursor1.moveToPosition(i);
                        String id1 = cursor1.getString(cursor1.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                        Log.d("SMS", id1);
                        Cursor phoneCursor = contentResolver.query(contactUri, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =? ", new String[]{id1}, null);

                        if (phoneCursor.moveToFirst()) {
                            String namaKontak = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
//                                String isiPesan = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA.toLowerCase()));
                            phoneCursor.close();
                            click.putExtra("no", namaKontak);
                        } else {
                            click.putExtra("no", view_pengirim);
//                                tvMessage.setText(pesanDB);
                        }
                    }
                    cursor1.close();
                } else {
                    click.putExtra("no", view_pengirim);
                }
//                send data to viewmessage
                click.putExtra("msg", view_isipesan);
                click.putExtra("idpesan", view_idpesan);
                click.putExtra("idthread", view_thread);
                click.putExtra("date", view_waktu);
                startActivity(click);
            }
        });
        listView.setAdapter(simpleCursorAdapter);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                New.start(Inbox.this);
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inbox, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.about:
                Intent delIntent = new Intent(Inbox.this, About.class);
                Inbox.this.startActivity(delIntent);
                return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if(id== R.id.sendActivity){
            Intent sendIntent = new Intent(Inbox.this, Sent.class);
            Inbox.this.startActivity(sendIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Inbox Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
