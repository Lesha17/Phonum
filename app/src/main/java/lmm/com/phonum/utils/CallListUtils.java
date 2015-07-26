package lmm.com.phonum.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.CallLog;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import lmm.com.phonum.NumberListAdapter;
import lmm.com.phonum.R;

/**
 * Created by HP on 22.07.2015.
 */

/**
 * Послание в далёкое будущее:
 * Можно применить реализацию БД в памяти
 * Если её вес грозит превысить 100 мегабайт (это 150 тысяч звонков)
 * Можно делать запросы к БД при каждой смене категории
 * Но зачем...
 */
public class CallListUtils {

    private static final String[] M_PROJECTION = {CallLog.Calls.CACHED_NORMALIZED_NUMBER, CallLog.Calls.CACHED_FORMATTED_NUMBER, CallLog.Calls.DATE,
            CallLog.Calls.DURATION, CallLog.Calls.CACHED_NAME, CallLog.Calls.TYPE};

    public static class Number implements Serializable{
        public String name;
        public boolean hasName;
        public String number;
        public String formatted_number;
        public String category;
        public String hint;
        public boolean assigned;
        public long last;

        public static class Call{
            public static final String DATE = "date";
            public static final String DURATION = "duration";
            public static final String TYPE = "type";

            public long date;
            public int duration;
            public int type;
        }

        public List<Call> calls;

        public Number(){
            this.calls = new ArrayList<>();
        }

        public Number(String number){
            this();
            this.number = number;
        }
    }

    public static int getDrawableIdFromType(int type){
        switch (type){
            case CallLog.Calls.INCOMING_TYPE:
                return R.drawable.incoming;
            case CallLog.Calls.OUTGOING_TYPE:
                return R.drawable.outgoing;
            case CallLog.Calls.MISSED_TYPE:
                return R.drawable.missed;
            default:
                return -1;
        }
    }

    public static List<Number> refreshNumbers(Context context){
        List<Number> numbersFromSavedDatabase = getAllNumberFromSavedDatabase(context);
        List<Number> numbersFromCallLog = getAllNumbersFromCallLog(context);

        int db_size = numbersFromSavedDatabase.size();
        int log_size = numbersFromCallLog.size();

        int db_i = 0;
        for(int l_i = 0; l_i < log_size; ){
            Number log_n = numbersFromCallLog.get(l_i);
            if(db_i  >= db_size){
                numbersFromSavedDatabase.add(log_n);
                l_i++;
                continue;
            }
            Number db_n = numbersFromSavedDatabase.get(db_i);

            if(log_n.number.compareTo(db_n.number) < 0){
                numbersFromSavedDatabase.add(log_n);
            }else if(log_n.number.equals(db_n.number)){
                for(Number.Call log_call:log_n.calls){
                    if(!db_n.calls.contains(log_call)){
                        db_n.calls.add(log_call);
                    }
                }
            }else if(log_n.number.compareTo(db_n.number) > 0){
                db_i++;
                continue;
            }

            l_i++;
        }

        writeNumbersToDatabase(context, numbersFromSavedDatabase);

        checkLast(numbersFromSavedDatabase);
        sortByLast(numbersFromSavedDatabase, 0,  numbersFromSavedDatabase.size() - 1);

        return numbersFromSavedDatabase;
    }

    //Sorted by number
    private static List<Number> getAllNumbersFromCallLog(Context context){
        Cursor recentCalls = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, M_PROJECTION, null, null,
                CallLog.Calls.CACHED_NORMALIZED_NUMBER + " ASC");

        ArrayList<Number> numbers_from_callog = new ArrayList<>();

