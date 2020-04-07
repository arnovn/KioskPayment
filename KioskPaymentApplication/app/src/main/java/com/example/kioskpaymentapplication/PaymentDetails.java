package com.example.kioskpaymentapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Activity in charge of visualizing order confirmation when everything went successfully
 */
public class PaymentDetails extends AppCompatActivity {

    /**
     * TextView visualizing the payment ID, amount & status on the UI layer
     */
    TextView textId, textAmount, textStatus;

    /**
     * Button for the user to sign out on the UI layer
     */
    Button signoutButton;

    /**
     * When the activity is created:
     *  - Initialize objects of the UI layer
     * @param savedInstanceState
     *              Bundle containing the activity's previously saved states
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);

        textId = findViewById(R.id.txtId);
        textAmount = findViewById(R.id.txtAmount);
        textStatus = findViewById(R.id.txtStatus);
        signoutButton = findViewById(R.id.signoutButtonStd);

        setOnClickLstn();

        Intent intent = getIntent();

        try{
            JSONObject jsonObject = new JSONObject(Objects.requireNonNull(intent.getStringExtra("PaymentDetails")));
            showDetails(jsonObject.getJSONObject("response"), intent.getIntExtra("PaymentAmount", 0));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set the TextViews of the UI layer
     * @param response
     *          PayPal response of the payment
     * @param paymentAmount
     *          Amount the has been payed
     */
    @SuppressLint("SetTextI18n")
    private void showDetails(JSONObject response, int paymentAmount){
        try {
            textId.setText(response.getString("id"));
            textAmount.setText("€" + paymentAmount);
            textStatus.setText(response.getString("state"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Button initializer for logout
     *  - CLEAR_TOP: all previous activities are cleared, the main activity is the first
     */
    private void setOnClickLstn(){
        signoutButton.setOnClickListener(v->{
            Intent returnIntent = new Intent(PaymentDetails.this, MainActivity.class);
            returnIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(returnIntent);
        });
    }

    /**
     * It is not possible to return to the previous activity when we're in this activity
     */
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
