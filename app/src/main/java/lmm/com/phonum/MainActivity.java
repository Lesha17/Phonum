package lmm.com.phonum;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import lmm.com.phonum.utils.CallListUtils;


public class MainActivity extends ActionBarActivity
        implements MenuItemCompat.OnActionExpandListener,
        NumberListFragment.OnFragmentInteractionListener,
        NumberInfoFrgment.OnFragmentInteractionListener{

    private Toolbar toolbar;

    private Drawer.Result drawerResult = null;

    private MenuItem searchMenuItem;
    private View searchActionView;
    private EditText searchEditText;
    private ImageButton searchButton;
    private Switch assigned_switch;

    private NumberListFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Инициализация Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        assigned_switch = (Switch)toolbar.findViewById(R.id.assigned_switch);

        assigned_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //TODO: Обработать
                Toast.makeText(MainActivity.this, "switch: " + isChecked, Toast.LENGTH_SHORT).show();
                Log.d("Switch", "check");
            }
        });


        //Инициализация Drawer
        drawerResult = new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.current).withIcon(FontAwesome.Icon.faw_phone),
                        new PrimaryDrawerItem().withName(R.string.deferred).withIcon(FontAwesome.Icon.faw_clock_o),
                        new PrimaryDrawerItem().withName(R.string.done).withIcon(FontAwesome.Icon.faw_check),
                        new PrimaryDrawerItem().withName(R.string.all).withIcon(FontAwesome.Icon.faw_mobile),

                        new DividerDrawerItem(),

                        //TODO: Добавить категории
                        new SecondaryDrawerItem().withName(R.string.add_category).withIcon(FontAwesome.Icon.faw_plus),

                        new DividerDrawerItem(),

                        new PrimaryDrawerItem().withName(R.string.deleted).withIcon(FontAwesome.Icon.faw_trash_o),

                        new DividerDrawerItem(),

                        new SecondaryDrawerItem().withName(R.string.settings).withIcon(FontAwesome.Icon.faw_gear),
                        new SecondaryDrawerItem().withName(R.string.help).withIcon(FontAwesome.Icon.faw_question),
                        new SecondaryDrawerItem().withName(R.string.about).withIcon(FontAwesome.Icon.faw_info)
                ).withOnDrawerListener(new Drawer.OnDrawerListener() {
            @Override
            public void onDrawerOpened(View view) {
                InputMethodManager inputMethodManager = (InputMethodManager) MainActivity.this.getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), 0);
            }

            @Override
            public void onDrawerClosed(View view) {
            }
        }).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {

            }
        }).withOnDrawerItemLongClickListener(new Drawer.OnDrawerItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {
                return false;
            }
        }).build();
        fragment = NumberListFragment.newInstance("All");

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if(drawerResult.isDrawerOpen()){
            drawerResult.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        searchMenuItem = menu.findItem(R.id.action_search);
        searchActionView = searchMenuItem.getActionView();
        searchEditText = (EditText)searchActionView.findViewById(R.id.search_edit_text);
        searchButton = (ImageButton)searchActionView.findViewById(R.id.search_button);

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                int inputType = searchEditText.getInputType();
                if(inputType == InputType.TYPE_CLASS_PHONE){
                    searchEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    searchEditText.setInputType(InputType.TYPE_CLASS_PHONE);
                }
                return true;
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fragment.showSearchResults(searchEditText.getText() + "");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.showSearchResults(searchEditText.getText() + "");
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchMenuItem, this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_search:
                //EditText seaEditText = (EditText)item.getActionView().findViewById(R.id.search_edit_frame);
                //InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                //inputMethodManager.showSoftInput(seaEditText, 0);
                Log.d("fuck", "off");
                break;
            default:
                break;
        }

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        if(item.getItemId() == R.id.action_search){
            if(searchActionView != null) {
                YoYo.with(Techniques.FadeInRight).duration(350).playOn(searchActionView);
                if(searchEditText.requestFocus()) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                    fragment.showSearchResults(searchEditText.getText() + "");
                }
            } else {
                Log.d("Fucking", "null");
            }
        }
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        if(searchActionView != null){
            YoYo.with(Techniques.FadeOutRight).duration(350).playOn(searchActionView);
        }
        InputMethodManager inputMethodManager = (InputMethodManager)MainActivity.this.getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), 0);

        fragment.reset_search();

        return true;
    }

    @Override
    public void showNumber(CallListUtils.Number number, int position) {
        Fragment numberInfo = NumberInfoFrgment.newInstance(number);
        YoYo.with(Techniques.SlideInRight).duration(250).playOn(findViewById(R.id.container));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, numberInfo)
                .commit();
    }

    @Override
    public void provideBackNavigation() {
        drawerResult.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        drawerResult.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
        drawerResult.getActionBarDrawerToggle().setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment)
                        .commit();
            }
        });
    }

    @Override
    public void returnToolbarHomeState() {
        YoYo.with(Techniques.SlideInLeft).duration(250).playOn(findViewById(R.id.container));
        drawerResult.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        drawerResult.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
    }
}
