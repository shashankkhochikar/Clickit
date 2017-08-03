package com.clickitproduct.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.clickitproduct.activities.Profile_new;
import com.clickitproduct.activities.UserLogin;
import com.clickitproduct.commonutil.common_variable;

public class SmsListener extends BroadcastReceiver
{
    public static final String SMS_BUNDLE = "pdus";

    public void onReceive(Context context, Intent intent)
    {
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null)
        {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";

            for (int i = 0; i < sms.length; ++i)
            {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                String smsBody = smsMessage.getMessageBody().toString();
                String address = smsMessage.getOriginatingAddress();
                smsMessageStr += smsBody;
            }
            String abc[]=smsMessageStr.split(" ");
            if(abc.length>10)
            {
                String con="";
                for(int i=0;i<6;i++)
                {
                    con+=abc[i];
                }

                if(con.trim().equals("Your One time password is :")) {
                    if (common_variable.forgot_password == 1) {
                        try {
                            UserLogin.etOtp.setText("" + abc[6].toString());
                            common_variable.forgot_password = 0;
                        } catch (Exception e) {
                        }
                    } else {
                        try {
                            Profile_new.etOtp.setText("" + abc[6].toString());
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
    }
}
