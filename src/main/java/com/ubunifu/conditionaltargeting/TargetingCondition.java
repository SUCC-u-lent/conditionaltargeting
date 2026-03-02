package com.ubunifu.conditionaltargeting;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.warden.AngerState;
import net.minecraft.entity.mob.warden.WardenEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.function.BiPredicate;

/**
 * A class representing a targeting condition, which can be evaluated against a target and source entity.
 * This class provides a set of common conditions that can be used for targeting logic, as well as the ability to create custom conditions.
 * @implNote When implementing a custom condition always consider it from the perspective of the source entity. e.g for {@link #IS_VEHICLE} the question is "Is the source entity a vehicle for the target?" not "Is the target entity a vehicle for the source?"
 */
public final class TargetingCondition
{
	// ===== Entity Validity Conditions =====
	/** This condition can be summarized as: "Is the target not null?" */
	public static final TargetingCondition IS_TARGET_NOT_NULL = create((target, source) -> target != null);
	/** This condition can be summarized as: "Is the source not null?" */
	public static final TargetingCondition IS_SOURCE_NOT_NULL = create((target, source) -> target != null);
	// ===== Entity Type Conditions =====
	/** This condition can be summarized as: "Is the target a player?" */
	public static final TargetingCondition IS_PLAYER = create((target, source) -> target instanceof PlayerEntity);
	/** This condition can be summarized as: "Is the target not a player?" */
	public static final TargetingCondition IS_NOT_PLAYER = create((target, source) -> !(target instanceof PlayerEntity));
	/** This condition can be summarized as: "Is the target a hostile mob?" */
	public static final TargetingCondition IS_HOSTILE = create((target, source) -> target instanceof HostileEntity);
	/** This condition can be summarized as: "Is the target not a hostile mob?" */
	public static final TargetingCondition IS_NOT_HOSTILE = create((target, source) -> !(target instanceof HostileEntity));
	/** This condition can be summarized as: "Is the target a passive entity?" */
	public static final TargetingCondition IS_PASSIVE = create((target, source) -> target instanceof PassiveEntity);

	// ===== Harming and Harmer Conditions =====
	/** This condition can be summarized as: "Is the source the attacker of the target?" */
	public static final TargetingCondition IS_ATTACKER = create((target, source) -> source instanceof LivingEntity living && living.getAttacker() != null && living.getAttacker().equals(target));
	/** This condition can be summarized as: "Is the source not the attacker of the target?" */
	public static final TargetingCondition IS_NOT_ATTACKER = create((target, source) -> !(source instanceof LivingEntity living) || living.getAttacker() == null || !living.getAttacker().equals(target));
	/** This condition can be summarized as: "Is the target the target of the source?" */
	public static final TargetingCondition IS_TARGETING = create((target, source) -> source instanceof MobEntity living && living.getTarget() != null && living.getTarget().equals(target));
	/** This condition can be summarized as: "Is the target not the target of the source?" */
	public static final TargetingCondition IS_NOT_TARGETING = create((target, source) -> !(source instanceof MobEntity living) || living.getTarget() == null || !living.getTarget().equals(target));
	/** This condition can be summarized as: "Is the source attacking the target?" */
	public static final TargetingCondition IS_ATTACKING = create((target, source) -> source instanceof LivingEntity living && living.getAttacking() != null && living.getAttacking().equals(target));
	/** This condition can be summarized as: "Is the source not attacking the target?" */
	public static final TargetingCondition IS_NOT_ATTACKING = create((target, source) -> !(source instanceof LivingEntity living) || living.getAttacking() == null || !living.getAttacking().equals(target));

