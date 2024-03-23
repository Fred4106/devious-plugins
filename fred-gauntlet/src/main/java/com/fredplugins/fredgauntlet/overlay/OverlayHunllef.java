package com.fredplugins.fredgauntlet.overlay;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.model.Jarvis;
import net.runelite.api.model.Vertex;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;
import com.fredplugins.fredgauntlet.FredGauntletConfig;
import com.fredplugins.fredgauntlet.FredGauntletPlugin;
import com.fredplugins.fredgauntlet.entity.Hunllef;
import com.fredplugins.fredgauntlet.entity.Missile;
import com.fredplugins.fredgauntlet.entity.Tornado;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.util.List;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

@Singleton
public class OverlayHunllef extends Overlay
{
    private static final Color[] COLORS = new Color[]{
            Color.BLUE,
            Color.RED,
            Color.GREEN,
            Color.ORANGE,
            Color.WHITE,
            Color.CYAN,
            Color.MAGENTA,
            Color.PINK,
            Color.YELLOW,
            Color.DARK_GRAY,
            Color.LIGHT_GRAY
    };

    private static final int COLOR_DURATION = 10;

    private final Client client;
    private final FredGauntletPlugin plugin;
    private final FredGauntletConfig config;
    private final ModelOutlineRenderer modelOutlineRenderer;

    private Hunllef hunllef;

    private int timeout;
    private int idx;

    @Inject
    private OverlayHunllef(final Client client, final FredGauntletPlugin plugin, final FredGauntletConfig config, final ModelOutlineRenderer modelOutlineRenderer)
    {
        super(plugin);

        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.modelOutlineRenderer = modelOutlineRenderer;

        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
        determineLayer();
    }

    @Override
    public Dimension render(final Graphics2D graphics2D)
    {
        hunllef = plugin.getHunllef();

        if (hunllef == null)
        {
            return null;
        }

        final NPC npc = hunllef.getNpc();

        if (npc == null)
        {
            return null;
        }

        if (npc.isDead())
        {
            renderDiscoMode();
            return null;
        }

        renderTornadoes(graphics2D);

        renderProjectile(graphics2D);

        renderHunllefWrongPrayerOutline();

        renderHunllefAttackCounter(graphics2D);

        renderHunllefAttackStyleIcon(graphics2D);

        renderHunllefTile(graphics2D);

        renderFlashOnWrongAttack(graphics2D);

        renderFlashOn51Method(graphics2D);

        return null;
    }

    public void determineLayer()
    {
        setLayer(OverlayLayer.UNDER_WIDGETS);
    }

    private void renderTornadoes(final Graphics2D graphics2D)
    {
        if ((!config.tornadoTickCounter() && !config.tornadoTileOutline()) || plugin.getTornadoes().isEmpty())
        {
            return;
        }

        for (final Tornado tornado : plugin.getTornadoes())
        {
            final int timeLeft = tornado.getTimeLeft();

            if (timeLeft < 0)
            {
                continue;
            }

            final NPC npc = tornado.getNpc();

            if (config.tornadoTileOutline())
            {

                final Polygon polygon = Perspective.getCanvasTilePoly(client, npc.getLocalLocation());

                if (polygon == null)
                {
                    continue;
                }

                drawOutlineAndFill(graphics2D, config.tornadoOutlineColor(), config.tornadoFillColor(),
                        config.tornadoTileOutlineWidth(), polygon);
            }

            if (config.tornadoTickCounter())
            {
                final String ticksLeftStr = String.valueOf(timeLeft);

                final Point point = npc.getCanvasTextLocation(graphics2D, ticksLeftStr, 0);

                if (point == null)
                {
                    return;
                }

                OverlayUtil.renderTextLocation(graphics2D, ticksLeftStr, config.tornadoFontSize(),
                        0, config.tornadoFontColor(), point.getAwtPoint(),
                        config.tornadoFontShadow(), 0);
            }
        }
    }

    private void renderProjectile(final Graphics2D graphics2D)
    {
        if ((!config.outlineProjectile() && !config.overlayProjectileIcon()) || plugin.getMissile() == null)
        {
            return;
        }

        final Missile missile = plugin.getMissile();

        final Polygon polygon = getProjectilePolygon(client, missile.getProjectile());

        if (polygon == null)
        {
            return;
        }

        if (config.outlineProjectile())
        {
            final Color originalColor = graphics2D.getColor();

            graphics2D.setColor(missile.getOutlineColor());
            graphics2D.draw(polygon);

            graphics2D.setColor(missile.getFillColor());
            graphics2D.fill(polygon);

            graphics2D.setColor(originalColor);
        }

        if (config.overlayProjectileIcon())
        {
            final BufferedImage icon = missile.getIcon();

            final Rectangle bounds = polygon.getBounds();

            final int x = (int) bounds.getCenterX() - (icon.getWidth() / 2);
            final int y = (int) bounds.getCenterY() - (icon.getHeight() / 2);

            graphics2D.drawImage(icon, x, y, null);
        }
    }

    private void renderHunllefWrongPrayerOutline()
    {
        if (!config.hunllefOverlayWrongPrayerOutline())
        {
            return;
        }

        final Hunllef.AttackPhase phase = hunllef.getAttackPhase();

        if (client.isPrayerActive(phase.getPrayer()))
        {
            return;
        }

        modelOutlineRenderer.drawOutline(hunllef.getNpc(), config.hunllefWrongPrayerOutlineWidth(), phase.getColor(),
                0);
    }

