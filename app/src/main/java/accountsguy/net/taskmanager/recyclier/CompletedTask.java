package accountsguy.net.taskmanager.recyclier;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import accountsguy.net.taskmanager.ActivityTodolist;
import accountsguy.net.taskmanager.ActivityTodolistEdit;
import accountsguy.net.taskmanager.MainActivity;
import accountsguy.net.taskmanager.R;
import accountsguy.net.taskmanager.database.DatabaseScheme;

/**
 * Created by advic on 07/04/2018.
 */

public class CompletedTask extends AppCompatActivity{

    private RecyclerView recyclerView;
    private Task task;
    private boolean TaskState = true;
    private RecyclierViewAdapter recyclierViewAdapter;
    private Snackbar snackbar;
    List<Task> taskList;
    public Cursor cursor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setBackgroundColor(getResources().getColor(R.color.toolbar));
        setSupportActionBar(toolbar);
    }
    private List<Task> createList(Cursor cursor){
        int tasksSize = cursor.getCount();
        List<Task> resultList = new ArrayList<>();
        if(tasksSize > 0) {
            do {
                Task task = new Task();

                task.columnId = cursor.getInt(cursor.getColumnIndex("_ID"));
                task.title = cursor.getString(cursor.getColumnIndex(DatabaseScheme.ToDoList.CN_TITLE));
                task.description = cursor.getString(cursor.getColumnIndex(DatabaseScheme.ToDoList.CN_DESCRIPTION));

                //Todo: Solved -> Getting date from SQLiteDatabase and storing in Local DateObject;
                String stringdate = cursor.getString(cursor.getColumnIndex(DatabaseScheme.ToDoList.CN_DATE));

                try{
                    stringdate = stringdate.replace(".","-");
                    String dateParts[] = stringdate.split("-");
                    task.date = new GregorianCalendar();
                    task.date.set(Calendar.YEAR, Integer.parseInt(dateParts[2]));
                    task.date.set(Calendar.MONTH, Integer.parseInt(dateParts[1]));
                    task.date.set(Calendar.DATE, Integer.parseInt(dateParts[0]));
                } catch (Exception e){
                    Log.i("CompletedTask.class - ", e.getMessage() + "at Date Formatting ");
                }

                if (cursor.getInt(cursor.getColumnIndex(DatabaseScheme.ToDoList.CN_STATUS)) == 1){
                    task.status = true;
                } else {
                    task.status = false;
                }

                resultList.add(task);
            } while (cursor.moveToNext() == true);
            cursor.close();
        }
        else {
            if(cursor != null){
                cursor.close();
            }
            Toast.makeText(this, "No Records found", Toast.LENGTH_SHORT).show();
        }
        return resultList;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menus, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void inflateTodolistEdit(View v, int columnId){
        ActivityTodolistEdit activityTodolist = new ActivityTodolistEdit(v, columnId);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        activityTodolist.show(fragmentTransaction, "Todolist_New_Dialog");
    }

    public void removeTask(View v, TextView idtextView, ImageView statusImage){
        try {
            // Filter results WHERE "title" = 'My Title'
            String whereClause = DatabaseScheme.ToDoList._ID + "=?";
            String[] whereArgs = new String[]{String.valueOf(idtextView.getText().toString())};

            int effectedRows = MainActivity.sqlHelper.deleteRecord(whereClause, whereArgs);

            if (effectedRows != 0) {
                onWindowFocusChanged(false);
                Toast.makeText(this, "Task Removed from the List",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Task Note Removed from the list",Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        onPause();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        cursor = MainActivity.sqlHelper.fetchTodolist(TaskState);
        if (cursor != null) {
            taskList = createList(cursor);
            recyclierViewAdapter = new RecyclierViewAdapter(taskList, this, null);
        } else {
            Toast.makeText(this, "No Records found", Toast.LENGTH_LONG).show();
        }

        recyclerView = (RecyclerView) findViewById(R.id.recyclierview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclierViewAdapter);
        recyclierViewAdapter = new RecyclierViewAdapter(taskList, this, null);
        recyclerView.setAdapter(recyclierViewAdapter);
        recyclierViewAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.removeItem(R.id.completed_menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getTitle().toString()){
            case "Add":
                ActivityTodolist activityTodolist = new ActivityTodolist(MainActivity.sqlHelper);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                        .beginTransaction();
                activityTodolist.show(fragmentTransaction, "Todolist_New_Dialog");
                break;
            case "IncompletedTask":
                Intent incompletedIntent = new Intent(getApplicationContext(), IncompletedTask.class);
                incompletedIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(incompletedIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //ToDO: This will clear all stack history and move to Main Activity
        Intent goToMainActivity = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(goToMainActivity);
    }

    @Override
    protected void onStop() {
        super.onStop();
        recyclerView = null;
        task = null;
        recyclierViewAdapter = null;
        snackbar = null;
        cursor = null;
        taskList.clear();
    }
}