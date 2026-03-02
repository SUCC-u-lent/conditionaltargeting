package com.ubunifu.conditionaltargeting;

import net.minecraft.entity.Entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * A class that tests whether a target is valid based on a set of conditions. It has three lists of conditions:
 * - Any conditions: If any of these conditions are true, the target is valid.
 * - All conditions: All of these conditions must be true for the target to be valid.
 * - None conditions: If any of these conditions are true, the target is invalid.
 */
public final class TargetTester
{
	private TargetTester(Builder builder)
	{
		this.enableLogging = builder.enableLogging;
		this.predicate = (source,target) -> {
			if (!builder.anyConditions.isEmpty()) {
				boolean anyMatched = false;

				for (TargetingCondition condition : builder.anyConditions) {
					if (condition.setLogging(enableLogging).evaluate(source, target)) {
						if (enableLogging)
							ConditionalTargetingMod.LOGGER.info(
								"[Successful Evaluation] {} evaluated a 'true' response to the 'any' condition: {} against {}",
								source, condition, target
							);

						anyMatched = true;
						break;
					}
				}

				if (!anyMatched) {
					if (enableLogging)
						ConditionalTargetingMod.LOGGER.info(
							"[Failed Evaluation] {} failed all 'any' conditions against {}",
							source, target
						);

					return false;
				}
			}
			for (TargetingCondition condition : builder.allConditions) {
				if (!condition.setLogging(enableLogging).evaluate(source, target)) {
					if (enableLogging)
						ConditionalTargetingMod.LOGGER.info(
							"[Failed Evaluation] {} evaluated a 'false' response to the 'all' condition: {} against {}",
							source, condition, target
						);

					return false;
				}
			}
			for (TargetingCondition condition : builder.noneConditions) {
				if (condition.setLogging(enableLogging).evaluate(source, target)) {
					if (enableLogging)
						ConditionalTargetingMod.LOGGER.info(
							"[Failed Evaluation] {} evaluated a 'true' response to the 'none' condition: {} against {}",
							source, condition, target
						);

					return false;
				}
			}

			if (enableLogging)
				ConditionalTargetingMod.LOGGER.info(
					"[Successful Evaluation] {} has completed an evaluation against {} and all checks have finished.",
					source, target
				);

			return true;
		};
	}
	BiPredicate<Entity, Entity> predicate;
	boolean enableLogging;

	/**
	 * Evaluates the target against the conditions. The source is the entity that is trying to target the target. This can be used for conditions that depend on the source, such as distance or line of sight.
	 * @param target The entity being tested as a target.
	 * @param source The entity that is trying to target the target.
	 * @return True if the target is valid based on the conditions, false otherwise.
	 * @implNote Errors in the conditions will not cause a crash but will instead cause the condition to evaluate to false.
	 */
	public boolean evaluate(Entity source, Entity target)
	{
		return predicate.test(source, target);
	}

	public static boolean containsDuplicateConditions(TargetingCondition... condition)
	{
		List<TargetingCondition> conditionList = Arrays.asList(condition);
		return conditionList.size() != conditionList.stream().distinct().count();
	}
	public static boolean containsDuplicateConditions(List<TargetingCondition> conditions) {
		return conditions.size() != conditions.stream().distinct().count();
	}

	/**
	 * A builder for creating a TargetTester. It allows you to add conditions to the any, all, and none lists. The build method creates a TargetTester with the specified conditions.
	 * @implNote The builder does not check for duplicate conditions or conflicting conditions.
	 */
	public static class Builder
	{
		private final List<TargetingCondition> anyConditions = new ArrayList<>();
		private final List<TargetingCondition> allConditions = new ArrayList<>();
		private final List<TargetingCondition> noneConditions = new ArrayList<>();
		private boolean enableLogging = false;

