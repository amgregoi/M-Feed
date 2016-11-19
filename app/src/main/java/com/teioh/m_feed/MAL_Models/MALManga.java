package com.teioh.m_feed.MAL_Models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.util.Date;

@Root(name = "entry") public class MALManga
{

    @Element(name = "id", required = false) public int id;

    @Element(name = "title", required = false) public String title;

    @Element(name = "english", required = false) public String english;

    @Element(name = "synonyms", required = false) public String synonyms; //comma seperated

    @Element(name = "chapters", required = false) public int chapters;

    @Element(name = "volumes", required = false) public int volumes;

    @Element(name = "score", required = false) public float score;

    @Element(name = "type", required = false) public String type;

    @Element(name = "status", required = false) public String status;

    @Element(name = "start_date", required = false) public Date start_date;

    @Element(name = "end_date", required = false) public Date end_date;

    @Element(name = "synopsis", required = false) public String synopsis;

    @Element(name = "image", required = false) public String image;

}
