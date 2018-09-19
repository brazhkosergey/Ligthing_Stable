package entity;

public class HideZoneArea {
    private String nameOfArea;
    private int heightOfZone;
    private int widthOfZone;
    private int xOfZone;
    private int yOfZone;

    public HideZoneArea(String nameOfArea) {
        this.nameOfArea = nameOfArea;
    }

    public String getNameOfArea() {
        return nameOfArea;
    }

    public void setNameOfArea(String nameOfArea) {
        this.nameOfArea = nameOfArea;
    }

    public int getHeightOfZone() {
        return heightOfZone;
    }

    public void setHeightOfZone(int heightOfZone) {
        this.heightOfZone = heightOfZone;
    }

    public int getWidthOfZone() {
        return widthOfZone;
    }

    public void setWidthOfZone(int widthOfZone) {
        this.widthOfZone = widthOfZone;
    }

    public int getxOfZone() {
        return xOfZone;
    }

    public void setxOfZone(int xOfZone) {
        this.xOfZone = xOfZone;
    }

    public int getyOfZone() {
        return yOfZone;
    }

    public void setyOfZone(int yOfZone) {
        this.yOfZone = yOfZone;
    }
}
