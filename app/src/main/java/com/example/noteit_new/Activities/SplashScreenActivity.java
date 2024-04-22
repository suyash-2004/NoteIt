package com.example.noteit_new.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.example.noteit_new.MainActivity;
import com.example.noteit_new.R;

/* loaded from: classes.dex */
public class SplashScreenActivity extends AppCompatActivity {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(new Runnable() { // from class: com.example.noteit.Activities.SplashScreenActivity$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                SplashScreenActivity.this.m102x437aac41();
            }
        }, 2000L);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$onCreate$0$com-example-noteit-Activities-SplashScreenActivity  reason: not valid java name */
    public /* synthetic */ void m102x437aac41() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
