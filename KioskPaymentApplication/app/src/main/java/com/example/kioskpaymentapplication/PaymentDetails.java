package com.example.kioskpaymentapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentDetails extends AppCompatActivity {

    TextView textId, textAmount, textStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);

        textId = findViewById(R.id.txtId);
        textAmount = findViewById(R.id.txtAmount);
        textStatus = findViewById(R.id.txtStatus);

        Intent intent = getIntent();

        try{
            JSONObject jsonObject = new JSONObject(intent.getStringExtra("PaymentDetails"));
            showDetails(jsonObject.getJSONObject("response"), intent.getIntExtra("PaymentAmount", 0));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showDetails(JSONObject response, int paymentAmount){
        try {
            textId.setText(response.getString("id"));
            textAmount.setText(String.format("€" + paymentAmount));
            textStatus.setText(response.getString("state"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
