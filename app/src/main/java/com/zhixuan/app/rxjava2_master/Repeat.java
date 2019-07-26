package com.zhixuan.app.rxjava2_master;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class Repeat {

  public static void main(String[] args) {
//    repeat();
		  repeatWhen();
  }

  /*
   * 对某一个Observable重复产生多次结果,当repeat() 接收到onComplete()会触发重订阅
   * */
  private static void repeat() {
    Disposable disposable =
        Observable.range(2, 5)
            .repeat(3)
            .subscribe(
                new Consumer<Integer>() {
                  @Override
                  public void accept(Integer integer) throws Exception {
                    System.out.println(integer);
                  }
                });
    disposable.isDisposed();
  }

  /*
  * 让订阅者多次订阅，如:第一次订阅1-5 间隔6秒后又会重新订阅一次
  * */
  private static void repeatWhen() {
    Observable.range(1, 5)
        .repeatWhen(new Function<Observable<Object>, ObservableSource<?>>() {
		        @Override
		        public ObservableSource<?> apply(Observable<Object> objectObservable) throws Exception {
				        return Observable.timer(6, TimeUnit.SECONDS);
		        }
        })
        .subscribe(new Observer<Integer>() {
		        @Override
		        public void onSubscribe(Disposable d) {

		        }

		        @Override
		        public void onNext(Integer integer) {
				        System.out.println(integer);
		        }

		        @Override
		        public void onError(Throwable e) {

		        }

		        @Override
		        public void onComplete() {
				        System.out.println("onComplete");
		        }
        });

		  try {
				  Thread.sleep(10000);
		  } catch (InterruptedException e) {
				  e.printStackTrace();
		  }
  }
}
