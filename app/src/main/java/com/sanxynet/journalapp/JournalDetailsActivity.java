package com.sanxynet.journalapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class JournalDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    final static int ADD_JOURNAL = 0;
    final static int EDIT_JOURNAL = 1;

    ArrayAdapter<String> adapter;
    List<Category> categoryList;
    List<String> categoryStringList;
    JournalDao journalDao;
    CategoryDao categoryDao;
    Journal caseJournal;
    int reqCode;

    private int yearTemp = 0;
    private int monthTemp = -1;
    private int dateTemp = -1;
    Date today = Calendar.getInstance().getTime();

    EditText journalNameEditText;
    Spinner journalCategorySpinner;
    EditText journalDateEditText;
    EditText journalTimeEditText;
    Switch journalAlarmSwitch;
    EditText journalDescEditText;
    RatingBar journalPriorityRatingBar;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_details);

        if(reqCode == ADD_JOURNAL){
            setTitle(getString(R.string.add_journal));
        }else{
            setTitle(getString(R.string.edit_journal));
        }

        journalNameEditText = findViewById(R.id.journal_name_edit_text);
        journalCategorySpinner = findViewById(R.id.journal_category_spinner);
        journalDateEditText = findViewById(R.id.journal_date_edit_text);
        journalTimeEditText = findViewById(R.id.journal_time_edit_text);
        journalAlarmSwitch = findViewById(R.id.journal_alarm_switch);
        journalDescEditText = findViewById(R.id.journal_desc_edit_text);
        journalPriorityRatingBar = findViewById(R.id.journal_priority_rating_bar);
        submitButton = findViewById(R.id.journal_submit_button);

        categoryList = new ArrayList<>();
        categoryStringList = new ArrayList<>();
        Intent intent = getIntent();
        reqCode = intent.getIntExtra(IntentConstants.REQ_CODE,-1);

        adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,categoryStringList);
        JournalDatabase database = JournalDatabase.getInstance(this);
        categoryDao = database.categoryDao();
        new  AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                categoryList.clear();
                categoryList.addAll(categoryDao.getAllCategories());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                for(int i = 0; i<categoryList.size(); i++){
                    adapter.add(categoryList.get(i).getCategory());
                }
                adapter.notifyDataSetChanged();
            }
        }.execute();

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        journalCategorySpinner.setAdapter(adapter);

        if(reqCode==EDIT_JOURNAL){
            final long id = intent.getLongExtra(IntentConstants.JOURNAL,-1);
            JournalDatabase journalDatabase = JournalDatabase.getInstance(this);
            journalDao = journalDatabase.journalDao();
            new AsyncTask<Void,Void,Void>(){
                @Override
                protected Void doInBackground(Void... params) {
                    caseJournal = journalDao.getTodoById(id);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);

                    journalNameEditText.setText(caseJournal.getJournalName());
                    //got caseJournal category position from categorylist
                    int index = categoryList.indexOf(caseJournal.getJournalCategory());
                    journalCategorySpinner.setSelection(index);

                    DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(caseJournal.getJournalDate());
                    yearTemp = calendar.get(Calendar.YEAR);
                    monthTemp = calendar.get(Calendar.MONTH);
                    dateTemp = calendar.get(Calendar.DATE);
                    String dateString = dateFormat.format(caseJournal.getJournalDate());
                    journalDateEditText.setText(dateString);

                    DateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);
                    String timeString = timeFormat.format(new Date(caseJournal.getJournalTime()));
                    journalTimeEditText.setText(timeString);

                    journalAlarmSwitch.setChecked(caseJournal.isJournalSetAlarm());
                    journalDescEditText.setText(caseJournal.getJournalDesc());
                    journalPriorityRatingBar.setRating(caseJournal.getJournalPriority());

                }
            }.execute();

        }else if(reqCode == ADD_JOURNAL) {
            caseJournal = new Journal();
        }

        journalDateEditText.setOnClickListener(this);
        journalTimeEditText.setOnClickListener(this);
        submitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id==R.id.journal_date_edit_text){
            journalDateEditText.setError(null);
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int date = calendar.get(Calendar.DATE);
            showDatePicker(this,year,month,date);

        }else if(id == R.id.journal_time_edit_text){
            if((yearTemp==0 || monthTemp == -1 || dateTemp==-1) || journalDateEditText.getText().toString().equals("")){
                journalDateEditText.setError(getString(R.string.required_field));
                return;
            }

            Calendar newCalendar = Calendar.getInstance();
            int initialHour = newCalendar.get(Calendar.HOUR_OF_DAY);
            int initialMin = newCalendar.get(Calendar.MINUTE);
            showTimePicker(JournalDetailsActivity.this,initialHour,initialMin);

        }else if(id== R.id.journal_submit_button) {
            String name = journalNameEditText.getText().toString();
            if (name.equals("")) {
                journalNameEditText.setError(getString(R.string.required_field));
                return;
            }

            if(journalDateEditText.getText().toString().equals("")){
                journalDateEditText.requestFocus();
                journalDateEditText.setError(getString(R.string.required_field));
                return;
            }

            if(journalTimeEditText.getText().toString().equals("")){
                setTime(0,0);
            }
            caseJournal.setJournalName(name);

            int categoryPosition = journalCategorySpinner.getSelectedItemPosition();
            if(categoryList.get(categoryPosition).getCategory().equals(DbConstants.CATEGORY_CHOOSE)){
                int index = categoryStringList.indexOf(DbConstants.CATEGORY_OTHERS);
                caseJournal.setJournalCategory(categoryList.get(index));
            }else{
                caseJournal.setJournalCategory(categoryList.get(categoryPosition));
            }
            caseJournal.setJournalDesc(journalDescEditText.getText().toString());
            caseJournal.setJournalSetAlarm(journalAlarmSwitch.isChecked());
            caseJournal.setJournalPriority((int) journalPriorityRatingBar.getRating());

            if(reqCode==ADD_JOURNAL){
                JournalDatabase database = JournalDatabase.getInstance(this);
                journalDao = database.journalDao();
                new AsyncTask<Void, Void, Void>(){

                    @Override
                    protected Void doInBackground(Void... params) {
                        journalDao.insertInDb(caseJournal);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        setResult(RESULT_OK);
                        finish();
                    }
                }.execute();
            }
            else if(reqCode==EDIT_JOURNAL){
                JournalDatabase database = JournalDatabase.getInstance(this);
                journalDao = database.journalDao();

                new AsyncTask<Void,Void,Void>(){
                    @Override
                    protected Void doInBackground(Void... params) {
                        journalDao.updateDb(caseJournal);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        setResult(RESULT_OK);
                        finish();
                    }
                }.execute();
            }
        }
    }

    private void showDatePicker(Context context, int initialYear, int initialMonth,
                                int initialDate) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year,month,dayOfMonth);
                yearTemp = year;
                monthTemp = month;
                dateTemp = dayOfMonth;

                DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
                Date selectedDate = calendar.getTime();
                if(selectedDate.before(today)){
                    Toast.makeText(JournalDetailsActivity.this,"Invalid Date",Toast.LENGTH_SHORT).show();
                    return;
                }
                String dateString = dateFormat.format(selectedDate);
                journalDateEditText.setText(dateString);
            }
        }, initialYear, initialMonth, initialDate);
        datePickerDialog.show();
    }

    private void showTimePicker(Context context, int initialHour, int initialMin) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                setTime(hourOfDay,minute);
            }
        }, initialHour, initialMin, false);

        timePickerDialog.show();
    }

    public void setTime(int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(yearTemp, monthTemp, dateTemp, hourOfDay, minute);
        caseJournal.setJournalDate(calendar.getTime());
        caseJournal.setJournalTime(calendar.getTimeInMillis());
        DateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);
        String timeString = timeFormat.format(new Date(caseJournal.getJournalTime()));
        journalTimeEditText.setText(timeString);
    }
}
