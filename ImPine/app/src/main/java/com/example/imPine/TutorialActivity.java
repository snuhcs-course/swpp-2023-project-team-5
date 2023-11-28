package com.example.imPine;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TutorialActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_page);

        TextView textViewTutorialContent = findViewById(R.id.textViewTutorialContent);
        String htmlText = "<div style='text-align:center;'><b style='font-size:20px;'>Welcome to I'm Pine Thank You!</b></div><br>"
                + "<b>1. Choosing a Plant:</b> Start with a healthy, mature pineapple. You can use a store-bought pineapple or buy a pineapple seedling online.<br><br>"
                + "<b>2. Preparing the Crown:</b> Cut off the top of the pineapple, including some of the fruit. Remove the lower leaves to expose the stem.<br><br>"
                + "<b>3. Drying the Crown:</b> Let the crown dry for a few days to allow the cut end to heal. This prevents rotting.<br><br>"
                + "<b>4. Planting:</b> Plant the crown in a well-draining potting mix. Ensure the soil is moist but not waterlogged.<br><br>"
                + "<b>5. Location:</b> Place your pineapple plant in a warm, sunny spot. Pineapples love warmth and sunlight.<br><br>"
                + "<b>6. Watering:</b> Water your plant regularly, allowing the soil to dry out slightly between waterings.<br><br>"
                + "<b>7. Fertilizing:</b> Feed your pineapple plant with a balanced fertilizer every few months.<br><br>"
                + "<b>8. Patience:</b> It can take up to 2-3 years for a pineapple plant to produce fruit. Be patient and enjoy the process!<br><br>"
                + "<b>9. Harvesting:</b> When the pineapple is big, golden, and gives off a sweet aroma, it's ready to harvest.<br><br>"
                + "<b>Watch Out for FCR:</b> Be aware of Fusarium Crown Rot (FCR), a common disease in pineapples. It can cause the fruit's core to rot, affecting the health and yield of your plant. Our app includes a prediction model that assesses the likelihood of your pineapple getting FCR, helping you take preventative measures. Stay informed and proactive for a healthy pineapple harvest!<br><br>"
                + "Remember, growing a pineapple can be a fun and rewarding experience. Enjoy your journey with your friends!";


        textViewTutorialContent.setText(Html.fromHtml(htmlText));

        Button getStartedButton = findViewById(R.id.btn_get_started);
        getStartedButton.setOnClickListener(v -> navigateToMakePlantActivity());
    }

    private void navigateToMakePlantActivity() {
        Intent intent = new Intent(TutorialActivity.this, MakePlantActivity.class);
        startActivity(intent);
        finish();
    }
}
