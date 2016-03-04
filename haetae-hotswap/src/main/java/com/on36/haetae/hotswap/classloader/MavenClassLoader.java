package com.on36.haetae.hotswap.classloader;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.collection.DependencyCollectionException;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.resolution.DependencyRequest;
import org.sonatype.aether.resolution.DependencyResolutionException;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.graph.PreorderNodeListGenerator;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public final class MavenClassLoader {

	public static class ClassLoaderBuilder {

		private static final String COMPILE_SCOPE = "compile";
		private static final String LOCALREPOSITORYDIR = "C:/Users/fjyfk";
		private static final ClassLoader SHARE_NOTHING = null;// Thread.currentThread().getContextClassLoader();

		private final ImmutableList<RemoteRepository> repositories;
		private final File localRepositoryDirectory;

		private ClassLoaderBuilder(RemoteRepository... repositories) {
			Preconditions.checkNotNull(repositories);
			Preconditions.checkArgument(repositories.length > 0,
					"Must specify at least one remote repository.");

			this.repositories = ImmutableList.copyOf(repositories);
			this.localRepositoryDirectory = new File(LOCALREPOSITORYDIR
					+ "/.m2/repository");
		}

		public URLClassLoader forGAVS(String... gavs) {
			try {
				CollectRequest collectRequest = createCollectRequestForGAV(gavs);
				List<Artifact> artifacts = collectDependenciesIntoArtifacts(collectRequest);
				List<URL> urls = Lists.newLinkedList();
				for (Artifact artifact : artifacts) {
					System.out.println(artifact.getFile().toURI().toURL());
					urls.add(artifact.getFile().toURI().toURL());
				}
				return new URLClassLoader(urls.toArray(new URL[urls.size()]),
						SHARE_NOTHING);
			} catch (Exception e) {
				throw Throwables.propagate(e);
			}
		}

		private CollectRequest createCollectRequestForGAV(String... gavs) {

			List<Dependency> deps = Lists.newLinkedList();
			for (String gav : gavs) {
				Dependency dependency = new Dependency(
						new DefaultArtifact(gav), COMPILE_SCOPE);
				deps.add(dependency);
			}

			CollectRequest collectRequest = new CollectRequest();
			collectRequest.setDependencies(deps);
			for (RemoteRepository repository : repositories) {
				collectRequest.addRepository(repository);
			}

			return collectRequest;
		}

		private List<Artifact> collectDependenciesIntoArtifacts(
				CollectRequest collectRequest) throws PlexusContainerException,
				ComponentLookupException, DependencyCollectionException,
				ArtifactResolutionException, DependencyResolutionException {

			RepositorySystem repositorySystem = newRepositorySystem();
			RepositorySystemSession session = newSession(repositorySystem);
			DependencyNode node = repositorySystem.collectDependencies(session,
					collectRequest).getRoot();
			DependencyRequest request = new DependencyRequest(node, null);

			repositorySystem.resolveDependencies(session, request);

			PreorderNodeListGenerator nlg = new PreorderNodeListGenerator();
			node.accept(nlg);

			return nlg.getArtifacts(false);
		}

		private RepositorySystem newRepositorySystem()
				throws PlexusContainerException, ComponentLookupException {
			return new DefaultPlexusContainer().lookup(RepositorySystem.class);
		}

		private RepositorySystemSession newSession(RepositorySystem system) {
			MavenRepositorySystemSession mavenRepositorySystemSession = new MavenRepositorySystemSession();
			LocalRepository localRepo = new LocalRepository(
					localRepositoryDirectory);
			mavenRepositorySystemSession.setLocalRepositoryManager(system
					.newLocalRepositoryManager(localRepo));
			return mavenRepositorySystemSession;
		}
	}

	public static URLClassLoader forGAVS(String... gavs) {
		return usingCentralRepo().forGAVS(Preconditions.checkNotNull(gavs));
	}

	public static ClassLoaderBuilder usingCentralRepo() {
		RemoteRepository central = new RemoteRepository("central", "default",
				"http://maven.oschina.net/content/groups/public");
		return new ClassLoaderBuilder(central);
	}
}
