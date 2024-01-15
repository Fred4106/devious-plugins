package com.lucidplugins.lucidhotkeys.overlay;

import com.lucidplugins.lucidhotkeys.LucidHotkeysConfig;
import com.lucidplugins.lucidhotkeys.LucidHotkeysPlugin;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.unethicalite.client.Static;

import javax.inject.Inject;
import java.awt.*;
import java.util.Map;

public class TileMarkersOverlay extends Overlay
{

    private final Client client;
    private final LucidHotkeysPlugin plugin;
    private final LucidHotkeysConfig config;
    private Player player;
    @Inject
    TileMarkersOverlay(final Client client, final LucidHotkeysPlugin plugin, final LucidHotkeysConfig config)
    {
        super(plugin);
        this.client = client;
        this.plugin = plugin;
        this.config = config;

        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics2D)
    {

        player = client.getLocalPlayer();

        if (player == null)
        {
            return null;
        }

        renderTileMarkers(graphics2D);

        return null;
    }

    private void renderTileMarkers(Graphics2D graphics2D)
    {
        for (Map.Entry<Point, String> entry : plugin.getLocalPointTileMarkers().entrySet())
        {
            final Point point = entry.getKey();
            final String text = entry.getValue();
            LocalPoint localPoint = LocalPoint.fromScene(point.getX(), point.getY());
            if (localPoint.isInScene())
            {
                renderTileMarkerLocalPoint(localPoint, graphics2D, text, Color.BLUE);
            }
        }

        for (Map.Entry<WorldPoint, String> entry : plugin.getWorldPointTileMarkers().entrySet())
        {
            final WorldPoint worldPoint = entry.getKey();
            final String text = entry.getValue();
            if (worldPoint.isInScene(Static.getClient()))
            {
                renderTileMarkerWorldPoint(worldPoint, graphics2D, text, Color.BLUE);
            }
        }

        if (plugin.getPlayersTracked() != null && plugin.getPlayersTracked().size() > 0)
        {
            for (Player p : plugin.getPlayersTracked())
            {
                if (p != null)
                {
                    renderTileMarkerWorldPoint(p.getWorldLocation(), graphics2D, p.getName(), Color.RED);
                }

            }

        }

        if (plugin.getNpcsTracked() != null && plugin.getNpcsTracked().size() > 0)
        {
            for (NPC npcTracked : plugin.getNpcsTracked())
            {
                if (npcTracked != null)
                {
                    renderTileMarkerWorldPoint(npcTracked.getWorldLocation(), graphics2D, npcTracked.getName(), Color.YELLOW);
                }
            }
        }
    }

    private void renderTileMarkerLocalPoint(LocalPoint lp, Graphics2D graphics2D, String text, Color color)
    {
        final Polygon polygon = Perspective.getCanvasTileAreaPoly(client, lp, 1);
        if (polygon == null)
        {
            return;
        }

        final Point point = Perspective.getCanvasTextLocation(client, graphics2D, lp, text, -25);
        if (point == null)
        {
            return;
        }

        final Font originalFont = graphics2D.getFont();
        graphics2D.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));

        drawOutlineAndFill(graphics2D, color, null, 2, polygon);
        OverlayUtil.renderTextLocation(graphics2D, point, text, color);
        graphics2D.setFont(originalFont);
    }

    private void renderTileMarkerWorldPoint(WorldPoint wp, Graphics2D graphics2D, String text, Color color)
    {
        renderTileMarkerLocalPoint(LocalPoint.fromWorld(Static.getClient(), wp), graphics2D, text, color);
    }
}