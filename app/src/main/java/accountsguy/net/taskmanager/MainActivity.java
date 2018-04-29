package accountsguy.net.taskmanager;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import accountsguy.net.taskmanager.database.DatabaseScheme;
import accountsguy.net.taskmanager.database.SQLHelper;
import accountsguy.net.taskmanager.recyclier.CompletedTask;
import accountsguy.net.taskmanager.recyclier.IncompletedTask;
import accountsguy.net.taskmanager.recyclier.RecyclierViewAdapter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static SQLHelper sqlHelper;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    private FloatingActionButton fab;
    private TextView completedView, incompletedView;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        completedView = (TextView) findViewById(R.id.completetasks);
        incompletedView = (TextView) findViewById(R.id.incompletetasks);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        completedView.setOnClickListener(this);
        incompletedView.setOnClickListener(this);
        fab.setOnClickListener(this);

        // SQLiteOpenHelper Implementation.
        sqlHelper = new SQLHelper(this, DatabaseScheme.DATABASE_NAME, null, DatabaseScheme.DATABASE_VERSION);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menus, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.completetasks:
                Intent completedIntent = new Intent(this, CompletedTask.class);
                startActivity(completedIntent);
                break;
            case R.id.incompletetasks:
                Intent incompletedIntent = new Intent(this, IncompletedTask.class);
                startActivity(incompletedIntent);
                break;
            case R.id.fab:
                ActivityTodolist activityTodolist = new ActivityTodolist(sqlHelper);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                        .beginTransaction();
                activityTodolist.show(fragmentTransaction, "Todolist_New_Dialog");
                break;
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        completedView.setText("Completed Tasks : "+String.valueOf(sqlHelper.fetchRecords(true)));

        incompletedView.setText("Incompleted Tasks : "+String.valueOf(sqlHelper.fetchRecords
                (false)));
    }
}
