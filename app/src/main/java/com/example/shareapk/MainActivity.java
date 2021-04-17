package com.example.shareapk;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
	List<App> apps = new ArrayList<>();
	RecyclerView recyclerView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		recyclerView = findViewById(R.id.app_list);

		//Get App List
		PackageManager pm  = getApplicationContext().getPackageManager();
		List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

		for(ApplicationInfo packageInfo: packages){

			String name;

			if((name = String.valueOf(pm.getApplicationLabel(packageInfo))).isEmpty()){
				name = packageInfo.packageName;
			}

			Drawable icon  = pm.getApplicationIcon(packageInfo);
			String apkPath = packageInfo.sourceDir;
			long apkSize = new File(packageInfo.sourceDir).length();


			apps.add(new App(name,icon,apkPath,apkSize));


		}

		Collections.sort(apps, new Comparator<App>() {
				  @Override
				  public int compare(App app, App t1) {
					  return app.getName().toLowerCase().compareTo(t1.getName().toLowerCase());
				  }
			  }
		);


		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(linearLayoutManager);
		recyclerView.setHasFixedSize(true);

		AppAdapter appAdapter = new AppAdapter(this,apps);
		recyclerView.setAdapter(appAdapter);

	}

	@Override
	public void onBackPressed()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

		builder.setMessage("Please take a moment to rate our app");
		builder.setTitle("Rate Us!");
		builder.setCancelable(false);

		builder.setPositiveButton("Yes, Of Course",
			  new DialogInterface.OnClickListener() {
				  @Override
				  public void onClick(DialogInterface dialogInterface, int i) {
					  LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					  View layout = inflater.inflate(R.layout.popupwindow, null);
					  PopupWindow window = new PopupWindow(layout, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);

					  window.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
					  window.setOutsideTouchable(true);
					  window.showAtLocation(layout, Gravity.CENTER, 0, 0);

					  RatingBar rating = layout.findViewById(R.id.ratingBar);
					  Button Rate = layout.findViewById(R.id.RateButton);

					  Rate.setOnClickListener(new View.OnClickListener() {

						  @Override
						  public void onClick(View v) {
						  	if(rating.getRating() > 0)
						    {
						    	window.dismiss();
						    	finishAffinity();
						    	System.exit(0);
						    }
						  }
					  });
				  }
			  });

		builder.setNegativeButton("No, Thanks",
			  new DialogInterface.OnClickListener() {
				  @Override
				  public void onClick(DialogInterface dialogInterface, int i) {
					  finishAffinity();
					  System.exit(0);
				  }
			  });

		builder.create();
		builder.show();
	}
}
