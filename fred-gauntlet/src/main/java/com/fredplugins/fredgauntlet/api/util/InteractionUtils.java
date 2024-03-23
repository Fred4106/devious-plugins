package com.fredplugins.fredgauntlet.api.util;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.api.movement.Reachable;
import net.unethicalite.api.scene.Tiles;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class InteractionUtils
{
    public static void useItemOnWallObject(Client client, Item item, WallObject wallObject)
    {
        final ItemComposition itemComposition = client.getItemComposition(item.getId());
        final ObjectComposition objectComposition = client.getObjectDefinition(wallObject.getId());
        client.invokeMenuAction("Use", "<col=ff9040>" + itemComposition.getName() + "</col>", 0, MenuAction.WIDGET_TARGET.getId(), item.getSlot(), InventoryUtils.calculateWidgetId(client, item), item.getId(), -1);
        client.invokeMenuAction("Use", "<col=ff9040>" + itemComposition.getName() + "</col><col=ffffff> -> <col=ffff>" + objectComposition.getName(), wallObject.getId(), MenuAction.WIDGET_TARGET_ON_GAME_OBJECT.getId(), wallObject.getLocalLocation().getSceneX(), wallObject.getLocalLocation().getSceneY(), -1, -1);
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

    public static void walk(WorldPoint worldPoint)
    {
        Movement.walk(worldPoint);
    }

    public static WorldPoint getClosestSafeLocation(Client client, List<LocalPoint> list)
    {
        List<Tile> safeTiles = Tiles.getAll(tile -> approxDistanceTo(tile.getWorldLocation(), client.getLocalPlayer().getWorldLocation()) < 6 && isWalkable(tile.getWorldLocation()));
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

    public static WorldPoint getSafeLocationNorthSouth(Client client, List<LocalPoint> list)
    {
        final WorldPoint loc = client.getLocalPlayer().getWorldLocation();
        final WorldPoint north = loc.dy(1);
        final WorldPoint northPlus = loc.dy(2);
        final WorldPoint south = loc.dy(-1);
        final WorldPoint southPlus = loc.dy(-2);

        // If last movement setup isnt available just find the first available instead
        if (list.stream().noneMatch(point -> WorldPoint.fromLocal(client, point).equals(north)) || !isWalkable(north))
        {
            return north;
        }
        if (list.stream().noneMatch(point -> WorldPoint.fromLocal(client, point).equals(south)) || !isWalkable(south))
        {
            return south;
        }
        if (list.stream().noneMatch(point -> WorldPoint.fromLocal(client, point).equals(northPlus)) || !isWalkable(northPlus))
        {
            return northPlus;
        }
        if (list.stream().noneMatch(point -> WorldPoint.fromLocal(client, point).equals(southPlus)) || !isWalkable(southPlus))
        {
            return southPlus;
        }
        return null;
    }

    public static WorldPoint getClosestSafeLocationNotUnderNPC(Client client, List<LocalPoint> list, NPC target)
    {
        List<Tile> safeTiles = Tiles.getAll(tile ->
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

    public static WorldPoint getClosestSafeLocationCustom(Client client, List<LocalPoint> list, Predicate<Tile> filter)
    {
        List<Tile> safeTiles = Tiles.getAll(filter);
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

    public static boolean isWalkable(WorldPoint point)
    {
        return Reachable.isWalkable(point);
    }

    public static int approxDistanceTo(WorldPoint point1, WorldPoint point2)
    {
        return Math.max(Math.abs(point1.getX() - point2.getX()), Math.abs(point1.getY() - point2.getY()));
    }

    public static float distanceTo2DHypotenuse(WorldPoint main, WorldPoint other)
    {
        return (float)Math.hypot((double)(main.getX() - other.getX()), (double)(main.getY() - other.getY()));
    }
}
