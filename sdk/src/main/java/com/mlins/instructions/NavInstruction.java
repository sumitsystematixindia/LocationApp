package com.mlins.instructions;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.mlins.utils.MathUtils;
import com.mlins.utils.PropertyHolder;
import com.spreo.nav.interfaces.INavInstruction;

public class NavInstruction implements INavInstruction {

    private String text;
    private Bitmap SignBitmap = null;
    private int id = -1;
    private final double distance;
    private int type = -1;

    public NavInstruction(Instruction instruction) {
        this(
                instruction.getID(),
                instruction.toString(),
                instruction.getImage().size() > 0
                        ? BitmapFactory.decodeResource(PropertyHolder.getInstance().getMlinsContext().getResources(),instruction.getImage().get(0)) : null,
                instruction.getDistance()
        );

        if (instruction.getType() == Instruction.TYPE_DESTINATION) {
            id = INavInstruction.DESTINATION_INSTRUCTION_TAG;
        }
    }

    public NavInstruction(int id, String text, Bitmap signBitmap, double distance) {
        super();
        this.text = text;
        SignBitmap = signBitmap;
        this.id = id;
        this.distance = distance;
    }

    @Override
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public Bitmap getSignBitmap() {

        return SignBitmap;
    }

    public void setSignBitmap(Bitmap signBitmap) {
        SignBitmap = signBitmap;
    }

    @Override
    public int getId() {

        return id;
    }

    @Override
    public double getDistance() {
        boolean useFeet = PropertyHolder.getInstance().isUseFeetForDistance();
        return !useFeet ? distance : MathUtils.metersToFeet(distance);
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
