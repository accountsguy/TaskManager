package accountsguy.net.taskmanager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import accountsguy.net.taskmanager.database.DatabaseScheme;

@SuppressLint("ValidFragment")
public class ActivityTodolistEdit extends DialogFragment implements View.OnClickListener {
    EditText title, description; TextView id; DatePicker datePicker;
    Button editButton, cancelButton;

    View dialogView, itemViews;
    Snackbar snackbar; int columnId;

    public ActivityTodolistEdit(View v, int columnID){
        itemViews = v;
        this.columnId = columnID;
    }

    @SuppressLint("NewApi")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dialogView = inflater.inflate(R.layout.layout_todolist, container, false);

        id = (TextView) dialogView.findViewById(R.id.idvalue);
        title = (EditText) dialogView.findViewById(R.id.title);
        description = (EditText) dialogView.findViewById(R.id.description);

        datePicker = (DatePicker) dialogView.findViewById(R.id.datepicker);
        //Todo: Completed - 5 Years added to the current year.
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.getInstance().getWeekYear()+5,12, 0);
        datePicker.setMaxDate(calendar.getTimeInMillis());

        //Todo: Completed - Settin default Date.
        TextView dateView = (TextView) itemViews.findViewById(R.id.taskdate);
        String dateString = dateView.getText().toString();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        String splitDate[] = dateString.split("-");
        try {

            GregorianCalendar calender = new GregorianCalendar();
            calendar.set(Calendar.YEAR,Integer.parseInt(splitDate[2]));
            calendar.set(Calendar.MONTH,Integer.parseInt(splitDate[1])-1);
            calendar.set(Calendar.DATE,Integer.parseInt(splitDate[0]));
            datePicker.setMinDate(calendar.getTimeInMillis());
            datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DATE));
        } catch (Exception e) {
            e.printStackTrace();
        }

        title.setText(((TextView)itemViews.findViewById(R.id.tasktitle)).getText().toString());
        description.setText(((TextView)itemViews.findViewById(R.id.taskdetails)).getText().toString());

        editButton = dialogView.findViewById(R.id.save_button);
        editButton.setText("Edit");
        cancelButton = dialogView.findViewById(R.id.cancel_button);
        editButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        ((TextView)dialogView.findViewById(R.id.heading)).setText("Update the Exisitng Task");
        return dialogView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_button:
                if (title.getText().length() > 5 && description.getText().length() > 5) {
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


                    try {
                        ContentValues updatableColumnvalues = new ContentValues();
                        updatableColumnvalues.put(DatabaseScheme.ToDoList.CN_TITLE, this.title.getText().toString());
                        updatableColumnvalues.put(DatabaseScheme.ToDoList.CN_DESCRIPTION, this.description
                                .getText().toString());
                        updatableColumnvalues.put(DatabaseScheme.ToDoList.CN_DATE, dateString);

                        // Filter results WHERE "title" = 'My Title'
                        String whereClause = DatabaseScheme.ToDoList._ID + "=?";
                        String[] whereArgs = new String[]{String.valueOf(columnId)};

                        int effectedRows = MainActivity.sqlHelper.updateTodoList
                                (updatableColumnvalues,whereClause, whereArgs);

                        if (effectedRows != 0) {
                            snackbar = Snackbar.make(dialogView.findViewById(R.id.todolist_container), "Task " +
                                            "Recorded Updated",
                                    Snackbar
                                            .LENGTH_SHORT);
                            snackbar.show();
                        } else {
                            snackbar = Snackbar.make(dialogView.findViewById(R.id.todolist_container)
                                    , "No Records Stored",
                                    Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                    } catch (Exception e) {
                        snackbar = Snackbar.make(dialogView.findViewById(R.id.todolist_container),
                                "Exception: " + e.getMessage(),
                                Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                    dismiss();
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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
                dismiss();
                break;
        }

    }
}