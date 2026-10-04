package id.rivai.sweep;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.plugin.PluginBase;

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
        if (!player.getInventory().getItemInHand().isSword()) return;

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
