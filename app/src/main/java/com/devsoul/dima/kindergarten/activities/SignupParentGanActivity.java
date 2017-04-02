package com.devsoul.dima.kindergarten.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;

import com.devsoul.dima.kindergarten.R;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

/**
 * The Signup KinderGan Activity enables the user that is a parent to create an account in the application.
 */

public class SignupParentGanActivity extends Activity {


    String [] GAN_LIST={"Stars Gan","Flowers Gan","Rainbow Gan"};
    String [] CLASS_LIST={"1","2","3","4"};

    private ImageButton btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_parent_gan);

        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,GAN_LIST);
        MaterialBetterSpinner betterSpinner=(MaterialBetterSpinner)findViewById(R.id.android_material_design_spinner);
        betterSpinner.setAdapter(arrayAdapter);

        ArrayAdapter<String> arrayAdapter1=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,CLASS_LIST);
        MaterialBetterSpinner betterSpinner1=(MaterialBetterSpinner)findViewById(R.id.android_material_design_spinner2);
        betterSpinner1.setAdapter(arrayAdapter1);



        btnBack = (ImageButton) findViewById(R.id.img_btn_back);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nexxt = new Intent(SignupParentGanActivity.this, SignupParentActivity.class);
                startActivity(nexxt);
            }
        });

    }

}
