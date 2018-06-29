package com.sanxynet.journalapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public final static int EDIT_JOURNAL = 1;
    public final static int ADD_JOURNAL = 0;
    public static Date todayDate;
    public static Date tomDate;

    JournalDao journalDao;
    CategoryDao categoryDao;

    RecyclerView mTodayRecyclerView;
    RecyclerView mUpComingRecyclerView;
    RecyclerView mDoneRecyclerView;

    List<Journal> journalList;
    ArrayList<Journal> todayJournalArrayList;
    ArrayList<Journal> upcomingJournalArrayList;
    ArrayList<Journal> doneJournalArrayList;

    RecyclerAdapter mTodayRecyclerAdapter;
    RecyclerAdapter mUpcomingRecyclerAdapter;
    RecyclerAdapter mDoneRecyclerAdapter;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        SharedPreferences appData = getSharedPreferences(getString(R.string.journal_shared_pref), MODE_PRIVATE);
        boolean isFirstTime = appData.getBoolean(getString(R.string.is_first_time), true);

        if (isFirstTime) {
            appData.edit().putBoolean(getString(R.string.is_first_time), false).apply();

            JournalDatabase database = JournalDatabase.getInstance(this);
            categoryDao = database.categoryDao();
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    categoryDao.newCat(new Category(DbConstants.CATEGORY_CHOOSE));
                    categoryDao.newCat(new Category(DbConstants.CATEGORY_PERSONAL));
                    categoryDao.newCat(new Category(DbConstants.CATEGORY_WORK));
                    categoryDao.newCat(new Category(DbConstants.CATEGORY_TECH));
                    categoryDao.newCat(new Category(DbConstants.CATEGORY_FINANCE));
                    categoryDao.newCat(new Category(DbConstants.CATEGORY_OTHERS));
                    // custom category work
                    return null;
                }
            }.execute();
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, JournalDetailsActivity.class);
                intent.putExtra(IntentConstants.REQ_CODE, ADD_JOURNAL);
                startActivityForResult(intent, ADD_JOURNAL);
            }
        });

        mTodayRecyclerView = findViewById(R.id.today_recycler_view);
        mUpComingRecyclerView = findViewById(R.id.upcoming_recycler_view);
        mDoneRecyclerView = findViewById(R.id.done_recycler_view);

        journalList = new ArrayList<>();
        todayJournalArrayList = new ArrayList<>();
        upcomingJournalArrayList = new ArrayList<>();
        doneJournalArrayList = new ArrayList<>();

        mTodayRecyclerAdapter = new RecyclerAdapter(this, todayJournalArrayList, new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                editTodayTodo(position);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                removeTodayTodo(position);

            }
        });
        mUpcomingRecyclerAdapter = new RecyclerAdapter(this, upcomingJournalArrayList, new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                editUpcomingTodo(position);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                removeUpcomingTodo(position);
            }
        });
        mDoneRecyclerAdapter = new RecyclerAdapter(this, doneJournalArrayList, new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                editDoneTodo(position);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                removeDoneTodo(position);
            }
        });

        mTodayRecyclerView.setAdapter(mTodayRecyclerAdapter);
        mUpComingRecyclerView.setAdapter(mUpcomingRecyclerAdapter);
        mDoneRecyclerView.setAdapter(mDoneRecyclerAdapter);

        mTodayRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mUpComingRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mDoneRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        mTodayRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mUpComingRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mDoneRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mTodayRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mUpComingRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mDoneRecyclerView.setItemAnimator(new DefaultItemAnimator());

        ItemTouchHelper todayItemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP, ItemTouchHelper.DOWN | ItemTouchHelper.UP) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        if (direction == ItemTouchHelper.UP) {
                            editTodayTodo(viewHolder.getAdapterPosition());
                        } else if (direction == ItemTouchHelper.DOWN) {
                            removeTodayTodo(viewHolder.getAdapterPosition());
                        }
                    }
                });

        ItemTouchHelper upcomingItemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP, ItemTouchHelper.DOWN | ItemTouchHelper.UP) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        if (direction == ItemTouchHelper.UP) {
                            editUpcomingTodo(viewHolder.getAdapterPosition());
                        } else if (direction == ItemTouchHelper.DOWN) {
                            removeUpcomingTodo(viewHolder.getAdapterPosition());
                        }
                    }
                });

        ItemTouchHelper doneItemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP, ItemTouchHelper.DOWN | ItemTouchHelper.UP) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        if (direction == ItemTouchHelper.UP) {
                            editDoneTodo(viewHolder.getAdapterPosition());
                        } else if (direction == ItemTouchHelper.DOWN) {
                            removeDoneTodo(viewHolder.getAdapterPosition());
                        }
                    }
                });

        todayItemTouchHelper.attachToRecyclerView(mTodayRecyclerView);
        upcomingItemTouchHelper.attachToRecyclerView(mUpComingRecyclerView);
        doneItemTouchHelper.attachToRecyclerView(mDoneRecyclerView);

        updateLists();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_logout:
                signOut();
                return true;

            case R.id.action_settings:
                Intent profileActivity = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(profileActivity);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            return;
        } else {
            updateLists();
        }
    }

    private void updateLists() {
        setTodayTomDate();
        journalList.clear();
        todayJournalArrayList.clear();
        mTodayRecyclerAdapter.notifyDataSetChanged();
        upcomingJournalArrayList.clear();
        mUpcomingRecyclerAdapter.notifyDataSetChanged();
        doneJournalArrayList.clear();
        mDoneRecyclerAdapter.notifyDataSetChanged();

        JournalDatabase database = JournalDatabase.getInstance(this);
        journalDao = database.journalDao();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                journalList = journalDao.getAllTodo();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                for (int i = 0; i < journalList.size(); i++) {
                    Journal currentJournal = journalList.get(i);
                    Date currentTodoDate = currentJournal.getJournalDate();

                    if (currentTodoDate.before(todayDate)) {
                        cancelAlarm(currentJournal);
                        int size = upcomingJournalArrayList.size();
                        doneJournalArrayList.add(currentJournal);
                        mDoneRecyclerAdapter.notifyItemInserted(size);

                    } else if (currentTodoDate.after(tomDate)) {
                        if (currentJournal.isJournalSetAlarm()) {
                            setAlarm(currentJournal);
                        } else {
                            cancelAlarm(currentJournal);
                        }
                        int size = upcomingJournalArrayList.size();
                        upcomingJournalArrayList.add(currentJournal);
                        mUpcomingRecyclerAdapter.notifyItemInserted(size);

                    } else {
                        if (currentJournal.isJournalSetAlarm()) {
                            setAlarm(currentJournal);
                        } else {
                            cancelAlarm(currentJournal);
                        }
                        int size = todayJournalArrayList.size();
                        todayJournalArrayList.add(currentJournal);
                        mTodayRecyclerAdapter.notifyItemInserted(size);
                    }
                }
            }
        }.execute();
    }

    private void cancelAlarm(Journal journal) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        intent.putExtra(IntentConstants.JOURNAL, journal.getJournalId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) journal.getJournalId(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);
    }

    private void setAlarm(Journal journal) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        intent.putExtra(IntentConstants.JOURNAL, journal.getJournalId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) journal.getJournalId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, journal.getJournalTime(), pendingIntent);
    }

    private void setTodayTomDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int date = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);

        calendar.set(year, month, date, hour, min, 0);
        todayDate = calendar.getTime();
        calendar.set(year, month, date + 1, 0, 0, 0);
        tomDate = calendar.getTime();
    }

    private void removeDoneTodo(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.remove_journal));
        builder.setCancelable(false);
        builder.setMessage(getString(R.string.are_you_sure));

        builder.setPositiveButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDoneRecyclerAdapter.notifyItemChanged(position);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                JournalDatabase database = JournalDatabase.getInstance(MainActivity.this);
                journalDao = database.journalDao();
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        Journal journal = doneJournalArrayList.get(position);
                        journalDao.deleteFromDb(journal);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        doneJournalArrayList.remove(position);
                        mDoneRecyclerAdapter.notifyItemRemoved(position);
                    }
                }.execute();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void removeUpcomingTodo(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.remove_journal));
        builder.setCancelable(false);
        builder.setMessage(getString(R.string.are_you_sure));

        builder.setPositiveButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mUpcomingRecyclerAdapter.notifyItemChanged(position);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                JournalDatabase database = JournalDatabase.getInstance(MainActivity.this);
                journalDao = database.journalDao();
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        Journal journal = upcomingJournalArrayList.get(position);
                        journalDao.deleteFromDb(journal);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        upcomingJournalArrayList.remove(position);
                        mUpcomingRecyclerAdapter.notifyItemRemoved(position);
                    }
                }.execute();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void removeTodayTodo(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.remove_journal));
        builder.setCancelable(false);
        builder.setMessage(getString(R.string.are_you_sure));

        builder.setPositiveButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mTodayRecyclerAdapter.notifyItemChanged(position);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                JournalDatabase database = JournalDatabase.getInstance(MainActivity.this);
                journalDao = database.journalDao();
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        Journal journal = todayJournalArrayList.get(position);
                        journalDao.deleteFromDb(journal);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        todayJournalArrayList.remove(position);
                        mTodayRecyclerAdapter.notifyItemRemoved(position);
                    }
                }.execute();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void editTodayTodo(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final Journal journal = todayJournalArrayList.get(position);
        builder.setTitle(journal.getJournalName());
        builder.setCancelable(false);

        View view = getLayoutInflater().inflate(R.layout.dailog_view, null);
        TextView dialogCat = view.findViewById(R.id.dialog_category);
        TextView dialogDesc = view.findViewById(R.id.dialog_desc);
        TextView dialogDate = view.findViewById(R.id.dialog_date);
        TextView dialogTime = view.findViewById(R.id.dialog_time);
        AppCompatRatingBar dialogPriority = view.findViewById(R.id.dialog_priority);
        SwitchCompat dialogSwitch = view.findViewById(R.id.dialog_alarm);

        dialogCat.setText("    " + journal.getJournalCategory().getCategory());
        dialogDesc.setText(journal.getJournalDesc());

        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        String dateString = dateFormat.format(journal.getJournalDate());
        dialogDate.setText(dateString);

        DateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);
        String timeString = timeFormat.format(new Date(journal.getJournalTime()));
        dialogTime.setText(timeString);

        dialogPriority.setRating(journal.getJournalPriority());
        dialogSwitch.setChecked(journal.isJournalSetAlarm());

        builder.setView(view);

        builder.setPositiveButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mTodayRecyclerAdapter.notifyItemChanged(position);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(getString(R.string.edit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this, JournalDetailsActivity.class);
                intent.putExtra(IntentConstants.REQ_CODE, EDIT_JOURNAL);
                intent.putExtra(IntentConstants.JOURNAL, journal.getJournalId());
                startActivityForResult(intent, EDIT_JOURNAL);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }

    private void editUpcomingTodo(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final Journal journal = upcomingJournalArrayList.get(position);
        builder.setTitle(journal.getJournalName());
        builder.setCancelable(false);

        View view = getLayoutInflater().inflate(R.layout.dailog_view, null);
        TextView dialogCat = view.findViewById(R.id.dialog_category);
        TextView dialogDesc = view.findViewById(R.id.dialog_desc);
        TextView dialogDate = view.findViewById(R.id.dialog_date);
        TextView dialogTime = view.findViewById(R.id.dialog_time);
        AppCompatRatingBar dialogPriority = view.findViewById(R.id.dialog_priority);
        SwitchCompat dialogSwitch = view.findViewById(R.id.dialog_alarm);

        dialogCat.setText("    " + journal.getJournalCategory().getCategory());
        dialogDesc.setText(journal.getJournalDesc());

        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        String dateString = dateFormat.format(journal.getJournalDate());
        dialogDate.setText(dateString);

        DateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);
        String timeString = timeFormat.format(new Date(journal.getJournalTime()));
        dialogTime.setText(timeString);

        dialogPriority.setRating(journal.getJournalPriority());
        dialogSwitch.setChecked(journal.isJournalSetAlarm());

        builder.setView(view);

        builder.setPositiveButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mUpcomingRecyclerAdapter.notifyItemChanged(position);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(getString(R.string.edit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this, JournalDetailsActivity.class);
                intent.putExtra(IntentConstants.REQ_CODE, EDIT_JOURNAL);
                intent.putExtra(IntentConstants.JOURNAL, journal.getJournalId());
                startActivityForResult(intent, EDIT_JOURNAL);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }

    private void editDoneTodo(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final Journal journal = doneJournalArrayList.get(position);
        builder.setTitle(journal.getJournalName());
        builder.setCancelable(false);

        View view = getLayoutInflater().inflate(R.layout.dailog_view, null);
        TextView dialogCat = view.findViewById(R.id.dialog_category);
        TextView dialogDesc = view.findViewById(R.id.dialog_desc);
        TextView dialogDate = view.findViewById(R.id.dialog_date);
        TextView dialogTime = view.findViewById(R.id.dialog_time);
        AppCompatRatingBar dialogPriority = view.findViewById(R.id.dialog_priority);
        SwitchCompat dialogSwitch = view.findViewById(R.id.dialog_alarm);

        dialogCat.setText("    " + journal.getJournalCategory().getCategory());
        dialogDesc.setText(journal.getJournalDesc());

        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        String dateString = dateFormat.format(journal.getJournalDate());
        dialogDate.setText(dateString);

        DateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);
        String timeString = timeFormat.format(new Date(journal.getJournalTime()));
        dialogTime.setText(timeString);

        dialogPriority.setRating(journal.getJournalPriority());
        dialogSwitch.setChecked(journal.isJournalSetAlarm());

        builder.setView(view);

        builder.setPositiveButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDoneRecyclerAdapter.notifyItemChanged(position);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(getString(R.string.edit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this, JournalDetailsActivity.class);
                intent.putExtra(IntentConstants.REQ_CODE, EDIT_JOURNAL);
                intent.putExtra(IntentConstants.JOURNAL, journal.getJournalId());
                startActivityForResult(intent, EDIT_JOURNAL);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    //sign out method
    public void signOut() {
        auth.signOut();
    }
}