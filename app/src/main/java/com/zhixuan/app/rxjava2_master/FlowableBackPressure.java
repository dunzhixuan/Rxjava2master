package com.zhixuan.app.rxjava2_master;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.internal.subscribers.BlockingBaseSubscriber;

public class FlowableBackPressure {

    public static void main(String[] args){
        request();
    }
  /*1、同步订阅关系和异步订阅关系*/

  /*https://www.jianshu.com/p/ceb48ed8719d*/
  private void flowable() {
    Flowable flowable =
        Flowable.create(
            new FlowableOnSubscribe<Integer>() {
              @Override
              public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                  emitter.requested();
              }
            },
            BackpressureStrategy.BUFFER);

    Flowable flowable1 = Flowable.just("1");

    Subscriber subscriber =
        new BlockingBaseSubscriber() {
          @Override
          public void onNext(Object o) {}

          @Override
          public void onError(Throwable t) {}
        };

    Flowable.create(
            new FlowableOnSubscribe<String>() {
              @Override
              public void subscribe(FlowableEmitter<String> emitter) throws Exception {
                  emitter.requested();
                  emitter.onNext("事件1");
                  System.out.println("发送了事件1");
                  emitter.onNext("事件2");
                  System.out.println("发送了事件2");
                  emitter.onNext("事件3");
                  System.out.println("发送了事件3");
              }
            },
            BackpressureStrategy.ERROR)
        .subscribe(
            new Subscriber<String>() {
              @Override
              public void onSubscribe(Subscription s) {
                s.request(2);
              }

              @Override
              public void onNext(String s) {
                  System.out.println("接收了："+ s);
              }

              @Override
              public void onError(Throwable t) {}

              @Override
              public void onComplete() {}
            });

    // ①、s.request(3);
    // 作用：决定观察者能够接收多少个事件
    // 如设置了s.request(3)，这就说明观察者能够接收3个事件（多出的事件存放在缓存区）
    // 官方默认推荐使用Long.MAX_VALUE，即s.request(Long.MAX_VALUE);

    // ②、BackpressureStrategy.ERROR各个参数的用法 @See <a https://www.jianshu.com/p/ceb48ed8719d /a>
  }

  private static void request(){
      Flowable.create(
              new FlowableOnSubscribe<String>() {
                  @Override
                  public void subscribe(FlowableEmitter<String> emitter) throws Exception {
		                  // 调用emitter.requested()获取当前观察者需要接收的事件数量
                      emitter.requested();
                      System.out.println(emitter.requested());
                      System.out.println("发送了事件1");
                      emitter.onNext("事件1");
                      System.out.println("发送了事件2");
                      emitter.onNext("事件2");
                      System.out.println("发送了事件3");
                      emitter.onNext("事件3");
                  }
              },
              BackpressureStrategy.ERROR)
              .subscribe(
                      new Subscriber<String>() {
                          @Override
                          public void onSubscribe(Subscription s) {
                              s.request(2);
                          }

                          @Override
                          public void onNext(String s) {
                              System.out.println("接收了："+ s);
                          }

                          @Override
                          public void onError(Throwable t) {}

                          @Override
                          public void onComplete() {}
                      });
  }
}
