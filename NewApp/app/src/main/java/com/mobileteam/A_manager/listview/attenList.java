package com.mobileteam.A_manager.listview;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mobileteam.A_manager.R;
import com.mobileteam.A_manager.calendar_page.popup_activity;
import com.mobileteam.A_manager.database.PreferenceManager;
import com.mobileteam.A_manager.database.Students;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

public class attenList extends AppCompatActivity {
    private ListView listview;
    private MyAdapter adapter;
    private EditText editTextFilter;
    private EditText edit_memo;
    private Realm realm;
    private RealmResults<Students> stu;
    private ArrayList<Students> studentlist;
    private RealmResults<Students> attendstudents;
    private TextView today_date_text;
    private String saved_date, new_date;
    private ArrayList<String> today_attend_id;
    private int month, day;
    private Calendar cal;
    int showday, showmonth;
    Calendar calcul_cal;
    String file, file_name;
    ImageView check_date, check_message;
    Button add_memo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendlistview);


        check_date = findViewById(R.id.check_date);
        check_message = findViewById(R.id.check_message);
        cal=Calendar.getInstance();
        calcul_cal= Calendar.getInstance();
        today_date_text = findViewById(R.id.today_date);
        int month = cal.get(Calendar.MONTH) + 1;
        today_date_text.setText(" " + month + "??? " + cal.get(Calendar.DATE) + "??? ??????");
        edit_memo = findViewById(R.id.show_memo);
        realm=Realm.getDefaultInstance();
        add_memo = findViewById(R.id.add_memo);

        saved_date = PreferenceManager.getString(this, "attend_date");
        new_date = cal.get(Calendar.YEAR) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.DATE);
        day = cal.get(Calendar.DATE);
        showmonth = month;
        showday = day;
        if (!saved_date.equals(new_date)) {
            PreferenceManager.setString(this, "attend_date", new_date);
            realm.beginTransaction();
            stu = realm.where(Students.class).equalTo("attendchk", true).findAll();
            for(Students student : stu){
                student.setAttendchk(false);
            }
            stu = realm.where(Students.class).equalTo("attended", true).findAll();
            for(Students student : stu){
                student.setAttended(false);
            }
            realm.commitTransaction();
        }
        //?????? ????????? ???????????? id??? ????????????
        today_attend_id = PreferenceManager.getArrayList(this, saved_date + "attend");
        file_name = calcul_cal.get(Calendar.MONTH)+""+calcul_cal.get(Calendar.DATE);
        read_memo(file_name);
        getStudent();
        CreateView();
        // filtering

        add_memo.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                FileOutputStream fos = null;

                try {
                    String readDay = calcul_cal.get(Calendar.MONTH)+""+calcul_cal.get(Calendar.DATE)+".txt";
                    fos = openFileOutput(readDay, MODE_NO_LOCALIZED_COLLATORS); //MODE_WORLD_WRITEABLE
                    String content = edit_memo.getText().toString();

                    // String.getBytes() = ???????????? ??????????????? ???????
                    fos.write(content.getBytes());
                    //fos.flush();
                    fos.close();

                    // getApplicationContext() = ?????? ?????????.this ?

                } catch (Exception e) { // Exception - ?????? ?????? ?????? ?????? // FileNotFoundException , IOException
                    e.printStackTrace();
                }
            }


        });

        editTextFilter=(EditText)findViewById(R.id.edittxt);
        editTextFilter.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text=editTextFilter.getText().toString().toLowerCase(Locale.getDefault());
                Log.i("Text",text);
                adapter.filter(text);
            }
        });


        //listview ????????? --> ??????
        listview.setOnItemClickListener((new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Students students=(Students)parent.getItemAtPosition(position);
                TextView txtResult=(TextView)findViewById(R.id.txtText);
                Intent intent=new Intent(getApplicationContext(), popup_activity.class);
                intent.putExtra("stu_Name", students.getName());
                intent.putExtra("cur_year", cal.get(Calendar.YEAR));
                intent.putExtra("cur_month", cal.get(Calendar.MONTH));
                intent.putExtra("cur_date", cal.get(Calendar.DATE));
                intent.putExtra("new_date?",1);
                startActivity(intent);


                //test ??? (????????? ???)
                Intent date_intent = getIntent();
                String new_date = date_intent.getStringExtra("new_date");
            }
        }));

    }

    private void CreateView(){
        //Adapter ??????
        adapter=new MyAdapter(this, studentlist,1);

        // ???????????? ?????? ??? Apater ??????
        listview=(ListView)findViewById(R.id.listview2);
        listview.setAdapter(adapter);
    }

    private void CreatenView(){
        //Adapter ??????
        adapter=new MyAdapter(this, studentlist,4);

        // ???????????? ?????? ??? Apater ??????
        listview=(ListView)findViewById(R.id.listview2);
        listview.setAdapter(adapter);
    }

    private void getStudent(){

        int day_of_week=calcul_cal.get(Calendar.DAY_OF_WEEK); //?????? ?????? ????????? ??????
        studentlist=new ArrayList<>();

        if(day_of_week==1){//???
            stu = realm.where(Students.class).notEqualTo("sun", -1).findAll();

        }
        else if(day_of_week==2){//???
            stu = realm.where(Students.class).notEqualTo("mon", -1).findAll();

        }
        else if(day_of_week==3){//???
            stu = realm.where(Students.class).notEqualTo("tue", -1).findAll();

        }
        else if(day_of_week==4){//???
            stu = realm.where(Students.class).notEqualTo("wed", -1).findAll();

        }
        else if(day_of_week==5){//???
            stu = realm.where(Students.class).notEqualTo("thu", -1).findAll();

        }
        else if(day_of_week==6){//???
            stu = realm.where(Students.class).notEqualTo("fri", -1).findAll();
        }
        else {//???
            stu = realm.where(Students.class).notEqualTo("sat", -1).findAll();
        }

        if(stu.size()>0)
            studentlist.addAll(realm.copyFromRealm(stu));
    }



    //?????? ?????? ????????? ????????? ???
    public void clicked_attend(View v){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("????????? ???????????? ????????? ???????????????.");
        alert.setPositiveButton("???", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                attendstudents = realm.where(Students.class).equalTo("attendchk", true).findAll();
                if(attendstudents.size() == 0){
                    Toast.makeText(attenList.this, "????????? ???????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                    return;
                }
                realm.beginTransaction();

                for(Students student : attendstudents){
                   // Log.i("?????????", student.getStd_id() + "");
                    student.setAttended(true);
                    student.setAttendchk(false);
                    today_attend_id.add(student.getStd_id() + "");
                }
                PreferenceManager.setArrayList(attenList.this, saved_date + "attend", today_attend_id);
                realm.commitTransaction();
                adapter.notifyDataSetChanged();
                getStudent();
                CreateView();
                Toast.makeText(attenList.this, "?????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
            }
        });
        alert.setNegativeButton("?????????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.show();
    }

    public void nextday(View view) {
        calcul_cal.add(Calendar.DATE, 1);
        today_date_text.setText(" " + (calcul_cal.get(Calendar.MONTH)+1) + "??? " + calcul_cal.get(Calendar.DATE) + "??? ??????");
        if((calcul_cal.get(Calendar.MONTH)+1)!=(cal.get(Calendar.MONTH)+1)||calcul_cal.get(Calendar.DATE)!=cal.get(Calendar.DATE)){
            getStudent();
            CreatenView();
            check_message.setVisibility(View.GONE);
            check_date.setVisibility(View.GONE);
        }
        if((calcul_cal.get(Calendar.MONTH)+1)==(cal.get(Calendar.MONTH)+1)&&calcul_cal.get(Calendar.DATE)==cal.get(Calendar.DATE)){
            getStudent();
            CreateView();
            check_message.setVisibility(View.VISIBLE);
            check_date.setVisibility(View.VISIBLE);
        }
        String date_file = calcul_cal.get(Calendar.MONTH)+""+ calcul_cal.get(Calendar.DATE) +".txt";
        read_memo(date_file);
    }

    //?????? ??? ?????? ????????? ???
    public void preday(View view) {
        calcul_cal.add(Calendar.DATE, -1);
        today_date_text.setText(" " + (calcul_cal.get(Calendar.MONTH)+1) + "??? " + calcul_cal.get(Calendar.DATE) + "??? ??????");
        if((calcul_cal.get(Calendar.MONTH)+1)!=(cal.get(Calendar.MONTH)+1)||calcul_cal.get(Calendar.DATE)!=cal.get(Calendar.DATE)){
            getStudent();
            CreatenView();
            check_message.setVisibility(View.GONE);
            check_date.setVisibility(View.GONE);
        }
        if((calcul_cal.get(Calendar.MONTH)+1)==(cal.get(Calendar.MONTH)+1)&&calcul_cal.get(Calendar.DATE)==cal.get(Calendar.DATE)){
            getStudent();
            CreateView();
            check_message.setVisibility(View.VISIBLE);
            check_date.setVisibility(View.VISIBLE);
        }
        String date_file = calcul_cal.get(Calendar.MONTH)+""+ calcul_cal.get(Calendar.DATE) +".txt";
        read_memo(date_file);
    }

    private void read_memo(String name) {


        // ?????? ?????? ?????????
        file = calcul_cal.get(Calendar.MONTH)+""+ calcul_cal.get(Calendar.DATE) +".txt";

        FileInputStream fis = null;
        try {
            fis = openFileInput(file);

            byte[] fileData = new byte[fis.available()];
            fis.read(fileData);
            fis.close();

            String str = new String(fileData);
            edit_memo.setText(str);
        } catch (Exception e) { // UnsupportedEncodingException , FileNotFoundException , IOException
            edit_memo.setText("");
            e.printStackTrace();
        }

    }


    @SuppressLint("WrongConstant")
    private void add_memo(String readDay) {

        FileOutputStream fos = null;

        try {
            fos = openFileOutput(readDay, MODE_NO_LOCALIZED_COLLATORS); //MODE_WORLD_WRITEABLE
            String content = edit_memo.getText().toString();

            // String.getBytes() = ???????????? ??????????????? ???????
            fos.write(content.getBytes());
            //fos.flush();
            fos.close();

            // getApplicationContext() = ?????? ?????????.this ?

        } catch (Exception e) { // Exception - ?????? ?????? ?????? ?????? // FileNotFoundException , IOException
            e.printStackTrace();
        }
    }

}
