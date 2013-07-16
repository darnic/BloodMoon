package uk.co.jacekk.bukkit.bloodmoon.nms;

import net.minecraft.server.v1_6_R2.World;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_6_R2.CraftServer;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftGiant;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftLivingEntity;
import org.bukkit.plugin.Plugin;

import uk.co.jacekk.bukkit.bloodmoon.BloodMoon;
import uk.co.jacekk.bukkit.bloodmoon.entity.BloodMoonEntityGiantZombie;
import uk.co.jacekk.bukkit.bloodmoon.entity.BloodMoonEntityType;

public class EntityGiantZombie extends net.minecraft.server.v1_6_R2.EntityGiantZombie {
	
	private BloodMoon plugin;
	private BloodMoonEntityGiantZombie bloodMoonEntity;
	
	public EntityGiantZombie(World world){
		super(world);
		
		Plugin plugin = Bukkit.getPluginManager().getPlugin("BloodMoon");
		
		if (plugin == null || !(plugin instanceof BloodMoon)){
			this.world.removeEntity(this);
			return;
		}
		
		this.plugin = (BloodMoon) plugin;
		
		this.bukkitEntity = new CraftGiant((CraftServer) this.plugin.server, this);
		this.bloodMoonEntity = new BloodMoonEntityGiantZombie(this.plugin, this, (CraftLivingEntity) this.bukkitEntity, BloodMoonEntityType.GIANT_ZOMBIE);
	}
	
	@Override
	public void bj(){
		try{
			this.bloodMoonEntity.onTick();
			super.bj();
		}catch (Exception e){
			plugin.log.warn("Exception caught while ticking entity");
			e.printStackTrace();
		}
	}
	
}
