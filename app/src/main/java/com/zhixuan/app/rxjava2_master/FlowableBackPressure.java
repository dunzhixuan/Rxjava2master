package com.zhixuan.app.rxjava2_master;

import org.reactivestreams.Subscriber;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.internal.subscribers.BlockingBaseSubscriber;

public class FlowableBackPressure {

		/*https://www.jianshu.com/p/ceb48ed8719d*/
		private void flowable(){
				Flowable flowable = Flowable.create(new FlowableOnSubscribe<Integer>() {
						@Override
						public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {

						}
				}, BackpressureStrategy.ERROR);

				Flowable flowable1 = Flowable.just("1");

				Subscriber subscriber = new BlockingBaseSubscriber() {
						@Override
						public void onNext(Object o) {

						}

						@Override
						public void onError(Throwable t) {

						}
				};

				flowable.subscribe(subscriber);
		}
}
