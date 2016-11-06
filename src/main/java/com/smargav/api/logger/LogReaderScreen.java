package com.smargav.api.logger;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.smargav.api.R;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.apache.commons.io.input.TailerListenerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LogReaderScreen extends AppCompatActivity {

    private ListView logList;
    private ArrayAdapter<String> adapter;
    private List<String> linesList = new ArrayList<String>();

    private Thread tailerThread;
    private Tailer tailer;
    private File file;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        TailerListener listener = new MyTailerListener();

        file = new File(getIntent().getAction());
        tailer = Tailer.create(file, listener, 2000, true, false, 200);

        tailerThread = new Thread(tailer);
        tailerThread.setDaemon(true); // optional
        tailerThread.start();

        logList = new ListView(this);
        logList.setFastScrollEnabled(true);
        logList.setDividerHeight(0);

        adapter = new ArrayAdapter<String>(this, R.layout.item_log, linesList);
        readAllLines();
        logList.setAdapter(adapter);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        logList.setLayoutParams(params);
        setContentView(logList);

        Toolbar toolbar = new Toolbar(this);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Log Reader");


    }

    private void readAllLines() {
        try {
            LineIterator iterator = FileUtils.lineIterator(file);
            int lines = 0;
            while (iterator.hasNext()) {
                linesList.add(iterator.nextLine());
            }
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void onPause() {
        super.onPause();
        try {
            tailer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class MyTailerListener extends TailerListenerAdapter {
        public void handle(final String line) {
            linesList.add(line);
            adapter.notifyDataSetChanged();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
