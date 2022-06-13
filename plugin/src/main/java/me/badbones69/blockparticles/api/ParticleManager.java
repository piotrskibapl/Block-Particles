package me.badbones69.blockparticles.api;

import me.badbones69.blockparticles.api.FileManager.Files;
import me.badbones69.blockparticles.api.enums.ParticleType;
import me.badbones69.blockparticles.api.enums.Particles;
import me.badbones69.blockparticles.api.objects.CustomFountain;
import me.badbones69.blockparticles.api.objects.Particle;
import me.badbones69.blockparticles.controllers.Fountains;
import me.badbones69.blockparticles.controllers.ParticleControl;
import me.badbones69.blockparticles.multisupport.NMS_v1_12_Down;
import me.badbones69.blockparticles.multisupport.NMS_v1_13_Up;
import me.badbones69.blockparticles.multisupport.Version;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class ParticleManager {
    
    private static ParticleManager instance = new ParticleManager();
    private FileManager fileManager = FileManager.getInstance();
    private Plugin plugin;
    private List<Entity> fountainItems = new ArrayList<>();
    private List<CustomFountain> customFountains = new ArrayList<>();
    private HashMap<Player, String> setCommandPlayers = new HashMap<>();
    private ParticleControl particleControl;
    
    public static ParticleManager getInstance() {
        return instance;
    }
    
    public void load() {
        particleControl = Version.getCurrentVersion().isNewer(Version.v1_12_R1) ? new NMS_v1_13_Up() : new NMS_v1_12_Down();
        customFountains.clear();
        if (hasOldFiles()) {
            String prefix = fileManager.getPrefix();
            boolean isLogging = fileManager.isLogging();
            if (isLogging) Bukkit.getLogger().log(Level.INFO, prefix + "Old files have been detected!");
            if (isLogging) Bukkit.getLogger().log(Level.INFO, prefix + "Converting old files.");
            convertOldFiles();
            if (isLogging) Bukkit.getLogger().log(Level.INFO, prefix + "Finished converting old files.");
        }
        FileConfiguration config = Files.CONFIG.getFile();
        if (config.contains("settings.heads")) {
            for (String customFountain : config.getConfigurationSection("settings.heads").getKeys(false)) {
                customFountains.add(new CustomFountain(customFountain, config.getStringList("settings.heads." + customFountain)));
            }
        }
    }
    
    public Plugin getPlugin() {
        if (plugin == null) {
            plugin = Bukkit.getServer().getPluginManager().getPlugin("BlockParticles");
        }
        return plugin;
    }
    
    public ParticleControl getParticleControl() {
        return particleControl;
    }
    
    public List<CustomFountain> getCustomFountains() {
        return customFountains;
    }
    
    public CustomFountain getCustomFountain(String name) {
        for (CustomFountain fountain : customFountains) {
            if (fountain.getName().equalsIgnoreCase(name)) {
                return fountain;
            }
        }
        return null;
    }
    
    public boolean hasOldFiles() {
        return Files.CONFIG.getFile().contains("Settings") || Files.DATA.getFile().contains("Locations");
    }
    
    public void convertOldFiles() {
        String prefix = fileManager.getPrefix();
        boolean isLogging = fileManager.isLogging();
        FileConfiguration config = Files.CONFIG.getFile();
        if (config.contains("Settings")) {
            config.set("settings.prefix", config.getString("Settings.Prefix"));
            config.set("Settings", null);
            Files.CONFIG.saveFile();
            if (isLogging) Bukkit.getLogger().log(Level.INFO, prefix + "Finished converting config.yml.");
        }
        FileConfiguration data = Files.DATA.getFile();
        if (data.contains("Locations")) {
            List<Particle> particles = new ArrayList<>();
            for (String id : data.getConfigurationSection("Locations").getKeys(false)) {
                particles.add(new Particle(id,
                data.getString("Locations." + id + ".World"),
                data.getInt("Locations." + id + ".X"),
                data.getInt("Locations." + id + ".Y"),
                data.getInt("Locations." + id + ".Z"),
                data.getString("Locations." + id + ".Particle")));
            }
            data.set("Locations", null);
            for (Particle particle : particles) {
                String id = particle.getID();
                data.set("locations." + id + ".world", particle.getWorld());
                data.set("locations." + id + ".x", particle.getX());
                data.set("locations." + id + ".y", particle.getY());
                data.set("locations." + id + ".z", particle.getZ());
                data.set("locations." + id + ".particle", particle.getParticleTypeName());
            }
            Files.DATA.saveFile();
            if (isLogging) Bukkit.getLogger().log(Level.INFO, prefix + "Finished converting data.yml.");
        }
    }
    
    public boolean hasParticle(Location loc) {
        FileConfiguration data = Files.DATA.getFile();
        if (data.contains("locations")) {
            for (String id : data.getConfigurationSection("locations").getKeys(false)) {
                World world = Bukkit.getServer().getWorld(data.getString("locations." + id + ".World"));
                int X = data.getInt("locations." + id + ".X");
                int Y = data.getInt("locations." + id + ".Y");
                int Z = data.getInt("locations." + id + ".Z");
                Location l = new Location(world, X, Y, Z);
                if (l.equals(loc)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public List<Entity> getFountainItem() {
        return fountainItems;
    }
    
    public void addFountainItem(Entity item) {
        fountainItems.add(item);
    }
    
    public void removeFountainItem(Entity item) {
        fountainItems.remove(item);
    }
    
    /**
     * Set a Particle to a specified Location;
     *
     * @param type The Particle you wish to use.
     * @param loc  The location you wish to spawn the Particles.
     * @param name The Location Name.
     * @param cooldown The Particle cooldown
     */
    public void setParticle(Particles type, Location loc, String name, int cooldown) {
        if (particleControl.getLocations().containsKey(name)) {
            Bukkit.getServer().getScheduler().cancelTask(particleControl.getLocations().get(name));
        }
        switch (type) {
            case LOVETORNADO:
                particleControl.playLoveTornado(loc, name, cooldown);
                break;
            case WITCHTORNADO:
                particleControl.playWitchTornado(loc, name, cooldown);
                break;
            case LOVEWELL:
                particleControl.playLoveWell(loc, name, cooldown);
                break;
            case BIGLOVEWELL:
                particleControl.playBigLoveWell(loc, name, cooldown);
                break;
            case HALO:
                particleControl.playHalo(loc, name, cooldown);
                break;
            case SANTAHAT:
                particleControl.playSantaHat(loc, name, cooldown);
                break;
            case SOULWELL:
                particleControl.playSoulWell(loc, name, cooldown);
                break;
            case BIGSOULWELL:
                particleControl.playBigSoulWell(loc, name, cooldown);
                break;
            case FLAMEWHEEL:
                particleControl.playFlameWheel(loc, name, cooldown);
                break;
            case MARIO:
                Fountains.startMario(loc, name, cooldown);
                break;
            case POKEMON:
                Fountains.startPokemon(loc, name, cooldown);
                break;
            case FOOD:
                Fountains.startFood(loc, name, cooldown);
                break;
            case MOBS:
                Fountains.startMobs(loc, name, cooldown);
                break;
            case SNOWBLAST:
                particleControl.playSnowBlast(loc, name, cooldown);
                break;
            case RAINBOW:
                particleControl.playRainbow(loc, name, cooldown);
                break;
            case ENDERSIGNAL:
                particleControl.playEnderSignal(loc, name, cooldown);
                break;
            case MOBSPAWNER:
                particleControl.playMobSpawner(loc, name, cooldown);
                break;
            case ANGRYVILLAGER:
                particleControl.playAngryVillager(loc, name, cooldown);
                break;
            case HAPPYVILLAGER:
                particleControl.playHappyVillager(loc, name, cooldown);
                break;
            case FOOTPRINT:
                particleControl.playFootPrint(loc, name, cooldown);
                break;
            case FIRESPEW:
                particleControl.playFireSpew(loc, name, cooldown);
                break;
            case SPEW:
                particleControl.playSpew(loc, name, cooldown);
                break;
            case STORM:
                particleControl.playStorm(loc, name, cooldown);
                break;
            case SNOWSTORM:
                particleControl.playSnowStorm(loc, name, cooldown);
                break;
            case FIRESTORM:
                particleControl.playFireStorm(loc, name, cooldown);
                break;
            case WITCH:
                particleControl.playSpiral(loc, name, Particles.WITCH, 5, cooldown);
                break;
            case DOUBLEWITCH:
                particleControl.playDoubleSpiral(loc, name, Particles.DOUBLEWITCH, 5, cooldown);
                break;
            case MAGIC:
                particleControl.playMagic(loc, name, cooldown);
                break;
            case PRESENTS:
                Fountains.startPresents(loc, name, cooldown);
                break;
            case MUSIC:
                particleControl.playMusic(loc, name, cooldown);
                break;
            case POTION:
                particleControl.playPotion(loc, name, cooldown);
                break;
            case SNOW:
                particleControl.playSnow(loc, name, cooldown);
                break;
            case WATER:
                particleControl.startWater(loc, name, cooldown);
                break;
            case CHAINS:
                particleControl.playChains(loc, name, cooldown);
                break;
            case ENCHANT:
                particleControl.playEnchant(loc, name, cooldown);
                break;
            case FOG:
                particleControl.playFog(loc, name, cooldown);
                break;
            case HEADS:
                Fountains.startHeads(loc, name, cooldown);
                break;
            case FLAME:
                particleControl.playFlame(loc, name, cooldown);
                break;
            case BIGFLAME:
                particleControl.playBigFlame(loc, name, cooldown);
                break;
            case HALLOWEEN:
                Fountains.startHalloween(loc, name, cooldown);
                break;
            case GEMS:
                Fountains.startGems(loc, name, cooldown);
                break;
            case VOLCANO:
                particleControl.playVolcano(loc, name, cooldown);
                break;
            case SPIRAL:
                particleControl.playSpiral(loc, name, Particles.SPIRAL, 1, cooldown);
                break;
            case DOUBLESPIRAL:
                particleControl.playDoubleSpiral(loc, name, Particles.DOUBLESPIRAL, 1, cooldown);
                break;
            case CRIT:
                particleControl.playCrit(loc, name, cooldown);
                break;
            case BIGCRIT:
                particleControl.playBigCrit(loc, name, cooldown);
                break;
        }
    }
    
    /**
     * Remove a Particle;
     *
     * @param name The Location Name.
     */
    public void removeParticle(String name) {
        if (particleControl.getLocations().containsKey(name)) {
            Bukkit.getServer().getScheduler().cancelTask(particleControl.getLocations().get(name));
            particleControl.getLocations().remove(name);
        }
    }
    
    /**
     * Get the Particle Type of a Particle (Particle/Fountain).
     *
     * @param particle The Particle you want to get the ParticleType from.
     * @return A Particle Type.
     */
    public ParticleType getType(Particles particle) {
        return particle.getType();
    }
    
    public void addSetCommandPlayer(Player player, String type) {
        setCommandPlayers.put(player, type);
    }
    
    public HashMap<Player, String> getSetCommandPlayers() {
        return setCommandPlayers;
    }
    
    public boolean useNewMaterial() {
        return Version.getCurrentVersion().isNewer(Version.v1_12_R1);
    }
    
}