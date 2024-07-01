package com.example.navigationcomponent.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.navigationcomponent.Fragment.FragmentFavorite;
import com.example.navigationcomponent.Fragment.FragmentHome;
import com.example.navigationcomponent.Fragment.FragmentUser;
import com.example.navigationcomponent.R;
import com.example.navigationcomponent.adapters.ViewPagerAdapter;
import com.example.navigationcomponent.database.TVShowDatabase;
import com.example.navigationcomponent.databinding.ActivityMainBinding;
import com.example.navigationcomponent.ultilities.PreferenceManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;

    private FragmentHome fragmentHome;
    private FragmentFavorite fragmentFavorite;
    private FragmentUser fragmentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        deleteDb();

        setUpFragment();

        setupViewPager();
        setupBottomNavigation();

//     componentNavigation();

        //test pass data
        testData();
    }

    private void deleteDb(){
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(TVShowDatabase.getTvShowDatabase(this).userTVShowDao().deleteAllUserTVShow()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> Log.d("deleteAllUserTVShow:","ok")));

        compositeDisposable.add(TVShowDatabase.getTvShowDatabase(this).userDao().deleteAllUser()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> Log.d("deleteAllUserTVShow:","ok")));

        compositeDisposable.add(TVShowDatabase.getTvShowDatabase(this).tvShowDao().deleteAllTVShow()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> Log.d("deleteAllUserTVShow:","ok")));
    }

    private void componentNavigation(){
        BottomNavigationView navView = binding.navView;

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        NavigationUI.setupWithNavController(navView, navController);
    }

    private void setUpFragment(){
        fragmentHome = new FragmentHome();
        fragmentFavorite = new FragmentFavorite();
        fragmentUser = new FragmentUser();
    }

    private void setupViewPager() {
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(fragmentHome);
        fragmentList.add(fragmentFavorite);
        fragmentList.add(fragmentUser);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this, fragmentList);
        binding.viewPagerMain.setAdapter(adapter);

        binding.viewPagerMain.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                       binding.navView.setSelectedItemId(R.id.itHome);
                        break;
                    case 1:
                        binding.navView.setSelectedItemId(R.id.itFavorite);
                        break;
                    case 2:
                        binding.navView.setSelectedItemId(R.id.itUser);
                        break;
                }
            }
        });
    }
    private void setupBottomNavigation() {
        binding.navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.itHome){
                    binding.viewPagerMain.setCurrentItem(0);
                    return true;
                } if(item.getItemId() == R.id.itFavorite){
                    binding.viewPagerMain.setCurrentItem(1);
                    return true;
                } if(item.getItemId() == R.id.itUser){
                    binding.viewPagerMain.setCurrentItem(2);
                    return true;
                }
                return false;
            }
        });
    }

    private void testData(){
        preferenceManager = new PreferenceManager(this);
        preferenceManager.putString("testSendData","successed");
    }
}