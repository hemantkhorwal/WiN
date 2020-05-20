package com.pubg.mobile.update.leaks;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    String endNode,lastNode="",firstNode;
    boolean endOfData=false , isLoading = false;
    private final int maxLoadCount = 13;
    ImageView about_us;
    int lastAdPut=0,newDataLoadCount=0;
    RecyclerView recyclerView;
  //  AutoCompleteTextView search;
    ProgressBar progressBar;
    private AdLoader adLoader;
    Adapter adapter;
    //SwipeRefreshLayout refreshLayout;
    FirebaseDatabase mDatabase;
    DatabaseReference posts ,hint;
    List<Object> postMetas = new ArrayList<>();
    List<Object> newData = new ArrayList<>();
    //ArrayList<String> hints = new ArrayList<>();
    private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();
    public static final int NUMBER_OF_ADS = 5;

    private boolean isAdLoaded = false;
    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mDatabase = FirebaseDatabase.getInstance();
        posts = mDatabase.getReference().child("posts");
        hint = mDatabase.getReference().child("hint");
        findView();
        loadNativeAds();
        getEndNode();
        //getHints();
        startLoading();

        adapter = new Adapter(postMetas,this);
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        DividerItemDecoration itemDecorator = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.divider)));

        recyclerView.addItemDecoration(itemDecorator);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager manager = LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
                assert manager != null;
                int item = manager.getItemCount();
                int lastVisible = manager.findLastVisibleItemPosition();

                if (!isLoading &&item <= (lastVisible+1)){
                    isLoading = true;
                    startLoading();
                }

            }
        });

        /*ArrayAdapter<String> hintAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,hints);
        search.setAdapter(hintAdapter);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchQuery(parent.getItemAtPosition(position).toString(),"EQUAL");
            }
        });

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    String q = search.getText().toString();
                    searchQuery(q.toUpperCase(),"EQUAL");
                    return true;
                }
                return false;
            }
        });
*/

       /* refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                endOfData=false;
                lastNode="";
                postMetas.clear();
                startLoading();
            }
        });*/

        about_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,AboutUs.class);
                startActivity(i);
            }
        });
    }

    public void findView(){
        //searchImg = findViewById(R.id.home_search);
        recyclerView = findViewById(R.id.recycle_view);
        //search = findViewById(R.id.search_bar);
        about_us = findViewById(R.id.about_us);
        //refreshLayout = findViewById(R.id.refreshLayout);
        progressBar = findViewById(R.id.progress_bar);
    }


    public interface OnGetDataListener {
        void onSuccess(DataSnapshot dataSnapshot);
        void onStart();
        void onFailure();
    }
    public void receiveData(Query ref, final OnGetDataListener listener) {
        listener.onStart();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }

    public void startLoading (){
        if(!endOfData) {
            Query query;
            if (lastNode.isEmpty()) {
                changeProgressBarState(true);
                query = posts.orderByKey().limitToLast(maxLoadCount-2);

            }
            /*else if(refreshLayout.isRefreshing()) {
               query = posts.orderByKey().limitToLast(maxLoadCount-2);
            }*/
            else{
                changeProgressBarState(true);
                query = posts.orderByKey().endAt(lastNode).limitToLast(maxLoadCount-2);
            }


            receiveData(query, new OnGetDataListener() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChildren()){
                        ArrayList<Object> newData = new ArrayList<>();
                        newDataLoadCount = (int) dataSnapshot.getChildrenCount();
                        Log.d("DevLog","NewDataLoadCount"+newDataLoadCount);

                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        String id = child.getKey().toString();
                        String headLine = child.child("HEADLINE").getValue().toString();
                        String headImg = child.child("HEADIMG").getValue().toString();
                        String metaTitle = child.child("METATITLE").getValue().toString();
                        String metaDesc = child.child("METADESC").getValue().toString();
                        String typeImg = child.child("TYPEIMG").getValue().toString();
                        String postURL = child.child("POSTURL").getValue().toString();
                        String type = child.child("TYPE").getValue().toString();
                        /*String headLine = child.getKey().toString();
                        String headImg = "https://1.bp.blogspot.com/-B8azQ4yejhY/XZry5kWSXJI/AAAAAAAAAh0/YpV4GIJVgsM0ogESuruuC_UzQFRIW9rgQCLcBGAsYHQ/s1600/test_img.jpg";
                        String typeImg = "https://lh3.googleusercontent.com/-ggoZFlOjaLI/XSlM1H93e1I/AAAAAAAAACw/DtjKw1AYG0k-PDwJO7N23Yd9A9qt-DqzwCEwYBhgL/w140-h140-p/icon.png";
                        String metaTitle = "POST Title";
                        String metaDesc = "Post Desc";
                        String postURL = "https://firebasestorage.googleapis.com/v0/b/updates-for-pubg-mobile.appspot.com/o/1000000.html";
                        String type = "POST";*/
                        PostMeta p = new PostMeta(headLine, headImg, typeImg, metaTitle, metaDesc, postURL, id,type);
                        newData.add(p);
                        Log.d("LoadLog","Child Added"+headLine);
                    }

                    PostMeta p  = (PostMeta) newData.get(0);
                    lastNode = p.getId();


                    if (!lastNode.equals(endNode)) {
                         newData.remove(0);
                    }else {

                        lastNode = "end";
                        endOfData = true;
                    }
                    Collections.reverse(newData);
                    changeProgressBarState(true);
                    loadNativeAds();
                    adapter.addNewData(newData);
                    isLoading= false;
                    //refreshLayout.setRefreshing(false);
                    changeProgressBarState(false);
                 }else {
                        changeProgressBarState(false);
                        //refreshLayout.setRefreshing(false);
                        isLoading = false;
                        endOfData = true;
                    }
                }

                @Override
                public void onStart() {

                }

                @Override
                public void onFailure() {

                }
            });
        }
    }

    private void getEndNode(){
        Query q = posts
                .orderByKey()
                .limitToFirst(1);
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot lastNode : dataSnapshot.getChildren()){
                    endNode = lastNode.getKey();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void changeProgressBarState(boolean makeVisible){

        if(makeVisible){
            progressBar.setVisibility(View.VISIBLE);
        }else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void loadNativeAds() {
        AdLoader.Builder builder = new AdLoader.Builder(this, getString(R.string.Test_Native));
        adLoader = builder.forUnifiedNativeAd(
                new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        // A native ad loaded successfully, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
                        mNativeAds.add(unifiedNativeAd);
                       if(!adLoader.isLoading()) {
                            insertAdsInPostItems();
                        }

                    }
                }).withAdListener(
                new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // A native ad failed to load, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
                        Log.e("MainActivity", "The previous native ad failed to load. Attempting to"
                                + " load another.");
                      if (!adLoader.isLoading()) {
                            insertAdsInPostItems();
                        }
                    }
                }).build();

        // Load the Native ads.
        adLoader.loadAds(new AdRequest.Builder().build(), NUMBER_OF_ADS);

    }

    public void insertAdsInPostItems(){
        if (mNativeAds.size() <= 0) {
            return;
        }

        if(postMetas.size()<5){
         postMetas.add(mNativeAds.get(0));
        }else {
            postMetas.add(postMetas.size()-1,mNativeAds.get(new Random().nextInt(5)));
            Log.d("DevLog","Ad at"+(postMetas.size()-1));
            if(newDataLoadCount>4){
                int putAdAt=(postMetas.size()-1)-(newDataLoadCount/2);
                postMetas.add(putAdAt,mNativeAds.get(new Random().nextInt(5)));
                Log.d("DevLog","Ad at"+putAdAt);
            }
        }

      /*  for (Object o : postMetas){
            if(o instanceof PostMeta){
                Log.d("CheckLog",((PostMeta) o).getId());
            }else {
                Log.d("CheckLog","Ad => "+((UnifiedNativeAd) o).getAdvertiser());
            }
        }*/

        adapter.refresh();
    }

  /*  public void getHints() {
        changeProgressBarState(true);
        hint.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot query : dataSnapshot.getChildren()){
                    hints.add(query.getValue().toString());
                }
                hint=null;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Unable to fetch hints",Toast.LENGTH_LONG).show();
            }
        });
    }*/

    private void searchQuery(final String query, final String QTYPE){
        Log.d("DevLog","Searching for "+query);
        Query fireQuery = null;
        switch(QTYPE){
            case "EQUAL":
                fireQuery= posts.orderByChild("HEADLINE").equalTo(query).limitToFirst(1);
                break;
            case "START":
                Log.d("DevLog","Qtype = "+QTYPE);
                fireQuery= posts.orderByChild("HEADLINE").startAt(query).limitToFirst(1);
                break;
            case "END":
                Log.d("DevLog","Qtype = "+QTYPE);
                fireQuery= posts.orderByChild("HEADLINE").endAt(query).limitToFirst(1);
        }

        fireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("Search",dataSnapshot.toString());
                if(dataSnapshot.getValue()==null){
                    if(QTYPE.equals("EQUAL")){
                        Log.d("DevLog","Swithing QType from "+QTYPE+" to Start");
                        searchQuery(query,"START");
                    }else if(QTYPE.equals("START")){
                        Log.d("DevLog","Swithing QType from "+QTYPE+" to END");
                        searchQuery(query,"END");
                    }else {
                        Log.d("DevLog","Nor Result");
                        Toast.makeText(getApplicationContext(),"No Result Found",Toast.LENGTH_LONG).show();
                    }
                }else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        mBundleRecyclerViewState = new Bundle();
        Parcelable listState = recyclerView.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, listState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mBundleRecyclerViewState != null) {
            Parcelable listState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            recyclerView.getLayoutManager().onRestoreInstanceState(listState);
        }
    }
}


//add ads in newData and then adapter.add