    private void renderHunllefAttackCounter(final Graphics2D graphics2D)
    {
        if (!config.hunllefOverlayAttackCounter())
        {
            return;
        }

        final NPC npc = hunllef.getNpc();

        final String text = String.format("%d | %d", hunllef.getAttackCount(),
                hunllef.getPlayerAttackCount());

        final Point point = npc.getCanvasTextLocation(graphics2D, text, 0);

        if (point == null)
        {
            return;
        }

        final Font originalFont = graphics2D.getFont();

        graphics2D.setFont(new Font(Font.SANS_SERIF,
                0, config.hunllefAttackCounterFontSize()));

        OverlayUtil.renderTextLocation(graphics2D, point, text, hunllef.getAttackPhase().getColor());

        graphics2D.setFont(originalFont);
    }

    private void renderHunllefAttackStyleIcon(final Graphics2D graphics2D)
    {
        if (!config.hunllefOverlayAttackStyleIcon())
        {
            return;
        }

        final NPC npc = hunllef.getNpc();

        final BufferedImage icon = hunllef.getIcon();

        final Point point = Perspective.getCanvasImageLocation(client, npc.getLocalLocation(), icon,
                npc.getLogicalHeight() - 100);

        if (point == null)
        {
            return;
        }

        graphics2D.drawImage((Image) icon, (int) point.getX(), (int) point.getY(), null);
    }

    private void renderHunllefTile(final Graphics2D graphics2D)
    {
        if (!config.hunllefOutlineTile())
        {
            return;
        }

        final NPC npc = hunllef.getNpc();

        final NPCComposition npcComposition = npc.getComposition();

        if (npcComposition == null)
        {
            return;
        }

        final Polygon polygon = Perspective.getCanvasTileAreaPoly(client, npc.getLocalLocation(),
                npcComposition.getSize());

        if (polygon == null)
        {
            return;
        }

        drawOutlineAndFill(graphics2D, config.hunllefOutlineColor(), config.hunllefFillColor(),
                config.hunllefTileOutlineWidth(), polygon);
    }

    private void renderFlashOnWrongAttack(final Graphics2D graphics2D)
    {
        if (!config.flashOnWrongAttack() || !plugin.isWrongAttackStyle())
        {
            return;
        }

        final Color originalColor = graphics2D.getColor();

        graphics2D.setColor(config.flashOnWrongAttackColor());

        graphics2D.fill(client.getCanvas().getBounds());

        graphics2D.setColor(originalColor);

        if (++timeout >= config.flashOnWrongAttackDuration())
        {
            timeout = 0;
            plugin.setWrongAttackStyle(false);
        }
    }

    private void renderFlashOn51Method(final Graphics2D graphics2D)
    {
        if (!config.flashOn51Method() || !plugin.isSwitchWeapon())
        {
            return;
        }

        final Color originalColor = graphics2D.getColor();

        graphics2D.setColor(config.flashOn51MethodColor());

        graphics2D.fill(client.getCanvas().getBounds());

        graphics2D.setColor(originalColor);

        if (++timeout >= config.flashOn51MethodDuration())
        {
            timeout = 0;
            plugin.setSwitchWeapon(false);
        }
    }

    private void renderDiscoMode()
    {
        if (!config.discoMode())
        {
            return;
        }

        if (++timeout > COLOR_DURATION)
        {
            timeout = 0;
            idx = idx >= COLORS.length - 1 ? 0 : idx + 1;
        }

        modelOutlineRenderer.drawOutline(hunllef.getNpc(), 6, COLORS[idx], 4);
    }

    private static Polygon getProjectilePolygon(final Client client, final Projectile projectile)
    {
        if (projectile == null || projectile.getModel() == null)
        {
            return null;
        }

        final Model model = projectile.getModel();

        final LocalPoint localPoint = new LocalPoint((int) projectile.getX(), (int) projectile.getY());

        final int tileHeight = Perspective.getTileHeight(client, localPoint, client.getPlane());

        double angle = Math.atan(projectile.getVelocityY() / projectile.getVelocityX());
        angle = Math.toDegrees(angle) + (projectile.getVelocityX() < 0 ? 180 : 0);
        angle = angle < 0 ? angle + 360 : angle;
        angle = 360 - angle - 90;

        double ori = angle * (512d / 90d);
        ori = ori < 0 ? ori + 2048 : ori;

        final int orientation = (int) Math.round(ori);

        final List<Vertex> vertices = model.getVertices();

        for (int i = 0; i < vertices.size(); ++i)
        {
            vertices.set(i, vertices.get(i).rotate(orientation));
        }

        final List<Point> list = new ArrayList<>();

        for (final Vertex vertex : vertices)
        {
            final Point point = Perspective.localToCanvas(client, localPoint.getX() - vertex.getX(),
                    localPoint.getY() - vertex.getZ(), tileHeight + vertex.getY() + (int) projectile.getZ());

            if (point == null)
            {
                continue;
            }

            list.add(point);
        }

        final List<Point> convexHull = Jarvis.convexHull(list);

        if (convexHull == null)
        {
            return null;
        }

        final Polygon polygon = new Polygon();

        for (final Point point : convexHull)
        {
            polygon.addPoint(point.getX(), point.getY());
        }

        return polygon;
    }
}
