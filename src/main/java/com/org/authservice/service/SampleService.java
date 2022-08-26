package com.org.authservice.service;

import com.org.authservice.dao.SampleDao;
import com.org.authservice.models.SampleEntry;
import org.skife.jdbi.v2.sqlobject.CreateSqlObject;

public abstract class SampleService {

    @CreateSqlObject
    abstract SampleDao sampleDao();

    public void create(SampleEntry entry) {
        sampleDao().create(entry);
    }
}

