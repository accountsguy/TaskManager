package accountsguy.net.taskmanager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import accountsguy.net.taskmanager.database.SQLHelper;

@SuppressLint("ValidFragment")
public class ActivityTodolist extends DialogFragment implements View.OnClickListener{
    EditText title, description; DatePicker datePicker;
    Button saveButton, cancelButton;

    View dialogView;
    Snackbar snackbar;

    public ActivityTodolist(SQLHelper sqlHelper){
        MainActivity.sqlHelper = sqlHelper;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dialogView = inflater.inflate(R.layout.layout_todolist, container, false);

        title = (EditText) dialogView.findViewById(R.id.title);
        description = (EditText) dialogView.findViewById(R.id.description);


        datePicker = (DatePicker) dialogView.findViewById(R.id.datepicker);
        datePicker.setMinDate(Calendar.getInstance().getTimeInMillis());
        Calendar calendar = Calendar.getInstance();
        //Todo: Completed - 5 Years added to the current year.
        calendar.set(Calendar.getInstance().getWeekYear()+5,12, 0);
        datePicker.setMaxDate(calendar.getTimeInMillis());

        saveButton = dialogView.findViewById(R.id.save_button);
        cancelButton = dialogView.findViewById(R.id.cancel_button);
        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        return dialogView;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.save_button:
                if(title.getText().length() > 5 && description.getText().length() > 5 ){
                    String title = this.title.getText().toString();
                    String description = this.description.getText().toString();

                    //Todo: Date Formatter.
                    GregorianCalendar calendar = new GregorianCalendar();
                    calendar.set(Calendar.YEAR, datePicker.getYear());
                    calendar.set(Calendar.MONTH, datePicker.getMonth());
                    calendar.set(Calendar.DATE, datePicker.getDayOfMonth());

                    DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale
                            .GERMAN);
                    String dateString  = dateFormat.format(new Date(calendar.getTimeInMillis()));

//                    String dateString = String.valueOf(calendar.getWeekYear())
//                            +"-"+String.valueOf(calendar.get(Calendar.MONTH))
//                            +"-"+String.valueOf(calendar.get(Calendar.DATE));
                    try{
                         if(MainActivity.sqlHelper.recordTodolist(title, description, dateString,
                                 false) != 0) {
                            snackbar = Snackbar.make(dialogView.findViewById(R.id.todolist_container) , "Task " +
                                            "Recorded Successfully",
                                    Snackbar
                                            .LENGTH_SHORT);
                            Log.i("Test - ", "Recorded Successfully");
                            snackbar.show();

//                            CompletedTask.updateTaskReport(null);
                        } else {
                            snackbar = Snackbar.make(dialogView.findViewById(R.id.todolist_container)
                                    , "No Records Stored",
                                    Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                    }
                    catch (Exception e) {
                        snackbar = Snackbar.make(dialogView.findViewById(R.id.todolist_container) ,
                                "Exception: "+e.getMessage(),
                                Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                    this.title.setText("");
                    this.description.setText("");
                    Date tempdate = new Date();
                    this.datePicker.updateDate(tempdate.getYear(), tempdate.getMonth(), tempdate.getDay());
                }
                else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.setIcon(R.drawable.add_new_task);
                    builder.setTitle("Wrong Input!")
                            .setItems(new String[]{"Title should be atlease 6 characters",
                            "Description should be atleast 6 characters"},
                            new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                break;
            case R.id.cancel_button:
                dismiss();;
                break;
        }
    }
}
