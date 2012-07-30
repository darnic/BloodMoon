package uk.co.jacekk.bukkit.bloodmoon.entities;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.util.UnsafeList;
import org.bukkit.entity.Skeleton;
import org.bukkit.plugin.Plugin;

import uk.co.jacekk.bukkit.bloodmoon.BloodMoon;
import uk.co.jacekk.bukkit.bloodmoon.Config;
import uk.co.jacekk.bukkit.bloodmoon.events.SkeletonMoveEvent;
import uk.co.jacekk.bukkit.bloodmoon.pathfinders.BloodMoonNavigation;
import uk.co.jacekk.bukkit.bloodmoon.pathfinders.BloodMoonPathfinderGoalArrowAttack;
import uk.co.jacekk.bukkit.bloodmoon.pathfinders.BloodMoonPathfinderGoalNearestAttackableTarget;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.PathfinderGoal;
import net.minecraft.server.PathfinderGoalArrowAttack;
import net.minecraft.server.PathfinderGoalFleeSun;
import net.minecraft.server.PathfinderGoalFloat;
import net.minecraft.server.PathfinderGoalHurtByTarget;
import net.minecraft.server.PathfinderGoalLookAtPlayer;
import net.minecraft.server.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.PathfinderGoalRandomLookaround;
import net.minecraft.server.PathfinderGoalRandomStroll;
import net.minecraft.server.PathfinderGoalRestrictSun;
import net.minecraft.server.World;

public class BloodMoonEntitySkeleton extends net.minecraft.server.EntitySkeleton {
	
	private BloodMoon plugin;
	
	@SuppressWarnings("unchecked")
	public BloodMoonEntitySkeleton(World world, Plugin plugin){
		super(world);
		
		if (plugin == null || !(plugin instanceof BloodMoon)){
			this.world.removeEntity(this);
			return;
		}
		
		this.plugin = (BloodMoon) plugin;
		
		if (this.plugin.config.getBoolean(Config.FEATURE_MOVEMENT_SPEED_ENABLED) && this.plugin.config.getStringList(Config.FEATURE_MOVEMENT_SPEED_MOBS).contains("SKELETON")){
			try{
				Field navigation = EntityLiving.class.getDeclaredField("navigation");
				navigation.setAccessible(true);
				navigation.set(this, new BloodMoonNavigation(this.plugin, this, this.world, 16.0f));
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		
		try{
			Field goala = this.goalSelector.getClass().getDeclaredField("a");
			goala.setAccessible(true);
			((UnsafeList<PathfinderGoal>) goala.get(this.goalSelector)).clear();
			
			Field targeta = this.targetSelector.getClass().getDeclaredField("a");
			targeta.setAccessible(true);
			((UnsafeList<PathfinderGoal>) targeta.get(this.targetSelector)).clear();
			
	        this.goalSelector.a(1, new PathfinderGoalFloat(this));
	        this.goalSelector.a(2, new PathfinderGoalRestrictSun(this));
	        this.goalSelector.a(3, new PathfinderGoalFleeSun(this, this.bb));
	        
	        if (this.plugin.config.getBoolean(Config.FEATURE_ARROW_RATE_ENABLED)){
	        	this.goalSelector.a(4, new BloodMoonPathfinderGoalArrowAttack(this, this.plugin, this.bb, 60));
	        }else{
	        	this.goalSelector.a(4, new PathfinderGoalArrowAttack(this, this.bb, 1, 60));
	        }
	        
	        this.goalSelector.a(5, new PathfinderGoalRandomStroll(this, this.bb));
	        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
	        this.goalSelector.a(6, new PathfinderGoalRandomLookaround(this));
	        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, false));
	        
	        if (this.plugin.config.getBoolean(Config.FEATURE_TARGET_DISTANCE_ENABLED) && this.plugin.config.getStringList(Config.FEATURE_TARGET_DISTANCE_MOBS).contains("SKELETON")){
	        	this.targetSelector.a(2, new BloodMoonPathfinderGoalNearestAttackableTarget(this.plugin, this, EntityHuman.class, 16.0F, 0, true));
	        }else{
	        	this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, 16.0F, 0, true));
	        }
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public BloodMoonEntitySkeleton(World world){
		this(world, Bukkit.getPluginManager().getPlugin("BloodMoon"));
	}
	
	@Override
	public void F_(){
		Skeleton skeleton = (Skeleton) this.getBukkitEntity();
		
		Location from = new Location(skeleton.getWorld(), this.lastX, this.lastY, this.lastZ, this.lastYaw, this.lastPitch);
		Location to = new Location(skeleton.getWorld(), this.locX, this.locY, this.locZ, this.yaw, this.pitch);
		
		SkeletonMoveEvent event = new SkeletonMoveEvent(skeleton, from, to);
		
		this.world.getServer().getPluginManager().callEvent(event);
		
		if (event.isCancelled() && !skeleton.isDead()){
			return;
		}
		
		super.F_();
	}
	
	@Override
	protected Entity findTarget(){
		float distance = (plugin.isActive(this.world.worldData.name) && plugin.config.getStringList(Config.FEATURE_TARGET_DISTANCE_MOBS).contains("SKELETON")) ? plugin.config.getInt(Config.FEATURE_TARGET_DISTANCE_MULTIPLIER) * 16.0f : 16.0f;
		
		EntityHuman entityhuman = this.world.findNearbyVulnerablePlayer(this, (double) distance);
		
		return entityhuman != null && this.h(entityhuman) ? entityhuman : null;
	}

}
