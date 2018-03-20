/*
 * RoomExecutor.java
 *
 * Description:
 *
 * Author Deng Xinliang
 *
 * Ver 1.0, Mar 13, 2018, Deng Xinliang, Create file
 */
package com.littleinc.orm_benchmark.room;

import android.content.Context;
import android.util.Log;

import com.littleinc.orm_benchmark.BenchmarkExecutable;

import java.sql.SQLException;

public class RoomExecutor implements BenchmarkExecutable {
    private static final String TAG = "RoomExecutor";

    private RoomDatabase db;

    @Override
    public String getOrmName() {
        return "ROOM";
    }

    @Override
    public void init(Context context, boolean useInMemoryDb) {
        db = RoomDatabase.getInstance(context, useInMemoryDb);
        db.dropTable();
    }

    @Override
    public long createDbStructure() throws SQLException {
        long start = System.nanoTime();
        db.createTable();
        return System.nanoTime() - start;
    }

    @Override
    public long writeWholeData() throws SQLException {
        RoomUser[] users = new RoomUser[NUM_USER_INSERTS];
        for (int i = 0; i < NUM_USER_INSERTS; i++) {
            RoomUser newUser = new RoomUser();
            newUser.fillUserWithRandomData();
            users[i] = newUser;
        }

        RoomMessage[] messages = new RoomMessage[NUM_MESSAGE_INSERTS];
        for (int i = 0; i < NUM_MESSAGE_INSERTS; i++) {
            RoomMessage newMessage = new RoomMessage();
            newMessage.fillMessageWithRandomData(i, NUM_USER_INSERTS);
            messages[i] = newMessage;
        }

        long start = System.nanoTime();
        db.getUserDao().insert(users);
        db.getMessageDao().insert(messages);
        return System.nanoTime() - start;
    }

    @Override
    public long readWholeData() throws SQLException {
        long start = System.nanoTime();
        RoomMessage[] messages = db.getMessageDao().query();
        Log.d(TAG, "Read, " + messages.length + " rows");
        return System.nanoTime() - start;
    }

    @Override
    public long readIndexedField() throws SQLException {
        long start = System.nanoTime();
        db.getMessageDao().queryByIndexed();
        return System.nanoTime() - start;
    }

    @Override
    public long readSearch() throws SQLException {
        long start = System.nanoTime();
        RoomMessage[] messages = db.getMessageDao().search();
        Log.d(TAG, "Read, " + messages.length + " rows");
        return System.nanoTime() - start;
    }

    @Override
    public long dropDb() throws SQLException {
        long start = System.nanoTime();
        db.dropTable();
        return System.nanoTime() - start;
    }
}
