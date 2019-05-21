package com.zhixuan.app.rxjava2_master;

import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.internal.observers.BlockingBaseObserver;
import io.reactivex.schedulers.Schedulers;

public class ObservableCreate {

  public static void main(String[] argss) {
    intervalRange();
  }

  private static void create() {

    Observable.create(
            new ObservableOnSubscribe<String>() {
              @Override
              public void subscribe(ObservableEmitter<String> emitter /* ①*/) throws Exception {
                // 保证顺序
                emitter.serialize();
              }
            })
        .subscribe(
            new BlockingBaseObserver<String>() {
              @Override
              public void onNext(String s) {}

              @Override
              public void onError(Throwable e) {}
            });

    Observable.create(
            new ObservableOnSubscribe<String>() {
              @Override
              public void subscribe(ObservableEmitter<String> emitter) throws Exception {}
            })
        .subscribe(
            new Observer<String>() {
              @Override
              public void onSubscribe(Disposable d) {
                // 其中isDisposed()方法用来判断当前订阅是否失效，dispose()方法用来取消当前订阅。
                // 只有当观察者Observer与可观察对象Observable之间建立订阅关系，并且订阅关系有效时，Observer才能对Observable进行响应。如果Observer在响应Observable的过程中，订阅关系被取消，
                // 则Observer无法对取消订阅关系之后Observable的行为作出响应。
                // 运行下面的代码，当Observable接收到第5条数据时，取消订阅关系。
                d.isDisposed();
                // 解除订阅关系
                d.dispose();
              }

              @Override
              public void onNext(String s) {}

              @Override
              public void onError(Throwable e) {}

              @Override
              public void onComplete() {}
            });
    // ①将Subscriber改为了ObservableEmitter
    // ②Disposable : 一次性资源
    // ③返回不再是Subscription

    Disposable disposable =
        Observable.just("1")
            .subscribe(
                new Consumer<String>() {
                  @Override
                  public void accept(String s) throws Exception {

                    //
                  }
                });

    Disposable disposable1 =
        Observable.just("1")
            .subscribe(
                new Consumer<String>() {
                  @Override
                  public void accept(String s) throws Exception {}
                },
                new Consumer<Throwable>() {
                  @Override
                  public void accept(Throwable throwable) throws Exception {}
                },
                new Action() {
                  @Override
                  public void run() throws Exception {}
                });

    Disposable disposable2 =
        Observable.just("1")
            .map(
                new Function<String, Integer>() {
                  @Override
                  public Integer apply(String s) throws Exception {
                    return null;
                  }
                })
            .subscribe(
                new Consumer<Integer>() {
                  @Override
                  public void accept(Integer integer) throws Exception {}
                });

    Observable observable = Observable.just("1");

    Disposable disposable3 =
        observable.subscribe(
            new Consumer<Integer>() {
              @Override
              public void accept(Integer integer) throws Exception {
                // 这里接收数据项
              }
            },
            new Consumer<Throwable>() {
              @Override
              public void accept(Throwable throwable) throws Exception {
                // 这里接收onError
              }
            },
            new Action() {
              @Override
              public void run() throws Exception {
                // 这里接收onComplete。
              }
            });

    Flowable flowable = Flowable.just("1");

    Disposable disposable4 = flowable.subscribe(
        new Consumer<String>() { // 相当于onNext
          @Override
          public void accept(String s) throws Exception {}
        },
        new Consumer<Throwable>() { // 相当于onError
          @Override
          public void accept(Throwable throwable) throws Exception {}
        },
        new Action() { // 相当于onComplete，注意这里是Action
          @Override
          public void run() throws Exception {}
        },
        new Consumer<Subscription>() { // 相当于onSubscribe
          @Override
          public void accept(Subscription subscription) throws Exception {}
        });
  }

  private static void from() {
    // 在Rxjava2中将所有的from全都拆散了
    Observable.fromArray(new String[] {}, new Integer[] {}).subscribe();
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
