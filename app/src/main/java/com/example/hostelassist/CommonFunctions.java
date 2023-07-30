package com.example.hostelassist;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CommonFunctions {
    public static boolean navigationItemSelect(MenuItem item, final Activity activity) {
        int id = item.getItemId();


        switch (id) {

            case R.id.nav_home: {
                activity.startActivity(new Intent().setClass(activity, HomeActivity.class));
                break;

            }

            case R.id.nav_mess: {
                activity.startActivity(new Intent().setClass(activity, MessActivity.class));
                break;

            }

            case R.id.nav_complaint: {
                activity.startActivity(new Intent().setClass(activity, ComplaintsActivity.class));
                break;

            }

            case R.id.nav_profile: {
                activity.startActivity(new Intent().setClass(activity, ProfileActivity.class));
                break;

            }
            case R.id.nav_pass: {
                activity.startActivity(new Intent().setClass(activity, UpdatePassword.class));
                break;

            }
            case R.id.nav_logout: {
                PreferenceManager preferenceManager=new PreferenceManager(activity.getApplicationContext());
                preferenceManager.setIsLoggedIn(false);
                preferenceManager.setLoginCredentials("email","password");

                FirebaseAuth loginAuth=FirebaseAuth.getInstance();
                loginAuth.signOut();
                Toast.makeText(activity.getApplicationContext(), "User successfully logged out.", Toast.LENGTH_SHORT).show();
                activity.startActivity(new Intent().setClass(activity, LoginActivity.class));


            }
        }

        DrawerLayout drawer = activity.findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    public static void setUser(Activity activity){
        FirebaseApp.initializeApp(activity);
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();

        if (firebaseUser!=null){
            NavigationView navigationView = activity.findViewById(R.id.nav_view);
            LinearLayout drawerHeader = (LinearLayout) navigationView.getHeaderView(0);

            TextView emailTextView = drawerHeader.findViewById(R.id.nav_header_email);
            TextView usernameTextView = drawerHeader.findViewById(R.id.nav_header_username);
            ImageView userImage=drawerHeader.findViewById(R.id.nav_header_imageView);

            emailTextView.setText(firebaseUser.getEmail());
            usernameTextView.setText(firebaseUser.getDisplayName());

            Uri photoUri = firebaseUser.getPhotoUrl();
            if(photoUri == null){
                String userName = firebaseUser.getDisplayName();
                char ch;
                if(userName != null) {
                    ch = userName.charAt(0);
                    TextDrawable drawable = TextDrawable.builder()
                            .buildRound(String.valueOf(ch), Color.BLUE);
                    Bitmap bitmap = drawableToBitmap(drawable);
                    Glide.with(activity.getApplicationContext())
                            .load(bitmap)
                            .into(userImage);
                }
            }
            else{
                Glide.with(activity.getApplicationContext())
                        .load(photoUri)
                        .into(userImage);
            }
        }
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 96;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 96;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
