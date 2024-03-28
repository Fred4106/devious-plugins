package net.unethicalite.plugins.lucidgauntlet;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.queries.GameObjectQuery;
import net.runelite.api.queries.GroundObjectQuery;
import net.runelite.api.queries.NPCQuery;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.TileItems;
import net.unethicalite.api.input.Keyboard;
import net.unethicalite.api.items.Equipment;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.api.movement.Reachable;
import net.unethicalite.api.utils.MessageUtils;
import net.unethicalite.api.widgets.Prayers;
import net.unethicalite.api.widgets.Widgets;
import net.unethicalite.plugins.lucidgauntlet.entity.*;
import net.unethicalite.plugins.lucidgauntlet.resource.ResourceManager;
import org.pf4j.Extension;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.function.Predicate;

@Extension
@PluginDescriptor(
        name = "Lucid Gauntlet Plus",
        enabledByDefault = false,
        description = "Full-auto gauntlet plugin",
        tags = {"gauntlet"}
)
@Singleton
public class LucidGauntletPlusPlugin extends Plugin
{
    public static final int ONEHAND_SLASH_AXE_ANIMATION = 395;
    public static final int ONEHAND_CRUSH_PICKAXE_ANIMATION = 400;
    public static final int ONEHAND_CRUSH_AXE_ANIMATION = 401;
    public static final int UNARMED_PUNCH_ANIMATION = 422;
    public static final int UNARMED_KICK_ANIMATION = 423;
    public static final int BOW_ATTACK_ANIMATION = 426;
    public static final int ONEHAND_STAB_HALBERD_ANIMATION = 428;
    public static final int ONEHAND_SLASH_HALBERD_ANIMATION = 440;
    public static final int ONEHAND_SLASH_SWORD_ANIMATION = 390;
    public static final int ONEHAND_STAB_SWORD_ANIMATION = 386;
    public static final int HIGH_LEVEL_MAGIC_ATTACK = 1167;
    public static final int HUNLLEF_TORNADO = 8418;
    public static final int HUNLLEF_ATTACK_ANIM = 8419;
    public static final int HUNLLEF_STYLE_SWITCH_TO_MAGE = 8754;
    public static final int HUNLLEF_STYLE_SWITCH_TO_RANGE = 8755;

    private int lastGatherAnimationTick = -1;

    private int fishLeftOnGround = 0;

    private int lastFishAmount = 0;

    private int demisKilled = 0;

    private static final List<Integer> BASIC_BOW = List.of(ItemID.CRYSTAL_BOW_BASIC, ItemID.CORRUPTED_BOW_BASIC);
    private static final List<Integer> ATTUNED_BOW = List.of(ItemID.CRYSTAL_BOW_ATTUNED, ItemID.CORRUPTED_BOW_ATTUNED);
    private static final List<Integer> PERFECTED_BOW = List.of(ItemID.CRYSTAL_BOW_PERFECTED, ItemID.CORRUPTED_BOW_PERFECTED);
    private static final List<Integer> BASIC_STAFF = List.of(ItemID.CRYSTAL_STAFF_BASIC, ItemID.CORRUPTED_STAFF_BASIC);
    private static final List<Integer> ATTUNED_STAFF = List.of(ItemID.CRYSTAL_STAFF_ATTUNED, ItemID.CORRUPTED_STAFF_ATTUNED);
    private static final List<Integer> PERFECTED_STAFF = List.of(ItemID.CRYSTAL_STAFF_PERFECTED, ItemID.CORRUPTED_STAFF_PERFECTED);
    private static final List<Integer> WEAPON_FRAMES = List.of(ItemID.WEAPON_FRAME, ItemID.WEAPON_FRAME_23871);
    private static final List<Integer> ORE_SOURCE = List.of(ObjectID.CRYSTAL_DEPOSIT, ObjectID.CORRUPT_DEPOSIT);
    private static final List<Integer> BARK_SOURCE = List.of(ObjectID.PHREN_ROOTS, ObjectID.CORRUPT_PHREN_ROOTS);
    private static final List<Integer> WOOL_SOURCE = List.of(ObjectID.LINUM_TIRINUM, ObjectID.CORRUPT_LINUM_TIRINUM);
    private static final List<Integer> ORE = List.of(ItemID.CRYSTAL_ORE, ItemID.CORRUPTED_ORE);
    private static final List<Integer> BARK = List.of(ItemID.PHREN_BARK, ItemID.PHREN_BARK_23878);
    private static final List<Integer> WOOL = List.of(ItemID.LINUM_TIRINUM, ItemID.LINUM_TIRINUM_23876);
    private static final List<Integer> SHARDS = List.of(ItemID.CRYSTAL_SHARDS, ItemID.CORRUPTED_SHARDS);
    private static final List<Integer> HELMS = List.of(ItemID.CRYSTAL_HELM_BASIC, ItemID.CORRUPTED_HELM_BASIC);
    private static final List<Integer> BODIES = List.of(ItemID.CRYSTAL_BODY_BASIC, ItemID.CORRUPTED_BODY_BASIC);
    private static final List<Integer> LEGS = List.of(ItemID.CRYSTAL_LEGS_BASIC, ItemID.CORRUPTED_LEGS_BASIC);

    public static final int[] MELEE_WEAPONS = {ItemID.CRYSTAL_HALBERD_PERFECTED, ItemID.CORRUPTED_HALBERD_PERFECTED, ItemID.CRYSTAL_HALBERD_ATTUNED, ItemID.CORRUPTED_HALBERD_ATTUNED, ItemID.CRYSTAL_HALBERD_BASIC, ItemID.CORRUPTED_HALBERD_BASIC};
    private static final int[] RANGE_WEAPONS = {ItemID.CRYSTAL_BOW_PERFECTED, ItemID.CORRUPTED_BOW_PERFECTED, ItemID.CRYSTAL_BOW_ATTUNED, ItemID.CORRUPTED_BOW_ATTUNED, ItemID.CRYSTAL_BOW_BASIC, ItemID.CORRUPTED_BOW_BASIC};
    private static final int[] MAGE_WEAPONS = {ItemID.CRYSTAL_STAFF_PERFECTED, ItemID.CORRUPTED_STAFF_PERFECTED, ItemID.CRYSTAL_STAFF_ATTUNED, ItemID.CORRUPTED_STAFF_ATTUNED, ItemID.CRYSTAL_STAFF_BASIC, ItemID.CORRUPTED_STAFF_BASIC};


    private static final Set<Integer> MELEE_ANIM_IDS = Set.of(
            ONEHAND_STAB_SWORD_ANIMATION, ONEHAND_SLASH_SWORD_ANIMATION,
            ONEHAND_SLASH_AXE_ANIMATION, ONEHAND_CRUSH_PICKAXE_ANIMATION,
            ONEHAND_CRUSH_AXE_ANIMATION, UNARMED_PUNCH_ANIMATION,
            UNARMED_KICK_ANIMATION, ONEHAND_STAB_HALBERD_ANIMATION,
            ONEHAND_SLASH_HALBERD_ANIMATION
    );

    private static final Set<Integer> ATTACK_ANIM_IDS = new HashSet<>();

    static
    {
        ATTACK_ANIM_IDS.addAll(MELEE_ANIM_IDS);
        ATTACK_ANIM_IDS.add(BOW_ATTACK_ANIMATION);
        ATTACK_ANIM_IDS.add(HIGH_LEVEL_MAGIC_ATTACK);
    }

    private static final Set<Integer> PROJECTILE_MAGIC_IDS = Set.of(
            ProjectileID.HUNLLEF_MAGE_ATTACK, ProjectileID.HUNLLEF_CORRUPTED_MAGE_ATTACK
    );

    private static final Set<Integer> PROJECTILE_RANGE_IDS = Set.of(
            ProjectileID.HUNLLEF_RANGE_ATTACK, ProjectileID.HUNLLEF_CORRUPTED_RANGE_ATTACK
    );

    private static final Set<Integer> PROJECTILE_PRAYER_IDS = Set.of(
            ProjectileID.HUNLLEF_PRAYER_ATTACK, ProjectileID.HUNLLEF_CORRUPTED_PRAYER_ATTACK
    );

    private static final Set<Integer> PROJECTILE_IDS = new HashSet<>();

    static
    {
        PROJECTILE_IDS.addAll(PROJECTILE_MAGIC_IDS);
        PROJECTILE_IDS.addAll(PROJECTILE_RANGE_IDS);
        PROJECTILE_IDS.addAll(PROJECTILE_PRAYER_IDS);
    }

    private static final Set<Integer> HUNLLEF_IDS = Set.of(
            NpcID.CRYSTALLINE_HUNLLEF, NpcID.CRYSTALLINE_HUNLLEF_9022,
            NpcID.CRYSTALLINE_HUNLLEF_9023, NpcID.CRYSTALLINE_HUNLLEF_9024,
            NpcID.CORRUPTED_HUNLLEF, NpcID.CORRUPTED_HUNLLEF_9036,
            NpcID.CORRUPTED_HUNLLEF_9037, NpcID.CORRUPTED_HUNLLEF_9038
    );

    private static final Set<Integer> TORNADO_IDS = Set.of(NullNpcID.NULL_9025, NullNpcID.NULL_9039);

    private static final Set<Integer> DEMIBOSS_IDS = Set.of(
            NpcID.CRYSTALLINE_BEAR, NpcID.CORRUPTED_BEAR,
            NpcID.CRYSTALLINE_DARK_BEAST, NpcID.CORRUPTED_DARK_BEAST,
            NpcID.CRYSTALLINE_DRAGON, NpcID.CORRUPTED_DRAGON
    );

    private static final Set<Integer> DEMIBOSS_IDS_NO_BEAR = Set.of(
            NpcID.CRYSTALLINE_DARK_BEAST, NpcID.CORRUPTED_DARK_BEAST,
            NpcID.CRYSTALLINE_DRAGON, NpcID.CORRUPTED_DRAGON
    );

    private static final Set<Integer> STRONG_NPC_IDS = Set.of(
            NpcID.CRYSTALLINE_SCORPION, NpcID.CORRUPTED_SCORPION,
            NpcID.CRYSTALLINE_UNICORN, NpcID.CORRUPTED_UNICORN,
            NpcID.CRYSTALLINE_WOLF, NpcID.CORRUPTED_WOLF
    );

    private static final Set<Integer> WEAK_NPC_IDS = Set.of(
            NpcID.CRYSTALLINE_BAT, NpcID.CORRUPTED_BAT,
            NpcID.CRYSTALLINE_RAT, NpcID.CORRUPTED_RAT,
            NpcID.CRYSTALLINE_SPIDER, NpcID.CORRUPTED_SPIDER
    );

    private static final Set<Integer> RESOURCE_IDS = Set.of(
            ObjectID.CRYSTAL_DEPOSIT, ObjectID.CORRUPT_DEPOSIT,
            ObjectID.PHREN_ROOTS, ObjectID.CORRUPT_PHREN_ROOTS,
            ObjectID.FISHING_SPOT_36068, ObjectID.CORRUPT_FISHING_SPOT,
            ObjectID.GRYM_ROOT, ObjectID.CORRUPT_GRYM_ROOT,
            ObjectID.LINUM_TIRINUM, ObjectID.CORRUPT_LINUM_TIRINUM
    );

    private static final Set<Integer> UTILITY_IDS = Set.of(
            ObjectID.SINGING_BOWL_35966, ObjectID.SINGING_BOWL_36063,
            ObjectID.RANGE_35980, ObjectID.RANGE_36077,
            ObjectID.WATER_PUMP_35981, ObjectID.WATER_PUMP_36078
    );

    private static final Set<Integer> NODES = Set.of(35999, 36001, 36102, 36104);

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private LucidGauntletPlusConfig config;

    @Inject
    private ResourceManager resourceManager;

    @Inject
    private SkillIconManager skillIconManager;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private GauntletInstanceGrid instanceGrid;

    @Getter
    private final Set<Resource> resources = new HashSet<>();

    @Getter
    private final Set<GameObject> utilities = new HashSet<>();

    @Getter
    private final Set<Tornado> tornadoes = new HashSet<>();

    @Getter
    private final Set<Demiboss> demibosses = new HashSet<>();

    @Getter
    private final Set<NPC> strongNpcs = new HashSet<>();

    @Getter
    private final Set<NPC> weakNpcs = new HashSet<>();

    private final List<Set<?>> entitySets = Arrays.asList(resources, utilities, tornadoes, demibosses, strongNpcs, weakNpcs);

    @Getter
    private Missile missile;

