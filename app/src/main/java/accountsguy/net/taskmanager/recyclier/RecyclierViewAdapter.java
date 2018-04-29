package accountsguy.net.taskmanager.recyclier;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import accountsguy.net.taskmanager.R;

/**
 * Created by advic on 07/04/2018.
 */

public class RecyclierViewAdapter extends RecyclerView.Adapter<RecyclierViewAdapter
        .TodolistHolder> {

    private List<Task> taskList;
    Activity activity;
    CompletedTask completedTask;
    IncompletedTask incompletedTask;

    public RecyclierViewAdapter(List<Task> taskList, @Nullable CompletedTask completedTask,
                                @Nullable IncompletedTask incompletedTask){
        this.taskList = taskList;
        activity = completedTask;
        if(completedTask!=null) this.completedTask = completedTask;
        else this.incompletedTask = incompletedTask;
    }

    //When ever a new View created this method will be called.
    @Override
    public TodolistHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .activity_task_view, parent, false);

        return new TodolistHolder(itemview);
    }

    // It will bind the data to view Holder.
    @Override
    public void onBindViewHolder(final TodolistHolder holder, final int position) {
        Task task = taskList.get(position);

        holder.idtextView.setText(String.valueOf(task.columnId));
        holder.title.setText(task.title);
        holder.description.setText(task.description);

        //Todo: Solved ->Converted from Text one format to another format in text date
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        try{
            Date date = new Date();
            date.setTime(task.date.getTimeInMillis());
            String stringdate = simpleDateFormat.format(date);
            stringdate = stringdate.replace(".","-");
            holder.date.setText(stringdate);

            holder.taskheading.setText(stringdate);
        }catch (Exception e){
            Log.i("RecyclierViewAdapter", e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class TodolistHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        protected TextView idtextView, taskheading, title, description, date;
        protected ImageView status;

        public TodolistHolder(View view){
            super(view);

            idtextView = (TextView) view.findViewById(R.id.idvalue);
            taskheading = (TextView) view.findViewById(R.id.taskdateheading);
            title = (TextView) view.findViewById(R.id.tasktitle);
            description = (TextView) view.findViewById(R.id.taskdetails);

            date = (TextView) view.findViewById(R.id.taskdate);
            status = (ImageView) view.findViewById(R.id.taskstatus);

            if(completedTask!=null){
                //Todo: Change drawable to Incomplete
                status.setBackground(completedTask.getResources().getDrawable(R.drawable
                        .complete));
            }

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }
        @Override
        public void onClick(View v) {
            if(incompletedTask != null){
                RecyclierViewAdapter.this.incompletedTask.inflateTodolistEdit(v, Integer.parseInt(idtextView.getText().toString()))                ;
            }

        }

        @Override
        public boolean onLongClick(View v) {
            if(completedTask != null){
                RecyclierViewAdapter.this.completedTask.removeTask(v, idtextView, status);
                return true;
            }
            else if(incompletedTask != null){
                RecyclierViewAdapter.this.incompletedTask.changeTaskStatus(v, idtextView, status);
                return true;
            }else return true;
        }
    }
}