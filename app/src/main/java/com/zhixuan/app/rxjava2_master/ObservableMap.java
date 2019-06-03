package com.zhixuan.app.rxjava2_master;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ObservableMap {

  public static void main(String[] args) {
//    flatmap();
    //      contcatmap();
      buffer();
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
                  }

                  // 采用flatMap（）变换操作符
                })
            .flatMap(
                new Function<Integer, ObservableSource<String>>() {
                  @Override
                  public ObservableSource<String> apply(Integer integer) throws Exception {
                    final List<String> list = new ArrayList<>();
                    for (int i = 0; i < 1000; i++) {
                      list.add("我是事件 " + integer + "拆分后的子事件" + i);
                      // 通过flatMap中将被观察者生产的事件序列先进行拆分，再将每个事件转换为一个新的发送三个String事件
                      // 最终合并，再发送给被观察者
                    }
                    return Observable.fromIterable(list);
                  }
                })
            .subscribe(
                new Consumer<String>() {
                  @Override
                  public void accept(String s) throws Exception {
                    System.out.println(s);
                  }
                });
  }

  private static void contcatmap() {
    // 采用RxJava基于事件流的链式操作
    Disposable disposable =
        Observable.create(
                new ObservableOnSubscribe<Integer>() {
                  @Override
                  public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                    emitter.onNext(1);
                    emitter.onNext(2);
                    emitter.onNext(3);
                  }

                  // 采用concatMap（）变换操作符
                })
            .concatMap(
                new Function<Integer, ObservableSource<String>>() {
                  @Override
                  public ObservableSource<String> apply(Integer integer) throws Exception {
                    final List<String> list = new ArrayList<>();
                    for (int i = 0; i < 3; i++) {
                      list.add("我是事件 " + integer + "拆分后的子事件" + i);
                      // 通过concatMap中将被观察者生产的事件序列先进行拆分，再将每个事件转换为一个新的发送三个String事件
                      // 最终合并，再发送给被观察者
                    }
                    return Observable.fromIterable(list);
                  }
                })
            .subscribe(
                new Consumer<String>() {
                  @Override
                  public void accept(String s) throws Exception {
                    System.out.println(s);
                  }
                });
  }

  private static void buffer() {
    // 被观察者 需要发送5个数字
    Observable.just(1, 2, 3, 4, 5)
        .buffer(3, 1) // 设置缓存区大小 & 步长
        // 缓存区大小 = 每次从被观察者中获取的事件数量
        // 步长 = 每次获取新事件的数量
        .subscribe(
            new Observer<List<Integer>>() {
              @Override
              public void onSubscribe(Disposable d) {}

              @Override
              public void onNext(List<Integer> stringList) {
                //
                System.out.println(" 缓存区里的事件数量 = " + stringList.size());
                for (Integer value : stringList) {
                  System.out.println(" 事件 = " + value);
                }
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
}