    @Getter
    private Hunllef hunllef;

    @Getter
    @Setter
    private boolean wrongAttackStyle;

    @Getter
    @Setter
    private boolean switchWeapon;

    private boolean inGauntlet;

    private boolean inHunllef;


    // Auto-related variables

    private int lastInteractionTick = -1;

    private int lastAttackTick = -1;

    private int lastPrayerTick = -1;

    private int lastCraftTick = -1;

    private int lastPotionTick = -1;

    private int lastFoodTick = -1;

    private boolean eatToFull = false;

    private int lastDodgeTick = -1;

    private WorldPoint lastSafeTile;

    private WorldPoint secondLastSafeTile;

    private Prayer currentProtectionPrayer = Prayer.INCREDIBLE_REFLEXES;

    private final WorldArea LOBBY = new WorldArea(new WorldPoint(3025, 6116, 1), new WorldPoint(3040, 6130, 1));

    private GauntletState state = GauntletState.STARTING;

    enum GauntletState
    {
        STARTING, GET_BASIC_BOW, GET_PERFECT_BOW, GET_ARMOR, GET_PERFECT_STAFF, GET_BASIC_STAFF, GET_FISH, COOK_FISH, READY_TO_FIGHT, LEAVE_ITS_FUCKED, HUNLLEF
    }


    @Provides
    LucidGauntletPlusConfig getConfig(final ConfigManager configManager)
    {
        return configManager.getConfig(LucidGauntletPlusConfig.class);
    }

    @Override
    protected void startUp()
    {
        if (client.getGameState() == GameState.LOGGED_IN)
        {
            clientThread.invoke(this::pluginEnabled);
        }
    }

    @Subscribe
    private void onActorDeath(final ActorDeath event)
    {
        if (!(event.getActor() instanceof Player))
        {
            return;
        }

        if (event.getActor() != client.getLocalPlayer())
        {
            return;
        }

        resetAll();
    }

    private void resetAll()
    {
        state = GauntletState.STARTING;
        inGauntlet = false;
        inHunllef = false;
        hunllef = null;
        missile = null;
        wrongAttackStyle = false;
        eatToFull = false;
        switchWeapon = false;
        fishLeftOnGround = 0;
        lastFishAmount = 0;
        demisKilled = 0;
        instanceGrid.reset();
        resourceManager.reset();

        entitySets.forEach(Set::clear);
    }

    @Subscribe
    private void onVarbitChanged(final VarbitChanged event)
    {
        if (isHunllefVarbitSet())
        {
            if (!inHunllef)
            {
                initHunllef();
            }
        }
        else if (isGauntletVarbitSet())
        {
            if (!inGauntlet)
            {
                initGauntlet();
            }
        }
    }

    @Subscribe
    private void onGameTick(final GameTick event)
    {
        updateAuto();
    }

    private void updateAuto()
    {
        if (inLobby())
        {
            if (inGauntlet || inHunllef)
            {
                resetAll();
            }

            deactivatePrayers();

            if (openRewardChest())
            {
                return;
            }

            enterGauntlet();
        }

        if (inGauntlet || inHunllef)
        {
            MessageUtils.addMessage("State: " + state);

            switch (state)
            {
                case STARTING:
                    handleStartUp();
                    break;
                case GET_BASIC_BOW:
                    handleGettingBasicBow();
                    break;
                case GET_PERFECT_BOW:
                    handleGettingPerfectBow();
                    break;
                case GET_PERFECT_STAFF:
                    handleGettingPerfectStaff();
                    break;
                case GET_ARMOR:
                    handleGettingArmor();
                    break;
                case GET_FISH:
                    handleGettingFish();
                    break;
                case COOK_FISH:
                    handleCookingFish();
                    break;
                case READY_TO_FIGHT:
                    handleReadyToFight();
                    break;
                case HUNLLEF:
                    handleHunllef();
                    break;
                case LEAVE_ITS_FUCKED:
                    handleLeaving();
                    break;
                default:
                    break;
            }
        }
    }

    private void initGrid()
    {
        if (!instanceGrid.isInitialized())
        {
            instanceGrid.initialize();
        }
    }

    private int neededArmorShards()
    {
        int armorShards = 160;
        if (hasAtLeastOne(HELMS, true))
        {
            armorShards -= 40;
        }

        if (hasAtLeastOne(BODIES, true))
        {
            armorShards -= 60;
        }

        if (hasAtLeastOne(LEGS, true))
        {
            armorShards -= 60;
        }

        return armorShards;
    }

    private boolean droppedPaddleFishForRoom()
    {
        if (Inventory.getFreeSlots() == 0)
        {
            if (Inventory.contains(ItemID.RAW_PADDLEFISH))
            {
                Item paddlefish = Inventory.getFirst(ItemID.RAW_PADDLEFISH);
                if (paddlefish != null)
                {
                    paddlefish.interact("Drop");
                    return true;
                }
            }
        }
        return false;
    }

    private boolean dropPaddleFishInStartingArea()
    {
        if (instanceGrid.getCookingRangeLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < 2 && !client.getLocalPlayer().isMoving())
        {
            Item paddlefish = Inventory.getFirst(ItemID.RAW_PADDLEFISH);
            if (paddlefish != null)
            {
                paddlefish.interact("Drop");
                return true;
            }
        }
        return false;
    }

    @Subscribe
    private void onGameStateChanged(final GameStateChanged event)
    {
        switch (event.getGameState())
        {
            case LOADING:
                resources.clear();
                utilities.clear();
                break;
            case LOGIN_SCREEN:
            case HOPPING:
                resetAll();
                break;
        }
    }

    @Subscribe
    private void onWidgetLoaded(final WidgetLoaded event)
    {
        if (event.getGroupId() == WidgetID.GAUNTLET_TIMER_GROUP_ID)
        {
            resourceManager.init();
        }
    }

    @Subscribe
    private void onGameObjectSpawned(final GameObjectSpawned event)
    {
        final GameObject gameObject = event.getGameObject();

        final int id = gameObject.getId();

        if (RESOURCE_IDS.contains(id))
        {
            resources.add(new Resource(gameObject, skillIconManager, 18));
        }
        else if (UTILITY_IDS.contains(id))
        {
            utilities.add(gameObject);
        }

        if (NODES.contains(id))
        {
            Point nodeGridLocation = instanceGrid.getGridLocationByWorldPoint(event.getTile().getWorldLocation());
            GauntletRoom litRoom = instanceGrid.getRoom(nodeGridLocation.getX(), nodeGridLocation.getY());
            if (!litRoom.isLit())
            {
                litRoom.setLit(true);
            }
        }
    }

    @Subscribe
    private void onGameObjectDespawned(final GameObjectDespawned event)
    {
        final GameObject gameObject = event.getGameObject();

        final int id = gameObject.getId();

        if (RESOURCE_IDS.contains(gameObject.getId()))
        {
            resources.removeIf(o -> o.getGameObject() == gameObject);
        }
        else if (UTILITY_IDS.contains(id))
        {
            utilities.remove(gameObject);
        }
    }

    @Subscribe
    private void onNpcSpawned(final NpcSpawned event)
    {
        final NPC npc = event.getNpc();

        final int id = npc.getId();

        if (HUNLLEF_IDS.contains(id))
        {
            hunllef = new Hunllef(npc, skillIconManager, 18);
        }
        else if (TORNADO_IDS.contains(id))
        {
            tornadoes.add(new Tornado(npc));
        }
        else if (DEMIBOSS_IDS.contains(id))
        {
            demibosses.add(new Demiboss(npc));
        }
        else if (STRONG_NPC_IDS.contains(id))
        {
            strongNpcs.add(npc);
        }
        else if (WEAK_NPC_IDS.contains(id))
        {
            weakNpcs.add(npc);
        }
    }

    @Subscribe
    private void onItemContainerChanged(ItemContainerChanged event)
    {
        if (event.getContainerId() == InventoryID.INVENTORY.getId())
        {
            int fishAmount = 0;
            for (Item item : event.getItemContainer().getItems())
            {
                if (item.getName().contains("paddlefish"))
                {
                    fishAmount++;
                }
            }
            if (fishAmount != lastFishAmount)
            {
                if (fishAmount < lastFishAmount && instanceGrid.getCookingRangeLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < 5)
                {
                    fishLeftOnGround++;
                }
                lastFishAmount = fishAmount;
            }
        }
    }

    @Subscribe
    private void onNpcDespawned(final NpcDespawned event)
    {
        final NPC npc = event.getNpc();

        final int id = npc.getId();

        if (HUNLLEF_IDS.contains(id))
        {
            hunllef = null;
        }
        else if (TORNADO_IDS.contains(id))
        {
            tornadoes.removeIf(t -> t.getNpc() == npc);
        }
        else if (DEMIBOSS_IDS.contains(id))
        {
            demibosses.removeIf(d -> d.getNpc() == npc);
        }
        else if (STRONG_NPC_IDS.contains(id))
        {
            strongNpcs.remove(npc);
        }
        else if (WEAK_NPC_IDS.contains(id))
        {
            weakNpcs.remove(npc);
        }
    }

    @Subscribe
    private void onProjectileSpawned(final ProjectileSpawned event)
    {
        if (hunllef == null)
        {
            return;
        }

        final Projectile projectile = event.getProjectile();

        final int id = projectile.getId();

        if (!PROJECTILE_IDS.contains(id))
        {
            return;
        }

        missile = new Missile(projectile, skillIconManager, 18);

    }

    @Subscribe
    private void onChatMessage(final ChatMessage event)
    {
        final ChatMessageType type = event.getType();

        if (type == ChatMessageType.SPAM || type == ChatMessageType.GAMEMESSAGE)
        {
            resourceManager.parseChatMessage(event.getMessage());
        }

        if (inHunllef && event.getMessage().contains("prayers have been disabled"))
        {
            currentProtectionPrayer = hunllef.getAttackPhase().getPrayer();
        }

        if (inGauntlet && event.getMessage().contains("You leave the Gauntlet."))
        {
            resetAll();
        }

        String message = event.getMessage();
        if (message.contains("Untradeable drop:"))
        {
            if (message.contains("Crystal orb") || message.contains("Corrupted orb") || message.contains("Crystalline bowstring") || message.contains("Corrupted bowstring") || message.contains("Crystal spike") || message.contains("Corrupted spike"))
            {
                demisKilled++;
                MessageUtils.addMessage("Demiboss kill registered. Kills: " + demisKilled);
            }
        }
    }

    @Subscribe
    private void onAnimationChanged(final AnimationChanged event)
    {
        final Actor actor = event.getActor();
        final int animationId = actor.getAnimation();

        if (animationId == 401 || animationId == 422 || animationId == 423 || animationId == BOW_ATTACK_ANIMATION)
        {
            lastAttackTick = client.getTickCount();
        }

        if (animationId == 2282 || animationId == 8325 || animationId == 8348)
        {
            lastGatherAnimationTick = client.getTickCount();
        }

        if (!isHunllefVarbitSet() || hunllef == null)
        {
            return;
        }

        if (actor instanceof Player)
        {
            if (!ATTACK_ANIM_IDS.contains(animationId))
            {
                return;
            }

            final boolean validAttack = isAttackAnimationValid(animationId);

            if (validAttack)
            {
                wrongAttackStyle = false;

                if (hunllef.getPlayerAttackCount() <= 2)
                {
                    if (hunllef.getPlayerAttackCount() == 1)
                    {
                        swapWeaponNormal();
                    }
                    //swapWeapon51(hunllef.getPlayerAttackCount(), hunllef.getNpc().getComposition().getOverheadIcon());
                }

                hunllef.updatePlayerAttackCount();

                if (hunllef.getPlayerAttackCount() == 1)
                {
                    switchWeapon = true;
                }
            }
            else
            {
                wrongAttackStyle = true;
            }
        }
        else if (actor instanceof NPC)
        {
            if (animationId == HUNLLEF_ATTACK_ANIM || animationId == HUNLLEF_TORNADO)
            {
                hunllef.updateAttackCount();
            }

            if (animationId == HUNLLEF_STYLE_SWITCH_TO_MAGE || animationId == HUNLLEF_STYLE_SWITCH_TO_RANGE)
            {
                hunllef.toggleAttackHunllefAttackStyle();

                currentProtectionPrayer = hunllef.getAttackPhase().getPrayer();
            }
        }
    }

