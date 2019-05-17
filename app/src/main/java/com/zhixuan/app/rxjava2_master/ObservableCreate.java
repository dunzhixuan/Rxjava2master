package com.zhixuan.app.rxjava2_master;

import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.observers.BlockingBaseObserver;
import io.reactivex.schedulers.Schedulers;

public class ObservableCreate {

  public static void main(String[] argss) {
    intervalRange();
  }

  private static void create(){

      Observable.create(new ObservableOnSubscribe<String>() {
          @Override
          public void subscribe(ObservableEmitter<String> emitter/* ①*/) throws Exception {

          }
      }).subscribe(new BlockingBaseObserver<String>() {
          @Override
          public void onNext(String s) {

          }

          @Override
          public void onError(Throwable e) {

          }
      });

      //①将Subscriber改为了ObservableEmitter
  }

  private static void intervalRange() {
    Observable.intervalRange(10, 5, 10, 1, TimeUnit.SECONDS, Schedulers.trampoline())
        .subscribe(
            new Observer<Long>() {
              @Override
              public void onSubscribe(Disposable d) {}

              @Override
              public void onNext(Long aLong) {}

              @Override
              public void onError(Throwable e) {}

              @Override
              public void onComplete() {}
            });
  }
}
