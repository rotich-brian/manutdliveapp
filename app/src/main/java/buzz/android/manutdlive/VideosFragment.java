package buzz.android.manutdlive;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class VideosFragment extends Fragment implements SelectListener{

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private ShimmerFrameLayout shimmerFrameLayout;
    LinearLayoutManager manager;

    private ArrayList<ModelPost> postArrayList;
    private AdapterPost adapterPost;
    boolean isLoading = false;

    private int rvCheckFill = 0;
    private String url ="";
    private String nextToken ="";
    private  static final  String TAG = "MAIN_TAG";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        recyclerView = view.findViewById(R.id.recyclerView);
        shimmerFrameLayout = view.findViewById(R.id.shimmer);
        shimmerFrameLayout.startShimmer();

        manager = new LinearLayoutManager(getContext());

        //Initiate and clear list before adding data
        postArrayList = new ArrayList<>();
        postArrayList.clear();

        recyclerView.setLayoutManager(manager);

        loadPosts();
        reloadPosts();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int lastItem = manager.findLastCompletelyVisibleItemPosition();

                if (!isLoading){
                    if (lastItem == postArrayList.size()-1) {
                        getMorePosts();
                        isLoading = true;
                    }
                }
                //recyclerView.scrollToPosition(lastItem);
                super.onScrolled(recyclerView, dx, dy);
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        return rootView;
    }

    private void getMorePosts() {

        //postArrayList.add(null);
        //adapterPost.notifyItemInserted(postArrayList.size()-1);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //REMOVE NULL HERE

                postArrayList.remove(postArrayList.size()-1);
                loadMore();
            }
        },1000);
    }

    private void loadPosts() {
        swipeRefresh.setRefreshing(true);
        if (isConnected()) {
            isLoading = true;

            if (nextToken.equals("")){
                Log.d(TAG, "loadPosts: Next Page Token is empty, no more posts");
                url = "https://www.googleapis.com/blogger/v3/blogs/"+Constants.BLOG_ID+
                        "/posts?maxResults="+Constants.MAX_RESULTS+"&key="+Constants.API_KEY+"&labels="+Constants.VIDEOS_LABEL;
            }
            else if (nextToken.equals("end")){
                Log.d(TAG, "loadPosts: Next Page Token is empty/end, no more posts");
                Toast.makeText(getContext(), "You are Caught up, no more posts", Toast.LENGTH_SHORT).show();
                swipeRefresh.setRefreshing(false);
                return;
            }
            else {
                Log.d(TAG, "loadPosts: Next Page Token "+nextToken);
                url = "https://www.googleapis.com/blogger/v3/blogs/"+Constants.BLOG_ID+
                        "/posts?maxResults="+Constants.MAX_RESULTS+
                        "&pageToken="+nextToken+"&key="+Constants.API_KEY+"&labels="+Constants.VIDEOS_LABEL;
            }
            Log.d(TAG, "loadPosts: URL "+url);

            //Request Data @GET

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    rvCheckFill = 1;
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    //we got response, dismiss dialog first

                    //PROGRESS DISMISS WAS HERE

                    Log.d(TAG, "onResponse: " + response);

                    //json data is the response parameter of this function, prone to Error
                    try {
                        //Response is in JSON Object
                        JSONObject jsonObject = new JSONObject(response);
                        try {
                            nextToken = jsonObject.getString("nextPageToken");
                            Log.d(TAG, "onResponse: Next page Token"+nextToken);

                        }catch (Exception e) {
                            // Toast.makeText(MainActivity.this, "Reached end of Page...", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onResponse: Reached end of page..."+ e.getMessage());
                            nextToken = "end";
                        }

                        //get all data from Json
                        JSONArray jsonArray = jsonObject.getJSONArray("items");

                        //continue getting data
                        for (int i=0;i<= jsonArray.length();i++) {
                            try {
                                //get data
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                String id = jsonObject1.getString("id");
                                String title = jsonObject1.getString("title");
                                String content = jsonObject1.getString("content");
                                String published = jsonObject1.getString("published");
                                String updated = jsonObject1.getString("updated");
                                String selfLink = jsonObject1.getString("selfLink");
                                String url = jsonObject1.getString("url");
                                String authorName = jsonObject1.getJSONObject("author").getString("displayName");
                                //String image = jsonObject1.getJSONObject("author").getJSONObject("image").getString("url");

                                //set data
                                ModelPost modelPost = new ModelPost(""+authorName,""
                                        +content,""+id,""+published,""
                                        +selfLink,""+title,""+updated,""+url);

                                //add data to listener
                                //ArrayList<ModelPost> modelPosts = new ArrayList<>();
                                //modelPosts.add(modelPost);
                                postArrayList.add(modelPost);

                            } catch (Exception e) {
                                Log.d(TAG, "onResponse: 1: "+e.getMessage());
                                //Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        //SETUP ADAPTER WAS HERE
                        //setup adapter
                        postArrayList.add(null);

                        adapterPost = new AdapterPost(getContext(), postArrayList, VideosFragment.this);
                        recyclerView.setAdapter(adapterPost);

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isLoading = false;
                                swipeRefresh.setRefreshing(false);
                            }
                        },1000);

                    }
                    catch (Exception e) {
                        Log.d(TAG, "onResponse: 2:"+e.getMessage());
                        // Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: "+error.getMessage());
                    // Toast.makeText(MainActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    swipeRefresh.setRefreshing(false);
                }
            });

            //add request to queue
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(stringRequest);
        }
        else {
            //showInternetSnackBar();
            Toast.makeText(getContext(), "No internet Connection", Toast.LENGTH_SHORT).show();
            swipeRefresh.setRefreshing(false);
        }
    }

    private void reloadPosts() {
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (isConnected()){

                    if (rvCheckFill == 1){
                        recyclerView.setVisibility(View.GONE);
                        shimmerFrameLayout.setVisibility(View.VISIBLE);
                        shimmerFrameLayout.startShimmer();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                nextToken ="";
                                postArrayList.clear();
                                loadPosts();
                                //set adapter to RecyclerView
                                adapterPost.notifyDataSetChanged();
                                //progressDialog.dismiss();
                                swipeRefresh.setRefreshing(false);
                            }
                        }, 1000);
                    }else
                        loadPosts();
                }else
                    showInternetSnackBar();
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    public void loadMore() {
        isLoading = true;

        if (nextToken.equals("")){
            Log.d(TAG, "loadPosts: Next Page Token is empty, no more posts");
            url = "https://www.googleapis.com/blogger/v3/blogs/"+Constants.BLOG_ID+
                    "/posts?maxResults="+Constants.MAX_RESULTS+"&key="+Constants.API_KEY+"&labels="+Constants.VIDEOS_LABEL;
        }
        else if (nextToken.equals("end")){
            Log.d(TAG, "loadPosts: Next Page Token is empty/end, no more posts");
            Toast.makeText(getContext(), "You are Caught up, no more videos", Toast.LENGTH_SHORT).show();
            swipeRefresh.setRefreshing(false);
            return;
        }
        else {
            Log.d(TAG, "loadPosts: Next Page Token "+nextToken);
            url = "https://www.googleapis.com/blogger/v3/blogs/"+Constants.BLOG_ID+
                    "/posts?maxResults="+Constants.MAX_RESULTS+
                    "&pageToken="+nextToken+"&key="+Constants.API_KEY+"&labels="+Constants.VIDEOS_LABEL;
        }
        Log.d(TAG, "loadPosts: URL "+url);

        //Request Data @GET

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //we got response, dismiss dialog first

                //PROGRESS DISMISS WAS HERE

                Log.d(TAG, "onResponse: " + response);

                //json data is the response parameter of this function, prone to Error
                try {
                    //Response is in JSON Object
                    JSONObject jsonObject = new JSONObject(response);
                    try {
                        nextToken = jsonObject.getString("nextPageToken");
                        Log.d(TAG, "onResponse: Next page Token"+nextToken);

                    }catch (Exception e) {
                        // Toast.makeText(MainActivity.this, "Reached end of Page...", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onResponse: Reached end of page..."+ e.getMessage());
                        nextToken = "end";
                    }

                    //get all data from Json
                    JSONArray jsonArray = jsonObject.getJSONArray("items");

                    //continue getting data
                    for (int i=0;i<= jsonArray.length();i++) {
                        try {
                            //get data
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String id = jsonObject1.getString("id");
                            String title = jsonObject1.getString("title");
                            String content = jsonObject1.getString("content");
                            String published = jsonObject1.getString("published");
                            String updated = jsonObject1.getString("updated");
                            String selfLink = jsonObject1.getString("selfLink");
                            String url = jsonObject1.getString("url");
                            String authorName = jsonObject1.getJSONObject("author").getString("displayName");
                            //String image = jsonObject1.getJSONObject("author").getJSONObject("image").getString("url");

                            //set data
                            ModelPost modelPost = new ModelPost(""+authorName,""
                                    +content,""+id,""+published,""
                                    +selfLink,""+title,""+updated,""+url);

                            //add data to listener
                            //ArrayList<ModelPost> modelPosts = new ArrayList<>();
                            //modelPosts.add(modelPost);
                            postArrayList.add(modelPost);

                        } catch (Exception e) {
                            Log.d(TAG, "onResponse: 1: "+e.getMessage());
                            //Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }

                    //SETUP ADAPTER WAS HERE
                    //setup adapter
                    postArrayList.add(null);
                    //adapterPost.notifyItemInserted(postArrayList.size()-1);
                    adapterPost.notifyDataSetChanged();

                    isLoading = false;
                }
                catch (Exception e) {
                    Log.d(TAG, "onResponse: 2:"+e.getMessage());
                    // Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: "+error.getMessage());
                // Toast.makeText(MainActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                swipeRefresh.setRefreshing(false);
            }
        });

        //add request to queue
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void OnPostClicked(ModelPost modelPost) {
        if (isConnected()) {

            Intent intent = new Intent(getActivity(), PostDetailsActivity.class);
            intent.putExtra("data", modelPost);
            startActivity(intent);

        } else {
            Snackbar.make(swipeRefresh, "No internet Connection", Snackbar.LENGTH_SHORT)
                    .setTextMaxLines(1)
                    .setTextColor(R.color.white)
                    .setBackgroundTint(R.color.black)
                    .show();
            swipeRefresh.setRefreshing(false);
        }
    }

    boolean isConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo!=null){
            if (networkInfo.isConnected())
                return  true;
            else
                return false;
        }else
            return false;
    }

    @SuppressLint("ResourceAsColor")
    public  void showInternetSnackBar(){
        Snackbar.make(swipeRefresh, "No internet Connection", Snackbar.LENGTH_INDEFINITE)
                .setTextMaxLines(1)
                .setTextColor(R.color.white)
                .setBackgroundTint(R.color.black)
                .setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onRefresh();
                        //Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                })
                .setActionTextColor(R.color.my_color)
                .show();
    }

    private void onRefresh() {
        if (isConnected()){

            if (rvCheckFill == 1){
                recyclerView.setVisibility(View.GONE);
                shimmerFrameLayout.setVisibility(View.VISIBLE);
                shimmerFrameLayout.startShimmer();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefresh.setRefreshing(true);
                        nextToken ="";
                        postArrayList.clear();
                        loadPosts();
                        //set adapter to RecyclerView
                        adapterPost.notifyDataSetChanged();
                        //progressDialog.dismiss();
                        //swipeRefresh.setRefreshing(false);
                    }
                }, 1000);
                swipeRefresh.setRefreshing(false);
            }else
                loadPosts();
        }else
            showInternetSnackBar();
        swipeRefresh.setRefreshing(false);
    }
}