    private boolean isAttackAnimationValid(final int animationId)
    {
        final HeadIcon headIcon = hunllef.getNpc().getComposition().getOverheadIcon();

        if (headIcon == null)
        {
            return true;
        }

        switch (headIcon)
        {
            case MELEE:
                if (MELEE_ANIM_IDS.contains(animationId))
                {
                    return false;
                }
                break;
            case RANGED:
                if (animationId == BOW_ATTACK_ANIMATION)
                {
                    return false;
                }
                break;
            case MAGIC:
                if (animationId == HIGH_LEVEL_MAGIC_ATTACK)
                {
                    return false;
                }
                break;
        }

        return true;
    }

    private void pluginEnabled()
    {
        if (isGauntletVarbitSet())
        {
            resourceManager.init();
            addSpawnedEntities();
            initGauntlet();
        }

        if (isHunllefVarbitSet())
        {
            initHunllef();
        }
    }

    private void addSpawnedEntities()
    {
        for (final GameObject gameObject : new GameObjectQuery().result(client))
        {
            GameObjectSpawned gameObjectSpawned = new GameObjectSpawned();
            gameObjectSpawned.setTile(null);
            gameObjectSpawned.setGameObject(gameObject);
            onGameObjectSpawned(gameObjectSpawned);
        }

        for (final NPC npc : client.getNpcs())
        {
            onNpcSpawned(new NpcSpawned(npc));
        }
    }

    private void initGauntlet()
    {
        inGauntlet = true;
    }

    private void initHunllef()
    {
        inHunllef = true;
        resourceManager.reset();
        lastSafeTile = client.getLocalPlayer().getWorldLocation();
        secondLastSafeTile = client.getLocalPlayer().getWorldLocation();
    }

    private boolean isGauntletVarbitSet()
    {
        return client.getVarbitValue(9178) == 1;
    }

    private boolean isHunllefVarbitSet()
    {
        return client.getVarbitValue(9177) == 1;
    }

    private static void togglePrayer(Prayer prayer)
    {
        Widget widget = Widgets.get(prayer.getWidgetInfo());
        if (widget != null)
        {
            widget.interact(0);
        }
    }

    private static void activatePrayer(Prayer prayer)
    {
        if (Prayers.isEnabled(prayer))
        {
            return;
        }

        Widget widget = Widgets.get(prayer.getWidgetInfo());
        if (widget != null)
        {
            widget.interact(0);
        }
    }

    private static void deactivatePrayers()
    {
        for (Prayer p : Prayer.values())
        {
            if (Prayers.isEnabled(p))
            {
                togglePrayer(p);
            }
        }
    }

    private Prayer getPrayerBasedOnWeapon()
    {
        if (Equipment.contains(RANGE_WEAPONS))
        {
            if (client.getVarbitValue(Varbits.RIGOUR_UNLOCKED) == 1)
            {
                return Prayer.RIGOUR;
            }
            return Prayer.EAGLE_EYE;
        }
        else if (Equipment.contains(MAGE_WEAPONS))
        {
            if (client.getVarbitValue(Varbits.AUGURY_UNLOCKED) == 1)
            {
                return Prayer.AUGURY;
            }
            return Prayer.MYSTIC_MIGHT;
        }
        else if (Equipment.contains(MELEE_WEAPONS))
        {
            return Prayer.PIETY;
        }

        return Prayer.PIETY;
    }
    private static void swapWeapon51(int attackCount, HeadIcon current)
    {
        if (attackCount == 2)
        {
            if (current == HeadIcon.MAGIC)
            {
                if (Equipment.contains(MAGE_WEAPONS) || Equipment.contains(RANGE_WEAPONS))
                {
                    Item mageWep = Equipment.getFirst(MAGE_WEAPONS);
                    Item rangeWep = Equipment.getFirst(RANGE_WEAPONS);
                    if (rangeWep != null)
                    {
                        rangeWep.interact("Remove");
                    }
                    else if (mageWep != null)
                    {
                        mageWep.interact("Remove");
                    }
                }
            }
            else if (current == HeadIcon.MELEE)
            {
                if (Inventory.contains(MAGE_WEAPONS))
                {
                    Inventory.getFirst(MAGE_WEAPONS).interact("Wield");
                }
            }
        }
        else
        {
            if (Inventory.contains(RANGE_WEAPONS))
            {
                Inventory.getFirst(RANGE_WEAPONS).interact("Wield");
            }
        }
    }

    private static void swapWeaponNormal()
    {
        if (Inventory.contains(RANGE_WEAPONS))
        {
            Inventory.getFirst(RANGE_WEAPONS).interact("Wield");
        }
        if (Inventory.contains(MAGE_WEAPONS))
        {
            Inventory.getFirst(MAGE_WEAPONS).interact("Wield");
        }
        if (Inventory.contains(MELEE_WEAPONS))
        {
            Inventory.getFirst(MELEE_WEAPONS).interact("Wield");
        }
    }

    // Auto-related add-ons

    public GameObject findOpenableChest(int ids)
    {
        assert client.isClientThread();

        if (client.getLocalPlayer() == null)
        {
            return null;
        }

        return new GameObjectQuery()
                .idEquals(ids)
                .actionEquals("Open")
                .result(client)
                .nearestTo(client.getLocalPlayer());
    }

    public GameObject findNodeForUnopenedRoom(GauntletRoom room)
    {
        return new GameObjectQuery()
                .filter(gameObject ->
                {
                    final boolean isAboveBy1 = gameObject.getWorldLocation().getY() == room.getBaseY() + 1;
                    final boolean isBelowBy1 = gameObject.getWorldLocation().getY() == room.getBaseY() - GauntletInstanceGrid.ROOM_SIZE - 1;
                    final boolean isRight = gameObject.getWorldLocation().getX() == room.getBaseX() + GauntletInstanceGrid.ROOM_SIZE;
                    final boolean isLeftBy2 = gameObject.getWorldLocation().getX() == room.getBaseX() - 2;
                    final boolean isInsideRoomX = gameObject.getWorldLocation().getX() >= room.getBaseX() && gameObject.getWorldLocation().getX() <= room.getBaseX() + GauntletInstanceGrid.ROOM_SIZE;
                    final boolean isInsideRoomY = gameObject.getWorldLocation().getY() <= room.getBaseY() && gameObject.getWorldLocation().getY() >= room.getBaseY() - GauntletInstanceGrid.ROOM_SIZE;
                   return ((isAboveBy1 && isInsideRoomX) ||
                           (isBelowBy1 && isInsideRoomX) ||
                           (isLeftBy2 && isInsideRoomY) ||
                           (isRight && isInsideRoomY)) &&
                           gameObject.getName().equals("Node");
                })
                .result(client)
                .nearestTo(client.getLocalPlayer());
    }

    public GameObject findNearestGameObject(int... ids)
    {
        return new GameObjectQuery()
                .idEquals(ids)
                .result(client)
                .nearestTo(client.getLocalPlayer());
    }

    public GameObject findNearestGameObject(Predicate<GameObject> object)
    {
        return new GameObjectQuery()
                .filter(object)
                .result(client)
                .nearestTo(client.getLocalPlayer());
    }

    public NPC findNearestWeakNpc()
    {
        return new NPCQuery()
                .idEquals(WEAK_NPC_IDS)
                .result(client)
                .nearestTo(client.getLocalPlayer());
    }

    public NPC findNearestStrongNpc()
    {
        return new NPCQuery()
                .idEquals(STRONG_NPC_IDS)
                .result(client)
                .nearestTo(client.getLocalPlayer());
    }

    public NPC findNearestDemibossNpc()
    {
        return new NPCQuery()
                .filter(npc -> demisKilled >= 2 ? DEMIBOSS_IDS.contains(npc.getId()) : DEMIBOSS_IDS_NO_BEAR.contains(npc.getId()))
                .result(client)
                .nearestTo(client.getLocalPlayer());
    }

    private int ticksSinceLastInteraction()
    {
        return client.getTickCount() - lastInteractionTick;
    }

    private int ticksSinceLastPotion()
    {
        return client.getTickCount() - lastPotionTick;
    }

    private int ticksSinceLastFood()
    {
        return client.getTickCount() - lastFoodTick;
    }

    private int ticksSinceLastDodge()
    {
        return client.getTickCount() - lastDodgeTick;
    }

    private int ticksSinceLastAttack()
    {
        return client.getTickCount() - lastAttackTick;
    }

    private int ticksSinceLastCraft()
    {
        return client.getTickCount() - lastCraftTick;
    }

    private int ticksSinceLastPrayerActivation()
    {
        return client.getTickCount() - lastPrayerTick;
    }

    private int ticksSinceLastGather()
    {
        return client.getTickCount() - lastGatherAnimationTick;
    }


    private boolean inLobby()
    {
        return client.getLocalPlayer().getWorldArea().intersectsWith(LOBBY);
    }

    private boolean openRewardChest()
    {
        int REWARD_CHEST = 37341;
        if (findOpenableChest(REWARD_CHEST) != null)
        {
            if (Inventory.getFreeSlots() > 2)
            {
                if (ticksSinceLastInteraction() > 2)
                {
                    MessageUtils.addMessage("Opening reward chest");
                    GameObject rewardChest = findNearestGameObject(REWARD_CHEST);
                    rewardChest.interact("Open");
                    lastInteractionTick = client.getTickCount();
                }
            }
            return true;
        }
        return false;
    }

    private boolean enterGauntlet()
    {
        int GAUNTLET_ENTRANCE = 37340;
        if (findNearestGameObject(GAUNTLET_ENTRANCE) != null)
        {
            if (ticksSinceLastInteraction() > 5)
            {
                MessageUtils.addMessage("Entering gauntlet - corrupted = " + config.enterCorrupted());
                GameObject gauntlet = findNearestGameObject(GAUNTLET_ENTRANCE);
                String interaction = config.enterCorrupted() ? "Enter-corrupted" : "Enter";
                gauntlet.interact(interaction);
                lastInteractionTick = client.getTickCount();
                resetAll();
                return true;
            }
        }
        return false;
    }

    private boolean hasAtLeastBasicBow()
    {
        return hasAtLeastOne(BASIC_BOW, true) || hasAtLeastOne(ATTUNED_BOW, true) || hasAtLeastOne(PERFECTED_BOW, true);
    }

    private boolean hasAtLeastAttunedBow()
    {
        return hasAtLeastOne(ATTUNED_BOW, true) || hasAtLeastOne(PERFECTED_BOW, true);
    }

    private boolean hasAtLeastPerfectBow()
    {
        return hasAtLeastOne(PERFECTED_BOW, true);
    }

    private boolean hasAtLeastBasicStaff()
    {
        return hasAtLeastOne(BASIC_STAFF, true) || hasAtLeastOne(ATTUNED_STAFF, true) || hasAtLeastOne(PERFECTED_STAFF, true);
    }

    private boolean hasAtLeastAttunedStaff()
    {
        return hasAtLeastOne(ATTUNED_STAFF, true) || hasAtLeastOne(PERFECTED_STAFF, true);
    }

    private boolean hasAtLeastPerfectStaff()
    {
        return hasAtLeastOne(PERFECTED_STAFF, true);
    }

    private boolean hasBowString()
    {
        return Inventory.contains(ItemID.CRYSTALLINE_BOWSTRING, ItemID.CORRUPTED_BOWSTRING) || hasAtLeastOne(PERFECTED_BOW, true);
    }

    private boolean hasOrb()
    {
        return Inventory.contains(ItemID.CRYSTAL_ORB, ItemID.CORRUPTED_ORB) || hasAtLeastOne(PERFECTED_STAFF, true);
    }

    private boolean useEgniolPotion()
    {
        boolean needToUse = client.getBoostedSkillLevel(Skill.PRAYER) < config.prayMin() || (Movement.getRunEnergy() < config.runMin() && !Movement.isStaminaBoosted());

        Item potion = Inventory.getFirst(item -> item.getName().contains("Egniol potion"));

        if (!Movement.isRunEnabled())
        {
            Movement.toggleRun();
        }

        if (potion != null && needToUse)
        {
            if (ticksSinceLastPotion() > 3)
            {
                potion.interact("Drink");
                lastPotionTick = client.getTickCount();
            }
            return true;
        }

        return false;
    }

    private boolean equipBow()
    {
        Item bow = Inventory.getFirst(ItemID.CRYSTAL_BOW_BASIC, ItemID.CORRUPTED_BOW_BASIC, ItemID.CRYSTAL_BOW_ATTUNED, ItemID.CORRUPTED_BOW_ATTUNED, ItemID.CRYSTAL_BOW_PERFECTED, ItemID.CORRUPTED_BOW_PERFECTED);
        if (bow != null)
        {
            bow.interact("Wield");
            lastInteractionTick = client.getTickCount();
            return true;
        }
        return false;
    }

