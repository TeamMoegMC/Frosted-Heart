# Frosted Heart 
- The Core Mod of The Winter Rescue
- Issues should go to the [TWR Issue Tracker](https://github.com/TeamMoegMC/The-Winter-Rescue/issues)

This Mod is where most of the contents of The Winter Rescue
are implemented. It is a NeoForge mod for Minecraft 1.20.1.

Core APIs:
- [Create](https://www.curseforge.com/minecraft/mc-mods/create)
- [Immersive Engineering](https://www.curseforge.com/minecraft/mc-mods/immersive-engineering)
- [FTB Teams](https://www.curseforge.com/minecraft/mc-mods/ftb-teams)

Content integration:
- [Tetra](https://www.curseforge.com/minecraft/mc-mods/tetra)
- [Curios](https://www.curseforge.com/minecraft/mc-mods/curios)
- [Just Enough Items](https://www.curseforge.com/minecraft/mc-mods/jei)
- [FTB Chunks](https://www.curseforge.com/minecraft/mc-mods/ftb-chunks)
- [FTB Quests](https://www.curseforge.com/minecraft/mc-mods/ftb-quests)

Libraries:
- [Flywheel](https://www.curseforge.com/minecraft/mc-mods/flywheel)
- [MUtils](https://www.curseforge.com/minecraft/mc-mods/mutils)
- [FTB Library](https://www.curseforge.com/minecraft/mc-mods/ftb-library)
- [Architectury](https://www.curseforge.com/minecraft/mc-mods/architectury-forge)

The following are written for anyone who wishes to join the
development of this exciting project. 

## Getting Started

Before anything, join the 
[discord channel](https://discord.gg/BWn6E94)
and let any dev team member know. If you don't use Discord,
send a PR or issue.

To get started, you need to have a working Forge development
environment. Install Java 17 JDK, and import the project into
your IDE. You can use IntelliJ IDEA, Eclipse, or any other
IDE that supports Gradle. You should be familar with Forge before 
contributing. There are extensive tutorials, below are some:
- [Forge Documentation](https://mcforge.readthedocs.io/en/1.16.x/)
- [Boson Tutorial](https://boson-english.v2mcdev.com/introduction/intro.html)
- [Minecraft by Example](https://github.com/TheGreyGhost/MinecraftByExample)
- [Boson Chinese](https://boson.v2mcdev.com/)

Once setup, you can run the following commands in the project:

`gradle runclient` or `./gradlew runclient` will start the game.

`gradle build` or `./gradlew build` will build the mod.

## Package organization

The philosophy is that topic matters more than functionality.
So our package is organized by topic, instead of functionality
or type of code constructs. But there are still some exceptions,
so here's an overview:

### Core packages

- `base`: The base package contains system level abstractions 
that are used throughout the project. 
However, it does *not* contain any actual game content.
It's just the base classes.
- `util`: The util package contains *utility* classes that are
  used throughout the project.
- `content`: The content package contains the game *content*,
or more precisely game mechanics and systems. It is divided by
subtopics, such as `agriculture`, `town`, `steamenergy`. If you
want to add a new game mechanic, this is the place to put it.
  (maybe we should name as `mechanics` instead of `content`?)
- `infrastructure`: The infrastructure package contains the
  infrastructure of the game, such as commands, data, gen, config.
- `compat`: Compatibility for other mods.
- `mixin`: Mixin classes.
- `events`: Event handling classes.

### Initializers
The rest packages are what's normally called *initializer* classes,
and are organized by type of code constructs.
They are used to actually initialize the contents. 
You will probably need to initialize your block classes 
created in `content` as objects in `FHBlocks` for example.

### Entry points

Finally, we have what we call the *entry point* classes. 
These are very like the `main` functions in Java applications.
There are two types of entry points:

- Registry entry points: where you actually tell Minecraft to 
register the objects you initialized into the game registry. 
For example, you will need to register your blocks.
- Event entry points: where you actually tell Minecraft to
do something when a certain event happens.
For example, you can let Minecraft tick the town you created
in a server tick event.

Depending on each type of content you are adding, you will need
to use either 
- `FHMain.java` where most registry entry points are located, or
- `events` where both registry and other event handling happens.

## Create your contribution

Now, you've know the basic structure, you can start contributing.
Most of time, you will just be working with the `content` or
`foundation` package. Know type of contribution you are making:
- If it is part of a self-contained new game mechanic, system, or
anything that can be organized as a topic, 
you should put it in `content`.
If the topic is new, create a new subpackage.
If not, put it in the existing subpackage.
Then, you should initialize it in the corresponding initializer.
Finally, use the entry points as needed. Examples:
  - Adding a new crop: `content.agriculture`
  - Adding a new town building: `content.town`
  - Adding a new steam machine: `content.steamenergy`
  - Adding gun system: create `content.gun`
- If you create any utility classes during the process, put it in
`util`.
- If you create any functional classes, put it in the corresponding
functional package.
- Normally, you should not need to touch `base`.
- Adding command? Put it in `infrastructure.commands`.
But if you need to add such abstract and system level classes,
you should put them there. But do not change existing classes
without a good reason unless we discussed it.

### For Artists

If you are an artist, you can contribute by creating textures,
or models, schematics for structures, sounds, or music.
You should know how they are organized in the `assets` folder.
You can create a new subdirectory for your content, and put them
in through GitHub or upload or other means. 
Then, you can create a PR to add them to the mod.
If you work with other developers, you can also let them
upload the content for you.

## Submit your contribution

Once you've done your contribution, you can submit it as a 
Pull Request. Make sure you've tested your code before submitting.
Also, write a good description of what you've done, and why you've
done it. If you have any questions, feel free to ask in the PR or
in the Discord channel.

## Credits

We will credit all contributors in the mod's credit list in
`mods.toml` and the GitHub page for `The Winter Rescue`.

Once you have created significant enough contribution
(normally considered as five or more PRs, but varies on actual
work), you will be
considered as a team member and be in the in-game credit page.

## License

GNU General Public License v3.0