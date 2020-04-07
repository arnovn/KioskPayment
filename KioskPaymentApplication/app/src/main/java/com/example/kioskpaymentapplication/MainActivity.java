package com.example.kioskpaymentapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * First activity of the application, in charge of login
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Code entered by the user.
     */
    String enteredCode;

    /**
     * TextView visualizing the first digit of the code with *
     *  - Giving feedback to the user he has inserted the first digit.
     */
    TextView firstEntry;

    /**
     * TextView visualizing the second digit of the code with *
     *  - Giving feedback to the user he has inserted the second digit.
     */
    TextView secondEntry;

    /**
     * TextView visualizing the third digit of the code with *
     *  - Giving feedback to the user he has inserted the third digit.
     */
    TextView thirdEntry;

    /**
     * TextView visualizing the fourth digit of the code with *
     *  - Giving feedback to the user he has inserted the fourth digit.
     */
    TextView fourthEntry;

    /**
     * List containing the four textviews
     */
    List<TextView> entryList;

    /**
     * List containing the input buttons
     */
    List<Button> buttonList;

    /**
     * EditText where the user can input his E-mail address.
     */
    EditText editMailTextStd;

    /**
     * E-mail address of the user
     */
    String mail;

    /**
     * The inputted mail is compared with this pattern to check if it is a legit E-mail address
     */
    String mailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    /**
     * Actual login code
     */
    String code;

    /**
     * Id of the user
     */
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enteredCode = "";
        code ="";
        entryList = new ArrayList<>();
        buttonList = new ArrayList<>();

        editMailTextStd = findViewById(R.id.editEmailText);

        connectTextViews();
        connectButtons();
        setOnClickListeners();
    }

    /**
     * Connect the TextView objects to the TextViews on the UI layer.
     * Add the TextView objects to the entryList.
     */
    public void connectTextViews(){
        firstEntry = findViewById(R.id.codeView1);
        secondEntry = findViewById(R.id.codeView2);
        thirdEntry = findViewById(R.id.codeView3);
        fourthEntry = findViewById(R.id.codeView4);

        entryList.add(firstEntry);
        entryList.add(secondEntry);
        entryList.add(thirdEntry);
        entryList.add(fourthEntry);
    }

    /**
     * Connect the Button objects to the Buttons on the UI layer.
     */
    public void connectButtons(){
        Button deleteButton = findViewById(R.id.buttonDel);
        Button loginButton = findViewById(R.id.loginButtonStd);
        Button button1 = findViewById(R.id.entryButton1);
        Button button2 = findViewById(R.id.entryButton2);
        Button button3 = findViewById(R.id.entryButton3);
        Button button4 = findViewById(R.id.entryButton4);
        Button button5 = findViewById(R.id.entryButton5);
        Button button6 = findViewById(R.id.entryButton6);
        Button button7 = findViewById(R.id.entryButton7);
        Button button8 = findViewById(R.id.entryButton8);
        Button button9 = findViewById(R.id.entryButton9);
        Button button0 = findViewById(R.id.entryButton0);

        buttonList.add(button0);
        buttonList.add(button1);
        buttonList.add(button2);
        buttonList.add(button3);
        buttonList.add(button4);
        buttonList.add(button5);
        buttonList.add(button6);
        buttonList.add(button7);
        buttonList.add(button8);
        buttonList.add(button9);
        buttonList.add(deleteButton);
        buttonList.add(loginButton);
    }

    /**
     * Set the OnClickListeners of the button objects of the UI layer
     */
    public void setOnClickListeners(){
        for(Button button: buttonList){
            if(button.getText().toString().equals("DEL")){
                button.setOnClickListener(v->deleteEntry());
            }else if(button.getText().toString().equals("LOGIN")){
                button.setOnClickListener(v->checkInput());
            }else{
                button.setOnClickListener(v->{
                    String entry = button.getText().toString();
                    addEntry(entry);
                });
            }
        }
    }

    /**
     * When one of the input buttons (0-9) has been pressed we add a * to one of the TextViews for feedback to the user
     * @param entry
     *              the code up to now.
     */
    public void addEntry(String entry){
        if(enteredCode.length()<4){
            enteredCode = enteredCode+entry;
            entryList.get(enteredCode.length()-1).setText("*");
        }
    }

    /**
     *  When the DeleteButton is pressed we remove one of the * of one of the TextViews for feedback to the user
     */
    public  void deleteEntry(){
        if(enteredCode.length()>0){
            enteredCode = enteredCode.substring(0, enteredCode.length()-1);
            entryList.get(enteredCode.length()).setText("");
        }
    }

    /**
     * Check the user mail input is inside the MySql Database.
     */
    public void checkInput() {
        //check if mail valid
        if(checkMailEdit()){
            //First check if mail address in database & get code assigned with the mail address
            Toast.makeText(getApplicationContext(),"Correct mail.",Toast.LENGTH_LONG).show();
            mail = editMailTextStd.getText().toString();
            new ConnectionGetUserCode().execute();
            //Check if code assigned to mail address is same as code inputted at kiosk
        }else{
            //User needs to input valid mail address.
            Toast.makeText(getApplicationContext(),"Input valid mail.",Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Check if inputted mail by the user is of the correct format.
     * @return
     *          true or false
     */
    public boolean checkMailEdit(){
        return editMailTextStd.getText().toString().trim().matches(mailPattern);
    }

    /**
     * Check if the inputted code is the same as the code from the MySql Database assigned to the mail.
     * @return
     *          true or false.
     */
    public boolean checkCodesMatch() throws NoSuchAlgorithmException {
        HashingObject hashingObject = new HashingObject(enteredCode, code);

        return code.equals(hashingObject.getGeneratedHash());
    }

    /**
     * Method in charge of creating the payment intent and starting the new activity
     */
    private void toNextWindow(){
        Intent intent = new Intent(MainActivity.this, PaymentActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("mail", mail);
        startActivity(intent);
    }

    /**
     * Class in charge retrieving the login code of the user.
     */
    @SuppressLint("StaticFieldLeak")
    class ConnectionGetUserCode extends AsyncTask<String, String, String> {
        String result = "";

        /**
         * Method in charge of querying the database through an HTTP request.
         * @param strings
         *          Paramaters passed when the execution of the AsyncTask is called;
         * @return
         *          Returns the response of the database.
         */
        @Override
        protected String doInBackground(String... strings) {
            try{
                String host = "http://"+ getResources().getString(R.string.ipphone) +"/getusercode.php?mail='"+ mail +"'";
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(host));
                HttpResponse response = client.execute(request);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                StringBuilder stringBuffer = new StringBuilder();

                String line;
                while((line = reader.readLine()) != null){
                    stringBuffer.append(line);
                }
                reader.close();
                result = stringBuffer.toString();
            } catch (Exception e) {
                System.out.println("The exception: "+e.getMessage());
                return "The exception: " + e.getMessage();
            }
            return result;
        }

        /**
         * Method in charge of handling the result gathered from the database:
         *  - We check if the retrieved code matches the inputted code
         *  - IF successful: we go to the next window
         *  - ELSE: login failed
         * @param s
         *          Parameters passed when the AsyncTask has finished.
         */
        @Override
        protected void onPostExecute(String s) {
            try{
                JSONObject jsonResult = new JSONObject(result);
                int success = jsonResult.getInt("success");
                if(success == 1){
                    JSONArray codeinfos = jsonResult.getJSONArray("code");
                    JSONObject codeinfo = codeinfos.getJSONObject(0);

                    code = codeinfo.getString("code");
                    id = codeinfo.getInt("id");

                    //Check if codes match
                    if(checkCodesMatch()){
                        //Successfull!
                        Toast.makeText(getApplicationContext(), "Logged in succesfully.", Toast.LENGTH_LONG).show();
                        toNextWindow();

                    }else{
                        //Unsuccesfull.
                        Toast.makeText(getApplicationContext(), "Failed: codes don't match", Toast.LENGTH_LONG).show();
                    }

                }else if(success == 0){
                    Toast.makeText(getApplicationContext(),"Failed: No user coupled to given mail.",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