		/**
		 * Creates a new Builder with no conditions. You can add conditions using the addAnyCondition, addAllCondition, and addNoneCondition methods.
		 */
		public Builder(){}
		private boolean isEmpty()
		{
			return anyConditions.isEmpty() && allConditions.isEmpty() && noneConditions.isEmpty();
		}
		private boolean hasDuplicateConditions(TargetingCondition[] conditions)
		{
			return containsDuplicateConditions(conditions);
		}
		private boolean addingDuplicateConditions(TargetingCondition[] conditions)
		{
			for(TargetingCondition condition : conditions)
				if(anyConditions.contains(condition) || allConditions.contains(condition) || noneConditions.contains(condition))
					return true;
			return false;
		}

		/**
		 * Enables logging for the builder. If enabled, the builder will log the result of every evaluation of the TargetTester built by this builder.
 		 * @return The builder with logging enabled, so you can chain method calls.
		 */
		public Builder enableLogging()
		{
			this.enableLogging = true;
			return this;
		}

		/**
		 * Adds conditions to the any list. If any of these conditions are true, the target is valid. You can add multiple conditions at once by passing an array of conditions. If you add duplicate conditions, a warning will be logged. If you add conditions that are already in the builder, a warning will be logged.
			 * @param conditions The conditions to add to the any list.
			 * @return The builder with the added conditions, so you can chain method calls.
		 */
		public Builder addAnyCondition(TargetingCondition... conditions)
		{
			if (conditions.length == 0) return this;
			if (hasDuplicateConditions(conditions)) ConditionalTargetingMod.LOGGER.warn("Added duplicate conditions to any conditions: {}", Arrays.toString(conditions));
			if (addingDuplicateConditions(conditions)) ConditionalTargetingMod.LOGGER.warn("Added conditions that are already in the builder to any conditions: {}", Arrays.toString(conditions));
			anyConditions.addAll(List.of(conditions));
			return this;
		}

		/**
		 * Adds conditions to the all list. All of these conditions must be true for the target to be valid. You can add multiple conditions at once by passing an array of conditions. If you add duplicate conditions, a warning will be logged. If you add conditions that are already in the builder, a warning will be logged.
		 * @param conditions The conditions to add to the all list.
		 * @return The builder with the added conditions, so you can chain method calls.
		 */
		public Builder addAllCondition(TargetingCondition... conditions)
		{
			if (conditions.length == 0) return this;
			if (hasDuplicateConditions(conditions)) ConditionalTargetingMod.LOGGER.warn("Added duplicate conditions to all conditions: {}", Arrays.toString(conditions));
			if (addingDuplicateConditions(conditions)) ConditionalTargetingMod.LOGGER.warn("Added conditions that are already in the builder to all conditions: {}", Arrays.toString(conditions));
			allConditions.addAll(List.of(conditions));
			return this;
		}

		/**
		 * Adds conditions to the none list. If any of these conditions are true, the target is invalid. You can add multiple conditions at once by passing an array of conditions. If you add duplicate conditions, a warning will be logged. If you add conditions that are already in the builder, a warning will be logged.
		 * @param conditions The conditions to add to the none list.
		 * @return The builder with the added conditions, so you can chain method calls.
		 */
		public Builder addNoneCondition(TargetingCondition... conditions)
		{
			if (conditions.length == 0) return this;
			if (hasDuplicateConditions(conditions)) ConditionalTargetingMod.LOGGER.warn("Added duplicate conditions to none conditions: {}", Arrays.toString(conditions));
			if (addingDuplicateConditions(conditions)) ConditionalTargetingMod.LOGGER.warn("Added conditions that are already in the builder to none conditions: {}", Arrays.toString(conditions));
			noneConditions.addAll(List.of(conditions));
			return this;
		}

		/**
		 * Builds a TargetTester with the specified conditions.
		 * @return A TargetTester with the specified conditions.
		 * @throws UnsupportedOperationException If no conditions were added to the builder.
		 */
		public TargetTester build()
		{
			if (this.isEmpty()) throw new UnsupportedOperationException("Cannot build a TargetTester with no conditions.");
			return new TargetTester(this);
		}
	}
}
