package com.teioh.m_feed;

import android.app.Application;

import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.Models.Manga;
import com.teioh.m_feed.Utils.Database.MangaFeedDbHelper;
import com.teioh.m_feed.Utils.SharedPrefsUtil;

import io.fabric.sdk.android.Fabric;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class MFeedApplication extends Application {


    static {
        cupboard().register(Manga.class);
        cupboard().register(Chapter.class);
    }

    private static MFeedApplication aInstance;

    public MFeedApplication() {
        aInstance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        //creates database if fresh install
        MangaFeedDbHelper.getInstance().createDatabase();
        SharedPrefsUtil.initializePreferences();

        MangaFeedDbHelper.getInstance().updateMangaFollow("/Blush-DC.");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Amatsuki");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Aphorism");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Arachnid");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Ares");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Baby Steps");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Baito Saki wa \"Aku no Soshiki\"?!");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Battle Through The Heavens");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Black Clover");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Blackout");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Blavet");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Bleach");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Blind Faith Descent");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Blood and Steel");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Change Guy");
        MangaFeedDbHelper.getInstance().updateMangaFollow("City of Dead Sorcerer");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Daiya no A - Act II");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Dark Air");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Days");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Deathtopia");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Delusional Love");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Demon King");
        MangaFeedDbHelper.getInstance().updateMangaFollow("DICE: the cube that changes everything");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Dorohedoro");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Duanzui Xiaoxue");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Ecstasy Hearts");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Fairy Tail");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Fantasista");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Freezing");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Fuuka");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Genyou no Meizu");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Girl the Wild's");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Gleipnir");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Good Night World");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Good Reaper");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Gosu (The Master)");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Groundless - Sekigan no Sogekihei");
        MangaFeedDbHelper.getInstance().updateMangaFollow("GTO Paradise Lost");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Gunner");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Hajime no Ippo");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Hanza Sky");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Hardcore Leveling Warrior");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Hikaru no go");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Imawa no Kuni no Alice");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Isekai de \"Kuro no Iyashi Te\" tte Yobarete Imasu");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Karate Shoukoushi Kohinata Minoru");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Kingdom");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Kiriwo Terrible");
        MangaFeedDbHelper.getInstance().updateMangaFollow("lessa The Crimson Knight");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Maeri-neun wehbakjoong");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Magi - Labyrinth of Magic");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Mahou Shoujo of the End");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Major 2nd");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Marchen - The Embodiment of Tales");
        MangaFeedDbHelper.getInstance().updateMangaFollow("MIX");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Monster x Monster");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Nejimaki Kagyu");
        MangaFeedDbHelper.getInstance().updateMangaFollow("New Prince of Tennis");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Noblesse");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Okitenemuru");
        MangaFeedDbHelper.getInstance().updateMangaFollow("One Piece");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Panlong");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Piano no mori");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Plunderer");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Red Storm");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Rure");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Savanna Game");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Shen Yin Wang Zuo");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Shirogane no Nina");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Shokugeki no Soma");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Silver Gravekeeper");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Skill of Lure");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Soul Cartel");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Soul Land II");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Spirit Blade Mountain");
        MangaFeedDbHelper.getInstance().updateMangaFollow("SS Sisters");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Sugarless");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Suicide Island");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Tale of Felluah");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Ten Prism");
        MangaFeedDbHelper.getInstance().updateMangaFollow("The Breaker");
        MangaFeedDbHelper.getInstance().updateMangaFollow("The Breaker: New Waves");
        MangaFeedDbHelper.getInstance().updateMangaFollow("The Gamer");
        MangaFeedDbHelper.getInstance().updateMangaFollow("The God of High School");
        MangaFeedDbHelper.getInstance().updateMangaFollow("The Great Ruler");
        MangaFeedDbHelper.getInstance().updateMangaFollow("The New Gate");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Tiger x Crane");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Tokyo ESP");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Tokyo Ghoul:re");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Toriko");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Tough");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Ubel Blatt");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Unbalance Triangle");
        MangaFeedDbHelper.getInstance().updateMangaFollow("UQ Holder!");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Vinland Saga");
        MangaFeedDbHelper.getInstance().updateMangaFollow("Wang Pai Yu Shi");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Glide.get(this).trimMemory(level);
    }

    public static synchronized MFeedApplication getInstance() {
        return aInstance;
    }

}
