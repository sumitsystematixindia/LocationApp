package com.mlins.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mlins.instructions.Instruction;
import com.mlins.instructions.InstructionBuilder;
import com.mlins.utils.ResourceTranslator;
import com.mlins.utils.logging.Log;
import com.spreo.spreosdk.R;

public class NavigationInstructionsViewer extends LinearLayout {
    private final static String TAG = "com.com.mlins.views.NavigationInstructionsViewer";

    private Context ctx;
    private ImageView navInsImage;
    private ImageView navInsBckgrnd;
    private TextView navText;
    private TextView navPoiText;
    private Bitmap image;
    private String text;
    private boolean isOpen;
    private ImageButton stopNavigation;

    public NavigationInstructionsViewer(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.getInstance().debug(TAG, "Enter, NavigationInstructionsViewer()");
        ctx = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.nav_ins_viewer, this, true);
        init();
        Log.getInstance().debug(TAG, "Exit, NavigationInstructionsViewer()");
    }

    public void init() {
        Log.getInstance().debug(TAG, "Enter, init()");
        RelativeLayout navinsrl = (RelativeLayout) findViewById(R.id.navigation_ins_RL);
//		android.view.ViewGroup.LayoutParams navinsrlparams = navinsrl.getLayoutParams();
//		navinsrlparams.width = SplashScreen.splashActivity.getLayoutWidth();
//		navinsrl.setLayoutParams(navinsrlparams);

        navInsBckgrnd = (ImageView) findViewById(R.id.navigation_ins_background);
//		android.view.ViewGroup.LayoutParams navInsBckgrndparams = navInsBckgrnd.getLayoutParams();
//		navInsBckgrndparams.width = SplashScreen.splashActivity.getLayoutWidth();
//		navInsBckgrnd.setLayoutParams(navInsBckgrndparams);

        navInsImage = (ImageView) findViewById(R.id.navigation_ins_image);
        navText = (TextView) findViewById(R.id.navigation_ins_textview);
        navPoiText = (TextView) findViewById(R.id.navigation_ins_poi_textview);
        Log.getInstance().debug(TAG, "Exit, init()");
    }

    public void hide() {
        setVisibility(View.INVISIBLE);
        isOpen = false;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void updateBubble() {
        Log.getInstance().debug(TAG, "Enter, updateBubble()");
        Instruction instruction = InstructionBuilder.getInstance().getNextInstruction();
        if (instruction != null) {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), instruction.getImage().get(0));
            Bitmap bmp2 = BitmapFactory.decodeResource(getResources(), R.drawable.navinsimagebackground);
            String text = getResources().getString(instruction.getText().get(0));
            navInsBckgrnd.setImageBitmap(bmp2);
            navInsImage.setImageBitmap(bmp);
            navText.setText(text);
        }
        Log.getInstance().debug(TAG, "Exit, updateBubble()");
    }

    public void updateBubble(double distance) {
        Log.getInstance().debug(TAG, "Enter, updateBubble()");
//		setBackgroundColor(getResources().getColor(color.halfwrite));
        Instruction instruction = InstructionBuilder.getInstance().getNextInstruction();
        if (instruction != null) {

//			navInsBckgrnd.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
////					Intent slideactivity = new Intent(ctx, Itinerary.class);
////					 
////					ctx.startActivity(slideactivity);
//					
//				}
//			});

            Bitmap bmp = BitmapFactory.decodeResource(getResources(), instruction.getImage().get(0));
            String format = getResources().getString(R.string.instruction_distance);
            Bitmap bmp2 = BitmapFactory.decodeResource(getResources(), R.drawable.navinsimagebackground);
            //String text = String.format(format, distance) + " "	+ instruction.toString();
            String text = instruction.toString();
            navInsBckgrnd.setImageBitmap(bmp2);
            navInsBckgrnd.setClickable(true);
            navInsImage.setImageBitmap(bmp);
            navText.setText(text);

            String insPOItext = instruction.getPoiName();
            if (instruction.getType() == Instruction.TYPE_DESTINATION) {
//				Location destination = aStarData.getInstance().getDestination();
//				if (destination != null && destination.getPoi() != null) {
//					String destinationname = destination.getPoi()
//							.getpoiDescription();
//					if (destinationname != null) {
//						navPoiText.setText(destinationname);
//					}
//				}
                navPoiText.setText("");
            } else if (insPOItext != null && insPOItext.length() > 0) {
                int tmptxt = ResourceTranslator.getInstance().getTranslatedResourceId("string", "by_the_for_instruction");
                String suffix = getResources().getString(tmptxt);
                navPoiText.setText(suffix + " " + insPOItext);
            } else {
                navPoiText.setText("");
            }

        }
        Log.getInstance().debug(TAG, "Exit, updateBubble()");
    }

    public void updateBubble(Instruction instruction) {
        Log.getInstance().debug(TAG, "Enter, updateBubble()");
        if (instruction != null) {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), instruction.getImage().get(0));
            Bitmap bmp2 = BitmapFactory.decodeResource(getResources(), R.drawable.navinsimagebackground);
            String text = getResources().getString(instruction.getText().get(0));
            navInsBckgrnd.setImageBitmap(bmp2);
            navInsImage.setImageBitmap(bmp);
            navText.setText(text);
        }
        Log.getInstance().debug(TAG, "Exit, updateBubble()");
    }

    public void clearInbstructionsBubbleText() {
        Log.getInstance().debug(TAG, "Enter, clearInbstructionsBubbleText()");
        setBackgroundColor(getResources().getColor(android.R.color.transparent));
        navText.setText("");
        navPoiText.setText("");
        navInsBckgrnd.setImageBitmap(null);
        navInsBckgrnd.setClickable(false);
        navInsImage.setImageBitmap(null);
        Log.getInstance().debug(TAG, "Exit, clearInbstructionsBubbleText()");
    }

}
