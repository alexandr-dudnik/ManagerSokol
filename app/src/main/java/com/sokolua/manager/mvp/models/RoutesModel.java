package com.sokolua.manager.mvp.models;

import com.sokolua.manager.ui.screens.routes.RouteListItem;

import java.util.Date;
import java.util.List;

import io.reactivex.Observable;

public class RoutesModel extends AbstractModel {
    public Observable<List<RouteListItem>> getVisitsByDate(Date day) {
        return mDataManager.getVisitsByDate(day)
                .flatMap(list->
                     Observable.fromIterable(list)
                            .map(RouteListItem::new)
                            .toList()
                            .toObservable()
                )
                ;
    }
}
