package amidst.map;

import java.util.Arrays;

import amidst.Options;
import amidst.fragment.colorprovider.BiomeColorProvider;
import amidst.fragment.colorprovider.SlimeColorProvider;
import amidst.fragment.constructor.BiomeDataConstructor;
import amidst.fragment.constructor.DummyConstructor;
import amidst.fragment.constructor.FragmentConstructor;
import amidst.fragment.constructor.ImageConstructor;
import amidst.fragment.drawer.FragmentDrawer;
import amidst.fragment.drawer.GridDrawer;
import amidst.fragment.drawer.ImageDrawer;
import amidst.fragment.drawer.WorldObjectDrawer;
import amidst.fragment.loader.BiomeDataLoader;
import amidst.fragment.loader.DummyLoader;
import amidst.fragment.loader.FragmentLoader;
import amidst.fragment.loader.ImageLoader;
import amidst.fragment.loader.WorldObjectLoader;
import amidst.map.layer.LayerId;
import amidst.map.layer.LayerType;
import amidst.minecraft.world.Resolution;
import amidst.minecraft.world.World;

public class LayerContainerFactory {
	private final LayerContainer layerContainer;

	public LayerContainerFactory(Options options, World world, Map map) {
		LayerDeclaration[] declarations = new LayerDeclaration[LayerId.NUMBER_OF_LAYERS];
		FragmentConstructor[] constructors = new FragmentConstructor[LayerId.NUMBER_OF_LAYERS];
		FragmentLoader[] loaders = new FragmentLoader[LayerId.NUMBER_OF_LAYERS];
		FragmentDrawer[] drawers = new FragmentDrawer[LayerId.NUMBER_OF_LAYERS];

		// @formatter:off
		declarations[LayerId.BIOME]            = new LayerDeclaration(LayerType.BIOME,           options.showBiomes);
		declarations[LayerId.SLIME]            = new LayerDeclaration(LayerType.SLIME,           options.showSlimeChunks);
		declarations[LayerId.GRID]             = new LayerDeclaration(LayerType.GRID,            options.showGrid);
		declarations[LayerId.VILLAGE]          = new LayerDeclaration(LayerType.VILLAGE,         options.showVillages);
		declarations[LayerId.OCEAN_MONUMENT]   = new LayerDeclaration(LayerType.OCEAN_MONUMENT,  options.showOceanMonuments);
		declarations[LayerId.STRONGHOLD]       = new LayerDeclaration(LayerType.STRONGHOLD,      options.showStrongholds);
		declarations[LayerId.TEMPLE]           = new LayerDeclaration(LayerType.TEMPLE,          options.showTemples);
		declarations[LayerId.SPAWN]            = new LayerDeclaration(LayerType.SPAWN,           options.showSpawn);
		declarations[LayerId.NETHER_FORTRESS]  = new LayerDeclaration(LayerType.NETHER_FORTRESS, options.showNetherFortresses);
		declarations[LayerId.PLAYER]           = new LayerDeclaration(LayerType.PLAYER,          options.showPlayers);

		constructors[LayerId.BIOME]            = new BiomeDataConstructor(declarations[LayerId.BIOME], Resolution.QUARTER);
		constructors[LayerId.SLIME]            = new ImageConstructor(    declarations[LayerId.SLIME], Resolution.CHUNK);
		constructors[LayerId.GRID]             = new DummyConstructor();
		constructors[LayerId.VILLAGE]          = new DummyConstructor();
		constructors[LayerId.OCEAN_MONUMENT]   = new DummyConstructor();
		constructors[LayerId.STRONGHOLD]       = new DummyConstructor();
		constructors[LayerId.TEMPLE]           = new DummyConstructor();
		constructors[LayerId.SPAWN]            = new DummyConstructor();
		constructors[LayerId.NETHER_FORTRESS]  = new DummyConstructor();
		constructors[LayerId.PLAYER]           = new DummyConstructor();

		loaders[LayerId.BIOME]                 = new BiomeDataLoader(  declarations[LayerId.BIOME], Resolution.QUARTER, new BiomeColorProvider(map), world.getBiomeDataOracle());
		loaders[LayerId.SLIME]                 = new ImageLoader(      declarations[LayerId.SLIME], Resolution.CHUNK, new SlimeColorProvider(world.getSlimeChunkOracle()));
		loaders[LayerId.GRID]                  = new DummyLoader();
		loaders[LayerId.VILLAGE]               = new WorldObjectLoader(declarations[LayerId.VILLAGE], world.getVillageProducer());
		loaders[LayerId.OCEAN_MONUMENT]        = new WorldObjectLoader(declarations[LayerId.OCEAN_MONUMENT], world.getOceanMonumentProducer());
		loaders[LayerId.STRONGHOLD]            = new WorldObjectLoader(declarations[LayerId.STRONGHOLD], world.getStrongholdProducer());
		loaders[LayerId.TEMPLE]                = new WorldObjectLoader(declarations[LayerId.TEMPLE], world.getTempleProducer());
		loaders[LayerId.SPAWN]                 = new WorldObjectLoader(declarations[LayerId.SPAWN], world.getSpawnProducer());
		loaders[LayerId.NETHER_FORTRESS]       = new WorldObjectLoader(declarations[LayerId.NETHER_FORTRESS], world.getNetherFortressProducer());
		loaders[LayerId.PLAYER]                = new WorldObjectLoader(declarations[LayerId.PLAYER], world.getPlayerProducer());

		drawers[LayerId.BIOME]                 = new ImageDrawer(      declarations[LayerId.BIOME], Resolution.QUARTER);
		drawers[LayerId.SLIME]                 = new ImageDrawer(      declarations[LayerId.SLIME], Resolution.CHUNK);
		drawers[LayerId.GRID]                  = new GridDrawer(       declarations[LayerId.GRID], map);
		drawers[LayerId.VILLAGE]               = new WorldObjectDrawer(declarations[LayerId.VILLAGE], map);
		drawers[LayerId.OCEAN_MONUMENT]        = new WorldObjectDrawer(declarations[LayerId.OCEAN_MONUMENT], map);
		drawers[LayerId.STRONGHOLD]            = new WorldObjectDrawer(declarations[LayerId.STRONGHOLD], map);
		drawers[LayerId.TEMPLE]                = new WorldObjectDrawer(declarations[LayerId.TEMPLE], map);
		drawers[LayerId.SPAWN]                 = new WorldObjectDrawer(declarations[LayerId.SPAWN], map);
		drawers[LayerId.NETHER_FORTRESS]       = new WorldObjectDrawer(declarations[LayerId.NETHER_FORTRESS], map);
		drawers[LayerId.PLAYER]                = new WorldObjectDrawer(declarations[LayerId.PLAYER], map);
		// @formatter:on
		this.layerContainer = new LayerContainer(Arrays.asList(declarations),
				Arrays.asList(constructors), Arrays.asList(loaders),
				Arrays.asList(drawers));
	}

	public LayerContainer createLayerContainer() {
		return layerContainer;
	}
}
