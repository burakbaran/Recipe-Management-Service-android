package com.recipemanagement.recipemanagement;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;


public class FcmTokenRegistrationService extends IntentService {

    public FcmTokenRegistrationService() {
        super("FcmTokenRegistrationService");
    }
    static String token = "";
    @Override
    protected void onHandleIntent(Intent intent) {

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        // Get new Instance ID token

                        token = task.getResult().getToken();

                    }
                });
    }

    public String getString(){
        return token;
    }
}