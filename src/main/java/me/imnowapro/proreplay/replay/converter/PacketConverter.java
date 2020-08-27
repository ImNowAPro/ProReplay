package me.imnowapro.proreplay.replay.converter;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import me.imnowapro.proreplay.ProReplay;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public interface PacketConverter {

  int getPacketID(PacketType type);

  PacketType getPacketType(int id);

  PacketContainer createLoginSuccessPacket(Player player);

  PacketContainer createLoginPacket(Player player);

  PacketContainer createPlayerListItemPacket(Player player);

  PacketContainer createPlayerSpawnPacket(Player player);

  PacketContainer createPlayerSpawnPacket(Player player, Location location);

  PacketContainer createPositionPacket(Location position);

  PacketContainer createPositionPacket(Player player, Vector move);

  PacketContainer createPositionLookPacket(Player player, Vector move, float yaw, float pitch);

  PacketContainer createLookPacket(Player player, float yaw, float pitch);

  PacketContainer createTeleportPacket(Player player, Location location);

  PacketContainer createHeadRotationPacket(Player player, float yaw);

  PacketContainer convertAnimationPacket(Player player, PacketContainer oldPacket);

  static Optional<PacketConverter> getConverter() {
    try {
      String version = Bukkit.getServer().getClass().getPackage().getName()
          .replace(".", ",").split(",")[3].substring(1);
      return Optional.of((PacketConverter) Class
          .forName(PacketConverter.class.getCanonicalName() + "_" + version)
          .getConstructors()[0].newInstance());
    } catch (InstantiationException | IllegalAccessException
        | ClassNotFoundException | InvocationTargetException e) {
      ProReplay.getInstance().getLogger().log(Level.WARNING, "Failed to get PacketConverter.", e);
    }
    return Optional.empty();
  }

  static Set<PacketType> getPacketTypes() {
    return getPacketTypes(ProReplay.getInstance().getConfig().getBoolean("record.chat"),
        ProReplay.getInstance().getConfig().getBoolean("record.scoreboard"),
        ProReplay.getInstance().getConfig().getBoolean("record.title"));
  }

  static Set<PacketType> getPacketTypes(boolean chat, boolean scoreboard, boolean title) {
    Collection<PacketType> types = new HashSet<>(Arrays.asList(PacketType.Play.Client.ARM_ANIMATION,
        PacketType.Play.Server.REL_ENTITY_MOVE,
        PacketType.Play.Server.REL_ENTITY_MOVE_LOOK, PacketType.Play.Server.ENTITY_LOOK,
        PacketType.Play.Server.MAP_CHUNK, PacketType.Play.Server.MAP_CHUNK_BULK,
        PacketType.Play.Server.LIGHT_UPDATE, PacketType.Play.Server.WORLD_BORDER,
        PacketType.Play.Server.WORLD_PARTICLES, PacketType.Play.Server.WORLD_EVENT,
        PacketType.Play.Server.NAMED_SOUND_EFFECT, PacketType.Play.Server.LOOK_AT,
        PacketType.Play.Server.VIEW_CENTRE, PacketType.Play.Server.UPDATE_TIME,
        PacketType.Play.Server.COLLECT, PacketType.Play.Server.NAMED_ENTITY_SPAWN,
        PacketType.Play.Server.SPAWN_ENTITY, PacketType.Play.Server.SPAWN_ENTITY_EXPERIENCE_ORB,
        PacketType.Play.Server.SPAWN_ENTITY_LIVING, PacketType.Play.Server.SPAWN_ENTITY_WEATHER,
        PacketType.Play.Server.SPAWN_ENTITY_PAINTING, PacketType.Play.Server.ENTITY,
        PacketType.Play.Server.ENTITY_VELOCITY, PacketType.Play.Server.ENTITY_TELEPORT,
        PacketType.Play.Server.ENTITY_STATUS, PacketType.Play.Server.ATTACH_ENTITY,
        PacketType.Play.Server.ENTITY_EFFECT, PacketType.Play.Server.REMOVE_ENTITY_EFFECT,
        PacketType.Play.Server.ENTITY_EQUIPMENT, PacketType.Play.Server.EXPLOSION,
        PacketType.Play.Server.ENTITY_METADATA, PacketType.Play.Server.ENTITY_DESTROY,
        PacketType.Play.Server.ENTITY_SOUND, PacketType.Play.Server.UNLOAD_CHUNK,
        PacketType.Play.Server.MULTI_BLOCK_CHANGE, PacketType.Play.Server.BLOCK_CHANGE,
        PacketType.Play.Server.BLOCK_ACTION, PacketType.Play.Server.UPDATE_SIGN,
        PacketType.Play.Server.BLOCK_BREAK_ANIMATION, PacketType.Play.Server.GAME_STATE_CHANGE));
    if (chat) {
      types.add(PacketType.Play.Server.CHAT);
    }
    if (scoreboard) {
      types.add(PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE);
      types.add(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
      types.add(PacketType.Play.Server.SCOREBOARD_SCORE);
      types.add(PacketType.Play.Server.SCOREBOARD_TEAM);
    }
    if (title) {
      types.add(PacketType.Play.Server.TITLE);
    }
    return types.stream()
        .filter(PacketType::isSupported)
        .collect(Collectors.toSet());
  }
}
