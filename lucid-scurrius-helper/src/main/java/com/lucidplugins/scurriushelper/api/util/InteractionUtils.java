package com.lucidplugins.scurriushelper.api.util;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.api.movement.Reachable;
import net.unethicalite.api.scene.Tiles;

import java.util.ArrayList;
import java.util.List;

public class InteractionUtils
{
    public static void useItemOnWallObject(Client client, Item item, WallObject wallObject)
    {
        final ItemComposition itemComposition = client.getItemComposition(item.getId());
        final ObjectComposition objectComposition = client.getObjectDefinition(wallObject.getId());
        client.invokeMenuAction("Use", "<col=ff9040>" + itemComposition.getName() + "</col>", 0, MenuAction.WIDGET_TARGET.getId(), item.getSlot(), InventoryUtils.calculateWidgetId(client, item), item.getId(), -1);
        client.invokeMenuAction("Use", "<col=ff9040>" + itemComposition.getName() + "</col><col=ffffff> -> <col=ffff>" + objectComposition.getName(), wallObject.getId(), MenuAction.WIDGET_TARGET_ON_GAME_OBJECT.getId(), wallObject.getLocalLocation().getSceneX(), wallObject.getLocalLocation().getSceneY(), -1, -1);
    }

    public static WorldPoint getClosestSafeLocationInNPCMeleeDistance(Client client, List<LocalPoint> list, NPC target)
    {
        List<Tile> safeTiles = Tiles.getAll(tile ->
                distanceTo2DHypotenuse(getCenterTileFromWorldArea(target.getWorldArea()), tile.getWorldLocation()) >= Math.floor((double)target.getWorldArea().getWidth() / 2) + 1 &&
                        distanceTo2DHypotenuse(getCenterTileFromWorldArea(target.getWorldArea()), tile.getWorldLocation()) <= Math.floor((double)target.getWorldArea().getWidth() / 2) + 1.5 &&
                        !target.getWorldArea().contains(tile.getWorldLocation()) &&
                        approxDistanceTo(tile.getWorldLocation(), client.getLocalPlayer().getWorldLocation()) < 6 &&
                        isWalkable(tile.getWorldLocation()));

        List<Tile> trueSafeTiles = new ArrayList<>();
        for (Tile t : safeTiles)
        {
            boolean safe = true;
            for (LocalPoint unsafeTile : list)
            {
                if (t.getWorldLocation().equals(WorldPoint.fromLocal(client, unsafeTile)))
                {
                    safe = false;
                }
            }
            if (safe)
            {
                trueSafeTiles.add(t);
            }
        }

        WorldPoint closestTile = null;

        if (trueSafeTiles.size() > 0)
        {
            float closest = 999;
            for (Tile closeTile : trueSafeTiles)
            {
                float testDistance = distanceTo2DHypotenuse(client.getLocalPlayer().getWorldLocation(), closeTile.getWorldLocation());

                if (testDistance < closest)
                {
                    closestTile = closeTile.getWorldLocation();
                    closest = testDistance;
                }
            }
        }
        return closestTile;
    }

    public static WorldPoint getClosestSafeLocationNotInNPCMeleeDistance(Client client, List<LocalPoint> list, NPC target)
    {
        List<Tile> safeTiles = Tiles.getAll(tile ->
                approxDistanceTo(target.getWorldArea().getCenter(), tile.getWorldLocation()) > (target.getWorldArea().getWidth() / 2) + 1  &&
                        !target.getWorldArea().contains(tile.getWorldLocation()) &&
                        approxDistanceTo(tile.getWorldLocation(), client.getLocalPlayer().getWorldLocation()) < 6 &&
                        isWalkable(tile.getWorldLocation()));

        List<Tile> trueSafeTiles = new ArrayList<>();
        for (Tile t : safeTiles)
        {
            boolean safe = true;
            for (LocalPoint unsafeTile : list)
            {
                if (t.getWorldLocation().equals(WorldPoint.fromLocal(client, unsafeTile)))
                {
                    safe = false;
                }
            }
            if (safe)
            {
                trueSafeTiles.add(t);
            }
        }

        WorldPoint closestTile = null;

        if (trueSafeTiles.size() > 0)
        {
            float closest = 999;
            for (Tile closeTile : trueSafeTiles)
            {
                float testDistance = distanceTo2DHypotenuse(client.getLocalPlayer().getWorldLocation(), closeTile.getWorldLocation());

                if (testDistance < closest)
                {
                    closestTile = closeTile.getWorldLocation();
                    closest = testDistance;
                }
            }
        }
        return closestTile;
    }

    public static boolean sleep(Client client, long ms)
    {
        if (client.isClientThread())
        {
            return false;
        }
        else
        {
            try
            {
                Thread.sleep(ms);
                return true;
            }
            catch (InterruptedException var3)
            {
                return false;
            }
        }
    }

    public static float distanceTo2DHypotenuse(WorldPoint main, WorldPoint other)
    {
        return (float)Math.hypot((double)(main.getX() - other.getX()), (double)(main.getY() - other.getY()));
    }

    public static boolean isWalkable(WorldPoint point)
    {
        return Reachable.isWalkable(point);
    }

    public static int approxDistanceTo(WorldPoint point1, WorldPoint point2)
    {
        return Math.max(Math.abs(point1.getX() - point2.getX()), Math.abs(point1.getY() - point2.getY()));
    }

    public static WorldPoint getCenterTileFromWorldArea(WorldArea area)
    {
        return new WorldPoint(area.getX() + area.getWidth() / 2, area.getY() + area.getHeight() / 2, area.getPlane());
    }

    public static void walk(WorldPoint worldPoint)
    {
        Movement.walk(worldPoint);
    }
}
