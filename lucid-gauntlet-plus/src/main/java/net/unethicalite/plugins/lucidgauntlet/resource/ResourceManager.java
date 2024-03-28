package net.unethicalite.plugins.lucidgauntlet.resource;

import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.util.Text;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.unethicalite.plugins.lucidgauntlet.LucidGauntletPlusConfig;
import net.unethicalite.plugins.lucidgauntlet.LucidGauntletPlusPlugin;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
public class ResourceManager
{
    private static final int NORMAL_GAUNTLET_REGION_ID = 7512;
    private static final int CORRUPTED_GAUNTLET_REGION_ID = 7768;

    private static final String MESSAGE_UNTRADEABLE_DROP = "Untradeable drop: ";

    private static final Pattern PATTERN_RESOURCE_DROP = Pattern.compile("((?<quantity>\\d+) x )?(?<name>.+)");

    @Inject
    private Client client;

    @Inject
    private LucidGauntletPlusPlugin plugin;

    @Inject
    private LucidGauntletPlusConfig config;

    @Inject
    private ItemManager itemManager;

    @Inject
    private InfoBoxManager infoBoxManager;

    private final Map<Resource, ResourceCounter> resources = new HashMap();

    private Region region = Region.UNKNOWN;

    private String prefix;

    public void init()
    {
        prefix = isLootVarbitSet() ? MESSAGE_UNTRADEABLE_DROP : getNamedDropMessage();
        region = getRegion();
    }

    public void reset()
    {
        prefix = null;
        region = Region.UNKNOWN;

        resources.clear();

        infoBoxManager.removeIf(ResourceCounter.class::isInstance);
    }

    public void parseChatMessage(String chatMessage)
    {
        if (region == Region.UNKNOWN || prefix == null)
        {
            return;
        }

        chatMessage = Text.removeTags(chatMessage);

        if (chatMessage.startsWith(prefix))
        {
            chatMessage = chatMessage.replace(prefix, "");

            processNpcResource(chatMessage);
        }
        else
        {
            processSkillResource(chatMessage);
        }
    }

    private void processNpcResource(final String chatMessage)
    {
        final Matcher matcher = PATTERN_RESOURCE_DROP.matcher(chatMessage);

        if (!matcher.matches())
        {
            return;
        }

        final String itemName = matcher.group("name");

        if (itemName == null)
        {
            return;
        }

        final Resource resource = Resource.fromName(itemName, region == Region.CORRUPTED);

        if (resource == null)
        {
            return;
        }

        final String quantity = matcher.group("quantity");
        final int itemCount = quantity != null ? Integer.parseInt(quantity) : 1;

        processResource(resource, itemCount);
    }

    private void processSkillResource(final String chatMessage)
    {
        final Map<Resource, Integer> mapping = Resource.fromPattern(chatMessage, region == Region.CORRUPTED);

        if (mapping == null)
        {
            return;
        }

        final Resource resource = mapping.keySet().iterator().next();

        if (!resources.containsKey(resource))
        {
            return;
        }

        final int itemCount = mapping.get(resource);

        processResource(resource, itemCount);
    }

    private void processResource(final Resource resource, final int itemCount)
    {
        if (!resources.containsKey(resource))
        {
            initResource(resource, itemCount);
        }
        else
        {
            ResourceCounter counter = resources.get(resource);
            counter.incrementCount(itemCount);
        }
    }

    private void initResource(final Resource resource, final int itemCount)
    {
        final ResourceCounter counter = new ResourceCounter(plugin, resource,
                itemManager.getImage(resource.getItemId()), itemCount);

        resources.put(resource, counter);
        infoBoxManager.addInfoBox(counter);
    }

    private String getNamedDropMessage()
    {
        final Player player = client.getLocalPlayer();

        if (player == null)
        {
            return null;
        }

        return player.getName() + " received a drop: ";
    }

    private boolean isLootVarbitSet()
    {
        return client.getVarbitValue(5399) == 1 &&
                client.getVarbitValue(5402) == 1;
    }

    private Region getRegion()
    {
        final int regionId = client.getMapRegions()[0];

        if (regionId == CORRUPTED_GAUNTLET_REGION_ID)
        {
            return Region.CORRUPTED;
        }

        if (regionId == NORMAL_GAUNTLET_REGION_ID)
        {
            return Region.NORMAL;
        }

        return Region.UNKNOWN;
    }

    private static boolean isNonBasicResource(final Resource resource)
    {
        switch (resource)
        {
            case TELEPORT_CRYSTAL:
            case CORRUPTED_TELEPORT_CRYSTAL:
            case WEAPON_FRAME:
            case CORRUPTED_WEAPON_FRAME:
            case CRYSTALLINE_BOWSTRING:
            case CORRUPTED_BOWSTRING:
            case CRYSTAL_SPIKE:
            case CORRUPTED_SPIKE:
            case CRYSTAL_ORB:
            case CORRUPTED_ORB:
                return true;
            default:
                return false;
        }
    }

    private enum Region
    {
        UNKNOWN, NORMAL, CORRUPTED
    }
}