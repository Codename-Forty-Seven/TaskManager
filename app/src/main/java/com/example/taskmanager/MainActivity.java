package com.example.taskmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;

import com.example.taskmanager.adapters.BoxWithTasks;
import com.example.taskmanager.adapters.MainAdapter;
import com.example.taskmanager.dataBase.AppExecuted;
import com.example.taskmanager.dataBase.MyDbManager;
import com.example.taskmanager.dataBase.OnDataReceived;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnDataReceived {

    private MyDbManager myDbManager;
    private Button btnCreateNewTask;
    private RecyclerView rvWithTasks;
    private MainAdapter mainAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initAllComponents();

        btnCreateNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent actToCreateNewTask = new Intent(MainActivity.this, WriteNewTaskActivity.class);
                startActivity(actToCreateNewTask);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        myDbManager.openDb();
        readFromDb("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myDbManager.closeDb();
    }

    private void initAllComponents() {
        mainAdapter = new MainAdapter(this);
        myDbManager = new MyDbManager(this);
        rvWithTasks = findViewById(R.id.rvWithTasks);
        rvWithTasks.setLayoutManager(new LinearLayoutManager(this));
        getItemSwipe().attachToRecyclerView(rvWithTasks);
        rvWithTasks.setAdapter(mainAdapter);
        btnCreateNewTask = findViewById(R.id.btnCreateNewTask);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu_to_search, menu);
        MenuItem item = menu.findItem(R.id.search_task);
        SearchView search = (SearchView) item.getActionView();
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String s) {
                readFromDb(s);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void readFromDb(final String mainText) {
        AppExecuted.getInstance().getSecondIO().execute(new Runnable() {
            @Override
            public void run() {
                myDbManager.readFromDb(mainText, MainActivity.this);
            }
        });

    }

    private ItemTouchHelper getItemSwipe() {
        return new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                mainAdapter.removeAndUpList(viewHolder.getAdapterPosition(), myDbManager);
            }
        });
    }

    @Override
    public void onReceived(List<BoxWithTasks> boxWithTasks) {
        AppExecuted.getInstance().getMainIO().execute(new Runnable() {
            @Override
            public void run() {
                mainAdapter.updateAdapter(boxWithTasks);
            }
        });
    }
}