	// ===== Warden Conditions =====
	/** This condition can be summarized as: "Is the source a warden?" */
	public static final TargetingCondition IS_WARDEN = create((target, source) -> source instanceof WardenEntity);
	/** This condition can be summarized as: "Is the source not a warden?" */
	public static final TargetingCondition IS_NOT_WARDEN = create((target, source) -> !(source instanceof WardenEntity));
	/** This condition can be summarized as: "Is the source a warden that is angry at the target?" */
	public static final TargetingCondition IS_WARDEN_ANGRY_AT = create((target, source) -> source instanceof WardenEntity warden && target instanceof PlayerEntity plr && warden.isAngryAt(plr));
	/** This condition can be summarized as: "Is the source not a warden that is angry at the target?" */
	public static final TargetingCondition IS_WARDEN_NOT_ANGRY_AT = create((target, source) -> !(source instanceof WardenEntity warden) || !(target instanceof PlayerEntity plr) || !warden.isAngryAt(plr));
	/** This condition can be summarized as: "Is the source a warden that is currently in the angry state?" */
	public static final TargetingCondition IS_WARDEN_ANGRY = create((target, source) -> source instanceof WardenEntity warden && warden.getAngerState() == AngerState.ANGRY);
	/** This condition can be summarized as: "Is the source a warden that is currently in the agitated state?" */
	public static final TargetingCondition IS_WARDEN_AGITATED = create((target, source) -> source instanceof WardenEntity warden && warden.getAngerState() == AngerState.AGITATED);
	/** This condition can be summarized as: "Is the source a warden that is currently in the calm state?" */
	public static final TargetingCondition IS_WARDEN_CALM = create((target, source) -> source instanceof WardenEntity warden && warden.getAngerState() == AngerState.CALM);

	// ===== Team and Relationship Conditions =====
	/** This condition can be summarized as: "Is the source a teammate of the target?" */
	public static final TargetingCondition IS_ALLY = create((target, source) -> source.isTeammate(target));
	/** This condition can be summarized as: "Is the source not a teammate of the target?" */
	public static final TargetingCondition IS_NOT_ALLY = create((target, source) -> !source.isTeammate(target));
	/** This condition can be summarized as: "Is the target the same entity type as the source?" */
	public static final TargetingCondition IS_SAME_TYPE = create((target, source) -> target.getClass() == source.getClass());
	/** This condition can be summarized as: "Is the target a different entity type than the source?" */
	public static final TargetingCondition IS_DIFFERENT_TYPE = create((target, source) -> target.getClass() != source.getClass());

	// ===== Entity Group Conditions =====
	/** This condition can be summarized as: "Does the target belong to the undead entity group?" */
	public static final TargetingCondition IS_UNDEAD = create((target, source) -> target instanceof LivingEntity living && living.getGroup() == EntityGroup.UNDEAD);
	/** This condition can be summarized as: "Does the target belong to the aquatic entity group?" */
	public static final TargetingCondition IS_AQUATIC = create((target, source) -> target instanceof LivingEntity living && living.getGroup() == EntityGroup.AQUATIC);
	/** This condition can be summarized as: "Does the target belong to the arthropod entity group?" */
	public static final TargetingCondition IS_ARTHROPOD = create((target, source) -> target instanceof LivingEntity living && living.getGroup() == EntityGroup.ARTHROPOD);
	/** This condition can be summarized as: "Does the target belong to the illager entity group?" */
	public static final TargetingCondition IS_ILLAGER = create((target, source) -> target instanceof LivingEntity living && living.getGroup() == EntityGroup.ILLAGER);
	/** This condition can be summarized as: "Does the target belong to the default entity group?" */
	public static final TargetingCondition IS_DEFAULT_GROUP = create((target, source) -> target instanceof LivingEntity living && living.getGroup() == EntityGroup.DEFAULT);

	// ===== Life State Conditions =====
	/** This condition can be summarized as: "Is the target alive?" */
	public static final TargetingCondition IS_ALIVE = create((target, source) -> target instanceof LivingEntity living && living.isAlive());
	/** This condition can be summarized as: "Is the target dead?" */
	public static final TargetingCondition IS_DEAD = create((target, source) -> target instanceof LivingEntity living && !living.isAlive());
	/** This condition can be summarized as: "Has the target been removed from the world?" */
	public static final TargetingCondition IS_REMOVED = create((target, source) -> target.isRemoved());
	/** This condition can be summarized as: "Can the source target the target?" */
	public static final TargetingCondition CAN_TARGET = create((target, source) -> source instanceof LivingEntity sourceEntity && target instanceof LivingEntity targetEntity && sourceEntity.canTarget(targetEntity));

