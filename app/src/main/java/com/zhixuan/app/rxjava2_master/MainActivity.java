package com.zhixuan.app.rxjava2_master;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.reactivestreams.Subscriber;

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // 直接导致空指针
    //    Observable.just(null);
    flatmap();
  }

  private static void flatmap() {
    // 采用RxJava基于事件流的链式操作
    Disposable disposable =
        Observable.create(
                new ObservableOnSubscribe<Integer>() {
                  @Override
                  public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                    emitter.onNext(1);
                    emitter.onNext(2);
                    emitter.onNext(3);
                    emitter.onNext(4);
                    emitter.onNext(5);
                  }

                  // 采用flatMap（）变换操作符
                })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap(
                new Function<Integer, ObservableSource<String>>() {
                  @Override
                  public ObservableSource<String> apply(Integer integer) throws Exception {
                    final List<String> list = new ArrayList<>();
                    for (int i = 0; i < 5; i++) {
                      list.add("我是事件 " + integer + "拆分后的子事件" + i);
                      // 通过flatMap中将被观察者生产的事件序列先进行拆分，再将每个事件转换为一个新的发送三个String事件
                      // 最终合并，再发送给被观察者
                    }
                    return Observable.fromIterable(list);
                  }
                })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                new Consumer<String>() {
                  @Override
                  public void accept(String s) throws Exception {
                    System.out.println(s);
                  }
                });
  }



}
