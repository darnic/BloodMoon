package uk.co.jacekk.bukkit.bloodmoon.nms;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.minecraft.server.v1_4_R1.Entity;
import net.minecraft.server.v1_4_R1.EntityHuman;
import net.minecraft.server.v1_4_R1.EntityLiving;
import net.minecraft.server.v1_4_R1.IEntitySelector;
import net.minecraft.server.v1_4_R1.PathfinderGoalTarget;
import uk.co.jacekk.bukkit.baseplugin.v9_1.config.PluginConfig;
import uk.co.jacekk.bukkit.bloodmoon.BloodMoon;
import uk.co.jacekk.bukkit.bloodmoon.Config;

public class PathfinderGoalNearestAttackableTarget extends PathfinderGoalTarget {
	
	private BloodMoon plugin;
	
	private EntityLiving entity;
	private Class<?> targetType;
	private int c;
	private DistanceComparator comparator;
	private IEntitySelector entitySelctor;
	
	public PathfinderGoalNearestAttackableTarget(BloodMoon plugin, EntityLiving entity, Class<?> targetType, float distance, int i, boolean flag, boolean flag1, IEntitySelector entitySelector){
		super(entity, distance, flag, flag1);
		
		this.plugin = plugin;
		
		this.entity = entity;
		this.targetType = targetType;
		this.e = distance;
		this.c = i;
		this.comparator = new DistanceComparator(entity);
		this.entitySelctor = entitySelector;
	}
	
	public PathfinderGoalNearestAttackableTarget(BloodMoon plugin, EntityLiving entity, Class<?> oclass, float distance, int i, boolean flag, boolean flag1){
		this(plugin, entity, oclass, distance, i, flag, flag1, null);
	}
	
	public PathfinderGoalNearestAttackableTarget(BloodMoon plugin, EntityLiving entity, Class<?> oclass, float distance, int i, boolean flag){
		this(plugin, entity, oclass, distance, i, flag, false, null);
	}
	
	@Override
	public boolean a(){
		String worldName = this.entity.world.worldData.getName();
		String entityName = this.entity.getBukkitEntity().getType().name().toUpperCase();
		PluginConfig worldConfig = plugin.getConfig(worldName);
		
		float distance = this.e;
		
		if (plugin.isActive(worldName) && worldConfig.getBoolean(Config.FEATURE_TARGET_DISTANCE_ENABLED) && worldConfig.getStringList(Config.FEATURE_TARGET_DISTANCE_MOBS).contains(entityName)){
			distance *= worldConfig.getInt(Config.FEATURE_TARGET_DISTANCE_MULTIPLIER);
		}
		
		if (this.c > 0 && this.d.aB().nextInt(this.c) != 0){
			return false;
		}
		
		if (this.targetType == EntityHuman.class){
			EntityHuman entityhuman = this.d.world.findNearbyVulnerablePlayer(this.d, distance);
			
			if (this.a(entityhuman, false)){
				this.entity = entityhuman;
				return true;
			}
		}else{
			List<Entity> list = this.d.world.a(this.targetType, this.d.boundingBox.grow(distance, 4.0D, distance));
			
			Collections.sort(list, this.comparator);
			Iterator<Entity> iterator = list.iterator();
			
			while (iterator.hasNext()){
				Entity entity = (Entity) iterator.next();
				EntityLiving entityliving = (EntityLiving) entity;
				
				if (this.a(entityliving, false)){
					this.entity = entityliving;
					return true;
				}
			}
		}
		
		return false;
	}
	
	@Override
	public void c(){
		this.d.setGoalTarget(this.entity);
		super.c();
	}
	
}