    private boolean equipHelm()
    {
        Item helm = Inventory.getFirst(ItemID.CRYSTAL_HELM_BASIC, ItemID.CORRUPTED_HELM_BASIC);
        if (helm != null)
        {
            helm.interact("Wear");
            lastInteractionTick = client.getTickCount();
            return true;
        }
        return false;
    }

    private boolean equipBody()
    {
        Item body = Inventory.getFirst(ItemID.CRYSTAL_BODY_BASIC, ItemID.CORRUPTED_BODY_BASIC);
        if (body != null)
        {
            body.interact("Wear");
            lastInteractionTick = client.getTickCount();
            return true;
        }
        return false;
    }

    private boolean equipLegs()
    {
        Item legs = Inventory.getFirst(ItemID.CRYSTAL_LEGS_BASIC, ItemID.CORRUPTED_LEGS_BASIC);
        if (legs != null)
        {
            legs.interact("Wear");
            lastInteractionTick = client.getTickCount();
            return true;
        }
        return false;
    }

    private boolean hasMaterialsForBasicBowAndVials()
    {
        return hasAtLeastOne(WEAPON_FRAMES, false) && getShardCount() >= (20 + (config.potionAmt() * 10)) ;
    }

    private boolean takeItemsOffGround()
    {
        TileItem weaponFrame = TileItems.getNearest(item -> WEAPON_FRAMES.contains(item.getId()));
        if (weaponFrame != null && weaponFramesNeeded() > 0 && weaponFrame.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < 18)
        {
            MessageUtils.addMessage("Taking weapon frame");
            weaponFrame.interact("Take");
            lastInteractionTick = client.getTickCount();
            return true;
        }

        TileItem shards = TileItems.getNearest(item -> SHARDS.contains(item.getId()));
        if (shards != null  && shards.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < 18)
        {
            MessageUtils.addMessage("Taking the shards");
            shards.interact("Take");
            lastInteractionTick = client.getTickCount();
            return true;
        }

        TileItem teleport = TileItems.getNearest(ItemID.TELEPORT_CRYSTAL, ItemID.CORRUPTED_TELEPORT_CRYSTAL);
        if (teleport != null  && teleport.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < 18)
        {
            MessageUtils.addMessage("Taking the teleport");
            teleport.interact("Take");
            lastInteractionTick = client.getTickCount();
            return true;
        }

        TileItem grymLeaf = TileItems.getNearest(ItemID.GRYM_LEAF, ItemID.GRYM_LEAF_23875);
        if (neededGrymLeaves() > 0  && grymLeaf != null  && grymLeaf.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < 18)
        {
            MessageUtils.addMessage("Taking the grym leaf");
            grymLeaf.interact("Take");
            lastInteractionTick = client.getTickCount();
            return true;
        }

        TileItem fish = TileItems.getNearest(ItemID.RAW_PADDLEFISH);
        if (neededFish() > 0 && fish != null && (hasAllResourcesExceptFish() || Inventory.getCount(ItemID.RAW_PADDLEFISH) < 4) && Inventory.getFreeSlots() > 5 && fish.getWorldLocation().distanceTo2D(instanceGrid.getCookingRangeLocation()) > 5
        && fish.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) <= GauntletInstanceGrid.ROOM_SIZE)
        {
            MessageUtils.addMessage("Taking the fish");
            fish.interact("Take");
            lastInteractionTick = client.getTickCount();
            return true;
        }

