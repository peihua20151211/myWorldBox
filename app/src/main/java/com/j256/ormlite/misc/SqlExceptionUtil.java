package com.j256.ormlite.misc;

import java.sql.SQLException;

public class SqlExceptionUtil {
    private SqlExceptionUtil() {
    }

    public static SQLException create(String message, Throwable cause) {
        SQLException sqlException;
        if (cause instanceof SQLException) {
            sqlException = new SQLException(message, ((SQLException) cause).getSQLState());
        } else {
            sqlException = new SQLException(message);
        }
        sqlException.initCause(cause);
        return sqlException;
    }
}
