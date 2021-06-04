package princess.tenergistics.modifiers;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class BlockingModifier extends SingleUseModifier
	{
	public BlockingModifier()
		{
		super(0x13666);
		
		MinecraftForge.EVENT_BUS.register(this);
		}
		
	@Override
	public ActionResultType onToolUse(IModifierToolStack stack, int level, World worldIn, PlayerEntity player, Hand hand)
		{
		player.setActiveHand(hand);
		return ActionResultType.CONSUME;
		}
		
	@Override
	public int getUseDuration(IModifierToolStack stack, int level)
		{
		return 72000;
		}
		
	//Vanilla blocking, transcribed into an event.
	@SubscribeEvent
	protected void onShieldHit(LivingHurtEvent event)
		{
		LivingEntity defender = event.getEntityLiving();
		ItemStack stack = defender.getActiveItemStack();
		ToolStack tool = ToolStack.from(stack);
		
		if (tool.getModifierLevel(this) > 0)
			{
			float damage = event.getAmount();
			DamageSource source = event.getSource();
			if (damage > 0.0 && canBlockDamageSource(defender, source))
				{
				ToolDamageUtil.damage(tool, (int) damage, defender, stack);
				
				//TODO: a way to give modifiers control over it
				//Screw vanilla armor, honestly. This (v) turns into a big fat 0 change. 
				//event.setAmount(CombatRules.getDamageAfterAbsorb(damage, 2f, 8f));
				event.setAmount(damage * 0.25f);
				
				if (!source.isProjectile())
					{
					Entity attacker = source.getImmediateSource();
					if (attacker instanceof LivingEntity)
						{
						LivingEntity livingAttacker = (LivingEntity) attacker;
						livingAttacker.applyKnockback(0.5F, livingAttacker.getPosX() - defender
								.getPosX(), livingAttacker.getPosZ() - defender.getPosZ());
						
						if (livingAttacker instanceof ServerPlayerEntity)
							{
							TinkerNetwork.getInstance()
									.sendVanillaPacket(livingAttacker, new SEntityVelocityPacket(livingAttacker));
							}
							
						if (defender instanceof PlayerEntity && livingAttacker.getHeldItemMainhand()
								.canDisableShield(defender.getActiveItemStack(), defender, livingAttacker))
							{
							((PlayerEntity) defender).disableShield(true);
							}
						}
					}
				}
			}
		}
		
	@SubscribeEvent
	protected void renderThirdPerson(RenderPlayerEvent.Pre event)
		{
		//TODO: change to semi-raw access for cheapness reasons
		ToolStack tool = ToolStack.from(event.getPlayer().getActiveItemStack());
		
		if (tool.getModifierLevel(this) > 0)
			{
			PlayerEntity player = event.getPlayer();
			PlayerModel<AbstractClientPlayerEntity> model = event.getRenderer().getEntityModel();
			
			if ((player.getPrimaryHand() == HandSide.RIGHT) == (player.getActiveHand() == Hand.MAIN_HAND))
				{
				model.rightArmPose = BipedModel.ArmPose.BLOCK;
				}
			else
				{
				model.leftArmPose = BipedModel.ArmPose.BLOCK;
				}
			}
		}
		
	protected static boolean canBlockDamageSource(LivingEntity entity, DamageSource source)
		{
		Entity immediateSource = source.getImmediateSource();
		boolean pierced = false;
		if (immediateSource instanceof AbstractArrowEntity)
			{
			AbstractArrowEntity arrowEntity = (AbstractArrowEntity) immediateSource;
			if (arrowEntity.getPierceLevel() > 0)
				{
				pierced = true;
				}
			}
			
		if (!source.isUnblockable() && !pierced)
			{
			Vector3d damageLocation = source.getDamageLocation();
			if (damageLocation != null)
				{
				Vector3d lookVector = entity.getLook(1.0F);
				Vector3d blockVector = damageLocation.subtractReverse(entity.getPositionVec()).normalize();
				blockVector = new Vector3d(blockVector.x, 0.0D, blockVector.z);
				if (blockVector.dotProduct(lookVector) < 0.0D)
					{ return true; }
				}
			}
			
		return false;
		}
	}
