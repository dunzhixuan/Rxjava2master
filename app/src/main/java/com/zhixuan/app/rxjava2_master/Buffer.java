package com.zhixuan.app.rxjava2_master;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class Buffer {

		public static void main(String[] args) {
				buffer();
		}

		private static void buffer(){
				Observable<Integer> observable = new Observable<Integer>() {
						@Override
						protected void subscribeActual(Observer<? super Integer> observer) {
								observer.onNext(1);
								observer.onNext(2);
								observer.onNext(3);
								observer.onNext(4);
						}
				};

				Disposable disposable = observable.buffer(2).subscribe(new Consumer<List<Integer>>() {
						@Override
						public void accept(List<Integer> integers) throws Exception {
								System.out.println("accept: " + integers.size());
						}
				}, new Consumer<Throwable>() {
						@Override
						public void accept(Throwable throwable) throws Exception {

						}
				}, new Action() {
						@Override
						public void run() throws Exception {

						}
				});
				disposable.isDisposed();
		}
}
