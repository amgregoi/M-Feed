package com.teioh.m_feed.WebSources.Sources;

import android.content.ContentValues;
import android.util.Log;
import android.widget.Toast;

import com.teioh.m_feed.MFeedApplication;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.Utils.MFDBHelper;
import com.teioh.m_feed.Utils.NetworkService;
import com.teioh.m_feed.WebSources.RequestWrapper;
import com.teioh.m_feed.WebSources.Source;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MangaJoy extends Source {

    final public static String SourceKey = "MangaJoy";//SourceType.MangaJoy.name();

    final static String mBaseUrl = "http://funmanga.com/";
    final static String mUpdatesUrl = "http://funmanga.com/latest-chapters";

    final public static String genres[] = {
            "Action",
            "Adult",
            "Adventure",
            "Comedy",
            "Doujinshi",
            "Drama",
            "Ecchi",
            "Fantasy",
            "Gender Bender",
            "Harem",
            "Historical",
            "Horror",
            "Josei",
            "Lolicon",
            "Manga",
            "Manhua",
            "Manhwa",
            "Martial Arts",
            "Mature",
            "Mecha",
            "Mystery",
            "One shot",
            "Psychological",
            "Romance",
            "School Life",
            "Sci fi",
            "Seinen",
            "Shotacon",
            "Shoujo",
            "Shoujo Ai",
            "Shounen",
            "Shounen Ai",
            "Slice of Life",
            "Smut",
            "Sports",
            "Supernatural",
            "Tragedy",
            "Yaoi",
            "Yuri"
    };

    /***
     * builds list of manga for recently updated page
     *
     * @return
     */
    public Observable<List<Manga>> getRecentUpdatesObservable() {
        return NetworkService.getTemporaryInstance()
                .getResponse(mUpdatesUrl)
                .flatMap(response -> NetworkService.mapResponseToString(response))
                .flatMap(html -> Observable.just(scrapeUpdatestoManga(html)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(10)
                .doOnError(Throwable::printStackTrace);
    }

    /***
     * TODO...
     *
     * @param unparsedHtml
     * @return
     */
    private List<Manga> scrapeUpdatestoManga(final String unparsedHtml) {
        List<Manga> lMangaList = new ArrayList<>();
        Document lParsedDocument = Jsoup.parse(unparsedHtml);
        Elements lMangaElements = lParsedDocument.select("div.manga_updates").select("dl");

        for (Element wholeElement : lMangaElements) {
            Document parseSections = Jsoup.parse(wholeElement.toString());
            Elements usefulElements = parseSections.select("dt");
            for (Element usefulElement : usefulElements) {
                String lMangaTitle = usefulElement.select("a").attr("title");
                String lMangaUrl = usefulElement.select("a").attr("href");

                if(lMangaUrl.charAt(lMangaUrl.length()-1) != '/') lMangaUrl+= "/"; //add ending slash to url if missing
                Manga lManga = MFDBHelper.getInstance().getManga(lMangaUrl, SourceKey);
                if (lManga != null) {
                    lMangaList.add(lManga);
                } else {
                    lManga = new Manga(lMangaTitle, lMangaUrl, SourceKey);
                    lMangaList.add(lManga);
                    MFDBHelper.getInstance().putManga(lManga);
                    updateMangaObservable(new RequestWrapper(lManga))
                            .subscribeOn(Schedulers.computation())
                            .doOnError(throwable -> Toast.makeText(MFeedApplication.getInstance(), throwable.getMessage(), Toast.LENGTH_SHORT))
                            .subscribe();
                }
            }
        }
        Log.i("Pull Recent Updates", "Finished pulling updates");
        if (lMangaList.size() == 0) return null;
        return lMangaList;
    }

    /***
     * builds list of chapters for manga object
     *
     * @param request
     * @return
     */
    public Observable<List<Chapter>> getChapterListObservable(RequestWrapper request) {
        NetworkService currService = NetworkService.getTemporaryInstance();

        return currService
                .getResponse(request.getMangaUrl())
                .flatMap(response -> NetworkService.mapResponseToString(response))
                .flatMap(html -> Observable.just(parseHtmlToChapters(request, html)))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(throwable -> throwable.printStackTrace());
    }

    /***
     * TODO...
     *
     * @param aRequest
     * @param unparsedHtml
     * @return
     */
    private List<Chapter> parseHtmlToChapters(RequestWrapper aRequest, String unparsedHtml) {
        Document parsedDocument = Jsoup.parse(unparsedHtml);
        List<Chapter> chapterList = scrapeChaptersFromParsedDocument(parsedDocument, aRequest.getMangaTitle());
        return chapterList;
    }

    /***
     * TODO...
     *
     * @param parsedDocument
     * @param title
     * @return
     */
    private List<Chapter> scrapeChaptersFromParsedDocument(final Document parsedDocument, final String title) {
        List<Chapter> chapterList = new ArrayList<>();
        Elements chapterElements = parsedDocument.select("ul.chapter-list").select("li");
        int numChapters = chapterElements.size();


        for (Element chapterElement : chapterElements) {
            String chapterUrl = chapterElement.select("a").attr("href");
            String cTitle = chapterElement.select("span").first().text();

            String chapterDate = chapterElement.select("span").get(1).text();

            Chapter curChapter = new Chapter(chapterUrl, title, cTitle, chapterDate, numChapters);
            numChapters--;

            chapterList.add(curChapter);
        }
        return chapterList;
    }

    /***
     * TODO...
     *
     * @param aWrapper
     * @return
     */
    public Observable<String> getChapterImageListObservable(final RequestWrapper aWrapper) {
        final List<String> temporaryCachedImageUrls = new ArrayList<String>();
        final NetworkService currentService = NetworkService.getTemporaryInstance();

        return currentService
                .getResponse(aWrapper.getChapterUrl())
                .flatMap(aResponse -> NetworkService.mapResponseToString(aResponse))
                .flatMap(unparsedHtml -> Observable.just(parseHtmlToPageUrls(unparsedHtml)))
                .flatMap(pageUrls -> Observable.from(pageUrls.toArray(new String[pageUrls.size()])))
                .buffer(10)
                .concatMap(batchedPageUrls -> {
                    List<Observable<String>> imageUrlObservables = new ArrayList<>();
                    for (String pageUrl : batchedPageUrls) {
                        Observable<String> temporaryObservable = currentService
                                .getResponse(pageUrl)
                                .subscribeOn(Schedulers.io())
                                .flatMap(aResponse -> NetworkService.mapResponseToString(aResponse))
                                .flatMap(unparsedHtml -> Observable.just(parseHtmlToImageUrl(unparsedHtml)));
                        imageUrlObservables.add(temporaryObservable);
                    }

                    return Observable.zip(imageUrlObservables, args -> {
                        List<String> imageUrls = new ArrayList<>();
                        for (Object uncastImageUrl : args) {
                            imageUrls.add(String.valueOf(uncastImageUrl));
                        }
                        return imageUrls;
                    });
                })
                .concatMap(batchedImageUrls -> Observable.from(batchedImageUrls.toArray(new String[batchedImageUrls.size()])))
                .doOnNext(imageUrl -> temporaryCachedImageUrls.add(imageUrl))
                .onBackpressureBuffer();
    }

    /***
     * TODO...
     *
     * @param html
     * @return
     */
    public List<String> parseHtmlToPageUrls(String html) {
        List<String> images = new ArrayList<>();

        Document doc = Jsoup.parse(html);

        Elements nav = doc
                .select("h5.widget-heading")
                .select("select").select("option");

        int pages = nav.size();

        for (int i = 1; i < pages; i++) {
            String link = nav.get(i).attr("value");
            images.add(link);
        }
        return images;
    }

    /***
     * TODO...
     *
     * @param html
     * @return
     */
    public String parseHtmlToImageUrl(String html) {
        Document parsedDocument = Jsoup.parse(html);
        String link = parsedDocument.select("img.img-responsive").attr("src");
        return link;
    }

    /***
     * Adds new Manga and
     * gets missing manga information and updates database
     *
     * @param aRequest
     * @return
     */
    public Observable<Manga> updateMangaObservable(RequestWrapper aRequest) {
        NetworkService curService = NetworkService.getTemporaryInstance();

        return curService.getResponse(aRequest.getMangaUrl())
                .flatMap(response -> curService.mapResponseToString(response))
                .flatMap(html -> Observable.just(scrapeAndUpdateManga(html, aRequest)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /***
     * TODO...
     *
     * @param unparsedHtml
     * @param aRequest
     * @return
     */
    private Manga scrapeAndUpdateManga(final String unparsedHtml, RequestWrapper aRequest) {
        Document html = Jsoup.parse(unparsedHtml);

        try {
            //image url
            Element imageElement = html.body().select("img.img-responsive.mobile-img").first();
            //summary
            Element summaryElement = html.body().select("div.note.note-default.margin-top-15").first();

            Elements e = html.body().select("dl.dl-horizontal").select("dd");
            String img = imageElement.attr("src");
            String summary = summaryElement.text();
            String alternate = null;
            String author = null;
            String artist = null;
            String genres = null;
            String status = null;
            for (int i = 0; i < e.size(); i++) {
                if (i == 0) {
                    alternate = e.get(i).text();
                } else if (i == 5) {
                    author = e.get(i).text();
                } else if (i == 6) {
                    artist = e.get(i).text();
                } else if (i == 2) {
                    genres = e.get(i).text();
                } else if (i == 1) {
                    status = e.get(i).text();
                }
            }


            ContentValues values = new ContentValues(1);
            values.put("alternate", alternate);
            values.put("image", img);
            values.put("description", summary);
            values.put("artist", artist);
            values.put("author", author);
            values.put("genres", genres);
            values.put("status", status);
            values.put("source", SourceKey);

            MFDBHelper.getInstance().updateManga(values, aRequest.getMangaUrl());
            Manga manga = MFDBHelper.getInstance().getManga(aRequest.getMangaUrl(), SourceKey);
            return manga;
        }catch(Exception e){
            Log.e("rawr", e.getMessage());
        }

        return null;
    }
}