	// ===== Environmental State Conditions =====
	/** This condition can be summarized as: "Is the target on the ground?" */
	public static final TargetingCondition IS_ON_GROUND = create((target, source) -> target.isOnGround());
	/** This condition can be summarized as: "Is the target in water?" */
	public static final TargetingCondition IS_IN_WATER = create((target, source) -> target.isTouchingWater());
	/** This condition can be summarized as: "Is the target in lava?" */
	public static final TargetingCondition IS_IN_LAVA = create((target, source) -> target.isInLava());
	/** This condition can be summarized as: "Is the target on fire?" */
	public static final TargetingCondition IS_BURNING = create((target, source) -> target.isOnFire());
	/** This condition can be summarized as: "Is the target in water?" */
	public static final TargetingCondition IS_IN_BUBBLE_COLUMN = create((target, source) -> target.isSubmergedInWater());

	// ===== Player-Specific State Conditions =====
	/** This condition can be summarized as: "Is the target sneaking?" */
	public static final TargetingCondition IS_SNEAKING = create((target, source) -> target instanceof LivingEntity living && living.isSneaking());
	/** This condition can be summarized as: "Is the target sprinting?" */
	public static final TargetingCondition IS_SPRINTING = create((target, source) -> target instanceof LivingEntity living && living.isSprinting());
	/** This condition can be summarized as: "Is the target riding a vehicle?" */
	public static final TargetingCondition IS_RIDING = create((target, source) -> target.hasVehicle());
	/** This condition can be summarized as: "Does the target have passengers?" */
	public static final TargetingCondition HAS_PASSENGER = create((target, source) -> target.hasPassengers());
	/** This condition can be summarized as: "Is the source the vehicle of the target?" */
	public static final TargetingCondition IS_VEHICLE = create((target, source) -> target.getVehicle() != null && target.getVehicle().equals(source));
	/** This condition can be summarized as: "Is the source a passenger of the target?" */
	public static final TargetingCondition IS_PASSENGER = create(Entity::hasPassenger);

	// ===== Visibility and Awareness Conditions =====
	/** This condition can be summarized as: "Can the source see the target?" */
	public static final TargetingCondition CAN_SEE = create((target, source) -> source instanceof LivingEntity sourceEntity && sourceEntity.canSee(target));

	// ===== Health and Damage Conditions =====
	/** This condition can be summarized as: "Is the target at full health?" */
	public static final TargetingCondition IS_FULL_HEALTH = create((target, source) -> target instanceof LivingEntity living && living.getHealth() >= living.getMaxHealth());
	/** This condition can be summarized as: "Is the target at low health (below 50%)?" */
	public static final TargetingCondition IS_LOW_HEALTH = create((target, source) -> target instanceof LivingEntity living && living.getHealth() < living.getMaxHealth() / 2);
	/** This condition can be summarized as: "Is the target injured (not at full health)?" */
	public static final TargetingCondition IS_INJURED = create((target, source) -> target instanceof LivingEntity living && living.getHealth() < living.getMaxHealth());

	// ===== Distance Conditions =====
	/** This condition can be summarized as: "Is the target within 5 blocks of the source?" */
	public static final TargetingCondition IS_CLOSE = create((target, source) -> source.distanceTo(target) <= 5.0);
	/** This condition can be summarized as: "Is the target within 10 blocks of the source?" */
	public static final TargetingCondition IS_NEAR = create((target, source) -> source.distanceTo(target) <= 10.0);
	/** This condition can be summarized as: "Is the target within 20 blocks of the source?" */
	public static final TargetingCondition IS_WITHIN_RANGE = create((target, source) -> source.distanceTo(target) <= 20.0);
	/** This condition can be summarized as: "Is the target further than 5 blocks from the source?" */
	public static final TargetingCondition IS_FAR = create((target, source) -> source.distanceTo(target) > 5.0);

