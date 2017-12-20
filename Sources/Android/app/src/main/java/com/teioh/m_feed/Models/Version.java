package com.teioh.m_feed.Models;

public class Version
{
    int status;
    String status_message;
    String data;

    public Version(){};

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public String getStatus_message()
    {
        return status_message;
    }

    public void setStatus_message(String status_message)
    {
        this.status_message = status_message;
    }

    public String getData()
    {
        return data;
    }

    public void setData(String data)
    {
        this.data = data;
    }
}
