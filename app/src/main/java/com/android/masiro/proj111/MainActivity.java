package com.android.masiro.proj111;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
    ListView listview;
    LinearLayout linear1, linear2;
    DatePicker datepicker;
    EditText et;
    ArrayList<String> memo = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    Button b;
    TextView tv;
    int num_of_memo = 0;
    int pos = 0;
    int MODE_LISTSELECTED = 0;
    Comparator<String> nameAsc = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    };

    public void getprevdata(int position) {
        int prevyear = Integer.parseInt(memo.get(position).substring(0, 2));
        int prevmonth = Integer.parseInt(memo.get(position).substring(3, 5));
        int prevdate = Integer.parseInt(memo.get(position).substring(6, 8));

        try {
            String prevdays = "";

            if (prevmonth < 10) {

                prevdays = prevyear + "-0" + prevmonth + "-" + prevdate + ".memo";
            } else {
                prevdays = prevyear + "-" + prevmonth + "-" + prevdate + ".memo";
            }
            datepicker.init(prevyear + 2000, prevmonth - 1, prevdate, null);
            BufferedReader br = new BufferedReader(new FileReader(getExternalPath() + "/diary/" +
                    prevdays + ".txt"));
            String readstr = "";
            String str = null;
            while ((str = br.readLine()) != null) readstr += str + "\n";

            et.setText(readstr);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void OnButton(View v) {

        String strmemo = et.getText().toString();
        int year = datepicker.getYear() - 2000;
        int month = datepicker.getMonth() + 1;
        int date = datepicker.getDayOfMonth();
        String days = "";

        if (month < 10) {

            days = year + "-0" + month + "-" + date + ".memo";
        } else {
            days = year + "-" + month + "-" + date + ".memo";
        }

        if (v.getId() == R.id.btnsave) {
            //저장
            if (MODE_LISTSELECTED == 1) {
                String list_name = memo.get(pos);
                if (list_name.equals(days)) {
                    //날짜 변경이 없을 때 데이터 변경
                    try {
                        BufferedWriter bw = new BufferedWriter(new FileWriter(getExternalPath() + "/diary/" +
                                days + ".txt", false));
                        bw.write(strmemo);
                        bw.close();
                        Toast.makeText(this, "수정완료", Toast.LENGTH_SHORT).show();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }

                else {
                    //날짜 변경이 있을 때 기존 데이터 삭제 새로 저장
                    File file = new File(getExternalPath() + "/diary/" + memo.get(pos) + ".txt");
                    file.delete();
                    memo.remove(pos);
                    Collections.sort(memo, nameAsc);
                    adapter.notifyDataSetChanged();

                    //새로 메모 추가하기
                    int cnt = 0;
                    for (int i = 0; i < memo.size(); i++) {
                        if (memo.get(i).equals(days)) {
                            cnt++;
                            Toast.makeText(getApplicationContext(), "이미 메모가 존재합니다",
                                    Toast.LENGTH_SHORT).show();
                            int dataposition = memo.indexOf(memo.get(i));
                            getprevdata(dataposition);
                            File file2 = new File(getExternalPath() + "/diary/" + memo.get(i) + ".txt");
                            file2.delete();
                            MODE_LISTSELECTED = 2;
                        }
                    }

                    if (cnt == 0) {
                        try {
                            BufferedWriter bw = new BufferedWriter(new FileWriter(getExternalPath() + "/diary/" +
                                    days + ".txt", false));
                            bw.write(strmemo);
                            bw.close();
                            num_of_memo++;
                            memo.add(days);
                            Collections.sort(memo, nameAsc);
                            adapter.notifyDataSetChanged();
                            tv.setText("등록된 메모 개수: " + Integer.toString(num_of_memo));
                            Toast.makeText(this, "기존 데이터를 삭제하고 새로 저장합니다", Toast.LENGTH_SHORT).show();

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
            else if(MODE_LISTSELECTED == 2){
                //수정하기
                try {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(getExternalPath() + "/diary/" +
                            days + ".txt", false));
                    bw.write(strmemo);
                    bw.close();
                    Toast.makeText(this, "수정완료", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                num_of_memo--;
                tv.setText("등록된 메모 개수: " + Integer.toString(num_of_memo));
            }

            else {

                int cnt = 0;
                for (int i = 0; i < memo.size(); i++) {
                    if (memo.get(i).equals(days)) {
                        cnt++;
                        Toast.makeText(getApplicationContext(), "이미 메모가 존재합니다",
                                Toast.LENGTH_SHORT).show();
                        int dataposition = memo.indexOf(memo.get(i));
                        b.setText("수정");
                        getprevdata(dataposition);
                        MODE_LISTSELECTED = 2;
                    }
                }

                if (cnt == 0) {
                    try {
                        BufferedWriter bw = new BufferedWriter(new FileWriter(getExternalPath() + "/diary/" +
                                days + ".txt", false));
                        bw.write(strmemo);
                        bw.close();
                        num_of_memo++;
                        memo.add(days);
                        Collections.sort(memo, nameAsc);
                        adapter.notifyDataSetChanged();
                        tv.setText("등록된 메모 개수: " + Integer.toString(num_of_memo));
                        Toast.makeText(this, "저장완료", Toast.LENGTH_SHORT).show();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } else if (v.getId() == R.id.btncancel) {
            //취소
            linear1.setVisibility(View.VISIBLE);
            linear2.setVisibility(View.INVISIBLE);

        } else if (v.getId() == R.id.btn1) {
            //등록
            MODE_LISTSELECTED = 0;
            et.setText(null);
            linear1.setVisibility(View.INVISIBLE);
            linear2.setVisibility(View.VISIBLE);
            b.setText("저장");
        }
    }

    public String getExternalPath() {
        String sdPath = "";
        String ext = Environment.getExternalStorageState();
        if (ext.equals(Environment.MEDIA_MOUNTED)) {
            sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            sdPath = getFilesDir() + "";
        }
        return sdPath;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull
            String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SD card 쓰기권한 승인", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "SD card 쓰기권한 거부", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void checkPermission() {
        int permissioninfo = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissioninfo == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "SDcard 쓰기 권한 있음",
                    Toast.LENGTH_SHORT).show();
        } else {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                Toast.makeText(this, "권한 설명", Toast.LENGTH_SHORT).show();
            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listview = (ListView) findViewById(R.id.listview);
        linear1 = (LinearLayout) findViewById(R.id.linear1);
        linear2 = (LinearLayout) findViewById(R.id.linear2);
        datepicker = (DatePicker) findViewById(R.id.datepicker);
        et = (EditText) findViewById(R.id.edittext);
        b = (Button) findViewById(R.id.btnsave);
        tv = (TextView) findViewById(R.id.tvCount);

        adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, memo);
        listview.setAdapter(adapter);
        checkPermission();

        final String path = getExternalPath();
        File dir = new File(path + "/diary");
        dir.mkdir();

        if (dir.isDirectory() == false) {
            String msg = "디렉터리 생성 오류";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }

        File file = new File(path + "/diary/");
        final File[] files = file.listFiles();
        final int nCounter = files.length;
        num_of_memo = nCounter;
        tv.setText("등록된 메모 개수: " + Integer.toString(num_of_memo));

        for (int i = 0; i < nCounter; i++) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(files[i]));
                String readstr = "";
                String str = null;
                while ((str = br.readLine()) != null) readstr += str + "\n";
                int len = path.length() + 7;
                memo.add(files[i].toString().substring(len, files[i].toString().length() - 4));
                Collections.sort(memo, nameAsc);
                adapter.notifyDataSetChanged();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                dlg.setTitle("삭제");
                dlg.setMessage("삭제하시겠습니까?");
                dlg.setPositiveButton("취소", null);
                dlg.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        num_of_memo--;
                        String name = memo.get(pos);
                        File file = new File(path + "/diary/" + name + ".txt");
                        file.delete();
                        memo.remove(pos);
                        Collections.sort(memo, nameAsc);
                        adapter.notifyDataSetChanged();
                        tv.setText("등록된 메모 개수: " + Integer.toString(num_of_memo));
                    }
                });
                dlg.show();
                return true;
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
                b.setText("수정");
                linear1.setVisibility(View.INVISIBLE);
                linear2.setVisibility(View.VISIBLE);
                MODE_LISTSELECTED = 1;
                getprevdata(position);
            }
        });
    }
}
