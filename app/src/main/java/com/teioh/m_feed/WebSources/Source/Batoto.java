package com.teioh.m_feed.WebSources.Source;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.squareup.okhttp.Headers;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.Utils.Database.MangaFeedDbHelper;
import com.teioh.m_feed.WebSources.NetworkService;
import com.teioh.m_feed.WebSources.RequestWrapper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class Batoto {

    final public static String SourceKey = "Batoto";

    final static String mBaseUrl = "http://bato.to/";
    final static String mUpdatesUrl = "http://bato.to/search_ajax?order_cond=update&order=desc&p="; //add page number 1,2,3...

    private static Headers constructRequestHeaders() {
        Headers.Builder headerBuilder = new Headers.Builder();
        headerBuilder.add("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64)");
        headerBuilder.add("Cookie", "lang_option=English");

        return headerBuilder.build();
    }
    /**
     * builds list of manga for recently updated page
     *
     * @return
     */
    public static Observable<List<Manga>> getRecentUpdatesObservable() {
        List<Manga> returnList = new ArrayList<>();
        NetworkService currService = NetworkService.getTemporaryInstance();
        return currService
                .getResponseCustomHeaders(mUpdatesUrl + 1, constructRequestHeaders())
                .flatMap(response -> NetworkService.mapResponseToString(response))
                .flatMap(html -> Observable.just(scrapeUpdatestoManga(returnList, html)))
                .flatMap(list -> currService.getResponse(mUpdatesUrl + 2).flatMap(response -> NetworkService.mapResponseToString(response)).flatMap(html -> Observable.just(scrapeUpdatestoManga(list, html))))
                .flatMap(list -> currService.getResponse(mUpdatesUrl + 3).flatMap(response -> NetworkService.mapResponseToString(response)).flatMap(html -> Observable.just(scrapeUpdatestoManga(list, html))))
                .observeOn(AndroidSchedulers.mainThread())
                .retry(10)
                .doOnError(Throwable::printStackTrace);
    }

    private static List<Manga> scrapeUpdatestoManga(List<Manga> list, String html) {
        if (!html.contains("No (more) comics found!")) {
            Document parsedDocument = Jsoup.parse(html);
            SQLiteDatabase db = MangaFeedDbHelper.getInstance().getReadableDatabase();
            Elements mangaElements = parsedDocument.select("tr:not([id]):not([class])");


            for (Element htmlBlock : mangaElements) {
                Element urlElement = htmlBlock.select("a[href^=http://bato.to]").first();
                Element nameElement = urlElement;

                String mangaUrl = urlElement.attr("href");
                String mangaTitle = nameElement.text().trim();
                Manga manga = cupboard().withDatabase(db).query(Manga.class).withSelection("title = ? AND source = ?", mangaTitle, SourceKey).get();

                if (manga != null) {
                    list.add(manga);
                } else {
                    manga = new Manga(mangaTitle, mangaUrl, SourceKey);
                    list.add(manga);
                    cupboard().withDatabase(MangaFeedDbHelper.getInstance().getWritableDatabase()).put(manga);
                    Observable<Manga> observableManga = Batoto.updateMangaObservable(new RequestWrapper(manga));
                    observableManga.subscribe();
                }
            }
        }
        Log.i("Pull Recent Updates", "Finished pulling updates");
        return list;
    }

    /**
     * builds list of chapters for manga object
     *
     * @return
     */
    public static Observable<List<Chapter>> getChapterListObservable(final RequestWrapper request) {
        return NetworkService.getPermanentInstance()
                .getResponseCustomHeaders(request.getMangaUrl(), constructRequestHeaders())
                .flatMap(response -> NetworkService.mapResponseToString(response))
                .flatMap(unparsedHtml -> Observable.just(scrapeChaptersFromParsedDocument(request, unparsedHtml)))
                .observeOn(AndroidSchedulers.mainThread());
    }

    private static List<Chapter> scrapeChaptersFromParsedDocument(RequestWrapper request, String unparsedHtml) {
        List<Chapter> chapterList = new ArrayList<>();

        Document parsedDocument = Jsoup.parse(unparsedHtml);
        Elements chapterElements = parsedDocument.select("tr.row.lang_English.chapter_row");
        for (Element chapterElement : chapterElements) {

            Chapter newChapter = new Chapter();

            Element urlElement = chapterElement.select("a").first();
            Element nameElement = urlElement;
            Element dateElement = chapterElement.select("td").get(4);

            if (urlElement != null) {
                String fieldUrl = urlElement.attr("href");
                newChapter.setChapterUrl(fieldUrl);
            }
            if (nameElement != null) {
                String fieldName = nameElement.text().trim();
                newChapter.setChapterTitle(fieldName);
            }
            if (dateElement != null) {
                try {
                    long date = new SimpleDateFormat("dd MMMMM yyyy - hh:mm a", Locale.ENGLISH).parse(dateElement.text()).getTime();
                    newChapter.setChapterDate(new Date(date).toString());
                } catch (ParseException e) {

                }
            }

            newChapter.setMangaTitle(request.getMangaTitle());
            chapterList.add(newChapter);
        }

        Collections.reverse(chapterList);
        for(int i = 0; i<chapterList.size(); i++){
            chapterList.get(i).setChapterNumber(i+1);
        }

        return chapterList;
    }


    /**
     * ChapterFragment - takes a chapter url, and returns list of urls to chapter images
     *
     * @return
     */
    public static Observable<String> getChapterImageList(final RequestWrapper request) {
        final List<String> temporaryCachedImageUrls = new ArrayList<>();

        final NetworkService currentService = NetworkService.getTemporaryInstance();

        return currentService
                .getResponseCustomHeaders(request.getChapterUrl(), constructRequestHeaders())
                .flatMap(response -> NetworkService.mapResponseToString(response))
                .flatMap(unparsedHtml -> Observable.just(parseHtmlToPageUrls(unparsedHtml)))
                .flatMap(pageUrls -> Observable.from(pageUrls.toArray(new String[pageUrls.size()])))
                .buffer(5)
                .concatMap(batchedPageUrls -> {
                    List<Observable<String>> imageUrlObservables = new ArrayList<>();
                    for (String pageUrl : batchedPageUrls) {
                        Observable<String> temporaryObservable = currentService
                                .getResponseCustomHeaders(pageUrl, constructRequestHeaders())
                                .flatMap(response -> NetworkService.mapResponseToString(response))
                                .flatMap(unparsedHtml -> Observable.just(parseHtmlToImageUrl(unparsedHtml)))
                                .subscribeOn(Schedulers.io());

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

    private static List<String> parseHtmlToPageUrls(String unparsedHtml) {
        Document parsedDocument = Jsoup.parse(unparsedHtml);

        List<String> pageUrlList = new ArrayList<>();

        Elements pageUrlElements = parsedDocument.getElementById("page_select").getElementsByTag("option");
        for (Element pageUrlElement : pageUrlElements) {
            pageUrlList.add(pageUrlElement.attr("value"));
        }

        return pageUrlList;
    }

    private static String parseHtmlToImageUrl(String unparsedHtml) {
        int beginIndex = unparsedHtml.indexOf("<img id=\"comic_page\"");
        int endIndex = unparsedHtml.indexOf("</a>", beginIndex);
        String trimmedHtml = unparsedHtml.substring(beginIndex, endIndex);

        Document parsedDocument = Jsoup.parse(trimmedHtml);

        Element imageElement = parsedDocument.getElementById("comic_page");

        return imageElement.attr("src");
    }


    /**
     * Adds new Manga and
     * gets missing manga information and updates database
     *
     * @return
     */
    public static Observable<Manga> updateMangaObservable(final RequestWrapper request) {
        String mangaId = request.getMangaUrl().substring(request.getMangaUrl().lastIndexOf("r") + 1);
        return NetworkService.getPermanentInstance()
                .getResponseCustomHeaders("http://bato.to/comic_pop?id=" + mangaId, constructRequestHeaders())
                .flatMap(response -> NetworkService.mapResponseToString(response))
                .flatMap(unparsedHtml -> Observable.just(scrapeAndUpdateManga(request, unparsedHtml)));
    }

    private static Manga scrapeAndUpdateManga(RequestWrapper request, String unparsedHtml) {
        Document parsedDocument = Jsoup.parse(unparsedHtml);

        Element artistElement = parsedDocument.select("a[href^=http://bato.to/search?artist_name]").first();
        Element descriptionElement = parsedDocument.select("tr").get(5);
        Elements genreElements = parsedDocument.select("img[src=http://bato.to/forums/public/style_images/master/bullet_black.png]");
        Element thumbnailUrlElement = parsedDocument.select("img[src^=http://img.bato.to/forums/uploads/]").first();

        SQLiteDatabase sqLiteDatabase = MangaFeedDbHelper.getInstance().getWritableDatabase();
        StringBuilder selection = new StringBuilder();
        List<String> selectionArgs = new ArrayList<>();

        selection.append("source = ?");
        selectionArgs.add(request.getMangaTitle());
        selection.append(" AND ").append("title = ?");
        selectionArgs.add(request.getMangaUrl());

        Manga newManga = cupboard().withDatabase(sqLiteDatabase).query(Manga.class)
                .withSelection(selection.toString(), selectionArgs.toArray(new String[selectionArgs.size()]))
                .limit(1)
                .get();

        if(newManga == null) newManga = new Manga(request.getMangaTitle(), request.getMangaUrl(), SourceKey);

        if (artistElement != null) {
            String fieldArtist = artistElement.text();
            newManga.setArtist(fieldArtist);
            newManga.setAuthor(fieldArtist);
        }
        if (descriptionElement != null) {
            String fieldDescription = descriptionElement.text().substring("Description:".length()).trim();
            newManga.setDescription(fieldDescription);
        }
        if (genreElements != null) {
            String fieldGenres = "";
            for (int index = 0; index < genreElements.size(); index++) {
                String currentGenre = genreElements.get(index).attr("alt");

                if (index < genreElements.size() - 1) {
                    fieldGenres += currentGenre + ", ";
                } else {
                    fieldGenres += currentGenre;
                }
            }
            newManga.setmGenre(fieldGenres);
        }
        if (thumbnailUrlElement != null) {
            String fieldThumbnailUrl = thumbnailUrlElement.attr("src");
            newManga.setPicUrl(fieldThumbnailUrl);
        }

        boolean fieldCompleted = unparsedHtml.contains("<td>Complete</td>");
        if (fieldCompleted) newManga.setStatus("Complete");
        else newManga.setStatus("Ongoing");


        newManga.setInitialized(1);

        cupboard().withDatabase(sqLiteDatabase).put(newManga);
        Log.e("RAWR", newManga.getTitle() + ": updated");
        return newManga;

    }

}