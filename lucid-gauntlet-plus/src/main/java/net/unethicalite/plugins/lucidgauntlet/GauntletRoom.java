package net.unethicalite.plugins.lucidgauntlet;


import lombok.Getter;
import lombok.Setter;

public class GauntletRoom
{
    @Getter
    @Setter
    private boolean isLit;

    @Getter
    private int baseX;

    @Getter
    private int baseY;

    public GauntletRoom(int baseX, int baseY)
    {
        this.baseX = baseX;
        this.baseY = baseY;
    }

}