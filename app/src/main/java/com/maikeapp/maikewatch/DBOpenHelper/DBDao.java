package com.maikeapp.maikewatch.DBOpenHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.graphics.drawable.animated.BuildConfig;
import android.text.format.DateFormat;
import android.util.Log;

import com.maikeapp.maikewatch.bean.OneDayData;
import com.maikeapp.maikewatch.bean.TodayDatas;
import com.maikeapp.maikewatch.bean.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by aaa on 2016/6/13.
 */
public class DBDao {
    private DBHelper helper;

    private ArrayList<TodayDatas> mTodayList = new ArrayList<TodayDatas>();
    private ArrayList<OneDayData> mTodayList2 = new ArrayList<OneDayData>();

    private TodayDatas.HourStemp mHourStemp;
    private int userid;
    private int mDataid;
    private int mCompletedSteps = 0;
    private String the_date_str;
    private Date parse;
    private boolean isHourStep = true;
    private ArrayList<OneDayData> mTodayList7;
    private ArrayList<OneDayData> mTodayList30;
    private boolean isHave = true;
    private int  mCount = -30;

    public DBDao(Context context) {
        helper = new DBHelper(context);
    }

    /**
     * 储存所有从手表获取的数据
     */
    public void addallDayDatas(List<OneDayData> oneDayDatas, String phone, User user) {
//        deleteHourStep0();//删除所有为0的数据
        for (int i = 0; i < oneDayDatas.size(); i++) {
            int completedPercent = oneDayDatas.get(i).getCompletedPercent();
//            int completedSteps = oneDayDatas.get(i).getCompletedSteps();
            int completeHour = oneDayDatas.get(i).getCompleteHour();
            double v = oneDayDatas.get(i).getiKils();
            double kcal = oneDayDatas.get(i).getKcal();
            String loginName = oneDayDatas.get(i).getLoginName();
            int steps = oneDayDatas.get(i).getSteps();
            int targetSteps = user.getSportsTarget();
            String macAddress = oneDayDatas.get(i).getMacAddress();
            String sportsTime = oneDayDatas.get(i).getSportsTime();
            //需要找出datainfo中所有的时间的集合
            userid = findUser(loginName);
            if (userid == 0) {
                addUser(loginName, macAddress, phone);
                userid = findUser(loginName);//获取用户ID
            }
            mDataid = findData(userid, sportsTime);
            if (mDataid != 0) {//这天的数据在数据库里面是存在的,才用updata更新操作
                changeDataInfo(sportsTime, completedPercent, 0, kcal, v, userid, sportsTime);
                UpdataTotalStep(loginName, sportsTime);
            } else {
                addData(sportsTime, completedPercent, targetSteps, steps, kcal, v, userid);//添加当前的目标数
                UpdataTotalStep(loginName, sportsTime);//更新总数
                mDataid = findData(userid, sportsTime);
            }
            deleteHourStep1(mDataid,completeHour);//删除7天的步数数据
            addHourStep(completeHour, steps, mDataid);//添加新的7天的步数
                Date myDate = new Date();
                int thisYear = datePlus(myDate, mCount).getYear() + 1900;//thisYear = 2003
                int thisMonth = datePlus(myDate, mCount).getMonth() + 1;//thisMonth = 5
                int thisDate = datePlus(myDate, mCount).getDate();//thisDate = 30
                String _CurrentTime = String.valueOf(thisYear) + "-" + String.valueOf(thisMonth) + "-" + String.valueOf(thisDate);
                isHave = deleteDatas(userid, _CurrentTime);//返回是否有数据/并且删除数据
        }
        //上传目标步数(以上传的状态为准,以当前目标覆盖标记为0的目标数据,)
        changeDataTarStep(String.valueOf(user.getSportsTarget()), userid);
    }


    /**
     * 根据用户名和日期
     * 查询当天的所有数据
     */
    public ArrayList<TodayDatas> findTodayHourStep(String user, String Date) {
        int userid = findUser(user);
        findData(userid, Date);
        return mTodayList;
    }

