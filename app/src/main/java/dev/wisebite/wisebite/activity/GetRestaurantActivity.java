package dev.wisebite.wisebite.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.domain.OpenTime;
import dev.wisebite.wisebite.domain.Restaurant;
import dev.wisebite.wisebite.service.RestaurantService;
import dev.wisebite.wisebite.service.ServiceFactory;
import dev.wisebite.wisebite.utils.Utils;

public class GetRestaurantActivity extends AppCompatActivity {

    public static final String RESTAURANT_ID = "RESTAURANT_ID";

    private RestaurantService restaurantService;
    private Restaurant restaurant;
    private String restaurantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_restaurant);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getSerializableExtra(RESTAURANT_ID) != null) {
            this.restaurantId = getIntent().getExtras().getString(RESTAURANT_ID);
        }

        restaurantService = ServiceFactory.getRestaurantService(GetRestaurantActivity.this);
        restaurant = restaurantService.get(restaurantId);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editRestaurant();
            }
        });

        initializeGeneralInfo();
        initializeOpenTimes();
    }

    private void initializeGeneralInfo() {
        setTitle(restaurant.getName());
        TextView description, location, phone, website, numberOfTables;
        description = (TextView) findViewById(R.id.description_refill);
        location = (TextView) findViewById(R.id.location_refill);
        phone = (TextView) findViewById(R.id.phone_refill);
        website = (TextView) findViewById(R.id.website_refill);
        numberOfTables = (TextView) findViewById(R.id.number_of_tables_refill);
        description.setText(restaurant.getDescription());
        location.setText(restaurant.getLocation());
        phone.setText(String.valueOf(restaurant.getPhone()));
        website.setText(restaurant.getWebsite());
        numberOfTables.setText(String.valueOf(restaurant.getNumberOfTables() + " tables"));
    }

    private void initializeOpenTimes() {
        for (OpenTime openTime : restaurantService.getOpenTimesOf(restaurant)) {
            initializeOpenTimeTextView(openTime);
        }
    }

    private void initializeOpenTimeTextView(OpenTime openTime) {
        TextView textView = (TextView) findViewById(getTextView(openTime.getStartDate()));
        textView.setText(Utils.parseStartEndDate(openTime.getStartDate(), openTime.getEndDate()));
    }

    private int getTextView(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                return R.id.monday_open_time;
            case Calendar.TUESDAY:
                return R.id.tuesday_open_time;
            case Calendar.WEDNESDAY:
                return R.id.wednesday_open_time;
            case Calendar.THURSDAY:
                return R.id.thursday_open_time;
            case Calendar.FRIDAY:
                return R.id.friday_open_time;
            case Calendar.SATURDAY:
                return R.id.saturday_open_time;
            case Calendar.SUNDAY:
                return R.id.sunday_open_time;
        }
        return 0;
    }

    private void editRestaurant() {

    }
}
