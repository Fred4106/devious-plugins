package com.lucidplugins.lucidhotkeys;

import lombok.Getter;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.client.Static;

import java.util.Collection;

@Getter
public class LocalRegionTile
{

    private LocalPoint localTile;
    private int regionId;
    private int lpX;
    private int lpY;

    public LocalRegionTile(int regionId, int x, int y)
    {
        this.regionId = regionId;
        this.lpX = x;
        this.lpY = y;
        localTile = getInstanceLocalPoint(regionId, x, y);
    }

    public static LocalPoint getInstanceLocalPoint(int regionId, int x, int y)
    {
        final Collection<WorldPoint> worldPoints = WorldPoint.toLocalInstance(Static.getClient(),
                WorldPoint.fromRegion(Static.getClient().getLocalPlayer().getWorldLocation().getRegionID(), x, y, Static.getClient().getLocalPlayer().getWorldLocation().getPlane()));

        final WorldPoint worldPoint = worldPoints.stream().findFirst().orElse(null);

        if (worldPoint == null)
        {
            return null;
        }

        return LocalPoint.fromWorld(Static.getClient(), worldPoint);
    }

    public LocalPoint getLocalTile()
    {
        localTile = getInstanceLocalPoint(regionId, lpX, lpY);
        return localTile;
    }
}
