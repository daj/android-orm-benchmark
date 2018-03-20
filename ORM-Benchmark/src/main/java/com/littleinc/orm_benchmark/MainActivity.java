package com.littleinc.orm_benchmark;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.littleinc.orm_benchmark.BenchmarkExecutable.Task;
import com.littleinc.orm_benchmark.greendao.GreenDaoExecutor;
import com.littleinc.orm_benchmark.greendao3.GreenDao3Executor;
import com.littleinc.orm_benchmark.ormlite.ORMLiteExecutor;
import com.littleinc.orm_benchmark.room.RoomExecutor;
import com.littleinc.orm_benchmark.sqlite.SQLiteExecutor;
import com.littleinc.orm_benchmark.sqliteoptimized.OptimizedSQLiteExecutor;
import com.littleinc.orm_benchmark.util.Util;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.littleinc.orm_benchmark.BenchmarkExecutable.Task.CREATE_DB;
import static com.littleinc.orm_benchmark.BenchmarkExecutable.Task.DROP_DB;
import static com.littleinc.orm_benchmark.BenchmarkExecutable.Task.READ_DATA;
import static com.littleinc.orm_benchmark.BenchmarkExecutable.Task.READ_INDEXED;
import static com.littleinc.orm_benchmark.BenchmarkExecutable.Task.READ_SEARCH;
import static com.littleinc.orm_benchmark.BenchmarkExecutable.Task.WRITE_DATA;

public class MainActivity extends FragmentActivity {

    private static final boolean USE_IN_MEMORY_DB = true;

    private static final int NUM_ITERATIONS = 50;

    private int mCount = 0;

    private String mResults;

    private Button mShowResultsBtn;

    private BenchmarkExecutable[] mOrms = new BenchmarkExecutable[]{
            new SQLiteExecutor(),
            new OptimizedSQLiteExecutor(),
            new ORMLiteExecutor(),
            new GreenDaoExecutor(),
            new RoomExecutor(),
            new GreenDao3Executor()
    };

    private boolean mWasInitialized = false;

    private Map<String, Map<Task, List<Long>>> mGlobalResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGlobalResults = new HashMap<>();
        mShowResultsBtn = findViewById(R.id.show_results_btn);

        if (!mWasInitialized) {
            for (BenchmarkExecutable orm : mOrms) {
                orm.init(this, USE_IN_MEMORY_DB);
            }

            mWasInitialized = true;
        }
    }

    public void showGlobalResults(View v) {
        ResultDialog dialog = ResultDialog.newInstance(R.string.results_title, mResults);
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.add(dialog, ResultDialog.class.getSimpleName());
        tx.commit();
    }

    public void runBenchmark(View v) {
        if (mCount < NUM_ITERATIONS) {
            v.setEnabled(false);
            mShowResultsBtn.setEnabled(false);

            new ProfilerTask(v).execute(
                    CREATE_DB,
                    WRITE_DATA,
                    READ_DATA,
                    READ_INDEXED,
                    READ_SEARCH,
                    DROP_DB);
        } else {
            mResults = buildResults();
            Log.d(MainActivity.class.getSimpleName(), "Results:\n" + mResults);

            mCount = 0;
            v.setEnabled(true);
            mShowResultsBtn.setEnabled(true);
        }
    }

    private String buildResults() {
        StringBuilder builder = new StringBuilder();
        tasks:
        for (Task task : Task.values()) {
            builder.append("<b>Task ").append(task).append("</b><br />");
            for (BenchmarkExecutable orm : mOrms) {

                Map<Task, List<Long>> results = mGlobalResults.get(orm.getOrmName());

                if (results == null) {
                    continue;
                }
                List<Long> resultsPerTask = results.get(task);
                if (resultsPerTask == null) {
                    continue tasks;
                }
                int numExecutions = resultsPerTask.size();
                long resultsCount = 0;
                for (Long result : resultsPerTask) {
                    resultsCount += result;
                }
                builder.append(orm.getOrmName())
                        .append(" - Avg: ")
                        .append(Util.formatElapsedTime(resultsCount
                                / numExecutions)).append("<br />");
            }
            builder.append("<br />");
        }
        return builder.toString();
    }

    public static class ResultDialog extends DialogFragment {

        private static String TITLE_RES_ID = "title_res_id";

        private static String MESSAGE = "message";

        public static ResultDialog newInstance(int titleResId, String message) {
            ResultDialog dialog = new ResultDialog();

            Bundle args = new Bundle();
            args.putString(MESSAGE, message);
            args.putInt(TITLE_RES_ID, titleResId);
            dialog.setArguments(args);

            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            return builder
                    .setTitle(getArguments().getInt(TITLE_RES_ID))
                    .setMessage(Html.fromHtml(getArguments().getString(MESSAGE)))
                    .create();
        }
    }

    private class ProfilerTask extends AsyncTask<Task, Void, Void> {

        private View mView;

        public ProfilerTask(View v) {
            mView = v;
        }

        @Override
        protected Void doInBackground(Task... params) {
            for (BenchmarkExecutable item : mOrms) {
                for (Task task : params) {
                    try {
                        long result = 0;

                        switch (task) {
                            case CREATE_DB:
                                result = item.createDbStructure();
                                break;
                            case DROP_DB:
                                result = item.dropDb();
                                break;
                            case READ_DATA:
                                result = item.readWholeData();
                                break;
                            case READ_INDEXED:
                                result = item.readIndexedField();
                                break;
                            case READ_SEARCH:
                                result = item.readSearch();
                                break;
                            case WRITE_DATA:
                                result = item.writeWholeData();
                                break;
                        }
                        addProfilerResult(item.getOrmName(), task, result);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            mCount++;
            runBenchmark(mView);
        }

        ;

        private void addProfilerResult(String benchmarkName, Task task, long result) {
            Map<Task, List<Long>> profilerResults = mGlobalResults.get(benchmarkName);
            if (profilerResults == null) {
                profilerResults = new HashMap<>();
                profilerResults.put(task, new LinkedList<Long>());
                mGlobalResults.put(benchmarkName, profilerResults);
            }
            List<Long> resultPerTask = profilerResults.get(task);
            if (resultPerTask == null) {
                resultPerTask = new LinkedList<>();
                profilerResults.put(task, resultPerTask);
            }
            resultPerTask.add(result);
        }
    }
}
