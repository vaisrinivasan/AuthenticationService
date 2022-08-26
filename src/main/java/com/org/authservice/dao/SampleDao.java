package com.org.authservice.dao;

import com.org.authservice.models.SampleEntry;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

@RegisterMapper(SampleMapper.class)
public interface SampleDao {
    @SqlUpdate("insert into Sample values(:id, :text)")
    void create(@BindBean final SampleEntry entry);
}
