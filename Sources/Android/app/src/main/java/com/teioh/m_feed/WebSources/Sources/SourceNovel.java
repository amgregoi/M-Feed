package com.teioh.m_feed.WebSources.Sources;

import com.teioh.m_feed.MangaEnums;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.Utils.NetworkService;
import com.teioh.m_feed.WebSources.RequestWrapper;
import com.teioh.m_feed.WebSources.SourceBase;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by amgregoi on 6/22/17.
 */

public abstract class SourceNovel extends SourceBase
{
    @Override
    public abstract MangaEnums.eSourceType getSourceType();

    @Override
    public abstract String getRecentUpdatesUrl();

    @Override
    public abstract String[] getGenres();

    @Override
    public abstract List<Manga> parseResponseToRecentList(String aResponseBody);

    @Override
    public abstract Manga parseResponseToManga(RequestWrapper aRequest, String aResponseBody);

    @Override
    public abstract List<Chapter> parseResponseToChapters(RequestWrapper aRequest, String aResponseBody);

    @Override
    public abstract List<String> parseResponseToPageUrls(String aResponseBody);

    @Override
    public abstract String parseResponseToImageUrls(String aResponseBody, String aResponseUrl);

    public Observable<String> getChapterImageListObservable(final RequestWrapper aRequest)
    {
        final NetworkService lCurrentService = NetworkService.getTemporaryInstance();

        return lCurrentService.getResponse(aRequest.getChapterUrl())
                              .flatMap(aResponse -> NetworkService.mapResponseToString(aResponse))
                              .flatMap(aUnparsedHtml -> Observable.just(parseResponseToImageUrls(aUnparsedHtml, aRequest.getChapterUrl())))
                              .onBackpressureBuffer();

    }

}