        return false;
    }

    private boolean takeBowString()
    {
        TileItem bowstring = TileItems.getNearest(ItemID.CRYSTALLINE_BOWSTRING, ItemID.CORRUPTED_BOWSTRING);
        if (bowstring != null && !Inventory.contains(ItemID.CRYSTALLINE_BOWSTRING, ItemID.CORRUPTED_BOWSTRING))
        {
            if (canPickUpItem())
            {
                MessageUtils.addMessage("Taking bow string");
                bowstring.interact("Take");
                lastInteractionTick = client.getTickCount();
            }
            return true;
        }
        return false;
    }

    private boolean takeOrb()
    {
        TileItem orb = TileItems.getNearest(ItemID.CRYSTAL_ORB, ItemID.CORRUPTED_ORB);
        if (orb != null && !Inventory.contains(ItemID.CRYSTAL_ORB, ItemID.CORRUPTED_ORB))
        {
            if (canPickUpItem())
            {
                MessageUtils.addMessage("Taking orb");
                orb.interact("Take");
                lastInteractionTick = client.getTickCount();
            }
            return true;
        }
        return false;
    }

    private boolean canPickUpItem()
    {
        return (ticksSinceLastInteraction() > 0 && !client.getLocalPlayer().isMoving()) || ticksSinceLastInteraction() > 2;
    }

    private boolean canTakeResource()
    {
        return ticksSinceLastGather() > 3 && ticksSinceLastInteraction() > 1 && !client.getLocalPlayer().isMoving();
    }

    private boolean canAttackNpc()
    {
        return ticksSinceLastAttack() > 4 && ticksSinceLastInteraction() > 1;
    }


    private boolean fightWeakNpc()
    {
        NPC toFight = findNearestWeakNpc();
        if (toFight != null && toFight.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < GauntletInstanceGrid.ROOM_SIZE)
        {
            MessageUtils.addMessage("Attacking weak npc");
            if (canAttackNpc())
            {
                if (toFight.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < GauntletInstanceGrid.ROOM_SIZE)
                {
                    toFight.interact("Attack");
                }
                else
                {
                    if (Reachable.isWalkable(toFight.getWorldLocation()))
                    {
                        Movement.walk(toFight.getWorldLocation());
                    }
                    else
                    {
                        Movement.walkTo(toFight.getWorldLocation());
                    }
                }
                lastInteractionTick = client.getTickCount();

            }
            return true;
        }
        return false;
    }

    private boolean fightStrongNpc()
    {
        if (getShardCount() > 220)
        {
            return false;
        }

        NPC toFightStronger = findNearestStrongNpc();
        if ((instanceGrid.getNextUnlitRoomFirstPass() == null && toFightStronger != null) || (state == GauntletState.GET_ARMOR && toFightStronger != null))
        {
            MessageUtils.addMessage("Attacking strong npc");
            if (canAttackNpc())
            {
                if (toFightStronger.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < GauntletInstanceGrid.ROOM_SIZE)
                {
                    toFightStronger.interact("Attack");
                }
                else
                {
                    if (Reachable.isWalkable(toFightStronger.getWorldLocation()))
                    {
                        Movement.walk(toFightStronger.getWorldLocation());
                    }
                    else
                    {
                        Movement.walkTo(toFightStronger.getWorldLocation());
                    }
                }
            }
            return true;
        }
        return false;
    }

    private NPC meleeRangeNearby()
    {
        NPC toFightStrong = findNearestStrongNpc();
        if (toFightStrong != null && (toFightStrong.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < 5 && toFightStrong.getWorldArea().hasLineOfSightTo(client, client.getLocalPlayer().getWorldLocation())))
        {
            return toFightStrong;
        }
        return null;
    }

    private NPC demiBossNearby()
    {
        NPC toFightDemiBoss = findNearestDemibossNpc();
        if (toFightDemiBoss != null && toFightDemiBoss.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) <= GauntletInstanceGrid.ROOM_SIZE)
        {
            return toFightDemiBoss;
        }
        return null;
    }

    private void attackDemiBoss(NPC demiBoss)
    {
        if (canAttackNpc())
        {
            if (demiBoss.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) > 4)
            {
                Movement.walk(demiBoss.getWorldLocation());
            }
            else
            {
                demiBoss.interact("Attack");
                lastInteractionTick = client.getTickCount();
            }
        }
    }

    private boolean takeResources()
    {
        if (resourceExistsThatWeNeed())
        {
            MessageUtils.addMessage("Resources nearby that we need");
            takeNeededResource();
            return true;
        }
        return false;
    }

    private boolean hasAllResourcesExceptFish()
    {
        if (weaponFramesNeeded() > 0)
        {
            return false;
        }

        if (!hasAtLeastOne(BASIC_STAFF, true) && getShardCount() < 20)
        {
            return false;
        }

        if (neededBark() > 0 || neededWool() > 0 || neededOre() > 0)
        {
            return false;
        }

        if (neededArmorShards() > getShardCount())
        {
            return false;
        }

        if (neededGrymLeaves() > 0)
        {
            return false;
        }

        return true;
    }

    private boolean hasAllArmorResources()
    {

        if (neededBark() > 0 || neededWool() > 0 || neededOre() > 0)
        {
            return false;
        }

        if (neededArmorShards() > getShardCount())
        {
            return false;
        }

        return true;
    }

    private boolean needBowAndCanMakeOne()
    {
       return !hasAtLeastBasicBow() && getShardCount() >= 20 && hasAtLeastOne(WEAPON_FRAMES, false);
    }

    private boolean hasMaterialsForPerfectBow()
    {
        return ((hasAtLeastBasicBow() && getShardCount() >= 60) || hasAtLeastAttunedBow()) && hasBowString();
    }

    private boolean hasMaterialsForPerfectStaff()
    {
        int shardsNeeded = 80;
        int framesNeeded = 1;
        if (hasAtLeastBasicStaff())
        {
            framesNeeded = 0;
            shardsNeeded -= 20;
        }
        if (hasAtLeastAttunedStaff())
        {
            shardsNeeded -= 60;
        }
        return getShardCount() >= shardsNeeded && Inventory.getCount(ItemID.WEAPON_FRAME, ItemID.WEAPON_FRAME_23871) >= framesNeeded && hasOrb();
    }

    private boolean needStaffAndCanMakeOne()
    {
        return !hasAtLeastOne(BASIC_STAFF, true) && getShardCount() >= 20 && hasAtLeastOne(WEAPON_FRAMES, false);
    }

    private boolean isCraftingMenuOpen()
    {
        Widget craftingMenu = Widgets.get(270, 0);

        return craftingMenu != null && !craftingMenu.isHidden();
    }

    private void goToSingingBowl(boolean useTeleport)
    {
        if (client.getLocalPlayer().getWorldLocation().distanceTo2D(instanceGrid.getStartLocation()) > 25)
        {
            if (useTeleport && Inventory.contains(ItemID.TELEPORT_CRYSTAL, ItemID.CORRUPTED_TELEPORT_CRYSTAL))
            {
                Item teleport = Inventory.getFirst(ItemID.TELEPORT_CRYSTAL, ItemID.CORRUPTED_TELEPORT_CRYSTAL);
                if (ticksSinceLastInteraction() > 2)
                {
                    teleport.interact("Activate");
                    lastInteractionTick = client.getTickCount();
                }
            }
            else
            {
                if (ticksSinceLastInteraction() > 2)
                {
                    if (instanceGrid.getStartLocation().isInScene(client) || client.getLocalPlayer().getWorldLocation().distanceTo2D(instanceGrid.getStartLocation()) <= 20)
                    {
                        Movement.walk(instanceGrid.getStartLocation());
                    }
                    else
                    {
                        Movement.walkTo(instanceGrid.getStartLocation());
                    }
                    lastInteractionTick = client.getTickCount();
                }
            }
        }
        else
        {
            if ((state != GauntletState.GET_FISH && neededFish() > 0 && Inventory.contains(ItemID.RAW_PADDLEFISH)) || (state == GauntletState.GET_FISH && neededFish() == 0))
            {
                goToCookingRange();
            }
            else
            {
                if (client.getLocalPlayer().getWorldLocation().distanceTo2D(instanceGrid.getStartLocation()) > 5)
                {
                    if (ticksSinceLastInteraction() > 2)
                    {
                        Movement.walk(instanceGrid.getStartLocation());
                        lastInteractionTick = client.getTickCount();
                    }
                }
                else
                {
                    openCraftingMenu();
                }
            }
        }
    }

    private void goToCookingRange()
    {
        if (client.getLocalPlayer().getWorldLocation().distanceTo2D(instanceGrid.getCookingRangeLocation()) > 1)
        {
            if (ticksSinceLastInteraction() > 2)
            {
                Movement.walk(instanceGrid.getCookingRangeLocation());
                lastInteractionTick = client.getTickCount();
            }
        }
    }

    private void openCraftingMenu()
    {
        GameObject singingBowl = findNearestGameObject(ObjectID.SINGING_BOWL_36063, ObjectID.SINGING_BOWL_35966);
        if (singingBowl != null)
        {
            if (ticksSinceLastInteraction() > 5 && !isCraftingMenuOpen())
            {
                singingBowl.interact("Sing-crystal");
                lastInteractionTick = client.getTickCount();
            }
        }
    }

    private boolean makeBowFromCraftingMenu()
    {
        if (!needBowAndCanMakeOne())
        {
            return false;
        }

        if (ticksSinceLastCraft() > 2)
        {
            Keyboard.type("8");
            lastInteractionTick = client.getTickCount();
            lastCraftTick = client.getTickCount();
        }
        return true;
    }

    private boolean hasMaterialsForArmor()
    {
        return getBarkCount() > 0 && getOreCount() > 0 && getWoolCount() > 0 && (hasAtLeastOne(HELMS, true) ? getShardCount() >= 40 : getShardCount() >= 60);
    }

    private boolean makeArmorFromCraftingMenu()
    {
        if (!hasMaterialsForArmor())
        {
            return false;
        }

        boolean madeSomething = false;

        if (!hasAtLeastOne(HELMS, true))
        {
            if (ticksSinceLastCraft() > 2)
            {
                Keyboard.type("3");
                lastInteractionTick = client.getTickCount();
                lastCraftTick = client.getTickCount();
                madeSomething = true;
            }
        }

        if (!hasAtLeastOne(BODIES, true))
        {
            if (ticksSinceLastCraft() > 2)
            {
                Keyboard.type("4");
                lastInteractionTick = client.getTickCount();
                lastCraftTick = client.getTickCount();
                madeSomething = true;
            }
        }

        if (!hasAtLeastOne(LEGS, true))
        {
            if (ticksSinceLastCraft() > 2)
            {
                Keyboard.type("5");
                lastInteractionTick = client.getTickCount();
                lastCraftTick = client.getTickCount();
                madeSomething = true;
            }
        }

        return madeSomething;
    }

    private boolean makeAttunedBow()
    {
        if (!hasAtLeastOne(BASIC_BOW, true) || getShardCount() < 60)
        {
            return false;
        }

        if (ticksSinceLastCraft() > 2)
        {
            Keyboard.type("8");
            lastInteractionTick = client.getTickCount();
            lastCraftTick = client.getTickCount();
        }
        return true;
    }

    private boolean makeAttunedStaff()
    {
        if (!hasAtLeastOne(BASIC_STAFF, true) || getShardCount() < 60)
        {
            return false;
        }

        if (ticksSinceLastCraft() > 2)
        {
            Keyboard.type("7");
            lastInteractionTick = client.getTickCount();
            lastCraftTick = client.getTickCount();
        }
        return true;
    }

    private boolean makePerfectBow()
    {
        if (!hasAtLeastOne(ATTUNED_BOW, true) || !Inventory.contains(ItemID.CRYSTALLINE_BOWSTRING, ItemID.CORRUPTED_BOWSTRING))
        {
            return false;
        }

        if (ticksSinceLastCraft() > 2)
        {
            Keyboard.type("8");
            lastInteractionTick = client.getTickCount();
            lastCraftTick = client.getTickCount();
        }
        return true;
    }

    private boolean makePerfectStaff()
    {
        if (!hasAtLeastOne(ATTUNED_STAFF, true) || !Inventory.contains(ItemID.CRYSTAL_ORB, ItemID.CORRUPTED_ORB))
        {
            return false;
        }

        if (ticksSinceLastCraft() > 2)
        {
            Keyboard.type("7");
            lastInteractionTick = client.getTickCount();
            lastCraftTick = client.getTickCount();
        }
        return true;
    }

    private boolean makeBasicStaff()
    {
        if (!needStaffAndCanMakeOne())
        {
            return false;
        }

        if (ticksSinceLastCraft() > 2)
        {
            Keyboard.type("7");
            lastInteractionTick = client.getTickCount();
            lastCraftTick = client.getTickCount();
        }
        return false;
    }

    private boolean makeTeleport()
    {
        if (Inventory.contains(ItemID.TELEPORT_CRYSTAL, ItemID.CORRUPTED_TELEPORT_CRYSTAL) ||
                (!Inventory.contains(ItemID.TELEPORT_CRYSTAL, ItemID.CORRUPTED_TELEPORT_CRYSTAL) && getShardCount() < 60))
        {
            return false;
        }

        if (ticksSinceLastCraft() > 2)
        {
            Keyboard.type("1");
            lastInteractionTick = client.getTickCount();
            lastCraftTick = client.getTickCount();
        }
        return false;
    }

    private boolean makeVialFromCraftingMenu()
    {
        if (getShardCount() < 10 || neededVials() < 1)
        {
            return false;
        }

        if (ticksSinceLastCraft() > 2)
        {
            Keyboard.type("2");
            lastInteractionTick = client.getTickCount();
            lastCraftTick = client.getTickCount();
        }
        return true;
    }


    private boolean openNewRoomFirstRound()
    {
        MessageUtils.addMessage("Opening a new room");
        GauntletRoom gauntletRoom = instanceGrid.getNextUnlitRoomFirstPass();
        GameObject closestNode = findNodeForUnopenedRoom(gauntletRoom);
        if (closestNode != null && (ticksSinceLastInteraction() > 1 && !client.getLocalPlayer().isMoving()))
        {
            if (closestNode.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) > 20)
            {
                if (closestNode.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) > 25)
                {
                    Movement.walkTo(closestNode.getWorldLocation());
                }
                else
                {
                    Movement.walk(closestNode.getWorldLocation());
                }

            }
            else
            {
                if (closestNode.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) > GauntletInstanceGrid.ROOM_SIZE)
                {
                    Movement.walk(closestNode.getWorldLocation());
                }
                else
                {
                    closestNode.interact("Light");
                }
            }
            lastInteractionTick = client.getTickCount();
            return true;
        }
        return false;
    }

    private boolean openNewRoomSecondRound()
    {
        MessageUtils.addMessage("Opening a new room");
        GauntletRoom gauntletRoom = instanceGrid.getNextUnlitRoomSecondPass();
        GameObject closestNode = findNodeForUnopenedRoom(gauntletRoom);
        if (closestNode != null && ticksSinceLastInteraction() > 3)
        {
            if (closestNode.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) > 20)
            {
                if (closestNode.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) > 25)
                {
                    Movement.walkTo(closestNode.getWorldLocation());
                }
                else
                {
                    Movement.walk(closestNode.getWorldLocation());
                }

            }
            else
            {
                if (closestNode.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) > GauntletInstanceGrid.ROOM_SIZE)
                {
                    Movement.walk(closestNode.getWorldLocation());
                }
                else
                {
                    closestNode.interact("Light");
                }
            }
            lastInteractionTick = client.getTickCount();
            return true;
        }
        return false;
    }

    private boolean openNewRoomLastRound()
    {
        MessageUtils.addMessage("Opening a new room");
        GauntletRoom gauntletRoom = instanceGrid.getNextUnlitRoomLastPass();
        GameObject closestNode = findNodeForUnopenedRoom(gauntletRoom);
        if (closestNode != null && ticksSinceLastInteraction() > 3)
        {
            if (closestNode.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) > 20)
            {
                if (closestNode.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) > 25)
                {
                    Movement.walkTo(closestNode.getWorldLocation());
                }
                else
                {
                    Movement.walk(closestNode.getWorldLocation());
                }
            }
            else
            {
                if (closestNode.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) > GauntletInstanceGrid.ROOM_SIZE)
                {
                    Movement.walk(closestNode.getWorldLocation());
                }
                else
                {
                    closestNode.interact("Light");
                }
            }
            lastInteractionTick = client.getTickCount();
            return true;
        }
        return false;
    }

    private int getShardCount()
    {
        return Inventory.getCount(true, ItemID.CRYSTAL_SHARDS, ItemID.CORRUPTED_SHARDS);
    }

    private int getWoolCount()
    {
        return Inventory.getCount(item -> WOOL.contains(item.getId()));
    }

    private int getBarkCount()
    {
        return Inventory.getCount(item -> BARK.contains(item.getId()));
    }

    private int getOreCount()
    {
        return Inventory.getCount(item -> ORE.contains(item.getId()));
    }

    private boolean resourceExistsThatWeNeed()
    {
        GameObject woolSource = findNearestGameObject(gameObject -> WOOL_SOURCE.contains(gameObject.getId()));
        if (neededWool() > 0 && woolSource != null  && woolSource.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < 18)
        {
            MessageUtils.addMessage("Need the wool source");
            return true;
        }

        GameObject oreSource = findNearestGameObject(gameObject -> ORE_SOURCE.contains(gameObject.getId()));
        if (neededOre() > 0 && oreSource != null  && oreSource.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < 18)
        {
            MessageUtils.addMessage("Need the ore source");
            return true;
        }

        GameObject barkSource = findNearestGameObject(gameObject -> BARK_SOURCE.contains(gameObject.getId()));
        if (neededBark() > 0 && barkSource != null  && barkSource.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < 18)
        {
            MessageUtils.addMessage("Need the bark source");
            return true;
        }

        GameObject leafSource = findNearestGameObject(gameObject -> gameObject.getName().contains("Grym Root") && !gameObject.getName().contains("Depleted"));
        if (neededGrymLeaves() > 0 && leafSource != null  && leafSource.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < 18)
        {
            MessageUtils.addMessage("Need the leaf source");
            return true;
        }

        GameObject fishSource = findNearestGameObject(gameObject -> gameObject.getName().contains("Fishing Spot") && gameObject.hasAction("Fish"));
        if (neededFish() > 0 && fishSource != null && (hasAllResourcesExceptFish() || Inventory.getCount(ItemID.RAW_PADDLEFISH) < 8) && Inventory.getFreeSlots() > 5
                && fishSource.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < 20)
        {
            if (!hasBowString() && !hasAtLeastPerfectBow() && fishSource.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < GauntletInstanceGrid.ROOM_SIZE)
            {
                MessageUtils.addMessage("Need fish source");
                return true;
            }
            else if ((hasBowString() || hasAtLeastPerfectBow()))
            {
                MessageUtils.addMessage("Need fish source");
                return true;
            }
        }

        return false;
    }

    private void handlePrayerActivations()
    {
        if (currentProtectionPrayer == Prayer.INCREDIBLE_REFLEXES)
        {
            deactivatePrayers();
        }
        else
        {
            Prayer offensivePrayer = getPrayerBasedOnWeapon();
            if (Prayers.isEnabled(currentProtectionPrayer) && config.oneTickFlick())
            {
                togglePrayer(currentProtectionPrayer);

                if ((ticksSinceLastAttack() < 9 || inHunllef) && config.useOffensivePrayer())
                {
                    if (Prayers.isEnabled(offensivePrayer))
                    {
                        togglePrayer(offensivePrayer);
                        togglePrayer(offensivePrayer);
                    }
                    else
                    {
                        activatePrayer(offensivePrayer);
                    }
                }

                togglePrayer(currentProtectionPrayer);
            }
            else
            {
                activatePrayer(currentProtectionPrayer);

                if ((ticksSinceLastAttack() < 9 || inHunllef) && config.useOffensivePrayer())
                {
                    activatePrayer(offensivePrayer);
                }
            }
        }
    }

    private void dropTools()
    {
        if (neededOre() < 1)
        {
            if (Inventory.contains(ItemID.CORRUPTED_PICKAXE, ItemID.CRYSTAL_PICKAXE_23863) && ticksSinceLastInteraction() > 2)
            {
                Inventory.getFirst(ItemID.CORRUPTED_PICKAXE, ItemID.CRYSTAL_PICKAXE_23863).interact("Drop");
                lastInteractionTick = client.getTickCount();
            }
        }

        if (neededBark() < 1)
        {
            if (Inventory.contains(ItemID.CORRUPTED_AXE, ItemID.CRYSTAL_AXE_23862) && ticksSinceLastInteraction() > 2)
            {
                Inventory.getFirst(ItemID.CORRUPTED_AXE, ItemID.CRYSTAL_AXE_23862).interact("Drop");
                lastInteractionTick = client.getTickCount();
            }
        }

        if (neededFish() <= 0)
        {
            if (Inventory.contains(ItemID.CRYSTAL_HARPOON_23864, ItemID.CORRUPTED_HARPOON) && ticksSinceLastInteraction() > 2)
            {
                Inventory.getFirst(ItemID.CRYSTAL_HARPOON_23864, ItemID.CORRUPTED_HARPOON).interact("Drop");
                lastInteractionTick = client.getTickCount();
            }
        }
    }

    private void handleStartUp()
    {
        if (inLobby())
        {
            state = GauntletState.STARTING;
            return;
        }

        initGrid();

        if (instanceGrid.isInitialized())
        {
            if (hunllef.getNpc().getComposition().getOverheadIcon() == null)
            {
                return;
            }

            if (hunllef.getNpc().getComposition().getOverheadIcon() == HeadIcon.RANGED)
            {
                state = GauntletState.LEAVE_ITS_FUCKED;
                return;
            }

            //state = GauntletState.READY_TO_FIGHT; // TODO: Temporarily set this as the starting condition to test hunllef
            state = GauntletState.GET_BASIC_BOW;
        }
    }

    private void handleGettingBasicBow()
    {
        // Starting condition that needs satisfied
        if (hasAtLeastBasicBow() && neededVials() == 0)
        {
            state = GauntletState.GET_PERFECT_BOW;
            return;
        }

        if (handleHighPriorityTasks())
        {
            return;
        }

        if ((isCraftingMenuOpen() && ticksSinceLastCraft() < 6) || ticksSinceLastGather() < 8)
        {
            return;
        }

        if (hasMaterialsForBasicBowAndVials())
        {
            if (takeItemsOffGround())
            {
                return;
            }

            if (!isCraftingMenuOpen() && ticksSinceLastCraft() > 3)
            {
                goToSingingBowl(false);
            }
        }
        else
        {
            lootStuffAndOpenRooms();
        }
    }

    private boolean handleCrafting()
    {
        if (isCraftingMenuOpen())
        {
            if (makeBowFromCraftingMenu())
            {
                return true;
            }

            if (makeVialFromCraftingMenu())
            {
                return true;
            }

            if (makeAttunedBow())
            {
                return true;
            }

            if (makePerfectBow())
            {
                return true;
            }

            if (makeBasicStaff())
            {
                return true;
            }

            if (makeAttunedStaff())
            {
                return true;
            }

            if (makePerfectStaff())
            {
                return true;
            }

            if (makeArmorFromCraftingMenu())
            {
                return true;
            }

            if (makeTeleport())
            {
                return true;
            }
        }
        return false;
    }

    private void handleGettingPerfectBow()
    {
        if (hasAtLeastPerfectBow())
        {
            state = GauntletState.GET_PERFECT_STAFF;
            return;
        }

        if (handleHighPriorityTasks())
        {
            return;
        }

        if ((isCraftingMenuOpen() && ticksSinceLastCraft() < 6) || ticksSinceLastGather() < 8)
        {
            return;
        }

        if (takeBowString())
        {
            return;
        }

        if (takeOrb())
        {
            return;
        }

        if (hasMaterialsForPerfectBow())
        {
            if (takeItemsOffGround())
            {
                return;
            }

            if (!isCraftingMenuOpen() && ticksSinceLastCraft() > 3)
            {
                goToSingingBowl(true);
            }
        }
        else
        {
            lootStuffAndOpenRooms();
        }

    }

    private void handleGettingPerfectStaff()
    {
        if (hasAtLeastPerfectStaff())
        {
            state = GauntletState.GET_ARMOR;
            return;
        }

        if (handleHighPriorityTasks())
        {
            return;
        }

        if ((isCraftingMenuOpen() && ticksSinceLastCraft() < 6) || ticksSinceLastGather() < 8)
        {
            return;
        }

        if (takeBowString())
        {
            return;
        }

        if (takeOrb())
        {
            return;
        }

        if (hasMaterialsForPerfectStaff())
        {
            if (takeItemsOffGround())
            {
                return;
            }

            if (!isCraftingMenuOpen() && ticksSinceLastCraft() > 3)
            {
                goToSingingBowl(true);
            }
        }
        else
        {
            lootStuffAndOpenRooms();
        }

    }

    private void handleGettingArmor()
    {
        if (hasAtLeastOne(HELMS, true) && hasAtLeastOne(BODIES, true) && hasAtLeastOne(LEGS, true))
        {
            state = GauntletState.GET_FISH;
            return;
        }

        if (handleHighPriorityTasks())
        {
            return;
        }

        if ((isCraftingMenuOpen() && ticksSinceLastCraft() < 6) || ticksSinceLastGather() < 8)
        {
            return;
        }

        if (hasAllArmorResources())
        {
            if (takeItemsOffGround())
            {
                return;
            }

            if (ticksSinceLastGather() > 10 && ticksSinceLastCraft() > 5)
            {
                goToSingingBowl(true);
            }
        }
        else
        {
            lootStuffAndOpenRooms();
        }
    }

    private void handleGettingBasicStaff()
    {
        if (hasAtLeastOne(BASIC_STAFF, true))
        {
            state = GauntletState.GET_FISH;
            return;
        }

        if (handleHighPriorityTasks())
        {
            return;
        }

        if (isCraftingMenuOpen() && ticksSinceLastCraft() < 6)
        {
            return;
        }

        if (hasAtLeastOne(WEAPON_FRAMES, false) && getShardCount() >= 20)
        {
            goToSingingBowl(false);
        }
        else
        {
            lootStuffAndOpenRooms();
        }
    }

    private void handleGettingFish()
    {
        if (neededFish() <= 0 && instanceGrid.getCookingRangeLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < 2)
        {
            state = GauntletState.COOK_FISH;
            return;
        }

        if ((isCraftingMenuOpen() && ticksSinceLastCraft() < 6) || ticksSinceLastGather() < 8)
        {
            return;
        }

        if (handleHighPriorityTasks())
        {
            return;
        }

        if (neededFish() <= 0)
        {
            if (instanceGrid.getCookingRangeLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) > 1)
            {
                goToSingingBowl(true);
            }
        }
        else
        {
            lootStuffAndOpenRooms();
        }
    }

    private void handleCookingFish()
    {
        if (Inventory.getCount(ItemID.PADDLEFISH) >= config.foodAmt())
        {
            state = GauntletState.READY_TO_FIGHT;
            return;
        }

        if (inHunllef)
        {
            currentProtectionPrayer = Prayer.PROTECT_FROM_MISSILES;
            state = GauntletState.HUNLLEF;
            return;
        }

        if (handleHighPriorityTasks())
        {
            return;
        }

        TileItem fish = TileItems.getNearest(ItemID.RAW_PADDLEFISH);
        if (fish != null && fish.getWorldLocation().distanceTo2D(instanceGrid.getCookingRangeLocation()) < 4)
        {
            fish.interact("Take");
            return;
        }

        if (Inventory.getCount(ItemID.RAW_PADDLEFISH) > 0)
        {
            cookFish();
        }
    }

    private void handleReadyToFight()
    {
        if (inHunllef)
        {
            currentProtectionPrayer = Prayer.PROTECT_FROM_MISSILES;
            state = GauntletState.HUNLLEF;
            return;
        }

        GameObject barrier = findNearestGameObject(gameObject -> gameObject.getName().equals("Barrier"));

        if (barrier != null && ticksSinceLastInteraction() > 2)
        {
            barrier.interact("Quick-pass");
            lastInteractionTick = client.getTickCount();
        }

    }

    private void handleHunllef()
    {
        if (hunllef == null)
        {
            return;
        }

        hunllefTickUpdates();

        handlePrayerActivations();

        handleHunllefHighPriorityTasks();

        WorldPoint safeTile = getToSafeTile();

        attackHunllef(safeTile.distanceTo2D(client.getLocalPlayer().getWorldLocation()) > 2);

    }

    private void handleLeaving()
    {
        GameObject teleportPlatform = findNearestGameObject(gameObject -> gameObject.getName().equals("Teleport Platform"));
        if (teleportPlatform != null)
        {
            if (ticksSinceLastInteraction() > 4)
            {
                teleportPlatform.interact("Quick-exit");
                lastInteractionTick = client.getTickCount();
            }
        }
    }

    private void handleHunllefHighPriorityTasks()
    {
        if (client.getBoostedSkillLevel(Skill.HITPOINTS) <= config.healthMin())
        {
            eatToFull = true;
        }

        if (client.getBoostedSkillLevel(Skill.HITPOINTS) >= config.healthMax())
        {
            eatToFull = false;
        }

        if (eatToFull)
        {
            Item paddlefish = Inventory.getFirst(ItemID.PADDLEFISH);
            if (paddlefish != null)
            {
                if (ticksSinceLastFood() > 2)
                {
                    paddlefish.interact("Eat");
                    lastFoodTick = client.getTickCount();
                }
            }
            else
            {
                MessageUtils.addMessage("We're out of food, good luck us!");
                eatToFull = false;
            }
        }
        else
        {
            useEgniolPotion();
        }
    }

    private WorldPoint getToSafeTile()
    {
        WorldPoint safeTile = getClosestSafeTile();

        if (tileUnderUsUnsafe(false))
        {
            if (ticksSinceLastDodge() > 1 && safeTile != null && !safeTile.equals(client.getLocalPlayer().getWorldLocation()))
            {
                Movement.walk(safeTile);
                secondLastSafeTile = lastSafeTile;
                lastSafeTile = safeTile;
                lastDodgeTick = client.getTickCount();
                return safeTile;
            }
        }
        else if (tileUnderUsUnsafe(true))
        {
            if (ticksSinceLastDodge() > 1 || tooCloseToTornado(client.getLocalPlayer().getWorldLocation(), 3))
            {
                if (safeTile != null && !safeTile.equals(client.getLocalPlayer().getWorldLocation()))
                {
                    Movement.walk(safeTile);
                    secondLastSafeTile = lastSafeTile;
                    lastSafeTile = safeTile;
                    lastDodgeTick = client.getTickCount();
                    return safeTile;
                }
            }
        }
        return null;
    }

    private void attackHunllef(boolean delayAttack)
    {
        if (ticksSinceLastDodge() < (delayAttack ? 2 : 1) || ticksSinceLastFood() < 3 || ticksSinceLastAttack() < 4 || hunllef == null) //(Equipment.contains(ItemID.CRYSTAL_BOW_PERFECTED, ItemID.CORRUPTED_BOW_PERFECTED) ? 4 : 5)
        {
            return;
        }

        hunllef.getNpc().interact("Attack");
    }

    private boolean tileUnderUsUnsafe(boolean checkTornados)
    {
        WorldArea biggerArea = new WorldArea(hunllef.getNpc().getWorldLocation().dx(-2).dy(-2), hunllef.getNpc().getWorldLocation().dx(6).dy(6));
        boolean isTileSafe = new GroundObjectQuery()
                .idEquals(36047, 36048)
                .filter(groundObject -> groundObject.getWorldLocation().equals(client.getLocalPlayer().getWorldLocation()))
                .result(client)
                .stream().count() == 0;

        boolean underHunllef = isTileSafe ? biggerArea.contains(client.getLocalPlayer().getWorldLocation()) : hunllef.getNpc().getWorldArea().contains(client.getLocalPlayer());

        boolean tooCloseToTornados = tooCloseToTornado(client.getLocalPlayer().getWorldLocation(), 3);

        if (!isTileSafe)
        {
            MessageUtils.addMessage("Need to move from unsafe tile!");
        }

        if (underHunllef)
        {
            MessageUtils.addMessage("Need to get out from under the beast!");
        }

        if (tooCloseToTornados)
        {
            MessageUtils.addMessage("There's a tornado about to fuck us up");
        }

        return !isTileSafe || underHunllef || (checkTornados && tooCloseToTornados);
    }

    private boolean tooCloseToTornado(WorldPoint point, int range)
    {
        if (tornadoes.size() == 0)
        {
            return false;
        }

        for (Tornado t : tornadoes)
        {
            NPC tornado = t.getNpc();
            if (tornado.getWorldLocation().distanceTo2D(point) < range)
            {
                return true;
            }
        }
        return false;
    }

    private WorldPoint getClosestSafeTile()
    {
        WorldArea biggerArea = new WorldArea(hunllef.getNpc().getWorldLocation().dx(-2).dy(-2), hunllef.getNpc().getWorldLocation().dx(6).dy(6));

        GroundObject target1 = new GroundObjectQuery()
                .idEquals(36046)
                .filter(groundObject -> !biggerArea.contains(groundObject.getWorldLocation())
                        && !tooCloseToTornado(groundObject.getWorldLocation(), 5)
                        && groundObject.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) >= 1
                        && (groundObject.getWorldLocation().getX() == client.getLocalPlayer().getWorldLocation().getX() || groundObject.getWorldLocation().getY() == client.getLocalPlayer().getWorldLocation().getY())
                        && isInsideArena(groundObject.getWorldLocation())
                        && (lastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1
                        || secondLastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1)
                        && groundObject.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < 5)
                .result(client)
                .nearestTo(client.getLocalPlayer());

        GroundObject target2 = new GroundObjectQuery()
                .idEquals(36046)
                .filter(groundObject -> !biggerArea.contains(groundObject.getWorldLocation())
                        && !tooCloseToTornado(groundObject.getWorldLocation(), 4)
                        && groundObject.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) >= 1
                        && (groundObject.getWorldLocation().getX() == client.getLocalPlayer().getWorldLocation().getX() || groundObject.getWorldLocation().getY() == client.getLocalPlayer().getWorldLocation().getY())
                        && isInsideArena(groundObject.getWorldLocation())
                        && (lastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1
                        || secondLastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1)
                        && groundObject.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < 5)
                .result(client)
                .nearestTo(client.getLocalPlayer());

        GroundObject target3 = new GroundObjectQuery()
                .idEquals(36046)
                .filter(groundObject -> !biggerArea.contains(groundObject.getWorldLocation())
                        && !tooCloseToTornado(groundObject.getWorldLocation(), 3)
                        && groundObject.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) >= 1
                        && (groundObject.getWorldLocation().getX() == client.getLocalPlayer().getWorldLocation().getX() || groundObject.getWorldLocation().getY() == client.getLocalPlayer().getWorldLocation().getY())
                        && isInsideArena(groundObject.getWorldLocation())
                        && (lastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1
                        || secondLastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1)
                        && groundObject.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < 5)
                .result(client)
                .nearestTo(client.getLocalPlayer());

        GroundObject target4 = new GroundObjectQuery()
                .idEquals(36046)
                .filter(groundObject -> !biggerArea.contains(groundObject.getWorldLocation())
                        && !tooCloseToTornado(groundObject.getWorldLocation(), 2)
                        && groundObject.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) >= 1
                        && (groundObject.getWorldLocation().getX() == client.getLocalPlayer().getWorldLocation().getX() || groundObject.getWorldLocation().getY() == client.getLocalPlayer().getWorldLocation().getY())
                        && isInsideArena(groundObject.getWorldLocation())
                        && (lastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1
                        || secondLastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1)
                        && groundObject.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < 5)
                .result(client)
                .nearestTo(client.getLocalPlayer());


        GroundObject target5 = new GroundObjectQuery()
                .idEquals(36046)
                .filter(groundObject -> !biggerArea.contains(groundObject.getWorldLocation())
                        && !tooCloseToTornado(groundObject.getWorldLocation(), 5)
                        && groundObject.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) >= 1
                        && isInsideArena(groundObject.getWorldLocation())
                        && (lastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1
                        || secondLastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1)
                        && groundObject.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < 5)
                .result(client)
                .nearestTo(client.getLocalPlayer());

        GroundObject target6 = new GroundObjectQuery()
                .idEquals(36046)
                .filter(groundObject -> !biggerArea.contains(groundObject.getWorldLocation())
                        && !tooCloseToTornado(groundObject.getWorldLocation(), 4)
                        && groundObject.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) >= 1
                        && isInsideArena(groundObject.getWorldLocation())
                        && (lastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1
                        || secondLastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1)
                        && groundObject.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < 5)
                .result(client)
                .nearestTo(client.getLocalPlayer());

        GroundObject target7 = new GroundObjectQuery()
                .idEquals(36046)
                .filter(groundObject -> !biggerArea.contains(groundObject.getWorldLocation())
                        && !tooCloseToTornado(groundObject.getWorldLocation(), 3)
                        && groundObject.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) >= 1
                        && isInsideArena(groundObject.getWorldLocation())
                        && (lastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1
                        || secondLastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1)
                        && groundObject.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < 5)
                .result(client)
                .nearestTo(client.getLocalPlayer());

        GroundObject target8 = new GroundObjectQuery()
                .idEquals(36046)
                .filter(groundObject -> !biggerArea.contains(groundObject.getWorldLocation())
                        && !tooCloseToTornado(groundObject.getWorldLocation(), 2)
                        && groundObject.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) >= 1
                        && isInsideArena(groundObject.getWorldLocation())
                        && (lastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1
                        || secondLastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1)
                        && groundObject.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < 5)
                .result(client)
                .nearestTo(client.getLocalPlayer());

        GroundObject target9 = new GroundObjectQuery()
                .idEquals(36046)
                .filter(groundObject -> !hunllef.getNpc().getWorldArea().contains(groundObject.getWorldLocation())
                        && !tooCloseToTornado(groundObject.getWorldLocation(), 5)
                        && groundObject.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) >= 1
                        && isInsideArena(groundObject.getWorldLocation())
                        && (lastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1
                        || secondLastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1))
                .result(client)
                .nearestTo(client.getLocalPlayer());

        GroundObject target10 = new GroundObjectQuery()
                .idEquals(36046)
                .filter(groundObject -> !hunllef.getNpc().getWorldArea().contains(groundObject.getWorldLocation())
                        && !tooCloseToTornado(groundObject.getWorldLocation(), 4)
                        && groundObject.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) >= 1
                        && isInsideArena(groundObject.getWorldLocation())
                        && (lastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1
                        || secondLastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1))
                .result(client)
                .nearestTo(client.getLocalPlayer());

        GroundObject target11 = new GroundObjectQuery()
                .idEquals(36046)
                .filter(groundObject -> !hunllef.getNpc().getWorldArea().contains(groundObject.getWorldLocation())
                        && !tooCloseToTornado(groundObject.getWorldLocation(), 3)
                        && groundObject.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) >= 1
                        && isInsideArena(groundObject.getWorldLocation())
                        && (lastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1
                        || secondLastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1))
                .result(client)
                .nearestTo(client.getLocalPlayer());

        GroundObject target12 = new GroundObjectQuery()
                .idEquals(36046)
                .filter(groundObject -> !hunllef.getNpc().getWorldArea().contains(groundObject.getWorldLocation())
                        && !tooCloseToTornado(groundObject.getWorldLocation(), 2)
                        && groundObject.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) >= 1
                        && isInsideArena(groundObject.getWorldLocation())
                        && (lastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1
                        || secondLastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1))
                .result(client)
                .nearestTo(client.getLocalPlayer());


        if (target1 != null)
        {
            return target1.getWorldLocation();
        }
        else if (target2 != null)
        {
            return target2.getWorldLocation();
        }
        else if (target3 != null)
        {
            return target3.getWorldLocation();
        }
        else if (target4 != null)
        {
            return target4.getWorldLocation();
        }
        else if (target5 != null)
        {
            return target5.getWorldLocation();
        }
        else if (target6 != null)
        {
            return target6.getWorldLocation();
        }
        else if (target7 != null)
        {
            return target7.getWorldLocation();
        }
        else if (target8 != null)
        {
            return target8.getWorldLocation();
        }
        else if (target9 != null)
        {
            return target9.getWorldLocation();
        }
        else if (target10 != null)
        {
            return target10.getWorldLocation();
        }
        else if (target11 != null)
        {
            return target11.getWorldLocation();
        }
        else if (target12 != null)
        {
            return target12.getWorldLocation();
        }
        return null;
    }

    private boolean isInsideArena(WorldPoint point)
    {
        final int hunllefBaseX = instanceGrid.getRoom(3, 3).getBaseX();
        final int hunllefBaseY = instanceGrid.getRoom(3, 3).getBaseY();
        final WorldPoint arenaSouthWest = new WorldPoint(hunllefBaseX + 2, hunllefBaseY - 13, client.getLocalPlayer().getPlane());
        final WorldPoint arenaNorthEast = new WorldPoint(hunllefBaseX + 13, hunllefBaseY - 2, client.getLocalPlayer().getPlane());

        return new WorldArea(arenaSouthWest, arenaNorthEast).contains(point);
    }

    private void hunllefTickUpdates()
    {
        hunllef.decrementTicksUntilNextAttack();

        if (missile != null && missile.getProjectile().getRemainingCycles() <= 0)
        {
            missile = null;
        }

        if (!tornadoes.isEmpty())
        {
            tornadoes.forEach(Tornado::updateTimeLeft);
        }
    }

    private boolean handleHighPriorityTasks()
    {

        setPrayerAccordingToThreats();

        handlePrayerActivations();

        dropTools();

        useEgniolPotion();

        if (handleCrafting())
        {
            return true;
        }

        if (!isCraftingMenuOpen() || ticksSinceLastCraft() > 5)
        {
            if (makeDust())
            {
                return true;
            }

            if (equipBow())
            {
                return true;
            }

            if (equipHelm())
            {
                return true;
            }

            if (equipBody())
            {
                return true;
            }

            if (equipLegs())
            {
                return true;
            }
        }

        dropPestle();

        if (fillVials())
        {
            return true;
        }

        if (makeUnfinishedEgniols())
        {
            return true;
        }

        if (makeEgniols())
        {
            return true;
        }

        if (state != GauntletState.GET_FISH && state != GauntletState.COOK_FISH)
        {
            return dropPaddleFishInStartingArea();
        }

        return false;
    }

    private void lootStuffAndOpenRooms()
    {
        if (takeItemsOffGround())
        {
            return;
        }

        if (state != GauntletState.GET_FISH)
        {
            int shardsNeeded = (neededVials() * 10) + neededArmorShards() + (weaponFramesNeeded() * 20);

            if (weaponFramesNeeded() > 0 || shardsNeeded > getShardCount() || !hasAtLeastPerfectBow() || !hasAtLeastPerfectStaff())
            {
                if (fightWeakNpc())
                {
                    return;
                }

                if ((state == GauntletState.GET_PERFECT_BOW && !hasBowString()) || (state == GauntletState.GET_PERFECT_STAFF && !hasOrb()))
                {
                    NPC demiBoss = demiBossNearby();
                    if (demiBoss != null)
                    {
                        NPC meleeRange = meleeRangeNearby();
                        if (meleeRange != null && !demiBoss.getName().contains("Bear"))
                        {
                            meleeRange.interact("Attack");
                        }
                        else
                        {
                            attackDemiBoss(demiBoss);
                        }
                        return;
                    }
                }
                else
                {
                    if (fightStrongNpc())
                    {
                        return;
                    }
                }
            }
        }

        if (takeResources())
        {
            return;
        }

        if (state == GauntletState.GET_BASIC_BOW)
        {
            openNewRoomFirstRound();
        }
        else if (state == GauntletState.GET_PERFECT_BOW || state == GauntletState.GET_PERFECT_STAFF)
        {
            openNewRoomSecondRound();
        }
        else if (state == GauntletState.GET_ARMOR)
        {
            openNewRoomLastRound();
        }
        else
        {
            openNewRoomLastRound();
        }
    }


    private void dropPestle()
    {
        if (neededVials() < 1 && !needDust() && Inventory.contains("Pestle and mortar"))
        {
            if (ticksSinceLastInteraction() > 1)
            {
                Inventory.getFirst("Pestle and mortar").interact("Drop");
                lastInteractionTick = client.getTickCount();
            }
        }
    }

    private int neededWool()
    {
        int needed = 3;
        needed -= getWoolCount();

        if (hasAtLeastOne(HELMS, true))
        {
            needed--;
        }

        if (hasAtLeastOne(BODIES, true))
        {
            needed--;
        }

        if (hasAtLeastOne(LEGS, true))
        {
            needed--;
        }

        return needed;
    }

    private int neededOre()
    {
        int needed = 3;
        needed -= getOreCount();

        if (hasAtLeastOne(HELMS, true))
        {
            needed--;
        }

        if (hasAtLeastOne(BODIES, true))
        {
            needed--;
        }

        if (hasAtLeastOne(LEGS, true))
        {
            needed--;
        }

        return needed;
    }

    private int neededBark()
    {
        int needed = 3;
        needed -= getBarkCount();

        if (hasAtLeastOne(HELMS, true))
        {
            needed--;
        }

        if (hasAtLeastOne(BODIES, true))
        {
            needed--;
        }

        if (hasAtLeastOne(LEGS, true))
        {
            needed--;
        }

        return needed;
    }

    private int neededGrymLeaves()
    {
        int needed = config.potionAmt();

        int leafCount = Inventory.getCount(ItemID.GRYM_LEAF, ItemID.GRYM_LEAF_23875);
        int unfPotCount = Inventory.getCount(ItemID.GRYM_POTION_UNF);
        int egniols = Inventory.getCount(item -> item.getName().contains("Egniol potion"));
        needed -= leafCount;
        needed -= unfPotCount;
        needed -= egniols;
        return needed;
    }

    private int neededVials()
    {
        int needed = config.potionAmt();
        int vialCount = Inventory.getCount(ItemID.VIAL_23879, ItemID.VIAL_23839);
        int waterFilledVialCount = Inventory.getCount(ItemID.WATERFILLED_VIAL);
        int unfPotCount = Inventory.getCount(ItemID.GRYM_POTION_UNF);
        int egniols = Inventory.getCount(item -> item.getName().contains("Egniol potion"));
        needed -= vialCount;
        needed -= waterFilledVialCount;
        needed -= unfPotCount;
        needed -= egniols;
        return needed;
    }

    private int neededWaterFilledVials()
    {
        int needed = config.potionAmt();
        int vialCount = Inventory.getCount(ItemID.WATERFILLED_VIAL);
        int unfPotCount = Inventory.getCount(ItemID.GRYM_POTION_UNF);
        int egniols = Inventory.getCount(item -> item.getName().contains("Egniol potion"));
        needed -= vialCount;
        needed -= unfPotCount;
        needed -= egniols;
        return needed;
    }

    private int neededFish()
    {
        int needed = config.foodAmt();
        int rawFish = Inventory.getCount(ItemID.RAW_PADDLEFISH);
        int cookedFish = Inventory.getCount(ItemID.PADDLEFISH);
        needed -= rawFish;
        needed -= cookedFish;
        needed -= fishLeftOnGround;
        return needed;
    }

    private int neededUnfinishedEgniols()
    {
        int needed = config.potionAmt();
        int unfPotCount = Inventory.getCount(ItemID.GRYM_POTION_UNF);
        int egniols = Inventory.getCount(item -> item.getName().contains("Egniol potion"));
        needed -= unfPotCount;
        needed -= egniols;
        return needed;
    }

    private int neededEgniols()
    {
        int needed = config.potionAmt();
        int egniols = Inventory.getCount(item -> item.getName().contains("Egniol potion"));
        needed -= egniols;
        return needed;
    }

    private boolean waterSourceNearby()
    {
        GameObject waterSource = findNearestGameObject(object -> object.getName().contains("Water Pump"));
        if (waterSource != null && waterSource.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < 12)
        {
            return true;
        }
        return false;
    }
    private boolean fillVials()
    {
        if (waterSourceNearby() && neededWaterFilledVials() > 0 && hasAtLeastOne(List.of(ItemID.VIAL_23879, ItemID.VIAL_23839), false))
        {
            GameObject waterSource = findNearestGameObject(object -> object.getName().contains("Water Pump"));
            if (waterSource != null)
            {
                if (ticksSinceLastInteraction() > 3)
                {
                    waterSource.interact("Fill-from");
                    lastInteractionTick = client.getTickCount();
                }
                return true;
            }
        }
        return false;
    }

    private boolean cookFish()
    {
        GameObject range = findNearestGameObject(object -> object.getName().equals("Range"));

        if (range != null)
        {
            if (ticksSinceLastInteraction() > 3)
            {
                range.interact("Cook");
                lastInteractionTick = client.getTickCount();
            }
            return true;
        }
        return false;

    }

    private boolean makeUnfinishedEgniols()
    {
        if (neededUnfinishedEgniols() > 0 && Inventory.contains(ItemID.WATERFILLED_VIAL) && hasAtLeastOne(List.of(ItemID.GRYM_LEAF, ItemID.GRYM_LEAF_23875), false))
        {
            Item waterFilledVial = Inventory.getFirst(ItemID.WATERFILLED_VIAL);
            Item grymLeaf = Inventory.getFirst(ItemID.GRYM_LEAF, ItemID.GRYM_LEAF_23875);
            if (waterFilledVial != null && grymLeaf != null)
            {
                if (ticksSinceLastInteraction() > 2)
                {
                    waterFilledVial.useOn(grymLeaf);
                    lastInteractionTick = client.getTickCount();
                }
                return true;
            }
        }
        return false;
    }

    private boolean makeEgniols()
    {
        if (neededEgniols() > 0 && Inventory.contains(ItemID.GRYM_POTION_UNF) && Inventory.getCount(true, ItemID.CRYSTAL_DUST_23867, ItemID.CORRUPTED_DUST) >= 10)
        {
            Item unfPotion = Inventory.getFirst(ItemID.GRYM_POTION_UNF);
            Item dust = Inventory.getFirst(ItemID.CRYSTAL_DUST_23867, ItemID.CORRUPTED_DUST);
            if (unfPotion != null && dust != null)
            {
                if (ticksSinceLastInteraction() > 2)
                {
                    unfPotion.useOn(dust);
                    lastInteractionTick = client.getTickCount();
                }
                return true;
            }
        }
        return false;
    }

    private boolean needDust()
    {
        int needed = config.potionAmt() * 10;
        int dustCount = Inventory.getCount(true, ItemID.CRYSTAL_DUST_23867, ItemID.CORRUPTED_DUST);
        int egniols = Inventory.getCount(item -> item.getName().contains("Egniol potion"));
        needed -= dustCount;
        needed -= (egniols * 10);
        return needed > 0;
    }

    private boolean makeDust()
    {
        if (neededVials() < 1 && needDust() && getShardCount() >= 10)
        {
            Item pestle = Inventory.getFirst("Pestle and mortar");
            Item shard = Inventory.getFirst(ItemID.CRYSTAL_SHARDS, ItemID.CORRUPTED_SHARDS);
            if (pestle != null && shard != null)
            {
                if (ticksSinceLastInteraction() > 1)
                {
                    pestle.useOn(shard);
                    lastInteractionTick = client.getTickCount();
                }
                return true;
            }
        }
        return false;
    }

    private void takeNeededResource()
    {
        List<GameObject> neededResources = new ArrayList<>();

        MessageUtils.addMessage("Taking needed resources");
        GameObject woolSource = findNearestGameObject(gameObject -> WOOL_SOURCE.contains(gameObject.getId()));
        if (neededWool() > 0 && woolSource != null  && woolSource.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < 18)
        {
            neededResources.add(woolSource);
        }

        GameObject oreSource = findNearestGameObject(gameObject -> ORE_SOURCE.contains(gameObject.getId()));
        if (neededOre() > 0 && oreSource != null  && oreSource.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < 18)
        {
            neededResources.add(oreSource);
        }

        GameObject barkSource = findNearestGameObject(gameObject -> BARK_SOURCE.contains(gameObject.getId()));
        if (neededBark() > 0 && barkSource != null  && barkSource.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < 18)
        {
            neededResources.add(barkSource);
        }

        GameObject leafSource = findNearestGameObject(gameObject -> gameObject.getName().contains("Grym Root") && !gameObject.getName().contains("Depleted"));
        if (neededGrymLeaves() > 0 && leafSource != null  && leafSource.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < 18)
        {
            neededResources.add(leafSource);
        }

        GameObject fishSource = findNearestGameObject(gameObject -> gameObject.getName().contains("Fishing Spot") && gameObject.hasAction("Fish"));
        if (neededFish() > 0 && fishSource != null && (hasAllResourcesExceptFish() || Inventory.getCount(ItemID.RAW_PADDLEFISH) < 8) && Inventory.getFreeSlots() > 5
                && fishSource.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < 20)
        {
            neededResources.add(fishSource);
        }

        boolean droppedFish = droppedPaddleFishForRoom();
        if (!droppedFish && Inventory.getFreeSlots() == 0)
        {
            return;
        }

        GameObject closestResource = closestResourceFromList(neededResources);
        if (closestResource != null)
        {
            if (canTakeResource())
            {
                if (closestResource.hasAction("Pick"))
                {
                    closestResource.interact("Pick");
                }

                if (closestResource.hasAction("Chop"))
                {
                    closestResource.interact("Chop");
                }

                if (closestResource.hasAction("Mine"))
                {
                    closestResource.interact("Mine");
                }

                if (closestResource.hasAction("Fish") && !droppedFish)
                {
                    closestResource.interact("Fish");
                }

                lastInteractionTick = client.getTickCount();
            }
        }

    }

    private GameObject closestResourceFromList(List<GameObject> objects)
    {
        return new GameObjectQuery()
                .filter(objects::contains)
                .result(client)
                .nearestTo(client.getLocalPlayer());
    }

    private boolean hasAtLeastOne(List<Integer> ids, boolean searchEquipment)
    {
        return inventoryContainsAtLeastOne(ids) || (searchEquipment && equipmentContainsAtLeastOne(ids));
    }

    private boolean inventoryContainsAtLeastOne(List<Integer> ids)
    {
        return Inventory.getFirst(item -> ids.contains(item.getId())) != null;
    }

    private boolean equipmentContainsAtLeastOne(List<Integer> ids)
    {
        return Equipment.getFirst(item -> ids.contains(item.getId())) != null;
    }

    private int weaponFramesNeeded()
    {
        int needed = 2;

        if (Equipment.contains(item -> PERFECTED_BOW.contains(item.getId())))
        {
            needed--;
        }

        if (Inventory.contains(item -> PERFECTED_BOW.contains(item.getId())))
        {
            needed--;
        }

        if (Equipment.contains(item -> ATTUNED_BOW.contains(item.getId())))
        {
            needed--;
        }

        if (Inventory.contains(item -> ATTUNED_BOW.contains(item.getId())))
        {
            needed--;
        }

        if (Equipment.contains(item -> BASIC_BOW.contains(item.getId())))
        {
            needed--;
        }

        if (Inventory.contains(item -> BASIC_BOW.contains(item.getId())))
        {
            needed--;
        }

        if (Equipment.contains(item -> BASIC_STAFF.contains(item.getId())))
        {
            needed--;
        }

        if (Inventory.contains(item -> BASIC_STAFF.contains(item.getId())))
        {
            needed--;
        }

        if (Equipment.contains(item -> ATTUNED_STAFF.contains(item.getId())))
        {
            needed--;
        }

        if (Inventory.contains(item -> ATTUNED_STAFF.contains(item.getId())))
        {
            needed--;
        }

        if (Equipment.contains(item -> PERFECTED_STAFF.contains(item.getId())))
        {
            needed--;
        }

        if (Inventory.contains(item -> PERFECTED_STAFF.contains(item.getId())))
        {
            needed--;
        }

        List<Item> frames = Inventory.getAll(item -> WEAPON_FRAMES.contains(item.getId()));
        if (frames != null)
        {
            needed -= frames.size();
        }

        if (needed < 0)
        {
            needed = 0;
        }

        return needed;
    }

    private boolean setPrayerAccordingToThreats()
    {
        NPC lowThreat = NPCs.getNearest(npc -> WEAK_NPC_IDS.contains(npc.getId()));
        NPC mediumThreat = NPCs.getNearest(npc -> STRONG_NPC_IDS.contains(npc.getId()));
        NPC dragon = NPCs.getNearest(NpcID.CRYSTALLINE_DRAGON, NpcID.CORRUPTED_DRAGON);
        NPC darkBeast = NPCs.getNearest(NpcID.CRYSTALLINE_DARK_BEAST, NpcID.CORRUPTED_DARK_BEAST);
        NPC bear = NPCs.getNearest(NpcID.CRYSTALLINE_BEAR, NpcID.CORRUPTED_BEAR);

        if (dragon != null && dragon.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < GauntletInstanceGrid.ROOM_SIZE)
        {
            currentProtectionPrayer = Prayer.PROTECT_FROM_MAGIC;
            return true;
        }
        else if (darkBeast != null && darkBeast.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < GauntletInstanceGrid.ROOM_SIZE)
        {
            currentProtectionPrayer = Prayer.PROTECT_FROM_MISSILES;
            return true;
        }
        else if (bear != null && bear.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < 5 && bear.getWorldArea().hasLineOfSightTo(client, client.getLocalPlayer().getWorldLocation()))
        {
            currentProtectionPrayer = Prayer.PROTECT_FROM_MELEE;
            return true;
        }
        else if (mediumThreat != null && mediumThreat.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < 5 &&
                mediumThreat.getWorldArea().hasLineOfSightTo(client, client.getLocalPlayer().getWorldArea()))
        {
            currentProtectionPrayer = Prayer.PROTECT_FROM_MELEE;
            return true;
        }
        else if (lowThreat != null && lowThreat.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < 5 &&
                lowThreat.getWorldArea().hasLineOfSightTo(client, client.getLocalPlayer().getWorldArea()))
        {
            currentProtectionPrayer = Prayer.PROTECT_FROM_MELEE;
            return true;
        }
        currentProtectionPrayer = Prayer.INCREDIBLE_REFLEXES;
        return false;
    }
}