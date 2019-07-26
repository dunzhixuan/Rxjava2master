package com.zhixuan.app.rxjava2_master;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.widget.ImageView;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.view.ViewScrollChangeEvent;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.LongConsumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

  private ImageView img;
  private static final String TAG = "MainActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    img = findViewById(R.id.img);

    intervalBackPressure2();
    //    IO();
    /* 防抖点击监听*/
    RxView.clicks(img)
        .throttleFirst(2, TimeUnit.SECONDS)
        .subscribe(
            new Consumer<Object>() {
              @Override
              public void accept(Object o) throws Exception {
                //                Toast.makeText(MainActivity.this, "11111111111",
                // Toast.LENGTH_LONG).show();
                Log.e(TAG, "111111111111");
              }
            });

    /* 长按监听*/
    RxView.longClicks(img)
        .subscribe(
            new Consumer<Object>() {
              @Override
              public void accept(Object o) throws Exception {
                //                Toast.makeText(MainActivity.this, "2222222222",
                // Toast.LENGTH_LONG).show();
                Log.e(TAG, "222222222222");
              }
            });
    //
    //    /* 绘制监听*/
    RxView.draws(img)
        .subscribe(
            new Consumer<Object>() {
              @Override
              public void accept(Object o) throws Exception {
                //                Toast.makeText(MainActivity.this, "绘制" + o.toString(),
                // Toast.LENGTH_LONG).show();
                //                  Log.e(TAG,"绘制");
              }
            });
    //
    /* 拖拽监听 ???? */
    RxView.drags(img)
        .subscribe(
            new Consumer<DragEvent>() {
              @Override
              public void accept(DragEvent dragEvent) throws Exception {
                Toast.makeText(MainActivity.this, "被拖拽了" + dragEvent.toString(), Toast.LENGTH_LONG)
                    .show();
                Log.e(TAG, "被拖拽了" + dragEvent.toString());
              }
            });

    // 滑动时触发
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      RxView.scrollChangeEvents(img)
          .subscribe(
              new Consumer<ViewScrollChangeEvent>() {
                @Override
                public void accept(ViewScrollChangeEvent viewScrollChangeEvent) throws Exception {
                  Log.e(TAG, "被滑动了" + viewScrollChangeEvent.toString());
                }
              });
    }

    // 直接导致空指针
    //    Observable.just(null);
    //    flatmap();

    //    Observable.just(1)
    //        .map(
    //            new Function<Integer, String>() {
    //              @Override
    //              public String apply(Integer integer) throws Exception {
    //                return null;
    //              }
    //            })
    //        .subscribe();
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

  private void concat() {
    Observable.create(
            new ObservableOnSubscribe<Integer>() {
              @Override
              public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; i < 10; i++) {
                  System.out.println("1111" + i);
                }
              }
            })
        .subscribeOn(Schedulers.io())
        .concatWith(
            Observable.create(
                new ObservableOnSubscribe<Integer>() {
                  @Override
                  public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                    for (int i = 0; i < 10; i++) {
                      System.out.println("2222" + i);
                    }
                  }
                }))
        .subscribe(
            new Consumer<Integer>() {
              @Override
              public void accept(Integer integer) throws Exception {}
            });
  }

  private void IO() {
    // TODO 切换线程
    Observable.interval(1, TimeUnit.SECONDS)
        .take(5)
        .subscribeOn(Schedulers.io())
        .subscribe(
            new Consumer<Long>() {
              @Override
              public void accept(Long aLong) throws Exception {
                System.out.println("TBG 当前线程：" + Thread.currentThread().getName());
              }
            });
  }

  static Subscription subscription = null;

  private void intervalBackPressure() {
    //      Observable.interval(1,TimeUnit.MICROSECONDS);
    //    Flowable<Long> flowable =
    Flowable.interval(1, TimeUnit.MICROSECONDS)
        .doOnRequest(
            new LongConsumer() {
              @Override
              public void accept(long t) throws Exception {
                Log.e("TBG", "doOnRequest");
              }
            })
        .subscribeOn(Schedulers.io())
        .doOnSubscribe(
            new Consumer<Subscription>() {
              @Override
              public void accept(Subscription subscription) throws Exception {
                Log.e("TBG", "doOnSubscribe");
              }
            })
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(
            new Consumer<Long>() {
              @Override
              public void accept(Long aLong) throws Exception {
                Log.e("TBG", "doOnNext");
              }
            })
        .doOnTerminate(
            new Action() {
              @Override
              public void run() throws Exception {
                Log.e("TBG", "doOnTerminate");
              }
            })
        .doOnEach(
            new Consumer<Notification<Long>>() {
              @Override
              public void accept(Notification<Long> longNotification) throws Exception {
                Log.e("TBG", "doOnEach");
              }
            })
        .doOnComplete(
            new Action() {
              @Override
              public void run() throws Exception {
                Log.e("TBG", "doOnComplete");
              }
            })
        .subscribe(
            new Subscriber<Long>() {
              @Override
              public void onSubscribe(Subscription s) {
                subscription = s;
                subscription.request(1);
              }

              @Override
              public void onNext(Long l) {
                Log.e("TBG", l + "");
                try {
                  Thread.sleep(1000);
                } catch (InterruptedException e) {
                  e.printStackTrace();
                }
                subscription.request(1);
              }

              @Override
              public void onError(Throwable t) {
                Log.e("TBG", "onError:" + t.toString());
              }

              @Override
              public void onComplete() {
                Log.e("TBG", "onComplete");
              }
            });

    //      Observable<Long> observable2 = Observable.interval(1, TimeUnit.MILLISECONDS);
    //
    //      observable2
    //              .subscribeOn(Schedulers.io())
    //              .observeOn(AndroidSchedulers.mainThread())
    //              .subscribe(new Observer<Long>() {
    //                  @Override
    //                  public void onSubscribe(Disposable d) {
    //                  }
    //
    //                  @Override
    //                  public void onNext(Long aLong) {
    //                  }
    //
    //                  @Override
    //                  public void onError(Throwable e) {
    //
    //                  }
    //
    //                  @Override
    //                  public void onComplete() {
    //
    //                  }
    //              });
  }

  private static void intervalBackPressure2() {

    Flowable<Long> flowable =
        Flowable.interval(1, TimeUnit.MILLISECONDS, Schedulers.trampoline()).take(225);

    flowable
        .observeOn(Schedulers.newThread())
        .subscribe(
            new Subscriber<Long>() {

              @Override
              public void onSubscribe(Subscription s) {
                subscription = s;
                subscription.request(100);
              }

              @Override
              public void onNext(Long aLong) {
                System.out.println(aLong);
                  Log.e("TBG", "" + aLong);
              }

              @Override
              public void onError(Throwable t) {
                Log.e("TBG", "" + t.toString());
              }

              @Override
              public void onComplete() {
                Log.e("TBG", "onComplete");
              }
            });

  }
}
