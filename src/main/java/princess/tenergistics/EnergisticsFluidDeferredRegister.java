package princess.tenergistics;

import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fluids.ForgeFlowingFluid.Properties;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.registration.DelayedSupplier;
import slimeknights.mantle.registration.FluidBuilder;
import slimeknights.mantle.registration.deferred.DeferredRegisterWrapper;
import slimeknights.mantle.registration.object.FluidObject;

public class EnergisticsFluidDeferredRegister extends DeferredRegisterWrapper<Fluid>
	{
	
	private final DeferredRegister<Block> blockRegister;
	
	public EnergisticsFluidDeferredRegister(String modID)
		{
		super(ForgeRegistries.FLUIDS, modID);
		this.blockRegister = DeferredRegister.create(ForgeRegistries.BLOCKS, modID);
		}
		
	public <I extends Fluid> RegistryObject<I> registerFluid(final String name, final Supplier<? extends I> sup)
		{
		return register.register(name, sup);
		}
		
	public <F extends ForgeFlowingFluid> FluidObject<F> registerNoBucket(String name, String tagName, FluidBuilder builder, Function<Properties, ? extends F> still, Function<Properties, ? extends F> flowing, Function<Supplier<? extends FlowingFluid>, ? extends FlowingFluidBlock> block)
		{
		DelayedSupplier<F> stillDelayed = new DelayedSupplier<>();
		DelayedSupplier<F> flowingDelayed = new DelayedSupplier<>();
		
		RegistryObject<FlowingFluidBlock> blockObj = blockRegister
				.register(name + "_fluid", () -> block.apply(stillDelayed));
		
		Properties props = builder.block(blockObj).build(stillDelayed, flowingDelayed);
		
		Supplier<F> stillSup = registerFluid(name, () -> still.apply(props));
		stillDelayed.setSupplier(stillSup);
		Supplier<F> flowingSup = registerFluid("flowing_" + name, () -> flowing.apply(props));
		flowingDelayed.setSupplier(flowingSup);
		
		return new FluidObject<>(resource(name), tagName, stillSup, flowingSup, blockObj);
		}
		
	public <F extends ForgeFlowingFluid> FluidObject<F> registerNoBucket(String name, String tagName, FluidAttributes.Builder builder, Function<Properties, ? extends F> still, Function<Properties, ? extends F> flowing, Material material, int lightLevel)
		{
		return registerNoBucket(name, tagName, new FluidBuilder(builder)
				.explosionResistance(100f), still, flowing, (fluid) -> new FlowingFluidBlock(fluid, Block.Properties
						.create(material)
						.doesNotBlockMovement()
						.hardnessAndResistance(100.0F)
						.noDrops()
						.setLightLevel((state) -> lightLevel)));
		}
		
	public <F extends ForgeFlowingFluid> FluidObject<F> registerNoBucket(String name, FluidAttributes.Builder builder, Function<Properties, ? extends F> still, Function<Properties, ? extends F> flowing, Material material, int lightLevel)
		{
		return registerNoBucket(name, name, builder, still, flowing, material, lightLevel);
		}
		
	public FluidObject<ForgeFlowingFluid> registerNoBucket(String name, String tagName, FluidAttributes.Builder builder, Material material, int lightLevel)
		{
		return registerNoBucket(name, tagName, builder, ForgeFlowingFluid.Source::new, ForgeFlowingFluid.Flowing::new, material, lightLevel);
		}
		
	public FluidObject<ForgeFlowingFluid> registerNoBucket(String name, FluidAttributes.Builder builder, Material material, int lightLevel)
		{
		return registerNoBucket(name, name, builder, material, lightLevel);
		}
	}
