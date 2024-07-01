package com.example.navigationcomponent.activities;


import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.navigationcomponent.R;
import com.example.navigationcomponent.adapters.CommentAdapter;
import com.example.navigationcomponent.adapters.EpisodesAdapter;
import com.example.navigationcomponent.adapters.ImageSliderAdapter;
import com.example.navigationcomponent.databinding.ActivityTvshowDetailsBinding;
import com.example.navigationcomponent.databinding.LayoutEpisodesBottomSheetBinding;
import com.example.navigationcomponent.databinding.LayoutImageTvshowBottomsheetBinding;
import com.example.navigationcomponent.models.Comment;
import com.example.navigationcomponent.models.TVShow;
import com.example.navigationcomponent.models.UserTVShow;
import com.example.navigationcomponent.ultilities.Constants;
import com.example.navigationcomponent.ultilities.PreferenceManager;
import com.example.navigationcomponent.ultilities.TempDataHolder;
import com.example.navigationcomponent.viewmodels.TVShowDetailsViewModel;
import com.example.navigationcomponent.viewmodels.UserTVShowViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class TVShowDetailsActivity extends AppCompatActivity {

    private ActivityTvshowDetailsBinding activityTvshowDetailsBinding;
    private TVShowDetailsViewModel tvShowDetailsViewModel;
    private BottomSheetDialog episodesBottomSheetDialog;
    private BottomSheetDialog imageTVShowBottomSheetDialog;
    private LayoutEpisodesBottomSheetBinding layoutEpisodesBottomSheetBinding;
    private LayoutImageTvshowBottomsheetBinding layoutImageTVShowBottomSheetBinding;
    private TVShow tvShow;
    private Boolean isTVShowAvailableInWatchList = false;
    private PreferenceManager preferenceManager;
    private int userId;
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();

    private final List<Comment> commentList = new ArrayList<>();
    private UserTVShowViewModel userTVShowViewModel;
    private CommentAdapter commentAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTvshowDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_tvshow_details);
        preferenceManager = new PreferenceManager(getApplicationContext());
        userId = Integer.parseInt(preferenceManager.getString(Constants.KEY_USER_ID));

        setCommentAdapter();

        doInitialization();
    }

    private void setCommentAdapter(){
        commentAdapter = new CommentAdapter(commentList);
        activityTvshowDetailsBinding.rcvComment.setAdapter(commentAdapter);
    }

    private void doInitialization(){
        userTVShowViewModel = new ViewModelProvider(this).get(UserTVShowViewModel.class);
        tvShowDetailsViewModel = new ViewModelProvider(this).get(TVShowDetailsViewModel.class);
        activityTvshowDetailsBinding.imageBack.setOnClickListener(view -> onBackPressed());
        tvShow = (TVShow) getIntent().getSerializableExtra("tvShow");
        checkTVShowInWatchList();
        getTVShowDetails();

    }

    private void testGetUserTVShow(int userId){
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(userTVShowViewModel.getUserTVShow(userId)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userTvShow -> {
                        Log.d("testGetUserTVShow","come");
                        Log.d("testGetUserTVShow",userTvShow.getUserId()+" and "+ userTvShow.getTvShowId());
                    compositeDisposable.dispose();
                }));
    }
    private void checkTVShowInWatchList(){
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(userTVShowViewModel.checkTvShowUser(userId, tvShow.getId())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tvShow -> {
                    isTVShowAvailableInWatchList = true;
                    activityTvshowDetailsBinding.imageWatchlist.setImageResource(R.drawable.icon_done);
                    compositeDisposable.dispose();
                }));
    }

    private void getTVShowDetails(){
        activityTvshowDetailsBinding.setIsLoading(true);
        Log.d("IDIDID",""+getIntent().getIntExtra("id",-1));
        String tvShowId = String.valueOf(tvShow.getId());
        tvShowDetailsViewModel.getTVShowDetails(tvShowId).observe(
                this, tvShowDetailsResponse -> {
                    activityTvshowDetailsBinding.setIsLoading(false);
                    if(tvShowDetailsResponse.getTvShowDetails() != null){
                        if(tvShowDetailsResponse.getTvShowDetails().getPictures() != null){
                            loadImageSlider(tvShowDetailsResponse.getTvShowDetails().getPictures());
                        }
                        activityTvshowDetailsBinding.setTvShowImageURL(
                                tvShowDetailsResponse.getTvShowDetails().getImagePath()
                        );
                        activityTvshowDetailsBinding.imageTVShow.setVisibility(View.VISIBLE);
                        activityTvshowDetailsBinding.setDescription(
                                String.valueOf(
                                        HtmlCompat.fromHtml(
                                                tvShowDetailsResponse.getTvShowDetails().getDescription(),
                                                HtmlCompat.FROM_HTML_MODE_LEGACY
                                        )
                                )
                        );
                        activityTvshowDetailsBinding.textDescription.setVisibility(View.VISIBLE);
                        activityTvshowDetailsBinding.textReadMore.setVisibility(View.VISIBLE);
                        activityTvshowDetailsBinding.textReadMore.setOnClickListener(view -> {
                            if(activityTvshowDetailsBinding.textReadMore.getText().toString().equals("Read More")){
                                activityTvshowDetailsBinding.textDescription.setMaxLines(Integer.MAX_VALUE);
                                activityTvshowDetailsBinding.textDescription.setEllipsize(null);
                                activityTvshowDetailsBinding.textReadMore.setText("Read Less");
                            }else {
                                activityTvshowDetailsBinding.textDescription.setMaxLines(4);
                                activityTvshowDetailsBinding.textDescription.setEllipsize(TextUtils.TruncateAt.END);
                                activityTvshowDetailsBinding.textReadMore.setText("Read More");
                            }
                        });
                        activityTvshowDetailsBinding.setRating(
                                String.format(
                                        Locale.getDefault(),
                                        "%.2f",
                                        Double.parseDouble(tvShowDetailsResponse.getTvShowDetails().getRating())
                                )
                        );
                        if(tvShowDetailsResponse.getTvShowDetails().getGenres() != null){
                            activityTvshowDetailsBinding.setGenre(tvShowDetailsResponse.getTvShowDetails().getGenres()[0]);
                        }else {
                            activityTvshowDetailsBinding.setGenre("N/A");
                        }
                        activityTvshowDetailsBinding.setRuntime(tvShowDetailsResponse.getTvShowDetails().getRuntime() + " Min");
                        activityTvshowDetailsBinding.viewDivider1.setVisibility(View.VISIBLE);
                        activityTvshowDetailsBinding.layoutMisc.setVisibility(View.VISIBLE);
                        activityTvshowDetailsBinding.viewDivider2.setVisibility(View.VISIBLE);
                        activityTvshowDetailsBinding.btnWebsite.setOnClickListener(view -> {
                            Intent intent = new Intent(this, WebViewActivity.class);
                            intent.putExtra("urlTVShow",tvShowDetailsResponse.getTvShowDetails().getUrl());
                            intent.putExtra("nameTVShow",tvShow.getName());
                            startActivity(intent);
                        });

                        String urlImage = tvShowDetailsResponse.getTvShowDetails().getImagePath();
                        activityTvshowDetailsBinding.imageTVShow.setOnClickListener(v -> watchImageTVShow(urlImage));

                        activityTvshowDetailsBinding.btnWebsite.setVisibility(View.VISIBLE);
                        activityTvshowDetailsBinding.btnEpisodes.setVisibility(View.VISIBLE);
                        activityTvshowDetailsBinding.btnEpisodes.setOnClickListener(view -> {
                            if(episodesBottomSheetDialog == null){
                                episodesBottomSheetDialog = new BottomSheetDialog(TVShowDetailsActivity.this);
                                layoutEpisodesBottomSheetBinding = DataBindingUtil.inflate(
                                        LayoutInflater.from(TVShowDetailsActivity.this),
                                        R.layout.layout_episodes_bottom_sheet,
                                        findViewById(R.id.episodesContainer),
                                        false
                                );
                                episodesBottomSheetDialog.setContentView(layoutEpisodesBottomSheetBinding.getRoot());
                                layoutEpisodesBottomSheetBinding.episodesRecyclerView.setAdapter(
                                        new EpisodesAdapter(tvShowDetailsResponse.getTvShowDetails().getEpisodes())
                                );
                                layoutEpisodesBottomSheetBinding.textTile.setText(
                                        String.format("Episodes | %s",tvShow.getName())
                                );
                                layoutEpisodesBottomSheetBinding.imageClose.setOnClickListener(v -> episodesBottomSheetDialog.dismiss());
                            }

                            // Optional section start
                            FrameLayout frameLayout = episodesBottomSheetDialog.findViewById(
                                    com.google.android.material.R.id.design_bottom_sheet
                            );
                            if(frameLayout != null){
                                BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(frameLayout);
                                bottomSheetBehavior.setPeekHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
                                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            }
                            // Optional section end
                            episodesBottomSheetDialog.show();
                        });

//                        activityTvshowDetailsBinding.imageWatchlist.setOnClickListener(view -> {
//                                    CompositeDisposable compositeDisposable = new CompositeDisposable();
//                                    if (isTVShowAvailableInWatchList) {
//                                        isTVShowAvailableInWatchList = false;
//                                        compositeDisposable.add(tvShowDetailsViewModel.removeTVShowFromWatchList(tvShow)
//                                                .subscribeOn(Schedulers.computation())
//                                                .observeOn(AndroidSchedulers.mainThread())
//                                                .subscribe(() -> {
////                                                    deleteFilmFireBase(tvShow);
//                                                    TempDataHolder.IS_WATCHLIST_UPDATED = true;
//                                                    activityTvshowDetailsBinding.imageWatchlist.setImageResource(R.drawable.icon_watchlist);
//                                                    Toast.makeText(getApplicationContext(), "Removed from watchList", Toast.LENGTH_SHORT).show();
//                                                })
//                                        );
//                                    }else {
//                                        isTVShowAvailableInWatchList = true;
//                                        compositeDisposable.add(tvShowDetailsViewModel.addToWatchList(tvShow)
//                                                .subscribeOn(Schedulers.io())
//                                                .observeOn(AndroidSchedulers.mainThread())
//                                                .subscribe(() -> {
//
////                                                    addFilmToFireBase(tvShow);
//                                                    TempDataHolder.IS_WATCHLIST_UPDATED = true;
//                                                    activityTvshowDetailsBinding.imageWatchlist.setImageResource(R.drawable.icon_done);
//                                                    Toast.makeText(getApplicationContext(), "Added to watchList", Toast.LENGTH_SHORT).show();
//                                                })
//                                        );
//                                    }
//                                }
//                        );

                        testUserTVShow();

                        activityTvshowDetailsBinding.imageWatchlist.setVisibility(View.VISIBLE);
                        loadBasicTVShowDetails();

                        activityTvshowDetailsBinding.viewDivider3.setVisibility(View.VISIBLE);

                        activityTvshowDetailsBinding.layoutInputComment.setVisibility(View.VISIBLE);
                        activityTvshowDetailsBinding.rcvComment.setVisibility(View.VISIBLE);
                        Log.d("sizeListComment",commentList.size()+"");
                        activityTvshowDetailsBinding.layoutSend.setOnClickListener(v -> {
                            String content = activityTvshowDetailsBinding.inputComment.getText().toString();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String formattedDate = sdf.format(new Date());
                            Comment comment = new Comment(userId,tvShow.getId(),preferenceManager.getString(Constants.KEY_IMAGE),content,formattedDate);
                            userComment(comment);
                        });

                    }
                }
        );
    }
    private void userComment(Comment comment){
        database.collection(Constants.KEY_COMMENT).document(String.valueOf(tvShow.getId()))
                .collection("userComments")
                .add(comment)
                .addOnSuccessListener(documentReference -> {
                    activityTvshowDetailsBinding.inputComment.setText("");
                    Toast.makeText(TVShowDetailsActivity.this, "Comment Success", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->{
                    Toast.makeText(TVShowDetailsActivity.this, "Comment failed: "+ e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
        getAllComment();
    }

    private void getAllComment() {
        database.collection(Constants.KEY_COMMENT).document(String.valueOf(tvShow.getId()))
                .collection("userComments")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    commentList.clear();
                    for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots.getDocuments()){
                        Comment comment = documentSnapshot.toObject(Comment.class);
                        commentList.add(comment);
                    }
                    commentAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(TVShowDetailsActivity.this, "getAllComment Failed: "+ e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

            getAllComment();

    }

    private void testUserTVShow() {
        UserTVShow userTVShow = new UserTVShow(userId,tvShow.getId());
        Log.d("testUserTVShow",userTVShow.getUserId()+" and "+ userTVShow.getTvShowId());
        activityTvshowDetailsBinding.imageWatchlist.setOnClickListener(view -> {
                    CompositeDisposable compositeDisposable = new CompositeDisposable();
                    if (isTVShowAvailableInWatchList) {
                        isTVShowAvailableInWatchList = false;
                        compositeDisposable.add(userTVShowViewModel.deleteUserTVShow(userTVShow)
                                        .subscribeOn(Schedulers.computation())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(() -> {
//                                                    deleteFilmFireBase(tvShow);
                                            TempDataHolder.IS_WATCHLIST_UPDATED = true;
                                            activityTvshowDetailsBinding.imageWatchlist.setImageResource(R.drawable.icon_watchlist);
                                            Toast.makeText(getApplicationContext(), "Removed from watchList", Toast.LENGTH_SHORT).show();
                                        })
                        );
                    }else {
                        isTVShowAvailableInWatchList = true;

                        compositeDisposable.add(userTVShowViewModel.insertUserTVShow(userTVShow)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(() -> {
                                            testGetUserTVShow(userTVShow.getUserId());
                                            addTVShowToDB();
//                                                    addFilmToFireBase(tvShow);
                                            TempDataHolder.IS_WATCHLIST_UPDATED = true;
                                            activityTvshowDetailsBinding.imageWatchlist.setImageResource(R.drawable.icon_done);
                                            Toast.makeText(getApplicationContext(), "Added to watchList", Toast.LENGTH_SHORT).show();
                                            compositeDisposable.dispose();
                                        })
                        );

                    }
                }
        );

    }

    private void addTVShowToDB(){
        Log.d("Added to DBTVShow","come");
        CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(tvShowDetailsViewModel.addToWatchList(tvShow)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(() -> {
                                Log.d("Added to DBTVShow","success");
                                compositeDisposable.dispose();
                            })
            );
        }

//    private void addFilmToFireBase(TVShow tvShow){
//        HashMap<String, Object> tvShowHash = new HashMap<>();
//        tvShowHash.put("id",tvShow.getId());
//        tvShowHash.put("name",tvShow.getName());
//        tvShowHash.put("country",tvShow.getCountry());
//        tvShowHash.put("start_date",tvShow.getStart_date());
//        tvShowHash.put("status",tvShow.getStatus());
//
//        database.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID))
//                .collection(Constants.COLLECTION_FAVOURITE_FILM)
//                .add(tvShowHash)
//                .addOnSuccessListener(documentReference -> {
//                    Log.d("addFireBase", documentReference.getId());
//                    preferenceManager.putString(Constants.KEY_ID_FAVOURITE_FILM, documentReference.getId());
//                })
//                .addOnFailureListener(e -> Log.d("AddFireBaseFail", e.getMessage()));
//    }

//    private void deleteFilmFireBase(TVShow tvShow){
//        database.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID))
//                .collection(Constants.COLLECTION_FAVOURITE_FILM)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if(task.isSuccessful()) {
//                        for (QueryDocumentSnapshot doc : task.getResult()) {
//                            long id = doc.getLong("id");
//                            Log.d("iDDDDDDDD", String.valueOf(id));
//                            if(id == tvShow.getId()){
//                                database.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID))
//                                        .collection(Constants.COLLECTION_FAVOURITE_FILM)
//                                        .document(doc.getId()).delete();
//                            }
//                        }
//                    }
//                })
//                .addOnFailureListener(e -> Log.d("Fail delete", e.getMessage()));
//    }

    private void loadImageSlider(String[] sliderImage ){
        activityTvshowDetailsBinding.sliderViewPager.setOffscreenPageLimit(1);
        activityTvshowDetailsBinding.sliderViewPager.setAdapter(new ImageSliderAdapter(sliderImage));
        activityTvshowDetailsBinding.sliderViewPager.setVisibility(View.VISIBLE);
        activityTvshowDetailsBinding.viewFadingEdge.setVisibility(View.VISIBLE);
        setUpSliderIndicator(sliderImage.length);
        activityTvshowDetailsBinding.sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentSliderIndicator(position);
            }
        });

    }

    private void setUpSliderIndicator(int count){
        ImageView[] indicator = new ImageView[count];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8,0,8,0);
        for(int i=0; i < indicator.length;i++){
            indicator[i] = new ImageView(getApplicationContext());
            indicator[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.background_slider_indicator_inactive
            ));
            indicator[i].setLayoutParams(layoutParams);
            activityTvshowDetailsBinding.layoutSliderIndicator.addView(indicator[i]);
        }
        activityTvshowDetailsBinding.layoutSliderIndicator.setVisibility(View.VISIBLE);
        setCurrentSliderIndicator(0);
    }

    private void setCurrentSliderIndicator(int position){
        int childCount = activityTvshowDetailsBinding.layoutSliderIndicator.getChildCount();
        for(int i =0; i< childCount; i++){
            ImageView imageView = (ImageView) activityTvshowDetailsBinding.layoutSliderIndicator.getChildAt(i);
            if(i == position){
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(),R.drawable.background_slider_indicator_active)
                );
            }else {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(),R.drawable.background_slider_indicator_inactive)
                );
            }
        }
    }

    private void loadBasicTVShowDetails(){
        activityTvshowDetailsBinding.setTvShowName(tvShow.getName());
        activityTvshowDetailsBinding.setNetworkCountry(
               tvShow.getNetwork() + "(" +
                        tvShow.getCountry() +")"
        );
        activityTvshowDetailsBinding.setStatus(tvShow.getStatus());
        activityTvshowDetailsBinding.setStartedDate(tvShow.getStart_date());
        activityTvshowDetailsBinding.textName.setVisibility(View.VISIBLE);
        activityTvshowDetailsBinding.textNetworkCountry.setVisibility(View.VISIBLE);
        activityTvshowDetailsBinding.textStatus.setVisibility(View.VISIBLE);
        activityTvshowDetailsBinding.textStarted.setVisibility(View.VISIBLE);

    }

    private void watchImageTVShow(String url){
        if(imageTVShowBottomSheetDialog == null){
            imageTVShowBottomSheetDialog = new BottomSheetDialog(TVShowDetailsActivity.this);
            layoutImageTVShowBottomSheetBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(TVShowDetailsActivity.this),
                    R.layout.layout_image_tvshow_bottomsheet,
                    findViewById(R.id.imageTVShowContainer),
                    false
            );
            imageTVShowBottomSheetDialog.setContentView(layoutImageTVShowBottomSheetBinding.getRoot());
            layoutImageTVShowBottomSheetBinding.NameTvShow.setText(tvShow.getName());
            layoutImageTVShowBottomSheetBinding.setTvShowImageURL(url);
            layoutImageTVShowBottomSheetBinding.imageBack.setOnClickListener(view -> imageTVShowBottomSheetDialog.dismiss());
        }
        // Optional section start
        FrameLayout frameLayout = imageTVShowBottomSheetDialog.findViewById(
                com.google.android.material.R.id.design_bottom_sheet
        );
        if(frameLayout != null){
            BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(frameLayout);
            bottomSheetBehavior.setPeekHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
        // Optional section end
        imageTVShowBottomSheetDialog.show();
    }



}