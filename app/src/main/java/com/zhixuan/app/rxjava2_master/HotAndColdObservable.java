package com.zhixuan.app.rxjava2_master;

import android.support.annotation.NonNull;

import org.reactivestreams.Subscriber;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.schedulers.Schedulers;

public class HotAndColdObservable {

  public static void main(String[] args) {
    //    connectable();

    ConnectableObservable<Long> observable = interval();
    observable.connect();
    try {
      Thread.sleep(50L);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    observable.observeOn(Schedulers.newThread()).subscribe(
        new Observer<Long>() {
          @Override
          public void onSubscribe(Disposable d) {}

          @Override
          public void onNext(Long aLong) {
            System.out.println("Long" + aLong);
          }

          @Override
          public void onError(Throwable e) {
            System.out.println(e.toString());
          }

          @Override
          public void onComplete() {}
        });

    try {
      Thread.sleep(100L);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static ConnectableObservable<Long> interval() {
        ConnectableObservable<Long> connectableObservable =
            Observable.create(
                    new ObservableOnSubscribe<Long>() {
                      @Override
                      public void subscribe(ObservableEmitter<Long> emitter) throws Exception {
                        Observable.interval(10, TimeUnit.MILLISECONDS, Schedulers.computation()).take(Long.MAX_VALUE)
                            .subscribe(emitter::onNext);
                      }
                    })
                .publish();

        //TODO 为何这样不行
//    ConnectableObservable<Long> connectableObservable =
//        Observable.interval(1, TimeUnit.MILLISECONDS, Schedulers.trampoline()).take(Long.MAX_VALUE).publish();
    return connectableObservable;
  }

  static class Person {
    Person() {
      System.out.println("执行了构造函数");
    }
  }

  public static void connectable() {
    Consumer<Long> subscriber1 =
        new Consumer<Long>() {
          @Override
          public void accept(@NonNull Long aLong) throws Exception {
            System.out.println("subscriber1: " + aLong);
          }
        };

    Consumer<Long> subscriber2 =
        new Consumer<Long>() {
          @Override
          public void accept(@NonNull Long aLong) throws Exception {
            System.out.println("   subscriber2: " + aLong);
          }
        };

    Consumer<Long> subscriber3 =
        new Consumer<Long>() {
          @Override
          public void accept(@NonNull Long aLong) throws Exception {
            System.out.println("      subscriber3: " + aLong);
          }
        };

    ConnectableObservable<Long> observable =
        Observable.create(
                new ObservableOnSubscribe<Long>() {
                  @Override
                  public void subscribe(@NonNull ObservableEmitter<Long> e) throws Exception {
                    Observable.interval(10, TimeUnit.MILLISECONDS, Schedulers.computation())
                        .take(Integer.MAX_VALUE)
                        .subscribe(e::onNext);
                  }
                })
            .observeOn(Schedulers.newThread())
            .publish();
    observable.connect();

    //    observable.subscribe(subscriber1);
    //    observable.subscribe(subscriber2);

    try {
      Thread.sleep(50L);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    observable.subscribe(subscriber3);

    try {
      Thread.sleep(100L);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
