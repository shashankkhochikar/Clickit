package com.clickitproduct.SQLite;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseConnection
{
	public DataBaseConnection(Context context)
	{
		ourContext=context;
	}
	private static final int DATABASE_VERSION=1;

	SQLiteDatabase dbs;
	 
	static String DATABASE_NAME="Clickit";
	private DbHelper ourHelper;
	private final Context ourContext;
	private SQLiteDatabase ourDatabase;
	Cursor corser;

	private static class DbHelper extends SQLiteOpenHelper
	{
		Context context;
		private SQLiteDatabase myDB;
		public DbHelper(Context context)
		{
			super(context,DATABASE_NAME, null,DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
			// TODO Auto-generated method stub
			db.execSQL("CREATE TABLE IF NOT EXISTS UserDetail(id INTEGER PRIMARY KEY AUTOINCREMENT, user_id TEXT, fcm_id TEXT)");
			db.execSQL("CREATE TABLE IF NOT EXISTS UserShop(user_id TEXT, shop INTEGER)");
			db.execSQL("INSERT INTO UserDetail(user_id, fcm_id) VALUES('0', '0')");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			// TODO Auto-generated method stub
		}
	 }

    public DataBaseConnection open()throws SQLException
    {
        ourHelper= new DbHelper(ourContext);
        ourDatabase= ourHelper.getWritableDatabase();
        return this;
    }
	 
    public void close()
    {
        ourHelper.close();
    }

	public void updateFCM_ID(String fcmId)
	{
		try
		{
			ourDatabase.execSQL("UPDATE UserDetail set fcm_id = '"+fcmId+"' WHERE id = 1");
//			ourDatabase.execSQL("insert into UserDetail(user_id, fcm_id) values('0', '"+fcmId+"')" );
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

    public void updateUser_ID(String user_id)
    {
        try
        {
            ourDatabase.execSQL("update UserDetail set user_id = '"+user_id+"' where id = 1" );
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

	public String getFCM_ID()
	{
		String fcmId="";
		try {
			corser = ourDatabase.rawQuery("select fcm_id from UserDetail where id = 1", null);
			if (corser.moveToNext())
            {
                fcmId=corser.getString(0);
			}
            else
            {
                fcmId="";
			}
		}catch (Exception e)
		{
			e.printStackTrace();
		}
		return fcmId;
	}

    public String getUSER_ID()
    {
        String userId="";
        try {
            corser = ourDatabase.rawQuery("select user_id from UserDetail where id = 1", null);
            if (corser.moveToNext())
            {
                userId=corser.getString(0);
            }
            else
            {
                userId="";
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return userId;
    }
}