	// ===== Dimension Conditions =====
	/** This condition can be summarized as: "Is the target in the same dimension as the source?" */
	public static final TargetingCondition IS_SAME_DIMENSION = create((target, source) -> source.getWorld() == target.getWorld());
	/** This condition can be summarized as: "Is the target in the overworld?" */
	public static final TargetingCondition IS_IN_OVERWORLD = create((target, source) -> target.getWorld().getRegistryKey().equals(net.minecraft.world.World.OVERWORLD));
	/** This condition can be summarized as: "Is the target in the nether?" */
	public static final TargetingCondition IS_IN_NETHER = create((target, source) -> target.getWorld().getRegistryKey().equals(net.minecraft.world.World.NETHER));
	/** This condition can be summarized as: "Is the target in the end?" */
	public static final TargetingCondition IS_IN_END = create((target, source) -> target.getWorld().getRegistryKey().equals(net.minecraft.world.World.END));

	// The actual code for the condition.
	final BiPredicate<Entity, Entity> predicate;
	TargetingCondition(BiPredicate<Entity, Entity> predicate)
	{
		this.predicate = predicate;
	}

	// Swaps the target and source in the condition, meaning `target` will be treated as `source` and `source` will be treated as `target`.

	/**
	 * Creates a new TargetingCondition that evaluates the same logic as this condition but with the target and source entities swapped. This can be useful for creating complementary conditions without having to rewrite the logic.
	 * @return A new TargetingCondition with the target and source swapped in the evaluation logic.
	 */
	public TargetingCondition invertOrder()
	{
		return create((target, source) -> this.predicate.test(source, target));
	}

	/**
	 * Creates a new TargetingCondition that evaluates the opposite of this condition. This can be useful for creating complementary conditions without having to rewrite the logic.
	 * @return A new TargetingCondition that negates the result of this condition's evaluation.
	 */
	public TargetingCondition negate()
	{
		return create((target, source) -> !this.predicate.test(target, source));
	}

	/**
	 * Combines this TargetingCondition with another TargetingCondition using a specified logical operator. The combiner is a BiPredicate that takes the results of evaluating both conditions and returns a boolean result based on the desired logic (e.g., AND, OR).
	 * @param other The other TargetingCondition to combine with this one.
	 * @param combiner A BiPredicate that defines how to combine the results of the two conditions. For example, to combine with a logical AND, you could use `(a, b) -> a && b`, and for a logical OR, you could use `(a, b) -> a || b`.
	 * @return A new TargetingCondition that represents the combination of this condition and the other condition using the specified combiner logic.
	 * @implNote This method is not advised to be used. It is recommended to use the {@link TargetTester.Builder}.
	 */
	public TargetingCondition combine(TargetingCondition other, BiPredicate<Boolean, Boolean> combiner)
	{
		return create((target, source) -> {
			boolean thisResult = this.evaluate(target, source);
			boolean otherResult = other.evaluate(target, source);
			return combiner.test(thisResult, otherResult);
		});
	}

	/**
	 * Creates a new TargetingCondition based on a BiPredicate that takes a target and source entity and returns a boolean result. This allows for the creation of custom conditions that can evaluate any logic based on the target and source entities.
	 * @param predicate A BiPredicate that defines the logic of the condition. It takes two Entity parameters (the target and source) and returns a boolean indicating whether the condition is met.
	 * @return A new TargetingCondition that evaluates the provided predicate logic against a target and source entity.
	 */
	public static TargetingCondition create(BiPredicate<Entity, Entity> predicate)
	{
		return new TargetingCondition(predicate);
	}

	/**
	 * Evaluates the condition against a given target and source entity. The target is the entity being evaluated as a potential target, while the source is the entity that is trying to target the target. This method returns true if the condition is met based on the logic defined in the predicate, and false otherwise. If an exception occurs during evaluation, it will be caught and logged, and the method will return false to indicate that the condition was not met.
	 * @param target The entity being evaluated as a potential target. This is the entity that the source is trying to target.
	 * @param source The entity that is trying to target the target. This can be used for conditions that depend on the source, such as distance or line of sight.
	 * @return True if the condition is met based on the logic defined in the predicate, and false otherwise. If an exception occurs during evaluation, it will be caught and logged, and the method will return false to indicate that the condition was not met.
	 */
	public boolean evaluate(Entity target, Entity source)
	{
		try{
			return predicate.test(target, source);
		}catch (Exception e)
		{
			ConditionalTargetingMod.LOGGER.error("Error evaluating targeting condition: " + e.getMessage(), e);
			return false;
		}
	}

}