    /**
     * 传入用户名和时间,输出数据库每小时的数据,用于界面显示
     *
     * @param user
     * @param Date
     * @return
     */
    public ArrayList<OneDayData> findTodayHourStep2(String user, String Date) {
        int userid = findUser(user);
        ArrayList<OneDayData> TodayList2 = findData2(userid, Date);

        return TodayList2;
    }
    /**
     * 传入用户名和时间,输出数据库一周的运动数据
     *
     * @param user
     * @param Date
     * @return
     */
    public ArrayList<OneDayData> weekDatas(String user) {
        mTodayList7 = new ArrayList<OneDayData>();
        int userid = findUser(user);
        //获取7天的数据集合
        for (int i = 0; i >-7; i--) {
            Log.d("i的数据", "i:" + i);
            Date myDate = new Date();
            int thisYear = datePlus(myDate, i).getYear() + 1900;//thisYear = 2003
            int thisMonth = datePlus(myDate, i).getMonth() + 1;//thisMonth = 5
            int thisDate = datePlus(myDate, i).getDate();//thisDate = 30
            String _CurrentTime = String.valueOf(thisYear) + "-" + String.valueOf(thisMonth) + "-" + String.valueOf(thisDate);
            Log.d("_CurrentTime的数据u", _CurrentTime);
            findDataWeek(userid, _CurrentTime);
        }//7天的数据集合
        Log.d("mTodayList7的数据", "mTodayList7:" + mTodayList7);
        return mTodayList7;
    }
    /**
     * 传入用户名和时间,输出数据库30天的运动数据
     *
     * @param user
     * @param Date
     * @return
     */
    public ArrayList<OneDayData> monthDatas(String user) {
        mTodayList30 = new ArrayList<OneDayData>();
        int userid = findUser(user);
        //获取7天的数据集合
        for (int i = 0; i >-30; i--) {
            Log.d("i的数据", "i:" + i);
            Date myDate = new Date();
            int thisYear = datePlus(myDate, i).getYear() + 1900;//thisYear = 2003
            int thisMonth = datePlus(myDate, i).getMonth() + 1;//thisMonth = 5
            int thisDate = datePlus(myDate, i).getDate();//thisDate = 30
            String _CurrentTime = String.valueOf(thisYear) + "-" + String.valueOf(thisMonth) + "-" + String.valueOf(thisDate);
            Log.d("_CurrentTime的数据u", _CurrentTime);
            findDataMonth(userid, _CurrentTime);
        }//7天的数据集合
        Log.d("mTodayList7的数据", "mTodayList7:" + mTodayList30);
        return mTodayList30;
    }
    /**
     * 更新数据库的总步数
     *
     * @param user
     * @param Date
     * @return
     */
    public void UpdataTotalStep(String user, String Date) {
        int userid = findUser(user);
        ArrayList<OneDayData> TodayList2 = findData2(userid, Date);
        int sum_step = 0;
        for (int i = 0; i < TodayList2.size(); i++) {
            sum_step += TodayList2.get(i).getSteps();
        }
        changeDataInfoTotal(sum_step, findUser(user), Date);

//        Log.d("sum_step的数据", "sum_step:" + sum_step);
    }


    /**
     * 传入Userid,和时间删除相应的数据
     */
    public boolean deleteDatas(int userid, String date) {
        int dataid = findData(userid, date);
        deleteData(userid, date);
        deleteHourStep(dataid);
        return dataid != 0;
    }


    /**
     * 传入Userid,和时间删除相应的数据
     */
    public boolean deleteHourStemp(int userid, String date) {
        int dataid = findData(userid, date);
        deleteData(userid, date);
        deleteHourStep(dataid);
        return dataid != 0;
    }

    /**
     * 时间计算公式
     *
     * @param base 基础日期
     * @param days 增加天数(减天数则用负数)
     */
    public Date datePlus(Date base, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(base);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    /**
     * 将日期转换成指定格式的字符串。
     *
     * @param date   日期
     * @param format 输出格式
     * @return 日期字符串
     */
    public String convDate2Str(Date date, String format) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat _sdf = new SimpleDateFormat(format);
        String _TodayStr = _sdf.format(date);
        return _TodayStr;
    }

