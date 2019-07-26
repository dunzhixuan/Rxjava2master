package com.zhixuan.app.rxjava2_master;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class interval {
  private static Disposable disposable;

  static Subscription subscription = null;

  public static void main(String[] args) {
    //				interval();
    //				unsubscribe();
    intervalBackPressure();
  }

  private static void interval() {
    //				disposable = Observable.interval(1, TimeUnit.SECONDS,
    // Schedulers.newThread()).subscribe(new Consumer<Long>() {
    //						@Override
    //						public void accept(Long aLong) throws Exception {
    //								System.out.println(aLong);
    //						}
    //				});

    Observable observable = Observable.interval(1, TimeUnit.SECONDS, Schedulers.trampoline());

    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    observable.subscribe(
        new Consumer<Long>() {
          @Override
          public void accept(Long aLong) throws Exception {
            System.out.println(aLong);
          }
        },
        new Consumer<Throwable>() {
          @Override
          public void accept(Throwable throwable) throws Exception {
            System.out.println(throwable.toString());
          }
        });
  }

  private static void unsubscribe() {
    System.out.println("unsubscribe");
    disposable.dispose();
  }

  private static void intervalBackPressure() {

    Flowable<Long> flowable =
        Flowable.interval(1, TimeUnit.MILLISECONDS, Schedulers.trampoline()).take(1000);

    flowable
        .observeOn(Schedulers.newThread())
        .subscribe(
            new Subscriber<Long>() {

              @Override
              public void onSubscribe(Subscription s) {
                subscription = s;
                subscription.request(10);
              }

              @Override
              public void onNext(Long aLong) {
                System.out.println(aLong);
                if (aLong > 10) {
                  System.out.println("aLong1==" + aLong);
                } else {
                  System.out.println("aLong2==" + aLong);
                }
                subscription.request(1);
              }

              @Override
              public void onError(Throwable t) {
                System.out.println(t.toString());
              }

              @Override
              public void onComplete() {
                System.out.println("onComplete");
              }
            });

    try {
      Thread.sleep(100000);
    } catch (InterruptedException e) {
      System.out.println(e.fillInStackTrace());
    }
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

  // case1 : take（127）、noRequest的情况 : 发射的事件放在了缓存里，但是onNext中不接收事件,切缓存大小由16变成了128
  // case2 : take(129)、noRequest的情况 : io.reactivex.exceptions.MissingBackpressureException: Can't
  // deliver value 128 due to lack of requests
  // case3: take（129）、request（100）: 正常接收、request较小的值，MissingBackpressureException
  // case4: take（129）、request（100）、加入sleep: 正常接收
  // case5 : take(225) 、request（100）: MissingBackpressureException
  // request一个小于224的值：先接收完数据，再报出：io.reactivex.exceptions.MissingBackpressureException: Can't deliver
  // value 224 due to lack of requests、注意：项目不会崩溃，但是会调onError
  // case6: take(225)、request（224）: request的值大于224或者用Long.MAX_VALUE：全部接收
  // case7: .take(任意值) 、onSubscribe中subscription.request(50)、subscription.request(1);会处理全部事件
}
