package net.unethicalite.plugins.lucidanimationprayers;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.runelite.api.Prayer;

@Data
@AllArgsConstructor
public class AnimationPrayer
{

    private Prayer prayerToActivate;

    private int tickDelay;
}