    /**
     * 查找一个用户返回一个id
     *
     * @return
     */
    public int findUser(String user) {
        int _id = 0;
        // 获取到可读的数据库
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("userinfo", new String[]{"_id", "user"}, "user=?", new String[]{user}, null, null, null);
        while (cursor.moveToNext()) {
            _id = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return _id;
    }

    public ArrayList<String> findDataInfoTime() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("datainfo", new String[]{"_id", "date"}, null, null, null, null, null);
        ArrayList<String> times = new ArrayList<String>();
        while (cursor.moveToNext()) {
            String date = cursor.getString(1);
            times.add(date);
        }
        cursor.close();
        db.close();
        return times;
    }

    /**
     * 更新datainfo数据库
     */
    public boolean changeDataInfo(String date, int percent, int totalstep, double calorie, double mile, int userid, String Date) {

//        mCompletedSteps = totalstep + mCompletedSteps;//累加数据算出总步数

        // 获取到可写的数据库
        SQLiteDatabase db = helper.getWritableDatabase();//将每天的步数累加并更新数据库
        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("percent", percent);
//        values.put("targetstep", targetstep);
        values.put("totalstep", totalstep);
        values.put("calorie", calorie);
        values.put("mile", mile);

        int rownumber = db.update("datainfo", values, "date=? And userid=?", new String[]{Date, String.valueOf(userid)});
        if (rownumber == 0) {
            return false;
        } else {
            return true;
        }



    }


    /**
     * 更新datainfo数据库的步数总数
     */
    public boolean changeDataInfoTotal(int totalstep, int userid, String Date) {
        // 获取到可写的数据库
        SQLiteDatabase db = helper.getWritableDatabase();//将每天的步数累加并更新数据库
        ContentValues values = new ContentValues();
        values.put("totalstep", totalstep);
        int rownumber = db.update("datainfo", values, "date=? And userid=?", new String[]{Date, String.valueOf(userid)});
        return rownumber != 0;
    }


    /**
     * 更新datainfo数据库
     */
    public boolean changeDataTarStep(String targetstep, int userid) {
//        mCompletedSteps = totalstep + mCompletedSteps;//累加数据算出总步数
        // 获取到可写的数据库
        SQLiteDatabase db = helper.getWritableDatabase();//将每天的步数累加并更新数据库
        ContentValues values = new ContentValues();
        values.put("targetstep", targetstep);
        int rownumber = db.update("datainfo", values, "status=? And userid=?", new String[]{String.valueOf(0), String.valueOf(userid)});
        if (rownumber == 0) {
            return false;
        } else {
            return true;
        }
    }


    /**
     * 传入时间更新status
     */
    public void changeDataInfoStatus(String date, int userid) {
        // 获取到可写的数据库
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", 1);
        db.update("datainfo", values, "date=? And userid=?", new String[]{date, String.valueOf(userid)});
    }

    /**
     * 添加一个用户
     *
     * @return
     */
    public boolean addUser(String user, String mac, String phone) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user", user);
        values.put("mac", mac);
        values.put("phone", phone);

