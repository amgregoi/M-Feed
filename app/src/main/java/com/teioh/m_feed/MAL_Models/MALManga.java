package com.teioh.m_feed.MAL_Models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.util.Date;

@Root(name = "entry")
public class MALManga {

    @Element(name = "id", required = false)
    public int id;

    @Element(name = "title", required = false)
    public String title;

    @Element(name = "english", required = false)
    public String english;

    @Element(name = "synonyms", required = false)
    public String synonyms; //comma seperated

    @Element(name = "chapters", required = false)
    public int chapters;

    @Element(name = "volumes", required = false)
    public int volumes;

    @Element(name = "score", required = false)
    public float score;

    @Element(name = "type", required = false)
    public String type;

    @Element(name = "status", required = false)
    public String status;

    @Element(name = "start_date", required = false)
    public Date start_date;

    @Element(name = "end_date", required = false)
    public Date end_date;

    @Element(name = "synopsis", required = false)
    public String synopsis;

    @Element(name = "image", required = false)
    public String image;







//    public int chapter;
//    public int volume;
//    public int status;//string. 1/reading, 2/completed, 3/onhold, 4/dropped, 6/plantoread
//    public int score;
//    public int downloaded_chapters;
//    public int times_reread;
//    public int reread_value;
//    public Date date_start;
//    public Date date_finish;
//    public int priority;
//    public int enable_discussion; //1=enable, 0=disable
//    public int enable_rereading;
//    public String comments;
//    public String scan_group;
//    public String tags; //tags separated by commas
//    public int retail_volumes;
}
