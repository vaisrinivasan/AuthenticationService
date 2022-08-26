package com.org.authservice.dao;

import com.org.authservice.models.SampleEntry;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SampleMapper implements ResultSetMapper<SampleEntry> {

    private static final String ID = "id";
    private static final String TEXT = "text";

    public SampleEntry map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        return new SampleEntry(resultSet.getInt(ID), resultSet.getString(TEXT));
    }
}

