package com.example.owner.rxjava;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.internal.operators.observable.ObservableFromCallable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {
    TextView txt;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt = findViewById(R.id.txt);

        try {
            SingleRxjava();
        }catch (Exception e){
            Log.v("***","error : " + e);

        }
//        baseRxjava();
    }

    private void SingleRxjava() {
        baseset();
        Map<String, String> parameters = new HashMap<>();
        parameters.put("userId", "1");
        parameters.put("_sort", "id");
        parameters.put("_order", "desc");

        disposables.add(jsonPlaceHolderApi.getComment(5)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new DisposableSingleObserver<List<Comment>>() {
                @Override
                public void onSuccess(List<Comment> comments) {
                    for (Comment comment : comments){
                        String content = "";
                        content += "ID: " + comment.getId() + "\n" ;
                        content += "Post ID: " + comment.getPostId() + "\n";
                        content += "name: " + comment.getName() + "\n";
                        content += "email: " + comment.getEmail() + "\n";
                        content += "Text: " + comment.getText() + "\n\n";
                        txt.append(content);
                    }
                }

                @Override
                public void onError(Throwable e) {

                }
            }));

//        disposables.add(jsonPlaceHolderApi.getPosts(parameters)
//                .subscribeOn(Schedulers.io())                           //?????????????????????thread??????
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(new DisposableSingleObserver<List<Post>>() {
//                    @Override
//                    public void onSuccess(List<Post> posts) {
//                        for (Post post : posts) {
//                            String content = "";
//                            content += "ID: " + post.getId() + "\n";
//                            content += "User ID: " + post.getUserId() + "\n";
//                            content += "Title: " + post.getTitle() + "\n";
//                            content += "Text: " + post.getText() + "\n\n";
//                            txt.append(content);
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.v("***","error : " + e);
//                    }
//                }));
    }


    private void baseRxjava() {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                //???????????????
            }
        })
                .subscribeOn(Schedulers.io())                           //?????????????????????thread??????
                .observeOn(AndroidSchedulers.mainThread())               //?????????????????????
                .subscribe(new DisposableCompletableObserver() {
                    //????????????????????????
                    @Override
                    public void onComplete() {
                        txt.setText("11111");
                    }

                    //????????????????????????
                    @Override
                    public void onError(Throwable e) {
                        txt.setText("2222");
                    }
                });
    }

//    private  void  getpost(){
//        baseset();
//        Map<String,String> parameters = new HashMap<>();
//        parameters.put("userId","1");
//        parameters.put("_sort","id");
//        parameters.put("_order","desc");
//
//        //???????????????Call ????????????call???jsonPlaceHolderApi??????getPost()??????
//        Call<List<Post>> call = jsonPlaceHolderApi.getPosts(parameters);
//
//        call.enqueue(new Callback<List<Post>>() {
//            @Override
//            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
//                // ????????????
//                // ????????????????????????Post?????????????????????get????????????????????????
//                if(!response.isSuccessful()){
//                    txt.setText("Code : " + response.code());
//                }
//
//                List<Post> posts = response.body();
//
//                for (Post post : posts){
//                    String content = "";
//                    content += "ID: " + post.getId() + "\n" ;
//                    content += "User ID: " + post.getUserId() + "\n";
//                    content += "Title: " + post.getTitle() + "\n";
//                    content += "Text: " + post.getText() + "\n\n";
//                    txt.append(content);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Post>> call, Throwable t) {
//                //????????????
//
//            }
//        });
//    }

    private void baseset() {
        Gson gson = new GsonBuilder().serializeNulls().create();        //gson??????????????????????????????null??? ??????????????????null???????????????serializeNulls()??????

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request originalRequest = chain.request();

                        Request request = originalRequest.newBuilder()
                                .header("Interceptor_Header", "xyz")
                                .build();
                        return chain.proceed(request);
                    }
                })
                .addInterceptor(loggingInterceptor)
                .build();

        // ??????baseUrl?????????????????????addConverterFactory???Gson??????????????????Converter
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")              //API??????????????????????????????????????????HttpUrl
                .addConverterFactory(GsonConverterFactory.create(gson))             //??????????????????????????????????????????
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        disposables.clear();
    }

}

