/*
 * RoomDatabase.java
 *
 * Description:
 *
 * Author Deng Xinliang
 *
 * Ver 1.0, Mar 13, 2018, Deng Xinliang, Create file
 */
package com.littleinc.orm_benchmark.room;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.provider.BaseColumns;

@Database(entities = {RoomMessage.class, RoomUser.class}, version = 1)
public abstract class RoomDatabase extends android.arch.persistence.room.RoomDatabase {
    private static volatile RoomDatabase sInMemoryInstance;
    private static volatile RoomDatabase sDiskInstance;

    public static synchronized RoomDatabase getInstance(Context context, boolean useInMemory) {
        if (useInMemory) {
            return getInMemoryInstance(context);
        } else {
            return getDiskInstance(context);
        }
    }

    private static synchronized RoomDatabase getInMemoryInstance(Context context) {
        // DCL
        if (sInMemoryInstance == null) {
            synchronized (RoomDatabase.class) {
                if (sInMemoryInstance == null) {
                    initInMemory(context);
                }
            }
        }

        return sInMemoryInstance;
    }

    private static synchronized RoomDatabase getDiskInstance(Context context) {
        // DCL
        if (sDiskInstance == null) {
            synchronized (RoomDatabase.class) {
                if (sDiskInstance == null) {
                    initDisk(context);
                }
            }
        }
        return sDiskInstance;
    }

    private static void initInMemory(Context context) {
        final Context appContext = context.getApplicationContext();
        sInMemoryInstance = Room.inMemoryDatabaseBuilder(appContext, RoomDatabase.class)
                .allowMainThreadQueries()
                .build();
    }

    private static void initDisk(Context context) {
        final Context appContext = context.getApplicationContext();
        sInMemoryInstance = Room.databaseBuilder(appContext, RoomDatabase.class, "room")
                .allowMainThreadQueries()
                .build();
    }

    public void dropTable() {
        getOpenHelper().getWritableDatabase().execSQL("DROP TABLE user");
        getOpenHelper().getWritableDatabase().execSQL("DROP TABLE message");
    }

    public void createTable() {
        SupportSQLiteDatabase db = getOpenHelper().getWritableDatabase();
        db.execSQL("CREATE TABLE '" + "user" +
                "' ('" + BaseColumns._ID +
                "' INTEGER PRIMARY KEY AUTOINCREMENT, '" +
                "last_name" + "' TEXT, '" +
                "first_name" + "' TEXT);");

        db.execSQL("CREATE TABLE '" + "message" +
                "' ('" + BaseColumns._ID +
                "' INTEGER PRIMARY KEY AUTOINCREMENT, '" +
                "client_id" + "' INTEGER DEFAULT 0, '" + "sorted_by" +
                "' REAL DEFAULT 0, '" + "created_at" + "' INTEGER DEFAULT 0, '" +
                "content" + "' TEXT, '" + "sender_id" +
                "' INTEGER DEFAULT 0, '" + "channel_id" +
                "' INTEGER DEFAULT 0, '" + "command_id" +
                "' INTEGER DEFAULT 0);");

        db.execSQL("CREATE INDEX IDX_MESSAGE_COMMAND_ID ON message (command_id);");
    }

    public abstract RoomUserDao getUserDao();

    public abstract RoomMessageDao getMessageDao();
}
