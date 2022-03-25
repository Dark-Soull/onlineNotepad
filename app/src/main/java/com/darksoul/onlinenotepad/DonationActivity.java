package com.darksoul.onlinenotepad;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;

import java.math.BigDecimal;

import okhttp3.ResponseBody;

public class DonationActivity extends AppCompatActivity {

    private ImageView backBtn;
    private Button donateBtn;
    private CardView paypalBtn;
    private static final int PAYPAL_REQUEST_CODE = 101;
    private String Currency = "USD";
    private EditText Price;
    private String PRICE;
    public final static String Paypal_Client_Id = "AVnS0d-vueo-eozaAKs1ivNkF5plLZcnweOsI7KCLquWzX3AW_sNlUq1qqLL3-sw4HYuv5w-Fsz4FXfM";
    //paypal config
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(getPaypalStatus())
            .clientId(Paypal_Client_Id);

    private static String getPaypalStatus() {

        return PayPalConfiguration.ENVIRONMENT_SANDBOX;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);

        Price = findViewById(R.id.donationamount);

        // ---------- start paypal service ----------
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

        //back button event handling..........
        backBtn = findViewById(R.id.about_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DonationActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //donate button event handling.............
        donateBtn = findViewById(R.id.donate);
        donateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //payment button display and their events handling............
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View view = layoutInflater.inflate(R.layout.payment_option_buttons, null);
                final PopupWindow popup = new PopupWindow(view, 900, 900, true);
                popup.showAtLocation(v, Gravity.CENTER, 0, 0);

                paypalBtn = view.findViewById(R.id.paypal_btn);
                paypalBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String pricetemp = Price.getText().toString();
                        if (pricetemp.length() == 0) {
                            Toast.makeText(getApplicationContext(), "Enter Donation Amount", Toast.LENGTH_LONG).show();
                        } else {
                            processPaypalPayment();
                        }
                        popup.dismiss();
                    }
                });
            }
        });


    }


    //for paypal payment......................................................
    private void processPaypalPayment() {
        PRICE = Price.getText().toString();
        PayPalPayment payPalPayment = new PayPalPayment((new BigDecimal(String.valueOf(PRICE))),
                Currency,
                "Donation for Online Notepad App",
                PayPalPayment.PAYMENT_INTENT_SALE);

        Log.e("Payment", "currency: " + Currency);
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Payment is successful", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Payment is unsuccessful", Toast.LENGTH_LONG).show();
            }

        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Toast.makeText(this, "Payment is Invalid", Toast.LENGTH_LONG).show();
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DonationActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}