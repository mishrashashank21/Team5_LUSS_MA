package com.example.team5_luss;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import Model.CustomAdjustmentVoucher;

public class AdjustVoucherCreate extends AppCompatActivity {

    int itemId ;
    int userId = 1;
    String url ="https://10.0.2.2:44312/ItemPrice/mobile/getItemDetails/";
    String url_submit = "https://10.0.2.2:44312/AdjustmentVoucher/addAdjustment/";
    String responseString;
    CustomAdjustmentVoucher voucher;

    Button btnConfirm;
    RadioGroup adjustType;
    RadioButton add;
    RadioButton deduct;
    TextView itemCode;
    TextView description;
    TextView category;
    TextView uom;
    TextView unitPrice;
    TextView totalValue;
    EditText quantity;
    EditText reason;
    String type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_adj_voucher);

        //get userID from session
        //final SharedPreferences pref = getSharedPreferences("user_credentials",MODE_PRIVATE);
        //userId = pref.getString("userID",0);

        Intent intent = getIntent();
        itemId = intent.getIntExtra("itemId",0);
        loadAdjustmentCreate(itemId);

        reason = findViewById(R.id.reason);
        quantity = findViewById(R.id.adjustQty);
        quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(quantity.getText().toString().isEmpty()){
                    return;
                }
                totalValue.setText("" + voucher.getItemPrice() * Integer.parseInt(quantity.getText().toString()));
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(quantity.getText().toString().isEmpty()){
                    return;
                }
                totalValue.setText("" + voucher.getItemPrice() * Integer.parseInt(quantity.getText().toString()));

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(quantity.getText().toString().isEmpty()){
                    return;
                }
                totalValue.setText("" + voucher.getItemPrice() * Integer.parseInt(quantity.getText().toString()));
            }
        });

        adjustType = findViewById(R.id.adjustType);
        if(adjustType!=null){
            adjustType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    switch (i){
                        case R.id.btnAdd:
                            type = "Add";
                            break;
                        case R.id.btnDeduct:
                            type = "Deduct";
                            break;
                    }
                }
            });
        }

        btnConfirm = findViewById(R.id.confirmBtn);
        if(btnConfirm!=null){
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (adjustType.getCheckedRadioButtonId() == -1)
                    {
                        CodeSetting.warningToastMsg(AdjustVoucherCreate.this,"Adjust Type must be specified");
                    }
                    else if(quantity.getText().toString().isEmpty()||quantity.getText().toString().equalsIgnoreCase("0")){
                        CodeSetting.warningToastMsg(AdjustVoucherCreate.this,"Adjust Quantity must be specified");
                    }
                    else
                    submitAdjustmentVoucher(type, itemId, Integer.parseInt(quantity.getText().toString()),reason.getText().toString(), userId);
                }
            });
        }

        itemCode = findViewById(R.id.itemCode);
        description = findViewById(R.id.description);
        category = findViewById(R.id.category);
        uom = findViewById(R.id.uom);
        unitPrice = findViewById(R.id.price);
        totalValue = findViewById(R.id.value);
    }

    private void loadAdjustmentCreate(final int itemId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String target = url + itemId;
                    trustManager.trustAllCertificates();
                    URL url = new URL(target);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setRequestMethod("GET");
                    conn.connect();
                    InputStream in = conn.getInputStream();
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
                    StringBuffer response = new StringBuffer();
                    int data=bufferedInputStream.read();
                    while (data!=-1){
                        char current = (char) data;
                        response.append(current);
                        data=bufferedInputStream.read();
                    }
                    responseString = response.toString();
                    Gson gson = new Gson();
                    voucher = gson.fromJson(responseString, CustomAdjustmentVoucher.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            itemCode.setText(voucher.getItemCode());
                            category.setText(voucher.getCategoryName());
                            description.setText(voucher.getItemName());
                            uom.setText(voucher.getUOM());
                            unitPrice.setText("" + voucher.getItemPrice());
                            totalValue.setText("" + voucher.getItemPrice() * Integer.parseInt(quantity.getText().toString()));

                        }
                    });
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void submitAdjustmentVoucher(final String type, final int itemId, final int adjustQty, final String reason, final int userId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String target = url_submit + type + "/" + itemId + "/" + adjustQty + "/" + reason + "/" + userId;
                    trustManager.trustAllCertificates();
                    URL url = new URL(target);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setRequestMethod("GET");
                    conn.connect();
                    InputStream in = conn.getInputStream();
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
                    StringBuffer response = new StringBuffer();
                    int data=bufferedInputStream.read();
                    while (data!=-1){
                        char current = (char) data;
                        response.append(current);
                        data=bufferedInputStream.read();
                    }
                    responseString = response.toString();

                    Intent intent = new Intent();
                    setResult(RESULT_OK,intent);
                    finish();

                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //MENU: inflate
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        menu.setGroupVisible(R.id.deptRep_menu, false);
        menu.setGroupVisible(R.id.storeclerk_menu, true);
        menu.setGroupVisible(R.id.deptMng_menu, false);
        menu.setGroupVisible(R.id.storeMng_menu, false);
        menu.setGroupVisible(R.id.deptdlgt_menu, false);
        return true;
    }

    //MENU: handle selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        if(item.getItemId() == R.id.logout) {
            final SharedPreferences pref = getSharedPreferences("user_credentials", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.commit();
            finish();
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.store_item) {
            Intent intent = new Intent(this,ItemListing.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.store_home) {
            Intent intent = new Intent(this,DisbursementActivity.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.delivery) {
            Intent intent = new Intent(this,DeliveryListActivity.class);
            startActivity(intent);
        }
        return true;
    }
}