        if(recentCalls.moveToFirst()){
            Number current = new Number(recentCalls.getString(recentCalls.getColumnIndex(CallLog.Calls.CACHED_NORMALIZED_NUMBER)));
            numbers_from_callog.add(current);
            current.name = recentCalls.getString(recentCalls.getColumnIndex(CallLog.Calls.CACHED_NAME));
            current.formatted_number = recentCalls.getString(recentCalls.getColumnIndex(CallLog.Calls.CACHED_FORMATTED_NUMBER));
            current.hasName = (current.name != null);
            do{
                String num = recentCalls.getString(recentCalls.getColumnIndex(CallLog.Calls.CACHED_NORMALIZED_NUMBER));

                Number.Call call = new Number.Call();
                call.date = recentCalls.getLong(recentCalls.getColumnIndex(CallLog.Calls.DATE));
                call.duration = recentCalls.getInt(recentCalls.getColumnIndex(CallLog.Calls.DURATION));
                call.type = recentCalls.getInt(recentCalls.getColumnIndex(CallLog.Calls.TYPE));

                if(!current.number.equals(num)){
                    current = new Number(recentCalls.getString(recentCalls.getColumnIndex(CallLog.Calls.CACHED_NORMALIZED_NUMBER)));
                    numbers_from_callog.add(current);
                    current.name = recentCalls.getString(recentCalls.getColumnIndex(CallLog.Calls.CACHED_NAME));
                    current.formatted_number = recentCalls.getString(recentCalls.getColumnIndex(CallLog.Calls.CACHED_FORMATTED_NUMBER));
                    current.hasName = (current.name != null);
                }

                current.calls.add(call);
            } while (recentCalls.moveToNext());
        }
        recentCalls.close();
        return numbers_from_callog;
    }


    //Sorted by number
    private static List<Number> getAllNumberFromSavedDatabase(Context context){
        MySQLHelper helper = new MySQLHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(MySQLHelper.TABLE_NAME, null, null, null, null, null, MySQLHelper.NUMBER_COLUMN + " ASC");

        ArrayList<Number> numbers = new ArrayList<>();

        if(cursor.moveToFirst()){
            do{
                Number number = new Number();
                number.number = cursor.getString(cursor.getColumnIndex(MySQLHelper.NUMBER_COLUMN));
                number.formatted_number = cursor.getString(cursor.getColumnIndex(MySQLHelper.FORMATTED_NUMBER_COLUMN));
                number.name = cursor.getString(cursor.getColumnIndex(MySQLHelper.NAME_COLUMN));
                number.category = cursor.getString(cursor.getColumnIndex(MySQLHelper.CATEGORY_COLUMN));
                number.hint = cursor.getString(cursor.getColumnIndex(MySQLHelper.HINT_COLUMN));
                number.assigned = (cursor.getInt(cursor.getColumnIndex(MySQLHelper.ASSIGNED_COLUMN)) != 0);
                number.last = cursor.getLong(cursor.getColumnIndex(MySQLHelper.LAST_COLUMN));
                number.calls = readJson(cursor.getString(cursor.getColumnIndex(MySQLHelper.CALLS_COLUMN)));
                number.hasName = number.name != null;
                numbers.add(number);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return numbers;
    }

    private static void writeNumbersToDatabase(Context context, List<Number> numbers){
        MySQLHelper helper = new MySQLHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(MySQLHelper.TABLE_NAME, null, null);

        for(Number number : numbers){
            ContentValues values = new ContentValues();
            values.put(MySQLHelper.NUMBER_COLUMN, number.number);
            values.put(MySQLHelper.FORMATTED_NUMBER_COLUMN, number.formatted_number);
            values.put(MySQLHelper.NAME_COLUMN, number.name);
            values.put(MySQLHelper.CATEGORY_COLUMN, number.category);
            values.put(MySQLHelper.HINT_COLUMN, number.hint);
            if(number.assigned) {
                values.put(MySQLHelper.ASSIGNED_COLUMN, 1);
            } else {
                values.put(MySQLHelper.ASSIGNED_COLUMN, 0);
            }
            values.put(MySQLHelper.LAST_COLUMN, number.last);
            values.put(MySQLHelper.CALLS_COLUMN, writeCallsJSON(number));

            db.update(MySQLHelper.TABLE_NAME, values, MySQLHelper.NUMBER_COLUMN + "=" + number.number, null);
        }
        db.close();
    }

    private static String writeCallsJSON(Number number){
        StringWriter stringWriter = new StringWriter();
        JsonWriter writer = new JsonWriter(stringWriter);
        try{
            writer.beginArray();
            for(Number.Call call:number.calls){
                writer.beginObject().name(Number.Call.DATE).value(call.date)
                        .name(Number.Call.DURATION).value(call.duration)
                        .name(Number.Call.TYPE).value(call.type)
                        .endObject();
            }
            writer.endArray();
        } catch (IOException e){

        }
        return stringWriter.toString();
    }

    private static List<Number.Call> readJson(String json){
        ArrayList<Number.Call>  calls = new ArrayList<>();
        JsonReader reader = new JsonReader(new StringReader(json));
        try{
            reader.beginArray();
            while (reader.hasNext()){
                Number.Call call = new Number.Call();

                reader.beginObject();
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    if (name.equals(Number.Call.DATE)) {
                        call.date = reader.nextLong();
                    } else if (name.equals(Number.Call.DURATION)) {
                        call.duration = reader.nextInt();
                    } else if (name.equals(Number.Call.TYPE)) {
                        call.type = reader.nextInt();
                    } else {
                        reader.skipValue();
                    }
                }
                reader.endObject();

                calls.add(call);
            }
            reader.endArray();
        } catch (IOException e){

        }

        return calls;
    }

    private static void checkLast(List<Number> numbers){
        for (Number number:numbers){
            sortCalls(number.calls, 0, number.calls.size() - 1);
            number.last = number.calls.get(0).date;
        }
    }

    private static void sortCalls(List<Number.Call> calls, int l, int r){
        if(calls.size() <= 1){
            return;
        }
        int i = l;
        int j = r;
        long m = calls.get((l+r)/2).date;
        while (i <= j){
            while(calls.get(i).date > m){
                i++;
            }
            while(calls.get(j).date < m){
                j--;
            }
            if(i<=j){
                Number.Call  k = calls.get(i);
                calls.set(i, calls.get(j));
                calls.set(j, k);
                i++;
                j--;
            }
        }
        if(l < j){
            sortCalls(calls, l, j);
        }
        if(i < r){
            sortCalls(calls, i, r);
        }
    }

    private static void sortByLast(List<Number> numbers, int l, int r){
        if(numbers.size() <= 1){
            return;
        }
        int i = l;
        int j = r;
        long m = numbers.get((l+r)/2).last;
        while (i <= j){
            while(numbers.get(i).last > m){
                i++;
            }
            while (numbers.get(j).last < m){
                j--;
            }
            if(i <= j){
                Number k = numbers.get(i);
                numbers.set(i, numbers.get(j));
                numbers.set(j, k);
                i++;
                j--;
            }
        }
        if(l < j){
            sortByLast(numbers, l, j);
        }

        if(i < r){
            sortByLast(numbers, i, r);
        }
    }
}
