package com.zhixuan.app.rxjava2_master;

import android.util.Log;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

public class Request {

  public static void main(String[] args) {
    request();
  }

  private static String TBG = "TBG";

  public static void request() {
    // 1. 创建被观察者Flowable
    Flowable.create(
            new FlowableOnSubscribe<Integer>() {
              @Override
              public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                // 一共发送4个事件
                System.out.println("发送事件 1");
                emitter.onNext(1);
                System.out.println("发送事件 2");
                emitter.onNext(2);
                System.out.println("发送事件 3");
                emitter.onNext(3);
                System.out.println("发送事件 4");
                emitter.onNext(4);
                System.out.println("发送完成");
                emitter.onComplete();
              }
            },
            BackpressureStrategy.ERROR)
        .subscribeOn(Schedulers.trampoline()) // 设置被观察者在io线程中进行
        .observeOn(Schedulers.newThread()) // 设置观察者另外的线程中进行
        .subscribe(
            new Subscriber<Integer>() {
              @Override
              public void onSubscribe(Subscription s) {
                // 对比Observer传入的Disposable参数，Subscriber此处传入的参数 = Subscription
                // 相同点：Subscription参数具备Disposable参数的作用，即Disposable.dispose()切断连接,
                // 同样的调用Subscription.cancel()切断连接
                // 不同点：Subscription增加了void request(long n)

                s.request(3);
                // 作用：决定观察者能够接收多少个事件
                // 如设置了s.request(3)，这就说明观察者能够接收3个事件（多出的事件存放在缓存区）
                // 官方默认推荐使用Long.MAX_VALUE，即s.request(Long.MAX_VALUE);
              }

              @Override
              public void onNext(Integer integer) {
                System.out.println("接收到了事件" + integer);
              }

              @Override
              public void onError(Throwable t) {
                System.out.println("onError: " + t);
              }

              @Override
              public void onComplete() {
                System.out.println("onComplete");
              }
            });
  }
}
