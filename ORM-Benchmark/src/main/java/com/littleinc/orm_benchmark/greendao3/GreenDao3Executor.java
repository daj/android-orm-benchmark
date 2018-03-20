/*
 * GreenDao3Executor.java
 *
 * Description:
 *
 * Author Deng Xinliang
 *
 * Ver 1.0, Mar 19, 2018, Deng Xinliang, Create file
 */
package com.littleinc.orm_benchmark.greendao3;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.littleinc.orm_benchmark.BenchmarkExecutable;
import com.littleinc.orm_benchmark.util.Util;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import static com.littleinc.orm_benchmark.util.Util.getRandomString;

public class GreenDao3Executor implements BenchmarkExecutable {

    private static final String TAG = "GreenDao3Executor";

    private static final String DB_NAME = "greendao3_db";

    private DaoMaster mDaoMaster;
    private DaoSession mSession;

    @Override
    public void init(Context context, boolean useInMemoryDb) {
        Log.d(TAG, "Creating DataBaseHelper");
        SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(context, (useInMemoryDb ? null : DB_NAME));
        helper.getWritableDatabase().execSQL("DROP TABLE IF EXISTS \"user\"");
        helper.getWritableDatabase().execSQL("DROP TABLE IF EXISTS \"message\"");
        mDaoMaster = new DaoMaster(helper.getWritableDatabase());
        mSession = mDaoMaster.newSession();
    }

    @Override
    public long createDbStructure() throws SQLException {
        long start = System.nanoTime();
        DaoMaster.createAllTables(mDaoMaster.getDatabase(), true);
        return System.nanoTime() - start;
    }

    @Override
    public long writeWholeData() throws SQLException {
        final List<User> users = new LinkedList<User>();
        for (int i = 0; i < NUM_USER_INSERTS; i++) {
            User newUser = new User();
            newUser.setFirstName(getRandomString(10));
            newUser.setLastName(getRandomString(10));
            users.add(newUser);
        }

        final List<Message> messages = new LinkedList<Message>();
        for (long i = 0; i < NUM_MESSAGE_INSERTS; i++) {
            Message newMessage = new Message();
            newMessage.setCommandId(i);
            newMessage.setSortedBy((double) System.nanoTime());
            newMessage.setContent(Util.getRandomString(100));
            newMessage.setClientId(System.currentTimeMillis());
            newMessage.setSenderId(Math.round(Math.random() * NUM_USER_INSERTS));
            newMessage.setChannelId(Math.round(Math.random() * NUM_USER_INSERTS));
            newMessage.setCreatedAt((int) (System.currentTimeMillis() / 1000L));

            messages.add(newMessage);
        }

        long start = System.nanoTime();
        final DaoSession daoSession = mSession;
        daoSession.runInTx(new Runnable() {

            @Override
            public void run() {
                UserDao userDao = daoSession.getUserDao();
                for (User user : users) {
                    userDao.insertOrReplace(user);
                }
                Log.d(GreenDao3Executor.class.getSimpleName(), "Done, wrote "
                        + NUM_USER_INSERTS + " users");

                MessageDao messageDao = daoSession.getMessageDao();
                for (int i = 0, size = messages.size(); i < size; i++) {
                    Message message = messages.get(i);
                    message.setId(i + 1);
                    messageDao.insertOrReplace(message);
                }
                Log.d(GreenDao3Executor.class.getSimpleName(), "Done, wrote "
                        + NUM_MESSAGE_INSERTS + " messages");
                daoSession.clear();
            }
        });
        return System.nanoTime() - start;
    }

    @Override
    public long readWholeData() throws SQLException {
        long start = System.nanoTime();
        DaoSession daoSession = mSession;
        MessageDao messageDao = daoSession.getMessageDao();
        Log.d(GreenDao3Executor.class.getSimpleName(), "ReadWhole, "
                + messageDao.loadAll().size() + " rows");
        daoSession.clear();
        return System.nanoTime() - start;
    }

    @Override
    public long readIndexedField() throws SQLException {
        long start = System.nanoTime();
        DaoSession daoSession = mSession;
        MessageDao messageDao = daoSession.getMessageDao();
        Log.d(GreenDao3Executor.class.getSimpleName(),
                "Read, "
                        + messageDao
                        .queryBuilder()
                        .where(MessageDao.Properties.CommandId
                                .eq(LOOK_BY_INDEXED_FIELD)).list()
                        .size() + " rows");
        daoSession.clear();
        return System.nanoTime() - start;
    }

    @Override
    public long readSearch() throws SQLException {
        long start = System.nanoTime();
        DaoSession daoSession = mSession;
        MessageDao messageDao = daoSession.getMessageDao();
        Log.d(GreenDao3Executor.class.getSimpleName(),
                "Read, "
                        + messageDao
                        .queryBuilder()
                        .limit((int) SEARCH_LIMIT)
                        .where(MessageDao.Properties.Content.like("%"
                                + SEARCH_TERM + "%")).list().size()
                        + " rows");
        daoSession.clear();
        return System.nanoTime() - start;
    }

    @Override
    public long dropDb() throws SQLException {
        long start = System.nanoTime();
        DaoMaster.dropAllTables(mDaoMaster.getDatabase(), true);
        return System.nanoTime() - start;
    }

    @Override
    public String getOrmName() {
        return "GreenDAO3";
    }
}
