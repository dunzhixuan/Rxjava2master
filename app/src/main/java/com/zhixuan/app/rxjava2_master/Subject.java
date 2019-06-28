package com.zhixuan.app.rxjava2_master;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.AsyncSubject;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;

public class Subject {

  public static void main(String[] args) {
    //    asyncSubject();
    //    behaviorSubject();
    //    replaySubject();
    publishSubject();
  }

  /*
   * 观察者/订阅者只会接受到onComplete之前的最后一个数据。
   * */
  private static void asyncSubject() {
    AsyncSubject<Integer> subject = AsyncSubject.create();
    subject.onNext(0);
    subject.onNext(1);
    subject.onNext(2);
    subject.onComplete();
    subject.subscribe(
        new Observer<Integer>() {
          @Override
          public void onSubscribe(Disposable d) {}

          @Override
          public void onNext(Integer integer) {
            System.out.println("onNext" + integer);
          }

          @Override
          public void onError(Throwable e) {
            System.out.println("throwable");
          }

          @Override
          public void onComplete() {
            System.out.println("onComplete");
          }
        });
  }

  /*
   * 观察者/订阅者会收到订阅之前的最后一个数据，再继续接受之后发射过来的数据，若BehaviorSubject订阅之前未发射过数据，则发射一个默认值。
   * */
  private static void behaviorSubject() {
    BehaviorSubject<Integer> subject = BehaviorSubject.createDefault(9);
    Disposable disposable =
        subject.subscribe(
            new Consumer<Integer>() {
              @Override
              public void accept(Integer integer) throws Exception {
                System.out.println("accept: " + integer);
              }
            },
            new Consumer<Throwable>() {
              @Override
              public void accept(Throwable throwable) {
                System.out.println("throwable");
              }
            },
            new Action() {
              @Override
              public void run() throws Exception {
                System.out.println("run: onComplete");
              }
            });
    subject.onNext(0);
    subject.onNext(1);
    subject.onComplete();
  }

  private static void replaySubject() {
    // 1.无论何时订阅，都将发射所有的原始数据给订阅者。
    ReplaySubject<Integer> subject = ReplaySubject.create();
    // 2.缓存n条数据，当订阅时只发送缓存过的数据和之后数据。
    //    ReplaySubject<Integer> subject = ReplaySubject.createWithSize(2);
    subject.onNext(0);
    subject.onNext(1);
    subject.onNext(3);
    subject.onNext(4);
    subject.onNext(5);

    Disposable disposable =
        subject.subscribe(
            new Consumer<Integer>() {
              @Override
              public void accept(Integer integer) throws Exception {
                System.out.println("accept: " + integer);
              }
            },
            new Consumer<Throwable>() {
              @Override
              public void accept(Throwable throwable) {}
            },
            new Action() {
              @Override
              public void run() throws Exception {
                System.out.println("run: onComplete");
              }
            });
    subject.onNext(6);
    subject.onNext(7);
    subject.onComplete();
    disposable.isDisposed();
  }

  /*
   * 观察者只接受PublishSubject订阅之后的数据。
   * */
  private static void publishSubject() {
    PublishSubject<Integer> subject = PublishSubject.create();
    subject.onNext(0);
    subject.onNext(1);

    Disposable disposable =
        subject.subscribe(
            new Consumer<Integer>() {
              @Override
              public void accept(Integer integer) throws Exception {
                System.out.println("accept: " + integer);
              }
            },
            new Consumer<Throwable>() {
              @Override
              public void accept(Throwable throwable) {}
            },
            new Action() {
              @Override
              public void run() throws Exception {
                System.out.println("run: onComplete");
              }
            });
    subject.onNext(3);
    subject.onNext(4);
  }

  /*
   * Processor和Subject用法一样，只是Processor支持被压。
   * 它也包含4中类型：AsyncProcessor, BehaviorProcessor,ReplayProcessor,PublishProcessor。
   * 用法同Subject一样。
   * */
}
