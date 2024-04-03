package lol.j0.ZeroBridge;

import nilloader.api.ClassTransformer;
import nilloader.api.ModRemapper;
import nilloader.api.NilLogger;

// All entrypoint classes must implement Runnable.
public class ZeroBridgePremain implements Runnable {

	// NilLoader comes with a logger abstraction that Does The Right Thing depending on the environment.
	// You should always use it.
	public static final NilLogger log = NilLogger.get("ZeroBridge");
	
	@Override
	public void run() {

		// Any class transformers need to be registered with NilLoader like this.
		ClassTransformer.register(new MinecraftServerTransformer()); // startBridgeThread
		ClassTransformer.register(new NetServerHandlerTransformer()); // postChat + postDisconnect
		ClassTransformer.register(new ServerConfigurationManagerTransformer()); // postJoin
		ClassTransformer.register(new EntityServerPlayerTransformer()); // postDeath
	}

}
