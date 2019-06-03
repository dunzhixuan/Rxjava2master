package com.zhixuan.app.rxjava2_master;

import org.reactivestreams.Subscription;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.internal.observers.BlockingBaseObserver;
import io.reactivex.schedulers.Schedulers;

public class ObservableCreate {

  public static void main(String[] argss) {
    //    intervalRange();
    //    defer();
    //    create2();
//    just();
      concat();
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

    Disposable disposable4 =
        flowable.subscribe(
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
            .take(3)
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

  private static Integer i = 10;

  private static void defer() {

    // 2. 通过defer 定义被观察者对象
    // 注：此时被观察者对象还没创建
    Observable<Integer> observable =
        Observable.defer(
            new Callable<ObservableSource<? extends Integer>>() {
              @Override
              public ObservableSource<? extends Integer> call() throws Exception {
                return Observable.just(i);
              }
            });

    i = 15;

    // 注：此时，才会调用defer（）创建被观察者对象（Observable）
    observable.subscribe(
        new Observer<Integer>() {

          @Override
          public void onSubscribe(Disposable d) {
            System.out.println("开始采用subscribe连接");
          }

          @Override
          public void onNext(Integer value) {
            System.out.println("接收到的整数是" + value);
          }

          @Override
          public void onError(Throwable e) {
            System.out.println("对Error事件作出响应");
          }

          @Override
          public void onComplete() {
            System.out.println("对Complete事件作出响应");
          }
        });
  }

  private static void create2() {
    Observable<Integer> observable =
        Observable.create(
            new ObservableOnSubscribe<Integer>() {
              @Override
              public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(i);
                emitter.onComplete();
              }
            });

    i = 15;

    observable.subscribe(
        new Observer<Integer>() {
          @Override
          public void onSubscribe(Disposable d) {
            System.out.println("开始采用subscribe连接");
          }

          @Override
          public void onNext(Integer integer) {
            System.out.println("接收到的整数是" + integer);
          }

          @Override
          public void onError(Throwable e) {
            System.out.println("对Error事件作出响应");
          }

          @Override
          public void onComplete() {
            System.out.println("对Complete事件作出响应");
          }
        });
  }

  private static void just() {
    Observable observable = Observable.just(i);

    i = 15;

    observable.subscribe(
        new Observer() {
          @Override
          public void onSubscribe(Disposable d) {
            System.out.println("开始采用subscribe连接");
          }

          @Override
          public void onNext(Object o) {
            System.out.println("接收到的整数是" + o);
          }

          @Override
          public void onError(Throwable e) {
            System.out.println("对Error事件作出响应");
          }

          @Override
          public void onComplete() {
            System.out.println("对Complete事件作出响应");
          }
        });
  }

  private static void concat() {
    Disposable disposable =
        Observable.concat(Observable.just(1,2,3), Observable.just(5,1,2,6))
            .subscribe(
                new Consumer<Integer>() {
                  @Override
                  public void accept(Integer integer) throws Exception {
                    System.out.println(integer);
                  }
                });

    Disposable disposable1 = Observable.concat(Observable.just(1,2),Observable.just("!")).subscribe(new Consumer<Serializable>() {
        @Override
        public void accept(Serializable serializable) throws Exception {

        }
    });
  }
}
