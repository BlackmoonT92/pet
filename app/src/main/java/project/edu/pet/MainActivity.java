package project.edu.pet;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    ImageView imCase;
    ImageButton btnOpenClose;
    boolean caseOpening = false;
    public static List<String> schedules = new ArrayList<>();
    ListView listView;
    ArrayAdapter adapter;
    SharedPreferences shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        shared = getSharedPreferences("App_data", MODE_PRIVATE);
        // add values for your ArrayList any where...
        TabHost host = (TabHost) findViewById(R.id.tabHost);
        host.setup();

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Home");
        spec.setContent(R.id.linearLayout);
        spec.setIndicator("Home");
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("Schedule");
        spec.setContent(R.id.linearLayout2);
        spec.setIndicator("Schedule");
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec("Camera");
        spec.setContent(R.id.linearLayout3);
        spec.setIndicator("Camera");
        host.addTab(spec);
        //bilding
        btnOpenClose = (ImageButton) findViewById(R.id.btnOpenClose);
        imCase = (ImageView) findViewById(R.id.imCase);


        //set click events
        btnOpenClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caseOpening = !caseOpening;
                if (caseOpening) {
                    imCase.setImageResource(R.drawable.eating);
                    btnOpenClose.setImageResource(R.drawable.ic_stop);
                } else {
                    imCase.setImageResource(R.drawable.sleeping);
                    btnOpenClose.setImageResource(R.drawable.ic_start);
                }
            }
        });

        //list schedules
        listView = (ListView) findViewById(R.id.listSchedule);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, schedules);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select The Action");
        menu.add(0, v.getId(), 0, "Delete");//groupId, itemId, order, title
        menu.add(0, v.getId(), 0, "Cancel");

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getTitle() == "Delete") {
            Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_LONG).show();
            removeItem(info.position);
        }
        return true;
    }


    public void removeItem(int pos) {
        if (pos > -1) {
            schedules.remove(pos);
            adapter.notifyDataSetChanged();
        }
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            MainActivity.schedules.add(hourOfDay + " : " + minute);
            ((MainActivity) getActivity()).adapter.notifyDataSetChanged();

        }
    }

    public void saveSchedules() {
        SharedPreferences.Editor editor = shared.edit();
        Set<String> set = new HashSet<String>();
        set.addAll(schedules);
        editor.clear();
        editor.putStringSet("SCHEDULE", set);
        editor.apply();
    }


    private void retriveSharedValue() {
        Set<String> set = shared.getStringSet("SCHEDULE", null);
        if (null == schedules) {
            schedules = new ArrayList<>();
        }
        if (null != set) {
            schedules.clear();
            schedules.addAll(set);
            if(null != adapter){
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onResume() {
        retriveSharedValue();
        super.onResume();
    }

    @Override
    protected void onPause() {
        saveSchedules();
        super.onPause();
    }
}
