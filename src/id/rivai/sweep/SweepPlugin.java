package id.rivai.sweep;

import org.powernukkitx.Player;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityLiving;
import org.powernukkitx.event.EventHandler;
import org.powernukkitx.event.EventPriority;
import org.powernukkitx.event.Listener;
import org.powernukkitx.event.entity.EntityDamageByEntityEvent;
import org.powernukkitx.event.entity.EntityDamageEvent.DamageCause;
import org.powernukkitx.plugin.PluginBase;

public class SweepPlugin extends PluginBase implements Listener {

    // Ubah sesuai selera
    private static final double RADIUS = 1.5;      // jangkauan sweep (blok)
    private static final float SWEEP_DAMAGE = 2f;  // 2 = 1 hati

    // Mencegah sweep memicu sweep lagi (loop tak berujung)
    private boolean sweeping = false;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("SweepAttack aktif");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent e) {
        if (sweeping) return;
        if (e.getCause() != DamageCause.ENTITY_ATTACK) return;
        if (!(e.getDamager() instanceof Player player)) return;
        var inv = player.getInventory();
var item = inv.getItem(inv.getHeldItemIndex());
if (item == null || !item.getId().endsWith("_sword")) return;

        Entity target = e.getEntity();
        sweeping = true;
        try {
            for (Entity near : target.getLevel().getNearbyEntities(
                    target.getBoundingBox().grow(RADIUS, 0.25, RADIUS), target)) {
                if (near == player || near == target) continue;
                if (!(near instanceof EntityLiving)) continue;
                near.attack(new EntityDamageByEntityEvent(
                        player, near, DamageCause.ENTITY_ATTACK, SWEEP_DAMAGE));
            }
        } finally {
            sweeping = false;
        }
    }
}
