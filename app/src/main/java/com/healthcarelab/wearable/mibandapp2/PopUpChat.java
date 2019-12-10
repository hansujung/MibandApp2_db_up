package com.healthcarelab.wearable.mibandapp2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.zip.Inflater;

public class PopUpChat extends Activity {

    private InputMethodManager imm;

    private EditText txtText;
    ListView listview;
    String myname,address;

    Button button, butt_end;
    private ArrayList<String> arrayList;
    private  ArrayAdapter<String> adapter;

    private final String ChatURL = "http://localhost/setChatting.php?";
    phpInsert phpInsert;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_activity);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        listview = (ListView)findViewById(R.id.listv);

        String[] items = {"Miband 사용자들의 채팅방입니다."};
        arrayList = new ArrayList<>(Arrays.asList(items));
       // arrayList = new ArrayList<>(Arrays.asList(items));
        adapter=new ArrayAdapter<String>(this,R.layout.list_item,R.id.txtitem,arrayList);
        listview.setAdapter(adapter);
        button = (Button)findViewById(R.id.button);
        butt_end = (Button) findViewById(R.id.butt_end);

        //UI 객체생성
        txtText = (EditText)findViewById(R.id.txtText);
        Intent intent = getIntent();
        myname = intent.getExtras().getString("message");
        address = intent.getExtras().getString("address");
        String text = String.valueOf(txtText.getText());
        Log.d("tag", "text" + text);
        txtText.setText(myname+" : ");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String newItem = txtText.getText().toString();
               arrayList.add(newItem);
               adapter.notifyDataSetChanged();
                String id = address;
                String content = newItem;

                phpInsert = new phpInsert();
                phpInsert.execute(ChatURL + "&id='" + id + "'&content='" + content + "'");

                imm.hideSoftInputFromWindow(txtText.getWindowToken(), 0);
            }
        });

        butt_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