        long rowid = db.insert("userinfo", null, values);
        if (rowid == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * datainfo 表的增加数据
     */
    public boolean addData(String date, int percent, int targetstep, int totalstep, double calorie, double mile, int userid) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("percent", percent);
        values.put("targetstep", targetstep);
        values.put("totalstep", totalstep);
        values.put("calorie", calorie);
        values.put("mile", mile);
        values.put("userid", userid);
        values.put("status", 0);
        long rowid = db.insert("datainfo", null, values);
        if (rowid == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * datainfo表的查询数据
     * 返回一个外键id用于检索hourstepInfo数据表
     */

    public int findData(int userid, String Date) {
        int _dataid = 0;
        // 获取到可读的数据库
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("datainfo", new String[]{"_id"}, "userid=? AND date=?", new String[]{userid + "", Date}, null, null, null);
        while (cursor.moveToNext()) {
            _dataid = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return _dataid;
    }


    /**
     * datainfo表的查询数据
     * 返回一个外键id用于检索hourstepInfo数据表
     */

    public ArrayList<OneDayData> findData2(int userid, String Date) {
        ArrayList<OneDayData> _todayList2 = new ArrayList<OneDayData>();

        int _dataid = 0;
        // 获取到可读的数据库
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("datainfo", new String[]{"_id", "date", "percent", "targetstep", "totalstep", "calorie", "mile"}, "userid=? AND date=?", new String[]{userid + "", Date}, null, null, null);

        while (cursor.moveToNext()) {
            _dataid = cursor.getInt(0);
            String date = cursor.getString(1);
            int targetstep = cursor.getInt(3);

            SQLiteDatabase dbH = helper.getReadableDatabase();
            Cursor cursorH = dbH.query("hourstepinfo", new String[]{"_id", "hour", "step"}, "dataid=?", new String[]{_dataid + ""}, null, null, null);
            while (cursorH.moveToNext()) {
                OneDayData _oneDayData = new OneDayData();
                int hour = cursorH.getInt(1);
                int step = cursorH.getInt(2);
                _oneDayData.setSportsTime(date);//日期
                _oneDayData.setTargetSteps(targetstep);//目标
                _oneDayData.setCompleteHour(hour);//完成的小时
                _oneDayData.setSteps(step);//每小时的步数
                _oneDayData.setType("step");
                _todayList2.add(_oneDayData);    //将整个对象添加进数组
            }
            cursorH.close();
            dbH.close();
        }
        cursor.close();
        db.close();
        return _todayList2;
    }

    /**
     * datainfo表的查询一周的数据
     */

    public ArrayList<OneDayData> findDataWeek(int userid, String Date) {

        int _dataid = 0;
        // 获取到可读的数据库
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("datainfo", new String[]{"_id", "date", "percent", "targetstep", "totalstep", "calorie", "mile"}, "userid=? AND date=?", new String[]{userid + "", Date}, null, null, null);

        while (cursor.moveToNext()) {
            OneDayData _oneDayData = new OneDayData();
            String date = cursor.getString(1);
            int totalstep = cursor.getInt(4);

            //日期的格式化
            SimpleDateFormat _SDF = new SimpleDateFormat("yyyy-MM-dd");
            try {
                java.util.Date parseDate = _SDF.parse(Date);

                int thisYear = datePlus(parseDate,0).getYear() + 1900;//thisYear = 2003
                int thisMonth = datePlus(parseDate,0).getMonth() + 1;//thisMonth = 5
                int thisDate = datePlus(parseDate, 0).getDate();//thisDate = 30
                String _CurrentTime = String.valueOf(thisYear) + "/" + String.valueOf(thisMonth) + "/" + String.valueOf(thisDate);
                _oneDayData.setSportsTime(_CurrentTime);//时间
                _oneDayData.setCompletedSteps(totalstep);//总步数

            } catch (ParseException e) {
                e.printStackTrace();
            }
            mTodayList7.add(_oneDayData);    //将整个对象添加进数组
        }
        cursor.close();
        db.close();
        return mTodayList7;
    }


    /**
     * datainfo表的查询一周的数据
     */

    public ArrayList<OneDayData> findDataMonth(int userid, String Date) {

        int _dataid = 0;
        // 获取到可读的数据库
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("datainfo", new String[]{"_id", "date", "percent", "targetstep", "totalstep", "calorie", "mile"}, "userid=? AND date=?", new String[]{userid + "", Date}, null, null, null);

        while (cursor.moveToNext()) {
            OneDayData _oneDayData = new OneDayData();
            String date = cursor.getString(1);
            int totalstep = cursor.getInt(4);

            //日期的格式化
            SimpleDateFormat _SDF = new SimpleDateFormat("yyyy-MM-dd");
            try {
                java.util.Date parseDate = _SDF.parse(Date);

                int thisYear = datePlus(parseDate,0).getYear() + 1900;//thisYear = 2003
                int thisMonth = datePlus(parseDate,0).getMonth() + 1;//thisMonth = 5
                int thisDate = datePlus(parseDate, 0).getDate();//thisDate = 30
                String _CurrentTime = String.valueOf(thisYear) + "/" + String.valueOf(thisMonth) + "/" + String.valueOf(thisDate);
                _oneDayData.setSportsTime(_CurrentTime);//时间
                _oneDayData.setCompletedSteps(totalstep);//总步数

            } catch (ParseException e) {
                e.printStackTrace();
            }
            mTodayList30.add(_oneDayData);    //将整个对象添加进数组
        }
        cursor.close();
        db.close();
        return mTodayList30;
    }



    /**
     * 删除用户的某天数据
     */
    public boolean deleteData(int userid, String Date) {

        int _dataid = 0;
        // 获取到可读的数据库
        SQLiteDatabase db = helper.getWritableDatabase();
        int rownumber = db.delete("datainfo", "userid=? AND date<=?", new String[]{userid + "", Date});
        return rownumber != 0;
    }

    /**
     * 删除dataid的相应一天的数据
     *
     * @return
     */
    public boolean deleteHourStep(int dataid) {
        int _id = 0;
        // 获取到可读的数据库
        SQLiteDatabase db = helper.getWritableDatabase();
        int cursor = db.delete("hourstepinfo", "dataid=? ", new String[]{String.valueOf(dataid)});
        return cursor != 0;
    }


    /**
     * 删除dataid的相应一小时的数据
     *
     * @return
     */
    public boolean deleteHourStep1(int dataid,int hour) {
        int _id = 0;
        // 获取到可读的数据库
        SQLiteDatabase db = helper.getWritableDatabase();
        int cursor = db.delete("hourstepinfo", "dataid=? And hour=?", new String[]{String.valueOf(dataid),String.valueOf(hour)});
        return cursor != 0;
    }


    /**
     * 删除dataid的相应一天的数据
     *
     * @return
     */
    public boolean deleteHourStep0() {
        int _id = 0;
        // 获取到可读的数据库
        SQLiteDatabase db = helper.getWritableDatabase();
        int cursor = db.delete("hourstepinfo", "hour=?", new String[]{String.valueOf(0)});
        return cursor != 0;
    }


    /**
     * hourstepinfo表的增加
     */
    public boolean addHourStep(int hour, int step, int dataid) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("hour", hour);
        values.put("step", step);
        values.put("dataid", dataid);

        long rowid = db.insert("hourstepinfo", null, values);
        if (rowid == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 更新datainfo数据库
     */
    public boolean upDateHourStep(int hour, int step, int dataid) {

//        mCompletedSteps = totalstep + mCompletedSteps;//累加数据算出总步数

        // 获取到可写的数据库
        SQLiteDatabase db = helper.getWritableDatabase();//将每天的步数累加并更新数据库
        ContentValues values = new ContentValues();
        values.put("step", step);

        int rownumber = db.update("hourstepinfo", values, "hour=? And dataid=?", new String[]{String.valueOf(hour), String.valueOf(dataid)});
        if (rownumber == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 更新上传状态信息
     */
    public boolean updataStatus(User user, String date) {
        int userid = findUser(user.getLoginName());
        // 获取到可写的数据库
        SQLiteDatabase db = helper.getWritableDatabase();//将每天的步数累加并更新数据库
        ContentValues values = new ContentValues();
        values.put("status", 1);
        int rownumber = db.update("datainfo", values, "userid=? And date=?", new String[]{String.valueOf(userid), date});

        return rownumber != 0;
    }


    /**
     * 查找一个用户返回一个id
     *
     * @return
     */
    public boolean findHourStep(int hour, int dataid) {
        int id = 0;
        // 获取到可读的数据库
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("hourstepinfo", new String[]{"hour", "dataid"}, "hour=? And dataid=?", new String[]{hour + "", String.valueOf(dataid)}, null, null, null);
        while (cursor.moveToNext()) {
            id = cursor.getInt(0);
//            Log.d("_id的数据", "dataid:" + id);
        }

        cursor.close();
        db.close();
        return id != 0;
    }

    /**
     * 查找一个用户返回一个id
     *
     * @return
     */
    public boolean findHourStep0(int hour, int dataid) {
        int _id = 0;
        // 获取到可读的数据库
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query("hourstepinfo", new String[]{"hour", "dataid"}, "hour=? And dataid=?", new String[]{"0", "2"}, null, null, null);

        while (cursor.moveToNext()) {
            _id = cursor.getInt(0);
//            Log.d("_id的数据", "_id:" + _id);
        }

        cursor.close();
        db.close();
        if (_id == 0) {
            return false;
        } else {
            return true;
        }
    }


}
